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

import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.search.ui.text.Match;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.ui.CDIUIPlugin;

public class BeanMatch extends Match {
	private IBean bean;
	public BeanMatch(IBean bean){
		super(bean, 0, 0);
		try{
			ISourceRange range = bean.getBeanClass().getNameRange();
			setOffset(range.getOffset());
			setLength(range.getLength());
		}catch(JavaModelException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
		this.bean = bean;
	}
	
	public IBean getBean(){
		return bean;
	}
}
