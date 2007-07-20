 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.validation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.w3c.dom.Element;

/**
 * Contains information for seam validators that must be saved between
 * validation invoking.
 * @author Alexey Kazakov
 */
public class SeamValidationContext {

	private Map<String, Set<IPath>> resourcesByVariableName = new HashMap<String, Set<IPath>>();
	private Map<IPath, Set<String>> variableNamesByResource = new HashMap<IPath, Set<String>>();
	private Set<IPath> unnamedResources = new HashSet<IPath>();

	private Set<IFile> removedFiles = new HashSet<IFile>();
	private Set<IFile> registeredResources = new HashSet<IFile>();

	/**
	 * Save link between resource and variable name.
	 * It's needed for incremental validation because we must save all linked resources of changed java file.
	 */
	public void addLinkedResource(String variableName, IPath linkedResourcePath) {
		if(linkedResourcePath==null) {
			throw new RuntimeException("Linked resource path must not be null!");
		}
		if(variableName==null) {
			throw new RuntimeException("Variable name must not be null!");
		}
		Set<IPath> linkedResources = resourcesByVariableName.get(variableName);
		if(linkedResources==null) {
			// create set of linked resources with variable name.
			linkedResources = new HashSet<IPath>();
			resourcesByVariableName.put(variableName, linkedResources);
		}
		// save linked resources.
		linkedResources.add(linkedResourcePath);

		// Save link between resource and variable names. It's needed if variable name changes in resource file.
		Set<String> variableNames = variableNamesByResource.get(linkedResourcePath);
		if(variableNames==null) {
			variableNames = new HashSet<String>();
			variableNamesByResource.put(linkedResourcePath, variableNames);
		}
		variableNames.add(variableName);
	}

	/**
	 * Removes link between resource and variable name.
	 * @param oldVariableName
	 * @param linkedResourcePath
	 */
	public void removeLinkedResource(String name, IPath linkedResourcePath) {
		Set<IPath> linkedResources = resourcesByVariableName.get(name);
		if(linkedResources!=null) {
			// remove linked resource.
			linkedResources.remove(linkedResourcePath);
		}
		// Remove link between resource and variable names.
		Set<String> variableNames = variableNamesByResource.get(linkedResourcePath);
		if(variableNames!=null) {
			variableNames.remove(name);
		}
	}

	/**
	 * Removes link between resources and variable names.
	 * @param linkedResources
	 */
	public void removeLinkedResources(Set<IPath> resources) {
		for (IPath resource : resources) {
			Set<String> resourceNames = variableNamesByResource.get(resource);
			if(resourceNames!=null) {
				for (String name : resourceNames) {
					Set<IPath> linkedResources = resourcesByVariableName.get(name);
					if(linkedResources!=null) {
						linkedResources.remove(resource);
					}
				}
			}
			variableNamesByResource.remove(resource);
		}
	}

	public Set<IPath> getResourcesByVariableName(String variableName) {
		return resourcesByVariableName.get(variableName);
	}

	public Set<String> getVariableNamesByResource(IPath fullPath) {
		return variableNamesByResource.get(fullPath);
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

	public void clear() {
		resourcesByVariableName.clear();
		variableNamesByResource.clear();
		unnamedResources.clear();
		removedFiles.clear();
		registeredResources.clear();
	}

	public void store(Element root) {
		Element validation = XMLUtilities.createElement(root, "validation");
		Set<String> variables = resourcesByVariableName.keySet();
		for (String name: variables) {
			Set<IPath> paths = resourcesByVariableName.get(name);
			if(paths == null) continue;
			for (IPath path: paths) {
				Element linkedResource = XMLUtilities.createElement(validation, "linked-resource");
				linkedResource.setAttribute("name", name);
				linkedResource.setAttribute("path", path.toString());
			}
		}
		for (IPath unnamedPath: unnamedResources) {
			Element unnamedPathElement = XMLUtilities.createElement(validation, "unnamed-path");
			unnamedPathElement.setAttribute("path", unnamedPath.toString());
		}
	}

	public void load(Element root) {
		Element validation = XMLUtilities.getUniqueChild(root, "validation");
		if(validation == null) return;
		Element[] linkedResources = XMLUtilities.getChildren(validation, "linked-resource");
		if(linkedResources != null) for (int i = 0; i < linkedResources.length; i++) {
			String name = linkedResources[i].getAttribute("name");
			if(name == null || name.trim().length() == 0) continue;
			String path = linkedResources[i].getAttribute("path");
			if(path == null || path.trim().length() == 0) continue;
			try {
				IPath pathObject = new Path(path);
				addLinkedResource(name, pathObject);
			} catch (Exception e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
		}
		Element[] unnamedPathElement = XMLUtilities.getChildren(validation, "unnamed-path");
		if(unnamedPathElement != null) for (int i = 0; i < unnamedPathElement.length; i++) {
			String path = unnamedPathElement[i].getAttribute("path");
			try {
				IPath pathObject = new Path(path);
				addUnnamedResource(pathObject);
			} catch (Exception e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
		}
	}

	public Set<IFile> getRemovedFiles() {
		return removedFiles;
	}

	public void addRemovedFile(IFile file) {
		removedFiles.add(file);
	}

	public Set<IFile> getRegisteredFiles() {
		return registeredResources;
	}

	public void registerFile(IFile file) {
		registeredResources.add(file);
	}
}