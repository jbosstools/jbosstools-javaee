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
package org.jboss.tools.jsf.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInstance;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELModel;
import org.jboss.tools.common.el.core.parser.ELParser;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

/**
 * @author Alexey Kazakov
 */
public class JSFImplicitObjectELResolver extends JSFELCompletionEngine {

    private static final String FACES_CONTEXT = "facesContext"; //$NON-NLS-1$

    private static final Map<String, ELExpression> IMPLICT_OBJECTS_ELS = new HashMap<String, ELExpression>();
    static {
    	IMPLICT_OBJECTS_ELS.put("application", parseEl("#{facesContext.externalContext.context}")); //$NON-NLS-1$ //$NON-NLS-2$
    	IMPLICT_OBJECTS_ELS.put("applicationScope", parseEl("#{facesContext.externalContext.applicationMap}")); //$NON-NLS-1$ //$NON-NLS-2$
    	IMPLICT_OBJECTS_ELS.put("cookie", parseEl("#{facesContext.externalContext.requestCookieMap}")); //$NON-NLS-1$ //$NON-NLS-2$
    	IMPLICT_OBJECTS_ELS.put("header", parseEl("#{facesContext.externalContext.requestHeaderMap}")); //$NON-NLS-1$ //$NON-NLS-2$
    	IMPLICT_OBJECTS_ELS.put("headerValues", parseEl("#{facesContext.externalContext.requestHeaderValuesMap}")); //$NON-NLS-1$ //$NON-NLS-2$
    	IMPLICT_OBJECTS_ELS.put("initParam", parseEl("#{facesContext.externalContext.initParameterMap}")); //$NON-NLS-1$ //$NON-NLS-2$
    	IMPLICT_OBJECTS_ELS.put("param", parseEl("#{facesContext.externalContext.requestParameterMap}")); //$NON-NLS-1$ //$NON-NLS-2$
    	IMPLICT_OBJECTS_ELS.put("paramValues", parseEl("#{facesContext.externalContext.requestParameterValuesMap}")); //$NON-NLS-1$ //$NON-NLS-2$
    	IMPLICT_OBJECTS_ELS.put("request", parseEl("#{facesContext.externalContext.request}")); //$NON-NLS-1$ //$NON-NLS-2$
    	IMPLICT_OBJECTS_ELS.put("requestScope", parseEl("#{facesContext.externalContext.requestMap}")); //$NON-NLS-1$ //$NON-NLS-2$
//    	IMPLICT_OBJECTS_ELS.put("session", parseEl("#{facesContext.externalContext.getSession()}"));
    	IMPLICT_OBJECTS_ELS.put("session", parseEl("#{facesContext.externalContext.request}")); // FIXME we should fix EL resolving for methods with arguments (e.g. #{facesContext.externalContext.getSession(true)}) See https://jira.jboss.org/jira/browse/JBIDE-4580 //$NON-NLS-1$ //$NON-NLS-2$
    	IMPLICT_OBJECTS_ELS.put("sessionScope", parseEl("#{facesContext.externalContext.sessionMap}")); //$NON-NLS-1$ //$NON-NLS-2$
    	IMPLICT_OBJECTS_ELS.put("view", parseEl("#{facesContext.viewRoot}")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private IFile file; 

    /*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine#resolveVariables(org.eclipse.core.resources.IFile, org.jboss.tools.common.el.core.model.ELInvocationExpression, boolean, boolean)
	 */
	public List<IJSFVariable> resolveVariables(IFile file, ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames) {
		this.file = file;
		return super.resolveVariables(file, expr, isFinal, onlyEqualNames);
	}

    /*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jsf.model.JSFELCompletionEngine#resolveVariables(org.jboss.tools.common.model.project.IModelNature, java.lang.String, boolean)
	 */
	@Override
	protected List<IJSFVariable> resolveVariables(IModelNature project, String varName, boolean onlyEqualNames) {
		if(file.getProject() == null) {
			return null;
		}
		List<IJSFVariable> resolvedVariables = new ArrayList<IJSFVariable>();
		if(onlyEqualNames) {
			if(FACES_CONTEXT.equals(varName)) {
				return getFacesContextVariable();
			}
		} else {
			if(FACES_CONTEXT.startsWith(varName)) {
				resolvedVariables.addAll(getFacesContextVariable());
			}
		}
		Set<String> vars = IMPLICT_OBJECTS_ELS.keySet();
		List<String> elVars = new ArrayList<String>();
		for (String var : vars) {
			if(onlyEqualNames) {
				if(var.equals(varName)) {
					elVars.add(var);
				}
			} else if(var.startsWith(varName)) {
				elVars.add(var);
			}
		}

		for (String var : elVars) {
			try {
				TypeInfoCollector.MemberInfo info = resolveEL(file, IMPLICT_OBJECTS_ELS.get(var), false);
				if(info!=null) {
					IType type = info.getMemberType();
					if(type!=null) {
						resolvedVariables.add(new Variable(var, type));
					}
				}
			} catch (StringIndexOutOfBoundsException e) {
				log(e);
			} catch (BadLocationException e) {
				log(e);
			}
		}
		return resolvedVariables;
	}

	private static ELExpression parseEl(String el) {
		if(el.length()>3 && el.startsWith("#{") && el.endsWith("}")) { //$NON-NLS-1$ //$NON-NLS-2$
			ELParser parser = ELParserUtil.getDefaultFactory().createParser();
			ELModel model = parser.parse(el);
			if(model == null || model.getSyntaxErrors().size() > 0) {
				return null;
			}
			List<ELInstance> is = model.getInstances();
			if(is.size() == 0) {
				return null;
			}
			return is.get(0).getExpression();
		}
		return null;
	}

	private List<IJSFVariable> getFacesContextVariable() {
		IJavaProject jp = EclipseResourceUtil.getJavaProject(file.getProject());
		List<IJSFVariable> list = new ArrayList<IJSFVariable>();
		try {
			IType type = jp.findType("javax.faces.context.FacesContext"); //$NON-NLS-1$
			list.add(new Variable(FACES_CONTEXT, type));
		} catch (JavaModelException e) {
			log(e);
		}
		return list;
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