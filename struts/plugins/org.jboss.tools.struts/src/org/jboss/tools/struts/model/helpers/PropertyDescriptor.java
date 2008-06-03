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
package org.jboss.tools.struts.model.helpers;

public class PropertyDescriptor {
	private String name;
	private String upName;
		
	public PropertyDescriptor() {}
	public PropertyDescriptor(String name) {
		this.name = name;
		this.upName = name.substring(0,1).toUpperCase()+name.substring(1);
	}
	public String getName() {
		return name;
	}
	public String getUpName() {
		return upName;
	}
	public void setName(String string) {
		name = string;
	}
	public void setUpName(String string) {
		upName = string;
	}

}
