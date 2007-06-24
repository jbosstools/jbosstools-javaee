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


import java.util.List;
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
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.seam.internal.core.SeamComponent;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;

public class JavaScanner implements IFileScanner {
	static String NAME_ANNOTATION_TYPE = "org.jboss.seam.annotations.Name";
	static String SCOPE_ANNOTATION_TYPE = "org.jboss.seam.annotations.Scope";
	
	
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
	public SeamComponent[] parse(IFile f) throws Exception {
		ICompilationUnit u = getCompilationUnit(f);
		if(u == null) return null;
		ASTRequestorImpl requestor = new ASTRequestorImpl();
		ICompilationUnit[] us = new ICompilationUnit[]{u};
		ASTParser.newParser(AST.JLS3).createASTs(us, new String[0], requestor, null);
		SeamComponent component = requestor.getComponent();
		return component == null ? new SeamComponent[0] : new SeamComponent[]{component};
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
		private SeamComponent component = null;

		public SeamComponent getComponent() {
			return component;
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
				component = new SeamComponent();
				component.setClassName(n);
				component.setName(visitor.name);
				if(visitor.scope != null) {
					component.setScope(visitor.scope);
				}
			}			
		}
	}
	
	class ASTVisitorImpl extends ASTVisitor {
		IType type;
		String name = null;
		String scope = null;
		public boolean visit(SingleMemberAnnotation node) {
			if(checkAnnotationType(node, NAME_ANNOTATION_TYPE)) {
				name = checkExpression(node.getValue());
				return true;
			} else if(checkAnnotationType(node, SCOPE_ANNOTATION_TYPE)) {
				scope = checkExpression(node.getValue());
				if(scope != null) {
					int i = scope.lastIndexOf('.');
					if(i >= 0) scope = scope.substring(i + 1).toLowerCase();
				}
				return true;
			}
			return false;
		}
		public boolean visit(NormalAnnotation node) {
			if(checkAnnotationType(node, NAME_ANNOTATION_TYPE)) {
				name = getValue(node);
				return true;
			} else if(checkAnnotationType(node, SCOPE_ANNOTATION_TYPE)) {
				scope = getValue(node);
				return true;
			}
			return false;
		}
		
		String getValue(NormalAnnotation node) {
			List vs = node.values();
			if(vs != null) for (int i = 0; i < vs.size(); i++) {
				MemberValuePair p = (MemberValuePair)vs.get(i);
				if("value".equals(p.getName().getIdentifier())) {
					return checkExpression(p.getValue());
				}
			}
			return null;
		}
		
		
		boolean checkAnnotationType(Annotation node, String annotationType) {
			Name nm = node.getTypeName();
			if(nm instanceof SimpleName) {
				SimpleName sn = (SimpleName)nm;
				String n = sn.getIdentifier();
				if(type != null) {
					n = getResolvedType(type, n);
				}
				if(!annotationType.equals(n)) return false;
			} else if(nm instanceof QualifiedName) {
				QualifiedName qn = (QualifiedName)nm;
				if(!qn.getFullyQualifiedName().equals(annotationType)) return false;
				//improve
			} else {
				return false;
			}
			return true;
		}
		
		String checkExpression(Expression exp) {
			if(exp instanceof StringLiteral) {
				return ((StringLiteral)exp).getLiteralValue();
			} else if(exp instanceof QualifiedName) {
				return exp.toString();
			}
			return null;
		}

		public boolean visit(Block node) {
			return false;
		}
		public boolean visit(MethodDeclaration node) {
			return false;
		}
	}
	
	String getResolvedType(IType type, String n) {
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
