 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.scanner.xml;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.PositionHolder;
import org.jboss.tools.seam.core.IValueInfo;

/**
 * @author Viacheslav Kabanovich
 */
public class XMLValueInfo implements IValueInfo {
	XModelObject object;
	String attribute;
	
	PositionHolder h = null;
	
	public XMLValueInfo(XModelObject object, String attribute) {
		this.object = object;
		this.attribute = attribute;
	}

	public int getLength() {
		getPositionHolder();
		int length = h.getEnd() - h.getStart();
		return length < 0 ? 0 : length;
	}

	public int getStartPosition() {
		getPositionHolder();
		return h.getStart();
	}

	public String getValue() {
		return object.getAttributeValue(attribute);
	}
	
	PositionHolder getPositionHolder() {
		if(h == null) {
			h = PositionHolder.getPosition(object, attribute);
		}
		h.update();
		return h;
	}

}
