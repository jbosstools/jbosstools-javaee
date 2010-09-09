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
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IDecorator;
import org.jboss.tools.cdi.core.IInterceptor;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.jst.web.kb.KbQuery;

/**
 * @author Alexey Kazakov
 */
public class BeansXmlProcessor {

	private static final BeansXmlProcessor INSTANCE = new BeansXmlProcessor();

	public static final Image CLASS_PROPOSAL_IMAGE = JavaPluginImages.DESC_OBJS_CLASS.createImage();
	public static final Image ANNOTATION_PROPOSAL_IMAGE = JavaPluginImages.DESC_OBJS_ANNOTATION.createImage();

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
		if(parents.length>1) {
			CDICoreNature nature = CDICorePlugin.getCDI(project, false);
			if(nature!=null) {
				ICDIProject cdiProject = nature.getDelegate();
				int lastIndex = parents.length-1;
				int firstIndex = parents.length-2;
				if(CLASS_ELEMENT.equals(parents[lastIndex])) {
					if(ALTERNATIVES_ELEMENT.equals(parents[firstIndex])) {
						return getAlternativeBeans(query, cdiProject);
					} else if(DECORATORS_ELEMENT.equals(parents[firstIndex])) {
						return getDecorators(query, cdiProject);
					} else if(INTERCEPTOR_ELEMENT.equals(parents[firstIndex])) {
						return getInterceptors(query, cdiProject);
					}
				} else if(STEREOTYPE_ELEMENT.equals(parents[lastIndex]) && ALTERNATIVES_ELEMENT.equals(parents[firstIndex])) {
					return getAlternativeStereotypes(query, cdiProject);
				}
			}
		}
		return EMPTY_ARRAY;
	}

	private TextProposal[] getAlternativeBeans(KbQuery query, ICDIProject cdiProject) {
		List<TextProposal> proposals = new ArrayList<TextProposal>();
		String value = removeLeadingWhitespace(query.getValue());
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
		String value = removeLeadingWhitespace(query.getValue());
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
		String value = removeLeadingWhitespace(query.getValue());
		IDecorator[] decorators = cdiProject.getDecorators();
		for (IDecorator bean : decorators) {
			IType type = bean.getBeanClass();
			addMatchedType(type, value, proposals);
		}
		return proposals.toArray(new TextProposal[0]);
	}

	private TextProposal[] getInterceptors(KbQuery query, ICDIProject cdiProject) {
		List<TextProposal> proposals = new ArrayList<TextProposal>();
		String value = removeLeadingWhitespace(query.getValue());
		IInterceptor[] interceptors = cdiProject.getInterceptors();
		for (IInterceptor bean : interceptors) {
			IType type = bean.getBeanClass();
			addMatchedType(type, value, proposals);
		}
		return proposals.toArray(new TextProposal[0]);
	}

	private String removeLeadingWhitespace(String value) {
		int len = value.length();
		int st = 0;
		char[] val = value.toCharArray();
		while ((st < len) && (val[st] <= ' ')) {
		    st++;
		}
		return (st > 0) ? value.substring(st) : value;
	}

	private void addMatchedType(IType type, String value, List<TextProposal> proposals) {
		String fullTypeName = type.getFullyQualifiedName();
		if(fullTypeName.startsWith(value)) {
			TextProposal proposal = new TextProposal();
			proposal.setContextInfo(fullTypeName);
			proposal.setLabel(type.getElementName() + " - " + type.getPackageFragment().getElementName());
//			proposal.setLabel(fullTypeName);
			proposal.setReplacementString(fullTypeName);
			proposal.setPosition(fullTypeName.length());
//			proposal.setImage(CdiElResolver.CDI_EL_PROPOSAL_IMAGE);
			try {
				if(type.isClass()) {
					proposal.setImage(CLASS_PROPOSAL_IMAGE);
				} else {
					proposal.setImage(ANNOTATION_PROPOSAL_IMAGE);
				}
			} catch (JavaModelException e) {
				CDICorePlugin.getDefault().logError(e);
			}
			proposals.add(proposal);
		}
	}
}