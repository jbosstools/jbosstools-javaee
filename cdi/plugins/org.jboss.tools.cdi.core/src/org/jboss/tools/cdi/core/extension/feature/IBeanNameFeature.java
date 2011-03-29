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
 * This feature is invoked by bean when it computes its name.
 * The first non-null value is accepted.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IBeanNameFeature {
	public static String ID = "org.jboss.tools.cdi.core.extension.feature.IBeanNameFeature";

	public String computeBeanName(IBean bean);
}
