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
package org.jboss.tools.cdi.core.extension.feature;

import org.jboss.tools.cdi.core.IBean;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IBeanKeyProvider extends ICDIFeature {
	/**
	 * Returns a key for the bean that allows to link relevant resources 
	 * to the resource that declares the bean. Relevance is determined 
	 * by consistency of the incremental validation. The key needs not to be unique, 
	 * it just has to provide low probability of irrelevant links. 
	 * Implementation does not have to provide key for any bean.
	 * Method may return null.
	 * 
	 * @param bean
	 * @return a key for the bean to be used by the incremental validation for linking resources or null
	 */
	public String getKey(IBean bean);
}
