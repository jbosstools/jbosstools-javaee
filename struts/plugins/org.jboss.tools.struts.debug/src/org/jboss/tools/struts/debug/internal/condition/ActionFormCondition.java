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
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;

/**
 * @author igels
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ActionFormCondition extends Condition {

	private String condition;

	public ActionFormCondition(String actionMappingPath) {
		/*
		 * mapping.getPath().equals("<actionMappingPath>")
		 */
		StringBuffer condition = new StringBuffer();
		condition.append("mapping.getPath().equals(\"").append(actionMappingPath).append("\")");
		this.condition = condition.toString();
	}

	public String getCondition() {
		return condition;
	}

	public JDIStackFrame getFrame(IStackFrame[] stackFrames) throws DebugException {
		JDIStackFrame frame = (JDIStackFrame)stackFrames[1];
		if("process".equals(frame.getMethodName())) {
			return frame;
		}
		return null;
	}
}