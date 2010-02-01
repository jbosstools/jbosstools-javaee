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

import org.eclipse.osgi.util.NLS;

public class CDIUIMessages extends NLS{
	private static final String BUNDLE_NAME = "org.jboss.tools.cdi.ui.CDIUIMessages"; //$NON-NLS-1$
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, CDIUIMessages.class);
	}
	
	public static String INJECTION_POINT_LABEL_PROVIDER;
	public static String CDI_UI_IMAGESBASE_URL_FOR_IMAGE_REGISTRY_CANNOT_BE_NULL;
	public static String CDI_UI_IMAGESIMAGE_NAME_CANNOT_BE_NULL;
}
