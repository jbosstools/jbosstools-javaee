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
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.ui.bot.ext.Assertions;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTUtilExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.parts.SWTBotEditorExt;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;
import org.jboss.tools.vpe.ui.bot.test.VPEAutoTestCase;
/** Test Editing of faces-config.xml file
 * @author Vladimir Pakan
 *
 */
public class FacesConfigEditingTest extends JSFAutoTestCase{
  
  private static final String FACES_CONFIG_FILE_NAME = "faces-config.xml";
  private SWTBotEditor facesConfigEditor;
  private String originalContent;
  private SWTBotEditorExt facesConfigEditorExt;
  private SWTBotExt botExt;
  
  @Override
  public void setUp() throws Exception {
    super.setUp();
    facesConfigEditor = eclipse.openFile(VPEAutoTestCase.JBT_TEST_PROJECT_NAME, 
        "WebContent",
        "WEB-INF",
        FacesConfigEditingTest.FACES_CONFIG_FILE_NAME);
    originalContent = facesConfigEditor.toTextEditor().getText();
    facesConfigEditorExt = new SWTBotEditorExt(facesConfigEditor.toTextEditor().getReference(),bot);
    botExt = new SWTBotExt();
  }

  @Override
  public void tearDown() throws Exception {
    if (facesConfigEditor != null) {
      facesConfigEditor.toTextEditor().setText(originalContent);
      facesConfigEditor.saveAndClose();
    }
    super.tearDown();
  }
  /**
   * Test Managed Bean editing
   */
  public void testManagedBean(){
    facesConfigEditorExt.selectPage(IDELabel.FacesConfigEditor.TREE_TAB_LABEL);
    SWTBot editorBot = facesConfigEditorExt.bot();
    SWTBotTree tree = editorBot.tree();
    SWTUtilExt.displayAllBotWidgets(facesConfigEditor.bot());
    final String managedBeanName = "TestBean"; 
    final String managedBeanClass = "TestBeanClass";
    SWTBotTreeItem tiFacesConfigXml = tree.expandNode(FacesConfigEditingTest.FACES_CONFIG_FILE_NAME);
    SWTBotTreeItem tiManagedbean = tiFacesConfigXml.getNode(IDELabel.FacesConfigEditor.MANAGED_BEANS_NODE);
    tiManagedbean.select();
    bot.sleep(Timing.time1S());
    // Add managed bean
    editorBot.button(IDELabel.Button.ADD).click();
    bot.shell(IDELabel.Shell.NEW_MANAGED_BEAN).activate();
    bot.textWithLabel(IDELabel.FacesConfigEditor.NEW_MANAGED_BEAN_CLASS_LABEL)
      .setText(managedBeanClass);
    bot.textWithLabel(IDELabel.FacesConfigEditor.NEW_MANAGED_BEAN_NAME_LABEL)
      .setText(managedBeanName);
    bot.button(IDELabel.Button.FINISH).click();
    facesConfigEditorExt.save();
    bot.sleep(Timing.time1S());
    assertFacesConfigXmlHasNoErrors(botExt);
    final String selectedNode = tree.selection().get(0,0);
    assertTrue ("Selected node has to have label '" + managedBeanName +"'\n" +
        "but has '" + selectedNode + "'.", 
      selectedNode.equals(managedBeanName));
    Assertions.assertFileExistsInWorkspace(managedBeanClass + ".java",
        JBT_TEST_PROJECT_NAME,"JavaSource");
    Assertions.assertSourceEditorContains(facesConfigEditorExt.getText(), 
        "<managed-bean-name>" + managedBeanName + "</managed-bean-name>", 
        FacesConfigEditingTest.FACES_CONFIG_FILE_NAME);
    Assertions.assertSourceEditorContains(facesConfigEditorExt.getText(), 
        "<managed-bean-class>" + managedBeanClass + "</managed-bean-class>", 
        FacesConfigEditingTest.FACES_CONFIG_FILE_NAME);
    SWTUtilExt.displayAllBotWidgets(facesConfigEditor.bot());
    // Modify Managed Bean
    editorBot.textWithLabel(IDELabel.FacesConfigEditor.MANAGED_BEAN_CLASS_LABEL)
      .setText(managedBeanClass + "xxqq");
    facesConfigEditorExt.save();
    bot.sleep(Timing.time1S());
    assertFacesConfigXmlHasErrors(botExt);
    editorBot.textWithLabel(IDELabel.FacesConfigEditor.MANAGED_BEAN_CLASS_LABEL)
     .setText(managedBeanClass);
    facesConfigEditorExt.save();
    bot.sleep(Timing.time1S());
    // Delete Managed Bean
    tiManagedbean.select();
    editorBot.table().select(managedBeanName);
    editorBot.button(IDELabel.Button.REMOVE_WITH_DOTS).click();
    bot.shell(IDELabel.Shell.CONFIRMATION).activate();
    bot.checkBox(IDELabel.FacesConfigEditor.DELETE_JAVA_SOURCE_CHECK_BOX).select();
    bot.button(IDELabel.Button.OK).click();
    boolean managedBeanWasDeleted = false;
    try{
      editorBot.table().select(managedBeanName);
    } catch (WidgetNotFoundException wnfe){
      managedBeanWasDeleted = true;
    } catch (IllegalArgumentException iae){
      managedBeanWasDeleted = true;
    }
    assertTrue("Managed bean " + managedBeanName + " was not deleted properly.",
        managedBeanWasDeleted);
    Assertions.assertFileNotExistsInWorkspace(managedBeanClass + ".java",
        JBT_TEST_PROJECT_NAME,"JavaSource");
    Assertions.assertSourceEditorNotContain(facesConfigEditorExt.getText(), 
        "<managed-bean-name>" + managedBeanName + "</managed-bean-name>",
        FacesConfigEditingTest.FACES_CONFIG_FILE_NAME);
    Assertions.assertSourceEditorNotContain(facesConfigEditorExt.getText(), 
        "<managed-bean-class>" + managedBeanClass + "</managed-bean-class>",
        FacesConfigEditingTest.FACES_CONFIG_FILE_NAME);
  }
  /**
   * Asserts if faces-config.xml has no errors 
   * @param botExt
   */
  private static void assertFacesConfigXmlHasNoErrors (SWTBotExt botExt){
    
    SWTBotTreeItem[] errors = ProblemsView.getFilteredErrorsTreeItems(botExt, null, null, FacesConfigEditingTest.FACES_CONFIG_FILE_NAME, null);
    boolean areThereNoErrors = ((errors == null) || (errors.length == 0));
    assertTrue("There are errors in Problems view: " + 
        (areThereNoErrors ? "" : errors[0].getText()),
      areThereNoErrors);
  }
  /**
   * Asserts if faces-config.xml has errors 
   * @param botExt
   */
  private static void assertFacesConfigXmlHasErrors (SWTBotExt botExt){
    
    SWTBotTreeItem[] errors = ProblemsView.getFilteredErrorsTreeItems(botExt, null, null, FacesConfigEditingTest.FACES_CONFIG_FILE_NAME, null);
    boolean areThereErrors = ((errors != null) && (errors.length > 0));
    assertTrue("There are missing errors in Problems view for " + FacesConfigEditingTest.FACES_CONFIG_FILE_NAME + " file.",
        areThereErrors);
  }
  // adding Component
  // adding Custom Converter
  // adding Render Kit
  // adding Referenced Bean
  // adding Validator

}