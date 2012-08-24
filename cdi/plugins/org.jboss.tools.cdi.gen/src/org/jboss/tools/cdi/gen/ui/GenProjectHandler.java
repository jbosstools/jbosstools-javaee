/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.gen.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.RefreshAction;
import org.jboss.tools.cdi.gen.CDIProjectGenerator;
import org.jboss.tools.common.model.plugin.ModelPlugin;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class GenProjectHandler implements IObjectActionDelegate {
	ISelection selection;

	@Override
	public void run(IAction action) {
		CDIProjectGenerator g = new CDIProjectGenerator();
		IProject project = null;
		if(selection != null && !selection.isEmpty() && selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection)selection;
			Object o = ss.getFirstElement();
			if(o instanceof IProject) {
				project = (IProject)o;
				if(!project.isAccessible()) project = null;
			}
		}
		if(project != null) {
			g.setWorkspaceLocation(project.getLocation().toFile().getParentFile());
			g.generate(project.getName());
			RefreshAction refreshAction = new RefreshAction(ModelPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow());
			refreshAction.selectionChanged(new StructuredSelection(project));
			refreshAction.run();
		} else {
			g.setWorkspaceLocation(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile());
			g.generate("GeneratedProject");
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

}
