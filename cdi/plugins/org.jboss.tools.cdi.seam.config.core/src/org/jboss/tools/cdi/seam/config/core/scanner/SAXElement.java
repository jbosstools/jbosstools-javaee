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
package org.jboss.tools.cdi.seam.config.core.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.common.text.ITextSourceReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SAXElement extends SAXNode {
	protected String uri;
	protected String localName;
	protected String name;
	protected ITextSourceReference nameLocation;
	protected Map<String, SAXAttribute> attributes = new HashMap<String, SAXAttribute>();
	protected SAXText text;

	protected SAXElement parent = null;
	protected List<SAXElement> children = new ArrayList<SAXElement>();

	public SAXElement() {}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setNameLocation(ITextSourceReference location) {
		nameLocation = location;
	}

	public ITextSourceReference getNameLocation() {
		return nameLocation;
	}

	public void setURI(String uri) {
		this.uri = uri;
	}

	public String getURI() {
		return uri;
	}

	public void setLocalName(String name) {
		localName = name;
	}

	public String getLocalName() {
		return localName;
	}

	public void setTextNode(SAXText text) {
		this.text = text;
	}

	public SAXText getTextNode() {
		return text;
	}

	public void addAttribute(SAXAttribute a) {
		a.setParent(this);
		attributes.put(a.getName(), a);
	}

	public boolean hasAttribute(String name) {
		return attributes.containsKey(name);
	}

	public SAXAttribute getAttribute(String name) {
		return attributes.get(name);
	}

	public void addChildElement(SAXElement child) {
		child.setParent(this);
		children.add(child);
	}

	public List<SAXElement> getChildElements() {
		return children;
	}

	public void setParent(SAXElement parent) {
		this.parent = parent;
	}

	public SAXElement getParent() {
		return parent;
	}

}
