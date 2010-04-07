/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.ui.test;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditorPart;

import static junit.framework.Assert.*;

/**
 * 
 * @author yzhishko
 *
 */

public class TestUtil {
	
	  /** Editor in which we open visual page. */
    public final static String EDITOR_ID = "org.jboss.tools.jst.jsp.jspeditor.JSPTextEditor"; //$NON-NLS-1$
	
	/** The Constant MAX_IDLE. */
	public static final long MAX_IDLE = 15*1000L;

	public static void delay(long waitTimeMillis) {
		Display display = Display.getCurrent();
		if (display != null) {
			long endTimeMillis = System.currentTimeMillis() + waitTimeMillis;
			while (System.currentTimeMillis() < endTimeMillis) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.update();
		}
		// Otherwise, perform a simple sleep.
		else {
			try {
				Thread.sleep(waitTimeMillis);
			} catch (InterruptedException e) {
				// Ignored.
			}
		}
	}
	
	public static void waitForIdle(long maxIdle) {
		long start = System.currentTimeMillis();
		while (!Job.getJobManager().isIdle()) {
			delay(500);
			if ( (System.currentTimeMillis()-start) > maxIdle ) 
				throw new RuntimeException("A long running task detected"); //$NON-NLS-1$
		}
	}
	
	public static void waitForIdle() {
		waitForIdle(MAX_IDLE);
	}
	
    public static JSPMultiPageEditorPart openEditor(IEditorInput input) throws PartInitException {

        // get editor
        JSPMultiPageEditorPart part = (JSPMultiPageEditorPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
                input, EDITOR_ID, true);

        assertNotNull(part);
        return part;

    }

}
