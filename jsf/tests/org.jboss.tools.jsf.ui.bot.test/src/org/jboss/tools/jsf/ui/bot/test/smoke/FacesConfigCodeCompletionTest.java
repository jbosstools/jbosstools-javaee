/*******************************************************************************
 * Copyright (c) 2007-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.bot.test.smoke;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.helper.ContentAssistHelper;
import org.jboss.tools.ui.bot.ext.parts.SWTBotEditorExt;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.vpe.ui.bot.test.VPEAutoTestCase;
/** * Test Code Completion functionality of faces-config.xml file
 * @author Vladimir Pakan
 *
 */
public class FacesConfigCodeCompletionTest extends JSFAutoTestCase{
  
  private static final String FACES_CONFIG_FILE_NAME = "faces-config.xml";
  private SWTBotEditor facesConfigEditor;
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    facesConfigEditor = eclipse.openFile(VPEAutoTestCase.JBT_TEST_PROJECT_NAME, 
        "WebContent",
        "WEB-INF",
        FacesConfigCodeCompletionTest.FACES_CONFIG_FILE_NAME);
    new SWTBotEditorExt(facesConfigEditor.toTextEditor().getReference(),bot)
      .selectPage(IDELabel.FacesConfigEditor.SOURCE_TAB_LABEL);
  }
  @Override
  protected void tearDown() throws Exception {
    if (facesConfigEditor != null){
      facesConfigEditor.saveAndClose();
    }
    super.tearDown();
  }
  /**
   * Test Code Completion functionality of faces-config.xml file
   */
  public void testCodeCompletionOfFacesConfig(){
    String textToSelect = "<managed-bean>";
    // check Content Assist inside <faces-config> node
    ContentAssistHelper.checkContentAssistContent(SWTTestExt.bot, 
        FacesConfigCodeCompletionTest.FACES_CONFIG_FILE_NAME,
        textToSelect, 
        0, 
        0,
        0,
        getInsideFacesConfigTagProposalList());
    textToSelect = "<description>User Name Bean</description>";
    // check Content Assist inside <managed-bean> node
    ContentAssistHelper.checkContentAssistContent(SWTTestExt.bot, 
        FacesConfigCodeCompletionTest.FACES_CONFIG_FILE_NAME,
        textToSelect, 
        0, 
        0,
        0,
        getInsideManagedBeanTagProposalList());
    textToSelect = "<property-name>name</property-name>";
    // check Content Assist inside <managed-property> node
    ContentAssistHelper.checkContentAssistContent(SWTTestExt.bot, 
        FacesConfigCodeCompletionTest.FACES_CONFIG_FILE_NAME,
        textToSelect, 
        0, 
        0,
        0,
        getInsideManagedPropertyTagProposalList());
    textToSelect = "<from-view-id>";
    // check Content Assist inside <navigation-rule> node
    ContentAssistHelper.checkContentAssistContent(SWTTestExt.bot, 
        FacesConfigCodeCompletionTest.FACES_CONFIG_FILE_NAME,
        textToSelect, 
        0, 
        0,
        0,
        getInsideNavigationRuleTagProposalList());
    textToSelect = "<from-outcome>";
    // check Content Assist inside <navigation-case> node
    ContentAssistHelper.checkContentAssistContent(SWTTestExt.bot, 
        FacesConfigCodeCompletionTest.FACES_CONFIG_FILE_NAME,
        textToSelect, 
        0, 
        0,
        0,
        getInsideNavigationCaseTagProposalList());
  }
  /**
   * Returns list of expected proposals inside <faces-config> tag
   * @return
   */
  private static List<String> getInsideFacesConfigTagProposalList(){
    LinkedList<String> result = new LinkedList<String>();
    
    result.add("application");
    result.add("component");
    result.add("converter");
    result.add("faces-config-extension");
    result.add("factory");
    result.add("lifecycle");
    result.add("managed-bean");
    result.add("navigation-rule");
    result.add("referenced-bean");
    result.add("render-kit");
    result.add("validator");
    result.add("XSL processing instruction - XSL processing instruction");
    result.add("comment - xml comment");
    result.add("user : User");
    
    return result;
  }
  /**
   * Returns list of expected proposals inside <managed-bean> tag
   * @return
   */
  private static List<String> getInsideManagedBeanTagProposalList(){
    LinkedList<String> result = new LinkedList<String>();
    
    result.add("description");
    result.add("display-name");
    result.add("icon");
    result.add("list-entries");
    result.add("managed-bean-class");
    result.add("managed-bean-extension");
    result.add("managed-bean-name");
    result.add("managed-bean-scope");
    result.add("managed-property");
    result.add("map-entries");
    result.add("XSL processing instruction - XSL processing instruction");
    result.add("comment - xml comment");
    result.add("user : User");
    return result;
  }
  /**
   * Returns list of expected proposals inside <managed-property> tag
   * @return
   */
  private static List<String> getInsideManagedPropertyTagProposalList(){
    LinkedList<String> result = new LinkedList<String>();
    
    result.add("description");
    result.add("display-name");
    result.add("icon");
    result.add("list-entries");
    result.add("map-entries");
    result.add("null-value");
    result.add("property-class");
    result.add("property-name");
    result.add("value");
    result.add("XSL processing instruction - XSL processing instruction");
    result.add("comment - xml comment");
    result.add("user : User");
    return result;
  }
  /**
   * Returns list of expected proposals inside <navigation-rule> tag
   * @return
   */
  private static List<String> getInsideNavigationRuleTagProposalList(){
    LinkedList<String> result = new LinkedList<String>();
    
    result.add("description");
    result.add("display-name");
    result.add("icon");
    result.add("from-view-id");
    result.add("navigation-case");
    result.add("navigation-rule-extension");
    result.add("XSL processing instruction - XSL processing instruction");
    result.add("comment - xml comment");
    result.add("user : User");
    return result;
  }
  /**
   * Returns list of expected proposals inside <navigation-case> tag
   * @return
   */
  private static List<String> getInsideNavigationCaseTagProposalList(){
    LinkedList<String> result = new LinkedList<String>();
    
    result.add("description");
    result.add("display-name");
    result.add("from-action");
    result.add("icon");
    result.add("from-outcome");
    result.add("redirect");
    result.add("to-view-id");
    result.add("XSL processing instruction - XSL processing instruction");
    result.add("comment - xml comment");
    result.add("user : User");
    return result;
  }

}