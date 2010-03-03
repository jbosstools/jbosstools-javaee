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
package org.jboss.tools.cdi.internal.core.el;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanManager;
import org.jboss.tools.cdi.core.IBeanMember;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.parser.ELParserFactory;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector.MemberInfo;

/**
 * @author Alexey Kazakov
 */
public class CdiElResolver extends AbstractELCompletionEngine<IBean> {

	private static ELParserFactory factory = ELParserUtil.getJbossFactory();

	public static final Image CDI_EL_PROPOSAL_IMAGE = 
		CDICorePlugin.getDefault().getImage(CDICorePlugin.CA_CDI_EL_IMAGE_PATH);


	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine#getELProposalImage()
	 */
	@Override
	public Image getELProposalImage() {
		return CDI_EL_PROPOSAL_IMAGE;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine#log(java.lang.Exception)
	 */
	@Override
	protected void log(Exception e) {
		CDICorePlugin.getDefault().logError(e);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine#getMemberInfoByVariable(org.jboss.tools.common.el.core.resolver.IVariable, boolean)
	 */
	@Override
	protected MemberInfo getMemberInfoByVariable(IBean bean, boolean onlyEqualNames) {
		IMember member = null;
		if(bean instanceof IClassBean) {
			member = bean.getBeanClass();
		} else if(bean instanceof IBeanMember) {
			IBeanMember beanMember = (IBeanMember)bean;
			member = beanMember.getSourceMember();
		}
		return TypeInfoCollector.createMemberInfo(member);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine#resolveVariables(org.eclipse.core.resources.IFile, org.jboss.tools.common.el.core.model.ELInvocationExpression, boolean, boolean)
	 */
	@Override
	public List<IBean> resolveVariables(IFile file,	ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames) {
		ArrayList<IBean> beans = new ArrayList<IBean>();

		IProject project = file.getProject();
		if (project == null) {
			return beans; 
		}

		String varName = expr.toString();

		Set<IBean> resolvedBeans = null;
		if (varName != null) {
			IBeanManager manager = CDICorePlugin.getCDI(project, false).getDelegate();
			if(onlyEqualNames) {
				resolvedBeans = manager.getBeans(varName, true);
				beans.addAll(resolvedBeans);
			} else {
				resolvedBeans = manager.getNamedBeans(true);
				for (IBean bean : resolvedBeans) {
					if(bean.getName().startsWith(varName)) {
						beans.add(bean);
					}
				}
				resolvedBeans.clear();
				resolvedBeans.addAll(beans);
			}
		}
		if (resolvedBeans != null && !resolvedBeans.isEmpty()) {
			List<IBean> newResolvedVars = new ArrayList<IBean>();
			for (IBean var : resolvedBeans) {
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