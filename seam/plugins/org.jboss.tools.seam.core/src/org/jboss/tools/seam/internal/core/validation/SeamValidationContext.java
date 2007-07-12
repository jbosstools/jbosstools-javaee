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

import org.eclipse.core.runtime.IPath;

/**
 * Contains information for seam validators that must be saved between
 * validation invoking.
 * @author Alexey Kazakov
 */
public class SeamValidationContext {

	private Map<String, Set<IPath>> resourcesByVariableName = new HashMap<String, Set<IPath>>();
	private Map<IPath, Set<String>> variableNamesByResource = new HashMap<IPath, Set<String>>();

	/**
	 * Save link between resource and variable name.
	 * It's needed for incremental validation because we must save all linked resources of changed java file.
	 */
	public void addLinkedResource(String variableName, IPath linkedResourcePath) {
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
	public void removeLinkedResource(String oldVariableName, IPath linkedResourcePath) {
		Set<IPath> linkedResources = resourcesByVariableName.get(oldVariableName);
		if(linkedResources!=null) {
			// remove linked resource.
			linkedResources.remove(linkedResourcePath);
		}
		// Remove link between resource and variable names.
		Set<String> variableNames = variableNamesByResource.get(linkedResourcePath);
		if(variableNames!=null) {
			variableNames.remove(oldVariableName);
		}
	}

	public Set<IPath> getResourcesByVariableName(String variableName) {
		return resourcesByVariableName.get(variableName);
	}

	public Set<String> getVariableNamesByResource(IPath sourcePath) {
		return variableNamesByResource.get(sourcePath);
	}

	public void clear() {
		resourcesByVariableName.clear();
		variableNamesByResource.clear();
	}
}