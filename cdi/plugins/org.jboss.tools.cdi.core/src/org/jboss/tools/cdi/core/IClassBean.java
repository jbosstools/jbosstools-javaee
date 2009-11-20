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

import java.util.List;

/**
 * Represents a class-based bean.
 * 
 * @author Alexey Kazakov
 */
public interface IClassBean extends IBean {

	/**
	 * Returns a list of producers that are declared in this bean class.
	 * 
	 * @return a list of producers that are declared in this bean class.
	 */
	List<IProducer> getProducers();

	/**
	 * Returns a list of disposer methods that are declared in this bean class.
	 * 
	 * @return a list of disposer methods that are declared in this bean class.
	 */
	List<IBeanMethod> getDisposers();

	/**
	 * Returns a list of bean constructor of the bean.
	 * 
	 * @return a list of bean constructor of the bean.
	 */
	List<IBeanMethod> getBeanConstructor();
}