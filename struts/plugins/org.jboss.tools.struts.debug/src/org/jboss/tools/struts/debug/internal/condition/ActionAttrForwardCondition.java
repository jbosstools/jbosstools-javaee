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
 * @author Igels
 */
public class ActionAttrForwardCondition extends Condition {

	private String condition;

	public ActionAttrForwardCondition(String actionMappingPath, String forwardName) {
		/*
		 * "<ForwardName>".equals(mapping.getForward()) &&
		 * "<ActionMappingPath>".equals(mapping.getPath())
		*/
		StringBuffer condition = new StringBuffer();
		condition.append("\"").append(forwardName).append("\".equals(mapping.getForward()) && ").
		append("\"").append(actionMappingPath).append("\".equals(mapping.getPath())");
		this.condition = condition.toString();
	}

	public String getCondition() {
		return condition;
	}
}