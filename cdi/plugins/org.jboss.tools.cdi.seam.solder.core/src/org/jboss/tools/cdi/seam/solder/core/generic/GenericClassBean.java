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
package org.jboss.tools.cdi.seam.solder.core.generic;

import java.util.Set;

import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.internal.core.impl.AbstractBeanElement;
import org.jboss.tools.cdi.internal.core.impl.ClassBean;
import org.jboss.tools.cdi.internal.core.impl.ProducerField;
import org.jboss.tools.cdi.internal.core.impl.ProducerMethod;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.FieldDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;
import org.jboss.tools.cdi.seam.solder.core.Version;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class GenericClassBean extends ClassBean implements IGenericBean {
	protected AbstractMemberDefinition genericProducerBean;
	Version version;
	
	public GenericClassBean(Version version) {
		this.version = version;
	}

	public Version getVersion() {
		return version;
	}

	@Override
	protected ProducerMethod newProducerMethod(MethodDefinition m) {
		return new GenericBeanProducerMethod(version);
	}

	@Override
	protected ProducerField newProducerField(FieldDefinition f) {
		return new GenericBeanProducerField(version);
	}

	public void setGenericProducerBeanDefinition(AbstractMemberDefinition def) {
		genericProducerBean = def;
	}

	public IBean getGenericProducerBean() {
		Set<IBean> bs = getCDIProject().getBeans(genericProducerBean.getTypeDefinition().getType().getPath());
		for (IBean b: bs) {
			if(b instanceof AbstractBeanElement) {
				if(((AbstractBeanElement)b).getDefinition() == genericProducerBean) {
					return b;
				}
			}
		}
		return null;
	}

	@Override
	protected void computeScope() {
		if(definition.isAnnotationPresent(version.getApplyScopeAnnotationTypeName())) {
			IBean generic = getGenericProducerBean();
			if(generic != null) {
				scope = generic.getScope();
			}
		}
		if(scope == null) {
			super.computeScope();
		}
	}
}