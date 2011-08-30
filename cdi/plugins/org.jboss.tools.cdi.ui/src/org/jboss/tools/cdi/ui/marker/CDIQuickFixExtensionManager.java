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
package org.jboss.tools.cdi.ui.marker;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.common.el.core.ELCorePlugin;

public class CDIQuickFixExtensionManager {
	public static String EXTENSION_POINT = "org.jboss.tools.cdi.ui.quickFixExtension"; //$NON-NLS-1$

	String id;
	ICDIMarkerResolutionGeneratorExtension extension;

	public CDIQuickFixExtensionManager() {}

	public String getId() {
		return id;
	}

	static ICDIMarkerResolutionGeneratorExtension[] INSTANCES;

	public static ICDIMarkerResolutionGeneratorExtension[] getInstances() {
		if(INSTANCES != null) return INSTANCES;
		List<ICDIMarkerResolutionGeneratorExtension> list = new ArrayList<ICDIMarkerResolutionGeneratorExtension>();
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT);
		IConfigurationElement[] es = point.getConfigurationElements();
		for (IConfigurationElement e: es) {
			CDIQuickFixExtensionManager n = new CDIQuickFixExtensionManager();
			n.id = e.getAttribute("id"); //$NON-NLS-1$
			try{
				n.extension = (ICDIMarkerResolutionGeneratorExtension)e.createExecutableExtension("class"); //$NON-NLS-1$
				list.add(n.extension);
			}catch(CoreException ex){
				ELCorePlugin.getDefault().logError(ex);
			}
			
		}
		return INSTANCES = list.toArray(new ICDIMarkerResolutionGeneratorExtension[0]);
	}
}
