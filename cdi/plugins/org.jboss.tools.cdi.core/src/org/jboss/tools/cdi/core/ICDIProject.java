/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core;

/**
 * @author Alexey Kazakov
 */
public interface ICDIProject extends IBeanManager {

	public CDICoreNature getNature();
	public void setNature(CDICoreNature n);
	public void update(boolean updateDependent);
	
	public boolean isTypeAlternative(String qualifiedName);
	public boolean isStereotypeAlternative(String qualifiedName);
	public boolean isClassAlternativeActivated(String fullQualifiedTypeName);
	
	public CDIVersion getVersion();
}