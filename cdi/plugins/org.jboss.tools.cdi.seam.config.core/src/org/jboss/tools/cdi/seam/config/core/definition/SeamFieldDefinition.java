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

import org.eclipse.jdt.core.IField;
import org.jboss.tools.cdi.seam.config.core.xml.SAXAttribute;
import org.jboss.tools.cdi.seam.config.core.xml.SAXElement;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SeamFieldDefinition extends AbstractSeamFieldDefinition {
	protected IField field;

	public SeamFieldDefinition() {}

	public void setField(IField field) {
		this.field = field;
	}

	public IField getField() {
		return field;
	}

	public String getName() {
		if(field != null) return field.getElementName();
		if(getNode() instanceof SAXElement) return ((SAXElement)getNode()).getLocalName();
		if(getNode() instanceof SAXAttribute) return ((SAXAttribute)getNode()).getName();
		return null;
	}

}
