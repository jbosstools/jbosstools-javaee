/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.template.util.model;

import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.TypeInfo;

public class AttributeProxy extends NodeProxy implements IDOMAttr  {

	public AttributeProxy(IDOMAttr basicAttr, int basicOffset) {
		super(basicAttr, basicOffset);
	}

	public ITextRegion getEqualRegion() {

		return ((IDOMAttr) basicNode).getEqualRegion();
	}

	public int getNameRegionEndOffset() {
		return ((IDOMAttr) basicNode).getNameRegionEndOffset() + basicOffset;
	}

	public int getNameRegionStartOffset() {
		return ((IDOMAttr) basicNode).getNameRegionStartOffset() + basicOffset;
	}

	public String getNameRegionText() {
		return ((IDOMAttr) basicNode).getNameRegionText();
	}

	public int getNameRegionTextEndOffset() {
		return ((IDOMAttr) basicNode).getNameRegionTextEndOffset()
				+ basicOffset;
	}

	public int getValueRegionStartOffset() {
		return ((IDOMAttr) basicNode).getValueRegionStartOffset() + basicOffset;
	}

	public String getValueRegionText() {
		return ((IDOMAttr) basicNode).getValueRegionText();
	}

	public boolean hasNameOnly() {
		return ((IDOMAttr) basicNode).hasNameOnly();
	}

	public boolean hasNestedValue() {
		return ((IDOMAttr) basicNode).hasNestedValue();
	}

	public boolean isGlobalAttr() {
		return ((IDOMAttr) basicNode).isGlobalAttr();
	}

	public boolean isXMLAttr() {
		return ((IDOMAttr) basicNode).isXMLAttr();
	}

	public String getName() {
		return ((IDOMAttr) basicNode).getName();
	}

	public Element getOwnerElement() {
		return ((IDOMAttr) basicNode).getOwnerElement();
	}

	public TypeInfo getSchemaTypeInfo() {
		return ((IDOMAttr) basicNode).getSchemaTypeInfo();
	}

	public boolean getSpecified() {
		return ((IDOMAttr) basicNode).getSpecified();
	}

	public String getValue() {
		return ((IDOMAttr) basicNode).getValue();
	}

	public void setValue(String value) throws DOMException {
		((IDOMAttr) basicNode).setValue(value);

	}

}
