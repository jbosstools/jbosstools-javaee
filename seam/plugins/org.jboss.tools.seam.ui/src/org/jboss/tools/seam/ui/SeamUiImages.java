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
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.seam.core.SeamCoreMessages;

public class SeamUiImages {

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
	public static final Image COMPONENT_IMAGE = getImage("view/component.gif"); //$NON-NLS-1$
	public static final Image ROLE_IMAGE = getImage("view/role.gif"); //$NON-NLS-1$
	public static final Image JAVA_IMAGE = getImage("view/java.gif"); //$NON-NLS-1$
	public static final Image JAVA_BINARY_IMAGE = getImage("view/java_binary.gif"); //$NON-NLS-1$
	public static final Image PACKAGE_IMAGE = getImage("view/package.gif"); //$NON-NLS-1$
	public static final Image FACTORY_IMAGE = getImage("view/factory.gif"); //$NON-NLS-1$

	public static String SEAM_CREATE_PROJECT_ACTION = "view/seam_project_new.gif"; //$NON-NLS-1$

	
	public static Image getImage(String key) {
		return INSTANCE.createImageDescriptor(key).createImage();
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		return INSTANCE.createImageDescriptor(key);
	}

	public static void setImageDescriptors(IAction action, String iconName)	{
		action.setImageDescriptor(INSTANCE.createImageDescriptor(iconName));
	}
	
	public static SeamUiImages getInstance() {
		return INSTANCE;
	}

	private URL baseUrl;
	private SeamUiImages parentRegistry;
	
	protected SeamUiImages(URL registryUrl, SeamUiImages parent){

		if(registryUrl == null) throw new IllegalArgumentException(SeamCoreMessages.SEAM_UI_IMAGESBASE_URL_FOR_IMAGE_REGISTRY_CANNOT_BE_NULL);
		baseUrl = registryUrl;
		parentRegistry = parent;
	}
	
	protected SeamUiImages(URL url){
		this(url,null);		
	}

	public Image getImageByFileName(String key) {
		return createImageDescriptor(key).createImage();
	}

	public ImageDescriptor createImageDescriptor(String key) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(key));
		} catch (MalformedURLException e) {
			if(parentRegistry == null) {
				return ImageDescriptor.getMissingImageDescriptor();
			} else {
				return parentRegistry.createImageDescriptor(key);
			}
			
		}		
	}

	private URL makeIconFileURL(String name) throws MalformedURLException {
		if (name == null) throw new MalformedURLException(SeamCoreMessages.SEAM_UI_IMAGESIMAGE_NAME_CANNOT_BE_NULL);
		return new URL(baseUrl, name);
	}	

}
