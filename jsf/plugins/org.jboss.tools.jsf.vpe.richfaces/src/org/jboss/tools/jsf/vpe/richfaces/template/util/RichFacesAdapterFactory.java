/*******************************************************************************
 * Copyright (c) 2007-2011 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template.util;

import org.eclipse.core.runtime.IAdapterFactory;
import org.jboss.tools.jsf.vpe.richfaces.template.RichFacesCollapsibleSubTableTemplate;
import org.jboss.tools.jsf.vpe.richfaces.template.RichFacesTogglePanelTemplate;
import org.jboss.tools.vpe.editor.template.VpeTemplateSafeWrapper;

public class RichFacesAdapterFactory implements IAdapterFactory {

	private static final Class[] types = {
		RichFacesCollapsibleSubTableTemplate.class, RichFacesTogglePanelTemplate.class
	  };
	
	@Override
	public Class[] getAdapterList() {
		return types;
	}
	
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		Object result = null;
		if (adaptableObject instanceof VpeTemplateSafeWrapper) {
			VpeTemplateSafeWrapper wrapper = (VpeTemplateSafeWrapper) adaptableObject;
			if (adapterType == RichFacesCollapsibleSubTableTemplate.class) {
				result = wrapper.castDelegateTo(RichFacesCollapsibleSubTableTemplate.class);
			} else if (adapterType == RichFacesTogglePanelTemplate.class) {
				result = wrapper.castDelegateTo(RichFacesTogglePanelTemplate.class);
			}
		}
		return result;
	}

}
