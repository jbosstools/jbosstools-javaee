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
package org.jboss.tools.cdi.ui.wizard;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;

public abstract class AbstractModifyInjectionPointWizard extends Wizard {
	protected IInjectionPoint injectionPoint;
	protected List<IBean> beans;
	protected IBean bean;
	
	public AbstractModifyInjectionPointWizard(IInjectionPoint injectionPoint, List<IBean> beans){
		this.injectionPoint = injectionPoint;
		this.beans = beans;
	}
	

	public AbstractModifyInjectionPointWizard(IInjectionPoint injectionPoint, List<IBean> beans, IBean bean){
		this.injectionPoint = injectionPoint;
		this.beans = beans;
		this.bean = bean;
	}

	public IInjectionPoint getInjectionPoint(){
		return injectionPoint;
	}
	
	public List<IBean> getBeans(){
		return beans;
	}
	
	public IBean getBean(){
		return bean;
	}
}
