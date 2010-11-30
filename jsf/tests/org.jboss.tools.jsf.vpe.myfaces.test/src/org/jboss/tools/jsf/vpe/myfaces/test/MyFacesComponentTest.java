package org.jboss.tools.jsf.vpe.myfaces.test;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.PartInitException;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;

public class MyFacesComponentTest extends VpeTest {

	// import project name
    public static final String IMPORT_PROJECT_NAME = "myFacesTest"; //$NON-NLS-1$
	
	public MyFacesComponentTest(String name) {
		super(name);
		setCheckWarning(false);
	}
	
	public void testAliasBean() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/aliasBean.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testCheckbox() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
			"components/checkbox.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	    }

	public void testCommandButton() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/commandButton.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testCommandLink() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/commandLink.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testCommandNavigation() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/commandNavigation.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testCommandSortHeader() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/commandSortHeader.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testDataList() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/dataList.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testDataScroller() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/dataScroller.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testDataTable() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/dataTable.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testJscookMenu() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/jscookMenu.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testInputDate() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/inputDate.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testIconProvider() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/iconProvider.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testInputText() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/inputText.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testInputTextarea() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/inputTextarea.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testInputFileUpload() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/inputFileUpload.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testInputCalendar() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/inputCalendar.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testMessage() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/message.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testMessages() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/messages.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testOutputLabel() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/outputLabel.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testNavigationMenuItem() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/navigationMenuItem.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testNavigationMenuItems() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/navigationMenuItems.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testOutputText() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/outputText.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testPanelNavigation() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/panelNavigation.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testPanelTab() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/panelTab.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testPanelTabbedPane() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/panelTabbedPane.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testPanelLayout() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/panelLayout.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testPanelStack() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/panelStack.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testRadio() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/radio.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testSaveState() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/saveState.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testSelectManyCheckbox() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/selectManyCheckbox.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testSelectOneRadio() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/selectOneRadio.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testSelectOneMenu() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/selectOneMenu.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testStylesheet() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/stylesheet.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testTabChangeListener() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/tabChangeListener.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testTree() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/tree.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testTreeSelectionListener() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/treeSelectionListener.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testUpdateActionListener() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/updateActionListener.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testValidateEmail() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/validateEmail.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testValidateRegExpr() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/validateRegExpr.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testValidateCreditCard() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/validateCreditCard.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testValidateEqual() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/validateEqual.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testValidateISBN() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/validateISBN.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testPopup() throws PartInitException, Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/popup.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

}
