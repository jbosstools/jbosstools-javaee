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
package org.jboss.tools.seam.internal.core.validation;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamRuntimeValidation {
	static final String TYPE = "org.jboss.tools.seam.core.seamProblem";
	static final String ATTR_KIND = "kind";
	static final String RUNTIME_KIND = "SeamRuntimeProblem";
	
	public SeamRuntimeValidation() {}
	
	public void validate(ISeamProject project) throws CoreException {
		IMarker marker = findErrorMarker(project);
		String runtimeName = project.getRuntimeName();
		SeamRuntime runtime = project.getRuntime();
		if(runtime != null || runtimeName == null || runtimeName.length() == 0) {
			if(marker == null) return;
			removeMarker(marker);
			return;
		}
		String message = "Seam runtime " + runtimeName + " does not exist.";
		if(marker != null) {
			if(message.equals(marker.getAttribute(IMarker.MESSAGE, null))) {
				return;
			}
			marker.setAttribute(IMarker.MESSAGE, message);
		} else {
			createMarker(project, message);
		}
	}
	
	private IMarker findErrorMarker(ISeamProject project) throws CoreException {
		IMarker[] ms = project.getProject().findMarkers(TYPE, true, IResource.DEPTH_ZERO);
		if(ms == null) return null;
		for (int i = 0; i < ms.length; i++) {
			int s = ms[i].getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			if(s != IMarker.SEVERITY_WARNING) continue;
			String kind = ms[i].getAttribute(ATTR_KIND, null);
			if(kind == null) continue;
			if(kind.equals(RUNTIME_KIND)) {
				return ms[i];
			}
		}
		return null;
	}
	
	private void removeMarker(final IMarker marker) {
		IWorkspaceRunnable r = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				marker.delete();
			}
		};
		run(r);
	}
	
	private void createMarker(final ISeamProject project, final String message) throws CoreException {
		IWorkspaceRunnable r = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				doCreateMarker(project, message);
			}
		};
		run(r);
	}

	private void doCreateMarker(ISeamProject project, String message) throws CoreException {
		IMarker marker = project.getProject().createMarker(TYPE);
		marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(ATTR_KIND, RUNTIME_KIND);
	}

	private void run(IWorkspaceRunnable r) {
		try {
			ResourcesPlugin.getWorkspace().run(r, null,IWorkspace.AVOID_UPDATE, null);
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
	}

}
