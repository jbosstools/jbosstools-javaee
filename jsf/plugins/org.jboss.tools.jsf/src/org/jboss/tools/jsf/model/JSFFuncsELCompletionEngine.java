/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.el.core.ELCorePlugin;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector.ArtificialTypeInfo;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.kb.internal.XmlContextImpl;
import org.jboss.tools.jst.web.kb.taglib.IELFunction;
import org.jboss.tools.jst.web.kb.taglib.IFunctionLibrary;
import org.jboss.tools.jst.web.kb.taglib.INameSpace;
import org.jboss.tools.jst.web.kb.taglib.ITagLibrary;
import org.jboss.tools.jst.web.kb.taglib.TagLibraryManager;

public class JSFFuncsELCompletionEngine extends JSFELCompletionEngine {
	private static final Image JSF_EL_PROPOSAL_IMAGE = JSFModelPlugin.getDefault().getImage(JSFModelPlugin.CA_JSF_EL_IMAGE_PATH);

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine#getELProposalImage()
	 */
	public Image getELProposalImage() {
		return JSF_EL_PROPOSAL_IMAGE;
	}

	public JSFFuncsELCompletionEngine() {}
	
	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine#getMemberInfoByVariable(org.jboss.tools.jst.web.kb.el.AbstractELCompletionEngine.IVariable, boolean)
	 */
	protected TypeInfoCollector.MemberInfo getMemberInfoByVariable(IJSFVariable var, boolean onlyEqualNames, int offset) {
		// Need to create artificial member info based on the Source Member type, but having only method named after the func's name
		if (!(var instanceof Variable))
			return null;
		
		Variable variable = (Variable)var;
		IType sourceMember = (IType)variable.getSourceMember();
		
		if (variable.funcResolvedMethod == null)
			return null;
		
		TypeInfoCollector.MemberInfo result = null;
		
		try {
			result = new ArtificialTypeInfo(sourceMember, 
					variable.funcResolvedMethod, 
					variable.funcName);
		} catch (JavaModelException e) {
			ELCorePlugin.getPluginLog().logError(e);
		}

		return result;		
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.el.core.ca.AbstractELCompletionEngine#resolveVariables(org.eclipse.core.resources.IFile, org.jboss.tools.common.el.core.model.ELInvocationExpression, boolean, boolean)
	 */
	@Override
	public List<IJSFVariable> resolveVariables(IFile file,
			ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames, int offset) {
		return resolveVariablesInternal(file, expr, isFinal, onlyEqualNames, offset);
	}

	public List<IJSFVariable> resolveVariablesInternal(IFile file, ELInvocationExpression expr, boolean isFinal, boolean onlyEqualNames, int offset) {
		ELContext context = PageContextFactory.createPageContext(file);

		if (!(context instanceof XmlContextImpl)) {
			return null;
		}
			
		ITagLibrary[] libraries = TagLibraryManager.getLibraries(file.getProject());
		if (libraries == null)
			return null;

		List<IJSFVariable> result = new ArrayList<IJSFVariable>();
		
		
		String varName = expr.toString();
		
		// AbstractELCompletionEngine sets up a current offset in beginning of each ELResolver method (if it has appropriate parameter)
		// but if resolve variables is called (because it is public) from somewhere outside, then we'll use all the namespaces we could find 
		Map<String, List<INameSpace>> namespacesByOffset = ((XmlContextImpl)context).getNameSpaces(offset);

		for (ITagLibrary l : libraries) {
			if (l instanceof IFunctionLibrary) {
				String uri = l.getURI();
				Collection<INameSpace> namespaces = namespacesByOffset.get(uri);
				if (namespaces != null) {
					for (INameSpace ns : namespaces) {
						String name = ns.getPrefix();
						if(!isFinal || onlyEqualNames) {
							if(!name.equals(varName)) continue;
						}
						if(!name.startsWith(varName)) continue;
						if(varName.lastIndexOf('.') > name.length()) continue; //It is the java variable case
						IELFunction[] functions = ((IFunctionLibrary)l).getFunctions();
						if (functions == null) continue;
						for (IELFunction f : functions) {
							String funcClass = f.getFunctionClass();
							String funcSignature = f.getFunctionSignature();
							String funcName = f.getName();
							Variable v = new Variable(name, file,funcName, funcClass, funcSignature);
							result.add(v);
						}
					}
				}
			}
		}

		return result;
	}

	protected void setImage(TextProposal kbProposal) {
		kbProposal.setImage(getELProposalImage());
	}

	static class Variable implements IJSFVariable {
		IFile f;
		String name;
		String funcName;
		String funcClass;
		String funcSignature;
		IMethod funcResolvedMethod;
		IType  funcSourceMember;

		public Variable(String name, IFile f, String funcName, String funcClass, String funcSignature) {
			this.name = name;
			this.f = f;
			this.funcName = funcName;
			this.funcClass = funcClass;
			this.funcSignature = funcSignature;
			this.funcResolvedMethod = null;
		}

		public String getName() {
			return name;
		}

		public Collection<String> getKeys() {
			List<String> result = new ArrayList<String>();
			
			if (funcResolvedMethod != null)
				return result;

			if (f == null || f.getProject() == null)
				return result;

			funcSourceMember = EclipseResourceUtil.getValidType(f.getProject(), funcClass);
			if (funcSourceMember == null)
				return result;

		
			IType currentType = funcSourceMember;
			try {
			while (currentType != null) {
				IMethod[] binMethods = currentType.getMethods();
				if (binMethods != null) {
					for (IMethod method : binMethods) {
						if (method.isConstructor() || (method.getFlags() & Flags.AccStatic) == 0) {
							continue;
						}
						
						String methodName = method.getElementName();
						String methodReturnType = method.getReturnType();
						String methodReturnTypeSimple = methodReturnType;
						if (Signature.getTypeSignatureKind(methodReturnType) == Signature.BASE_TYPE_SIGNATURE) {
							methodReturnType = Signature.toString(methodReturnType);
							methodReturnTypeSimple = methodReturnType;
						} else {
							methodReturnType = EclipseJavaUtil.resolveTypeAsString(currentType, methodReturnType);
							methodReturnTypeSimple = Signature.getSimpleName(methodReturnType);
						}
												
						String[] methodParamTypes = method.getParameterTypes();
						int paramTypesCount = methodParamTypes == null ? 0 : methodParamTypes.length;
						int startParamIndex = funcSignature.indexOf('(');
						int endParamIndex = funcSignature.indexOf(')');
						if (startParamIndex == -1 || endParamIndex == -1 || startParamIndex > endParamIndex)
							continue;
						String paramsString = funcSignature.substring(startParamIndex + 1, endParamIndex);
						String[] params = paramsString.split(",");
						if (!(funcSignature.substring(0, startParamIndex).contains(methodReturnType) || funcSignature.substring(0, startParamIndex).contains(methodReturnTypeSimple)))
							continue;
						if (!funcSignature.substring(0, startParamIndex).contains(methodName))
							continue;
						int paramsCount = params == null ? 0 : params.length;
						if (paramTypesCount != paramsCount)
							continue;
						boolean paramsAreEqual = true;
						for (int i = 0; methodParamTypes != null && i < methodParamTypes.length; i++) {
							String methodParamType = methodParamTypes[i];
							String methodParamTypeSimple = methodParamType;
							if (Signature.getTypeSignatureKind(methodParamType) == Signature.BASE_TYPE_SIGNATURE) {
								methodParamType = Signature.toString(methodParamType);
								methodReturnTypeSimple = methodParamType;
							} else {
								methodParamType = EclipseJavaUtil.resolveTypeAsString(currentType, methodParamType);
								methodParamTypeSimple = Signature.getSimpleName(methodParamType);
							}
							
							if (params[i] == null || (!params[i].trim().equals(methodParamType) && !params[i].trim().equals(methodParamTypeSimple))) {
								paramsAreEqual = false;
								break;
							}
						}
						
						if (!paramsAreEqual)
							continue;
						
						funcResolvedMethod = method;
						result.add(funcName);
						break;
					}
				}
				currentType = TypeInfoCollector.getSuperclass(currentType);
			}
			} catch (JavaModelException e) {
				JSFModelPlugin.log("An error occurred while retrieving methods for type '" + funcClass + "'", e);
			}

			return result;
		}

		public IMember getSourceMember() {
			getKeys(); // Initialize source member
			return funcSourceMember;
		}
	}
}
