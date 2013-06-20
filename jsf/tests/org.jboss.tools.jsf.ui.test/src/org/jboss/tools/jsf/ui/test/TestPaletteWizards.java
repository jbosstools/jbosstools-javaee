package org.jboss.tools.jsf.ui.test;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IEditorPart;
import org.jboss.tools.common.model.ui.editors.dnd.DefaultDropWizardPage;
import org.jboss.tools.common.model.ui.editors.dnd.IElementGenerator;
import org.jboss.tools.common.model.ui.editors.dnd.TagAttributesWizardPage;
import org.jboss.tools.common.model.ui.editors.dnd.composite.TagAttributesComposite.AttributeDescriptorValue;
import org.jboss.tools.jsf.ui.wizard.palette.DataTableWizardPage;
import org.jboss.tools.jsf.ui.wizard.palette.OutputLinkWizardPage;
import org.jboss.tools.jsf.ui.wizard.palette.PanelGridWizardPage;
import org.jboss.tools.jsf.ui.wizard.palette.SelectItemsWizardPage;
import org.jboss.tools.jst.web.ui.palette.html.wizard.HTMLConstants;
import org.jboss.tools.jst.web.ui.test.AbstractPaletteEntryTest;

public class TestPaletteWizards extends AbstractPaletteEntryTest implements HTMLConstants {
	static String GROUP_HTML = "JSF HTML";
	IEditorPart editor = null;

	public TestPaletteWizards() {}

	public void setUp() {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("testJSFProject");
		editor = openEditor("WebContent/pages/testPalette.xhtml");
	}

	protected void tearDown() throws Exception {
		if(currentDialog != null) {
			currentDialog.close();
		}
		if(editor != null) {
			editor.getSite().getPage().closeEditor(editor, false);
			editor = null;
		}
		super.tearDown();
	}

	public void testNewCommandButtonWizard() {
		IWizardPage currentPage = runToolEntry(GROUP_HTML, "commandButton", true);
		assertTrue(currentPage instanceof TagAttributesWizardPage);
		TagAttributesWizardPage page = (TagAttributesWizardPage)currentPage;
		page.getDropWizardModel().setAttributeValue(ATTR_ALT, "aaaa");

		compareGeneratedAndInsertedText(page);
	}

	public void testNewOutputTextWizard() {
		IWizardPage currentPage = runToolEntry(GROUP_HTML, "outputText", true);
		assertTrue(currentPage instanceof TagAttributesWizardPage);
		TagAttributesWizardPage page = (TagAttributesWizardPage)currentPage;
		page.getDropWizardModel().setAttributeValue(ATTR_VALUE, "#{user.name}");

		compareGeneratedAndInsertedText(page);
	}

	public void testNewDataTableWizard() {
		IWizardPage currentPage = runToolEntry(GROUP_HTML, "dataTable", true);
		assertTrue(currentPage instanceof DataTableWizardPage);
		DataTableWizardPage page = (DataTableWizardPage)currentPage;
		page.getDropWizardModel().setAttributeValue("var", "v123");
		page.getDropWizardModel().setAttributeValue(ATTR_VALUE, "#{user.name}");

		compareGeneratedAndInsertedText(page);
	}

	public void testNewOutputLinkWizard() {
		IWizardPage currentPage = runToolEntry(GROUP_HTML, "outputLink", true);
		assertTrue(currentPage instanceof OutputLinkWizardPage);
		OutputLinkWizardPage page = (OutputLinkWizardPage)currentPage;
		page.setText("abcde");
		page.getDropWizardModel().setAttributeValue(ATTR_VALUE, "#{user.name}");
		compareGeneratedAndInsertedText(page);
	}

	public void testNewSelectManyCheckboxWizard() {
		IWizardPage currentPage = runToolEntry(GROUP_HTML, "selectManyCheckbox", true);
		assertTrue(currentPage instanceof SelectItemsWizardPage);
		SelectItemsWizardPage page = (SelectItemsWizardPage)currentPage;
		page.setText("xyz");
		page.getDropWizardModel().setAttributeValue(ATTR_VALUE, "#{user.name}");
		compareGeneratedAndInsertedText(page);
	}

	public void testNewPanelGridWizard() {
		IWizardPage currentPage = runToolEntry(GROUP_HTML, "panelGrid", true);
		assertTrue(currentPage instanceof PanelGridWizardPage);
		PanelGridWizardPage page = (PanelGridWizardPage)currentPage;
		page.setOptionFooterChecked(true);
//		printAttributes(page);
		compareGeneratedAndInsertedText(page);
	}

	void printAttributes(DefaultDropWizardPage page) {
		AttributeDescriptorValue[] values = page.getDropWizardModel().getAttributeValueDescriptors();
		for (int i = 0; i < values.length; i++) {
			System.out.println(values[i].getName() + "=" + values[i].getValue());
		}
	}

	private void compareGeneratedAndInsertedText(DefaultDropWizardPage page) {
		IElementGenerator g = page.getDropWizardModel().getElementGenerator();
		String generatedText = g.generateStartTag() + g.generateEndTag();
		page.getWizard().performFinish();

		String insertedText = getInsertedText();
		System.out.println(insertedText);
		assertTrue(isSameHTML(generatedText, insertedText));
	}

}
