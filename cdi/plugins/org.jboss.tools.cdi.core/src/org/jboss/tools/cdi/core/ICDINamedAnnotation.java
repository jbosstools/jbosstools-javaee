/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
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
 * A common interface for an annotation interface with can have @Named
 * annotation.
 * 
 * @author Alexey Kazakov
 */
public interface ICDINamedAnnotation extends ICDIAnnotation {

	/**
	 * Returns the declaration of @Named declaration of this annotation type. If
	 * the interface doesn't have the @Named declaration then null will be
	 * returned.
	 * 
	 * @return the declaration of @Named declaration of this bean
	 */
	IAnnotationDeclaration getNameDeclaration();
}