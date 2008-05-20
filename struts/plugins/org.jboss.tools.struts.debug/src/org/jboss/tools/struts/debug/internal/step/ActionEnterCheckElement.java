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
package org.jboss.tools.struts.debug.internal.step;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.jdt.debug.core.IJavaStackFrame;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.debug.internal.ActionEnterBreakpoint;
import org.jboss.tools.struts.debug.internal.StrutsDebugPlugin;
import org.jboss.tools.struts.debug.internal.condition.ICondition;

public class ActionEnterCheckElement extends BaseCheckElement {

	private String typeName;

	public ActionEnterCheckElement(XModelObject xObject) {
		super(xObject);
		typeName = getAttributeValue(xObject, StrutsConstants.ATT_TYPE);
		StrutsDebugPlugin.log("ActionEnterCheckElement(" + typeName + ")");
	}

	private String getTypeName() {
		return typeName;
	}

	private String getMethodName() {
		return ActionEnterBreakpoint.BREAKPOINT_METHOD_NAME;
	}

	public boolean canStop(IStackFrame[] stackFrames) {
		boolean result = false;

		if (stackFrames.length > 0 && stackFrames[0] instanceof IJavaStackFrame) {
			IJavaStackFrame javaStackFrame = (IJavaStackFrame)stackFrames[0];
			String receivingTypeName = null;
			String methodName = null;
			try	{
				receivingTypeName = javaStackFrame.getReceivingTypeName();
				methodName = javaStackFrame.getMethodName();
			} catch (DebugException ex) {
                StrutsDebugPlugin.log(ex);
			}

			result = getTypeName().equals(receivingTypeName) && getMethodName().equals(methodName);
		}

		return result;
	}

	public ICondition getCondition() {
		return null;
	}
}