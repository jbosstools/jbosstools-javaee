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
package org.jboss.tools.seam.ui.wizard;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIMessages;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.ui.widget.editor.ButtonFieldEditor;
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.SeamUIMessages;

/**
 * @author Alexey Kazakov
 */
public class SelectJavaPackageAction extends ButtonFieldEditor.ButtonPressedAction {

	public SelectJavaPackageAction() {
		super(SeamUIMessages.SELECT_SEAM_PROJECT_ACTION_BROWSE);
	}

	@Override
	public void run() {
		String projectName = (String)getFieldEditor().getData(IParameter.SEAM_PROJECT_NAME);
		if(projectName == null) {
			return;
		}
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if(project == null) {
			SeamGuiPlugin.getPluginLog().logError("Can't find java project with name: " + projectName);
			return;
		}
		SeamProjectsSet seamPrjSet = new SeamProjectsSet(project);
		IProject ejbProject = seamPrjSet.getEjbProject();
		if(ejbProject!=null) {
			project = ejbProject;
		}
		IJavaProject javaProject = EclipseResourceUtil.getJavaProject(project);
		if(javaProject == null) {
			SeamGuiPlugin.getPluginLog().logError("Can't find java project with name: " + projectName);
			return;
		}
		IPackageFragmentRoot packageFragmentRoot = null;
		IPackageFragmentRoot[] roots;
		try {
			roots = javaProject.getPackageFragmentRoots();
			for (int i = 0; i < roots.length; i++) {
				if (roots[i].getKind() == IPackageFragmentRoot.K_SOURCE) {
					packageFragmentRoot = roots[i];
					break;
				}
			}
		} catch (JavaModelException e) {
			SeamGuiPlugin.getPluginLog().logError(e);
		}
		if (packageFragmentRoot == null) {
			packageFragmentRoot = javaProject.getPackageFragmentRoot(javaProject.getResource());
		}
		if (packageFragmentRoot == null) {
			SeamGuiPlugin.getPluginLog().logError("Can't find src folder for project " + projectName);
			return;
		}
		IJavaElement[] packages = null;
		try {
			packages = packageFragmentRoot.getChildren();
		} catch (JavaModelException e) {
			SeamGuiPlugin.getPluginLog().logError(e);
		}
		if (packages == null) {
			packages = new IJavaElement[0];
		}

		String initialValue = getFieldEditor().getValue().toString();
		IJavaElement initialElement = null;
		ArrayList<IJavaElement> packagesWithoutDefaultPackage = new ArrayList<IJavaElement>();
		for (IJavaElement packageElement : packages) {
			String packageName = packageElement.getElementName();
			if(packageName.length()>0) {
				packagesWithoutDefaultPackage.add(packageElement);
				if(packageName.equals(initialValue)) {
					initialElement = packageElement;
				}
			}
		}

		packages = packagesWithoutDefaultPackage.toArray(new IJavaElement[packagesWithoutDefaultPackage.size()]);
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(Display.getCurrent().getActiveShell(), new JavaElementLabelProvider(
				JavaElementLabelProvider.SHOW_DEFAULT));
		dialog.setTitle(J2EEUIMessages.PACKAGE_SELECTION_DIALOG_TITLE);
		dialog.setMessage(J2EEUIMessages.PACKAGE_SELECTION_DIALOG_DESC);
		dialog.setEmptyListMessage(J2EEUIMessages.PACKAGE_SELECTION_DIALOG_MSG_NONE);
		dialog.setElements(packages);
		if(initialElement!=null) {
			dialog.setInitialSelections(new Object[]{initialElement});
		}
		if (dialog.open() == Window.OK) {
			IPackageFragment fragment = (IPackageFragment) dialog.getFirstResult();
			if (fragment != null) {
				getFieldEditor().setValue(fragment.getElementName());
			} else {
				getFieldEditor().setValue("");
			}
		}
	}
}