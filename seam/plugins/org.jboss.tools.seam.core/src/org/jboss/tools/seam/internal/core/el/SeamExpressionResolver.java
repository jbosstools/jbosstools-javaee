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
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IMember;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentMethod;
import org.jboss.tools.seam.core.ISeamContextShortVariable;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamJavaSourceReference;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamXmlFactory;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamComponentMethodType;

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

	/**
	 * Returns Seam project variables which names start from specified value
	 * Search is performed using scope
	 *  
	 * @param project
	 * @param scope
	 * @param name
	 * @return
	 */
	private static List<ISeamContextVariable> internalResolveVariablesByScope(ISeamProject project, ScopeType scope, String name, boolean onlyEqualNames) {
		Set<ISeamContextVariable> variables = project.getVariablesByScope(scope, true);
		return internalResolveVariables(project, name, onlyEqualNames, variables);
	}
	
	private static List<ISeamContextVariable> internalResolveVariables(ISeamProject project, String name, boolean onlyEqualNames, Set<ISeamContextVariable> variables) {
		List<ISeamContextVariable> resolvedVariables = new ArrayList<ISeamContextVariable>();
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
	 * Returns the IMember for the variable specified 
	 * 
	 * @param variable
	 * @return
	 */
	public static TypeInfoCollector.MemberInfo getMemberInfoByVariable(ISeamContextVariable variable, boolean onlyEqualNames) {
		TypeInfoCollector.MemberInfo member = null;
		if(variable instanceof ISeamContextShortVariable) {
			return getMemberInfoByVariable(((ISeamContextShortVariable)variable).getOriginal(), onlyEqualNames);
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
				}
			}
		}
		if (member == null && variable instanceof IBijectedAttribute) {
			member = TypeInfoCollector.createMemberInfo(((ISeamJavaSourceReference)variable).getSourceMember());
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
							member = getMemberInfoByVariable(var, onlyEqualNames);
							break;
						}
					}
				}
			}
		}
		return member;
	}

	public static TypeInfoCollector collectTypeInfo(TypeInfoCollector.MemberInfo member) {
		TypeInfoCollector typeInfo = new TypeInfoCollector(member);
		typeInfo.collectInfo();
		return typeInfo;
	}
}