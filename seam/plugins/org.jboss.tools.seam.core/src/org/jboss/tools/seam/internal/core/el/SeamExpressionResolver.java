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

import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamJavaSourceReference;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamXmlFactory;
import org.jboss.tools.seam.core.ScopeType;

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
	public static List<ISeamContextVariable> resolveVariables(ISeamProject project, ScopeType scope, String name) {
		if (project == null || name == null) return null;
		return (scope == null ? internalResolveVariables(project, name) :
				internalResolveVariablesByScope(project, scope, name));
	}
	
	/**
	 * Returns Seam project variables which names start from specified value
	 * No scope used
	 *  
	 * @param project
	 * @param name
	 * @return
	 */
	private static List<ISeamContextVariable> internalResolveVariables(ISeamProject project, String name) {
		List<ISeamContextVariable> resolvedVariables = new ArrayList<ISeamContextVariable>();
		Set<ISeamContextVariable> variables = project.getVariables();
		for (ISeamContextVariable variable : variables) {
			if (variable.getName().startsWith(name)) {
				resolvedVariables.add(variable);
			}
		}
		return resolvedVariables;
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
	private static List<ISeamContextVariable> internalResolveVariablesByScope(ISeamProject project, ScopeType scope, String name) {
		List<ISeamContextVariable> resolvedVariables = new ArrayList<ISeamContextVariable>();
		Set<ISeamContextVariable> variables = project.getVariablesByScope(scope);
		for (ISeamContextVariable variable : variables) {
			if (variable.getName().startsWith(name)) {
				resolvedVariables.add(variable);
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
	public static IMember getMemberByVariable(ISeamContextVariable variable) {
		IMember member = null;
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
				if (value.startsWith("#{") || value.startsWith("${"))
					value = value.substring(2);
				if (value.endsWith("}"))
					value = value.substring(0, value.length() - 1);
			}
			if (value != null && value.length() > 0) {
				// TODO: Need to make sure that it's correct way to get the project and 
				// the scope from the factory 
				ISeamProject project = ((ISeamElement)factory).getSeamProject();
//				ISeamProject project = getSeamProject(factory.getResource());
				if (project != null) {
					List<ISeamContextVariable> resolvedValues = resolveVariables(project, factory.getScope(), value);
					for (ISeamContextVariable var : resolvedValues) {
						if (var.getName().equals(value)) {
							member = getMemberByVariable(var);
							break;
						}
					}
				}
			}
		}
		return member;
	}
	
	/**
	 * Returns the methods for the type specified  
	 * 
	 * @param type
	 * @return
	 */
	public static Set<IMember> getMethods(IType type) {
		Set<IMember> methods = new HashSet<IMember>();
		if (type != null) {
			try {
				IMethod[] mthds = type.getMethods();
				for (int i = 0; mthds != null && i < mthds.length; i++) {
					IMethod m = mthds[i];
					if (Modifier.isPublic(m.getFlags()) && 
							(!m.getElementName().startsWith("get") && !m.getElementName().startsWith("set")) ||
							"get".equals(m.getElementName()) || "set".equals(m.getElementName())) {
						methods.add(m);
					}
				}
			} catch (JavaModelException e) {
			}
		}
		return methods;
	}

	/**
	 * Returns the method presentation strings for the type specified  
	 * 
	 * @param type
	 * @return
	 */
	public static Set<String> getMethodPresentations(IType type) {
		Set<String> methods = new HashSet<String>();
		if (type != null) {
			try {
				IMethod[] mthds = type.getMethods();
				for (int i = 0; mthds != null && i < mthds.length; i++) {
					IMethod m = mthds[i];
					if (Modifier.isPublic(m.getFlags()) && 
							(!m.getElementName().startsWith("get") && !m.getElementName().startsWith("set")) ||
							"get".equals(m.getElementName()) || "set".equals(m.getElementName())) {
						
						StringBuffer name = new StringBuffer(m.getElementName());
						
						name.append('(');
						String[] mParams = null;
						mParams = m.getParameterNames();
						for (int j = 0; mParams != null && j < mParams.length; j++) {
							if (j > 0) name.append(", ");
							name.append(mParams[j]);
						}
						name.append(')');
						
						methods.add(name.toString());
					}
				}
			} catch (JavaModelException e) {
			}
		}
		return methods;
	}

	/**
	 * Returns the properties for the type specified  
	 * 
	 * @param type
	 * @return
	 */
	public static Set<IMember> getProperties(IType type) {
		Set<IMember> properties = new HashSet<IMember>(); 
		if (type != null) {
			try {
				IMethod[] props = type.getMethods();
				for (int i = 0; props != null && i < props.length; i++) {
					IMethod m = props[i];
					if (Modifier.isPublic(m.getFlags()) && 
							(m.getElementName().startsWith("get") && !"get".equals(m.getElementName())) ||
							(m.getElementName().startsWith("set") && !"set".equals(m.getElementName()))) {
						properties.add(m);
					}
				}
			} catch (JavaModelException e) {
			}

			try {
				IField[] fields = type.getFields();
				for (int i = 0; fields != null && i < fields.length; i++) {
					IField f = fields[i];
					if (Modifier.isPublic(f.getFlags())) {
						properties.add(f);
					}
				}
			} catch (JavaModelException e) {
			}
		}
		return properties;
	}

	/**
	 * Returns the property presentation strings for the type specified  
	 * 
	 * @param type
	 * @return
	 */
	public static Set<String> getPropertyPresentations(IType type) {
		Set<String> properties = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER); 
		if (type != null) {
			try {
				IMethod[] props = type.getMethods();
				for (int i = 0; props != null && i < props.length; i++) {
					IMethod m = props[i];
					if (Modifier.isPublic(m.getFlags()) && 
							(m.getElementName().startsWith("get") && !"get".equals(m.getElementName())) ||
							(m.getElementName().startsWith("set") && !"set".equals(m.getElementName()))) {
	
						StringBuffer name = new StringBuffer(m.getElementName());
						name.delete(0, 3);
						name.setCharAt(0, Character.toLowerCase(name.charAt(0)));
	
						properties.add(name.toString());
					}
				}
			} catch (JavaModelException e) {
			}

			try {
				IField[] fields = type.getFields();
				for (int i = 0; fields != null && i < fields.length; i++) {
					IField f = fields[i];
					if (Modifier.isPublic(f.getFlags())) {
						properties.add(f.getElementName());
					}
				}
			} catch (JavaModelException e) {
			}
		}
		return properties;
	}
}
