 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.scanner.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.scanner.Util;

/**
 * Processes AST tree to find annotated type, fields and methods.
 * 
 * @author Viacheslav Kabanovich
 */
public class ASTVisitorImpl extends ASTVisitor implements SeamAnnotations {
	
	public static class TypeData {
		TypeData parent = null;
		List<TypeData> children = new ArrayList<TypeData>();

		public IType type;
		int innerLock = 0;
		
		public AnnotatedASTNode<AbstractTypeDeclaration> annotatedType = null;
		public Set<AnnotatedASTNode<FieldDeclaration>> annotatedFields = new HashSet<AnnotatedASTNode<FieldDeclaration>>();
		public Set<AnnotatedASTNode<MethodDeclaration>> annotatedMethods = new HashSet<AnnotatedASTNode<MethodDeclaration>>();

		AnnotatedASTNode<?> currentAnnotatedNode = null;
		AnnotatedASTNode<FieldDeclaration> currentAnnotatedField = null;
		AnnotatedASTNode<MethodDeclaration> currentAnnotatedMethod = null;
		
		public boolean hasSeamComponentItself() {
			if(!annotatedFields.isEmpty() || !annotatedMethods.isEmpty()) return true;
			if(annotatedType != null && annotatedType.getAnnotations() != null) return true;
			return false;
		}
		
		public boolean hasSeamComponent() {
			if(hasSeamComponentItself()) return true;
			for (TypeData c: children) {
				if(c.hasSeamComponent()) return true;
			}
			return false;
		}
		
	}
	
	public TypeData root = null;	
	TypeData current = null;	
	
	public ASTVisitorImpl() {}
	
	public void setType(IType type) {
		root = new TypeData();
		root.type = type;
	}
	
	
	public boolean hasSeamComponent() {
		return root.hasSeamComponent();
	}

	public boolean visit(SingleMemberAnnotation node) {
		if(current.innerLock > 0) return false;
		String type = resolveType(node);
		if(Util.isSeamAnnotationType(type) && current.currentAnnotatedNode != null) {
			current.currentAnnotatedNode.addAnnotation(new ResolvedAnnotation(type, node));
		}
		return false;
	}

	public boolean visit(NormalAnnotation node) {
		if(current.innerLock > 0) return false;
		String type = resolveType(node);
		if(Util.isSeamAnnotationType(type) && current.currentAnnotatedNode != null) {
			current.currentAnnotatedNode.addAnnotation(new ResolvedAnnotation(type, node));
		}
		return false;
	}

	public boolean visit(MarkerAnnotation node) {
		if(current.innerLock > 0) return false;
		String type = resolveType(node);
		if(Util.isSeamAnnotationType(type) && current.currentAnnotatedNode != null) {
			current.currentAnnotatedNode.addAnnotation(new ResolvedAnnotation(type, node));
		}
		return true;
	}

	boolean checkAnnotationType(Annotation node, String annotationType) {
		String n = resolveType(node);
		return n != null && n.equals(annotationType);
	}
	
	String resolveType(Annotation node) {
		return resolveType(current.type, node);
	}

	static String resolveType(IType type, Annotation node) {
		Name nm = node.getTypeName();
		if(nm instanceof SimpleName) {
			SimpleName sn = (SimpleName)nm;
			String n = sn.getIdentifier();
			if(type != null) {
				return JavaScanner.getResolvedType(type, n);
			}
		} else if(nm instanceof QualifiedName) {
			QualifiedName qn = (QualifiedName)nm;
			return qn.getFullyQualifiedName();
		}
		return null;
	}

	public boolean visit(Block node) {
		return false;
	}

	public boolean visit(TypeDeclaration node) {
		return _visit(node);
	}
	
	public boolean visit(EnumDeclaration node) {
		return _visit(node);
	}

	public boolean visit(AnnotationTypeDeclaration node) {
		return _visit(node);
	}

	private boolean _visit(AbstractTypeDeclaration node) {
		if(current == null) {
			String n = node.getName().getFullyQualifiedName();
			if(n != null && n.indexOf('.') < 0) n = EclipseJavaUtil.resolveType(root.type, n);
			String nr = root.type.getFullyQualifiedName();
			if(n == null || !n.equals(nr)) return false;
			current = root;
		}
		if(current.annotatedType == null) {
			current.annotatedType = new AnnotatedASTNode<AbstractTypeDeclaration>(node);
			current.currentAnnotatedNode = current.annotatedType;
		} else {
			String n = node.getName().getFullyQualifiedName();
			if(n != null && n.indexOf('.') < 0) n = EclipseJavaUtil.resolveType(current.type, n);
			IType[] ts = null;
			try {
				ts = current.type.getTypes();
			} catch (JavaModelException e) {
				SeamCorePlugin.getDefault().logError(e);
			}
			IType t = null;
			if(ts != null) for (int i = 0; t == null && i < ts.length; i++) {
				try {
					if(!Flags.isStatic(ts[i].getFlags())) continue;
				} catch (JavaModelException e) {
					continue;
				}
				String ni = ts[i].getFullyQualifiedName();
				if(ni != null) ni = ni.replace('$', '.');
				if(n == null || !n.equals(ni)) continue;
				t = ts[i];
			}
			if(t == null) {
				current.innerLock++;
				return false;
			} else {
				TypeData d = new TypeData();
				d.type = t;
				d.parent = current;
				current.children.add(d);
				current = d;
				current.annotatedType = new AnnotatedASTNode<AbstractTypeDeclaration>(node);
				current.currentAnnotatedNode = current.annotatedType;
			}			
		}
		return true;
	}

	public void endVisit(TypeDeclaration node) {
		_endVisit(node);
	}
	public void endVisit(AnnotationTypeDeclaration node) {
		_endVisit(node);
	}
	public void endVisit(EnumDeclaration node) {
		_endVisit(node);
	}
	public void _endVisit(AbstractTypeDeclaration node) {
		if(current == null) return;
		if(current.currentAnnotatedNode != null && current.currentAnnotatedNode.node == node) {
			current.currentAnnotatedNode = null;
			current = current.parent;
		} else {
			current.innerLock--;
		}
	}
	
	public boolean visit(FieldDeclaration node) {
		if(current == null || current.innerLock > 0) return false;
		current.currentAnnotatedField = new AnnotatedASTNode<FieldDeclaration>(node);
		current.currentAnnotatedNode = current.currentAnnotatedField;
		return true;
	}

	public void endVisit(FieldDeclaration node) {
		if(current == null || current.innerLock > 0) return;
		if(current.currentAnnotatedField != null && current.currentAnnotatedField.getAnnotations() != null) {
			current.annotatedFields.add(current.currentAnnotatedField);
		}
		current.currentAnnotatedField = null;
		current.currentAnnotatedNode = current.annotatedType;
	}
	
	public boolean visit(MethodDeclaration node) {
		if(current == null || current.innerLock > 0) return false;
		current.currentAnnotatedMethod = new AnnotatedASTNode<MethodDeclaration>(node);
		current.currentAnnotatedNode = current.currentAnnotatedMethod;
		return true;
	}

	public void endVisit(MethodDeclaration node) {
		if(current == null || current.innerLock > 0) return;
		if(current.currentAnnotatedMethod != null && current.currentAnnotatedMethod.getAnnotations() != null) {
			current.annotatedMethods.add(current.currentAnnotatedMethod);
		}
		current.currentAnnotatedMethod = null;
		current.currentAnnotatedNode = current.annotatedType;
	}
	
}
