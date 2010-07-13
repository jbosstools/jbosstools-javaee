/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
/**
 * Parse and Compare Css File Content
 * @author Vladimir Pakan
 */
public class CssFileParser {
  private LinkedList<Properties> cssClasses =  new LinkedList<Properties>();
  private Properties atKeywords =  new Properties();
  
  private LinkedList<String> fileLines = new LinkedList<String>();
  private boolean parsed = false;
  private enum ParserStatus {OUT_OF_CLASS_DEFINITION, 
    IN_CLASS_DEFINTION}; 
  
  public CssFileParser(){
    
  }
  
  public CssFileParser(String... lines){
    addLines(lines);
  }
  
  public boolean isParsed() {
    return parsed;
  }
  
  public LinkedList<Properties> getCssClasses() {
    return cssClasses;
  }
  
  public Properties getAtKeywords() {
    return atKeywords;
  }

  /**
   * Adds only non empty lines. Do not change it because parser doesn't
   * check for empy lines 
   * @param line
   */
  public void addLine(String line){
    if (line != null && line.trim().length() > 0){
      fileLines.add(line);
      parsed = false;
    }
  }
  
  public void addLines(String... lines){
    for (String line : lines){
      addLine(line);
    }
  }
  
  public void reset(){
    cssClasses.clear();
    fileLines.clear();
    parsed = false;
  }
  
  public void parse(){
    cssClasses.clear();
    ParserStatus parserStatus = ParserStatus.OUT_OF_CLASS_DEFINITION;
    Properties classProperties = null;
    for (String line : fileLines){
      line = line.trim();
      // inside Css Class Definition
      if (parserStatus.equals(ParserStatus.IN_CLASS_DEFINTION)){
        if (line.startsWith("}")){
          parserStatus = ParserStatus.OUT_OF_CLASS_DEFINITION;
          cssClasses.add(classProperties);
          classProperties = null;
          // parse atKeywords
          if (line.length() > 1){
            parseAtKeywords(line.substring(1).trim());
          }
        }
        else{
          String[] styleLineParts = line.split(":");
          if (styleLineParts.length == 2){
            classProperties.put(styleLineParts[0], 
              (styleLineParts[1].endsWith(";")) ?
                 styleLineParts[1].substring(0 , styleLineParts[1].length() - 1) : styleLineParts[1]);
          }
          else{
            throw new CssFileParserException("Style Definition Line within CSS File has wrong format: "
              + line);   
          }
        }
      }// Waiting for css class definition
      else if(parserStatus.equals(ParserStatus.OUT_OF_CLASS_DEFINITION)){
         if (line.startsWith("cssclass")){
           if (line.endsWith("{")){
             parserStatus = ParserStatus.IN_CLASS_DEFINTION;
             classProperties = new Properties();
           }
           else{
             throw new CssFileParserException("Line should ends with '{' string but is: "
               + line);   
           }
         }
         else if (line.startsWith("@")){
           parseAtKeywords(line);
         }
         // This is exception for this issue https://jira.jboss.org/browse/JBIDE-6604
         else if (!line.trim().equals("}")){
           throw new CssFileParserException("Line should starts with 'cssclass' or '@' string but is: "
             + line);   
         }
      }
    }
    parsed = true;
  }
  
  public boolean compare (CssFileParser cssFileParser){
    boolean result = false;
    
    if (cssFileParser != null){
      if (!cssFileParser.isParsed()){
        cssFileParser.parse();
      }
      if (!this.isParsed()){
        this.parse();
      }
      // Compare CSS Files Contents
      boolean propertiesAreEqual = true;
      boolean atKeywordsAreEqual = true;
      if (this.getCssClasses().size() == cssFileParser.getCssClasses().size()){
        Iterator<Properties> itThisProperties = this.getCssClasses().iterator();
        Iterator<Properties> cssFileProperties = cssFileParser.getCssClasses().iterator();
        while (propertiesAreEqual && itThisProperties.hasNext()){
          propertiesAreEqual = compareProperties(itThisProperties.next(), cssFileProperties.next());
        }
        // Compare At Keywords
        if (propertiesAreEqual){
          atKeywordsAreEqual = compareProperties(getAtKeywords(),cssFileParser.getAtKeywords());
        }
        result = propertiesAreEqual && atKeywordsAreEqual;
      }
    }
    
    return result;
  }
  /**
   * Parse At Keywords
   * @param line
   */
  private void parseAtKeywords(String line) {
    if (line.length() > 0) {
      if (line.startsWith("@")) {
        if (line.length() > 1) {
          String[] splitLine = line.split(" ");
          atKeywords
              .put(splitLine[0], splitLine.length > 1 ? splitLine[1] : "");
        } else {
          throw new CssFileParserException(
              "Style Definition Line within CSS File has wrong format. At Keyword is empty. Line: "
                  + line);
        }
      } else {
        throw new CssFileParserException(
            "Style Definition Line within CSS File has wrong format. At Keyword is expected but line content is "
                + line);
      }
    }
  }
  /**
   * Compare two sets of properties
   * @param props1
   * @param props2
   * @return
   */
  private boolean compareProperties (Properties props1 , Properties props2){
    boolean propertiesAreEqual = true;
    Properties props2Clone = (Properties)props2.clone();
    Iterator<Object> itProps1Name = props1.keySet().iterator();
    while (propertiesAreEqual && itProps1Name.hasNext()){
      String propertyName = (String)itProps1Name.next();
      if (props2Clone.containsKey(propertyName)){
        propertiesAreEqual = props1.getProperty(propertyName).trim()
          .equals(props2Clone.getProperty(propertyName).trim());
        // Remove checked property from props2Clone properties
        props2Clone.remove(propertyName);
      }
      else{
        propertiesAreEqual = false;
      }
    }
    // If there are left properties in CSS file then files are not equal
    if (props2Clone.size() > 0){
      propertiesAreEqual = false;
    }
    
    return propertiesAreEqual;
    
  }
  // TODO: Remove It
  public static void main (String[] args){
    CssFileParser parser1 = new CssFileParser(
        "cssclass{",
        "color: red;",
        "background-color: green;",
        "font-weight: bold;",
        "text-decoration: underline",
        "}",
        "cssclass{",
        "color: green;",
        "background-color: red;",
        "font-weight: lighter;",
        "text-decoration: overline",
        "}@CHARSET \"UTF-8\"");

    CssFileParser parser2 = new CssFileParser(
        "cssclass{",
        "color: red;",
        "background-color: green;",
        "text-decoration: underline",
        "font-weight: bold;",
        "}",
        "cssclass{",
        "color: green;",
        "background-color: red;",
        "text-decoration: overline",
        "font-weight: lighter;",
        "}@CHARSET \"UTF-8\";",
        "}");
    
    System.out.println(parser1.compare(parser2));

  }
  
}
