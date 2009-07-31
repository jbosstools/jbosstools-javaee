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
package org.jboss.tools.jsf.model.handlers;

import java.util.*;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.XModelObjectUtil;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jsf.model.helpers.JSFProcessStructureHelper;

public class CreateCommentHandler extends DefaultCreateHandler implements JSFConstants {
	protected Properties pc = null;

	public CreateCommentHandler() {}

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		pc = p;
		/*TRIAL_JSF*/
		super.executeHandler(object, p);
		JSFProcessStructureHelper.instance.showComments(object);
		pc = null;
	}

	protected void setOtherProperties(XModelObject object, Properties p) {
		String name = XModelObjectUtil.createNewChildName("comment", object); //$NON-NLS-1$
		p.setProperty("name", name); //$NON-NLS-1$
	}
	/*TRIAL_JSF_CLASS*/
	protected XModelObject modifyCreatedObject(XModelObject o) {
		setShape(o, pc);
		return o;
	}

	protected void setShape(XModelObject o, Properties p) {
		if(p == null) return;
		String x = p.getProperty("process.mouse.x"); //$NON-NLS-1$
		String y = p.getProperty("process.mouse.y"); //$NON-NLS-1$
		if(x == null || y == null) return;
		o.setAttributeValue("shape", "" + x + "," + y + ",0,0"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

}
