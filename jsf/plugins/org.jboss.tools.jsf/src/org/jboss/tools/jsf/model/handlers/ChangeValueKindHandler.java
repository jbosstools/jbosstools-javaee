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
import org.jboss.tools.common.meta.key.WizardKeys;
import org.jboss.tools.common.model.*;
import org.jboss.tools.jsf.messages.JSFUIMessages;

public class ChangeValueKindHandler extends AbstractHandler {
	public static String MESSAGE_KEY = "JSFManagedProperty_ChangeValueKind"; //$NON-NLS-1$
	
	public static boolean isNewValueKind(XModelObject object, String kind) {
		String objectKind = object.getAttributeValue("value-kind"); //$NON-NLS-1$
		return objectKind != null && kind != null && !objectKind.equals(kind);
	}

	public static boolean checkChangeSignificance(XModelObject object) {
		String kind = object.getAttributeValue("value-kind"); //$NON-NLS-1$
		if("null-value".equals(kind)) return true; //$NON-NLS-1$
		if("value".equals(kind)) { //$NON-NLS-1$
			if(object.getAttributeValue("value").length() == 0) return true; //$NON-NLS-1$
		} if("map-entries".equals(kind) || "list-entries".equals(kind)) { //$NON-NLS-1$ //$NON-NLS-2$
			XModelObject c = object.getChildByPath("Entries"); //$NON-NLS-1$
			if(c == null) return true;
			//check attributes
			if(c.getChildren().length == 0) return true;  
		}
		return openConfirmation(object.getModel());
	}

	public static boolean openConfirmation(XModel model) {
		String message = "" + WizardKeys.getMessage(MESSAGE_KEY); //$NON-NLS-1$
		ServiceDialog d = model.getService();
		int q = d.showDialog(JSFUIMessages.CONFIRMATION, message, new String[]{JSFUIMessages.OK, JSFUIMessages.CANCEL}, null, ServiceDialog.QUESTION);
		return q == 0;
	}
	
	public boolean isEnabled(XModelObject object) {
		return object != null && object.isObjectEditable() && isNewValueKind(object, action.getProperty("value-kind")); //$NON-NLS-1$
	}
	
	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		if(!isEnabled(object)) return;
		if(!checkChangeSignificance(object)) return;
		String targetValueKind = action.getProperty("value-kind"); //$NON-NLS-1$
		object.getModel().changeObjectAttribute(object, "value-kind", targetValueKind); //$NON-NLS-1$
	}	
	
}
