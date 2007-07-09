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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.internal.core.SeamAnnotatedFactory;
import org.jboss.tools.seam.internal.core.SeamJavaComponentDeclaration;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;

/**
 * Builds component using results of ASTVisitorImpl
 * 
 * @author Viacheslav Kabanovich
 */
public class ComponentBuilder implements SeamAnnotations {
	LoadedDeclarations ds = null;

	AnnotatedASTNode annotatedType = null;
	Set<AnnotatedASTNode> annotatedFields = new HashSet<AnnotatedASTNode>();
	Set<AnnotatedASTNode> annotatedMethods = new HashSet<AnnotatedASTNode>();
	
	SeamJavaComponentDeclaration component = new SeamJavaComponentDeclaration();
	
	
	public ComponentBuilder(LoadedDeclarations ds, ASTVisitorImpl visitor) {
		annotatedType = visitor.annotatedType;
		annotatedFields = visitor.annotatedFields;
		annotatedMethods = visitor.annotatedMethods;

		String n = visitor.type.getElementName();
		n = JavaScanner.getResolvedType(visitor.type, n);

		ds.getComponents().add(component);
		component.setType(visitor.type);
		component.setId(visitor.type);
		component.setClassName(n);
		
		process();
	}
	
	void process() {
		if(annotatedType == null) return;
		ResolvedAnnotation[] as = annotatedType.getAnnotations();
		for (int i = 0; i < as.length; i++) {
			String type = as[i].getType();
			if(NAME_ANNOTATION_TYPE.equals(type)) {
				component.setName(ValueInfo.getValueInfo(as[i].getAnnotation(), null));
			} else if(SCOPE_ANNOTATION_TYPE.equals(type)) {
				ValueInfo scope = ValueInfo.getValueInfo(as[i].getAnnotation(), null);
				if(scope != null && scope.value != null) {
					int q = scope.value.lastIndexOf('.');
					if(q >= 0) scope.value = scope.value.substring(q + 1).toLowerCase();
				}
				component.setScope(scope);
			} else if(INSTALL_ANNOTATION_TYPE.equals(type)) {
				component.setPrecedence(ValueInfo.getValueInfo(as[i].getAnnotation(), "precedence"));
			} else if(STATEFUL_ANNOTATION_TYPE.equals(type)) {
				ValueInfo stateful = new ValueInfo();
				stateful.value = "true";
				stateful.valueStartPosition = as[i].getAnnotation().getStartPosition();
				stateful.valueLength = as[i].getAnnotation().getLength();
				component.setStateful(true);
			}
			//TODO entity
		}
		
		processFactories();
		processBijections();
		processComponentMethods();
		
		//TODO
	}
	
	void processFactories() {
		for (AnnotatedASTNode n: annotatedMethods) {
			Annotation a = findAnnotation(n, FACTORY_ANNOTATION_TYPE);
			if(a == null) continue;
			MethodDeclaration m = (MethodDeclaration)n.getNode();
			ValueInfo factoryName = ValueInfo.getValueInfo(a, null);
			if(factoryName == null) {
				factoryName = new ValueInfo();
				factoryName.value = m.getName().getIdentifier();
				factoryName.valueLength = m.getName().getLength();
				factoryName.valueStartPosition = m.getName().getStartPosition();
			}
			System.out.println("");
			ValueInfo scope = ValueInfo.getValueInfo(a, ISeamXmlComponentDeclaration.SCOPE);
			ValueInfo autoCreate = ValueInfo.getValueInfo(a, "autoCreate");

			SeamAnnotatedFactory factory = new SeamAnnotatedFactory();
			factory.setMethod(findMethod(m));
			factory.setName(factoryName);
			if(autoCreate != null) factory.setAutoCreate(true);
			if(scope != null) {
				factory.setScope(scope);
			}
			ds.getFactories().add(factory);
		}
	}
	
	void processBijections() {
		//TODO
	}
	
	void processComponentMethods() {
		//TODO
	}
	
	void processRoles() {
		//TODO
	}

	private Annotation findAnnotation(AnnotatedASTNode n, String type) {
		ResolvedAnnotation[] as = n.getAnnotations();
		for (int i = 0; i < as.length; i++) {
			if(type.equals(as[i].getType())) return as[i].getAnnotation();
		}
		return null;
	}
	
	private IMethod findMethod(MethodDeclaration m) {
//		IType type = (IType)component.getSourceMember();
		IJavaElement e = m.resolveBinding().getJavaElement();
		if(e instanceof IMethod) return (IMethod)e;
		return null;
	}

}
