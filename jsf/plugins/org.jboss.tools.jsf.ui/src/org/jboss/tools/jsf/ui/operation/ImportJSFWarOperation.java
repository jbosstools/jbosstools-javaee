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
import java.io.IOException;

import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jst.web.context.IImportWebProjectContext;
import org.jboss.tools.jst.web.context.ImportWebWarContext;

public class ImportJSFWarOperation extends JSFProjectAdoptOperation {
	
	public ImportJSFWarOperation(IImportWebProjectContext context) {
		super(context);
	}

	protected AbstractOperation createWTPNature(IProgressMonitor monitor) throws CoreException {
		copyProject();
		getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
		return super.createWTPNature(monitor);
	}

	protected void createWebNature() throws CoreException {
		super.createWebNature();
	}
	
	protected void execute() throws XModelException {
		((ImportWebWarContext)context).prepareModules();
		super.execute();
	}

	protected void copyProject() {
		String targetLocation = context.getSuggestedProjectLocation();
		ImportWebWarContext warContext = (ImportWebWarContext)context;
		String warLocation = warContext.getWarLocation();
		File target = new File(targetLocation);
		String[] os = warContext.getOriginalSources();
		String[] jss = warContext.getExistingSources();
		boolean classes = false;
		if(!warContext.isClassicEclipseProject()) {
			try {
				FileUtil.unjar(target, warLocation);
			} catch (IOException e) {
				JsfUiPlugin.getPluginLog().logError(e);
			}
			for (int i = 0; i < os.length; i++) {
				File d = new File(os[i]);
				if(!d.isDirectory()) continue;
				if("classes".equals(d.getName())) classes = true; //$NON-NLS-1$
			}
		} else {
			File webContent = new File(target, "WebContent"); //$NON-NLS-1$
			try {
				FileUtil.unjar(webContent, warLocation);
			} catch (IOException e) {
				JsfUiPlugin.getPluginLog().logError(e);
			}
			for (int i = 0; i < os.length; i++) {
				File d = new File(os[i]);
				if(!d.isDirectory()) continue;
				File js = new File(jss[i]);
				if("classes".equals(d.getName())) classes = true; //$NON-NLS-1$
				if(d.equals(js)) continue;
				boolean delete = copySrc(d, js);
				if(delete) {
					FileUtil.clear(d);
					d.delete();
				}
			}
		}
		if(!classes && jss.length > 0) {
			File d = new File(context.getWebInfLocation() + "/classes"); //$NON-NLS-1$
			if(d.isDirectory()) copySrc(d, new File(jss[0]));
		}

	}
	
	//returns true if only *.java and *.properties files are found 
	private boolean copySrc(File from, File to) {
		File[] fs = from.listFiles();
		if(fs == null) return true;
		boolean result = true;
		for (int i = 0; i < fs.length; i++) {
			String n = fs[i].getName();
			File to1 = new File(to, n);
			if(fs[i].isDirectory()) {				
				if(!copySrc(fs[i], to1)) result = false;
			}
			if(fs[i].isFile()) {
				if(!n.endsWith(".java") && !n.endsWith(".properties")) { //$NON-NLS-1$ //$NON-NLS-2$
					result = false;
				} else {
					FileUtil.copyFile(fs[i], to1, true);
				}
			}
		}
		return result;
	}

}
