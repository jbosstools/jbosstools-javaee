/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jst.web.model.helpers.InnerModelHelper;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;
import org.jboss.tools.seam.internal.core.scanner.ScannerException;
import org.jboss.tools.seam.internal.core.scanner.java.JavaScanner;
import org.jboss.tools.seam.internal.core.scanner.xml.PropertiesScanner;
import org.jboss.tools.seam.internal.core.scanner.xml.XMLScanner;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamResourceVisitor implements IResourceVisitor, IResourceDeltaVisitor {
	static IFileScanner[] FILE_SCANNERS = {
		new JavaScanner(), 
		new XMLScanner(), 
		new PropertiesScanner(),
	};
	SeamProject p;
	
	IPath[] outs = new IPath[0];
	IPath[] srcs = new IPath[0];
	IPath webinf = null;
	
	public SeamResourceVisitor(SeamProject p) {
		this.p = p;

		if(p.getProject() != null && p.getProject().isOpen()) {
			getJavaSourceRoots(p.getProject());

			XModel model = InnerModelHelper.createXModel(p.getProject());
			if(model != null) {
				XModelObject wio = FileSystemsHelper.getWebInf(model);
				if(wio != null) {
					IResource wir = (IResource)wio.getAdapter(IResource.class);
					if(wir != null) {
						webinf = wir.getFullPath();
					}
				}
			}
		}
	}

	public boolean visit(IResource resource) {
		if(resource instanceof IFile) {
			IFile f = (IFile)resource;
			if(!shouldVisitFile(f)) return false;
			for (int i = 0; i < FILE_SCANNERS.length; i++) {
				IFileScanner scanner = FILE_SCANNERS[i];
				if(scanner.isRelevant(f)) {
					long t = System.currentTimeMillis();
					if(!scanner.isLikelyComponentSource(f)) {
						p.pathRemoved(f.getFullPath());
						return false;
					}
					LoadedDeclarations c = null;
					try {
						c = scanner.parse(f, p);
					} catch (ScannerException e) {
						SeamCorePlugin.getDefault().logError(e);
					}
					if(c != null) componentsLoaded(c, f);
					long dt = System.currentTimeMillis() - t;
					timeUsed += dt;
//					System.out.println("Time=" + timeUsed);
				}
			}
			return true;
		}
		return shouldVisitFolder(resource);
	}
	
	static long timeUsed = 0;
	
	void componentsLoaded(LoadedDeclarations c, IFile resource) {
		if(c == null || c.getComponents().size() + c.getFactories().size() + c.getNamespaces().size() + c.getImports().size() == 0) return;
		p.registerComponents(c, resource.getFullPath());
	}

	void getJavaSourceRoots(IProject project) {
		IJavaProject javaProject = EclipseResourceUtil.getJavaProject(project);
		if(javaProject == null) return;
		List<IPath> ps = new ArrayList<IPath>();
		List<IPath> os = new ArrayList<IPath>();
		try {
			IPath output = javaProject.getOutputLocation();
			if(output != null) os.add(output);
			IClasspathEntry[] es = javaProject.getResolvedClasspath(true);
			for (int i = 0; i < es.length; i++) {
				if(es[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					IResource findMember = ResourcesPlugin.getWorkspace().getRoot().findMember(es[i].getPath());
					if(findMember != null && findMember.exists()) {
						ps.add(findMember.getFullPath());
					}
					IPath out = es[i].getOutputLocation();
					if(out != null && !os.contains(out)) {
						os.add(out);
					}
				} 
			}
			srcs = ps.toArray(new IPath[ps.size()]);
			outs = os.toArray(new IPath[os.size()]);
		} catch(CoreException ce) {
			ModelPlugin.getPluginLog().logError("Error while locating java source roots for " + project, ce);
		}
	}

	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		switch (delta.getKind()) {
		case IResourceDelta.ADDED:
		case IResourceDelta.CHANGED:
			if(resource instanceof IFile) {
				IFile f = (IFile)resource;
				if(!shouldVisitFile(f)) return false;
				for (int i = 0; i < FILE_SCANNERS.length; i++) {
					IFileScanner scanner = FILE_SCANNERS[i];
					if(scanner.isRelevant(f)) {
						long t = System.currentTimeMillis();
						if(!scanner.isLikelyComponentSource(f)) {
							p.pathRemoved(f.getFullPath());
							return false;
						}
						LoadedDeclarations c = null;
						try {
							c = scanner.parse(f, p);
						} catch (ScannerException e) {
							SeamCorePlugin.getDefault().logError(e);
						}
						if(c != null) componentsLoaded(c, f);
						long dt = System.currentTimeMillis() - t;
						timeUsed += dt;
//						System.out.println("Time=" + timeUsed);
					}
				}
			}
			if(resource instanceof IFolder) {
				return shouldVisitFolder(resource);
			}
			//return true to continue visiting children.
			return true;
		case IResourceDelta.REMOVED:
			p.pathRemoved(resource.getFullPath());
			break;
		}
		return true;
	}

	boolean shouldVisitFile(IResource resource) {
		for (int i = 0; i < outs.length; i++) {
			if(outs[i].isPrefixOf(resource.getFullPath())) {
				return false;
			}
		}
		return true;
	}

	boolean shouldVisitFolder(IResource resource) {
		IPath path = resource.getFullPath();
		for (int i = 0; i < outs.length; i++) {
			if(outs[i].isPrefixOf(path)) {
				return false;
			}
		}
		for (int i = 0; i < srcs.length; i++) {
			if(srcs[i].isPrefixOf(path) || path.isPrefixOf(srcs[i])) {
				return true;
			}
		}
		if(webinf != null) {
			if(webinf.isPrefixOf(path) || path.isPrefixOf(webinf)) {
				return true;
			}
		}
		if(resource == resource.getProject()) {
			return true;
		}
		return false;
	}

}
