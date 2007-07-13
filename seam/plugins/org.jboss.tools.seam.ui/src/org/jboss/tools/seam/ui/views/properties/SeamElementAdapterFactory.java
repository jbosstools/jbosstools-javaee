/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.views.properties;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamElement;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamElementAdapterFactory implements IAdapterFactory {

	private static Class[] PROPERTIES = new Class[] {
		IPropertySource.class,
	};

	public Object getAdapter(Object object, Class key) {
		if(!(object instanceof ISeamElement)) return null;
		ISeamElement element = (ISeamElement)object;
		if (IPropertySource.class.equals(key)) {
			return getProperties(element);
		}
		return null;
	}

	public Class[] getAdapterList() {
		return PROPERTIES;
	}
	
	private IPropertySource getProperties(ISeamElement element) {
		if(element instanceof ISeamComponent) {
			return new SeamComponentProperties((ISeamComponent)element);
		}
		return null;
	}			

}
