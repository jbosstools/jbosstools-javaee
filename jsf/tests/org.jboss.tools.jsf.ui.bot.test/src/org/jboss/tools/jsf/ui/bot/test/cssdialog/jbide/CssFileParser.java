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
        if (line.equals("}")){
          parserStatus = ParserStatus.OUT_OF_CLASS_DEFINITION;
          cssClasses.add(classProperties);
          classProperties = null;
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
         else{
           throw new CssFileParserException("Line should starts with 'cssclass' string but is: "
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
      if (this.getCssClasses().size() == cssFileParser.getCssClasses().size()){
        Iterator<Properties> itThisProperties = this.getCssClasses().iterator();
        Iterator<Properties> cssFileProperties = cssFileParser.getCssClasses().iterator();
        while (propertiesAreEqual && itThisProperties.hasNext()){
          Properties propsThis = (Properties)itThisProperties.next().clone();
          Properties propsCssFile = (Properties)cssFileProperties.next().clone();
          boolean styleAttributesAreEqual = true;
          Iterator<Object> itThisPropertyName = propsThis.keySet().iterator();
          while (styleAttributesAreEqual && itThisPropertyName.hasNext()){
            String propertyName = (String)itThisPropertyName.next();
            if (propsCssFile.containsKey(propertyName)){
              styleAttributesAreEqual = propsThis.getProperty(propertyName).trim()
              .equals(propsCssFile.getProperty(propertyName).trim());
            // Remove checked property from CSS file properties
            propsCssFile.remove(propertyName);
            }
            else{
              styleAttributesAreEqual = false;
            }
          }
          // If there are left properties in CSS file then files are not equal
          propertiesAreEqual = styleAttributesAreEqual && propsCssFile.size() == 0;
        }
        result = propertiesAreEqual;
      }
    }
    
    return result;
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
        "}");

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
        "}");
    
    System.out.println(parser1.compare(parser2));

  }
  
}
