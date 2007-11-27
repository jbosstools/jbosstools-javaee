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

import java.io.File;

/**
 * @author eskimo
 *
 */
public class SeamRuntime {
	
	/**
	 * 
	 */
	SeamVersion version = null;
	
	/**
	 * 
	 */
	String name = null;
	
	/**
	 * 
	 */
	String homeDir = null;

	/**
	 * 
	 */
	boolean defaultRt = false;
	
	/**
	 * 
	 */
	public SeamRuntime() {}
	
	/**
	 * 
	 * @return
	 */
	public SeamVersion getVersion() {
		return version;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getHomeDir() {
		return homeDir;
	}

	/**
	 * 
	 * @param version
	 */
	public void setVersion(SeamVersion version) {
		this.version = version;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @param homeDir
	 */
	public void setHomeDir(String homeDir) {
		this.homeDir = homeDir;
	}

	/**
	 * @param b
	 */
	public void setDefault(boolean b) {
		this.defaultRt  = b;
	}
	
	/**
	 * @return
	 */
	public boolean isDefault() {
		return defaultRt;
	}
	
	public String getSeamGenDir() {
		return getHomeDir()+"/seam-gen"; //$NON-NLS-1$
	}
	
	public String getSrcTemplatesDir() {
		return getSeamGenDir()+"/src"; //$NON-NLS-1$
	}
	
	public String getViewTemplatesDir() {
		return getSeamGenDir()+"/view"; //$NON-NLS-1$
	}
	
	public String getResourceTemplatesDir() {
		return getSeamGenDir()+"/resources"; //$NON-NLS-1$
	}
	
	public String getTestTemplatesDir() {
		return getSeamGenDir()+"/test"; //$NON-NLS-1$
	}
	
	public String getTemplatesDir() {
		return getSeamGenDir();
	}
	
}
