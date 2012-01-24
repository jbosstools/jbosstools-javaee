/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.jboss.tools.cdi.seam.config.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class CDISeamConfigUiImages {

	private static CDISeamConfigUiImages INSTANCE;
	
	static {
		try {
			INSTANCE = new CDISeamConfigUiImages(new URL(CDISeamConfigUIPlugin.getDefault().getBundle().getEntry("/"), "icons/")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (MalformedURLException e) {
			CDISeamConfigUIPlugin.getDefault().logError(e);
		}
	}
	
	public static final Image PACKAGE_IMAGE = getImage("package.gif"); //$NON-NLS-1$
	
	public static Image getImage(String key) {
		return INSTANCE.createImageDescriptor(key).createImage();
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		return INSTANCE.createImageDescriptor(key);
	}

	public static void setImageDescriptors(IAction action, String iconName)	{
		action.setImageDescriptor(INSTANCE.createImageDescriptor(iconName));
	}
	
	public static CDISeamConfigUiImages getInstance() {
		return INSTANCE;
	}

	private URL baseUrl;
	private CDISeamConfigUiImages parentRegistry;
	
	protected CDISeamConfigUiImages(URL registryUrl, CDISeamConfigUiImages parent){

		if(registryUrl == null) throw new IllegalArgumentException();
		baseUrl = registryUrl;
		parentRegistry = parent;
	}
	
	protected CDISeamConfigUiImages(URL url){
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
		if (name == null) throw new MalformedURLException();
		return new URL(baseUrl, name);
	}	

}
