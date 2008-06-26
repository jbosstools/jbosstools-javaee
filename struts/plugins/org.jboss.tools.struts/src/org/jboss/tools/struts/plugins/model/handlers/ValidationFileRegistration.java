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
package org.jboss.tools.struts.plugins.model.handlers;

public class ValidationFileRegistration extends PluginRegistration {

	protected String getPluginClassName() {
		return "org.apache.struts.validator.ValidatorPlugIn";
	}

	protected String getSetPropertyName() {
		return "pathnames";
	}

	protected String getDefaultSetPropertyValue() {
		return "/WEB-INF/validator-rules.xml, /WEB-INF/validation.xml";
	}

	protected boolean isOldNameDefault() {
		if(oldPath == null) return false;
		if("/WEB-INF/validator-rules.xml".equals(oldPath)) return true;
		if("/WEB-INF/validation.xml".equals(oldPath)) return true;
		return false;
	}

}
