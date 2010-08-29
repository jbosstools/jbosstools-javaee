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

import java.util.HashSet;
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
import org.jboss.tools.cdi.core.preferences.CDIPreferences;

/**
 * CDI annotation validator.
 * 
 * @author Alexey Kazakov
 */
public class AnnotationValidationDelegate extends CDICoreValidationDelegate {

	public AnnotationValidationDelegate(CDICoreValidator validator) {
		super(validator);
	}

	public void validateStereotypeAnnotationTypeAnnotations(IStereotype stereotype, IResource resource) throws JavaModelException {
		/*
		 * Stereotype annotation type should be annotated with @Target with correct targets [JSR-299 §2.7.1]
		 * Stereotype annotation type should be annotated with @Retention(RUNTIME)
		 */
		String[][] variants = {{TARGET_METHOD, TARGET_FIELD, TARGET_TYPE}, 
					           {TARGET_METHOD, TARGET_FIELD},
					           {TARGET_TYPE}, {TARGET_METHOD}, {TARGET_FIELD}};
		validateTargetAnnotation(stereotype, variants, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE, resource);

		/*
		 * Stereotype annotation type should be annotated with @Retention(RUNTIME)
		 */
		validateRetentionAnnotation(stereotype, CDIValidationMessages.MISSING_RETENTION_ANNOTATION_IN_STEREOTYPE_TYPE, resource);
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
		String[][] variants = {{TARGET_TYPE, TARGET_METHOD, TARGET_FIELD}};
		validateTargetAnnotation(scope, variants, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE, resource);
		
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

	Set<String> getTargetAnnotationValues(IAnnotationDeclaration target) throws JavaModelException {
		Set<String> result = new HashSet<String>();
		IMemberValuePair[] ps = target.getDeclaration().getMemberValuePairs();
		for (IMemberValuePair p: ps) {
			if(!"value".equals(p.getMemberName())) continue;
			Object o = p.getValue();
			if(o instanceof Object[]) {
				Object[] os = (Object[])o;
				for (Object q: os) {
					String s = q.toString();
					int i = s.lastIndexOf('.');
					if(i >= 0) s = s.substring(i + 1);
					result.add(s);
				}
			} else if(o != null) {
				String s = o.toString();
				int i = s.lastIndexOf('.');
				if(i >= 0) s = s.substring(i + 1);
				result.add(s);
			}
		}
		return result;
	}

	static String TARGET_METHOD = "METHOD";
	static String TARGET_FIELD = "FIELD";
	static String TARGET_PARAMETER = "PARAMETER";
	static String TARGET_TYPE = "TYPE";

	public void validateQualifierAnnotationTypeAnnotations(IQualifier qualifier, IResource resource) throws JavaModelException {
		/*
		 * Qualifier annotation type should be annotated with @Target({METHOD, FIELD, PARAMETER, TYPE}) or  @Target({"FIELD", "PARAMETER"})
		 * Qualifier annotation type should be annotated with @Retention(RUNTIME)
		 */
		String[][] variants = {{TARGET_METHOD, TARGET_FIELD, TARGET_PARAMETER, TARGET_TYPE}, 
				               {TARGET_FIELD, TARGET_PARAMETER}};
		validateTargetAnnotation(qualifier, variants, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE, resource);

		/*
		 * Qualifier annotation type should be annotated with @Retention(RUNTIME)
		 */
		validateRetentionAnnotation(qualifier, CDIValidationMessages.MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE, resource);
	}

	private void validateTargetAnnotation(ICDIAnnotation annotationType, String[][] variants, String message, IResource resource) throws JavaModelException {
		IAnnotationDeclaration target = annotationType.getAnnotationDeclaration(CDIConstants.TARGET_ANNOTATION_TYPE_NAME);
		if(target == null) {
			validator.addError(message, CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, CDIUtil.convertToSourceReference(annotationType.getSourceType().getNameRange()), resource);
		} else {
			Set<String> vs = getTargetAnnotationValues(target);
			boolean ok = false;
			for (int i = 0; i < variants.length; i++) {
				if(vs.size() == variants[i].length) {
					boolean ok2 = true;
					String[] values = variants[i];
					for (String s: values) {
						if(!vs.contains(s)) {
							ok2 = false;
							break;
						}
					}
					if(ok2) {
						ok = true;
						break;
					}
				}
			}
			if(!ok) {
				validator.addError(message, CDIPreferences.MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE, target, resource);
			}
		}
	}
}