/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.cdi.internal.core.validation;

import java.util.Collection;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.CDIVersion;
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.core.preferences.CDIPreferences;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IAnnotationType;

/**
 * CDI annotation validator.
 * 
 * @author Alexey Kazakov
 */
public class AnnotationValidationDelegate extends CDICoreValidationDelegate {

	public static final String ELEMENT_TYPE_TYPE_NAME = "java.lang.annotation.ElementType";
	public static final String TARGET_METHOD = "METHOD";
	public static final String TARGET_FIELD = "FIELD";
	public static final String TARGET_PARAMETER = "PARAMETER";
	public static final String TARGET_TYPE = "TYPE";

	static final String[] TMF = {TARGET_METHOD, TARGET_FIELD, TARGET_TYPE};
	static final String[] MF = {TARGET_METHOD, TARGET_FIELD};
	static final String[][] STEREOTYPE_GENERAL_TARGET_VARAINTS = {TMF, MF, {TARGET_TYPE}, {TARGET_METHOD}, {TARGET_FIELD}};
	static final String[][] QUALIFIER_GENERAL_TARGET_VARIANTS = {{TARGET_METHOD, TARGET_FIELD, TARGET_PARAMETER, TARGET_TYPE}, {TARGET_FIELD, TARGET_PARAMETER}};
	static final String[][] SCOPE_GENERAL_TARGET_VARIANTS = {TMF};
	static final String[][] STEREOTYPE_TMF_VARIANTS = {TMF};
	static final String[][] STEREOTYPE_MF_VARIANTS = {MF};
	static final String[][] STEREOTYPE_M_VARIANTS = {{TARGET_METHOD}};
	static final String[][] STEREOTYPE_F_VARIANTS = {{TARGET_FIELD}};

	static final String[][] TYPE_VARIANTS = {{TARGET_TYPE}};
	static final String[][] TYPE__METHOD_VARIANTS = {{TARGET_TYPE, TARGET_METHOD}};

	public AnnotationValidationDelegate(CDICoreValidator validator) {
		super(validator);
	}

	public void validateStereotypeAnnotationTypeAnnotations(IStereotype stereotype, IResource resource) throws JavaModelException {
		/*
		 * Stereotype annotation type should be annotated with @Target with correct targets [JSR-299 §2.7.1]
		 * Stereotype annotation type should be annotated with @Retention(RUNTIME)
		 */
		if(stereotype.getCDIProject().getVersion() == CDIVersion.CDI_1_0) {
			validateTargetAnnotation(stereotype, STEREOTYPE_GENERAL_TARGET_VARAINTS, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE[validator.getVersionIndex(stereotype)], resource, CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE_ID);
		}

		/*
		 * Stereotype annotation type should be annotated with @Retention(RUNTIME)
		 */
		validateRetentionAnnotation(stereotype, CDIValidationMessages.MISSING_RETENTION_ANNOTATION_IN_STEREOTYPE_TYPE[validator.getVersionIndex(stereotype)], resource, CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_STEREOTYPE_TYPE_ID);

		IAnnotationDeclaration target = stereotype.getAnnotationDeclaration(CDIConstants.TARGET_ANNOTATION_TYPE_NAME);
		if(target!=null) {
			/*
			 * 2.7.1.5. Stereotypes with additional stereotypes
			 * - Stereotypes declared @Target(TYPE) may not be applied to stereotypes declared @Target({TYPE, METHOD, FIELD}),
			 *   @Target(METHOD), @Target(FIELD) or @Target({METHOD, FIELD}).
			 */
			for (IStereotypeDeclaration stereotypeDeclaration : stereotype.getStereotypeDeclarations()) {
				IStereotype superStereotype = stereotypeDeclaration.getStereotype();
				if(superStereotype!=null) {
					Boolean result = CDIUtil.checkTargetAnnotation(superStereotype, TYPE_VARIANTS);
					if(result!=null && result) {
						result = CDIUtil.checkTargetAnnotation(target, STEREOTYPE_TMF_VARIANTS);
						String stName = stereotype.getSourceType().getElementName();
						String superStName = superStereotype.getSourceType().getElementName();
						if(result) {
							validator.addProblem(CDIValidationMessages.ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_TMF[validator.getVersionIndex(stereotype)], CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, new String[]{superStName, stName}, stereotypeDeclaration, resource);
							continue;
						}
						result = CDIUtil.checkTargetAnnotation(target, STEREOTYPE_M_VARIANTS);
						if(result) {
							validator.addProblem(CDIValidationMessages.ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_M[validator.getVersionIndex(stereotype)], CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, new String[]{superStName, stName}, stereotypeDeclaration, resource);
							continue;
						}
						result = CDIUtil.checkTargetAnnotation(target, STEREOTYPE_F_VARIANTS);
						if(result) {
							validator.addProblem(CDIValidationMessages.ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_F[validator.getVersionIndex(stereotype)], CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, new String[]{superStName, stName}, stereotypeDeclaration, resource);
							continue;
						}
						result = CDIUtil.checkTargetAnnotation(target, STEREOTYPE_MF_VARIANTS);
						if(result) {
							validator.addProblem(CDIValidationMessages.ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_MF[validator.getVersionIndex(stereotype)], CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, new String[]{superStName, stName}, stereotypeDeclaration, resource);
						}
					}
				}
			}
			/*
			 * 9.1.2. Interceptor bindings for stereotypes
			 * - If a stereotype declares interceptor bindings, it must be defined as @Target(TYPE).
			 */
			Collection<IInterceptorBindingDeclaration> interceptorBindingDeclarations = stereotype.getInterceptorBindingDeclarations(false);
			if(!interceptorBindingDeclarations.isEmpty() && !CDIUtil.checkTargetAnnotation(target, TYPE_VARIANTS)) {
				StringBuffer bindings = new StringBuffer();
				boolean first = true;
				for (IInterceptorBindingDeclaration binding : interceptorBindingDeclarations) {
					if(!first) {
						bindings.append(", ");
					}
					bindings.append(binding.getType().getElementName());
					first = false;
				}
				String stName = stereotype.getSourceType().getElementName();
				validator.addProblem(CDIValidationMessages.ILLEGAL_TARGET_IN_INTERCEPTOR_BINDING_TYPE_FOR_STEREOTYPE[validator.getVersionIndex(stereotype)], CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, new String[]{stName, bindings.toString()}, target, resource);
			}
		}
	}

	public void validateInterceptorBindingAnnotationTypeAnnotations(IInterceptorBinding binding) throws JavaModelException {
		/*
		 * 9.1.1. Interceptor binding types with additional interceptor bindings
		 * - Interceptor binding types declared @Target(TYPE) may not be applied to interceptor binding types declared
		 *   @Target({TYPE, METHOD}).
		 */
		Collection<IInterceptorBindingDeclaration> declarations = binding.getInterceptorBindingDeclarations(false);
		if(!declarations.isEmpty()) {
			IAnnotationDeclaration target = binding.getAnnotationDeclaration(CDIConstants.TARGET_ANNOTATION_TYPE_NAME);
			if(target!=null) {
				if(CDIUtil.checkTargetAnnotation(target, TYPE__METHOD_VARIANTS)) {
					for (IInterceptorBindingDeclaration declaration : declarations) {
						IAnnotationType superBinding = declaration.getAnnotation();
						Boolean result = CDIUtil.checkTargetAnnotation(superBinding, TYPE_VARIANTS);
						if(result!=null && result) {
							validator.addProblem(CDIValidationMessages.ILLEGAL_TARGET_IN_INTERCEPTOR_BINDING_TYPE[validator.getVersionIndex(binding)], CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, new String[]{superBinding.getSourceType().getElementName(), binding.getSourceType().getElementName()}, declaration, binding.getResource());
						}
					}
				}
			}
		}
	}

	/**
	 * Validates a scope type.
	 * 
	 * @param qualifier
	 */
	public void validateScopeType(IScope scope) {
		if(scope==null) {
			return;
		}
		IResource resource = scope.getResource();
		if(!validator.shouldValidateResourceOfElement(resource)) {
			return;
		}
		try {
			validateScopeAnnotationTypeAnnotations(scope, resource);
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	private void validateScopeAnnotationTypeAnnotations(IScope scope, IResource resource) throws JavaModelException {
		/*
		 * Scope annotation type should be annotated with @Target({TYPE, METHOD, FIELD})
		 */
		if(scope.getCDIProject().getVersion() == CDIVersion.CDI_1_0) {
			validateTargetAnnotation(scope, SCOPE_GENERAL_TARGET_VARIANTS, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE[validator.getVersionIndex(scope)], resource, CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE_ID);
		}
		
		/*
		 * Scope annotation type should be annotated with @Retention(RUNTIME)
		 */
		validateRetentionAnnotation(scope, CDIValidationMessages.MISSING_RETENTION_ANNOTATION_IN_SCOPE_TYPE[validator.getVersionIndex(scope)], resource, CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_SCOPE_TYPE_ID);
	}

	void validateRetentionAnnotation(ICDIAnnotation type, String message, IResource resource, int message_id) throws JavaModelException {
		IAnnotationDeclaration retention = type.getAnnotationDeclaration(CDIConstants.RETENTION_ANNOTATION_TYPE_NAME);
		if(retention == null) {
			validator.addProblem(message, CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, CDIUtil.convertToSourceReference(type.getSourceType().getNameRange(), resource, type.getSourceType()), resource, message_id);
		} else {
			Object o = retention.getMemberValue(null);
			if(o == null || !CDIConstants.RETENTION_POLICY_RUNTIME_TYPE_NAME.equals(o.toString())) {
				validator.addProblem(message, CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, retention, resource, message_id);
			}
		}
	}

	public void validateQualifierAnnotationTypeAnnotations(IQualifier qualifier, IResource resource) throws JavaModelException {
		/*
		 * Qualifier annotation type should be annotated with @Target({METHOD, FIELD, PARAMETER, TYPE}) or  @Target({"FIELD", "PARAMETER"})
		 * Qualifier annotation type should be annotated with @Retention(RUNTIME)
		 */
		if(qualifier.getCDIProject().getVersion() == CDIVersion.CDI_1_0) { 
			validateTargetAnnotation(qualifier, QUALIFIER_GENERAL_TARGET_VARIANTS, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE[validator.getVersionIndex(qualifier)], resource, CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE_ID);
		}

		/*
		 * Qualifier annotation type should be annotated with @Retention(RUNTIME)
		 */
		validateRetentionAnnotation(qualifier, CDIValidationMessages.MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE[validator.getVersionIndex(qualifier)], resource, CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE_ID);
	}

	private void validateTargetAnnotation(ICDIAnnotation annotationType, String[][] variants, String message, IResource resource, int message_id) throws JavaModelException {
		IAnnotationDeclaration target = annotationType.getAnnotationDeclaration(CDIConstants.TARGET_ANNOTATION_TYPE_NAME);
		if(target==null) {
			validator.addProblem(message, CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, CDIUtil.convertToSourceReference(annotationType.getSourceType().getNameRange(), resource, annotationType.getSourceType()), resource, message_id);
		} else if(!CDIUtil.checkTargetAnnotation(target, variants)) {
			validator.addProblem(message, CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, target, resource, message_id);
		}
	}
}