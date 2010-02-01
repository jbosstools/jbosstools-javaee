/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.jboss.tools.cdi.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class CDIUiImages {

	private static CDIUiImages INSTANCE;
	
	static {
		try {
			INSTANCE = new CDIUiImages(new URL(CDIUIPlugin.getDefault().getBundle().getEntry("/"), "icons/")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (MalformedURLException e) {
			CDIUIPlugin.getDefault().logError(e);
		}
	}
	
	public static final Image CDI_BEAN_IMAGE = getImage("search/cdi_bean.gif"); //$NON-NLS-1$
	public static final Image WELD_IMAGE = getImage("search/weld_icon_16x.gif"); //$NON-NLS-1$


	
	public static Image getImage(String key) {
		return INSTANCE.createImageDescriptor(key).createImage();
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		return INSTANCE.createImageDescriptor(key);
	}

	public static void setImageDescriptors(IAction action, String iconName)	{
		action.setImageDescriptor(INSTANCE.createImageDescriptor(iconName));
	}
	
	public static CDIUiImages getInstance() {
		return INSTANCE;
	}

	private URL baseUrl;
	private CDIUiImages parentRegistry;
	
	protected CDIUiImages(URL registryUrl, CDIUiImages parent){

		if(registryUrl == null) throw new IllegalArgumentException(CDIUIMessages.CDI_UI_IMAGESBASE_URL_FOR_IMAGE_REGISTRY_CANNOT_BE_NULL);
		baseUrl = registryUrl;
		parentRegistry = parent;
	}
	
	protected CDIUiImages(URL url){
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
		if (name == null) throw new MalformedURLException(CDIUIMessages.CDI_UI_IMAGESIMAGE_NAME_CANNOT_BE_NULL);
		return new URL(baseUrl, name);
	}	

}
