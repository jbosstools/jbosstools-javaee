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
package org.jboss.tools.struts.debug.internal.condition;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.jdt.debug.core.IJavaPrimitiveValue;
import org.eclipse.jdt.debug.eval.IEvaluationResult;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;

import org.jboss.tools.jst.web.debug.xpl.EvaluationSupport;
import org.jboss.tools.struts.debug.internal.StrutsDebugPlugin;

/**
 * @author igels
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class Condition implements ICondition {

	public boolean check(IStackFrame[] stackFrames) throws DebugException {
		JDIStackFrame frame = getFrame(stackFrames);
		if(frame==null) {
			return false;
		}
		IEvaluationResult evaluationResult = EvaluationSupport.evaluateExpression(frame, getCondition());

		if (evaluationResult.hasErrors()) {
			StrutsDebugPlugin.log("Some errors during evaluating expression!");
			return false;
		}
		try {
			IValue value = evaluationResult.getValue();
			if (value instanceof IJavaPrimitiveValue) {
				IJavaPrimitiveValue javaValue = (IJavaPrimitiveValue)value;
				if (javaValue.getJavaType().getName().equals("boolean") && javaValue.getBooleanValue()) {
					return true;
				}
			}
			return false;
		} catch (DebugException e) {
            StrutsDebugPlugin.log(e);
		}
		return false;
	}

	public JDIStackFrame getFrame(IStackFrame[] stackFrames) throws DebugException {
		return (JDIStackFrame)stackFrames[0];
	}

	public String toString() {
		return getCondition();
	}
}