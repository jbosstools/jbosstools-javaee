 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.core;

/**
 * Represents <component> element of components.xml file.
 * @author Alexey Kazakov
 */
public interface ISeamXmlComponentDeclaration extends ISeamPropertiesDeclaration {

	/*
	 * Names of <component> attributes.
	 */
	public final static String NAME = "name"; //$NON-NLS-1$
	public final static String CLASS = "class"; //$NON-NLS-1$
	public final static String SCOPE = "scope"; //$NON-NLS-1$
	public final static String PRECEDENCE = "precedence"; //$NON-NLS-1$
	public final static String INSTALLED = "installed"; //$NON-NLS-1$
	public final static String AUTO_CREATE = "auto-create"; //$NON-NLS-1$
	public final static String JNDI_NAME = "jndi-name"; //$NON-NLS-1$
	public final static String STARTUP = "startup"; //$NON-NLS-1$
	public final static String STARTUP_DEPENDS = "startupDepends"; //$NON-NLS-1$

	/**
	 * @return scope type
	 */
	public ScopeType getScope();

	/**
	 * @return string value of 'scope' attribute 
	 */
	public String getScopeAsString();

	/**
	 * @return string value of 'class' attribute
	 */
	public String getClassName();

	public boolean isClassNameGuessed();

	/**
	 * @return string value of 'jndi-name' attribute
	 */
	public String getJndiName();

	/**
	 * @return true if attribute 'installed' is 'true'
	 */
	public boolean isInstalled();

	/**
	 * @return string value of 'installed' attribute
	 */
	public boolean getInstalledAsString();

	/**
	 * @return string value of 'precedence' attribute
	 */
	public String getPrecedence();

	/**
	 * @return true if attribute 'auto-create' is 'true'
	 */
	public boolean isAutoCreate();

	/**
	 * @return string value of 'auto-create' attribute
	 */
	public String getAutoCreateAsString();

	public ISeamXmlComponentDeclaration clone() throws CloneNotSupportedException;

}