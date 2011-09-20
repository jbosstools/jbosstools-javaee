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

package org.jboss.tools.cdi.core;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.cdi.internal.core.impl.EventBean;

public class CDIImages {

	private static CDIImages INSTANCE;
	
	static {
		try {
			INSTANCE = new CDIImages(new URL(CDICorePlugin.getDefault().getBundle().getEntry("/"), "images/")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (MalformedURLException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}
	
	public static final Image CDI_BEAN_IMAGE = getImage("search/cdi_bean.gif"); //$NON-NLS-1$
	public static final Image WELD_IMAGE = getImage("search/weld_icon_16x.gif"); //$NON-NLS-1$
	
	public static final Image BEAN_CLASS_IMAGE = getImage("bean_class.png"); //$NON-NLS-1$
	public static final Image BEAN_METHOD_IMAGE = getImage("bean_method.png"); //$NON-NLS-1$
	public static final Image BEAN_FIELD_IMAGE = getImage("bean_field.png"); //$NON-NLS-1$
	public static final Image INJECTION_POINT_IMAGE = getImage("injection_point.png"); //$NON-NLS-1$
	public static final Image ANNOTATION_IMAGE = getImage("annotation.png"); //$NON-NLS-1$
	public static final Image CDI_EVENT_IMAGE = getImage("event.png"); //$NON-NLS-1$
	
	public static final Image QUICKFIX_ADD = getImage("quickfixes/cdi_add.png"); //$NON-NLS-1$
	public static final Image QUICKFIX_REMOVE = getImage("quickfixes/cdi_remove.png"); //$NON-NLS-1$
	public static final Image QUICKFIX_EDIT = getImage("quickfixes/cdi_edit.png"); //$NON-NLS-1$
	public static final Image QUICKFIX_CHANGE = getImage("quickfixes/cdi_change.png"); //$NON-NLS-1$
	
	public static final String WELD_WIZARD_IMAGE_PATH = "wizard/WeldWizBan.gif"; //$NON-NLS-1$
	
	public static Image getImage(String key) {
		return INSTANCE.createImageDescriptor(key).createImage();
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		return INSTANCE.createImageDescriptor(key);
	}

	public static void setImageDescriptors(IAction action, String iconName)	{
		action.setImageDescriptor(INSTANCE.createImageDescriptor(iconName));
	}
	
	public static CDIImages getInstance() {
		return INSTANCE;
	}

	private URL baseUrl;
	private CDIImages parentRegistry;
	
	protected CDIImages(URL registryUrl, CDIImages parent){

		if(registryUrl == null) throw new IllegalArgumentException(CDICoreMessages.CDI_IMAGESBASE_URL_FOR_IMAGE_REGISTRY_CANNOT_BE_NULL);
		baseUrl = registryUrl;
		parentRegistry = parent;
	}
	
	protected CDIImages(URL url){
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
		if (name == null) throw new MalformedURLException(CDICoreMessages.CDI_IMAGESIMAGE_NAME_CANNOT_BE_NULL);
		return new URL(baseUrl, name);
	}
	
	public static Image getImageByElement(ICDIElement element){
		
		if(element instanceof IClassBean){
			return BEAN_CLASS_IMAGE;
		}else if(element instanceof IInjectionPoint){
			return INJECTION_POINT_IMAGE;
		}else if(element instanceof ICDIAnnotation){
			return ANNOTATION_IMAGE;
		}else if(element instanceof EventBean){
			return CDI_EVENT_IMAGE;
		}else if(element instanceof IBeanMethod){
			return BEAN_METHOD_IMAGE;
		}else if(element instanceof IBeanField){
			return BEAN_FIELD_IMAGE;
		}
		System.out.println("NO IMAGE for - "+element.getClass());
		return WELD_IMAGE;
	}

}
