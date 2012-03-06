/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.views.actions;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.common.meta.action.SpecialWizard;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.internal.core.SeamObject;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.refactoring.SeamRefactorContributionFactory;

/**
 * Rename component action for Seam Components view.
 * @author Viacheslav Kabanovich
 */
public class RenameComponentAction extends Action implements SpecialWizard {
	ISeamComponent component;

	public RenameComponentAction() {		
	}

	public RenameComponentAction(ISeamComponent component) {
		setText(SeamCoreMessages.RENAME_SEAM_COMPONENT);
		this.component = component;
	}

	@Override
	public void run() {
		if(component != null) {
			Shell activeShell = SeamGuiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			SeamRefactorContributionFactory.invokeRenameComponentWizard(component, activeShell);
		}
	}

	public int execute() {
		run();
		return 0;
	}

	public void setObject(Object object) {
		if(object instanceof XModelObject) {
			XModelObject o = (XModelObject)object;
			IProject project = EclipseResourceUtil.getProject(o);
			if(project == null) return;
			ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
			if(seamProject == null) return;
			ISeamComponent[] cs = seamProject.getComponents();
			for (ISeamComponent c: cs) {
				Set<ISeamXmlComponentDeclaration> ds = c.getXmlDeclarations();
				if(ds == null || ds.size() == 0) continue;
				ISeamXmlComponentDeclaration d = ds.iterator().next();
				Object id = ((SeamObject)d).getId();
				if(id == o) {
					component = c;
					break;
				}
			}
			if(component == null) {
				String name = o.getAttributeValue("name");
				if(name != null && name.length() > 0) {
					component = seamProject.getComponent(name);
				}
			}
		}
		
	}

}
