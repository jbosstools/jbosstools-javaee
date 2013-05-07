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
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.cdi.internal.core.impl.EventBean;
import org.jboss.tools.cdi.xml.CDIXMLImages;
import org.jboss.tools.common.ui.CommonUIImages;

public class CDIImages extends CommonUIImages {

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
	public static final ImageDescriptor BEANS_XML_IMAGE = CDIXMLImages.BEANS_XML_IMAGE;

	public static final ImageDescriptor MESSAGE_BUNDLE_IMAGE = getImageDescriptor("message_bundle.gif"); //$NON-NLS-1$

	public static final ImageDescriptor QUICKFIX_ADD = getImageDescriptor("quickfixes/cdi_add.png"); //$NON-NLS-1$
	public static final ImageDescriptor QUICKFIX_REMOVE = getImageDescriptor("quickfixes/cdi_remove.png"); //$NON-NLS-1$
	public static final ImageDescriptor QUICKFIX_EDIT = getImageDescriptor("quickfixes/cdi_edit.png"); //$NON-NLS-1$
	public static final ImageDescriptor QUICKFIX_CHANGE = getImageDescriptor("quickfixes/cdi_change.png"); //$NON-NLS-1$

	public static final String CDI_PROJECT_IMAGE = "wizard/CDIProjectWizBan.png"; //$NON-NLS-1$
	public static final String CDI_BEANS_XML_IMAGE = "wizard/CDIBeansXMLWizBan.png"; //$NON-NLS-1$
	public static final String CDI_CLASS_IMAGE = "wizard/CDIClassWizBan.png"; //$NON-NLS-1$
	public static final String CDI_ANNOTATION_IMAGE = "wizard/CDIAnnotationWizBan.png"; //$NON-NLS-1$

	public static Image getImage(String key) {
		return getImageDescriptor(key).createImage();
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		return getInstance().getOrCreateImageDescriptor(key);
	}

	public static void setImageDescriptors(IAction action, String iconName)	{
		action.setImageDescriptor(INSTANCE.createImageDescriptor(iconName));
	}

	public static CDIImages getInstance() {
		return INSTANCE;
	}

	protected CDIImages(URL registryUrl, CDIImages parent){
		super(registryUrl, parent);
	}
	
	protected CDIImages(URL url){
		this(url,null);		
	}

	protected ImageRegistry getImageRegistry() {
		return CDICorePlugin.getDefault().getImageRegistry();
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