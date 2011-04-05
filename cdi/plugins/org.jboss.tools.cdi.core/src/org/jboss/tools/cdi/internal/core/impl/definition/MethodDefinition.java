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
package org.jboss.tools.cdi.internal.core.impl.definition;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;
import org.jboss.tools.cdi.internal.core.impl.ClassBean;
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

	public void setMethod(IMethod method, IRootDefinitionContext context) {
		this.method = method;
		setAnnotatable(method, method.getDeclaringType(), context);
	}

	public IMethod getMethod() {
		return method;
	}

	public boolean isConstructor() {
		return isConstructor;
	}

	protected void init(IType contextType, IRootDefinitionContext context) throws CoreException {
		super.init(contextType, context);
		isConstructor = method.isConstructor();
		//TODO process parameters for disposers and observers
		loadParamDefinitions(contextType, context);
	}

	public boolean parametersAreInjectionPoints() {
		return getProducesAnnotation() != null || getInjectAnnotation() != null;
	}

	void loadParamDefinitions(IType contextType, IRootDefinitionContext context) throws CoreException {
		if(method == null) return;
		boolean parametersAreInjectionPoints = parametersAreInjectionPoints();
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
		if(!parametersAreInjectionPoints && paramsString.indexOf("@Observes") >= 0) {
			parametersAreInjectionPoints = true;
		}
		if(!parametersAreInjectionPoints && paramsString.indexOf('@') < 0) return;
		String[] params = getParams(paramsString);
		String[] ps = method.getParameterTypes();
		int start = paramStart + 1;

		for (int i = 0; i < params.length; i++) {
			if(ps.length <= i) {
				// CDICorePlugin.getDefault().logError(new IllegalArgumentException("Cannot parse method parameters for " + paramsString));
				// The source code may be broken. Just ignore such errors.
				break;
			}
			if(!parametersAreInjectionPoints && params[i].indexOf('@') < 0) {
				start += params[i].length() + 1;
				continue; //do not need parameters without annotation
			}

			ParameterDefinition pd = new ParameterDefinition();

			ParametedType type = context.getProject().getTypeFactory().getParametedType(method, ps[i]);

			pd.methodDefinition = this;
			pd.name = parameterNames[i];
			pd.index = i;
			pd.type = type;
			
			String p = params[i].trim();
			int pi = params[i].indexOf(p);
			
			ValueInfo v = new CheckingValueInfo(method);
			v.setValue(params[i]);
			v.valueStartPosition = start + pi;
			v.valueLength = p.length();
			pd.setPosition(v);

			String[] tokens = getParamTokens(p);
			for (String q: tokens) {
				if(!q.startsWith("@")) continue;
				v = new CheckingValueInfo(method);
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

	@Override
	public boolean isCDIAnnotated() {
		return super.isCDIAnnotated() || isDisposer() || isObserver() || getPreDestroyMethod() != null || getPostConstructorMethod() != null || !getInterceptorBindings().isEmpty() || hasStereotypeDeclarations();
	}

	public Set<IInterceptorBinding> getInterceptorBindings() {
		Set<IInterceptorBinding> result = new HashSet<IInterceptorBinding>();
		Set<IInterceptorBindingDeclaration> declarations = ClassBean.getInterceptorBindingDeclarations(this);
		for (IInterceptorBindingDeclaration declaration: declarations) {
			result.add(declaration.getInterceptorBinding());
		}
		return result;
	}

	public boolean hasStereotypeDeclarations() {
		List<IAnnotationDeclaration> as = getAnnotations();
		for (IAnnotationDeclaration a: as) {
			if(a instanceof IStereotypeDeclaration) {
				return true;
			}
		}
		return false;
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

	public AnnotationDeclaration getPreDestroyMethod() {
		return annotationsByType.get(CDIConstants.PRE_DESTROY_TYPE_NAME);
	}

	public AnnotationDeclaration getPostConstructorMethod() {
		return annotationsByType.get(CDIConstants.POST_CONSTRUCTOR_TYPE_NAME);
	}

	static String[] getParams(String paramsString) {
		List<String> result = new ArrayList<String>();
		int i = 0;
		int c1 = 0;
		int c2 = 0;
		char quote = '\0';
		StringBuffer sb = new StringBuffer();
		while(i < paramsString.length()) {
			char c = paramsString.charAt(i);
			if(c == ',' && c1 == 0 && c2 == 0 && quote == '\0') {
				if(sb.toString().trim().length() > 0) {
					result.add(sb.toString());
				}
				sb.setLength(0);
				i++;
				continue;
			} else if(c == '(' && quote == '\0') {
				c1++;
			} else if(c == ')' && quote == '\0') {
				c1--;
			} else if(c == '<' && quote == '\0') {
				c2++;
			} else if(c == '>' && quote == '\0') {
				c2--;
			} else if((c == '\'' || c == '"') && quote == '\0') {
				quote = c;
			} else if(quote == c) {
				quote = '\0';
			}
			sb.append(c);
			i++;
		}
		if(sb.length() > 0) {
			result.add(sb.toString());
		}
		return result.toArray(new String[0]);
	}

	static String[] getParamTokens(String paramsString) {
		List<String> result = new ArrayList<String>();
		int i = 0;
		int c1 = 0;
		int c2 = 0;
		char quote = '\0';
		StringBuffer sb = new StringBuffer();
		while(i < paramsString.length()) {
			char c = paramsString.charAt(i);
			boolean ws = Character.isWhitespace(c);
			if(ws && c1 == 0 && c2 == 0 && quote == '\0') {
				String t = sb.toString().trim();
				if(t.length() > 0) {
					result.add(t);
				}
				sb.setLength(0);
				i++;
				continue;
			} else if(c == '(' && quote == '\0') {
				c1++;
			} else if(c == ')' && quote == '\0') {
				c1--;
			} else if(c == '<' && quote == '\0') {
				c2++;
			} else if(c == '>' && quote == '\0') {
				c2--;
			} else if((c == '\'' || c == '"') && quote == '\0') {
				quote = c;
			} else if(quote == c) {
				quote = '\0';
			}
			sb.append(c);
			i++;
		}
		if(sb.length() > 0) {
			result.add(sb.toString());
		}
		return result.toArray(new String[0]);
	}

	class CheckingValueInfo extends ValueInfo {
		IMethod m;
		CheckingValueInfo(IMethod m) {
			this.m = m;
		}
		
		void check() {
			ISourceRange r = null;
			try {
				r = m.getSourceRange();
			} catch (JavaModelException e) {
				System.out.println("Method is obsolete: " + m);
			}
			if(r == null) {
				valueStartPosition = 0;
				valueLength = 0;
			} else {
				if(valueStartPosition + valueLength > r.getOffset() + r.getLength()) {
					System.out.println("Method is modified: " + m);
					valueStartPosition = 0;
					valueLength = 0;
				}
			}
			
		}
		
		public int getStartPosition() {
			check();
			return valueStartPosition;
		}

		public int getLength() {
			check();
			return valueLength;
		}

	}

}
