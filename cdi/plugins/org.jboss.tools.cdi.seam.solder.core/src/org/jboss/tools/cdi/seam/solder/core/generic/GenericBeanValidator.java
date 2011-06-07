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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMember;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.validation.CDICoreValidator;
import org.jboss.tools.cdi.seam.solder.core.CDISeamSolderCorePlugin;
import org.jboss.tools.cdi.seam.solder.core.CDISeamSolderPreferences;
import org.jboss.tools.cdi.seam.solder.core.validation.SeamSolderValidationMessages;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class GenericBeanValidator {

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
		Map<AbstractMemberDefinition, List<IQualifierDeclaration>> bs = c.getGenericProducerBeans();
		for (AbstractMemberDefinition d: bs.keySet()) {
			if(d.getResource() != null && d.getResource().equals(file) && !d.getTypeDefinition().isVetoed()) {
				List<IQualifierDeclaration> ds = bs.get(d);
				for (AbstractMemberDefinition d1: bs.keySet()) {
					List<IQualifierDeclaration> ds2 = bs.get(d1);
					if(ds2 != ds && !d1.getTypeDefinition().isVetoed() && CDIProject.areMatchingQualifiers(ds, ds2)) {
						ITextSourceReference reference = CDIUtil.convertToSourceReference(((IMember)d.getMember()).getNameRange(), file);
						validator.addError(SeamSolderValidationMessages.AMBIGUOUS_GENERIC_BEAN, CDISeamSolderPreferences.AMBIGUOUS_GENERIC_BEAN, new String[0], reference, file);
					}
				}
			}
		}
		//TODO check`

	}
	

}
