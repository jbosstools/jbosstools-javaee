package org.jboss.tools.jsf.ui.test;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.util.WorkbenchUtils;

import junit.framework.TestCase;

public class PropertiesNewWizardTest extends TestCase {
	public void testPropertiesFileNewWizardTestIsCreated() {
		IWizard
		aWizard = WorkbenchUtils.findWizardByDefId("org.jboss.tools.common.model.ui.wizard.newfile.NewPropertiesFileWizard");
		
		WizardDialog dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				aWizard);
		dialog.setBlockOnOpen(false);
		dialog.open();
		try {
			IWizardPage startPage = aWizard.getStartingPage();
			assertNotNull(startPage);
		}catch(Exception ex){
			fail(ex.getMessage());
		} finally {
			dialog.close();
		}
		
	}
}
