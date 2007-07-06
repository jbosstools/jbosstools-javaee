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
package org.jboss.tools.seam.internal.core.scanner.java;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTRequestor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.seam.internal.core.SeamJavaComponentDeclaration;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;

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
		if(resource.getName().endsWith(".java")) return true;
		return false;
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
		int a = content.indexOf("org.jboss.seam.annotations.");
		if(a < 0) return false;
		int i = content.indexOf("@");
		if(i < 0) return false;
		return true;
	}
	
	/**
	 * Returns component or list of component
	 * TODO change return type
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public LoadedDeclarations parse(IFile f) throws Exception {
		ICompilationUnit u = getCompilationUnit(f);
		if(u == null) return null;
		ASTRequestorImpl requestor = new ASTRequestorImpl(f);
		ICompilationUnit[] us = new ICompilationUnit[]{u};
		ASTParser.newParser(AST.JLS3).createASTs(us, new String[0], requestor, null);
		return requestor.getDeclarations();
	}
	
	private ICompilationUnit getCompilationUnit(IFile f) throws Exception {
		IProject project = f.getProject();
		IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
		IResource[] rs = EclipseResourceUtil.getJavaSourceRoots(project);
		for (int i = 0; i < rs.length; i++) {
			if(rs[i].getFullPath().isPrefixOf(f.getFullPath())) {
				IPath path = f.getFullPath().removeFirstSegments(rs[i].getFullPath().segmentCount());
				IJavaElement e = javaProject.findElement(path);
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
			visitor.name = null;
			
			try {
				IType[] ts = source.getTypes();
				if(ts != null && ts.length > 0) {
					visitor.type = ts[0];
				}
			} catch (Exception e) {
				//ignore
			}
			ast.accept(visitor);
			if(visitor.name != null && visitor.type != null) {
				String n = visitor.type.getElementName();
				n = getResolvedType(visitor.type, n);
				SeamJavaComponentDeclaration component = new SeamJavaComponentDeclaration();
				
				component.setId(visitor.type);
				component.setSourcePath(sourcePath);
				component.setResource(resource);

				ds.getComponents().add(component);
				component.setType(visitor.type);
				component.setId(visitor.type);
				component.setClassName(n);
				component.setName(visitor.name);
				if(visitor.scope != null) {
					component.setScope(visitor.scope);
				}
			}			
		}
	}
	
	static String getResolvedType(IType type, String n) {
		try {
			String[][] rs = type.resolveType(n);
			if(rs != null && rs.length > 0) {
				return (rs[0][0].length() == 0) ? rs[0][1] : rs[0][0] + "." + rs[0][1];
			}
		} catch (Exception e) {
			//ignore
		}
		return n;
	}

}
