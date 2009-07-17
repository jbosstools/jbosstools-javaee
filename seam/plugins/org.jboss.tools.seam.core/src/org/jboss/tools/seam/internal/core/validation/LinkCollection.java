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
package org.jboss.tools.seam.internal.core.validation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.w3c.dom.Element;

/**
 * @author Alexey Kazakov
 */
public class LinkCollection {
	protected Map<String, Set<IPath>> resourcesByVariableName = new HashMap<String, Set<IPath>>();
	protected Map<IPath, Set<String>> variableNamesByResource = new HashMap<IPath, Set<String>>();
	protected Map<String, Set<IPath>> resourcesByDeclaringVariableName = new HashMap<String, Set<IPath>>();
	protected Map<IPath, Set<String>> declaringVariableNamesByResource = new HashMap<IPath, Set<String>>();
	protected Set<IPath> unnamedResources = new HashSet<IPath>();

	/**
	 * Save link between resource and variable name.
	 * It's needed for incremental validation because we must save all linked resources of changed java file.
	 */
	public void addLinkedResource(String variableName, IPath linkedResourcePath, boolean declaration) {
		if(linkedResourcePath==null) {
			throw new IllegalArgumentException(SeamCoreMessages.SEAM_VALIDATION_CONTEXT_LINKED_RESOURCE_PATH_MUST_NOT_BE_NULL);
		}
		if(variableName==null) {
			throw new IllegalArgumentException(SeamCoreMessages.SEAM_VALIDATION_CONTEXT_VARIABLE_NAME_MUST_NOT_BE_NULL);
		}

		synchronized(this) {
			Set<IPath> linkedResources = resourcesByVariableName.get(variableName);
			if(linkedResources==null) {
				// create set of linked resources with variable name.
				linkedResources = new HashSet<IPath>();
				resourcesByVariableName.put(variableName, linkedResources);
			}
			// save linked resources.
			linkedResources.add(linkedResourcePath);
		}

		// Save link between resource and variable names. It's needed if variable name changes in resource file.
		Set<String> variableNames = variableNamesByResource.get(linkedResourcePath);
		if(variableNames==null) {
			variableNames = new HashSet<String>();
			variableNamesByResource.put(linkedResourcePath, variableNames);
		}
		variableNames.add(variableName);

		if(declaration) {
			synchronized(this) {
				Set<IPath> linkedResources = resourcesByDeclaringVariableName.get(variableName);
				if(linkedResources==null) {
					// create set of linked resources with declaring variable name.
					linkedResources = new HashSet<IPath>();
					resourcesByDeclaringVariableName.put(variableName, linkedResources);
				}
				// save linked resources.
				linkedResources.add(linkedResourcePath);
			}

			// Save link between resource and declaring  variable names. It's needed if variable name changes in resource file.
			variableNames = declaringVariableNamesByResource.get(linkedResourcePath);
			if(variableNames==null) {
				variableNames = new HashSet<String>();
				declaringVariableNamesByResource.put(linkedResourcePath, variableNames);
			}
			variableNames.add(variableName);
		}
	}

	/**
	 * Removes link between resource and variable name.
	 * @param oldVariableName
	 * @param linkedResourcePath
	 */
	public void removeLinkedResource(String name, IPath linkedResourcePath) {
		synchronized(this) {
			Set<IPath> linkedResources = resourcesByVariableName.get(name);
			if(linkedResources!=null) {
				// remove linked resource.
				linkedResources.remove(linkedResourcePath);
			}
			if(linkedResources.isEmpty()) {
				resourcesByVariableName.remove(name);
			}
		}
		// Remove link between resource and declaring variable names.
		Set<String> variableNames = variableNamesByResource.get(linkedResourcePath);
		if(variableNames!=null) {
			variableNames.remove(name);
		}
		if(variableNames.isEmpty()) {
			variableNamesByResource.remove(linkedResourcePath);
		}
		synchronized(this) {
			Set<IPath> linkedResources = resourcesByDeclaringVariableName.get(name);
			if(linkedResources!=null) {
				// remove linked resource.
				linkedResources.remove(linkedResourcePath);
			}
			if(linkedResources.isEmpty()) {
				resourcesByDeclaringVariableName.remove(name);
			}
		}
		// Remove link between resource and declaring variable names.
		variableNames = declaringVariableNamesByResource.get(linkedResourcePath);
		if(variableNames!=null) {
			variableNames.remove(name);
		}
		if(variableNames.isEmpty()) {
			declaringVariableNamesByResource.remove(linkedResourcePath);
		}
	}

	/**
	 * Removes link between resources and variable names.
	 * @param linkedResources
	 */
	public void removeLinkedResources(Set<IPath> resources) {
		for (IPath resource : resources) {
			removeLinkedResource(resource);
		}
	}

	/**
	 * Removes link between resource and variable names.
	 * @param linkedResources
	 */
	public synchronized void removeLinkedResource(IPath resource) {
		Set<String> resourceNames = variableNamesByResource.get(resource);
		if(resourceNames!=null) {
			for (String name : resourceNames) {
				Set<IPath> linkedResources = resourcesByVariableName.get(name);
				if(linkedResources!=null) {
					linkedResources.remove(resource);
					if(linkedResources.isEmpty()) {
						resourcesByVariableName.remove(name);
					}
				}
			}
		}
		variableNamesByResource.remove(resource);

		resourceNames = declaringVariableNamesByResource.get(resource);
		if(resourceNames!=null) {
			for (String name : resourceNames) {
				Set<IPath> linkedResources = resourcesByDeclaringVariableName.get(name);
				if(linkedResources!=null) {
					linkedResources.remove(resource);
					if(linkedResources.isEmpty()) {
						resourcesByDeclaringVariableName.remove(name);
					}
				}
			}
		}
		declaringVariableNamesByResource.remove(resource);
	}

	public Set<IPath> getResourcesByVariableName(String variableName, boolean declaration) {
		return declaration?resourcesByDeclaringVariableName.get(variableName):resourcesByVariableName.get(variableName);
	}

	public synchronized Set<String> getVariableNamesByResource(IPath fullPath, boolean declaration) {
		return declaration?declaringVariableNamesByResource.get(fullPath):variableNamesByResource.get(fullPath);
	}

	/**
	 * Adds resource without any link to any context variable name.
	 * @param fullPath
	 */
	public void addUnnamedResource(IPath fullPath) {
		unnamedResources.add(fullPath);
	}

	/**
	 * @return Set of resources without any link to any context variable name.
	 * @param fullPath
	 */
	public Set<IPath> getUnnamedResources() {
		return unnamedResources;
	}

	/**
	 * Removes unnamed resource.
	 * @param fullPath
	 */
	public void removeUnnamedResource(IPath fullPath) {
		unnamedResources.remove(fullPath);
	}

	/**
	 * Clear all references
	 */
	public synchronized void clearAll() {
		resourcesByVariableName.clear();
		variableNamesByResource.clear();
		declaringVariableNamesByResource.clear();
		resourcesByDeclaringVariableName.clear();
		unnamedResources.clear();
	}

	/**
	 * Store the collection to XML
	 * @param root
	 */
	public synchronized void store(Element root) {
		Set<String> variables = resourcesByVariableName.keySet();
		for (String name: variables) {
			Set<IPath> paths = resourcesByVariableName.get(name);
			if(paths == null) continue;
			for (IPath path: paths) {
				Element linkedResource = XMLUtilities.createElement(root, "linked-resource"); //$NON-NLS-1$
				linkedResource.setAttribute("name", name); //$NON-NLS-1$
				linkedResource.setAttribute("path", path.toString()); //$NON-NLS-1$
				if(checkDeclaration(path, name)) {
					linkedResource.setAttribute("declaration", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		for (IPath unnamedPath: unnamedResources) {
			Element unnamedPathElement = XMLUtilities.createElement(root, "unnamed-path"); //$NON-NLS-1$
			unnamedPathElement.setAttribute("path", unnamedPath.toString()); //$NON-NLS-1$
		}
	}

	private boolean checkDeclaration(IPath resource, String variableName) {
		Set<IPath> paths = resourcesByDeclaringVariableName.get(variableName);
		if(paths!=null) {
			for (IPath path : paths) {
				if(path.equals(resource)) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * Load the collection from XML
	 * @param root
	 */
	public void load(Element root) {
		if(root == null) return;
		Element[] linkedResources = XMLUtilities.getChildren(root, "linked-resource"); //$NON-NLS-1$
		if(linkedResources != null) for (int i = 0; i < linkedResources.length; i++) {
			String name = linkedResources[i].getAttribute("name"); //$NON-NLS-1$
			if(name == null || name.trim().length() == 0) continue;
			String path = linkedResources[i].getAttribute("path"); //$NON-NLS-1$
			if(path == null || path.trim().length() == 0) continue;
			String declaration = linkedResources[i].getAttribute("declaration"); //$NON-NLS-1$
			boolean declarationFlag = "true".equals(declaration); //$NON-NLS-1$
			IPath pathObject = new Path(path);
			addLinkedResource(name, pathObject, declarationFlag);
		}
		Element[] unnamedPathElement = XMLUtilities.getChildren(root, "unnamed-path"); //$NON-NLS-1$
		if(unnamedPathElement != null) for (int i = 0; i < unnamedPathElement.length; i++) {
			String path = unnamedPathElement[i].getAttribute("path"); //$NON-NLS-1$
			IPath pathObject = new Path(path);
			addUnnamedResource(pathObject);
		}
	}
}