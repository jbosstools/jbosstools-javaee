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

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.parts.SWTBotEditorExt;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.vpe.ui.bot.test.VPEAutoTestCase;

/**
 * * Test web.xml file editor functionality
 * 
 * @author Vladimir Pakan
 * 
 */
public class WebXmlEditorTest extends JSFAutoTestCase {
  public static final String SESSION_CONFIG_NODE =  "session-config";
  public static final String WELCOME_FILE_LIST_NODE = "welcome-file-list";
  public static final String JSP_CONFIG_NODE = "JSP Config"; 
  public static final String LOGIN_CONFIG_NODE = "login-config"; 
  public static final String LOCALE_ENCODING_MAPPING_LIST = "locale-encoding-mapping-list"; 
  public static final String SERVLETS_NODE = "Servlets";
  private static final String WEB_XML_FILE_NAME = "web.xml";
  private static final String SERVLET_NAME = "TestChangeServlet";
  private static final String DISPLAY_NAME = "Test Change Servlet";
  private static final String SERVLET_CLASS = "org.jboss.tests.TestChangeServlet.java";
  private static final String SERVLET_DESCRIPTION = "Dummy Servlet just for testing web.xml editor functionality";
  private static final String URL_PATTERN = "*xhtml";
  private SWTBotEditor webXmlConfigEditor;
  private SWTBotEditorExt webXmlConfigEditorExt;
  private String origWebXmlFileContent;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    webXmlConfigEditor = eclipse.openFile(VPEAutoTestCase.JBT_TEST_PROJECT_NAME,
        "WebContent", "WEB-INF", WebXmlEditorTest.WEB_XML_FILE_NAME);
    origWebXmlFileContent = webXmlConfigEditor.toTextEditor().getText();
    webXmlConfigEditorExt = new SWTBotEditorExt(webXmlConfigEditor.toTextEditor().getReference(), bot);
  }

  @Override
  public void tearDown() throws Exception {
    if (webXmlConfigEditor != null) {
      webXmlConfigEditor.toTextEditor().setText(origWebXmlFileContent);
      webXmlConfigEditor.saveAndClose();
    }
    super.tearDown();
  }

  /**
   * Test web.xml file editor
   */
  public void testWebXmlEditor() {
    // open web.xml file editor
    webXmlConfigEditorExt.selectPage(IDELabel.WebXmlEditor.TREE_TAB_LABEL);
    SWTBotTree tree = webXmlConfigEditor.bot().tree();
    SWTBotTreeItem tiWebXml = tree.expandNode(WebXmlEditorTest.WEB_XML_FILE_NAME);
    // check content of web.xml tree select each treeitem and expand when possible
    bot.sleep(Timing.time1S());
    tiWebXml.getNode(WebXmlEditorTest.SESSION_CONFIG_NODE).select();
    bot.sleep(Timing.time1S());
    tiWebXml.getNode(WebXmlEditorTest.WELCOME_FILE_LIST_NODE).expand().select();
    bot.sleep(Timing.time1S());
    tiWebXml.getNode(WebXmlEditorTest.JSP_CONFIG_NODE).select();
    bot.sleep(Timing.time1S());
    tiWebXml.getNode(WebXmlEditorTest.LOGIN_CONFIG_NODE).select();
    bot.sleep(Timing.time1S());
    tiWebXml.getNode(WebXmlEditorTest.LOCALE_ENCODING_MAPPING_LIST).select();
    bot.sleep(Timing.time1S());
    tiWebXml.getNode(WebXmlEditorTest.SERVLETS_NODE).expand().select();
    // try to add new servlet 
    tiWebXml.getNode(WebXmlEditorTest.SERVLETS_NODE).select();
    bot.button(IDELabel.Button.ADD,0).click();
    bot.shell(IDELabel.WebXmlEditor.ADD_SERVLET_DIALOG_TITLE).activate();
    bot.textWithLabel(IDELabel.WebXmlEditor.ADD_SERVLET_DIALOG_SERVLET_NAME_LABEL)
      .setText(WebXmlEditorTest.SERVLET_NAME);
    bot.textWithLabel(IDELabel.WebXmlEditor.ADD_SERVLET_DIALOG_DISPLAY_NAME_LABEL)
      .setText(WebXmlEditorTest.DISPLAY_NAME);
    bot.textWithLabel(IDELabel.WebXmlEditor.ADD_SERVLET_DIALOG_SERVLET_CLASS_LABEL)
      .setText(WebXmlEditorTest.SERVLET_CLASS);
    bot.textWithLabel(IDELabel.WebXmlEditor.ADD_SERVLET_DIALOG_DESCRITPION_LABEL)
      .setText(WebXmlEditorTest.SERVLET_DESCRIPTION);
    bot.button(IDELabel.Button.FINISH).click();
    webXmlConfigEditor.save();
    String editorText = webXmlConfigEditorExt.getText();
    String textToContain = "<servlet-name>" + WebXmlEditorTest.SERVLET_NAME + "</servlet-name>";
    assertTrue("Web.xml editor has to contain text '" +
      textToContain +
      "' but it doesn't.",
      editorText.toLowerCase().contains(textToContain.toLowerCase()));
    textToContain = "<display-name>" + WebXmlEditorTest.DISPLAY_NAME + "</display-name>";
    assertTrue("Web.xml editor has to contain text '" +
      textToContain +
      "' but it doesn't.",
      editorText.toLowerCase().contains(textToContain.toLowerCase()));
    textToContain = "<servlet-class>" + WebXmlEditorTest.SERVLET_CLASS + "</servlet-class>";
    assertTrue("Web.xml editor has to contain text '" +
      textToContain +
      "' but it doesn't.",
      editorText.toLowerCase().contains(textToContain.toLowerCase()));
    textToContain = "<description>" + WebXmlEditorTest.SERVLET_DESCRIPTION + "</description>";
    assertTrue("Web.xml editor has to contain text '" +
      textToContain +
      "' but it doesn't.",
      editorText.toLowerCase().contains(textToContain.toLowerCase()));
    // try to add new servlet mapping
    tiWebXml.getNode(WebXmlEditorTest.SERVLETS_NODE).select();
    bot.button(IDELabel.Button.ADD,1).click();
    bot.shell(IDELabel.WebXmlEditor.ADD_SERVLET_MAPPING_DIALOG_TITLE).activate();
    bot.comboBoxWithLabel(IDELabel.WebXmlEditor.ADD_SERVLET_MAPPING_DIALOG_SERVLET_NAME_LABEL)
      .setText(WebXmlEditorTest.SERVLET_NAME);
    bot.textWithLabel(IDELabel.WebXmlEditor.ADD_SERVLET_MAPPING_DIALOG_URL_PATTERN_LABEL)
      .setText(WebXmlEditorTest.URL_PATTERN);
    bot.button(IDELabel.Button.FINISH).click();
    webXmlConfigEditor.save();
    editorText = webXmlConfigEditorExt.getText()
      .replaceAll("\n", "")
      .replaceAll("\t", "")
      .replaceAll("\r", "")
      .replaceAll(" ", "");
    textToContain = "<servlet-mapping><servlet-name>" + WebXmlEditorTest.SERVLET_NAME + "</servlet-name>";
    assertTrue("Web.xml editor has to contain text '" +
      textToContain +
      "' but it doesn't.",
      editorText.toLowerCase().contains(textToContain.toLowerCase()));
    textToContain = "<url-pattern>" + WebXmlEditorTest.URL_PATTERN + "</url-pattern></servlet-mapping>";
    assertTrue("Web.xml editor has to contain text '" +
      textToContain +
      "' but it doesn't.",
      editorText.toLowerCase().contains(textToContain.toLowerCase()));
  }

}