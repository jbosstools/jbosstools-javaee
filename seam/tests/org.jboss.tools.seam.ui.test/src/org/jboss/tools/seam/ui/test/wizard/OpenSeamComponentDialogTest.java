package org.jboss.tools.seam.ui.test.wizard;

import junit.framework.TestCase;

import org.eclipse.ui.PlatformUI;
import org.jboss.tools.seam.ui.wizard.OpenSeamComponentDialog;

/**
 * @author Daniel Azarov
 * 
 */
public class OpenSeamComponentDialogTest extends TestCase{
	
	public void testOpenSeamComponentDialogIsCreated() {
		OpenSeamComponentDialog dialog = new OpenSeamComponentDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		dialog.open();
	}
}
