/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.internal.core.impl.ParametedType;
import org.jboss.tools.common.model.project.ext.impl.ValueInfo;
import org.jboss.tools.common.model.util.EclipseJavaUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class MethodDefinition extends BeanMemberDefinition {
	IMethod method;
	boolean isConstructor;

	List<ParameterDefinition> parameters = new ArrayList<ParameterDefinition>();

	public MethodDefinition() {}

	public void setMethod(IMethod method, DefinitionContext context) {
		this.method = method;
		setAnnotatable(method, method.getDeclaringType(), context);
	}

	public IMethod getMethod() {
		return method;
	}

	public boolean isConstructor() {
		return isConstructor();
	}

	protected void init(IType contextType, DefinitionContext context) throws CoreException {
		super.init(contextType, context);
		isConstructor = method.isConstructor();
		//TODO process parameters for disposers and observers
		loadParamDefinitions(contextType, context);
		
	}

	void loadParamDefinitions(IType contextType, DefinitionContext context) throws CoreException {
		if(method == null) return;
		boolean isProducer = getProducesAnnotation() != null;
		String[] parameterNames = method.getParameterNames();
		if(parameterNames == null || parameterNames.length == 0) return;
		if(contextType == null || contextType.isBinary()) return;
		String content = typeDefinition.getContent();
		if(content == null) return;
		ISourceRange range = method.getSourceRange();
		ISourceRange nameRange = method.getNameRange();
		if(nameRange != null) range = nameRange;
		int paramStart = content.indexOf('(', range.getOffset());
		if(paramStart < 0) return;
		int declEnd = content.indexOf('{', paramStart);
		if(declEnd < 0) return;
		int paramEnd = content.lastIndexOf(')', declEnd);
		if(paramEnd < 0) return;
		String paramsString = content.substring(paramStart + 1, paramEnd);
		if(paramsString.indexOf('@') < 0) return;
		String[] params = paramsString.split(",");
		String[] ps = method.getParameterTypes();
		int start = paramStart + 1;

		for (int i = 0; i < params.length; i++) {
			if(params[i].indexOf('@') < 0 && !isProducer) {
				start += params[i].length() + 1;
				continue; //do not need parameters without annotation
			}

			ParameterDefinition pd = new ParameterDefinition();

			ParametedType type = context.getProject().getTypeFactory().getParametedType(contextType, ps[i]);

			pd.methodDefinition = this;
			pd.name = parameterNames[i];
			pd.index = i;
			pd.type = type;
			
			String p = params[i].trim();
			int pi = params[i].indexOf(p);
			
			ValueInfo v = new ValueInfo();
			v.setValue(params[i]);
			v.valueStartPosition = start + pi;
			v.valueLength = p.length();
			pd.setPosition(v);

			StringTokenizer tokens = new StringTokenizer(p, " \r\n\t");
			while (tokens.hasMoreElements()) {
				String q = tokens.nextToken();
				if(!q.startsWith("@")) continue;
				v = new ValueInfo();
				v.setValue(q);
				v.valueStartPosition = start + params[i].indexOf(q);
				v.valueLength = q.length();
				int s = q.indexOf('(');
				if(s >= 0) q = q.substring(0, s).trim();
				String annotationType = EclipseJavaUtil.resolveType(contextType, q.substring(1).trim());
				if(annotationType != null) pd.annotationsByTypeName.put(annotationType, v);
			}
			
			parameters.add(pd);

			start += params[i].length() + 1;			
		}
		
	}

	public boolean isCDIAnnotated() {
		return super.isCDIAnnotated() || isDisposer() || isObserver();
	}

	public List<ParameterDefinition> getParameters() {
		return parameters;
	}

	public boolean isDisposer() {
		for (ParameterDefinition p: parameters) {
			if(p.isAnnotationPresent(CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME)) return true;
		}
		return false;
	}

	public boolean isObserver() {
		for (ParameterDefinition p: parameters) {
			if(p.isAnnotationPresent(CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME)) return true;
		}
		return false;
	}
	

}
