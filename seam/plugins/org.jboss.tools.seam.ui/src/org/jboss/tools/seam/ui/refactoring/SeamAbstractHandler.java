package org.jboss.tools.seam.ui.refactoring;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.Job;
import org.jboss.tools.seam.ui.SeamGuiPlugin;

public abstract class SeamAbstractHandler  extends AbstractHandler {
	protected static void saveAndBuild(){
		if(!SeamGuiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors(true))
			return;
		
		try {
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		} catch (InterruptedException e) {
			// do nothing
		}
	}
}
