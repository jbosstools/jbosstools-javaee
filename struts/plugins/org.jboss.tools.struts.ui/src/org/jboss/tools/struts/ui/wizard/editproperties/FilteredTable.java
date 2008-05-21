/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.struts.ui.wizard.editproperties;

import java.util.*;
import org.jboss.tools.common.meta.*;
import org.jboss.tools.common.meta.action.*;

import org.jboss.tools.common.model.ui.objecteditor.*;

public class FilteredTable extends XModelObjectEditor {
	protected boolean isAdvanced = false;
	protected XEntityData entityData;
	
	public void setAdvanced(boolean b) {
		isAdvanced = b;
	}
	
	public void setXEntityData(XEntityData data) {
		this.entityData = data;
	}

	protected void loadAttributes() {
		ArrayList<XAttribute> list = new ArrayList<XAttribute>();
		XAttributeData[] attributeData = entityData.getAttributeData(); 		
		for (int i = 0; i < attributeData.length; i++) {
			if(attributeData[i].isAdvanced() == isAdvanced) { 
				list.add(attributeData[i].getAttribute());
			}
		}
		attributes = list.toArray(new XAttribute[0]);
	}
	
	public int getPropertiesLength() {
		return attributes.length;
	}
	
}
