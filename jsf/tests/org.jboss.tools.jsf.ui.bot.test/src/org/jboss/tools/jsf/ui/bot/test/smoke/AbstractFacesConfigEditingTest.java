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

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefViewer;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.jsf.ui.bot.test.smoke.gefutils.FacesConfigGefEditorBot;
import org.jboss.tools.jsf.ui.bot.test.smoke.gefutils.FacesConfigGefEditorPartMatcher;
import org.jboss.tools.jsf.ui.bot.test.smoke.gefutils.FacesConfigGefEditorUtil;
import org.jboss.tools.ui.bot.ext.Assertions;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.helper.DragAndDropHelper;
import org.jboss.tools.ui.bot.ext.helper.TableHelper;
import org.jboss.tools.ui.bot.ext.parts.SWTBotEditorExt;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;
/** Parent test for editing of faces-config.xml file
 * @author Vladimir Pakan
 *
 */
public abstract class AbstractFacesConfigEditingTest extends JSFAutoTestCase{
  
  protected static final String FACES_CONFIG_FILE_NAME = "faces-config.xml";
  private SWTBotEditor facesConfigEditor;
  private String originalContent;
  private SWTBotEditorExt facesConfigEditorExt;
  private SWTBotExt botExt;
  private SWTBotGefEditPart gefObjectAddedViaViewTool = null;
  private SWTBotGefEditPart gefObjectAddedViaDnDTool = null;
  private SWTBotGefViewer gefViewer = null;
  private String testProjectName = null;
  
  protected static enum TestProjectType {
    JSF,
    JSF2; 
  }
  
  @Override
  public void setUp() throws Exception {
    super.setUp();
    eclipse.closeAllEditors();
    intializeTestProject();
    facesConfigEditor = getFacesConfigEditor();
    testProjectName = getTestProjectName();
    originalContent = facesConfigEditor.toTextEditor().getText();
    facesConfigEditorExt = new SWTBotEditorExt(facesConfigEditor.toTextEditor().getReference(),bot);
    botExt = new SWTBotExt();
  }
  
  @Override
  public void tearDown() throws Exception {
    if (gefViewer != null){
      if (gefObjectAddedViaViewTool != null){
        gefObjectAddedViaViewTool.click();
        bot.sleep(Timing.time1S());
        addIgnoredExceptionFromEclipseLog("java.lang.Exception");
        gefViewer.clickContextMenu(IDELabel.Menu.DELETE);
        removeIgnoredExceptionFromEclipseLog("java.lang.Exception");
        confirmViewDelete();
        bot.sleep(Timing.time1S());
      }
      if (gefObjectAddedViaDnDTool != null){
        gefObjectAddedViaDnDTool.click();
        bot.sleep(Timing.time1S());
        gefViewer.clickContextMenu(IDELabel.Menu.DELETE);
        confirmViewDelete();
        bot.sleep(Timing.time1S());
      }      
    }
    if (facesConfigEditor != null) {
      facesConfigEditor.toTextEditor().setText(originalContent);
      facesConfigEditor.save();
      facesConfigEditor.close();
      bot.sleep(Timing.time1S());
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
    final String managedBeanName = "TestBean"; 
    final String managedBeanClass = "TestBeanClass";
    SWTBotTreeItem tiFacesConfigXml = tree.expandNode(AbstractFacesConfigEditingTest.FACES_CONFIG_FILE_NAME);
    SWTBotTreeItem tiManagedbean = tiFacesConfigXml.getNode(IDELabel.FacesConfigEditor.MANAGED_BEANS_NODE);
    tiManagedbean.select();
    bot.sleep(Timing.time1S());
    // Add managed bean
    editorBot.button(IDELabel.Button.ADD).click();
    bot.shell(AbstractFacesConfigEditingTest.getAddManagedBeanDialogTitle(getTestProjectType())).activate();
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
        testProjectName,"JavaSource");
    Assertions.assertSourceEditorContains(stripXMLSourceText(facesConfigEditorExt.getText()),
        "<managed-bean><managed-bean-name>" + managedBeanName + "</managed-bean-name>" +
        "<managed-bean-class>" + managedBeanClass + "</managed-bean-class>" +
        "<managed-bean-scope>request</managed-bean-scope></managed-bean>",
        AbstractFacesConfigEditingTest.FACES_CONFIG_FILE_NAME);
    // Modify Managed Bean
    editorBot.textWithLabel(IDELabel.FacesConfigEditor.MANAGED_BEAN_CLASS_LABEL)
      .setText(managedBeanClass + "xxqq");
    facesConfigEditorExt.save();
    bot.sleep(Timing.time1S());
    if (getCheckForExistingManagedBeanClass()){
      assertFacesConfigXmlHasValidationProblems(botExt);
    }      
    editorBot.textWithLabel(IDELabel.FacesConfigEditor.MANAGED_BEAN_CLASS_LABEL)
     .setText(managedBeanClass);
    facesConfigEditorExt.save();
    bot.sleep(Timing.time1S());
    // Delete Managed Bean and add it back via New Managed Bean Form
    tiManagedbean.select();
    editorBot.table().select(managedBeanName);
    editorBot.button(IDELabel.Button.REMOVE_WITH_DOTS).click();
    bot.shell(IDELabel.Shell.CONFIRMATION).activate();
    bot.checkBox(IDELabel.FacesConfigEditor.DELETE_JAVA_SOURCE_CHECK_BOX).deselect();
    bot.button(IDELabel.Button.OK).click();
    editorBot.button(IDELabel.Button.ADD).click();
    bot.shell(AbstractFacesConfigEditingTest.getAddManagedBeanDialogTitle(getTestProjectType())).activate();
    bot.button(IDELabel.Button.BROWSE).click();
    bot.shell(IDELabel.Shell.SELECT_CLASS).activate();
    bot.text().setText(managedBeanClass);
    bot.sleep(Timing.time2S());
    final String selectedClassLabel = TableHelper.getSelectionText(bot.table());
    assertTrue ("Selected item in table has to start with " + managedBeanClass +
        "\n but is:\n" + selectedClassLabel,
      selectedClassLabel.startsWith(managedBeanClass));
    bot.button(IDELabel.Button.OK).click();
    bot.textWithLabel(IDELabel.FacesConfigEditor.NEW_MANAGED_BEAN_NAME_LABEL)
      .setText(managedBeanName);
    bot.button(IDELabel.Button.FINISH).click();
    facesConfigEditorExt.save();
    bot.sleep(Timing.time1S());
    Assertions.assertSourceEditorContains(stripXMLSourceText(facesConfigEditorExt.getText()),
        "<managed-bean><managed-bean-name>" + managedBeanName + "</managed-bean-name>" +
        "<managed-bean-class>" + managedBeanClass + "</managed-bean-class>" +
        "<managed-bean-scope>request</managed-bean-scope></managed-bean>",
        AbstractFacesConfigEditingTest.FACES_CONFIG_FILE_NAME);
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
        testProjectName,"JavaSource");
    Assertions.assertSourceEditorNotContain(facesConfigEditorExt.getText(), 
        "<managed-bean-name>" + managedBeanName + "</managed-bean-name>",
        AbstractFacesConfigEditingTest.FACES_CONFIG_FILE_NAME);
    Assertions.assertSourceEditorNotContain(facesConfigEditorExt.getText(), 
        "<managed-bean-class>" + managedBeanClass + "</managed-bean-class>",
        AbstractFacesConfigEditingTest.FACES_CONFIG_FILE_NAME);
  }
  /**
   * Tests Component editing
   */
  public void testComponent (){
    checkFacesConfigNodeEditing(IDELabel.FacesConfigEditor.COMPONENTS_NODE,
        IDELabel.Shell.ADD_COMPONENT,
        IDELabel.FacesConfigEditor.NEW_COMPONENT_TYPE_LABEL,
        "TestComponentType",
        IDELabel.FacesConfigEditor.NEW_COMPONENT_CLASS_LABEL,
        "TestComponentClass",
        "component",
        "component-type",
        "component-class",
        true);
  }
  /**
   * Tests Converter editing
   */
  public void testConverter (){
    checkFacesConfigNodeEditing(IDELabel.FacesConfigEditor.CONVERTERS_NODE,
        IDELabel.Shell.ADD_CONVERTER,
        IDELabel.FacesConfigEditor.NEW_CONVERTER_ID_LABEL,
        "TestConverterID",
        IDELabel.FacesConfigEditor.NEW_CONVERTER_CLASS_LABEL,
        "TestConverterClass",
        "converter",
        "converter-id",
        "converter-class",
        true);
  }
  /**
   * Tests Referenced Bean editing
   */
  public void testReferencedBean (){
    checkFacesConfigNodeEditing(IDELabel.FacesConfigEditor.REFERENCED_BEAN_NODE,
        IDELabel.Shell.ADD_REFERENCED_BEAN,
        IDELabel.FacesConfigEditor.NEW_REFERENCED_BEAN_NAME_LABEL,
        "TestReferencedBeanName",
        IDELabel.FacesConfigEditor.NEW_REFERENCED_BEAN_CLASS_LABEL,
        "TestReferencedBeanClass",
        "referenced-bean",
        "referenced-bean-name",
        "referenced-bean-class",
        true);
  }
  /**
   * Tests Render Kit editing
   */
  public void testRenderKit (){
    checkFacesConfigNodeEditing(IDELabel.FacesConfigEditor.RENDER_KITS_NODE,
        AbstractFacesConfigEditingTest.getAddRenderKitDialogTitle(getTestProjectType()),
        IDELabel.FacesConfigEditor.NEW_RENDER_KIT_ID_LABEL,
        "TestRenderKitID",
        IDELabel.FacesConfigEditor.NEW_RENDER_KIT_CLASS_LABEL,
        "TestRenderKitClass",
        "render-kit",
        "render-kit-id",
        "render-kit-class",
        false);
  }
  /**
   * Tests Validator editing
   */
  public void testValidator (){
    checkFacesConfigNodeEditing(IDELabel.FacesConfigEditor.VALIDATOR_NODE,
        IDELabel.Shell.ADD_VALIDATOR,
        IDELabel.FacesConfigEditor.NEW_VALIDATOR_ID_LABEL,
        "TestValidatorID",
        IDELabel.FacesConfigEditor.NEW_VALIDATOR_CLASS_LABEL,
        "TestValidatorClass",
        "validator",
        "validator-id",
        "validator-class",
        false);
  }
  /**
   * Asserts if faces-config.xml has no errors 
   * @param botExt
   */
  protected static void assertFacesConfigXmlHasNoErrors (SWTBotExt botExt){
    
    SWTBotTreeItem[] errors = ProblemsView.getFilteredErrorsTreeItems(botExt, null, null, AbstractFacesConfigEditingTest.FACES_CONFIG_FILE_NAME, null);
    boolean areThereNoErrors = ((errors == null) || (errors.length == 0));
    assertTrue("There are errors in Problems view: " + 
        (areThereNoErrors ? "" : errors[0].getText()),
      areThereNoErrors);
  }
  /**
   * Asserts if faces-config.xml has errors 
   * @param botExt
   */
  protected static void assertFacesConfigXmlHasValidationProblems (SWTBotExt botExt){
    
    SWTBotTreeItem[] problems = ProblemsView.getFilteredErrorsTreeItems(botExt, null, null, AbstractFacesConfigEditingTest.FACES_CONFIG_FILE_NAME, null);
    boolean areThereProblems = ((problems != null) && (problems.length > 0));
    if (!areThereProblems){
      problems = ProblemsView.getFilteredWarningsTreeItems(botExt, "references to non-existent class", null,
        AbstractFacesConfigEditingTest.FACES_CONFIG_FILE_NAME, null);
      areThereProblems = ((problems != null) && (problems.length > 0));
    }
    assertTrue("There are missing problems in Problems view for " + AbstractFacesConfigEditingTest.FACES_CONFIG_FILE_NAME + " file.",
        areThereProblems);
  }
  /**
   * Check editing of particular tree node within Face Config Editor Tree
   * @param treeNodeLabel
   * @param addWizardTitle
   * @param nameTextLabel
   * @param typeTextValue
   * @param classTextLabel
   * @param classTextValue
   * @param xmlNodeName
   * @param nameXmlNodeName
   * @param classXmlNodeName
   * @param checkForValdiationErrors
   */
  protected void checkFacesConfigNodeEditing(String treeNodeLabel, 
      String addWizardTitle,
      String nameTextLabel,
      String typeTextValue,
      String classTextLabel,
      String classTextValue,
      String xmlNodeName,
      String nameXmlNodeName,
      String classXmlNodeName,
      boolean checkForValdiationErrors){
    
    facesConfigEditorExt.selectPage(IDELabel.FacesConfigEditor.TREE_TAB_LABEL);
    SWTBot editorBot = facesConfigEditorExt.bot();
    SWTBotTree tree = editorBot.tree();
    SWTBotTreeItem tiFacesConfigXml = tree.expandNode(AbstractFacesConfigEditingTest.FACES_CONFIG_FILE_NAME);
    SWTBotTreeItem tiNodeToCheck = tiFacesConfigXml.getNode(treeNodeLabel);
    tiNodeToCheck.select();
    bot.sleep(Timing.time1S());
    // Add Node
    editorBot.button(IDELabel.Button.ADD).click();
    bot.shell(addWizardTitle).activate();
    bot.textWithLabel(nameTextLabel).setText(typeTextValue);
    bot.textWithLabel(classTextLabel).setText(classTextValue);
    bot.button(IDELabel.Button.FINISH).click();
    facesConfigEditorExt.save();
    bot.sleep(Timing.time1S());
    if (checkForValdiationErrors){
      assertFacesConfigXmlHasValidationProblems(botExt);  
    }    
    final String selectedNode = tree.selection().get(0,0);
    assertTrue ("Selected node has to have label '" + typeTextValue +"'\n" +
        "but has '" + selectedNode + "'.", 
      selectedNode.equals(typeTextValue));
    Assertions.assertSourceEditorContains(stripXMLSourceText(facesConfigEditorExt.getText()),
        "<" + xmlNodeName + ">" +
        "<" + nameXmlNodeName + ">" + typeTextValue + "</" + nameXmlNodeName +">" +
        "<" + classXmlNodeName + ">" + classTextValue + "</" + classXmlNodeName +">" +
        "</" + xmlNodeName + ">", 
        AbstractFacesConfigEditingTest.FACES_CONFIG_FILE_NAME);
    // Delete Node
    tiNodeToCheck.select();
    editorBot.table().select(typeTextValue);
    editorBot.button(IDELabel.Button.REMOVE_WITH_DOTS).click();
    bot.shell(IDELabel.Shell.CONFIRMATION).activate();
    bot.button(IDELabel.Button.OK).click();
    boolean nodeWasDeleted = false;
    try{
      editorBot.table().select(typeTextValue);
    } catch (WidgetNotFoundException wnfe){
      nodeWasDeleted = true;
    } catch (IllegalArgumentException iae){
      nodeWasDeleted = true;
    }
    assertTrue(typeTextValue + " was not deleted properly.",
        nodeWasDeleted);
    Assertions.assertSourceEditorNotContain(facesConfigEditorExt.getText(), 
        "<" + nameXmlNodeName + ">" + typeTextValue + "</" + nameXmlNodeName +">",
        AbstractFacesConfigEditingTest.FACES_CONFIG_FILE_NAME);
    Assertions.assertSourceEditorNotContain(facesConfigEditorExt.getText(), 
        "<" + classXmlNodeName + ">" + classTextValue + "</" + classXmlNodeName +">",
        AbstractFacesConfigEditingTest.FACES_CONFIG_FILE_NAME);
  }
  /**
   * Test editing via Diagram tab
   */
  public void testDiagramEditing(){
    final int verticalSpacing = 100;
    facesConfigEditorExt.selectPage(IDELabel.FacesConfigEditor.DIAGRAM_TAB_LABEL);
    final FacesConfigGefEditorBot gefEditorBot = new FacesConfigGefEditorBot(facesConfigEditorExt.getReference());
    gefViewer =  gefEditorBot.getViewer();
    SWTBotGefEditPart mainPart = gefViewer.mainEditPart();
    // add View to diagram via pallete tool
    gefViewer.activateTool(IDELabel.FacesConfigEditor.GEF_VIEW_TEMPLATE_TOOL);
    SWTBotGefEditPart gefObjectPart = mainPart.descendants(
        new FacesConfigGefEditorPartMatcher(AbstractFacesConfigEditingTest.getInputNamePageName(getTestProjectType()))).get(0);
    gefViewer.click(FacesConfigGefEditorUtil.getGefPartPosition(gefObjectPart).x,
        FacesConfigGefEditorUtil.getGefPartPosition(gefObjectPart).y + verticalSpacing);
    final String viewAddedViaToolName = "addedViaTool";
    handleNewViewWizard(viewAddedViaToolName + 
        AbstractFacesConfigEditingTest.getNewPagesExtension(getTestProjectType()));
    bot.sleep(Timing.time3S());
    facesConfigEditor.save();
    bot.sleep(Timing.time3S());
    assertFacesConfigXmlHasNoErrors(botExt);
    Assertions.assertFileExistsInWorkspace(viewAddedViaToolName + 
          AbstractFacesConfigEditingTest.getNewPagesExtension(getTestProjectType()),
        getTestProjectName(),
        "WebContent");
    gefViewer.activateTool(IDELabel.FacesConfigEditor.GEF_CREATE_NEW_CONNECTION_TOOL);
    gefObjectPart.click();
    bot.sleep(Timing.time1S());
    gefObjectAddedViaViewTool = mainPart.descendants(new FacesConfigGefEditorPartMatcher("/" + viewAddedViaToolName + 
        AbstractFacesConfigEditingTest.getNewPagesExtension(getTestProjectType()))).get(0);
    gefObjectAddedViaViewTool.click();
    bot.sleep(Timing.time1S());
    facesConfigEditor.save();
    bot.sleep(Timing.time3S());
    Assertions.assertSourceEditorContains(AbstractFacesConfigEditingTest.stripXMLSourceText(facesConfigEditorExt.getText()), 
        "<navigation-case><from-outcome>" + viewAddedViaToolName +
        "</from-outcome><to-view-id>/" + viewAddedViaToolName + 
        AbstractFacesConfigEditingTest.getNewPagesExtension(getTestProjectType()) +
        "</to-view-id></navigation-case>", 
        AbstractFacesConfigEditingTest.FACES_CONFIG_FILE_NAME);
    // add View to Diagram via D'n'D
    final String dndPageName = "testDnDPage";
    createTestPage(dndPageName + 
        AbstractFacesConfigEditingTest.getNewPagesExtension(getTestProjectType()));
    facesConfigEditor.show();
    facesConfigEditor.setFocus();
    SWTBotTreeItem tiPage = SWTEclipseExt.selectTreeLocation(open.viewOpen(ActionItem.View.JBossToolsWebWebProjects.LABEL).bot(),
        getTestProjectName(),
        "WebContent",
        "pages",
        dndPageName + AbstractFacesConfigEditingTest.getNewPagesExtension(getTestProjectType()));
    DragAndDropHelper.dnd((TreeItem)tiPage.widget, (FigureCanvas) gefEditorBot.getControl());
    facesConfigEditor.save();
    bot.sleep(Timing.time3S());
    assertFacesConfigXmlHasNoErrors(botExt);
    gefViewer.activateTool(IDELabel.FacesConfigEditor.GEF_CREATE_NEW_CONNECTION_TOOL);
    gefObjectPart.click();
    bot.sleep(Timing.time1S());
    gefObjectAddedViaDnDTool = mainPart.descendants(new FacesConfigGefEditorPartMatcher("/pages/" + dndPageName + 
        AbstractFacesConfigEditingTest.getNewPagesExtension(getTestProjectType()))).get(0);
    gefObjectAddedViaDnDTool.click();
    bot.sleep(Timing.time1S());
    gefViewer.click(FacesConfigGefEditorUtil.getGefPartPosition(gefObjectAddedViaDnDTool).x,
        FacesConfigGefEditorUtil.getGefPartPosition(gefObjectAddedViaDnDTool).y + verticalSpacing);
    gefViewer.clickContextMenu(IDELabel.Menu.AUTO_LAYOUT);
    bot.shell(IDELabel.Shell.AUTO_LAYOUT).activate();
    bot.button(IDELabel.Button.OK).click();
    facesConfigEditor.save();
    bot.sleep(Timing.time3S());
    Assertions.assertSourceEditorContains(AbstractFacesConfigEditingTest.stripXMLSourceText(facesConfigEditorExt.getText()), 
        "<navigation-case><from-outcome>" + dndPageName +
        "</from-outcome><to-view-id>/pages/" + dndPageName + 
        AbstractFacesConfigEditingTest.getNewPagesExtension(getTestProjectType()) + 
        "</to-view-id></navigation-case>", 
        AbstractFacesConfigEditingTest.FACES_CONFIG_FILE_NAME);
  }
  /**
   * Handle adding new View
   * @param fromViewID
   */
  protected void handleNewViewWizard(String fromViewID){
    SWTBot dialogBot = bot.shell(IDELabel.Shell.NEW_VIEW).activate().bot();
    dialogBot.textWithLabel(IDELabel.NewViewDialog.FROM_VIEW_ID_TEXT_LABEL)
      .setText(fromViewID);
    dialogBot.checkBox(IDELabel.NewViewDialog.CREATE_FILE_ON_DISK_CHECKBOX_LABEL)
      .select();
    dialogBot.button(IDELabel.Button.FINISH).click();
    
  }
  /**
   * Returns XML Source striped from spaces, tabs and EOL
   * 
   * @return String
   */
  protected static String stripXMLSourceText(String editorText) {
    return editorText.replaceAll("\n", "").replaceAll("\t", "")
        .replaceAll("\b", "").replaceAll(" ", "").replaceAll("\r", "")
        .replaceAll("\f", "");
  }
  /**
   * Confirm deletion of View from Diagram Editor
   */
  protected void confirmViewDelete(){
    bot.shell(IDELabel.Shell.CONFIRMATION).activate();
    bot.checkBox(IDELabel.FacesConfigEditor.DELETE_FILE_FROM_DISK_CHECK_BOX).select();
    bot.button(IDELabel.Button.OK).click();
  }
  /**
   * Returns proper Add Managed Bean Dialog Title 
   * @param testProjectType
   * @return
   */
  private static String getAddManagedBeanDialogTitle(TestProjectType testProjectType){
    String result;
    if (testProjectType.equals(TestProjectType.JSF)){
      result = IDELabel.Shell.NEW_MANAGED_BEAN;
    }
    else if (testProjectType.equals(TestProjectType.JSF2)){
      result = IDELabel.Shell.NEW_MANAGED_BEAN_JSF2;
    }
    else {
      throw new IllegalArgumentException("Not supported TestProjectType " + testProjectType);
    }
    
    return result;
    
  }
  /**
   * Returns proper Add Render Kit Dialog Title
   * @param testProjectType
   * @return
   */
  private static String getAddRenderKitDialogTitle(TestProjectType testProjectType){
    String result;
    if (testProjectType.equals(TestProjectType.JSF)){
      result = IDELabel.Shell.ADD_RENDER_KIT;
    }
    else if (testProjectType.equals(TestProjectType.JSF2)){
      result = IDELabel.Shell.ADD_RENDER_KIT_JSF2;
    }
    else {
      throw new IllegalArgumentException("Not supported TestProjectType " + testProjectType);
    }
    
    return result;
    
  }
  /**
   * Returns proper Input Name Page Name
   * @param testProjectType
   * @return
   */
  private static String getInputNamePageName(TestProjectType testProjectType){
    String result;
    if (testProjectType.equals(TestProjectType.JSF)){
      result = "/pages/inputUserName.jsp";
    }
    else if (testProjectType.equals(TestProjectType.JSF2)){
      result = "/pages/inputname.xhtml";
    }
    else {
      throw new IllegalArgumentException("Not supported TestProjectType " + testProjectType);
    }
    
    return result;
    
  }
  /**
   * Returns proper Extension for new Web Pages
   * @param testProjectType
   * @return
   */
  private static String getNewPagesExtension(TestProjectType testProjectType){
    String result;
    if (testProjectType.equals(TestProjectType.JSF)){
      result = ".jsp";
    }
    else if (testProjectType.equals(TestProjectType.JSF2)){
      result = ".xhtml";
    }
    else {
      throw new IllegalArgumentException("Not supported TestProjectType " + testProjectType);
    }
    
    return result;
    
  }
  
  private void createTestPage (String testPageName){
    if (testPageName.endsWith(".jsp")){
      createJspPage(testPageName, getTestProjectName(), "WebContent", "pages");
    }
    else if (testPageName.endsWith(".xhtml")){
      createXhtmlPage(testPageName, getTestProjectName(), "WebContent", "pages");
    }
    else {
      throw new IllegalArgumentException("Not supported Test Page Extension when creating page " + testPageName);
    }
  }
  /**
   * Returns current SWTBotEditor
   * @return
   */
  protected abstract SWTBotEditor getFacesConfigEditor();
  /**
   * Returns Test Project Name
   * @return
   */
  protected abstract String getTestProjectName();
  /**
   * Initializes Test Project
   */
  protected abstract void intializeTestProject();
  /**
   * Returns Test Project Type
   * @return
   */
  protected abstract TestProjectType getTestProjectType();
  /**
   * Returns true when test has to check if Managed Class exists
   * @return
   */
  protected abstract boolean getCheckForExistingManagedBeanClass();
  
}
  
