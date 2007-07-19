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

import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.seam.core.IOpenableElement;
import org.jboss.tools.seam.core.ISeamTextSourceReference;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.IValueInfo;

/**
 * @author Viacheslav Kabanovich
 */
public abstract class AbstractSeamDeclaration extends SeamObject implements ISeamTextSourceReference, IOpenableElement {
	public static final String PATH_OF_NAME = "name";

	protected String name;
	
	protected Map<String,IValueInfo> attributes = new HashMap<String, IValueInfo>();
	
	public AbstractSeamDeclaration() {}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void open() {}

	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getStartPosition() {
		// TODO Auto-generated method stub
		return 0;
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

	boolean stringsEqual(String s1, String s2) {
		return s1 == null ? s2 == null : s1.equals(s2);
	}

}
