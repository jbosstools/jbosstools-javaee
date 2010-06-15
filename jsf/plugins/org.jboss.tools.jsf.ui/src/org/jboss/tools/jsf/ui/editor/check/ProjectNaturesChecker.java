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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.reporting.ProblemReportingHelper;
import org.jboss.tools.jsf.ui.JsfUIMessages;
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

	private ProjectNaturesPartListener partListener = new ProjectNaturesPartListener();
	private static final String SEARCH_CLASS = "javax.faces.webapp.FacesServlet"; //$NON-NLS-1$
	public static final QualifiedName IS_JSF_NATURES_CHECK_NEED = new QualifiedName(
			"", JsfUIMessages.IS_JSF_NATURE_CHECK_NEED); //$NON-NLS-1$
	public static final QualifiedName IS_KB_NATURES_CHECK_NEED = new QualifiedName(
			"", JsfUIMessages.IS_KB_NATURE_CHECK_NEED); //$NON-NLS-1$
	public static final QualifiedName IS_JSF_CHECK_NEED = new QualifiedName(
			"", JsfUIMessages.IS_JSF_CHECK_NEED); //$NON-NLS-1$
	private Set<IProject> projectsCollection;
	private static final String STRUTS_NATURE_ID = "org.jboss.tools.struts.strutsnature"; //$NON-NLS-1$

	private static ProjectNaturesChecker checker;

	public static ProjectNaturesChecker getInstance() {
		if (checker == null) {
			checker = new ProjectNaturesChecker();
		}
		return checker;
	}

	private ProjectNaturesChecker() {
		projectsCollection = new HashSet<IProject>(0);
//		ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
//				IResourceChangeEvent.POST_CHANGE);
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		window.getPartService().addPartListener(partListener);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
				.addDisposeListener(new DisposeListener() {

					public void widgetDisposed(DisposeEvent e) {
						dispose();
					}
				});
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
		boolean isJSFNaturesCheck = true;
		boolean isKBNaturesCheck = true;
		updateProjectPersistentProperties(project);
		isJSFCheck = Boolean.parseBoolean(project
				.getPersistentProperty(IS_JSF_CHECK_NEED));
		isJSFNaturesCheck = Boolean.parseBoolean(project
				.getPersistentProperty(IS_JSF_NATURES_CHECK_NEED));
		isKBNaturesCheck = Boolean.parseBoolean(project
				.getPersistentProperty(IS_KB_NATURES_CHECK_NEED));
		KbProject.checkKBBuilderInstalled(project);
		String missingNature = checkMissingNatures(project);
		if (missingNature != null) {
			ProjectNaturesInfoDialog dialog = null;
			if (KbProject.NATURE_ID.equals(missingNature) && isKBNaturesCheck) {
				dialog = new KBNaturesInfoDialog(project);
			} else if (WebProject.JSF_NATURE_ID.equals(missingNature)
					&& isJSFNaturesCheck && isJSFCheck) {
				dialog = new JSFNaturesInfoDialog(project);
			}
			if (dialog != null) {
				dialog.open();
			}
		}
	}

	private String checkMissingNatures(IProject project) throws CoreException {
		if (project.getNature(STRUTS_NATURE_ID) != null) {
			return null;
		}
		if (project.getNature(WebProject.JSF_NATURE_ID) == null) {
			return WebProject.JSF_NATURE_ID;
		}
		if (getKBProblemMarker(project) != null) {
			return IKbProject.NATURE_ID;
		}
		return null;
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
					ProblemReportingHelper.reportProblem(
							JspEditorPlugin.PLUGIN_ID, e);
				}
			}
		}
	}

	private void updateProjectPersistentProperties(IProject project)
			throws CoreException {
		if (project.isAccessible()) {
//			String jsfCheckString = project
//					.getPersistentProperty(IS_JSF_CHECK_NEED);
//			if (jsfCheckString == null) {
				updateProjectJSFPersistents(project);
//			}
			if (project.getPersistentProperty(IS_JSF_NATURES_CHECK_NEED) == null) {
				project
						.setPersistentProperty(IS_JSF_NATURES_CHECK_NEED,
								Boolean.TRUE.toString());
			}
			if (project.getPersistentProperty(IS_KB_NATURES_CHECK_NEED) == null) {
				project.setPersistentProperty(IS_KB_NATURES_CHECK_NEED, Boolean.TRUE.toString());
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
		if (partListener != null) {
			IWorkbenchWindow window = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			window.getPartService().removePartListener(partListener);
			partListener = null;
		}
//		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
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
					project.setPersistentProperty(IS_JSF_CHECK_NEED, Boolean.FALSE.toString());
				} else {
					project.setPersistentProperty(IS_JSF_CHECK_NEED, Boolean.TRUE.toString());
				}
			} catch (CoreException e) {
				project.setPersistentProperty(IS_JSF_CHECK_NEED, Boolean.FALSE.toString());
			}
		}
	}

	private IMarker getKBProblemMarker(IProject project) {
		IMarker kbProblemMarker = null;
		try {
			IMarker[] markers = project.findMarkers(null, false, 1);
			for (int i = 0; i < markers.length; i++) {
				IMarker marker = markers[i];
				String _type = marker.getType();
				if (_type != null
						&& _type.equals(KbProject.KB_PROBLEM_MARKER_TYPE)) {
					kbProblemMarker = marker;
					break;
				}
			}
		} catch (CoreException e) {
		}
		return kbProblemMarker;
	}

}
