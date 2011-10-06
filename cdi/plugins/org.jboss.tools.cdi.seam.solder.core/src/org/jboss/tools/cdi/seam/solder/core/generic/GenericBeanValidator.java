/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.solder.core.generic;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.validation.CDICoreValidator;
import org.jboss.tools.cdi.seam.solder.core.CDISeamSolderCorePlugin;
import org.jboss.tools.cdi.seam.solder.core.CDISeamSolderPreferences;
import org.jboss.tools.cdi.seam.solder.core.Version;
import org.jboss.tools.cdi.seam.solder.core.validation.SeamSolderValidationMessages;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class GenericBeanValidator {
	Version version;

	public GenericBeanValidator(Version version) {
		this.version = version;
	}

	public void validateResource(IFile file, CDICoreValidator validator, CDICoreNature project, GenericBeanDefinitionContext context) {
		Map<String, GenericConfiguration> cs = context.getGenericConfigurations();
		for (GenericConfiguration c: cs.values()) {
			if(c.getInvolvedTypes().contains(file.getFullPath())) {
				try {
					validateConfiguration(file, c, validator, project, context);
				} catch (CoreException e) {
					CDISeamSolderCorePlugin.getDefault().logError(e);
				}
			}
		}
	}

	public void validateConfiguration(IFile file, GenericConfiguration c, CDICoreValidator validator, CDICoreNature project, GenericBeanDefinitionContext context) throws CoreException {
		IParametedType t = c.getConfigType();
		AnnotationDefinition genericType = c.getGenericTypeDefinition();

		if(genericType == null) {
			String n = c.getGenericTypeName();
			for (TypeDefinition d: c.getGenericBeans()) {
				if(d.getResource() != null && d.getResource().equals(file)) {
					IAnnotationDeclaration a = d.getAnnotation(version.getGenericConfigurationAnnotationTypeName());
					validator.addError(SeamSolderValidationMessages.WRONG_GENERIC_CONFIGURATION_ANNOTATION_REFERENCE, 
						CDISeamSolderPreferences.WRONG_GENERIC_CONFIGURATION_ANNOTATION_REFERENCE, new String[]{n}, a, file);
				}
			}
		} else if(file.equals(genericType.getResource())) {
			if(t != null && context.isGenericBean(t.getType().getFullyQualifiedName())) {
				IAnnotationDeclaration a = genericType.getAnnotation(version.getGenericTypeAnnotationTypeName());
				validator.addError(SeamSolderValidationMessages.GENERIC_CONFIGURATION_TYPE_IS_A_GENERIC_BEAN, 
						CDISeamSolderPreferences.GENERIC_CONFIGURATION_TYPE_IS_A_GENERIC_BEAN, new String[0], a, file);
			}
		}

		Map<AbstractMemberDefinition, List<IQualifierDeclaration>> bs = c.getGenericConfigurationPoints();
		for (AbstractMemberDefinition d: bs.keySet()) {
			if(d.getResource() != null && d.getResource().equals(file) && !d.getTypeDefinition().isVetoed()) {
				ITextSourceReference reference = CDIUtil.convertToSourceReference(((IMember)d.getMember()).getNameRange(), file);
				
				/*
				 * If several generic configuration points have the same set of qualifiers,
				 * than respective generic beans will result in ambiguous beans for injections
				 * with that set of qualifiers.
				 */
				StringBuffer duplicates = new StringBuffer();
				List<IQualifierDeclaration> ds = bs.get(d);
				for (AbstractMemberDefinition d1: bs.keySet()) {
					List<IQualifierDeclaration> ds2 = bs.get(d1);
					if(ds2 != ds && !d1.getTypeDefinition().isVetoed() 
							&& CDIProject.areMatchingQualifiers(ds, ds2)
							&& CDIProject.areMatchingQualifiers(ds2, ds)) {
						duplicates.append(", ").append(definitionToString(d1));
					}
				}
				if(duplicates.length() > 0) {
					duplicates.insert(0, definitionToString(d));
					String message = NLS.bind(SeamSolderValidationMessages.AMBIGUOUS_GENERIC_CONFIGURATION_POINT, duplicates.toString());
					validator.addError(message, 
							CDISeamSolderPreferences.AMBIGUOUS_GENERIC_CONFIGURATION_POINT, new String[0], reference, file);
				}

				/*
				 * Type of generic configuration point must be assignable to the configuration type.
				 */
				IBean b = findGenericBean(file, (IMember)d.getMember(), project);
				if(t == null || b == null || !CDIProject.containsType(b.getAllTypes(), t)) {
					validator.addError(SeamSolderValidationMessages.WRONG_TYPE_OF_GENERIC_CONFIGURATION_POINT, 
							CDISeamSolderPreferences.WRONG_TYPE_OF_GENERIC_CONFIGURATION_POINT, new String[0], reference, file);
				}
				
			}
		}
		//TODO check`

	}

	private String definitionToString(AbstractMemberDefinition d) {
		IAnnotatable e = d.getMember();
		String result = "";
		if(e instanceof IType) {
			result = ((IType)e).getElementName();
		} else if(e instanceof IMember) {
			IMember m = (IMember)e;
			result = m.getDeclaringType().getElementName() + "." + m.getElementName();
			if(e instanceof IMethod) {
				result += "()";
			}
		}
		return result;
	}

	private IBean findGenericBean(IFile file, IMember member, CDICoreNature project) {
		Set<IBean> bs = project.getDelegate().getBeans(file.getFullPath());
		for (IBean b: bs) {
			if(b instanceof IClassBean) {
				if(member.equals(((IClassBean)b).getBeanClass())) return b;
			} else if(b instanceof IProducer) {
				if(member.equals(((IProducer)b).getSourceMember())) return b;
			}
		}
		return null;
	}
	

}
