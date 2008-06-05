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
package org.jboss.tools.seam.pages.xml.model.handlers;

import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;

public class OpenPageHandler extends DefaultRedirectHandler implements SeamPagesConstants {

	protected XModelObject getTrueSource(XModelObject source) {
		String entity = source.getModelEntity().getName();
		String attr = ATTR_VIEW_ID;
		if(entity.startsWith(ENT_NAVIGATION_RULE)
			|| entity.startsWith(ENT_RULE)) {
			source = source.getChildByPath("target");
			attr = ATTR_VIEW_ID;
		} else if(ENT_DIAGRAM_ITEM.equals(entity) ||
		          ENT_DIAGRAM_ITEM_OUTPUT.equals(entity)) {
			attr = ATTR_PATH;
		}
		if(attr == null) return null;
		String path = source.getAttributeValue(attr);
		if(path == null || path.length() == 0 || path.indexOf('*') >= 0) return null;
		path = path.replace('\\', '/');
		if(path.indexOf('?') >= 0) {
			path = path.substring(0, path.indexOf('?'));
		}
		return (path.startsWith("/")) ? source.getModel().getByPath(path) : null;
	}

}
