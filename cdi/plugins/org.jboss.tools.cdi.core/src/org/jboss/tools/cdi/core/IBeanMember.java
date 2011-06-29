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

import org.jboss.tools.common.java.IAnnotated;
import org.jboss.tools.common.java.IJavaSourceReference;
import org.jboss.tools.common.java.IParametedType;

/**
 * Represents a member of bean class.
 * 
 * @author Alexey Kazakov
 */
public interface IBeanMember extends IJavaSourceReference, IAnnotated, ICDIElement {

	/**
	 * Returns the class bean that declares this method.
	 * 
	 * @return
	 */
	IClassBean getClassBean();

	/**
	 * Returns type of underlying Java member.
	 *  
	 * @return
	 */
	IParametedType getMemberType();
}
