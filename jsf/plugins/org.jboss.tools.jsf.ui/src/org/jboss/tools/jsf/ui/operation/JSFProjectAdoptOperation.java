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
package org.jboss.tools.jsf.ui.operation;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.common.model.XJob;
import org.jboss.tools.common.model.XModelConstants;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.JSFPreference;
import org.jboss.tools.jsf.project.JSFAutoLoad;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.web.JSFTemplate;
import org.jboss.tools.jsf.web.helpers.context.AdoptJSFProjectFinisher;
import org.jboss.tools.jst.web.context.IImportWebProjectContext;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.ui.operation.WebProjectAdoptOperation;

public class JSFProjectAdoptOperation extends WebProjectAdoptOperation {

	public JSFProjectAdoptOperation(IImportWebProjectContext context) {
		super(context);
	}
		
	protected void execute() throws XModelException {
		AdoptJSFProjectFinisher finisher = new AdoptJSFProjectFinisher();
		finisher.setContext(model, context);
		finisher.execute();
	}
	
	protected void copyLibraries() {
		if(!context.getAddLibraries()) return;
		String version = context.getTemplateVersion();
		JSFTemplate template = new JSFTemplate();
		String[] jars = template.getLibraries(version);
		String libDir = context.getLibLocation();
		if(libDir == null || libDir.trim().length() == 0) {
			//should not be
			return;
		}

		for (int i = 0; i < jars.length; i++) {
			File source = new File(jars[i]); 
			FileUtil.copyFile(source, new File(libDir, source.getName()), true);
		}
	}

	protected void postCreateWebNature() {
		final File projectFile = getEclipseFile();
		final IProject p = getProject();
		model.getProperties().put(XModelConstants.AUTOLOAD, new JSFAutoLoad());
		if(projectFile != null) {
			if(projectFile.isFile()) {
				XJob.addRunnable(new XJob.XRunnable() {
					public String getId() {
						return "Remove temporary working files."; //$NON-NLS-1$
					}
					public void run() {
						IFile f = EclipseResourceUtil.getFile(projectFile
								.getAbsolutePath());
						if (f != null && f.exists()) {
							try {
								f.delete(true, new NullProgressMonitor());
							} catch (CoreException e) {
								JSFModelPlugin.getPluginLog().logError(e);
								deleteExternally();
							}
						} else {
							deleteExternally();
						}
					}

					private void deleteExternally() {
						projectFile.delete();
						if(p != null && p.exists()) {
							try {
								p.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
							} catch (CoreException e2) {
								JSFModelPlugin.getPluginLog().logError(e2);
							}
						}
					}
			
				});
			}
		}
		try {
			EclipseResourceUtil.addNatureToProject(getProject(), IKbProject.NATURE_ID);
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
	}

	
    private File getEclipseFile() {
		String fn = getProject().getLocation().toString() + "/" + IModelNature.PROJECT_FILE; //$NON-NLS-1$
		File f = new File(fn);
		if(f.exists()) return f;
		return null;
    }
    
	protected String getNatureID() {
		return JSFNature.NATURE_ID;
	}

	protected String getDefaultServletVersion() {
		return JSFPreference.DEFAULT_JSF_IMPORT_SERVLET_VERSION.getValue();
	}

}
