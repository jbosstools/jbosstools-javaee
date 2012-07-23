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
package org.jboss.tools.cdi.core;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.cdi.internal.core.impl.EventBean;
import org.jboss.tools.cdi.xml.CDIXMLImages;
import org.jboss.tools.common.ui.CommonUIPlugin;

public class CDIImages {

	private static CDIImages INSTANCE;

	static {
		try {
			INSTANCE = new CDIImages(new URL(CDICorePlugin.getDefault().getBundle().getEntry("/"), "images/")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (MalformedURLException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	public static final ImageDescriptor CDI_BEAN_IMAGE = getImageDescriptor("search/cdi_bean.gif"); //$NON-NLS-1$
	public static final ImageDescriptor WELD_IMAGE = getImageDescriptor("search/weld_icon_16x.gif"); //$NON-NLS-1$

	public static final ImageDescriptor BEAN_CLASS_IMAGE = CDIXMLImages.BEAN_CLASS_IMAGE;
	public static final ImageDescriptor BEAN_METHOD_IMAGE = getImageDescriptor("bean_method.png"); //$NON-NLS-1$
	public static final ImageDescriptor BEAN_FIELD_IMAGE = getImageDescriptor("bean_field.png"); //$NON-NLS-1$
	public static final ImageDescriptor INJECTION_POINT_IMAGE = getImageDescriptor("injection_point.png"); //$NON-NLS-1$
	public static final ImageDescriptor ANNOTATION_IMAGE = CDIXMLImages.ANNOTATION_IMAGE;
	public static final ImageDescriptor CDI_EVENT_IMAGE = getImageDescriptor("event.png"); //$NON-NLS-1$

	public static final ImageDescriptor MESSAGE_BUNDLE_IMAGE = getImageDescriptor("message_bundle.gif"); //$NON-NLS-1$

	public static final ImageDescriptor QUICKFIX_ADD = getImageDescriptor("quickfixes/cdi_add.png"); //$NON-NLS-1$
	public static final ImageDescriptor QUICKFIX_REMOVE = getImageDescriptor("quickfixes/cdi_remove.png"); //$NON-NLS-1$
	public static final ImageDescriptor QUICKFIX_EDIT = getImageDescriptor("quickfixes/cdi_edit.png"); //$NON-NLS-1$
	public static final ImageDescriptor QUICKFIX_CHANGE = getImageDescriptor("quickfixes/cdi_change.png"); //$NON-NLS-1$

	public static final String CDI_PROJECT_IMAGE = "wizard/CDIProjectWizBan.png"; //$NON-NLS-1$
	public static final String CDI_BEANS_XML_IMAGE = "wizard/CDIBeansXMLWizBan.png"; //$NON-NLS-1$
	public static final String CDI_CLASS_IMAGE = "wizard/CDIClassWizBan.png"; //$NON-NLS-1$
	public static final String CDI_ANNOTATION_IMAGE = "wizard/CDIAnnotationWizBan.png"; //$NON-NLS-1$

	public static Image getImage(ImageDescriptor descriptor) {
		return CommonUIPlugin.getImageDescriptorRegistry().get(descriptor);
	}

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

	public static Image getImageByElement(ICDIElement element) {
		return getImage(getImageDescriptorByElement(element));
	}

	public static ImageDescriptor getImageDescriptorByElement(ICDIElement element) {
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
		return WELD_IMAGE;
	}
}