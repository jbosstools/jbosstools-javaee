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

import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWTException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.ui.bot.ext.Assertions;
import org.jboss.tools.ui.bot.ext.SWTJBTExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.helper.BuildPathHelper;
import org.jboss.tools.ui.bot.ext.helper.ContentAssistHelper;
import org.jboss.tools.ui.bot.ext.helper.KeyboardHelper;
import org.jboss.tools.ui.bot.ext.helper.OpenOnHelper;
import org.jboss.tools.ui.bot.ext.parts.ContentAssistBot;
import org.jboss.tools.ui.bot.ext.parts.SWTBotEditorExt;
import org.jboss.tools.vpe.ui.bot.test.VPEAutoTestCase;
/** * Test Code Completion functionality of JSF components within xhtml page
 * @author Vladimir Pakan
 *
 */
public class CodeCompletionTest extends JSFAutoTestCase{
  private SWTBotEditorExt editor;
  private SWTBotEditorExt compositeComponentDefEditor;
  private SWTBotEditorExt compositeComponentContainerEditor;
  private String originalEditorText;
  private String compositeComponentDefEditorText;
  private String origCompositeComponentContainerEditorText;
  private String addedVariableRichfacesUiLocation = null;
  /**
   * Test Code Completion functionality for managed bean
   */
  public void testCodeCompletionOfManagedBean(){
    initFaceletsPageTest();
    String textForSelection = "value=\"#{person.name}\"";
    List<String> expectedProposals = new LinkedList<String>();
    expectedProposals.add("msg");
    expectedProposals.add("person : Person");
    // Check content assist for #{ prefix
    ContentAssistHelper.checkContentAssistContent(SWTTestExt.bot, 
        FACELETS_TEST_PAGE,
        textForSelection, 
        9, 
        0, 
        expectedProposals);
    // Check content assist for #{msg. prefix
    ContentAssistHelper.checkContentAssistAutoProposal(SWTTestExt.bot, 
        FACELETS_TEST_PAGE,
        textForSelection, 
        16, 
        0, 
        0,
        "name");
  }
  /**
   * Test Code Completion functionality for resource
   */
  public void testCodeCompletionOfResource(){
    initFaceletsPageTest();
    ContentAssistBot contentAssist = editor.contentAssist();
    String textForSelection = "${msg.prompt}";
    // Check content assist for ${ prefix
    SWTJBTExt.selectTextInSourcePane(SWTTestExt.bot, 
        FACELETS_TEST_PAGE,
        textForSelection, 
        2, 
        0, 
        0);
    contentAssist.checkContentAssist("msg", false);
    contentAssist.checkContentAssist("person : Person", false);
    // Check content assist for #{msg. prefix
    SWTJBTExt.selectTextInSourcePane(SWTTestExt.bot, 
        FACELETS_TEST_PAGE,
        textForSelection, 
        6, 
        0, 
        0);
    contentAssist.checkContentAssist("greeting", false);
    contentAssist.checkContentAssist("prompt", false);
  }
  /**
   * Test Code Completion functionality of <input> tag attributes within xhtml page
   */
	public void testCodeCompletionOfInputTagAttributes(){
	  initFaceletsPageTest();
	  ContentAssistBot contentAssist = editor.contentAssist();
    String textForSelection = "action=\"greeting\" value=\"Say Hello\" ";
    // Check content assist menu content
    ContentAssistHelper.checkContentAssistContent(SWTTestExt.bot, 
        FACELETS_TEST_PAGE,
        textForSelection, 
        36, 
        0, 
        getInputTagProposalList());
    // Check content assist insertion    
    String contentAssistToUse = "maxlength"; 
    contentAssist.checkContentAssist(contentAssistToUse, true);
    editor.save();
    String expectedInsertedText = textForSelection + contentAssistToUse + "=\"\"";
    assertTrue("Editor has to contain text '" + expectedInsertedText + "' but it doesn't\n" +
        "Editor Text is\n" + editor.getText(),
      editor.getText().contains(expectedInsertedText));
		
	}
  /**
   * Test Code Completion functionality of <input> tag for jsfc attribute within xhtml page
   */
  public void testCodeCompletionOfInputTagForJsfcAttribute(){
    initFaceletsPageTest();
    // check jsfc attribute insertion via Content Assist 
    SWTJBTExt.selectTextInSourcePane(SWTTestExt.bot, 
        FACELETS_TEST_PAGE,
        "<input ", 
        0, 
        0, 
        0);
    String textToInsert = "<input  />";
    editor.insertText(textToInsert);
    SWTJBTExt.selectTextInSourcePane(SWTTestExt.bot, 
        FACELETS_TEST_PAGE,
        textToInsert, 
        7, 
        0, 
        0);
    String contentAssistToUse = "jsfc";
    ContentAssistBot contentAssist = editor.contentAssist();
    SWTBotShell[] shellsBeforeCA = bot.shells();
    contentAssist.checkContentAssist(contentAssistToUse, true);
    editor.save();
    SWTBotShell caShell = contentAssist.getContentAssistShell(shellsBeforeCA, bot.shells());
    if (caShell != null){
      caShell.close();  
    }    
    String expectedInsertedText = "<input " + contentAssistToUse + "=\"\"";
    assertTrue("Editor has to contain text '" + expectedInsertedText + "' but it doesn't\n" +
        "Editor Text is\n" + editor.getText(),
      editor.getText().contains(expectedInsertedText));
    // hide Code Assist Window automatically opened when typing text
    // check jsfc attribute value Content Assist menu Content
    ContentAssistHelper.checkContentAssistContent(SWTTestExt.bot, 
        FACELETS_TEST_PAGE,
        expectedInsertedText, 
        13, 
        0, 
        getJsfcAttributeValueProposalList());
    // check jsfc attribute value insertion via Content Assist
    contentAssistToUse = "h:inputText";
    contentAssist.checkContentAssist(contentAssistToUse, true);
    expectedInsertedText = "<input jsfc=\"" + contentAssistToUse + "\"";
    assertTrue("Editor has to contain text '" + expectedInsertedText + "' but it doesn't\n" +
        "Editor Text is\n" + editor.getText(),
      editor.getText().contains(expectedInsertedText));
    editor.save();
    // check Content Assist content of jsfc attribute attribute
    ContentAssistHelper.checkContentAssistContent(SWTTestExt.bot, 
        FACELETS_TEST_PAGE,
        expectedInsertedText, 
        26, 
        0, 
        getJsfcAttributeValueAttributesProposalList());
    // check jsfc attribute value attribute insertion via Content Assist
    String contentAssistAttributeToUse = "accept";
    contentAssist.checkContentAssist(contentAssistAttributeToUse, true);
    expectedInsertedText = "<input jsfc=\"" + contentAssistToUse + "\" " + contentAssistAttributeToUse + "=\"\"";
    assertTrue("Editor has to contain text '" + expectedInsertedText + "' but it doesn't\n" +
        "Editor Text is\n" + editor.getText(),
      editor.getText().contains(expectedInsertedText));
    editor.save();
  }
  /**
   * Test Code Completion functionality for Composite Component
   */
  public void testCodeCompletionOfCompositeComponent(){
    initJSF2PageTest();
    ContentAssistBot contentAssist = compositeComponentContainerEditor.contentAssist();
    SWTJBTExt.selectTextInSourcePane(SWTTestExt.bot, 
        JSF2_TEST_PAGE,
        "<ez:input ", 
        0, 
        0, 
        0);
    String textToInsert = "<ez:";
    compositeComponentContainerEditor.insertText(textToInsert);
    SWTJBTExt.selectTextInSourcePane(SWTTestExt.bot, 
        JSF2_TEST_PAGE,
        textToInsert, 
        textToInsert.length(), 
        0, 
        0);
    // Check content assist menu content for "<ez:"
    contentAssist.checkContentAssist("ez:input", true);
    bot.sleep(Timing.time2S());
    compositeComponentContainerEditor.save();
    String currentLineText = compositeComponentContainerEditor.getTextOnCurrentLine();
    String expectedInsertedText = "<ez:input value=\"\" action=\"\"></ez:input>";
    if (!currentLineText.toLowerCase().contains(expectedInsertedText.toLowerCase())){
      expectedInsertedText = "<ez:input action=\"\" value=\"\"></ez:input>";
      assertTrue("Inserted text should be " + expectedInsertedText + " but is not.\n" 
          + "Current line text is " + currentLineText,
        currentLineText.toLowerCase().contains(expectedInsertedText.toLowerCase()));
    }
    // Check content assist menu content for Composite Components attributes    
    ContentAssistHelper.checkContentAssistContent(SWTTestExt.bot, 
        JSF2_TEST_PAGE,
        expectedInsertedText, 
        10, 
        0, 
        getCompositeComponentsAttributesProposalList());
    // Open Composite Component definition file
    String compositeComponentFileName = "input.xhtml";
    OpenOnHelper.checkOpenOnFileIsOpened(
        SWTTestExt.bot, JSF2_TEST_PAGE, "<ez:input ", 5,
        0, 0, compositeComponentFileName);
    compositeComponentDefEditor = SWTTestExt.bot.swtBotEditorExtByTitle(compositeComponentFileName);
    compositeComponentDefEditorText = compositeComponentDefEditor.getText();
    textToInsert = "<h:commandButton action=\"";
    SWTJBTExt.selectTextInSourcePane(SWTTestExt.bot, 
        compositeComponentFileName,
        textToInsert, 
        0, 
        0, 
        0);
    compositeComponentDefEditor.insertText(textToInsert + "\"/> ");  // add closing "/>
    SWTJBTExt.selectTextInSourcePane(SWTTestExt.bot, 
        compositeComponentFileName,
        textToInsert, 
        textToInsert.length(), 
        0, 
        0);
    // Check content assist menu content for ""<h:commandButton action="" />"
    contentAssist = compositeComponentDefEditor.contentAssist();
    contentAssist.checkContentAssist("cc.attrs", true);
    bot.sleep(Timing.time2S());
    compositeComponentDefEditor.save();
    currentLineText = compositeComponentDefEditor.getTextOnCurrentLine();
    expectedInsertedText = "#{cc.attrs}";
    assertTrue("Inserted text should be " + expectedInsertedText + " but is not.\n" 
        + "Current line text is " + currentLineText,
      currentLineText.toLowerCase().contains(expectedInsertedText.toLowerCase()));
    compositeComponentDefEditor.insertText(".");
    KeyboardHelper.typeKeyCodeUsingAWT(KeyEvent.VK_RIGHT);
    // Check content assist menu content for Composite Components attributes    
    ContentAssistHelper.checkContentAssistContent(SWTTestExt.bot, 
        compositeComponentFileName,
        "#{cc.attrs.}", 
        11, 
        0, 
        getCompositeComponentsAttributeDefProposalList());
    // check inserting of "submitlabel" content assist
    String contentAssistToUse = "submitlabel";
    contentAssist.checkContentAssist(contentAssistToUse, true);
    expectedInsertedText = "<h:commandButton action=\"#{cc.attrs." + contentAssistToUse + "}\"";
    assertTrue("Editor has to contain text '" + expectedInsertedText + "' but it doesn't\n" +
        "Editor Text is\n" + compositeComponentDefEditor.getText(),
        compositeComponentDefEditor.getText().toLowerCase().contains(expectedInsertedText.toLowerCase()));
    compositeComponentDefEditor.save();
  }
  /**
   * Test Code Completion functionality for Managed Bean 
   * referenced via @ManagedBean annotation  
   */
  public void testCodeCompletionOfReferencedManagedBean(){
    initJSF2PageTest();
    String textForSelection = "value=\"#{user.name}\"";
    List<String> expectedProposals = new LinkedList<String>();
    expectedProposals.add("msgs");
    expectedProposals.add("user : User");
    expectedProposals.add("\"#{user.name}\"");
    // Check content assist for #{ prefix
    ContentAssistHelper.checkContentAssistContent(SWTTestExt.bot, 
        JSF2_TEST_PAGE,
        textForSelection, 
        9, 
        0, 
        expectedProposals);
    // Check content assist for #{user. prefix
    expectedProposals.clear();
    expectedProposals.add("name : String - User");
    expectedProposals.add("sayHello() : String - User");
    expectedProposals.add("\"#{user.name}\"");
    ContentAssistHelper.checkContentAssistContent(SWTTestExt.bot, 
        FACELETS_TEST_PAGE,
        textForSelection, 
        14, 
        0, 
        expectedProposals);
  }
  /**
   * Test Code Completion functionality for msgs[
   */
  public void testCodeCompletionOfMsgsWithBrackets(){
    initFaceletsPageTest();
    SWTJBTExt.selectTextInSourcePane(SWTTestExt.bot, 
        FACELETS_TEST_PAGE,
        "<h:message ", 
        0, 
        0, 
        0);
    editor.insertText("\n");
    String textToInsert = "<h:outputText value=\"#{msg";
    final SWTBotShell[] shellsBefore = bot.shells();
    editor.typeText(textToInsert + "[");
    bot.sleep(Timing.time2S());
    // Check Content Assist invoked by typing
    ContentAssistBot contentAssist = editor.contentAssist();
    final List<String> caProposals = contentAssist.getProposalList(shellsBefore, bot.shells(), true);
    String useCodeAssist = "['greeting']";
    assertTrue("Content assist has to contain item " + useCodeAssist +
        " but it does not.",
      caProposals.contains(useCodeAssist));
    useCodeAssist = "['prompt']";
    assertTrue("Content assist has to contain item " + useCodeAssist +
        " but it does not.",
      caProposals.contains(useCodeAssist));
    // Check Content Assist invoked by Ctrl-Space
    useCodeAssist = "['greeting']";
    contentAssist.checkContentAssist(useCodeAssist, false);
    useCodeAssist = "['prompt']";
    contentAssist.checkContentAssist(useCodeAssist, true);
    final String textToInsertAtEnd = "/>";
    editor.insertText(editor.cursorPosition().line, 
      editor.cursorPosition().column + 2,
      textToInsertAtEnd);
    editor.save();
    bot.sleep(Timing.time1S());
    Assertions.assertSourceEditorContains(editor.getText(), 
      textToInsert + useCodeAssist + "}\"" + textToInsertAtEnd,
      FACELETS_TEST_PAGE);
  }
  /**
   * Test Code Completion functionality of src attribute for tags <link>, <h:link> and <a:loadStyle>
   */
  public void testCodeCompletionOfSrcAttribute(){
    initJSF2PageTest();
    addRichFacesToJSF2ProjectClassPath();
    SWTJBTExt.selectTextInSourcePane(SWTTestExt.bot, 
        JSF2_TEST_PAGE,
        "xmlns:f=\"http://java.sun.com/jsf/core\"", 
        0, 
        0, 
        0);
    compositeComponentContainerEditor.insertText("xmlns:a4j=\"http://richfaces.org/a4j\" \n");
    SWTJBTExt.selectTextInSourcePane(SWTTestExt.bot, 
        JSF2_TEST_PAGE,
        "<h:message ", 
        0, 
        0, 
        0);
    compositeComponentContainerEditor.insertText("\n");
    final String linkTag = "<link href=\"";
    compositeComponentContainerEditor.insertText(linkTag + "\"/>\n");
    final String hLinkTag = "<h:link value=\"";
    compositeComponentContainerEditor.insertText(hLinkTag + "\"/>\n");
    compositeComponentContainerEditor.save();
    bot.sleep(Timing.time2S());
    checkCodeCompletionOfSourceAttribute(linkTag);
    checkCodeCompletionOfSourceAttribute(hLinkTag);    
  }

  /**
   * Initialize test which are using facelets test page
   */
	private void initFaceletsPageTest() {
	  eclipse.closeAllEditors();
	  openPage(FACELETS_TEST_PAGE,FACELETS_TEST_PROJECT_NAME);
    editor = SWTTestExt.bot.swtBotEditorExtByTitle(FACELETS_TEST_PAGE);
    originalEditorText = editor.getText();
    setProjectName(FACELETS_TEST_PROJECT_NAME);
	}
	
  /**
   * Initialize test which are using JSF2 test page
   */
  private void initJSF2PageTest() {
    eclipse.closeAllEditors();
    createJSF2Project(JSF2_TEST_PROJECT_NAME);
    openPage(JSF2_TEST_PAGE, JSF2_TEST_PROJECT_NAME);
    compositeComponentContainerEditor = SWTTestExt.bot.swtBotEditorExtByTitle(FACELETS_TEST_PAGE);
    origCompositeComponentContainerEditorText = compositeComponentContainerEditor.getText();
    setProjectName(JSF2_TEST_PROJECT_NAME);
  } 
	/**
	 * Returns list of expected Content Assist proposals for Input tag
	 * @return
	 */
  private static List<String> getInputTagProposalList(){
    LinkedList<String> result = new LinkedList<String>();
    
    result.add("accept");
    result.add("accesskey");
    result.add("align=\"top\"");
    result.add("alt");
    result.add("checked=\"checked\"");
    result.add("class");
    result.add("dir=\"ltr\"");
    result.add("disabled=\"disabled\"");
    result.add("lang");
    result.add("maxlength");
    result.add("name");
    result.add("onblur");
    result.add("onchange");
    result.add("onclick");
    result.add("ondblclick");
    result.add("onfocus");
    result.add("onkeydown");
    result.add("onkeypress");
    result.add("onkeyup");
    result.add("onmousedown");
    result.add("onmousemove");
    result.add("onmouseout");
    result.add("onmouseover");
    result.add("onmouseup");
    result.add("onselect");
    result.add("readonly=\"readonly\"");
    result.add("size");
    result.add("src");
    result.add("style");
    result.add("tabindex");
    result.add("title");
    result.add("usemap");
    result.add("xml:lang");
    result.add("actionListener");
    result.add("binding");
    result.add("image");
    result.add("immediate");
    result.add("label");
    result.add("rendered");
    result.add("styleClass");
    
    return result;
  }
  /**
   * Returns list of expected Content Assist proposals for Jsfc attribute value
   * @return
   */
  private static List<String> getJsfcAttributeValueProposalList(){
    LinkedList<String> result = new LinkedList<String>();
    
    result.add("c:catch");
    result.add("c:choose");
    result.add("c:forEach");
    result.add("c:forTokens");
    result.add("c:if");
    result.add("c:import");
    result.add("c:otherwise");
    result.add("c:out");
    result.add("c:param");
    result.add("c:redirect");
    result.add("c:remove");
    result.add("c:set");
    result.add("c:url");
    result.add("c:when");
    result.add("f:actionListener");
    result.add("f:attribute");
    result.add("f:convertDateTime");
    result.add("f:convertNumber");
    result.add("f:converter");
    result.add("f:facet");
    result.add("f:loadBundle");
    result.add("f:param");
    result.add("f:phaseListener");
    result.add("f:selectItem");
    result.add("f:selectItems");
    result.add("f:setPropertyActionListener");
    result.add("f:subview");
    result.add("f:validateDoubleRange");
    result.add("f:validateLength");
    result.add("f:validateLongRange");
    result.add("f:validator");
    result.add("f:valueChangeListener");
    result.add("f:verbatim");
    result.add("f:view");
    result.add("h:column");
    result.add("h:commandButton");
    result.add("h:commandLink");
    result.add("h:dataTable");
    result.add("h:form");
    result.add("h:graphicImage");
    result.add("h:inputHidden");
    result.add("h:inputSecret");
    result.add("h:inputText");
    result.add("h:inputTextarea");
    result.add("h:message");
    result.add("h:messages");
    result.add("h:outputFormat");
    result.add("h:outputLabel");
    result.add("h:outputLink");
    result.add("h:outputText");
    result.add("h:panelGrid");
    result.add("h:panelGroup");
    result.add("h:selectBooleanCheckbox");
    result.add("h:selectManyCheckbox");
    result.add("h:selectManyListbox");
    result.add("h:selectManyMenu");
    result.add("h:selectOneListbox");
    result.add("h:selectOneMenu");
    result.add("h:selectOneRadio");
    result.add("msg");
    result.add("person : Person");
    return result;
  }
  
  @Override
  public void tearDown() throws Exception {
    if (editor != null){
      editor.setText(originalEditorText);
      editor.save();
      util.waitForNonIgnoredJobs();
      editor.close();
    }
    if (compositeComponentDefEditor != null){
      compositeComponentDefEditor.setText(compositeComponentDefEditorText);
      compositeComponentDefEditor.save();
      util.waitForNonIgnoredJobs();
      compositeComponentDefEditor.close();
    }
    if (compositeComponentContainerEditor != null){
      compositeComponentContainerEditor.setText(origCompositeComponentContainerEditorText);
      compositeComponentContainerEditor.save();
      util.waitForNonIgnoredJobs();
      compositeComponentContainerEditor.close();
    }
    util.waitForNonIgnoredJobs();
    removeRichFacesFromJSF2ProjectClassPath();

    super.tearDown();
  }
  /**
   * Returns list of expected Content Assist proposals for Jsfc attribute value attributes
   * @return
   */
  private static List<String> getJsfcAttributeValueAttributesProposalList(){
    LinkedList<String> result = new LinkedList<String>();
    
    result.add("accept");
    result.add("accesskey");
    result.add("align=\"top\"");
    result.add("alt");
    result.add("checked=\"checked\"");
    result.add("class");
    result.add("dir=\"ltr\"");
    result.add("disabled=\"disabled\"");
    result.add("id");
    result.add("lang");
    result.add("maxlength");
    result.add("name");
    result.add("onblur");
    result.add("onchange");
    result.add("onclick");
    result.add("ondblclick");
    result.add("onfocus");
    result.add("onkeydown");
    result.add("onkeypress");
    result.add("onkeyup");
    result.add("onmousedown");
    result.add("onmousemove");
    result.add("onmouseout");
    result.add("onmouseover");
    result.add("onmouseup");
    result.add("onselect");
    result.add("readonly=\"readonly\"");
    result.add("size");
    result.add("src");
    result.add("style");
    result.add("tabindex");
    result.add("title");
    result.add("type=\"text\"");
    result.add("usemap");
    result.add("value");
    result.add("xml:lang");
    result.add("autocomplete");
    result.add("binding");
    result.add("converter");
    result.add("converterMessage");
    result.add("immediate");
    result.add("label");
    result.add("rendered");
    result.add("required");
    result.add("requiredMessage");
    result.add("styleClass");
    result.add("validator");
    result.add("validatorMessage");
    result.add("valueChangeListener");
    return result;
  }
  /**
   * Returns list of expected Content Assist proposals for Composite Component Attributes
   * @return
   */
  private static List<String> getCompositeComponentsAttributesProposalList(){
    LinkedList<String> result = new LinkedList<String>();
    
    result.add("id");
    result.add("label");
    result.add("rendered");
    result.add("submitlabel");    
    return result;
  }
  
  /**
   * Returns list of expected Content Assist proposals for Composite Component Attributes
   * within file containing Composite Component definition
   * @return
   */
  private static List<String> getCompositeComponentsAttributeDefProposalList(){
    LinkedList<String> result = new LinkedList<String>();
    
    result.add("action");
    result.add("label");
    result.add("onclick");
    result.add("ondblclick");
    result.add("onkeydown");
    result.add("onkeypress");
    result.add("onkeyup");
    result.add("onmousedown");
    result.add("onmousemove");
    result.add("onmouseout");
    result.add("onmouseover");
    result.add("onmouseup");
    result.add("submitlabel");
    result.add("value");
    result.add("\"#{cc.attrs.}\"");    
    return result;
  }
  /**
   * Check Code Completion of src attribute of tagToCheck tag
   * @param tagToCheck 
   */
  private void checkCodeCompletionOfSourceAttribute(String tagToCheck){
    SWTJBTExt.selectTextInSourcePane(SWTTestExt.bot, 
        JSF2_TEST_PAGE,
        tagToCheck, 
        tagToCheck.length(), 
        0, 
        0);
    ContentAssistBot contentAssist = compositeComponentContainerEditor.contentAssist();
    contentAssist.checkContentAssist("/pages", false);
    contentAssist.checkContentAssist("/resources", false);
    contentAssist.checkContentAssist("/templates", false);
  }
  /**
   * Add RichFaces library to JSF2 project classpath
   */
  private void addRichFacesToJSF2ProjectClassPath(){
    Throwable exceptionBeforeCall = getException();
    addedVariableRichfacesUiLocation = BuildPathHelper.addExternalJar(VPEAutoTestCase.RICH_FACES_UI_JAR_LOCATION, 
      JSF2_TEST_PROJECT_NAME);
    if (exceptionBeforeCall == null 
        && getException() != null
        && getException() instanceof SWTException){
      setException(null);
    }
  }
  /**
   * Remove previously added RichFaces library from JSF2 project classpath
   */
  private void removeRichFacesFromJSF2ProjectClassPath(){
    if (addedVariableRichfacesUiLocation != null){
      BuildPathHelper.removeVariable(JSF2_TEST_PROJECT_NAME, addedVariableRichfacesUiLocation, true);
      eclipse.cleanAllProjects();
    }
  }
}