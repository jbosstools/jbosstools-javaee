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

package org.jboss.tools.cdi.internal.core.ca;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IDecorator;
import org.jboss.tools.cdi.core.IInterceptor;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.internal.core.el.CdiElResolver;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.jst.web.kb.KbQuery;

/**
 * @author Alexey Kazakov
 */
public class BeansXmlProcessor {

	private static final BeansXmlProcessor INSTANCE = new BeansXmlProcessor();

	/**
	 * @return instance of PageProcessor
	 */
	public static BeansXmlProcessor getInstance() {
		return INSTANCE;
	}

	private BeansXmlProcessor() {
	}

	private final static TextProposal[] EMPTY_ARRAY = new TextProposal[0];

	private final static String ALTERNATIVES_ELEMENT = "alternatives"; //$NON-NLS-1$
	private final static String CLASS_ELEMENT = "class"; //$NON-NLS-1$
	private final static String STEREOTYPE_ELEMENT = "stereotype"; //$NON-NLS-1$
	private final static String DECORATORS_ELEMENT = "decorators"; //$NON-NLS-1$
	private final static String INTERCEPTOR_ELEMENT = "interceptors"; //$NON-NLS-1$

	/**
	 * Returns proposals for the query
	 * @param query
	 * @return
	 */
	public TextProposal[] getProposals(KbQuery query, IProject project) {
		String[] parents = query.getParentTags();
		if(parents.length>2) {
			CDICoreNature nature = CDICorePlugin.getCDI(project, false);
			if(nature!=null) {
				ICDIProject cdiProject = nature.getDelegate();
				if(CLASS_ELEMENT.equals(parents[1])) {
					if(ALTERNATIVES_ELEMENT.equals(parents[0])) {
						return getAlternativeBeans(query, cdiProject);
					} else if(DECORATORS_ELEMENT.equals(parents[0])) {
						return getDecorators(query, cdiProject);
					} else if(INTERCEPTOR_ELEMENT.equals(parents[0])) {
						return getInterceptors(query, cdiProject);
					}
				} else if(STEREOTYPE_ELEMENT.equals(parents[1]) && ALTERNATIVES_ELEMENT.equals(parents[0])) {
					return getAlternativeStereotypes(query, cdiProject);
				}
			}
		}
		return EMPTY_ARRAY;
	}

	private TextProposal[] getAlternativeBeans(KbQuery query, ICDIProject cdiProject) {
		List<TextProposal> proposals = new ArrayList<TextProposal>();
		String value = query.getValue().trim();
		IBean[] alternatives = cdiProject.getAlternatives();
		for (IBean bean : alternatives) {
			if(bean instanceof IClassBean) {
				IType type = bean.getBeanClass();
				addMatchedType(type, value, proposals);
			}
		}
		return proposals.toArray(new TextProposal[0]);
	}

	private TextProposal[] getAlternativeStereotypes(KbQuery query, ICDIProject cdiProject) {
		List<TextProposal> proposals = new ArrayList<TextProposal>();
		String value = query.getValue().trim();
		IStereotype[] alternatives = cdiProject.getStereotypes();
		for (IStereotype stereotype : alternatives) {
			if(stereotype.isAlternative()) {
				IType type = stereotype.getSourceType();
				addMatchedType(type, value, proposals);
			}
		}
		return proposals.toArray(new TextProposal[0]);
	}

	private TextProposal[] getDecorators(KbQuery query, ICDIProject cdiProject) {
		List<TextProposal> proposals = new ArrayList<TextProposal>();
		String value = query.getValue().trim();
		IDecorator[] decorators = cdiProject.getDecorators();
		for (IDecorator bean : decorators) {
			IType type = bean.getBeanClass();
			addMatchedType(type, value, proposals);
		}
		return proposals.toArray(new TextProposal[0]);
	}

	private TextProposal[] getInterceptors(KbQuery query, ICDIProject cdiProject) {
		List<TextProposal> proposals = new ArrayList<TextProposal>();
		String value = query.getValue().trim();
		IInterceptor[] interceptors = cdiProject.getInterceptors();
		for (IInterceptor bean : interceptors) {
			IType type = bean.getBeanClass();
			addMatchedType(type, value, proposals);
		}
		return proposals.toArray(new TextProposal[0]);
	}

	private void addMatchedType(IType type, String value, List<TextProposal> proposals) {
		String fullTypeName = type.getFullyQualifiedName();
		if(fullTypeName.startsWith(value)) {
			TextProposal proposal = new TextProposal();
			proposal.setLabel(fullTypeName);
			proposal.setReplacementString(fullTypeName);
			proposal.setPosition(fullTypeName.length());
			proposal.setImage(CdiElResolver.CDI_EL_PROPOSAL_IMAGE);

			proposals.add(proposal);
		}
	}
}