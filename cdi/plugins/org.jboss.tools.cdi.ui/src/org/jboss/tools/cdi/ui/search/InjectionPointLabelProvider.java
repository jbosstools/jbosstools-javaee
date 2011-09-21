/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.search;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIElement;
import org.jboss.tools.cdi.core.IInitializerMethod;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.ui.CDIUIMessages;

public class InjectionPointLabelProvider implements ILabelProvider {

	public Image getImage(Object element) {
		if(element instanceof CDIElementWrapper){
			return CDIImages.getImageByElement(((CDIElementWrapper)element).getCDIElement());
		}
		return CDIImages.WELD_IMAGE;
	}

	public String getText(Object element) {
		if(element instanceof CDIElementWrapper){
			ICDIElement cdiElement = ((CDIElementWrapper)element).getCDIElement();
			String label = ((CDIElementWrapper)element).getLabel();
			if(cdiElement instanceof IBean){
				return NLS.bind(CDIUIMessages.INJECTION_POINT_LABEL_PROVIDER_INJECT_BEAN, label);
			}else if(cdiElement instanceof IObserverMethod){
				return NLS.bind(CDIUIMessages.INJECTION_POINT_LABEL_PROVIDER_OBSERVER_METHOD, label);
			}else if(cdiElement instanceof IInjectionPointField){
				return NLS.bind(CDIUIMessages.INJECTION_POINT_LABEL_PROVIDER_EVENT, label);
			}else if(cdiElement instanceof IInitializerMethod){
				return NLS.bind(CDIUIMessages.INJECTION_POINT_LABEL_PROVIDER_EVENT, label);
			}else if(cdiElement instanceof IInjectionPointParameter){
				return NLS.bind(CDIUIMessages.INJECTION_POINT_LABEL_PROVIDER_EVENT, label);
			}
		}
			return ""; //$NON-NLS-1$
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}
}
