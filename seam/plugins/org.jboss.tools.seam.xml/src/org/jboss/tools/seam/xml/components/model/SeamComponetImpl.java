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
package org.jboss.tools.seam.xml.components.model;

import org.jboss.tools.common.model.impl.CustomizedObjectImpl;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SeamComponetImpl extends CustomizedObjectImpl {
	private static final long serialVersionUID = 1L;

	public SeamComponetImpl() {}

	public String getPathPart() {
		String id = get(XModelObjectLoaderUtil.ATTR_ID_NAME);
		if(id == null || id.length() == 0) {
			return super.getPathPart();
		} else {
			return "" + super.getPathPart() + ":" + id;
		}
	}
	
}
