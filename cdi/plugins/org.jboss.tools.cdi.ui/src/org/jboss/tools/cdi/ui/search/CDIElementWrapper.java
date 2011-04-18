/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.search;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.Signature;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIElement;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointMethod;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.ui.marker.MarkerResolutionUtils;

public class CDIElementWrapper {
	private static String SPACE = " ";
	private static String DOT = ".";
	private static String OPEN = "(";
	private static String CLOSE = ")";
	private static String BRACKETS = OPEN+CLOSE;
	private ICDIElement element;
	private String label;
	private String path;
	private IJavaElement javaElement;
	
	public CDIElementWrapper(ICDIElement element){
		this.element = element;
		if(element instanceof IBean){
			javaElement = ((IBean)element).getBeanClass();
			label = javaElement.getElementName();
		}else if(element instanceof IObserverMethod){
			javaElement = ((IObserverMethod)element).getMethod();
			label = ((IObserverMethod)element).getMethod().getDeclaringType().getElementName()+DOT+((IObserverMethod)element).getMethod().getElementName()+BRACKETS;
		}else if(element instanceof IInjectionPointField){
			javaElement = ((IInjectionPointField)element).getField();
			label = ((IInjectionPointField)element).getField().getDeclaringType().getElementName()+DOT+((IInjectionPointField)element).getField().getElementName();
		}else if(element instanceof IInjectionPointMethod){
			javaElement = ((IInjectionPointMethod)element).getMethod();
			label = ((IInjectionPointMethod)element).getMethod().getDeclaringType().getElementName()+DOT+((IInjectionPointMethod)element).getMethod().getElementName()+BRACKETS;
		}else if(element instanceof IInjectionPointParameter){
			IMethod method = ((IInjectionPointParameter)element).getBeanMethod().getMethod();
			javaElement = MarkerResolutionUtils.getParameter(method, ((IInjectionPointParameter)element).getName());
			String type = Signature.getSignatureSimpleName(((ILocalVariable)javaElement).getTypeSignature());
			label = method.getDeclaringType().getElementName()+DOT+method.getElementName()+OPEN+type+SPACE+javaElement.getElementName()+CLOSE;
		}
		if(javaElement.getResource() != null)
			path = javaElement.getResource().getFullPath()+"/"+label;
		else
			path = "/"+label;
	}
	
	public ICDIElement getCDIElement(){
		return element;
	}
	
	public IJavaElement getJavaElement(){
		return javaElement;
	}
	
	public String getLabel(){
		return label;
	}

	public String getPath(){
		return label;
	}
}
