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

import java.text.MessageFormat;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.core.preferences.CDIPreferences;

/**
 * CDI annotation validator.
 * 
 * @author Alexey Kazakov
 */
public class AnnotationValidationDelegate extends CDICoreValidationDelegate {

	static final String TARGET_METHOD = "METHOD";
	static final String TARGET_FIELD = "FIELD";
	static final String TARGET_PARAMETER = "PARAMETER";
	static final String TARGET_TYPE = "TYPE";

	static final String[] TMF = {TARGET_METHOD, TARGET_FIELD, TARGET_TYPE};
	static final String[] MF = {TARGET_METHOD, TARGET_FIELD};
	static final String[][] STEREOTYPE_GENERAL_TARGET_VARAINTS = {TMF, MF,
	           {TARGET_TYPE}, {TARGET_METHOD}, {TARGET_FIELD}};
	static final String[][] QUALIFIER_GENERAL_TARGET_VARIANTS = {{TARGET_METHOD, TARGET_FIELD, TARGET_PARAMETER, TARGET_TYPE}, 
            {TARGET_FIELD, TARGET_PARAMETER}};
	static final String[][] SCOPE_GENERAL_TARGET_VARIANTS = {TMF};
	static final String[][] STEREOTYPE_TMF_VARIANTS = {TMF};
	static final String[][] STEREOTYPE_MF_VARIANTS = {MF};
	static final String[][] STEREOTYPE_M_VARIANTS = {{TARGET_METHOD}};
	static final String[][] STEREOTYPE_F_VARIANTS = {{TARGET_FIELD}};

	static final String[][] TYPE_VARIANTS = {{TARGET_TYPE}};

	public AnnotationValidationDelegate(CDICoreValidator validator) {
		super(validator);
	}

	public void validateStereotypeAnnotationTypeAnnotations(IStereotype stereotype, IResource resource) throws JavaModelException {
		/*
		 * Stereotype annotation type should be annotated with @Target with correct targets [JSR-299 §2.7.1]
		 * Stereotype annotation type should be annotated with @Retention(RUNTIME)
		 */
		validateTargetAnnotation(stereotype, STEREOTYPE_GENERAL_TARGET_VARAINTS, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE, resource);

		/*
		 * Stereotype annotation type should be annotated with @Retention(RUNTIME)
		 */
		validateRetentionAnnotation(stereotype, CDIValidationMessages.MISSING_RETENTION_ANNOTATION_IN_STEREOTYPE_TYPE, resource);

		/*
		 * 2.7.1.5. Stereotypes with additional stereotypes
		 * - Stereotypes declared @Target(TYPE) may not be applied to stereotypes declared @Target({TYPE, METHOD, FIELD}),
		 *   @Target(METHOD), @Target(FIELD) or @Target({METHOD, FIELD}).
		 */
		Set<IStereotypeDeclaration> stereotypes = stereotype.getStereotypeDeclarations();
		for (IStereotypeDeclaration stereotypeDeclaration : stereotypes) {
			IStereotype superStereotype = stereotypeDeclaration.getStereotype();
			if(superStereotype!=null) {
				Boolean result = CDIUtil.checkTargetAnnotation(superStereotype, TYPE_VARIANTS);
				if(result!=null && result) {
					IAnnotationDeclaration target = stereotype.getAnnotationDeclaration(CDIConstants.TARGET_ANNOTATION_TYPE_NAME);
					if(target!=null) {
						result = CDIUtil.checkTargetAnnotation(target, STEREOTYPE_TMF_VARIANTS);
						String stName = stereotype.getSourceType().getElementName();
						String superStName = superStereotype.getSourceType().getElementName();
						if(result) {
							validator.addError(CDIValidationMessages.ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_TMF, CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, new String[]{superStName, stName}, stereotypeDeclaration, resource);
							continue;
						}
						result = CDIUtil.checkTargetAnnotation(target, STEREOTYPE_M_VARIANTS);
						if(result) {
							validator.addError(CDIValidationMessages.ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_M, CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, new String[]{superStName, stName}, stereotypeDeclaration, resource);
							continue;
						}
						result = CDIUtil.checkTargetAnnotation(target, STEREOTYPE_F_VARIANTS);
						if(result) {
							validator.addError(CDIValidationMessages.ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_F, CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, new String[]{superStName, stName}, stereotypeDeclaration, resource);
							continue;
						}
						result = CDIUtil.checkTargetAnnotation(target, STEREOTYPE_MF_VARIANTS);
						if(result) {
							validator.addError(CDIValidationMessages.ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_MF, CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, new String[]{superStName, stName}, stereotypeDeclaration, resource);
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
		if(scope == null) {
			return;
		}
		IResource resource = scope.getResource();
		if (resource == null || !resource.getName().toLowerCase().endsWith(".java")) {
			// validate sources only
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
		validateTargetAnnotation(scope, SCOPE_GENERAL_TARGET_VARIANTS, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE, resource);
		
		/*
		 * Scope annotation type should be annotated with @Retention(RUNTIME)
		 */
		validateRetentionAnnotation(scope, CDIValidationMessages.MISSING_RETENTION_ANNOTATION_IN_SCOPE_TYPE, resource);
	}

	void validateRetentionAnnotation(ICDIAnnotation type, String message, IResource resource) throws JavaModelException {
		IAnnotationDeclaration retention = type.getAnnotationDeclaration(CDIConstants.RETENTION_ANNOTATION_TYPE_NAME);
		if(retention == null) {
			validator.addError(message, CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, CDIUtil.convertToSourceReference(type.getSourceType().getNameRange()), resource);
		} else {
			IMemberValuePair[] ps = retention.getDeclaration().getMemberValuePairs();
			boolean ok = false;
			for (IMemberValuePair p: ps) {
				if(!"value".equals(p.getMemberName())) continue;
				Object o = p.getValue();
				if(o != null) {
					ok = true;
					String s = o.toString();
					int i = s.lastIndexOf('.');
					if(i >= 0) s = s.substring(i + 1);
					if(!"RUNTIME".equals(s)) ok = false;
				}
			}
			if(!ok) {
				validator.addError(message, CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, retention, resource);
			}
		}
	}

	public void validateQualifierAnnotationTypeAnnotations(IQualifier qualifier, IResource resource) throws JavaModelException {
		/*
		 * Qualifier annotation type should be annotated with @Target({METHOD, FIELD, PARAMETER, TYPE}) or  @Target({"FIELD", "PARAMETER"})
		 * Qualifier annotation type should be annotated with @Retention(RUNTIME)
		 */
		validateTargetAnnotation(qualifier, QUALIFIER_GENERAL_TARGET_VARIANTS, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE, resource);

		/*
		 * Qualifier annotation type should be annotated with @Retention(RUNTIME)
		 */
		validateRetentionAnnotation(qualifier, CDIValidationMessages.MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE, resource);
	}

	private void validateTargetAnnotation(ICDIAnnotation annotationType, String[][] variants, String message, IResource resource) throws JavaModelException {
		IAnnotationDeclaration target = annotationType.getAnnotationDeclaration(CDIConstants.TARGET_ANNOTATION_TYPE_NAME);
		if(target==null) {
			validator.addError(message, CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, CDIUtil.convertToSourceReference(annotationType.getSourceType().getNameRange()), resource);
		} else if(!CDIUtil.checkTargetAnnotation(target, variants)) {
			validator.addError(message, CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, target, resource);
		}
	}
}