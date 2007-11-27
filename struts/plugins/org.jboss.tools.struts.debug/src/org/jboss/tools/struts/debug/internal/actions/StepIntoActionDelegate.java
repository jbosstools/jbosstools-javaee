/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.struts.debug.internal.actions;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStep;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.jboss.tools.struts.debug.internal.StrutsDebugPlugin;
import org.jboss.tools.struts.debug.internal.actions.xpl.StepIntoHandler;

/**
 * @author Jeremy
 */
public class StepIntoActionDelegate { // TODO-3.3: extends org.eclipse.debug.internal.ui.actions.context.StepIntoActionDelegate {

	/**
     * @see StepActionDelegate#checkCapability(IStep)
     */
    protected boolean checkCapability(IStep element) {
        if (element instanceof JDIStackFrame) {
            JDIStackFrame frame = (JDIStackFrame)element;
            return (frame.isSuspended() && frame.canStepInto());
        }
        return false;
    }

    /**
     * @see StepActionDelegate#stepAction(IStep)
     */
    protected void stepAction(IStep element) throws DebugException {
        if (StrutsDebugPlugin.isDebugEnabled()) {
        	StrutsDebugPlugin.println("StepIntoActionDelegate.stepAction(" + element + ")");
        	StrutsDebugPlugin.println("\t element == " + element);
        	StrutsDebugPlugin.println("\t element class == " + (element == null ? "null" : element.getClass().getName()));
        }
        JDIStackFrame frame = (JDIStackFrame)element;
        StepIntoHandler handler = new StepIntoHandler(frame);
        handler.step();
    }
}