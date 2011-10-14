/******************************************************************************* 
 * Copyright (c) 2009-2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.jsf2.bean.el;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.parser.ELParserFactory;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector.MemberInfo;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.jsf2.bean.model.IJSF2ManagedBean;
import org.jboss.tools.jsf.jsf2.bean.model.IJSF2Project;
import org.jboss.tools.jsf.jsf2.bean.model.JSF2ProjectFactory;

/**
 * @author Alexey Kazakov & Viacheslav Kabanovich
 */
public class JSF2ElResolver extends AbstractELCompletionEngine<IJSF2ManagedBean> {

	private static ELParserFactory factory = ELParserUtil.getJbossFactory();

	public static final Image JSF_EL_PROPOSAL_IMAGE = 
		JSFModelPlugin.getDefault().getImage(JSFModelPlugin.CA_JSF_EL_IMAGE_PATH);

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine#getELProposalImageForMember(org.jboss.tools.common.el.core.resolver.TypeInfoCollector.MemberInfo)
	 */
	@Override
	public Image getELProposalImageForMember(MemberInfo memberInfo) {
		return JSF_EL_PROPOSAL_IMAGE;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine#log(java.lang.Exception)
	 */
	@Override
	protected void log(Exception e) {
		JSFModelPlugin.getDefault().logError(e);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine#getMemberInfoByVariable(org.jboss.tools.common.el.core.resolver.IVariable, boolean)
	 */
	@Override
	protected MemberInfo getMemberInfoByVariable(IJSF2ManagedBean bean, boolean onlyEqualNames, int offset) {
		return TypeInfoCollector.createMemberInfo(bean.getBeanClass());
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine#resolveVariables(org.eclipse.core.resources.IFile, org.jboss.tools.common.el.core.model.ELInvocationExpression, boolean, boolean)
	 */
	@Override
	public List<IJSF2ManagedBean> resolveVariables(IFile file,	ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames, int offset) {
		ArrayList<IJSF2ManagedBean> beans = new ArrayList<IJSF2ManagedBean>();

		IProject project = file.getProject();
		if (project == null) {
			return beans; 
		}

		String varName = expr.toString();

		Set<IJSF2ManagedBean> resolvedBeans = null;
		if (varName != null) {
			IJSF2Project manager = JSF2ProjectFactory.getJSF2ProjectWithProgress(project);
			if (manager != null && !manager.isMetadataComplete()) {
				if(onlyEqualNames) {
					resolvedBeans = manager.getManagedBeans(varName);
					beans.addAll(resolvedBeans);
				} else {
					resolvedBeans = manager.getManagedBeans();
					for (IJSF2ManagedBean bean : resolvedBeans) {
						if(bean.getName().startsWith(varName)) {
							beans.add(bean);
						}
					}
					resolvedBeans.clear();
					resolvedBeans.addAll(beans);
				}
			}
		}
		if (resolvedBeans != null && !resolvedBeans.isEmpty()) {
			List<IJSF2ManagedBean> newResolvedVars = new ArrayList<IJSF2ManagedBean>();
			for (IJSF2ManagedBean var : resolvedBeans) {
				if(!isFinal) {
					// Do filter by equals (name)
					// In case of the last pass - do not filter by startsWith(name) instead of equals
					if (varName.equals(var.getName())) {
						newResolvedVars.add(var);
					}
				} else {
					newResolvedVars.add(var);
				}
			}
			return newResolvedVars;
		}
		return beans;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.resolver.ELResolver#getParserFactory()
	 */
	public ELParserFactory getParserFactory() {
		return factory;
	}

	@Override
	protected boolean isStaticMethodsCollectingEnabled() {
		return true;
	}
}