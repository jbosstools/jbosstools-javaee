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

import java.util.*;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.jsf.model.helpers.bean.ManagedBeanHelper;
import org.jboss.tools.common.model.refactoring.RenameProcessorRunner;

public class RenameManagedPropertyHandler extends AbstractHandler {
	boolean isLight = false;

	public boolean isEnabled(XModelObject object) {
		if(isLight) return false;
		if(object == null || !object.isObjectEditable()) return false;
		return true;
	}

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		if(!isEnabled(object)) return;
		IMember member = ManagedBeanHelper.getMember(object);
		String name = object.getAttributeValue("property-name"); //$NON-NLS-1$
		if(member != null) {
			name = member.getElementName();
			RenameSupport renameSupport = null;
			try {
			if (member instanceof IField) 
				renameSupport = RenameSupport.create((IField)member, null, RenameSupport.UPDATE_REFERENCES | RenameSupport.UPDATE_GETTER_METHOD | RenameSupport.UPDATE_SETTER_METHOD);
			else if (member instanceof IMethod)
				renameSupport = RenameSupport.create((IMethod)member, null, RenameSupport.UPDATE_REFERENCES);
			if (renameSupport == null || !renameSupport.preCheck().isOK()) return;

			Shell shell = ModelPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			renameSupport.openDialog(shell);
			} catch (CoreException e) {
				throw new XModelException(e);
			}
		} else {
			JSFRenameFieldProcessor processor = new JSFRenameFieldProcessor();
			processor.setModelObject(object);
			RenameProcessorRunner.run(processor, name);
		}
	}

}
