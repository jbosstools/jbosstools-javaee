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
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointMethod;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUiImages;

public class InjectionPointLabelProvider implements ILabelProvider {

	public Image getImage(Object element) {
		//return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_CLASS);
		return CDIUiImages.WELD_IMAGE;
	}

	public String getText(Object element) {
		if(element instanceof IBean){
			return NLS.bind(CDIUIMessages.INJECTION_POINT_LABEL_PROVIDER_INJECT_BEAN, ((IBean)element).getBeanClass().getElementName());
		}else if(element instanceof IObserverMethod){
			return NLS.bind(CDIUIMessages.INJECTION_POINT_LABEL_PROVIDER_OBSERVER_METHOD, ((IObserverMethod)element).getMethod().getDeclaringType().getElementName(), ((IObserverMethod)element).getMethod().getElementName());
		}else if(element instanceof IInjectionPointField){
			return NLS.bind(CDIUIMessages.INJECTION_POINT_LABEL_PROVIDER_EVENT, ((IInjectionPointField)element).getField().getDeclaringType().getElementName(), ((IInjectionPointField)element).getField().getElementName());
		}else if(element instanceof IInjectionPointMethod){
			return NLS.bind(CDIUIMessages.INJECTION_POINT_LABEL_PROVIDER_EVENT, ((IInjectionPointMethod)element).getMethod().getDeclaringType().getElementName(), ((IInjectionPointMethod)element).getMethod().getElementName());
		}else if(element instanceof IInjectionPointParameter){
			return NLS.bind(CDIUIMessages.INJECTION_POINT_LABEL_PROVIDER_EVENT, ((IInjectionPointParameter)element).getBeanMethod().getMethod().getDeclaringType().getElementName(), ((IInjectionPointParameter)element).getBeanMethod().getMethod().getElementName());
		}else
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
