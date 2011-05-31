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

import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.internal.core.impl.ProducerField;
import org.jboss.tools.cdi.seam.solder.core.CDISeamSolderConstants;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class GenericBeanProducerField extends ProducerField {

	public GenericBeanProducerField() {}

	@Override
	public IScope getScope() {
		IScope result = null;
		if(definition.isAnnotationPresent(CDISeamSolderConstants.APPLY_SCOPE_ANNOTATION_TYPE_NAME)) {
			if(getParent() instanceof GenericClassBean) {
				IBean generic = ((GenericClassBean)getParent()).getGenericProducerBean();
				if(generic != null) {
					result = generic.getScope();
				}
			}
		}
		if(result == null) {
			result = super.getScope();
		}
		return result;
	}
}