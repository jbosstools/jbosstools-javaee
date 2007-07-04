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
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

/**
 * This object collects changes in target that should be fired to listeners.
 * 
 * @author Viacheslav Kabanovich
 */
public class ASTVisitorImpl extends ASTVisitor {
	static String SEAM_ANNOTATION_TYPE_PREFIX = "org.jboss.seam.annotations.";
	static String NAME_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Name";
	static String SCOPE_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Scope";
	
	static int UNDEFINED_CONTEXT = -1;
	static int TYPE_CONTEXT = 0;
	static int FIELD_CONTEXT = 1;
	static int METHOD_CONTEXT = 2;

	IType type;
	String name = null;
	String scope = null;
	
	AnnotatedASTNode annotatedType = null;
	Set<Object> annotatedFields = new HashSet<Object>();
	Set<Object> annotatedMethods = new HashSet<Object>();

	AnnotatedASTNode currentAnnotatedNode = null;
	int context = UNDEFINED_CONTEXT;
	
	public boolean visit(SingleMemberAnnotation node) {
		String type = resolveType(node);
		if(isSeamAnnotationType(type) && currentAnnotatedNode != null) {
			currentAnnotatedNode.addAnnotation(new ResolvedAnnotation(type, node));
		}
		return false;
	}

	public boolean visit(NormalAnnotation node) {
		String type = resolveType(node);
		if(isSeamAnnotationType(type) && currentAnnotatedNode != null) {
			currentAnnotatedNode.addAnnotation(new ResolvedAnnotation(type, node));
		}
		return false;
	}

	public boolean visit(MarkerAnnotation node) {
		String type = resolveType(node);
		if(isSeamAnnotationType(type) && currentAnnotatedNode != null) {
			currentAnnotatedNode.addAnnotation(new ResolvedAnnotation(type, node));
		}
		return true;
	}

	boolean checkAnnotationType(Annotation node, String annotationType) {
		String n = resolveType(node);
		return n != null && n.equals(annotationType);
	}
	
	String resolveType(Annotation node) {
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

	boolean isSeamAnnotationType(String n) {
		return n != null && n.startsWith(SEAM_ANNOTATION_TYPE_PREFIX);
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


	public boolean visit(TypeDeclaration node) {
		annotatedType = new AnnotatedASTNode(node);
		currentAnnotatedNode = annotatedType; 
		return true;
	}
	
	public void endVisit(TypeDeclaration node) {
		currentAnnotatedNode = null;
		process();
	}
	
	public boolean visit(FieldDeclaration node) {
		currentAnnotatedNode = new AnnotatedASTNode(node);
		return true;
	}

	public void endVisit(FieldDeclaration node) {
		List fragments = node.fragments();
		for (int i = 0; i < fragments.size(); i++) {
			VariableDeclaration vd = (VariableDeclaration)fragments.get(i);
			String name = vd.getName().getIdentifier();
			System.out.println("-->" + name);
		}
		if(currentAnnotatedNode != null && currentAnnotatedNode.getAnnotations() != null) {
			annotatedFields.add(currentAnnotatedNode);
		}
		currentAnnotatedNode = null;
	}
	
	public boolean visit(MethodDeclaration node) {
		currentAnnotatedNode = new AnnotatedASTNode(node);
		return true;
	}

	public void endVisit(MethodDeclaration node) {
		if(currentAnnotatedNode != null && currentAnnotatedNode.getAnnotations() != null) {
			annotatedMethods.add(currentAnnotatedNode);
		}
		currentAnnotatedNode = null;
	}
	
	void process() {
		if(annotatedType == null) return;
		ResolvedAnnotation[] as = annotatedType.getAnnotations();
		for (int i = 0; i < as.length; i++) {
			String type = as[i].getType();
			if(NAME_ANNOTATION_TYPE.equals(type)) {
				name = getValue(as[i].getAnnotation());
			} else if(SCOPE_ANNOTATION_TYPE.equals(type)) {
				scope = getValue(as[i].getAnnotation());
				if(scope != null) {
					int q = scope.lastIndexOf('.');
					if(q >= 0) scope = scope.substring(q + 1).toLowerCase();
				}
			}
			//TODO
		}
		//TODO
	}
	
	String getValue(Annotation node) {
		if(node instanceof SingleMemberAnnotation) {
			return getValue((SingleMemberAnnotation)node);
		} else if(node instanceof NormalAnnotation) {
			return getValue((NormalAnnotation)node);
		} else {
			return null;
		}		
	}
	
	String getValue(SingleMemberAnnotation node) {
		return checkExpression(node.getValue());
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
}
