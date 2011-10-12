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
import org.jboss.tools.cdi.core.IBeanMember;
import org.jboss.tools.cdi.core.ICDIElement;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.internal.core.impl.AbstractBeanElement;
import org.jboss.tools.cdi.internal.core.impl.DisposerMethod;
import org.jboss.tools.cdi.internal.core.impl.EventBean;
import org.jboss.tools.cdi.internal.core.impl.InitializerMethod;
import org.jboss.tools.cdi.internal.core.impl.ObserverMethod;
import org.jboss.tools.cdi.internal.core.impl.ProducerField;
import org.jboss.tools.cdi.internal.core.impl.ProducerMethod;
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
		if(bean instanceof EventBean) {
			return "Event";
		}
		return null;
	}

	public static String getBeanLocation(IBean bean, boolean includeElementName) {
		StringBuilder sb = new StringBuilder();
		sb.append(SEPARATOR);
		
		ITextSourceReference origin = null;
		if(bean instanceof AbstractBeanElement){
			AbstractBeanElement e = (AbstractBeanElement)bean;
			origin = e.getDefinition().getOriginalDefinition();
		}
		
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
	
	public static String getCDIElementKind(ICDIElement element){
		if(element instanceof IBean){
			return getBeanKind((IBean)element);
		}else if(element instanceof IInjectionPointField){
			return "Injection Point Field";
		}else if(element instanceof IInjectionPointParameter){
			return "Injection Point Parameter";
		}else if(element instanceof DisposerMethod){
			return "Disposer Method";
		}else if(element instanceof InitializerMethod){
			return "Initializer Method";
		}else if(element instanceof ObserverMethod){
			return "Observer Method";
		}else if(element instanceof ProducerMethod){
			return "Producer Method";
		}else if(element instanceof ProducerField){
			return "Producer Field";
		}else if(element instanceof IParameter){
			return "Parameter";
		
		}
		return null;
	}
	
	public static String getCDIElementLocation(ICDIElement element, boolean includeElementName){
		if(element instanceof IBean){
			return getBeanLocation((IBean)element, includeElementName);
		}else if(element instanceof IInjectionPoint){
			return getBeanLocation(((IInjectionPoint) element).getBean(), includeElementName);
		}else if(element instanceof IBeanMember){
			return getBeanLocation(((IBeanMember) element).getClassBean(), includeElementName);
		}
		return null;
	}

}
