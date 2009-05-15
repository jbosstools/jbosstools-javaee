/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.core;

import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.common.model.project.ext.event.Change;
import org.w3c.dom.Element;

/**
 * Common interface for objects of seam model.
 *  
 * @author Viacheslav Kabanovich
 */
public interface ISeamElement extends Cloneable {
	
	/**
	 * Returns seam project that contains this object.
	 * @return
	 */
	public ISeamProject getSeamProject();

	/**
	 * Returns parent object of seam model.
	 * @return
	 */
	public ISeamElement getParent();
	
	/**
	 * Returns path of resource that declares this object.
	 * @return
	 */
	public IPath getSourcePath();

	/**
	 * Returns resource that declares this object.
	 * @return resource 
	 */
	public IResource getResource();
	
	public ISeamElement clone() throws CloneNotSupportedException;
	
	/**
	 * Updates this seam object to be equal to the passed seam object.
	 * @param s
	 * @return List of performed modifications.
	 */
	public List<Change> merge(ISeamElement s);
	
	/**
	 * Serializes this object to XML element and adds it as child to the passed element.
	 * @param parent
	 * @param context
	 * @return
	 */
	public Element toXML(Element parent, Properties context);

	/**
	 * Extracts data from the passed XML element to update this object 
	 * to be equal to seam object serialized to XML.
	 * @param element
	 * @param context
	 */
	public void loadXML(Element element, Properties context);

}
