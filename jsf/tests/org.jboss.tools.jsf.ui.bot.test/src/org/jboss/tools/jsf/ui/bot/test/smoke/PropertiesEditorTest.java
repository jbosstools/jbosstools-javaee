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

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.jboss.tools.jsf.ui.bot.test.JSFAutoTestCase;
import org.jboss.tools.ui.bot.ext.Assertions;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.helper.KeyboardHelper;
import org.jboss.tools.ui.bot.ext.parts.SWTBotEditorExt;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.vpe.ui.bot.test.VPEAutoTestCase;
/** Test for properties editor
 * @author Vladimir Pakan
 *
 */
public class PropertiesEditorTest extends JSFAutoTestCase{
  
  protected static final String PROPERTIES_FILE_NAME = "Messages.properties";
  private SWTBotEditor propertiesEditor;
  private String originalContent;
  private SWTBotEditorExt propertiesEditorExt;
    
  @Override
  public void setUp() throws Exception {
    super.setUp();
    eclipse.closeAllEditors();
    propertiesEditor = eclipse.openFile(VPEAutoTestCase.JBT_TEST_PROJECT_NAME, 
        "JavaSource",
        "demo",
        PropertiesEditorTest.PROPERTIES_FILE_NAME);
    originalContent = propertiesEditor.toTextEditor().getText();
    propertiesEditorExt = new SWTBotEditorExt(propertiesEditor.toTextEditor().getReference(),bot);
  }
  
  @Override
  public void tearDown() throws Exception {
    if (propertiesEditor != null) {
      propertiesEditor.toTextEditor().setText(originalContent);
      propertiesEditor.saveAndClose();
      bot.sleep(Timing.time1S());
    }
    super.tearDown();
  }
  /**
   * Tests Properties Editor
   */
  public void testPropertiesEditor(){
    propertiesEditorExt.selectPage(IDELabel.PropertiesEditor.SOURCE_TAB_LABEL);
    String[] originalLines = PropertiesEditorTest.splitEditorContentToLines(propertiesEditor.toTextEditor());
    propertiesEditorExt.selectPage(IDELabel.PropertiesEditor.PROPERTIES_TAB_LABEL);
    propertiesEditor.setFocus();
    // Add Property
    SWTBotTable propTable = bot.table();
    propTable.select(propTable.rowCount() - 1);
    bot.button(IDELabel.Button.ADD_WITHOUT_DOTS).click();
    bot.shell(IDELabel.Shell.ADD_PROPERTY).activate();
    final String newPropertyName = "newPropName";
    final String newPropertyValue = "newPropValue";
    bot.textWithLabel(IDELabel.PropertiesEditor.ADD_PROPERTIES_DIALOG_NAME_LABEL)
      .setText(newPropertyName);
    bot.textWithLabel(IDELabel.PropertiesEditor.ADD_PROPERTIES_DIALOG_VALUE_LABEL)
      .setText(newPropertyValue);
    bot.button(IDELabel.Button.FINISH).click();
    propertiesEditor.save();
    bot.sleep(Timing.time2S());
    int newPropertyIndex = originalLines.length;
    Assertions.assertSourceEditorContains(PropertiesEditorTest.stripXMLSourceText(
        propertiesEditor.toTextEditor().getText()), 
      getExpectedPropertiesEditorText(originalLines,
        newPropertyName,
        newPropertyValue,
        newPropertyIndex),
      PropertiesEditorTest.PROPERTIES_FILE_NAME);
    assertNotEnabled(bot.button(IDELabel.Button.DOWN));
    // Move Property
    propTable.select(newPropertyName);
    bot.button(IDELabel.Button.UP).click();
    propertiesEditor.save();
    bot.sleep(Timing.time2S());
    Assertions.assertSourceEditorContains(PropertiesEditorTest.stripXMLSourceText(
        propertiesEditor.toTextEditor().getText()), 
      getExpectedPropertiesEditorText(originalLines,
        newPropertyName,
        newPropertyValue,
        --newPropertyIndex),
      PropertiesEditorTest.PROPERTIES_FILE_NAME);
    assertEnabled(bot.button(IDELabel.Button.DOWN));
    assertEnabled(bot.button(IDELabel.Button.UP));
    bot.button(IDELabel.Button.DOWN).click();
    propertiesEditor.save();
    bot.sleep(Timing.time2S());
    Assertions.assertSourceEditorContains(PropertiesEditorTest.stripXMLSourceText(
        propertiesEditor.toTextEditor().getText()), 
      getExpectedPropertiesEditorText(originalLines,
        newPropertyName,
        newPropertyValue,
        ++newPropertyIndex),
      PropertiesEditorTest.PROPERTIES_FILE_NAME);
    assertNotEnabled(bot.button(IDELabel.Button.DOWN));
    assertEnabled(bot.button(IDELabel.Button.UP));
    // Move to Top
    while (newPropertyIndex > 0){
      bot.button(IDELabel.Button.UP).click();
      newPropertyIndex--;
    }
    propertiesEditor.save();
    bot.sleep(Timing.time2S());
    Assertions.assertSourceEditorContains(PropertiesEditorTest.stripXMLSourceText(
        propertiesEditor.toTextEditor().getText()), 
      getExpectedPropertiesEditorText(originalLines,
        newPropertyName,
        newPropertyValue,
        newPropertyIndex),
      PropertiesEditorTest.PROPERTIES_FILE_NAME);
    assertEnabled(bot.button(IDELabel.Button.DOWN));
    assertNotEnabled(bot.button(IDELabel.Button.UP));
    // Update Property Directly
    propTable.select(newPropertyIndex);
    final String updatedPropertyName = "updPropName";
    final String updatedPropertyValue = "updPropValue";
    propTable.select(newPropertyIndex);
    propTable.setFocus();
    bot.sleep(Timing.time2S());
    propTable.click(newPropertyIndex, 0);
    bot.sleep(Timing.time2S());
    KeyboardHelper.typeBasicStringUsingAWT(updatedPropertyName);
    propTable.click(newPropertyIndex, 1);
    KeyboardHelper.typeBasicStringUsingAWT(updatedPropertyValue);
    propTable.select(newPropertyIndex);
    propertiesEditor.save();
    bot.sleep(Timing.time2S());
    Assertions.assertSourceEditorContains(PropertiesEditorTest.stripXMLSourceText(
        propertiesEditor.toTextEditor().getText()), 
      getExpectedPropertiesEditorText(originalLines,
        updatedPropertyName,
        updatedPropertyValue,
        newPropertyIndex),
      PropertiesEditorTest.PROPERTIES_FILE_NAME);
    // Update Property via Dialog
    propTable.select(newPropertyIndex);
    bot.button(IDELabel.Button.EDIT_WITHOUT_DOTS).click();
    bot.shell(IDELabel.Shell.EDIT).activate();
    SWTBotText txName = bot.textWithLabel(IDELabel.PropertiesEditor.ADD_PROPERTIES_DIALOG_NAME_LABEL);
    assertEquals("Text with label Name: has to have value " + updatedPropertyName + 
        " but has " + txName.getText(),
      txName.getText(), updatedPropertyName);
    SWTBotText txValue = bot.textWithLabel(IDELabel.PropertiesEditor.ADD_PROPERTIES_DIALOG_VALUE_LABEL);
    assertEquals("Text with label Value: has to have value " + updatedPropertyValue + 
        " but has " + txValue.getText(),
      txValue.getText(), updatedPropertyValue);
    txName.setText(newPropertyName);
    txValue.setText(newPropertyValue);
    bot.button(IDELabel.Button.FINISH).click();
    propertiesEditor.save();
    bot.sleep(Timing.time2S());
    Assertions.assertSourceEditorContains(PropertiesEditorTest.stripXMLSourceText(
        propertiesEditor.toTextEditor().getText()), 
      getExpectedPropertiesEditorText(originalLines,
        newPropertyName,
        newPropertyValue,
        newPropertyIndex),
      PropertiesEditorTest.PROPERTIES_FILE_NAME);
    // Delete Property
    propTable.select(newPropertyIndex);
    bot.button(IDELabel.Button.DELETE).click();
    bot.shell(IDELabel.Shell.CONFIRMATION).activate();
    bot.button(IDELabel.Button.OK).click();
    propertiesEditor.save();
    bot.sleep(Timing.time2S());
    assertEquals("Properties file " + PropertiesEditorTest.PROPERTIES_FILE_NAME + 
        " should have content:\n" + originalContent +
        "\nbut has:]n" + propertiesEditor.toTextEditor().getText(),
      originalContent, propertiesEditor.toTextEditor().getText());
  }
  /**
   * Split editor content to particular lines
   * @param editor
   * @return
   */
  private static String[] splitEditorContentToLines(SWTBotEclipseEditor editor){
    
    String[] result = new String[editor.getLineCount()];
    
    for (int index = 0 ; index < result.length ; index ++){
      result[index] = editor.getTextOnLine(index);
    }
    
    return result;
    
  }
  /**
   * Returns Properties File Source striped from EOL
   * 
   * @return String
   */
  private static String stripXMLSourceText(String editorText) {
    return editorText.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\f", "");
  } 
  /**
   * Returns expected Properties Editor Text dependent on expected new Property position within
   * properties file
   * @param originalLines
   * @param propertyName
   * @param propertyValue
   * @param propertyIndex
   * @return
   */
  private static String getExpectedPropertiesEditorText (String[] originalLines, 
      String propertyName,
      String propertyValue,
      int propertyIndex){
    StringBuffer sbExpectedText = new StringBuffer("");
    
    for (int index = 0 ; index < propertyIndex ; index++){
      sbExpectedText.append(originalLines[index]);
    }
    sbExpectedText.append(propertyName);
    sbExpectedText.append("=");
    sbExpectedText.append(propertyValue);
    
    for (int index = propertyIndex ; index < originalLines.length ; index++){
      sbExpectedText.append(originalLines[index]);
    }
    
    return sbExpectedText.toString();
    
  }
}
  
