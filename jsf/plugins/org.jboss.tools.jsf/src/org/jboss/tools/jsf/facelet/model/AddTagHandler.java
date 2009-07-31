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
package org.jboss.tools.jsf.facelet.model;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.tld.model.handlers.CreateInCollapsedHandler;

/**
 * @author Viacheslav Kabanovich
 */
public class AddTagHandler extends CreateInCollapsedHandler {

    protected XModelObject modifyCreatedObject(XModelObject o) {
    	XModelObject c = o.getModel().createModelObject("FaceletTaglibHandler", null); //$NON-NLS-1$
    	o.addChild(c);
    	return o;
    }
}
