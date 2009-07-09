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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
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
import org.jboss.tools.common.model.project.ext.IValueInfo;
import org.jboss.tools.common.model.project.ext.impl.ValueInfo;
import org.jboss.tools.seam.core.BeanType;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.SeamComponentMethodType;
import org.jboss.tools.seam.internal.core.BijectedAttribute;
import org.jboss.tools.seam.internal.core.DataModelSelectionAttribute;
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
	
	
	public ComponentBuilder(LoadedDeclarations ds, ASTVisitorImpl.TypeData visitor) {
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
				if(scope != null && scope.getValue() != null) {
					int q = scope.getValue().lastIndexOf('.');
					if(q >= 0) scope.setValue(scope.getValue().substring(q + 1).toLowerCase());
				}
				component.setScope(scope);
			} else if(INSTALL_ANNOTATION_TYPE.equals(type)) {
				component.setPrecedence(ValueInfo.getValueInfo(as[i].getAnnotation(), "precedence")); //$NON-NLS-1$
			}
		}
		
		if(as != null) {
			Map<BeanType, IValueInfo> types = new HashMap<BeanType, IValueInfo>();
			for (int i = 0; i < BeanType.values().length; i++) {
				Annotation a = findAnnotation(annotatedType, BeanType.values()[i].getAnnotationType());
				if(a != null) {
					ValueInfo v = new ValueInfo();
					v.setValue("true"); //$NON-NLS-1$
					v.valueStartPosition = a.getStartPosition();
					v.valueLength = a.getLength();
					types.put(BeanType.values()[i], v);
				}
			}
			if(!types.isEmpty()) {
				component.setTypes(types);
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
				factoryName.setValue(toPropertyName(m.getName().getIdentifier(), "get"));
				factoryName.valueLength = m.getName().getLength();
				factoryName.valueStartPosition = m.getName().getStartPosition();
			}
			ValueInfo scope = ValueInfo.getValueInfo(a, ISeamXmlComponentDeclaration.SCOPE);
			ValueInfo autoCreate = ValueInfo.getValueInfo(a, "autoCreate"); //$NON-NLS-1$

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

			ValueInfo _a = new ValueInfo();
			_a.setValue(FACTORY_ANNOTATION_TYPE);
			_a.valueStartPosition = a.getStartPosition();
			_a.valueLength = a.getLength();
			factory.addAttribute(FACTORY_ANNOTATION_TYPE, _a);

			ds.getFactories().add(factory);
		}
	}
	
	private String toPropertyName(String methodName, String prefix) {
		if(methodName == null) {
			return methodName;
		}
		if(methodName.startsWith(prefix) && methodName.length() > prefix.length()) {
			String root = methodName.substring(prefix.length());
			return root.substring(0, 1).toLowerCase() + root.substring(1);
		} 
		return methodName;
	}
	
	void processBijections() {
		Map<BijectedAttributeType, Annotation> as = new HashMap<BijectedAttributeType, Annotation>();
		List<BijectedAttributeType> types = new ArrayList<BijectedAttributeType>();

		for (AnnotatedASTNode<MethodDeclaration> n: annotatedMethods) {
			Annotation main = getBijectedType(n, as, types);

			if(as.isEmpty()) continue;
			boolean isDataModelSelectionType = !types.get(0).isUsingMemberName();
			
			MethodDeclaration m = n.getNode();

			BijectedAttribute att = createBijectedAttribute(types);
			
			Annotation in = as.get(BijectedAttributeType.IN);
			Annotation out = as.get(BijectedAttributeType.OUT);
			Annotation data = as.get(BijectedAttributeType.DATA_BINDER);
			addLocation(att, in, IN_ANNOTATION_TYPE);
			addLocation(att, out, OUT_ANNOTATION_TYPE);
			addLocation(att, data, DATA_MODEL_ANNOTATION_TYPE);
			
			ValueInfo name = ValueInfo.getValueInfo(main, null);
			att.setValue(name);
			if(name == null || isDataModelSelectionType
				|| name.getValue() == null || name.getValue().length() == 0) {
				name = new ValueInfo();
				name.valueStartPosition = m.getStartPosition();
				name.valueLength = m.getLength();
				name.setValue(m.getName().getIdentifier());
				if(in != null) {
					name.setValue(toPropertyName(name.getValue(), "set"));
				} else if(out != null || data != null) {
					name.setValue(toPropertyName(name.getValue(), "get"));
				}
			}
			
			att.setName(name);

			ValueInfo scope = ValueInfo.getValueInfo(main, "scope"); //$NON-NLS-1$
			if(scope != null) att.setScope(scope);
			
			IMethod im = findMethod(m);
			att.setSourceMember(im);
			att.setId(im);
		}

		for (AnnotatedASTNode<FieldDeclaration> n: annotatedFields) {
			Annotation main = getBijectedType(n, as, types);

			if(as.isEmpty()) continue;
			boolean isDataModelSelectionType = !types.get(0).isUsingMemberName();
			
			FieldDeclaration m = n.getNode();

			BijectedAttribute att = createBijectedAttribute(types);
			
			addLocation(att, as.get(BijectedAttributeType.IN), IN_ANNOTATION_TYPE);
			addLocation(att, as.get(BijectedAttributeType.OUT), OUT_ANNOTATION_TYPE);
			addLocation(att, as.get(BijectedAttributeType.DATA_BINDER), DATA_MODEL_ANNOTATION_TYPE);

			ValueInfo name = ValueInfo.getValueInfo(main, null);
			att.setValue(name);
			if(name == null || isDataModelSelectionType
					|| name.getValue() == null || name.getValue().length() == 0) {
				name = new ValueInfo();
				name.valueStartPosition = m.getStartPosition();
				name.valueLength = m.getLength();
				name.setValue(getFieldName(m));
			}
			
			att.setName(name);

			ValueInfo scope = ValueInfo.getValueInfo(main, "scope"); //$NON-NLS-1$
			if(scope != null) att.setScope(scope);
			
			IField f = findField(m);
			att.setSourceMember(f);
			att.setId(f);
		}
	}
	private Annotation getBijectedType(AnnotatedASTNode<?> n,
				Map<BijectedAttributeType, Annotation> as, List<BijectedAttributeType> types) {
		as.clear();
		types.clear();
		Annotation main = null;
		for (int i = 0; i < BijectedAttributeType.values().length; i++) {
			Annotation a = findAnnotation(n, BijectedAttributeType.values()[i].getAnnotationType());
			if(a != null) {
				as.put(BijectedAttributeType.values()[i], a);
				if(main == null) main = a;
				types.add(BijectedAttributeType.values()[i]);
			}
		}
		return main;
	}
	private BijectedAttribute createBijectedAttribute(List<BijectedAttributeType> types) {
		boolean isDataModelSelectionType = !types.get(0).isUsingMemberName();
		BijectedAttribute att = (!isDataModelSelectionType)
				? new BijectedAttribute() : new DataModelSelectionAttribute();
		component.addBijectedAttribute(att);
		att.setTypes(types.toArray(new BijectedAttributeType[0]));

		return att;
	}
	private void addLocation(BijectedAttribute att, Annotation a, String name) {
		if(a != null) {
			ValueInfo _a = new ValueInfo();
			_a.setValue(name);
			_a.valueStartPosition = a.getStartPosition();
			_a.valueLength = a.getLength();
			att.addAttribute(name, _a);
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
		
		ValueInfo name = ValueInfo.getValueInfo(role, "name"); //$NON-NLS-1$
		if(name == null) return;
		
		r.setId("" + component.getName() + ":" + name.getValue()); //$NON-NLS-1$ //$NON-NLS-2$
		r.setName(name);

		ValueInfo scope = ValueInfo.getValueInfo(role, "scope"); //$NON-NLS-1$
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
		} catch (JavaModelException e) {
			//ignore
		}
		String name = m.getName().getIdentifier();
		if(ms != null) for (int i = 0; i < ms.length; i++) {
			if(!name.equals(ms[i].getElementName())) continue;
			int s = m.getStartPosition() + m.getLength() / 2;
			try {
				ISourceRange range = ms[i].getSourceRange();
				if(range == null) {
					//no source and we cannot check position.
					return ms[i];
				}
				int b = range.getOffset();
				int e = b + range.getLength();
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
		} catch (JavaModelException e1) {
			// ignore
		}

		String name = getFieldName(f);
		if(fs != null) for (int i = 0; i < fs.length; i++) {
			if(!name.equals(fs[i].getElementName())) continue;
			int s = f.getStartPosition() + f.getLength() / 2;
			try {
				ISourceRange range = fs[i].getSourceRange();
				if(range == null) {
					//no source and we cannot check position.
					return fs[i];
				}
				int b = range.getOffset();
				int e = b + range.getLength();
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
		return true;
	}

	public boolean visit(NormalAnnotation node) {
		String typeName = ASTVisitorImpl.resolveType(type, node);
		if(ROLE_ANNOTATION_TYPE.equals(typeName)) {
			annotations.add(node);
			return true;
		}
		return false;
	}

	public boolean visit(ArrayInitializer node) {
		return true;
	}
}
