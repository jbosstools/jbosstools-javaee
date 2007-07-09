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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamTextSourceReference;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.event.Change;
import org.jboss.tools.seam.internal.core.scanner.java.ValueInfo;

/**
 * @author Viacheslav Kabanovich
 */
public abstract class SeamComponentDeclaration implements ISeamComponentDeclaration {

	public static final String PATH_OF_NAME = "name";

	/**
	 * Path of resource where this component is declared.
	 */
	protected IPath source;
	
	protected IResource resource;

	/**
	 * Object that allows to identify this declaration.
	 */
	protected Object id;

	/**
	 * Seam component name.
	 */
	protected String name;
	
	protected Map<String,ValueInfo> attributes = new HashMap<String, ValueInfo>();
	
	public Object getId() {
		return id;
	}
	
	public void setId(Object id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSourcePath(IPath path) {
		source = path;
	}
	
	public IPath getSourcePath() {
		return source;
	}

	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public IResource getResource() {
		if(resource == null && source != null) {
			resource = ResourcesPlugin.getWorkspace().getRoot().getFile(source);
		}
		return resource;
	}
	
	public void setResource(IResource resource) {
		this.resource = resource;
	}

	public int getStartPosition() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * Merges loaded data into currently used declaration.
	 * If changes were done returns a list of changes. 
	 * @param d
	 * @return list of changes
	 */
	public List<Change> merge(SeamComponentDeclaration d) {
		List<Change> changes = null;
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
		final ValueInfo valueInfo = attributes.get(path);
		ISeamTextSourceReference reference = new ISeamTextSourceReference() {
			public int getLength() {
				return valueInfo != null ? valueInfo.getLength() : 0;
			}

			public IResource getResource() {
				return SeamComponentDeclaration.this.getResource();
			}

			public int getStartPosition() {
				return valueInfo != null ? valueInfo.getStartPosition() : 0;
			}
		};
		return reference;
	}
	
	public void setName(ValueInfo value) {
		attributes.put(ISeamXmlComponentDeclaration.NAME, value);
		name = value == null ? null : value.getValue();
	}

}