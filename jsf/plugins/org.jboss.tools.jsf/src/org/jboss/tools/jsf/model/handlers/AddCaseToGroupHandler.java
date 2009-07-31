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
import org.jboss.tools.common.meta.action.impl.AbstractHandler;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.*;
import org.jboss.tools.jsf.model.*;
import org.jboss.tools.jsf.model.helpers.JSFProcessHelper;
import org.jboss.tools.jsf.messages.JSFUIMessages;

public class AddCaseToGroupHandler extends AbstractHandler implements JSFConstants {

	public boolean isEnabled(XModelObject object) {
		return object != null && object.isObjectEditable();
	}

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		p = DefaultCreateHandler.extractProperties(data[0]);
		String fv = p.getProperty(ATT_TO_VIEW_ID);
		if(fv.indexOf("*") >= 0 || fv.length() == 0) { //$NON-NLS-1$
			ServiceDialog d = object.getModel().getService();
			int q = d.showDialog(JSFUIMessages.WARNING, JSFUIMessages.IT_ISNT_CORRECT_TO_MAKE_LINK_TO_A_PATTERN, 
					new String[]{JSFUIMessages.YES, JSFUIMessages.NO}, null, ServiceDialog.WARNING);
			if(q != 0) return;			 
		}
		fv = AddViewSupport.revalidatePath(fv);
		p.setProperty(ATT_TO_VIEW_ID, fv);
		XModelObject ncase = object.getModel().createModelObject(ENT_NAVIGATION_CASE, p); 
		ReferenceGroupImpl g = (ReferenceGroupImpl)object;
		/*TRIAL_JSF*/
		XModelObject[] rs = g.getReferences();
		if(rs.length > 0) {
			DefaultCreateHandler.addCreatedObject(rs[rs.length - 1], ncase, p); 
		} else {
			FacesProcessImpl process = (FacesProcessImpl)g.getParent();
			JSFNavigationModel n = (JSFNavigationModel)process.getReference();
			String path = g.getAttributeValue(ATT_PATH);
			String pp = g.getPathPart();
			int count = n.getRuleCount(path);
			String ppi = revalidateGroupPath(path, pp, count);
			if(!ppi.equals(pp)) {
				g.setAttributeValue(ATT_NAME, ppi);
			}
			XModelObject rule = n.addRule(path);
			DefaultCreateHandler.addCreatedObject(rule, ncase, p);
			g.setAttributeValue("persistent", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	public static String revalidateGroupPath(String path, String pathpart, int index) {
		if(!JSFProcessHelper.isPattern(path)) return pathpart;
		int s = pathpart.lastIndexOf(":"); //$NON-NLS-1$
		if(s < 6 && index == 0) return pathpart;
		if(index == 0) return pathpart.substring(0, s);
		if(s < 0) return pathpart + ":" + index; //$NON-NLS-1$
		return pathpart.substring(0, s) + ":" + index; //$NON-NLS-1$
	}
	/*TRIAL_JSF_CLASS*/
}
