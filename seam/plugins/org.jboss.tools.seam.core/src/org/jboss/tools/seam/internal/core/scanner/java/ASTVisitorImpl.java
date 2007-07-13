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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.jboss.tools.seam.internal.core.scanner.Util;

/**
 * Processes AST tree to find annotated type, fields and methods.
 * 
 * @author Viacheslav Kabanovich
 */
public class ASTVisitorImpl extends ASTVisitor implements SeamAnnotations {
	
	IType type;
	
	AnnotatedASTNode<TypeDeclaration> annotatedType = null;
	Set<AnnotatedASTNode<FieldDeclaration>> annotatedFields = new HashSet<AnnotatedASTNode<FieldDeclaration>>();
	Set<AnnotatedASTNode<MethodDeclaration>> annotatedMethods = new HashSet<AnnotatedASTNode<MethodDeclaration>>();

	AnnotatedASTNode<?> currentAnnotatedNode = null;
	AnnotatedASTNode<FieldDeclaration> currentAnnotatedField = null;
	AnnotatedASTNode<MethodDeclaration> currentAnnotatedMethod = null;
	
	public boolean hasSeamComponent() {
		if(annotatedFields.size() > 0 || annotatedMethods.size() > 0) return true;
		if(annotatedType != null && annotatedType.getAnnotations() != null) return true;		
		return false;
	}
	
	public boolean visit(SingleMemberAnnotation node) {
		String type = resolveType(node);
		if(Util.isSeamAnnotationType(type) && currentAnnotatedNode != null) {
			currentAnnotatedNode.addAnnotation(new ResolvedAnnotation(type, node));
		}
		return false;
	}

	public boolean visit(NormalAnnotation node) {
		String type = resolveType(node);
		if(Util.isSeamAnnotationType(type) && currentAnnotatedNode != null) {
			currentAnnotatedNode.addAnnotation(new ResolvedAnnotation(type, node));
		}
		return false;
	}

	public boolean visit(MarkerAnnotation node) {
		String type = resolveType(node);
		if(Util.isSeamAnnotationType(type) && currentAnnotatedNode != null) {
			currentAnnotatedNode.addAnnotation(new ResolvedAnnotation(type, node));
		}
		return true;
	}

	boolean checkAnnotationType(Annotation node, String annotationType) {
		String n = resolveType(node);
		return n != null && n.equals(annotationType);
	}
	
	String resolveType(Annotation node) {
		return resolveType(type, node);
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
		annotatedType = new AnnotatedASTNode<TypeDeclaration>(node);
		currentAnnotatedNode = annotatedType; 
		return true;
	}
	
	public void endVisit(TypeDeclaration node) {
		currentAnnotatedNode = null;
	}
	
	public boolean visit(FieldDeclaration node) {
		currentAnnotatedField = new AnnotatedASTNode<FieldDeclaration>(node);
		currentAnnotatedNode = currentAnnotatedField;
		return true;
	}

	public void endVisit(FieldDeclaration node) {
		List<?> fragments = node.fragments();
		for (int i = 0; i < fragments.size(); i++) {
			VariableDeclaration vd = (VariableDeclaration)fragments.get(i);
			String name = vd.getName().getIdentifier();
			System.out.println("-->" + name);
		}
		if(currentAnnotatedField != null && currentAnnotatedField.getAnnotations() != null) {
			annotatedFields.add(currentAnnotatedField);
		}
		currentAnnotatedField = null;
		currentAnnotatedNode = annotatedType;
	}
	
	public boolean visit(MethodDeclaration node) {
		currentAnnotatedMethod = new AnnotatedASTNode<MethodDeclaration>(node);
		currentAnnotatedNode = currentAnnotatedMethod;
		return true;
	}

	public void endVisit(MethodDeclaration node) {
		if(currentAnnotatedMethod != null && currentAnnotatedMethod.getAnnotations() != null) {
			annotatedMethods.add(currentAnnotatedMethod);
		}
		currentAnnotatedMethod = null;
		currentAnnotatedNode = annotatedType;
	}
	
}
