/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.jboss.tools.seam.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.ui.CommonUIImages;

public class SeamUiImages extends CommonUIImages {
	private static SeamUiImages INSTANCE;
	
	static {
		try {
			INSTANCE = new SeamUiImages(new URL(SeamGuiPlugin.getDefault().getBundle().getEntry("/"), "icons/")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (MalformedURLException e) {
			SeamGuiPlugin.getPluginLog().logError(e);
		}
	}
	
	public static final Image SCOPE_IMAGE = getImage("view/scope.gif"); //$NON-NLS-1$
	public static final Image PROJECT_IMAGE = getImage("view/seam_project.gif"); //$NON-NLS-1$
	public static final Image COMPONENT_IMAGE = getImage("view/component.png"); //$NON-NLS-1$
	public static final Image ROLE_IMAGE = getImage("view/role.gif"); //$NON-NLS-1$
	public static final Image JAVA_IMAGE = getImage("view/java.gif"); //$NON-NLS-1$
	public static final Image JAVA_BINARY_IMAGE = getImage("view/java_binary.gif"); //$NON-NLS-1$
	public static final Image PACKAGE_IMAGE = getImage("view/package.gif"); //$NON-NLS-1$
	public static final Image FACTORY_IMAGE = getImage("view/factory.gif"); //$NON-NLS-1$

	public static String SEAM_CREATE_PROJECT_ACTION = "view/seam_project_new.gif"; //$NON-NLS-1$

	
	public static Image getImage(String key) {
		return INSTANCE.getOrCreateImage(key);
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		return INSTANCE.getOrCreateImageDescriptor(key);
	}

	public static void setImageDescriptors(IAction action, String iconName)	{
		action.setImageDescriptor(INSTANCE.getOrCreateImageDescriptor(iconName));
	}
	
	public static SeamUiImages getInstance() {
		return INSTANCE;
	}

	protected SeamUiImages(URL registryUrl, SeamUiImages parent){
		super(registryUrl, parent);
	}
	
	protected SeamUiImages(URL url){
		this(url,null);		
	}

	protected ImageRegistry getImageRegistry() {
		return SeamGuiPlugin.getDefault().getImageRegistry();
	}
}
