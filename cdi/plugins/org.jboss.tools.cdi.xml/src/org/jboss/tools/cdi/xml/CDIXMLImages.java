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

package org.jboss.tools.cdi.xml;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class CDIXMLImages {

	private static CDIXMLImages INSTANCE;
	
	static {
		try {
			INSTANCE = new CDIXMLImages(new URL(CDIXMLPlugin.getDefault().getBundle().getEntry("/"), "images/")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (MalformedURLException e) {
			CDIXMLPlugin.log(e);
		}
	}
	
	public static final Image BEAN_CLASS_IMAGE = getImage("bean_class.png"); //$NON-NLS-1$

	public static Image getImage(String key) {
		return INSTANCE.createImageDescriptor(key).createImage();
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		return INSTANCE.createImageDescriptor(key);
	}

	public static void setImageDescriptors(IAction action, String iconName)	{
		action.setImageDescriptor(INSTANCE.createImageDescriptor(iconName));
	}
	
	public static CDIXMLImages getInstance() {
		return INSTANCE;
	}

	private URL baseUrl;
	
	protected CDIXMLImages(URL registryUrl) {
		baseUrl = registryUrl;
	}
	
	public Image getImageByFileName(String key) {
		return createImageDescriptor(key).createImage();
	}

	public ImageDescriptor createImageDescriptor(String key) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(key));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}		
	}

	private URL makeIconFileURL(String name) throws MalformedURLException {
		return new URL(baseUrl, name);
	}
	
}
