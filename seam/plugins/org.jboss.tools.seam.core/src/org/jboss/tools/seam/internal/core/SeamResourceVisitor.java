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
package org.jboss.tools.seam.internal.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;
import org.jboss.tools.seam.internal.core.scanner.java.JavaScanner;
import org.jboss.tools.seam.internal.core.scanner.xml.PropertiesScanner;
import org.jboss.tools.seam.internal.core.scanner.xml.XMLScanner;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamResourceVisitor implements IResourceVisitor {
	static IFileScanner[] FILE_SCANNERS = {
		new JavaScanner(), 
		new XMLScanner(), 
		new PropertiesScanner(),
	};
	SeamProject p;
	
	IPath output = null;
	
	public SeamResourceVisitor(SeamProject p) {
		this.p = p;
		getJavaProjectOutput(p.getProject());
	}
	
	public IResourceVisitor getVisitor() {
		return this;
	}

	public boolean visit(IResource resource) {
		if(resource instanceof IFile) {
			IFile f = (IFile)resource;
			if(output != null && output.isPrefixOf(resource.getFullPath())) {
				return false;
			}
			for (int i = 0; i < FILE_SCANNERS.length; i++) {
				IFileScanner scanner = FILE_SCANNERS[i];
				if(scanner.isRelevant(f)) {
					if(!scanner.isLikelyComponentSource(f)) {
						p.pathRemoved(f.getFullPath());
						return false;
					}
					LoadedDeclarations c = null;
					try {
						c = scanner.parse(f);
					} catch (Exception e) {
						SeamCorePlugin.getDefault().logError(e);
					}
					if(c != null) componentsLoaded(c, f);
				}
			}
		}
		if(resource instanceof IFolder) {
			if(output != null && output.isPrefixOf(resource.getFullPath())) {
				return false;
			}
		}
		//return true to continue visiting children.
		return true;
	}
	
	void componentsLoaded(LoadedDeclarations c, IFile resource) {
		if(c == null || c.getComponents().size() + c.getFactories().size() == 0) return;
		p.registerComponents(c, resource.getFullPath());
	}

	public IPath getJavaProjectOutput(IProject project) {
		if(project == null || !project.isOpen()) return null;
		if(output != null) return output;
		try {
			if(!project.hasNature(JavaCore.NATURE_ID)) return null;
			IJavaProject javaProject = JavaCore.create(project);		
			return output = javaProject.getOutputLocation();
		} catch (Exception e) {
			SeamCorePlugin.getPluginLog().logError(e);
			return null;
		}
	}

}
