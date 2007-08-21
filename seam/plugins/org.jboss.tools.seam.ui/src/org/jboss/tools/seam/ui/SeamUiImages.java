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

public class SeamUiImages {

	private static SeamUiImages INSTANCE;
	
	static {
		try {
			INSTANCE = new SeamUiImages(new URL(SeamGuiPlugin.getDefault().getBundle().getEntry("/"), "icons/"));
		} catch (MalformedURLException e) {
			SeamGuiPlugin.getPluginLog().logError(e);
		}
	}
	
	public static Image SCOPE_IMAGE = getImage("view/scope.gif");
	public static Image PROJECT_IMAGE = getImage("view/seam_project.gif");
	public static Image COMPONENT_IMAGE = getImage("view/component.gif");
	public static Image ROLE_IMAGE = getImage("view/role.gif");
	public static Image JAVA_IMAGE = getImage("view/java.gif");
	public static Image JAVA_BINARY_IMAGE = getImage("view/java_binary.gif");
	public static Image PACKAGE_IMAGE = getImage("view/package.gif");

	public static String SEAM_CREATE_PROJECT_ACTION = "view/seam_project_new.gif";

	
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

		if(registryUrl == null) throw new NullPointerException("Base url for image registry cannot be null.");
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
		if (name == null) throw new MalformedURLException("Image name cannot be null.");
		return new URL(baseUrl, name);
	}	

}
