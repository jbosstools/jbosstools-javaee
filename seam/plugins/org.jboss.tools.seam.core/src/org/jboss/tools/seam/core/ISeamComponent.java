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
package org.jboss.tools.seam.core;

public interface ISeamComponent {
	
	//Common to all components attributes
	public static String NAME = "name";
	public static String CLASS = "class";
	public static String SCOPE = "scope";
	public static String PRECEDENCE = "precedence";
	public static String INSTALLED = "installed";
	public static String AUTO_CREATE = "auto-create";
	public static String JNDI_NAME = "jndi-name";
	
	//If property has default value but it cannot be resolved yet, 
	//then set property value to this constant.
	//TODO specify string value 
	public String DEFAULT = "";	

	public ISeamProperty<? extends Object> getProperty(String propertyName);
	public void addProperty(ISeamProperty<? extends Object> property);

	/**
	 * Convenient access methods to properties common for all components
	 * Implementation has to create and access ISeamProperty instances for 
	 * these properties, to allow storing attribute location in source.
	 */
	public String getName();
	public String getClassName();
	public String getScope();
	public String getPrecedence();
	public boolean isInstalled();
	public boolean isAutoCreate();
	public String getJndiName();

}
