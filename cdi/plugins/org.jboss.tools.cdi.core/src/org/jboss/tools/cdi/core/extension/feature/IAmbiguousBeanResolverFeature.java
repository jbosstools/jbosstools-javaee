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

import java.util.Set;

import org.jboss.tools.cdi.core.IBean;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IAmbiguousBeanResolverFeature {

	/**
	 * Contributes to resolving ambiguous beans in methods ICDIProject.getBeans(boolean attemptToResolveAmbiguousDependency, ...)
	 * Invoked by ICDIProject only when attemptToResolveAmbiguousDependency = true.
	 * 
	 * @param result
	 * @return
	 */
	public Set<IBean> getResolvedBeans(Set<IBean> result);

}
