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
package org.jboss.tools.cdi.solder.core;

import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.extension.feature.IBeanNameFeature;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BeanNameFeature implements IBeanNameFeature {
	/**
	 * The singleton instance that processes requests without building inner state.
	 */
	public static final IBeanNameFeature instance = new BeanNameFeature();

	public String computeBeanName(IBean bean) {

		return null;
	}

}
