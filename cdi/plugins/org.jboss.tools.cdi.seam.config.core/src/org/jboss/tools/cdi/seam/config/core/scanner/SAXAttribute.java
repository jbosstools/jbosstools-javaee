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

public class SAXAttribute extends SAXText {
	private String name;
	private ITextSourceReference nameLocation;

	private SAXElement parent;
	
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

	public void setValueLocation(ITextSourceReference location) {
		setLocation(location);
	}

	public ITextSourceReference getValueLocation() {
		return getLocation();
	}

	public void setParent(SAXElement parent) {
		this.parent = parent;
	}

	public SAXElement getParent() {
		return parent;
	}

}
