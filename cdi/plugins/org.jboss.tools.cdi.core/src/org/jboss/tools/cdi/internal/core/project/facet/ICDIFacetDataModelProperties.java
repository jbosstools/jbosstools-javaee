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

package org.jboss.tools.cdi.internal.core.project.facet;

import org.eclipse.wst.common.project.facet.core.IActionConfigFactory;

/**
 * CDI facet properties.

 * @author Alexey Kazakov
 */
public interface ICDIFacetDataModelProperties extends IActionConfigFactory {

	/**
	 * CDI Facet ID constant
	 */
	String CDI_FACET_ID = "jst.cdi"; //$NON-NLS-1$

	/**
	 * CDI 1.0 Facet Version constant
	 */
	String CDI_FACET_VERSION_1 = "1.0"; //$NON-NLS-1$
}