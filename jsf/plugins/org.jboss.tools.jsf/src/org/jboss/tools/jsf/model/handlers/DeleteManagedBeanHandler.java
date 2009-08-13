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

import java.text.MessageFormat;
import java.util.Properties;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.*;

import org.jboss.tools.common.meta.action.SignificanceMessageFactory;
import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultRemoveHandler;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.pv.*;

public class DeleteManagedBeanHandler extends DefaultRemoveHandler {

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		boolean unregister = false;
		IFile f = getJavaFile(object);
		if(f != null) {
			ServiceDialog d = object.getModel().getService();
			Properties pd = new Properties();
			String message = SignificanceMessageFactory.getInstance().getMessage(action, object, null) + "?";
			pd.setProperty(ServiceDialog.DIALOG_MESSAGE, message);
			pd.setProperty(ServiceDialog.CHECKBOX_MESSAGE, JSFUIMessages.DELETE_JAVA_SOURCE);
			pd.put(ServiceDialog.CHECKED, Boolean.FALSE);
			if(!d.openConfirm(pd)) return;
			Boolean b = (Boolean)pd.get(ServiceDialog.CHECKED);
			unregister = b.booleanValue();
		}
		super.executeHandler(object, p);
		if(object.isActive()) return;
		if(unregister) {
			try {
				if(f != null && f.exists()) f.delete(true, null);
			} catch (CoreException e) {
				ServiceDialog d = object.getModel().getService();
				d.showDialog(JSFUIMessages.DeleteManagedBeanHandler_Failure, MessageFormat.format(JSFUIMessages.DeleteManagedBeanHandler_CannotDeleteFile, f), new String[]{SpecialWizardSupport.CLOSE}, null, ServiceDialog.ERROR);
			}
		}
	}

	public boolean getSignificantFlag(XModelObject object) {
		return getJavaFile(object) == null;
	}
	
	IFile getJavaFile(XModelObject object) {
		XModelObject o = JSFProjectsTree.getProjectsRoot(object.getModel());
		if(o == null) return null;
		JSFProjectBeans beans = (JSFProjectBeans)o.getChildByPath(JSFProjectTreeConstants.BEANS);
		if(beans == null) return null;
		IType type = beans.getType(object.getAttributeValue("managed-bean-class")); //$NON-NLS-1$
		if(type == null || type.isBinary()) return null;
		ICompilationUnit u = type.getCompilationUnit();
		if(u == null || u.getPath() == null) return null;
		IFile file = ModelPlugin.getWorkspace().getRoot().getFile(u.getPath());
		return (file == null || !file.isAccessible() || !file.exists()) ? null : file;
	}

}
