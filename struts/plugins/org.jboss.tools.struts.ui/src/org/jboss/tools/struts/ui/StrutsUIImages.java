/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.struts.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.tools.common.ui.CommonUIImages;

public class StrutsUIImages extends CommonUIImages{
	private static String WIZARDS_PATH         = "wizards/"; //$NON-NLS-1$
	
	public static String STRUTS_CONFIG_IMAGE    = WIZARDS_PATH + "StrutsConfigWizBan.png"; //$NON-NLS-1$
	public static String STRUTS_PROJECT_IMAGE    = WIZARDS_PATH + "StrutsProjectWizBan.png"; //$NON-NLS-1$
	public static String IMPORT_STRUTS_PROJECT_IMAGE    = WIZARDS_PATH + "ImportStrutsProjectWizBan.png"; //$NON-NLS-1$
	public static String VALIDATION_FILE_IMAGE    = WIZARDS_PATH + "ValidationFileWizBan.png"; //$NON-NLS-1$

	private static StrutsUIImages INSTANCE;
	
	static {
		try {
			INSTANCE = new StrutsUIImages(new URL(StrutsUIPlugin.getDefault().getBundle().getEntry("/"), "images/xstudio/")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (MalformedURLException e) {
			// do nothing
			StrutsUIPlugin.getPluginLog().logError(e);
		}
	}
	
	public static StrutsUIImages getInstance() {
		return INSTANCE;
	}
	
	protected StrutsUIImages(URL registryUrl, StrutsUIImages parent){
		super(registryUrl, parent);
	}
	
	protected StrutsUIImages(URL url){
		this(url,null);		
	}
}
