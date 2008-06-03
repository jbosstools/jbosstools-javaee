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
package org.jboss.tools.jsf.web;

public class ConfigFilesData {
	public String param;
	public String[] defaultList;
	public String separator;
	public boolean usesDefaultWithoutRegistration = false;
	
	public ConfigFilesData(String param, String[] defaultList) {
		this(param, defaultList, ",");
	}

	public ConfigFilesData(String param, String[] defaultList, String separator) {
		this.param = param;
		this.defaultList = defaultList;
		this.separator = separator;
	}

}
