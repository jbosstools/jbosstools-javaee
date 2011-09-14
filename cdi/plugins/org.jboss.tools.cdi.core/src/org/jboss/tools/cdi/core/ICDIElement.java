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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/**
 * Common interface for objects of CDI model.
 * 
 * @author Alexey Kazakov
 */
public interface ICDIElement {

	/**
	 * Returns CDI project that contains this object.
	 * @return
	 */
	ICDIProject getCDIProject();

	/**
	 * Returns CDI project that contains declaration of this object.
	 * The project that contains this object may be the same, or a dependent project.
	 * Theoretically, objects built by the same declaration in the declaring project and in 
	 * a dependent object may differ because of different sets of extensions visible in
	 * projects.
	 *  
	 * @return
	 */
	ICDIProject getDeclaringProject();

	/**
	 * Returns path of resource that declares this object.
	 * @return
	 */
	IPath getSourcePath();

	/**
	 * Returns resource that declares this object.
	 * @return resource 
	 */
	IResource getResource();

	/**
	 * Returns true while declarations of this object exist and can be found in sources
	 * by stored handles.
	 * 
	 * @return
	 */
	boolean exists();

	/**
	 * Returns the text representation of this element.
	 * For instance if the element is a class then the method is supposed to return the simple name of the class.
	 * If the element is a field then the method will return "TypeName.fieldName".
	 * If the bean is a method then the method will return "TypeName.methodName()".
	 * For a method parameter, a name of the parameter will be return.
	 * 
	 * Implementations may not return null.
	 *  
	 * @return the text representation of this bean.
	 */
	String getElementName();
}