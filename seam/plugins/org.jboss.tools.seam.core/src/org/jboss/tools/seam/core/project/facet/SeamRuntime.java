/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.core.project.facet;

/**
 * Class represent Seam Runtime parameters:
 * <ul>
 * 	<li>name</li>
 * 	<li>path to home directory</li>
 *  <li>version number</li>
 *  <li>default flag</li>
 * </ul>
 * and provide methods to get path to most relevant folders:
 * <ul>
 * 	<li>root templates</li>
 * 	<li>source templates</li>
 *  <li>test templates</li>
 *  <li>view templates</li>
 * </ul>
   
 * @author eskimo
 * 
 */
public class SeamRuntime {

	SeamVersion version = null;

	String name = null;

	String homeDir = null;

	boolean defaultRt = false;

	/**
	 * Default constructor
	 */
	public SeamRuntime() {
	}

	/**
	 * Get SeamVersion for Runtime
	 * 
	 * @return
	 * 
	 * SeamVersion constant
	 */
	public SeamVersion getVersion() {
		return version;
	}

	/**
	 * Get SeamRuntime name
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get path to home directory
	 * 
	 * @return home directory path as string
	 */
	public String getHomeDir() {
		return homeDir;
	}

	/**
	 * Set SeamVersion
	 * 
	 * @param version
	 *            new SeamVersion
	 */
	public void setVersion(SeamVersion version) {
		this.version = version;
	}

	/**
	 * Set SeamRuntime name
	 * 
	 * @param name
	 *            new SeamRuntime name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set home directory
	 * 
	 * @param homeDir
	 *            new SeamRuntime's home directory
	 */
	public void setHomeDir(String homeDir) {
		this.homeDir = homeDir;
	}

	/**
	 * Mark runtime as default
	 * 
	 * @param b
	 *            new value for default property
	 */
	public void setDefault(boolean b) {
		this.defaultRt = b;
	}

	/**
	 * Get default flag
	 * 
	 * @return default property
	 */
	public boolean isDefault() {
		return defaultRt;
	}

	/**
	 * Calculate path to seam-gen
	 * 
	 * @return absolute path to seam-gen folder
	 */
	public String getSeamGenDir() {
		return getHomeDir() + "/seam-gen"; //$NON-NLS-1$
	}

	/**
	 * Calculate path to seam-gen/build-scripts
	 * 
	 * @return absolute path to seam-gen/build-scripts folder
	 */
	public String getBuildScriptsDir() {
		return getSeamGenDir() + "/build-scripts"; //$NON-NLS-1$
	}

	public String getDeployedJarsEarListFile() {
		return getBuildScriptsDir() + "/deployed-jars-ear.list"; //$NON-NLS-1$
	}

	public String getDeployedJarsEarWarListFile() {
		return getBuildScriptsDir() + "/deployed-jars-ear-war.list"; //$NON-NLS-1$
	}

	public String getDeployedJarsWarListFile() {
		return getBuildScriptsDir() + "/deployed-jars-war.list"; //$NON-NLS-1$
	}

	/**
	 * Calculate path to lib folder
	 * 
	 * @return absolute path to lib folder
	 */
	public String getLibDir() {
		return getHomeDir() + "/lib"; //$NON-NLS-1$
	}

	/**
	 * Calculate path to source templates
	 * 
	 * @return absolute path to source templates
	 */
	public String getSrcTemplatesDir() {
		return getSeamGenDir() + "/src"; //$NON-NLS-1$
	}

	/**
	 * Calculate path to view templates
	 * 
	 * @return absolute path to view templates
	 */
	public String getViewTemplatesDir() {
		return getSeamGenDir() + "/view"; //$NON-NLS-1$
	}

	/**
	 * Calculate path to resource templates
	 * 
	 * @return absolute path to resource templates
	 */
	public String getResourceTemplatesDir() {
		return getSeamGenDir() + "/resources"; //$NON-NLS-1$
	}

	/**
	 * Calculate path to test templates
	 * 
	 * @return absolute path to test templates
	 */
	public String getTestTemplatesDir() {
		return getSeamGenDir() + "/test"; //$NON-NLS-1$
	}

	/**
	 * Calculate path to templates root directory
	 * 
	 * @return absolute path to templates root directory
	 */
	public String getTemplatesDir() {
		return getSeamGenDir();
	}
}