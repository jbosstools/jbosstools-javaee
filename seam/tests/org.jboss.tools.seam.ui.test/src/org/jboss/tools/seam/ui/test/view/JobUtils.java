package org.jboss.tools.seam.ui.test.view;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

public class JobUtils {
	public static void waitUsersJobsAreFinished() {
		Job waitJob = new Job("Waiting while all users jobs are finished") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				return Status.OK_STATUS;
			}
		};
		waitJob.setUser(true);
		waitJob.schedule(5000);
		
		try {
			waitJob.join();
		} catch (InterruptedException e) {
		}
	}
	
	public static void waitForJobs() {
		   while (!Platform.getJobManager().isIdle())
		      delay(1000);
	}
	
	/**
	* Process UI input but do not return for the
	* specified time interval.
	*
	* @param delayMillis the number of milliseconds
	*/
	protected static void delay(long delayMillis) {
		Display currentDisplay = Display.getCurrent();
		
		// If this is the UI thread
		if (currentDisplay != null) {
			long endTimeMillis = System.currentTimeMillis() + delayMillis;
			while (System.currentTimeMillis() < endTimeMillis) {
				if (!currentDisplay.readAndDispatch())	currentDisplay.sleep();
			}
			currentDisplay.update();
		} else {
			// perform a simple sleep		
		   try {
			   Thread.sleep(delayMillis);
		   } catch (InterruptedException e) {
			   // Just allow it running
		   }
		}
	}
	
}
