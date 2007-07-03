/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core;

import org.eclipse.core.resources.IResource;
import org.jboss.tools.seam.core.ISeamProperty;

public class SeamProperty implements ISeamProperty {
	protected String name;
	protected Object value;
	protected int startPosition = -1;
	protected int length = -1;

	public SeamProperty() {}

	public SeamProperty(String name) {
		this.name = name;
	}

	public SeamProperty(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public int getStartPosition() {
		return startPosition;
	}
	
	public void setStartPosition(int v) {
		startPosition = v;
	}

	public int getLength() {
		return length;
	}
	
	public void setLength(int v) {
		length = v;
	}

	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamXmlElement#getResource()
	 */
	public IResource getResource() {
		// TODO Auto-generated method stub
		return null;
	}
}