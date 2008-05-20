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
package org.jboss.tools.struts.model.helpers.autolayout;

import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.jst.web.model.helpers.autolayout.Item;
import org.jboss.tools.jst.web.model.helpers.autolayout.Items;

public class StrutsItems extends Items implements StrutsConstants {
	protected void initItem(Item item) {
		String type = item.getObject().getAttributeValue(ATT_TYPE);
		if(TYPE_FORWARD.equals(type) || TYPE_EXCEPTION.equals(type)) item.setWeight(-1);
		if(TYPE_PAGE.equals(type)) item.setYIndent(2);
	}

}

