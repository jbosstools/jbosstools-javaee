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

/**
 * @author igels
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class GlobalExceptionCondition extends Condition {

	private String condition;

	public GlobalExceptionCondition(String exceptionTypeName) {
		/*
	      mapping.findException(Class.forName("<ExceptionTypeName>")) == mapping.findException(exception.getClass()) &&
			appConfig.findExceptionConfig(exception.getClass().getName()) == mapping.findException(exception.getClass())
		*/
		StringBuffer condition = new StringBuffer();
		condition.append("mapping.findException(Class.forName(\"").append(exceptionTypeName).append("\")) == mapping.findException(exception.getClass()) && ")
		.append("appConfig.findExceptionConfig(exception.getClass().getName()) == mapping.findException(exception.getClass())");
		this.condition = condition.toString();
	}

	public String getCondition() {
		return condition;
	}
}