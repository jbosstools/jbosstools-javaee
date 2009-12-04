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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanManager;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.core.ITypeDeclaration;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.parser.ELParserFactory;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector.MemberInfo;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine;

/**
 * @author Alexey Kazakov
 */
public class CdiElResolver extends AbstractELCompletionEngine<IBean> {

	private static ELParserFactory factory = ELParserUtil.getJbossFactory();

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine#getELProposalImage()
	 */
	@Override
	public Image getELProposalImage() {
		// TODO Auto-generated method stub
		return null;
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
	protected MemberInfo getMemberInfoByVariable(IBean var,	boolean onlyEqualNames) {
		// TODO
		return null;
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

		if (varName != null) {
			IBeanManager manager = null;
			// TODO
//			Set<IBean> resolvedBeans = manager.getBeans(varName, true);
			Set<IBean> resolvedBeans = new HashSet<IBean>();
			if(onlyEqualNames) {
				beans.addAll(resolvedBeans);
			} else {
				for (IBean bean : resolvedBeans) {
					if(bean.getName().startsWith(varName)) {
						beans.add(bean);
					}
				}
			}
		}
		if (beans.isEmpty() && varName != null && (varName.startsWith("\"") || varName.startsWith("'"))
								&& (varName.endsWith("\"") || varName.endsWith("'"))) {
			IJavaProject jp = EclipseUtil.getJavaProject(project.getProject());
			try {
				IType type = jp.findType("java.lang.String");
				if(type != null) {
					IMethod m = type.getMethod("toString", new String[0]);
					if(m != null) {
						IBean bean = new StringVariable(m);
						beans.add(bean);
					}
				}
			} catch (JavaModelException e) {
				CDICorePlugin.getDefault().logError(e);
			}
			
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

	private static class StringVariable implements IBean {

		private IMember member;

		public StringVariable(IMember member) {
			this.member = member;
		}

		public Set<ITypeDeclaration> getAllTypeDeclarations() {
			return Collections.emptySet();
		}

		public IAnnotationDeclaration getAlternativeDeclaration() {
			return null;
		}

		public IType getBeanClass() {
			return member.getDeclaringType();
		}

		public Set<IInjectionPoint> getInjectionPoints() {
			return Collections.emptySet();
		}

		public Set<IType> getLegalTypes() {
			return Collections.emptySet();
		}

		public String getName() {
			return null;
		}

		public ITextSourceReference getNameLocation() {
			return null;
		}

		public Set<IAnnotationDeclaration> getQualifierDeclarations() {
			return Collections.emptySet();
		}

		public Set<ITypeDeclaration> getRestrictedTypeDeclaratios() {
			return Collections.emptySet();
		}

		public IBean getSpecializedBean() {
			return null;
		}

		public IAnnotationDeclaration getSpecializesAnnotationDeclaration() {
			return null;
		}

		public Set<IStereotypeDeclaration> getStereotypeDeclarations() {
			return Collections.emptySet();
		}

		public boolean isAlternative() {
			return false;
		}

		public boolean isDependent() {
			return false;
		}

		public boolean isEnabled() {
			return false;
		}

		public boolean isSpecializing() {
			return false;
		}

		public IType getScope() {
			return null;
		}

		public Set<IAnnotationDeclaration> getScopeDeclarations() {
			return Collections.emptySet();
		}

		public ICDIProject getCDIProject() {
			return null;
		}

		public IResource getResource() {
			return member.getResource();
		}

		public IPath getSourcePath() {
			return member.getPath();
		}
	}
}