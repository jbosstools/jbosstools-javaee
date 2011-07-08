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
package org.jboss.tools.jsf.jsf2.bean.model.impl;

import org.jboss.tools.jsf.jsf2.bean.model.JSF2Constants;

public class TypeDefinition extends AbstractTypeDefinition {

	public TypeDefinition() {}

	public boolean isManagedBean() {
		return isAnnotationPresent(JSF2Constants.MANAGED_BEAN_ANNOTATION_TYPE_NAME);
	}

}
