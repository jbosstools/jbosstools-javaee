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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;
import org.jboss.tools.common.java.IAnnotationDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class AnnotationDefinition extends AbstractTypeDefinition {
	public static final int NON_RELEVANT = 0;
	public static final int BASIC = 1; //has Inherited, Target, Retention
	public static final int CDI = 2; //has Model, Named, Typed, *Scoped, New
	public static final int QUALIFIER = 4; //has Qualifier
	public static final int STEREOTYPE = 8;	//has Stereotype
	public static final int INTERCEPTOR_BINDING = 16; //has InterceptorBinding
	public static final int SCOPE = 32; //has Scope or NormalScope

	public static final int EXTENDED = 1024;
	//TODO add other definition kinds of interest

	protected int kind = NON_RELEVANT;
	protected Object extendedKind = null;

	List<AnnotationMemberDefinition> methods = new ArrayList<AnnotationMemberDefinition>();

	public AnnotationDefinition() {}

	public void setKind(int kind) {
		this.kind = kind;
	}

	public void setExtendedKind(Object s) {
		extendedKind = s;
		kind |= EXTENDED;
	}

	public int getKind() {
		return kind;
	}

	public boolean hasExtendedKind(Object kind) {
		return extendedKind != null && extendedKind.equals(kind);
	}

	public List<AnnotationMemberDefinition> getMethods() {
		return methods;
	}

	@Override
	protected void init(IType contextType, IRootDefinitionContext context, int flags) throws CoreException {
		super.init(contextType, context, flags);
		if(annotations.isEmpty()) {
			//TODO check super ?
			return;
		}
		revalidateKind(context);
	}

	public void revalidateKind(IRootDefinitionContext context) {
		boolean hasMembers = (kind & QUALIFIER) > 0 || (kind & INTERCEPTOR_BINDING) > 0 || kind == EXTENDED;
		kind = NON_RELEVANT;
		
		Map<String, AnnotationDeclaration> ds = new HashMap<String, AnnotationDeclaration>();
		
		for (IAnnotationDeclaration a: annotations) {
			if(a instanceof AnnotationDeclaration) {
				AnnotationDeclaration aa = (AnnotationDeclaration)a;
				String typeName = aa.getTypeName();
				ds.put(typeName, aa);
			}
		}

		if(ds.containsKey(CDIConstants.SCOPE_ANNOTATION_TYPE_NAME) 
				|| ds.containsKey(CDIConstants.NORMAL_SCOPE_ANNOTATION_TYPE_NAME)) {
			kind = SCOPE;
		}
		if(ds.containsKey(CDIConstants.STEREOTYPE_ANNOTATION_TYPE_NAME)) {
			kind |= STEREOTYPE;
		}
		if(ds.containsKey(CDIConstants.QUALIFIER_ANNOTATION_TYPE_NAME)) {
			kind |= QUALIFIER;
		}
		if(ds.containsKey(CDIConstants.INTERCEPTOR_BINDING_ANNOTATION_TYPE_NAME)) {
			kind |= INTERCEPTOR_BINDING;
		}
		if(kind == NON_RELEVANT) {
			Set<IDefinitionContextExtension> es = context.getExtensions();
			for (IDefinitionContextExtension e: es) {
				e.computeAnnotationKind(this);
				if(kind == EXTENDED) break;
			}
		}
		if(kind == NON_RELEVANT) {
			if(AnnotationHelper.BASIC_ANNOTATION_TYPES.contains(qualifiedName)) {
				kind = AnnotationDefinition.BASIC;
			} else if(AnnotationHelper.CDI_ANNOTATION_TYPES.contains(qualifiedName)) {
				kind = AnnotationDefinition.CDI;
			}
		}

		boolean newHasMembers = (kind & QUALIFIER) > 0 || (kind & INTERCEPTOR_BINDING) > 0 || kind == EXTENDED;
		if(newHasMembers != hasMembers) {
			methods.clear();
			try {
				initMemberDefinitions(type, context);
			} catch (CoreException e) {
				CDICorePlugin.getDefault().logError(e);
			}
		}
	}

	void initMemberDefinitions(IType contextType, IRootDefinitionContext context) throws CoreException {
		IMethod[] ms = getType().getMethods();
		for (int i = 0; i < ms.length; i++) {
			AnnotationMemberDefinition m = new AnnotationMemberDefinition();
			m.setAnnotationDefinition(this);
			m.setMethod(ms[i], context, 0);
			if(m.isCDIAnnotated()) {
				methods.add(m);
			}
		}
	}

	public AnnotationDeclaration getInheritedAnnotation() {
		return getAnnotation(CDIConstants.INHERITED_ANNOTATION_TYPE_NAME);
	}

}
