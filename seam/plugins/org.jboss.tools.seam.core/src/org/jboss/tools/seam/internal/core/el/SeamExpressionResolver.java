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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
		return (scope == null ? internalResolveVariables(project, name, onlyEqualNames) :
				internalResolveVariablesByScope(project, scope, name, onlyEqualNames));
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
		List<ISeamContextVariable> resolvedVariables = new ArrayList<ISeamContextVariable>();
		Set<ISeamContextVariable> variables = project.getVariables();
		for (ISeamContextVariable variable : variables) {
			if(onlyEqualNames) {
				if (variable.getName().equals(name)) {
					resolvedVariables.add(variable);
				}
			} else {
				if (variable.getName().startsWith(name)) {
					resolvedVariables.add(variable);
				}
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
	private static List<ISeamContextVariable> internalResolveVariablesByScope(ISeamProject project, ScopeType scope, String name, boolean onlyEqualNames) {
		List<ISeamContextVariable> resolvedVariables = new ArrayList<ISeamContextVariable>();
		Set<ISeamContextVariable> variables = project.getVariablesByScope(scope);
		for (ISeamContextVariable variable : variables) {
			if(onlyEqualNames) {
				if (variable.getName().equals(name)) {
					resolvedVariables.add(variable);
				}
			} else {
				if (variable.getName().startsWith(name)) {
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
					List<ISeamContextVariable> resolvedValues = resolveVariables(project, factory.getScope(), value, onlyEqualNames);
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
				SeamCorePlugin.getDefault().logError(e);
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

						// Add method as 'foo'
						methods.add(name.toString());

						// Add method as 'foo(param1,param2)'
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
				SeamCorePlugin.getDefault().logError(e);
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
				SeamCorePlugin.getDefault().logError(e);
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
				SeamCorePlugin.getDefault().logError(e);
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
		return getPropertyPresentations(type, null);
	}

	/**
	 * Returns the property presentation strings for the type specified  
	 * 
	 * @param type
	 * @param unpairedGettersOrSetters - map of unpaired getters or setters of type's properties. 'key' is property name.
	 * @return
	 */
	public static Set<String> getPropertyPresentations(IType type, Map<String, IMethod> unpairedGettersOrSetters) {
		Set<String> properties = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER); 
		if (type != null) {
			try {
				IMethod[] props = type.getMethods();
				HashMap<String, IMethod> getters = new HashMap<String, IMethod>();
				HashMap<String, IMethod> setters = new HashMap<String, IMethod>();
				for (int i = 0; props != null && i < props.length; i++) {
					IMethod m = props[i];
					if (Modifier.isPublic(m.getFlags())) {
						String methodName = m.getElementName();
						boolean getter = (methodName.startsWith("get") && !"get".equals(methodName)) ||
										 (methodName.startsWith("is") && !"is".equals(methodName));
						boolean setter = methodName.startsWith("set") && !"set".equals(methodName);
						if(getter || setter) {
							StringBuffer name = new StringBuffer(methodName);
							if(methodName.startsWith("i")) {
								name.delete(0, 2);
							} else {
								name.delete(0, 3);
							}
							name.setCharAt(0, Character.toLowerCase(name.charAt(0)));
							String propertyName = name.toString();
							if(!properties.contains(propertyName)) {
								properties.add(propertyName);
							}
							if(unpairedGettersOrSetters!=null) {
								IMethod previousGetter = getters.get(propertyName);
								IMethod previousSetter = setters.get(propertyName);
								if((previousGetter!=null && setter)||(previousSetter!=null && getter)) {
									// We have both Getter and Setter
									unpairedGettersOrSetters.remove(propertyName);
								} else if(setter) {
									setters.put(propertyName, m);
									unpairedGettersOrSetters.put(propertyName, m);
								} else if(getter) {
									getters.put(propertyName, m);
									unpairedGettersOrSetters.put(propertyName, m);
								}
							}
						}
					}
				}
			} catch (JavaModelException e) {
				SeamCorePlugin.getDefault().logError(e);
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
				SeamCorePlugin.getDefault().logError(e);
			}
		}
		return properties;
	}
}