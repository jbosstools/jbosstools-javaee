/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.parser.ELParserFactory;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.el.core.resolver.IVariable;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.model.pv.JSFPromptingProvider;

/**
 * Utility class used to collect info for EL
 * 
 * @author Viacheslav Kabanovich
 */
public class JSFELCompletionEngine extends AbstractELCompletionEngine<JSFELCompletionEngine.IJSFVariable> {

	private static final Image JSF_EL_PROPOSAL_IMAGE = JSFModelPlugin.getDefault().getImage(JSFModelPlugin.CA_JSF_EL_IMAGE_PATH);
	private static ELParserFactory factory = ELParserUtil.getDefaultFactory();

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine#getELProposalImage()
	 */
	public Image getELProposalImage() {
		return JSF_EL_PROPOSAL_IMAGE;
	}

	public JSFELCompletionEngine() {}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine#log(java.lang.Exception)
	 */
	protected void log(Exception e) {
		JSFModelPlugin.getPluginLog().logError(e);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.resolver.ELCompletionEngine#getParserFactory()
	 */
	public ELParserFactory getParserFactory() {
		return factory;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine#resolveVariables(org.eclipse.core.resources.IFile, org.jboss.tools.common.el.core.model.ELInvocationExpression, boolean, boolean)
	 */
	public List<IJSFVariable> resolveVariables(IFile file, ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames) {
		IModelNature project = EclipseResourceUtil.getModelNature(file.getProject());
		return resolveVariables(file, project, expr, isFinal, onlyEqualNames);
	}

	/**
	 * 
	 * @param project
	 * @param expr
	 * @param isFinal
	 * @param onlyEqualNames
	 * @return
	 */
	public List<IJSFVariable> resolveVariables(IFile file, IModelNature project, ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames) {
		List<IJSFVariable>resolvedVars = new ArrayList<IJSFVariable>();
		
		if (project == null)
			return new ArrayList<IJSFVariable>(); 
		
		String varName = expr.toString();

		if (varName != null) {
			resolvedVars = resolveVariables(project, varName, onlyEqualNames);
		}
		if (resolvedVars != null && !resolvedVars.isEmpty()) {
			List<IJSFVariable> newResolvedVars = new ArrayList<IJSFVariable>();
			for (IJSFVariable var : resolvedVars) {
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
		} else if (varName != null && (varName.startsWith("\"") || varName.startsWith("'")) && (varName.endsWith("\"") || varName.endsWith("'"))) {
			IJavaProject jp = EclipseResourceUtil.getJavaProject(file.getProject());
			if(jp!=null) {
				try {
					IType type = jp.findType("java.lang.String");
					if (type != null) {
						IMethod m = type.getMethod("toString", new String[0]);
						if (m != null) {
							IJSFVariable v = new Variable("String", m);
							List<IJSFVariable> newResolvedVars = new ArrayList<IJSFVariable>();
							newResolvedVars.add(v);
							return newResolvedVars;
						}
					}
				} catch (JavaModelException e) {
					JSFModelPlugin.getDefault().logError(e);
				}
			}
		}
		return new ArrayList<IJSFVariable>(); 
	}

	protected List<IJSFVariable> resolveVariables(IModelNature project, String varName, boolean onlyEqualNames) {
		if(project == null) return null;
		List<IJSFVariable> beans = new JSFPromptingProvider().getVariables(project.getModel());
		List<IJSFVariable> resolvedVariables = new ArrayList<IJSFVariable>();
		for (IJSFVariable variable: beans) {
			String n = variable.getName();
			if(onlyEqualNames) {
				if (n.equals(varName)) {
					resolvedVariables.add(variable);
				}
			} else {
				if (n.startsWith(varName)) {
					resolvedVariables.add(variable);
				}
			}
		}
		return resolvedVariables;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine#getMemberInfoByVariable(org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine.IVariable, boolean)
	 */
	protected TypeInfoCollector.MemberInfo getMemberInfoByVariable(IJSFVariable var, boolean onlyEqualNames) {
		return TypeInfoCollector.createMemberInfo(((IJSFVariable)var).getSourceMember());		
	}

	public static interface IJSFVariable extends IVariable {
		public IMember getSourceMember();
	}

	public static class Variable implements IJSFVariable {

		private String name;
		private IMember source;

		public Variable(String name, IMember source) {
			this.name = name;
			this.source = source;
		}

		/*
		 * (non-Javadoc)
		 * @see org.jboss.tools.jsf.model.JSFELCompletionEngine.IJSFVariable#getSourceMember()
		 */
		public IMember getSourceMember() {
			return source;
		}

		/*
		 * (non-Javadoc)
		 * @see org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine.IVariable#getName()
		 */
		public String getName() {
			return name;
		}
	}
}