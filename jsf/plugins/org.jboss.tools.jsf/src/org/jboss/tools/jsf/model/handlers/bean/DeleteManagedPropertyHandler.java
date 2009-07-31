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
package org.jboss.tools.jsf.model.handlers.bean;

import java.text.MessageFormat;
import java.util.*;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultRemoveHandler;
import org.jboss.tools.common.model.*;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.helpers.bean.ManagedBeanHelper;

public class DeleteManagedPropertyHandler extends AbstractHandler {
	boolean isLight = false;

	public boolean isEnabled(XModelObject object) {
		return object != null && object.isObjectEditable();
	}

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		ServiceDialog d = object.getModel().getService();
		IMember member = ManagedBeanHelper.getMember(object);
		String title = DefaultCreateHandler.title(object, false); 
		String message = MessageFormat.format(JSFUIMessages.DeleteManagedPropertyHandler_Delete, title);
		boolean deleteField = !isLight && member != null;
		if(!deleteField) {
			if(0 != d.showDialog(JSFUIMessages.DELETE, message, new String[]{JSFUIMessages.OK, JSFUIMessages.CANCEL}, null, ServiceDialog.QUESTION)) return;
		} else {
			p = new Properties();
			p.setProperty(ServiceDialog.DIALOG_MESSAGE, message);
			p.setProperty(ServiceDialog.CHECKBOX_MESSAGE, JSFUIMessages.DeleteManagedPropertyHandler_DeleteJavaProperty);
			p.put(ServiceDialog.CHECKED, Boolean.FALSE);
			if(!d.openConfirm(p)) return;
			Boolean b = (Boolean)p.get(ServiceDialog.CHECKED);
			deleteField = (b != null) && b.booleanValue();
		}
		DefaultRemoveHandler.removeFromParent(object);
		if(deleteField) {
			try {
				IMember[] ms = findRelevantMembers(member);
				for (int i =  0; i < ms.length; i++) ms[i].delete(true, null);
			} catch (CoreException e) {
				throw new XModelException(e);
			}
		}
	}
	
	private IMember[] findRelevantMembers(IMember member) throws CoreException {
		List<IMember> list = new ArrayList<IMember>();
		list.add(member);
		IType type = member.getDeclaringType();
		IMethod[] ms = type.getMethods();
		String n = member.getElementName();
		if(member instanceof IMethod && n.startsWith("get") && n.length() > 3) { //$NON-NLS-1$
			n = n.substring(3, 4).toLowerCase() + n.substring(4);
		}
		String getter = "get" + n.substring(0, 1).toUpperCase() + n.substring(1); //$NON-NLS-1$
		String setter = "set" + n.substring(0, 1).toUpperCase() + n.substring(1); //$NON-NLS-1$
		for (int i = 0; i < ms.length; i++) {
			String ni = ms[i].getElementName();
			if(ni.equals(getter) || ni.equals(setter)) list.add(ms[i]);
		}
		return list.toArray(new IMember[0]);
	}

}
