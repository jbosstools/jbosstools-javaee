/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core;

import java.util.Collection;

public interface ICDICache {

	/**
	 * Cleans from database all data related to the project, 
	 * and creates tables for beans, legal types, and connections between them.
	 *  
	 * @param project
	 * @param bean
	 */
	public void rebuild(ICDIProject project, Collection<IBean> beans);

	/**
	 * Returns all beans that has 'legalType' as one of legal types.
	 * 
	 * @param project
	 * @param legalType
	 * @return
	 */
	public Collection<IBean> getBeansByLegalType(ICDIProject project, String legalType);

}