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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.SeamComponentMethodType;
import org.jboss.tools.seam.internal.core.BijectedAttribute;
import org.jboss.tools.seam.internal.core.Role;
import org.jboss.tools.seam.internal.core.SeamAnnotatedFactory;
import org.jboss.tools.seam.internal.core.SeamComponentMethod;
import org.jboss.tools.seam.internal.core.SeamJavaComponentDeclaration;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;

/**
 * Builds component using results of ASTVisitorImpl
 * 
 * @author Viacheslav Kabanovich
 */
public class ComponentBuilder implements SeamAnnotations {
	LoadedDeclarations ds = null;

	AnnotatedASTNode<?> annotatedType = null;
	Set<AnnotatedASTNode<FieldDeclaration>> annotatedFields = null;
	Set<AnnotatedASTNode<MethodDeclaration>> annotatedMethods = null;
	
	SeamJavaComponentDeclaration component = new SeamJavaComponentDeclaration();
	
	
	public ComponentBuilder(LoadedDeclarations ds, ASTVisitorImpl visitor) {
		this.ds = ds;
		annotatedType = visitor.annotatedType;
		annotatedFields = visitor.annotatedFields;
		annotatedMethods = visitor.annotatedMethods;

		String n = visitor.type.getElementName();
		n = JavaScanner.getResolvedType(visitor.type, n);

		ds.getComponents().add(component);
		component.setType(visitor.type);
		component.setId(visitor.type);
		component.setClassName(n);
		
	}
	
	void process() {
		if(annotatedType == null) return;
		ResolvedAnnotation[] as = annotatedType.getAnnotations();
		if(as != null) for (int i = 0; i < as.length; i++) {
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
				component.setStateful(stateful);
			} else if(ENTITY_ANNOTATION_TYPE.equals(type)) {
				ValueInfo entity = new ValueInfo();
				entity.value = "true";
				entity.valueStartPosition = as[i].getAnnotation().getStartPosition();
				entity.valueLength = as[i].getAnnotation().getLength();
				component.setEntity(entity);
			}
		}
		
		processFactories();
		processBijections();
		processComponentMethods();
		processRoles();
		
	}
	
	void processFactories() {
		for (AnnotatedASTNode<MethodDeclaration> n: annotatedMethods) {
			Annotation a = findAnnotation(n, FACTORY_ANNOTATION_TYPE);
			if(a == null) continue;
			MethodDeclaration m = n.getNode();
			ValueInfo factoryName = ValueInfo.getValueInfo(a, null);
			if(factoryName == null) {
				factoryName = new ValueInfo();
				factoryName.value = m.getName().getIdentifier();
				factoryName.valueLength = m.getName().getLength();
				factoryName.valueStartPosition = m.getName().getStartPosition();
			}
			ValueInfo scope = ValueInfo.getValueInfo(a, ISeamXmlComponentDeclaration.SCOPE);
			ValueInfo autoCreate = ValueInfo.getValueInfo(a, "autoCreate");

			SeamAnnotatedFactory factory = new SeamAnnotatedFactory();
			factory.setParentDeclaration(component);
			IMethod im = findMethod(m);
			factory.setSourceMember(im);
			factory.setId(im);
			factory.setSourcePath(component.getSourcePath());
			factory.setName(factoryName);
			if(autoCreate != null) factory.setAutoCreate(true);
			if(scope != null) {
				factory.setScope(scope);
			}
			ds.getFactories().add(factory);
		}
	}
	
	void processBijections() {
		for (AnnotatedASTNode<MethodDeclaration> n: annotatedMethods) {
			Map<BijectedAttributeType, Annotation> as = new HashMap<BijectedAttributeType, Annotation>();
			List<BijectedAttributeType> types = new ArrayList<BijectedAttributeType>();
			Annotation main = null;
			for (int i = 0; i < BijectedAttributeType.values().length; i++) {
				Annotation a = findAnnotation(n, BijectedAttributeType.values()[i].getAnnotationType());
				if(a != null) {
					as.put(BijectedAttributeType.values()[i], a);
					if(main == null) main = a;
					types.add(BijectedAttributeType.values()[i]);
				}
			}
			if(as.size() == 0) continue;
			
			MethodDeclaration m = n.getNode();

			BijectedAttribute att = new BijectedAttribute();
			component.addBijectedAttribute(att);

			att.setTypes(types.toArray(new BijectedAttributeType[0]));
			
			ValueInfo name = ValueInfo.getValueInfo(main, null);
			if(name == null && types.size() > 0 && types.get(0).isUsingMemberName()) {
				name = new ValueInfo();
				name.value = m.getName().getIdentifier();
			}
			
			att.setName(name);

			ValueInfo scope = ValueInfo.getValueInfo(main, "scope");
			if(scope != null) att.setScope(scope);
			
			IMethod im = findMethod(m);
			att.setSourceMember(im);
			att.setId(im);
		}

		for (AnnotatedASTNode<FieldDeclaration> n: annotatedFields) {
			Map<BijectedAttributeType, Annotation> as = new HashMap<BijectedAttributeType, Annotation>();
			List<BijectedAttributeType> types = new ArrayList<BijectedAttributeType>();
			Annotation main = null;
			for (int i = 0; i < BijectedAttributeType.values().length; i++) {
				Annotation a = findAnnotation(n, BijectedAttributeType.values()[i].getAnnotationType());
				if(a != null) {
					as.put(BijectedAttributeType.values()[i], a);
					if(main == null) main = a;
					types.add(BijectedAttributeType.values()[i]);
				}
			}
			if(as.size() == 0) continue;
			
			FieldDeclaration m = n.getNode();

			BijectedAttribute att = new BijectedAttribute();
			component.addBijectedAttribute(att);

			att.setTypes(types.toArray(new BijectedAttributeType[0]));
			
			ValueInfo name = ValueInfo.getValueInfo(main, null);
			if(name == null) {
				name = new ValueInfo();
				name.value = getFieldName(m);
			}
			
			att.setName(name);

			ValueInfo scope = ValueInfo.getValueInfo(main, "scope");
			if(scope != null) att.setScope(scope);
			
			IField f = findField(m);
			att.setSourceMember(f);
			att.setId(f);
		}
	}
	
	void processComponentMethods() {
		for (AnnotatedASTNode<MethodDeclaration> n: annotatedMethods) {
			SeamComponentMethod cm = null;
			for (int i = 0; i < SeamComponentMethodType.values().length; i++) {
				SeamComponentMethodType type = SeamComponentMethodType.values()[i];
				Annotation a = findAnnotation(n, type.getAnnotationType());
				if(a == null) continue;
				if(cm == null) {
					cm = new SeamComponentMethod();
					component.addMethod(cm);
					MethodDeclaration m = n.getNode();
					component.addMethod(cm);
					IMethod im = findMethod(m);
					cm.setSourceMember(im);
					cm.setId(im);
				}
				cm.getTypes().add(type);
			}
		}
	}
	
	void processRoles() {
		Annotation roles = findAnnotation(annotatedType, ROLES_ANNOTATION_TYPE);
		if(roles != null) {
			RolesVisitor visitor = new RolesVisitor((IType)component.getSourceMember());
			roles.accept(visitor);
			List<Annotation> rs = visitor.annotations;
			if(rs != null) for (Annotation role : rs) {
				createRole(role);
			}
		}
		ResolvedAnnotation[] as = annotatedType.getAnnotations();
		if(as != null) for (int i = 0; i < as.length; i++) {
			if(ROLE_ANNOTATION_TYPE.equals(as[i].getType())) {
				createRole(as[i].getAnnotation());
			}
		}		
	}
	
	void createRole(Annotation role) {
		Role r = new Role();
		r.setSourcePath(component.getSourcePath());
		r.setSourceMember(component.getSourceMember());
		
		ValueInfo name = ValueInfo.getValueInfo(role, "name");
		if(name == null) return;
		
		r.setId("" + component.getName() + ":" + name.getValue());
		r.setName(name);

		ValueInfo scope = ValueInfo.getValueInfo(role, "scope");
		if(scope != null) r.setScope(scope);
		
		component.addRole(r);
	}

	private Annotation findAnnotation(AnnotatedASTNode<?> n, String type) {
		ResolvedAnnotation[] as = n.getAnnotations();
		if(as == null) return null;
		for (int i = 0; i < as.length; i++) {
			if(type.equals(as[i].getType())) return as[i].getAnnotation();
		}
		return null;
	}
	
	private IMethod findMethod(MethodDeclaration m) {
		if(m == null || m.getName() == null) return null;
		IType type = (IType)component.getSourceMember();
		IMethod[] ms = null;
		try {
			ms = type.getMethods();
		} catch (Exception e) {
			//ignore
		}
		String name = m.getName().getIdentifier();
		if(ms != null) for (int i = 0; i < ms.length; i++) {
			if(!name.equals(ms[i].getElementName())) continue;
			int s = m.getStartPosition() + m.getLength() / 2;
			try {
				int b = ms[i].getSourceRange().getOffset();
				int e = b + ms[i].getSourceRange().getLength();
				if(s >= b && s <= e) return ms[i];
			} catch (JavaModelException e) {
				return ms[i];
			}
		}
		return null;
	}

	private IField findField(FieldDeclaration f) {
		if(f == null || getFieldName(f) == null) return null;
		IType type = (IType)component.getSourceMember();
		IField[] fs = null;
		try {
			fs = type.getFields();
		} catch (Exception e) {
			//ignore
		}
		String name = getFieldName(f);
		if(fs != null) for (int i = 0; i < fs.length; i++) {
			if(!name.equals(fs[i].getElementName())) continue;
			int s = f.getStartPosition() + f.getLength() / 2;
			try {
				int b = fs[i].getSourceRange().getOffset();
				int e = b + fs[i].getSourceRange().getLength();
				if(s >= b && s <= e) return fs[i];
			} catch (JavaModelException e) {
				return fs[i];
			}
		}
		return null;
	}
	
	/**
	 * Returns name of first field
	 * @param node
	 * @return
	 */
	String getFieldName(FieldDeclaration node) {
		List<?> fragments = node.fragments();
		for (int i = 0; i < fragments.size(); i++) {
			VariableDeclaration vd = (VariableDeclaration)fragments.get(i);
			String name = vd.getName().getIdentifier();
			return name;
		}
		return null;
	}

}

class RolesVisitor extends ASTVisitor implements SeamAnnotations {
	boolean arrayFound = false;
	IType type;
	List<Annotation> annotations = new ArrayList<Annotation>();
	
	public RolesVisitor(IType type) {
		this.type = type;
	}

	public boolean visit(SingleMemberAnnotation node) {
		if(arrayFound) {
			String typeName = ASTVisitorImpl.resolveType(type, node);
			if(!ROLE_ANNOTATION_TYPE.equals(typeName)) return false;
			annotations.add(node);
			return false;
		}
		return true;
	}

	public boolean visit(NormalAnnotation node) {
		if(arrayFound) {
			String typeName = ASTVisitorImpl.resolveType(type, node);
			if(!ROLE_ANNOTATION_TYPE.equals(typeName)) return false;
			annotations.add(node);
		}
		return false;
	}

	public boolean visit(ArrayInitializer node) {
		arrayFound = true;
		return true;
	}

}
