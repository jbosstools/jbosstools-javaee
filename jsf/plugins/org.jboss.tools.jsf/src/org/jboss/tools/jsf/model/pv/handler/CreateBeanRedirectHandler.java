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
package org.jboss.tools.jsf.model.pv.handler;

import org.jboss.tools.common.meta.action.impl.handlers.DefaultRedirectHandler;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jsf.model.JSFXModelUtil;
import org.jboss.tools.jsf.model.pv.JSFProjectTreeConstants;
import org.jboss.tools.jsf.model.pv.JSFProjectsTree;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CreateBeanRedirectHandler extends DefaultRedirectHandler {

	protected XModelObject getTrueSource(XModelObject source) {
		if(source == null) return null;
		XModel model = source.getModel();
		WebProjectNode r = JSFProjectsTree.getProjectsRoot(model);
		if(r == null) return null;
		WebProjectNode n = (WebProjectNode)r.getChildByPath(JSFProjectTreeConstants.CONFIGURATION);
		if(n == null) return null;
		for (XModelObject f: n.getTreeChildren()) {
			if(JSFXModelUtil.isFacesConfig(f) && f.isObjectEditable()) {
				return f.getChildByPath(JSFConstants.FOLDER_MANAGED_BEANS);
			}
		}
		return null;
	}

}
