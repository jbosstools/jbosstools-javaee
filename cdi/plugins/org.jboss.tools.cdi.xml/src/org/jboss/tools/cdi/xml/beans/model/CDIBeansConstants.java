/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.xml.beans.model;

public interface CDIBeansConstants {
	public String BEANS_NAMESPACE = "http://java.sun.com/xml/ns/javaee"; //$NON-NLS-1$
	
	public String ENT_CDI_BEANS = "FileCDIBeans"; //$NON-NLS-1$

	public String ENT_DECORATORS = "CDIDecorators"; //$NON-NLS-1$
	public String ENT_INTERCEPTORS = "CDIInterceptors"; //$NON-NLS-1$
	public String ENT_ALTERNATIVES = "CDIAlternatives"; //$NON-NLS-1$

	public String ENT_CDI_CLASS = "CDIClass"; //$NON-NLS-1$
	public String ENT_CDI_STEREOTYPE = "CDIStereotype"; //$NON-NLS-1$

	public String ATTR_NAME = "name"; //$NON-NLS-1$
	public String ATTR_CLASS = "class"; //$NON-NLS-1$
	public String ATTR_STEREOTYPE = "stereotype"; //$NON-NLS-1$

}
