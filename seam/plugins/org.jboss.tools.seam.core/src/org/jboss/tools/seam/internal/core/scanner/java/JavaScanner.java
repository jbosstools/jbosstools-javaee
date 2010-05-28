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
package org.jboss.tools.seam.internal.core.scanner.java;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTRequestor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;
import org.jboss.tools.seam.internal.core.scanner.ScannerException;

/**
 * This object collects changes in target that should be fired to listeners.
 * 
 * @author Viacheslav Kabanovich
 */
public class JavaScanner implements IFileScanner {
	
	public JavaScanner() {}

	/**
	 * Returns true if file is java source - has *.java mask.
	 * @param resource
	 * @return
	 */	
	public boolean isRelevant(IFile resource) {
		return resource.getName().endsWith(".java");
	}
	
	/**
	 * This method should be called only if isRelevant returns true;
	 * Makes simple check if this java file contains annotation Name. 
	 * @param resource
	 * @return
	 */
	public boolean isLikelyComponentSource(IFile f) {
		if(!f.isSynchronized(IFile.DEPTH_ZERO) || !f.exists()) return false;
		String content = FileUtil.readFile(f.getLocation().toFile());
		if(content == null) return false;
		int a = content.indexOf("org.jboss.seam.annotations."); //$NON-NLS-1$
		if(a < 0) {
			a = content.indexOf("javax.ejb.");
			if(a < 0) return false;
		}
		int i = content.indexOf("@"); //$NON-NLS-1$
		if(i < 0) return false;
		return true;
	}
	
	private ASTParser p = ASTParser.newParser(AST.JLS3);

	/**
	 * Returns component or list of component
	 * TODO change return type
	 * @param f
	 * @return
	 * @throws ScannerException
	 */
	public LoadedDeclarations parse(IFile f, ISeamProject sp) throws ScannerException {
		ICompilationUnit u = null;
		try {
			u = getCompilationUnit(f);
		} catch (CoreException e) {
			throw new ScannerException(
					NLS.bind(SeamCoreMessages.JAVA_SCANNER_CANNOT_GET_COMPILATION_UNIT_FOR,f), e);
		}
		if(u == null) return null;
		ICompilationUnit[] us = new ICompilationUnit[]{u};
		p.setSource(u);
		p.setResolveBindings(true);
		if("package-info.java".equals(f.getFullPath().lastSegment())) {
			PackageInfoRequestor requestor = new PackageInfoRequestor(f);
			p.createASTs(us, new String[0], requestor, null);
			return requestor.getDeclarations();
		}
		ASTRequestorImpl requestor = new ASTRequestorImpl(f);
		p.createASTs(us, new String[0], requestor, null);
		return requestor.getDeclarations();
	}
	
	private ICompilationUnit getCompilationUnit(IFile f) throws CoreException {
		IProject project = f.getProject();
		IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
		IResource[] rs = EclipseResourceUtil.getJavaSourceRoots(project);
		for (int i = 0; i < rs.length; i++) {
			if(rs[i].getFullPath().isPrefixOf(f.getFullPath())) {
				IPath path = f.getFullPath().removeFirstSegments(rs[i].getFullPath().segmentCount());
				IJavaElement e = javaProject.findElement(path);
				if(e == null && path.lastSegment().equals("package-info.java")) {
					//strange but sometimes only this works
					IJavaElement ep = javaProject.findElement(path.removeLastSegments(1));
					if(ep instanceof IPackageFragment) {
						e = ((IPackageFragment)ep).getCompilationUnit("package-info.java");
					}
				}
				if(e instanceof ICompilationUnit) {
					return (ICompilationUnit)e;
				}
			}
		}
		return null;
	}


	class ASTRequestorImpl extends ASTRequestor {
		private ASTVisitorImpl visitor = new ASTVisitorImpl();
		LoadedDeclarations ds = new LoadedDeclarations();
		IResource resource;
		IPath sourcePath;
		
		public ASTRequestorImpl(IResource resource) {
			this.resource = resource;
			this.sourcePath = resource.getFullPath();
		}

		public LoadedDeclarations getDeclarations() {
			return ds;
		}
		
		public void acceptAST(ICompilationUnit source, CompilationUnit ast) {
			IType[] ts = null;
			try {
				ts = source.getTypes();
			} catch (JavaModelException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
			if(ts == null || ts.length == 0) return;
			for (int i = 0; i < ts.length; i++) {
				visitor.setType(null);
				int f = 0;
				try {
					f = ts[i].getFlags();
				} catch (JavaModelException e) {
					SeamCorePlugin.getPluginLog().logError(e);
					continue;
				}
				if(Flags.isPublic(f)) {
					visitor.setType(ts[i]);
					ast.accept(visitor);
					if(!visitor.hasSeamComponent()) continue;
					processTypeData(visitor.root);
				}
			}
		}
		
		private void processTypeData(ASTVisitorImpl.TypeData data) {
			if(data.hasSeamComponentItself()) {
				ComponentBuilder b = new ComponentBuilder(ds, data);
				
				b.component.setSourcePath(sourcePath);
				b.component.setResource(resource);
				
				b.process();
			}
			for (ASTVisitorImpl.TypeData c: data.children) {
				processTypeData(c);
			}
		}
	}
	
	static String getResolvedType(IType type, String n) {
		String result = EclipseJavaUtil.resolveType(type, n);
		return result == null ? n : result;
	}

}
//:Pserver:anonymous@anoncvs.forge.jboss.com:/cvsroot/jboss