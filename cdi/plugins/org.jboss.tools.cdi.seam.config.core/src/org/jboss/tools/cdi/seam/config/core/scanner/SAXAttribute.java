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

import org.jboss.tools.common.text.ITextSourceReference;

public class SAXAttribute extends SAXNode {
	private String name;
	private ITextSourceReference nameLocation;
	private String value;
	private ITextSourceReference valueLocation;

	private SAXElement parent;
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setNameLocation(ITextSourceReference location) {
		nameLocation = location;
	}

	public void setValueLocation(ITextSourceReference location) {
		valueLocation = location;
	}

	public ITextSourceReference getNameLocation() {
		return nameLocation;
	}

	public ITextSourceReference getValueLocation() {
		return valueLocation;
	}

	public void setParent(SAXElement parent) {
		this.parent = parent;
	}

	public SAXElement getParent() {
		return parent;
	}

}
