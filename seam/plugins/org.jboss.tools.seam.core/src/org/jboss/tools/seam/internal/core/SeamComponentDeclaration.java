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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.jboss.tools.seam.core.IOpenableElement;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamTextSourceReference;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.IValueInfo;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.event.Change;

/**
 * @author Viacheslav Kabanovich
 */
public abstract class SeamComponentDeclaration extends SeamObject implements ISeamComponentDeclaration, IOpenableElement {

	public static final String PATH_OF_NAME = "name";
	public static final String PATH_OF_SCOPE = "scope";

	/**
	 * Seam component name.
	 */
	protected String name;
	
	protected Map<String,IValueInfo> attributes = new HashMap<String, IValueInfo>();
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setResource(IResource resource) {
		this.resource = resource;
	}

	public int getStartPosition() {
		// TODO Auto-generated method stub
		return 0;
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

		if(!source.equals(d.source)) {
			source = d.source;
		}
		if(!stringsEqual(name, d.name)) {
			changes = Change.addChange(changes, new Change(this, "name", name, d.name));
			name = d.name;
		}
		if(id != d.id) id = d.id;
		
		//be more specific
		this.attributes = d.attributes;
		
		return changes;
	}
	
	boolean stringsEqual(String s1, String s2) {
		return s1 == null ? s2 == null : s1.equals(s2);
	}

	/**
	 * @param path
	 * @return source reference for some member of declaration.
	 * e.g. if you need source reference for @Name you have to 
	 * invoke getLocationFor("name");
	 */
	public ISeamTextSourceReference getLocationFor(String path) {
		final IValueInfo valueInfo = attributes.get(path);
		ISeamTextSourceReference reference = new ISeamTextSourceReference() {
			public int getLength() {
				return valueInfo != null ? valueInfo.getLength() : 0;
			}

			public int getStartPosition() {
				return valueInfo != null ? valueInfo.getStartPosition() : 0;
			}
		};
		return reference;
	}
	
	public void setName(IValueInfo value) {
		attributes.put(ISeamXmlComponentDeclaration.NAME, value);
		name = value == null ? null : value.getValue();
	}
	
	public Set<ISeamContextVariable> getDeclaredVariables() {
		return null;
	}
	
	public void open() {
		
	}

}
