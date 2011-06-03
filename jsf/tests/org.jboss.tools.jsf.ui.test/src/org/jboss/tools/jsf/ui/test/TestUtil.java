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

package org.jboss.tools.jsf.ui.test;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditorPart;
import org.jboss.tools.test.util.JobUtils;

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
		JobUtils.delay(waitTimeMillis);
	}
	
	public static void waitForIdle(long maxIdle) {
		JobUtils.waitForIdle(500, maxIdle);
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
