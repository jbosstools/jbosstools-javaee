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
package org.jboss.tools.cdi.core.util;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.internal.core.impl.AbstractBeanElement;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BeanPresentationUtil {
	public static final String SEPARATOR = " - "; //$NON-NLS-1$
	public static final String DOT = "."; //$NON-NLS-1$

	/**
	 * Returns "@Alternative", "@Decorator", "@Interceptor", "@Produces",
	 * or null, if nothing is relevant.
	 * 
	 * @param bean
	 * @return
	 */
	public static String getBeanKind(IBean bean) {
		if(bean.isAlternative()) {
			return "@Alternative";
		}
		if(bean.isAnnotationPresent(CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME)) {
			return "@Decorator";
		}
		if(bean.isAnnotationPresent(CDIConstants.INTERCEPTOR_ANNOTATION_TYPE_NAME)) {
			return "@Interceptor";
		}
		if(bean instanceof IProducer) {
			return "@Produces";
		}
		return null;
	}

	public static String getBeanLocation(IBean bean, boolean includeElementName) {
		StringBuilder sb = new StringBuilder();
		sb.append(SEPARATOR);
		AbstractBeanElement e = (AbstractBeanElement)bean;
		ITextSourceReference origin = e.getDefinition().getOriginalDefinition();
		if(origin != null) {
			//If toString() is not enough, another interface should be introduced.
			sb.append(origin.toString());				
		} else {			
			String pkg = bean.getBeanClass().getPackageFragment().getElementName();
			if(pkg.length() > 0) {
				sb.append(pkg);
			}
			if(includeElementName) {
				if(pkg.length() > 0) {
					sb.append(DOT);
				}
				sb.append(bean.getElementName());
			}
			sb.append(SEPARATOR);
			IPath path = bean.getBeanClass().getPackageFragment().getParent().getPath();
			sb.append(path.toString());
		}
		
		return sb.toString();
	}

}
