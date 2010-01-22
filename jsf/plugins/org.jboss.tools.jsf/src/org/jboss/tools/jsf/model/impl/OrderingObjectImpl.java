/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.model.impl;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.CustomizedObjectImpl;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class OrderingObjectImpl extends CustomizedObjectImpl {
	private static final long serialVersionUID = 1L;

	public OrderingObjectImpl() {}

	public String getPresentationString() {
		XModelObject a = getChildByPath("After");
		if(a != null) {
			if(a.getChildren().length > 0) {
				return "After " + a.getChildren()[0].getAttributeValue("name");
			}
			if("true".equals(a.getAttributeValue("others"))) {
				return "After others";
			}
		}
		XModelObject b = getChildByPath("Before");
		if(b != null) {
			if(b.getChildren().length > 0) {
				return "Before " + b.getChildren()[0].getAttributeValue("name");
			}
			if("true".equals(b.getAttributeValue("others"))) {
				return "Before others";
			}
		}
		return "" + getModelEntity().getXMLSubPath();
	}

	public String name() {
		return "" + getModelEntity().getXMLSubPath() + get(XModelObjectLoaderUtil.ATTR_ID_NAME);
	}

}
