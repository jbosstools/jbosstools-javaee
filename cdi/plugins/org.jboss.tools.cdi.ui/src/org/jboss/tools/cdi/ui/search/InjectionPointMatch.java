/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.search;

import org.eclipse.search.ui.text.Match;
import org.jboss.tools.cdi.core.IBean;

public class InjectionPointMatch extends Match {
	private IBean bean;
	public InjectionPointMatch(IBean bean, int offset, int length){
		super(bean, offset, length);
		this.bean = bean;
	}
	
	public IBean getBean(){
		return bean;
	}
}
