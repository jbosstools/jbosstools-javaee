/*******************************************************************************
 * Copyright (c) 2007-2010 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.editor.check;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.common.reporting.ProblemReportingHelper;
import org.jboss.tools.jst.jsp.JspEditorPlugin;
import org.jboss.tools.jst.jsp.util.FileUtil;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.internal.KbProject;
import org.jboss.tools.jst.web.project.WebProject;

/**
 * 
 * @author yzhishko
 * 
 */

public class ProjectNaturesChecker implements IResourceChangeListener {

	private static final String SEARCH_CLASS = "javax.faces.webapp.FacesServlet"; //$NON-NLS-1$
	public static final QualifiedName IS_NATURES_CHECK_NEED = new QualifiedName(
			"", "Is natures check"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final QualifiedName IS_JSF_CHECK_NEED = new QualifiedName(
			"", "Is JSF check"); //$NON-NLS-1$ //$NON-NLS-2$
	private Set<IProject> projectsCollection;
	private static final String JSF_NATURE = "JavaServer Faces Nature"; //$NON-NLS-1$
	private static final String KB_NATURE = "Knowledge Base Nature"; //$NON-NLS-1$
	private static final String STRUTS_NATURE_ID = "org.jboss.tools.struts.strutsnature"; //$NON-NLS-1$
	
	private static ProjectNaturesChecker checker;
	
	public static ProjectNaturesChecker getInstance(){
		if (checker == null) {
			checker = new ProjectNaturesChecker();
		}
		return checker;
	}
	
	private ProjectNaturesChecker() {
		projectsCollection = new HashSet<IProject>(0);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
				IResourceChangeEvent.POST_CHANGE);
	}

	public void resourceChanged(final IResourceChangeEvent event) {
		Display display = Display.getDefault();
		if (display != null) {
			display.asyncExec(new Runnable() {
				public void run() {
					handleResourceChangeEvent(event);
				}
			});
		}
	}

	public void checkNatures(IProject project) throws CoreException {
		if (project == null) {
			return;
		}
		addProject(project);
		boolean isJSFCheck = true;
		boolean isNaturesCheck = true;
		updateProjectPersistentProperties(project);
		isJSFCheck = Boolean.parseBoolean(project
				.getPersistentProperty(IS_JSF_CHECK_NEED));
		isNaturesCheck = Boolean.parseBoolean(project
				.getPersistentProperty(IS_NATURES_CHECK_NEED));
		if (isJSFCheck) {
			if (isNaturesCheck) {
				String[] missingNatures = getMissingNatures(project);
				if (missingNatures != null) {
					KbProject.checkKBBuilderInstalled(project);
					ProjectNaturesInfoDialog dialog = new ProjectNaturesInfoDialog(
							missingNatures, project);
					dialog.open();
				}
			}
		}
	}

	private String[] getMissingNatures(IProject project) throws CoreException {
		List<String> missingNatures = new ArrayList<String>(0);
		if (project.getNature(STRUTS_NATURE_ID) != null) {
			return null;
		}
		if (project.getNature(IKbProject.NATURE_ID) == null) {
			missingNatures.add(JSF_NATURE);
		}
		if (project.getNature(WebProject.JSF_NATURE_ID) == null) {
			missingNatures.add(KB_NATURE);
		}
		if (missingNatures.size() == 0) {
			return null;
		}
		return missingNatures.toArray(new String[0]);
	}

	private void handleResourceChangeEvent(IResourceChangeEvent changeEvent) {
		IResourceDelta[] affectedChildren = changeEvent.getDelta()
				.getAffectedChildren();
		if (affectedChildren == null) {
			return;
		}
		for (int i = 0; i < affectedChildren.length; i++) {
			IResourceDelta resourceDelta = affectedChildren[i];
			if (resourceDelta.getResource() instanceof IProject) {
				IProject project = (IProject) resourceDelta.getResource();
				if (resourceDelta.getKind() == IResourceDelta.ADDED) {
					processAddProject(project);
					continue;
				}
				if (resourceDelta.getKind() == IResourceDelta.REMOVED) {
					processRemoveProject(project);
					continue;
				}
				try {
					updateProjectJSFPersistents(project);
				} catch (CoreException e) {
					ProblemReportingHelper.reportProblem(JspEditorPlugin.PLUGIN_ID, e);
				}
			}
		}
	}

	private void updateProjectPersistentProperties(IProject project)
			throws CoreException {
		if (project.isAccessible()) {
			String jsfCheckString = project
					.getPersistentProperty(IS_JSF_CHECK_NEED);
			if (jsfCheckString == null) {
				updateProjectJSFPersistents(project);
			}
			if (project.getPersistentProperty(IS_NATURES_CHECK_NEED) == null) {
				project.setPersistentProperty(IS_NATURES_CHECK_NEED, "true"); //$NON-NLS-1$
			}
		}
	}

	public IProject getProject(IProject project) {
		return projectsCollection.contains(project) ? project : null;
	}

	public void addProject(IProject project) {
		if (getProject(project) == null) {
			projectsCollection.add(project);
		}
	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		projectsCollection.clear();
	}

	private void processAddProject(IProject project) {
		addProject(project);
		try {
			updateProjectJSFPersistents(project);
		} catch (CoreException e) {
			ProblemReportingHelper.reportProblem(JspEditorPlugin.PLUGIN_ID, e);
				}
	}

	private void processRemoveProject(IProject project) {
		projectsCollection.remove(project);
	}

	private void updateProjectJSFPersistents(IProject project)
			throws CoreException {
		if (project.isAccessible()) {
			try {
				IJavaElement javaElement = FileUtil.searchForClass(JavaCore
						.create(project), SEARCH_CLASS);
				if (javaElement == null) {
					project.setPersistentProperty(IS_JSF_CHECK_NEED, "false"); //$NON-NLS-1$
				} else {
					project.setPersistentProperty(IS_JSF_CHECK_NEED, "true"); //$NON-NLS-1$
				}
			} catch (CoreException e) {
				project.setPersistentProperty(IS_JSF_CHECK_NEED, "false"); //$NON-NLS-1$
			}
		}
	}
	
}
