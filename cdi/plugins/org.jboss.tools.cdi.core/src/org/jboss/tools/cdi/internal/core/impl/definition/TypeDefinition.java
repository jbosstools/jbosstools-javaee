/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedMemberFeature;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.ParametedType;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class TypeDefinition extends AbstractTypeDefinition {
	boolean isAbstract;
	List<FieldDefinition> fields = new ArrayList<FieldDefinition>();
	List<MethodDefinition> methods = new ArrayList<MethodDefinition>();
	boolean hasBeanConstructor = false;

	public TypeDefinition() {
	}

	@Override
	protected void init(IType contextType, IRootDefinitionContext context, int flags) throws CoreException {
		super.init(contextType, context, flags);
		boolean allMembers = (flags & FLAG_ALL_MEMBERS) > 0;
		isAbstract = Flags.isAbstract(type.getFlags()) || type.isInterface();
		for (IAnnotationDeclaration a: annotations) {
			//provide initialization
			context.getAnnotationKind(a.getType());
		}
		Set<IProcessAnnotatedMemberFeature> extensions = context.getProject().getExtensionManager().getProcessAnnotatedMemberFeatures();
		IField[] fs = getType().getFields();
		for (int i = 0; i < fs.length; i++) {
			FieldDefinition f = newFieldDefinition();
			f.setTypeDefinition(this);
			f.setField(fs[i], context, flags);
			for (IProcessAnnotatedMemberFeature e: extensions) {
				e.processAnnotatedMember(f, context);
			}
			if(allMembers || f.isCDIAnnotated()) {
				fields.add(f);
			}
		}
		IMethod[] ms = getType().getMethods();
		boolean hasConstructor = false;
		for (int i = 0; i < ms.length; i++) {
			MethodDefinition m = newMethodDefinition();
			m.setTypeDefinition(this);
			m.setMethod(ms[i], context, flags);
			for (IProcessAnnotatedMemberFeature e: extensions) {
				e.processAnnotatedMember(m, context);
			}
			if(allMembers || m.isCDIAnnotated() || (ms[i].isConstructor() && ms[i].getNumberOfParameters()==0)) {
				methods.add(m);
			}
			if(ms[i].isConstructor()) {
				hasConstructor = true; 
				if(ms[i].getNumberOfParameters() == 0 || m.getInjectAnnotation() != null) {
					if(!isAbstract || isAnnotationPresent(CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME)) {
						setBeanConstructor(true);
					}
				}
			}
		}
		if(!hasConstructor && (!isAbstract || isAnnotationPresent(CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME))) {
			setBeanConstructor(true);
		}
	}

	protected FieldDefinition newFieldDefinition() {
		return new FieldDefinition();
	}

	protected MethodDefinition newMethodDefinition() {
		return new MethodDefinition();
	}

	public void setBeanConstructor(boolean b) {
		hasBeanConstructor = b;
	}

	public void checkConstructor() {
		for (MethodDefinition m: methods) {
			if(m.isConstructor() && m.getAnnotation(CDIConstants.INJECT_ANNOTATION_TYPE_NAME) != null) {
				setBeanConstructor(true);
			}
		}
	}

	public void annotationKindChanged(String typeName, IRootDefinitionContext context) {
		super.annotationKindChanged(typeName, context);
		for (FieldDefinition f: fields) f.annotationKindChanged(typeName, context);
		for (FieldDefinition m: fields) m.annotationKindChanged(typeName, context);
	}

	public ParametedType getSuperType() {
		return parametedType == null ? null : parametedType.getSuperType();
	}

	public List<FieldDefinition> getFields() {
		return fields;
	}

	public List<MethodDefinition> getMethods() {
		return methods;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public boolean hasBeanConstructor() {
		return hasBeanConstructor;
	}

	public AnnotationDeclaration getDecoratorAnnotation() {
		return getAnnotation(CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME);
	}

	public AnnotationDeclaration getInterceptorAnnotation() {
		return getAnnotation(CDIConstants.INTERCEPTOR_ANNOTATION_TYPE_NAME);
	}
	
	public AnnotationDeclaration getStatefulAnnotation() {
		return getAnnotation(CDIConstants.STATEFUL_ANNOTATION_TYPE_NAME);
	}

	public AnnotationDeclaration getStatelessAnnotation() {
		return getAnnotation(CDIConstants.STATELESS_ANNOTATION_TYPE_NAME);
	}

	public AnnotationDeclaration getSingletonAnnotation() {
		return getAnnotation(CDIConstants.SINGLETON_ANNOTATION_TYPE_NAME);
	}
}