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

	private Map<String, Set<IPath>> markedNonuniqueNamedResources = new HashMap<String, Set<IPath>>();
	private Map<IPath, String> nonuniqueNames = new HashMap<IPath, String>();

	/**
	 * Save linked resources of component name that we marked.
	 * It's needed for incremental validation because we must save all linked resources of changed java file.
	 */
	public void addLinkedResource(String componentName, IPath linkedResourcePath) {
		Set<IPath> linkedResources = markedNonuniqueNamedResources.get(componentName);
		if(linkedResources==null) {
			// create set of linked resources with component name that we must mark.
			linkedResources = new HashSet<IPath>();
			markedNonuniqueNamedResources.put(componentName, linkedResources);
		}
		if(!linkedResources.contains(linkedResourcePath)) {
			// save linked resources that we must mark.
			linkedResources.add(linkedResourcePath);
		}
		// Save link between component name and marked resource. It's needed if component name changes in java file.
		nonuniqueNames.put(linkedResourcePath, componentName);
	}

	public Set<IPath> getMarkedNonuniqueNamedResources(String componentName) {
		return markedNonuniqueNamedResources.get(componentName);
	}

	public String getNonuniqueNameOfComponent(IPath sourcePath) {
		return nonuniqueNames.get(sourcePath);
	}
}