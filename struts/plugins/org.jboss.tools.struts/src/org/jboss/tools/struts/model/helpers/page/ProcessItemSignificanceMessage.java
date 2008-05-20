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
package org.jboss.tools.struts.model.helpers.page;

import org.jboss.tools.common.meta.action.XAction;
import org.jboss.tools.common.meta.action.impl.SignificanceMessageImpl;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.StrutsConstants;

public class ProcessItemSignificanceMessage extends SignificanceMessageImpl implements StrutsConstants {
	
	public String getMessage(XAction action, XModelObject object, XModelObject[] objects) {
		if(objects != null && objects.length > 1) return super.getMessage(action, object, objects);
		String item_type = object.getAttributeValue(ATT_TYPE);
		if(!TYPE_PAGE.equals(item_type)) return super.getMessage(action, object, objects);
		String subtype = object.getAttributeValue(ATT_SUBTYPE);
		if(!SUBTYPE_TILE.equals(subtype)) return super.getMessage(action, object, objects);
		String d = action.getDisplayName();
		if(d.endsWith("...")) d = d.substring(d.length() - 3);
		String what = "tile " + "'" + object.getPresentationString() + "'";
		return d + " " + what;
	}

}
