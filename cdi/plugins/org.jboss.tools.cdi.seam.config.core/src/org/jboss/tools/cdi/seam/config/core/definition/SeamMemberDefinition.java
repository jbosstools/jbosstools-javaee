/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.config.core.definition;

import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.cdi.core.IJavaAnnotation;
import org.jboss.tools.cdi.seam.config.core.xml.Location;
import org.jboss.tools.cdi.seam.config.core.xml.SAXElement;
import org.jboss.tools.cdi.seam.config.core.xml.SAXNode;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public abstract class SeamMemberDefinition {
	protected SAXNode node;
	protected SAXElement replaces = null;
	protected SAXElement modifies = null;
	protected Map<String, IJavaAnnotation> annotations = new HashMap<String, IJavaAnnotation>();

	protected SeamMemberDefinition parent;
	
	public SeamMemberDefinition() {}

	public void setNode(SAXNode node) {
		this.node = node;
	}

	public SAXNode getNode() {
		return node;
	}

	public void setParent(SeamMemberDefinition parent) {
		this.parent = parent;
	}
	
	public SeamMemberDefinition getParent() {
		return parent;
	}
	public void setReplaces(SAXElement replaces) {
		this.replaces = replaces;
	}

	public void setModifies(SAXElement modifies) {
		this.modifies = modifies;
	}

	public void addAnnotation(IJavaAnnotation a) {
		annotations.put(a.getTypeName(), a);
	}

	public Map<String, IJavaAnnotation> getAnnotations() {
		return annotations;
	}

	public IJavaAnnotation getAnnotation(String typeName) {
		return annotations.get(typeName);
	}

	public Location getReplacesLocation() {
		return replaces == null ? null : replaces.getLocation();
	}

	public Location getModifiesLocation() {
		return modifies == null ? null : modifies.getLocation();
	}
}
