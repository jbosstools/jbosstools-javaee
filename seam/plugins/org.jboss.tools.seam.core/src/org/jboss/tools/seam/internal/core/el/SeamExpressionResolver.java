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

package org.jboss.tools.seam.internal.core.el;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
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
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELSegment;
import org.jboss.tools.common.el.core.resolver.JavaMemberELSegmentImpl;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector.MemberInfo;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector.Type;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector.TypeInfo;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector.TypeMemberInfo;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentMethod;
import org.jboss.tools.seam.core.ISeamContextShortVariable;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamJavaSourceReference;
import org.jboss.tools.seam.core.ISeamMessages;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamXmlFactory;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamComponentMethodType;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * Utility class used to resolve Seam project variables and to get the methods/properties and their presentation strings from type
 * 
 * @author Jeremy
 */
public class SeamExpressionResolver {

	/**
	 * Returns Seam project variables which names start from specified value
	 *  
	 * @param project
	 * @param scope
	 * @param name
	 * @return
	 */
	public static List<ISeamContextVariable> resolveVariables(ISeamProject project, ScopeType scope, String name, boolean onlyEqualNames) {
		if (project == null || name == null) return null;
		
		ISeamProject parent = project.getParentProject();
		if(parent != null) {
			project = parent;
		}
/*  
 * JBIDE-670 scope isn't used anymore
 */		
//		return (scope == null ? internalResolveVariables(project, name, onlyEqualNames) :
//				internalResolveVariablesByScope(project, scope, name, onlyEqualNames));
		return internalResolveVariables(project, name, onlyEqualNames);
	}
	
	/**
	 * Returns Seam project variables which names start from specified value
	 * No scope used
	 *  
	 * @param project
	 * @param name
	 * @return
	 */
	private static List<ISeamContextVariable> internalResolveVariables(ISeamProject project, String name, boolean onlyEqualNames) {
		Set<ISeamContextVariable> variables = project.getVariables(true);
		return internalResolveVariables(project, name, onlyEqualNames, variables);
	}

	private static List<ISeamContextVariable> internalResolveVariables(ISeamProject project, String name, boolean onlyEqualNames, Set<ISeamContextVariable> variables) {
		List<ISeamContextVariable> resolvedVariables = new ArrayList<ISeamContextVariable>();
		if(onlyEqualNames) {
			variables = project.getVariablesByName(name);
			if(variables != null) resolvedVariables.addAll(variables);
			return resolvedVariables;
		}
		for (ISeamContextVariable variable : variables) {
			String n = variable.getName();
			if(onlyEqualNames) {
				if (n.equals(name)) {
					resolvedVariables.add(variable);
				}
			} else {
				if (n.startsWith(name)) {
					resolvedVariables.add(variable);
				}
			}
		}
		return resolvedVariables;
	}

	/**
	 * Returns the IMember for the variable specified 
	 * 
	 * @param variable
	 * @return
	 */
	public static IMember getMemberByVariable(ISeamContextVariable variable, boolean onlyEqualNames) {
		IMember member = null;
		if(variable instanceof ISeamContextShortVariable) {
			return getMemberByVariable(((ISeamContextShortVariable)variable).getOriginal(), onlyEqualNames);
		}
		if (variable instanceof ISeamComponent) {
			ISeamComponent component = (ISeamComponent)variable;
			ISeamJavaComponentDeclaration decl = component.getJavaDeclaration();
			if (decl != null) {
				member = decl.getSourceMember();
			}
		}
		if (member == null && variable instanceof IBijectedAttribute) {
			member = ((ISeamJavaSourceReference)variable).getSourceMember();
		}
		if (member == null && variable instanceof ISeamJavaSourceReference) {
			member = ((ISeamJavaSourceReference)variable).getSourceMember();
		}
		if (member == null && variable instanceof ISeamXmlFactory) {
			ISeamXmlFactory factory = (ISeamXmlFactory)variable;
			String value = factory.getValue();
			if (value != null && value.length() > 0) {
				if (value.startsWith("#{") || value.startsWith("${")) //$NON-NLS-1$ //$NON-NLS-2$
					value = value.substring(2);
				if (value.endsWith("}")) //$NON-NLS-1$
					value = value.substring(0, value.length() - 1);
			}
			if (value != null && value.length() > 0) {
				// TODO: Need to make sure that it's correct way to get the project and 
				// the scope from the factory 
				ISeamProject project = ((ISeamElement)factory).getSeamProject();
//				ISeamProject project = getSeamProject(factory.getResource());
				if (project != null) {
					List<ISeamContextVariable> resolvedValues = resolveVariables(project, null /* factory.getScope()*/, value, onlyEqualNames);
					for (ISeamContextVariable var : resolvedValues) {
						if (var.getName().equals(value)) {
							member = getMemberByVariable(var, onlyEqualNames);
							break;
						}
					}
				}
			}
		}
		return member;
	}

	/**
	 * This object wraps "messages" context variable. 
	 * @author Alexey Kazakov
	 */
	public static class MessagesInfo extends TypeMemberInfo {

		private ISeamMessages messages;

		/**
		 * @param parentMember
		 * @param messages
		 * @throws JavaModelException
		 */
		protected MessagesInfo(MemberInfo parentMember, ISeamMessages messages) throws JavaModelException {
			super(null, null, messages.getName(), 0, null, null, false, null);
			this.messages = messages;
			IMember member = (IMember)getJavaElement();
			if(member!=null) {
				IType type = member.getDeclaringType();
				if(member instanceof IType) {
					type = (IType)member;
				}
				if(type!=null) {
					setSourceType(type);
					setDeclaringTypeQualifiedName(type==null?null:type.getFullyQualifiedName());
					setName(messages.getName());
					setModifiers(type.getFlags());
					setParentMember(parentMember);
					if(parentMember == null || parentMember instanceof TypeMemberInfo) {
						TypeInfo typeInfo = new TypeInfo(type, null, false);
						setDeclaratedType(typeInfo);
					} else {
						setDeclaratedType((TypeInfo)parentMember);
					}
					setDataModel(false);
					setType(type==null?null:new Type(null, type));
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * @see org.jboss.tools.common.model.util.TypeInfoCollector.MemberInfo#getJavaElement()
		 */
		@Override
		public IJavaElement getJavaElement() {
			if(messages instanceof ISeamJavaSourceReference) {
				return ((ISeamJavaSourceReference)messages).getSourceMember();
			} else if(messages instanceof ISeamComponent) {
				ISeamComponent c = (ISeamComponent)messages;
				ISeamJavaComponentDeclaration d = c.getJavaDeclaration();
				if(d != null) return d.getSourceMember();
			}
			return null;
		}

		/**
		 * @return property
		 */
		public ISeamMessages getMessages() {
			return messages;
		}

		/**
		 * @return keys of resource bundle
		 */
		public Collection<String> getKeys() {
			return messages.getPropertyNames();
		}
	}

	/**
	 * Returns the IMember for the variable specified 
	 * 
	 * @param variable
	 * @return
	 */
	public static TypeInfoCollector.MemberInfo getMemberInfoByVariable(ISeamContextVariable variable, boolean onlyEqualNames, SeamELCompletionEngine engine) {
		TypeInfoCollector.MemberInfo member = null;
		if(variable instanceof ISeamContextShortVariable) {
			return getMemberInfoByVariable(((ISeamContextShortVariable)variable).getOriginal(), onlyEqualNames, engine);
		}
		if(variable instanceof ISeamMessages) {
			MemberInfo info = null;;
			try {
				info = new MessagesInfo(null, (ISeamMessages)variable);
			} catch (JavaModelException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
			return info;
		}
		if (variable instanceof ISeamComponent) {
			ISeamComponent component = (ISeamComponent)variable;

			// Use UNWRAP method type instead of ISeamComponent type if it exists
			IMember unwrapSourceMember = null;
			for (ISeamComponentMethod method : component.getMethods()) {
				if (method.getTypes()!= null && method.getTypes().contains(SeamComponentMethodType.UNWRAP) ) {
					unwrapSourceMember = method.getSourceMember();
					break;
				}
			}
			if (unwrapSourceMember != null) {
				member = TypeInfoCollector.createMemberInfo(unwrapSourceMember);
			} else {
				ISeamJavaComponentDeclaration decl = component.getJavaDeclaration();
				if (decl != null) {
					member = TypeInfoCollector.createMemberInfo(decl.getSourceMember());
				} else {
					// Maybe it is framework component? Then let's try to find the class of it.  
					String className = component.getClassName();
					if(className!=null) {
						IJavaProject project = EclipseResourceUtil.getJavaProject(component.getSeamProject().getProject());
						try {
							IType type = project.findType(className);
							if(type!=null) {
								member = TypeInfoCollector.createMemberInfo(type);
							}
						} catch (JavaModelException e) {
							SeamCorePlugin.getDefault().logError(e);
						}
					}
				}
			}
		}
		if (member == null && variable instanceof IBijectedAttribute) {
			boolean isDataModel = false;
			BijectedAttributeType[] types = ((IBijectedAttribute)variable).getTypes();
			for(int i=0; i<types.length; i++) {
				if(types[i]==BijectedAttributeType.DATA_BINDER) {
					isDataModel = true;
					break;
				}
			}
			member = TypeInfoCollector.createMemberInfo(((ISeamJavaSourceReference)variable).getSourceMember(), isDataModel);
		}
		if (member == null && variable instanceof ISeamJavaSourceReference) {
			member = TypeInfoCollector.createMemberInfo(((ISeamJavaSourceReference)variable).getSourceMember());
		}
		if (member == null && variable instanceof ISeamXmlFactory) {
			ISeamXmlFactory factory = (ISeamXmlFactory)variable;
			String value = factory.getValue();
			if (value != null && value.length() > 0) {
				if (value.startsWith("#{") || value.startsWith("${")) //$NON-NLS-1$ //$NON-NLS-2$
					value = value.substring(2);
				if (value.endsWith("}")) //$NON-NLS-1$
					value = value.substring(0, value.length() - 1);
			}
			if (value != null && value.length() > 0) {
				// TODO: Need to make sure that it's correct way to get the project and 
				// the scope from the factory 
				ISeamProject project = ((ISeamElement)factory).getSeamProject();
//				ISeamProject project = getSeamProject(factory.getResource());
				if (project != null) {
					List<ISeamContextVariable> resolvedValues = resolveVariables(project, null /* factory.getScope()*/, value, onlyEqualNames); 
					for (ISeamContextVariable var : resolvedValues) {
						if (var.getName().equals(value)) {
							member = getMemberInfoByVariable(var, onlyEqualNames, engine);
							break;
						}
					}
				}
			}
			if(member == null) {
				ELParser p = ELParserUtil.getJbossFactory().createParser();
				ELModel m = p.parse(factory.getValue());
				ELInstance i = m.getInstances().isEmpty() ? null : m.getInstances().get(0);
				ELExpression ex = i == null ? null : i.getExpression();
				if(ex instanceof ELInvocationExpression) {
					ELInvocationExpression expr = (ELInvocationExpression)ex;
					try {
						ELResolution resolution = engine.resolveEL(null, expr, false);
						if(resolution.isResolved()) {
							ELSegment segment = resolution.getLastSegment();
							if(segment instanceof JavaMemberELSegmentImpl) {
								member = ((JavaMemberELSegmentImpl)segment).getMemberInfo();
							}
						}
					} catch (StringIndexOutOfBoundsException e) {
						SeamCorePlugin.getDefault().logError(e);
					} catch (BadLocationException e) {
						SeamCorePlugin.getDefault().logError(e);
					}
				}
			}
		}
		return member;
	}
}