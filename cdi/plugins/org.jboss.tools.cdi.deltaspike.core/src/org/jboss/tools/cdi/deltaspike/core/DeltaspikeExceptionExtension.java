/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.deltaspike.core;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedMemberFeature;
import org.jboss.tools.cdi.core.extension.feature.IValidatorFeature;
import org.jboss.tools.cdi.deltaspike.core.validation.DeltaspikeValidationMessages;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.BeanMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.ParameterDefinition;
import org.jboss.tools.cdi.internal.core.validation.CDICoreValidator;
import org.jboss.tools.common.java.IAnnotated;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IJavaAnnotation;
import org.jboss.tools.common.java.IJavaSourceReference;
import org.jboss.tools.common.java.impl.AnnotationLiteral;
import org.jboss.tools.common.preferences.SeverityPreferences;

/**
 * Runtime
 * org.apache.deltaspike.core.impl.exception.control.extension.ExceptionControlExtension
 * 
 * @author Viacheslav Kabanovich
 */
public class DeltaspikeExceptionExtension implements ICDIExtension, IProcessAnnotatedMemberFeature, IValidatorFeature, DeltaspikeConstants {

	@Override
	public void processAnnotatedMember(BeanMemberDefinition memberDefinition,
			IRootDefinitionContext context) {
		if(memberDefinition instanceof ParameterDefinition) {
			ParameterDefinition p = (ParameterDefinition)memberDefinition;
			if(isHandler(p)) {
				IJavaAnnotation ja = createFakeObservesAnnotation(memberDefinition, context);
				if(ja != null) {
					p.addAnnotation(ja, context);
				}
			}
		}		
	}

	private boolean isHandler(IAnnotated p) {
		 return p.isAnnotationPresent(HANDLES_ANNOTATION_TYPE_NAME) || p.isAnnotationPresent(BEFORE_HANDLES_ANNOTATION_TYPE_NAME);
	}

	@Override
	public void validateResource(IFile file, CDICoreValidator validator) {
		ICDIProject cdi = CDICorePlugin.getCDIProject(file.getProject(), true);
		Set<IBean> beans = cdi.getBeans(file.getFullPath());
		for (IBean b: beans) {
			if(b instanceof IClassBean) {
				IClassBean cb = (IClassBean)b;
				boolean isExceptionHandler = cb.isAnnotationPresent(EXCEPTION_HANDLER_ANNOTATION_TYPE_NAME);
				Set<IBeanMethod> ms = cb.getAllMethods();
				for (IBeanMethod m: ms) {
					for (IParameter p: m.getParameters()) {
						if(isHandler(p)) {
							if(!isExceptionHandler) {
								IJavaSourceReference s = p.getAnnotation(HANDLES_ANNOTATION_TYPE_NAME);
								if(s == null) s = p.getAnnotation(BEFORE_HANDLES_ANNOTATION_TYPE_NAME);
								if(s == null) s = p;
								validator.addError(DeltaspikeValidationMessages.NOT_A_HANDLER_BEAN,
										DeltaspikeSeverityPreferences.NOT_A_HANDLER_BEAN,  
										new String[]{}, 
										s, file);
							}
							IType t = p.getType().getType();
							if(t != null && !EXCEPTION_EVENT_TYPE_NAME.equals(t.getFullyQualifiedName())) {
								validator.addError(DeltaspikeValidationMessages.INVALID_HANDLER_TYPE,
										DeltaspikeSeverityPreferences.INVALID_HANDLER_TYPE,  
										new String[]{}, 
										p, file);
							}
						}
					}
				}
			}
		}
		
	}

	/**
	 * Returns new annotation object with Produces type if definition is annotated with Unwraps
	 * and Produces type is available in class path. Returns null otherwise.
	 * 
	 * @param def
	 * @param context
	 * @return new annotation object with Produces type or null
	 */
	IJavaAnnotation createFakeObservesAnnotation(AbstractMemberDefinition def, IRootDefinitionContext context) {
		IJavaAnnotation result = null;
		IAnnotationDeclaration a = def.getAnnotation(HANDLES_ANNOTATION_TYPE_NAME);
		if(a == null) a = def.getAnnotation(BEFORE_HANDLES_ANNOTATION_TYPE_NAME);
		if(a != null) {
			IType producesAnnotation = context.getProject().getType(CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME);
			if (producesAnnotation != null) {
				AnnotationLiteral literal  = new AnnotationLiteral(def.getResource(), a.getStartPosition(), a.getLength(), null, IMemberValuePair.K_UNKNOWN, producesAnnotation);
				literal.setParentElement((IJavaElement)def.getMember());
				result = literal;
			}
		}
		return result;
	}

	@Override
	public SeverityPreferences getSeverityPreferences() {
		return DeltaspikeSeverityPreferences.getInstance();
	}
}
