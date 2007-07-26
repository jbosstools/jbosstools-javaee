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
package org.jboss.tools.seam.internal.core;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.event.Change;

/**
 * @author Viacheslav Kabanovich
 */
public abstract class SeamComponentDeclaration extends AbstractSeamDeclaration implements ISeamComponentDeclaration {

	public static final String PATH_OF_SCOPE = "scope";
	
	public void setResource(IResource resource) {
		this.resource = resource;
	}

	public ScopeType getScope() {
		return ScopeType.UNSPECIFIED;
	}
	
	/**
	 * Merges loaded data into currently used declaration.
	 * If changes were done returns a list of changes. 
	 * @param d
	 * @return list of changes
	 */
	public List<Change> merge(SeamObject s) {
		List<Change> changes = super.merge(s);
		SeamComponentDeclaration d = (SeamComponentDeclaration)s;

		source = d.source;
		if(!stringsEqual(name, d.name)) {
			changes = Change.addChange(changes, new Change(this, "name", name, d.name));
			name = d.name;
		}
		if(id != d.id) id = d.id;
		
		//be more specific
		this.attributes = d.attributes;
		
		return changes;
	}
	
	public Set<ISeamContextVariable> getDeclaredVariables() {
		return null;
	}
	
}
