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

import java.util.List;
import java.util.Properties;

import org.jboss.tools.common.model.project.ext.IValueInfo;
import org.jboss.tools.common.model.project.ext.event.Change;
import org.jboss.tools.seam.core.ISeamElement;
import org.w3c.dom.Element;

/**
 * @author Viacheslav Kabanovich
 */
public class DataModelSelectionAttribute extends BijectedAttribute {
	public static String VALUE = "value";
	String value;
	
	public DataModelSelectionAttribute() {}

	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setValue(IValueInfo value) {
		attributes.put(VALUE, value);
		this.value = value == null ? null : value.getValue();
	}

	@Override
	public List<Change> merge(ISeamElement s) {
		List<Change> changes = super.merge(s);
		if(s instanceof DataModelSelectionAttribute) {
			DataModelSelectionAttribute sf = (DataModelSelectionAttribute)s;
			if(!stringsEqual(this.value, sf.value)) {
				changes = Change.addChange(changes, new Change(this, VALUE, this.value, sf.value));
				this.value = sf.value;
			}
		}
		return changes;
	}
	
	public String getXMLClass() {
		return SeamXMLConstants.CLS_DATA_MODEL;
	}

	public Element toXML(Element parent, Properties context) {
		Element element = super.toXML(parent, context);
		
		return element;
	}
	
	public void loadXML(Element element, Properties context) {
		super.loadXML(element, context);
		
		if(attributes.get(VALUE) != null) {
			setValue(attributes.get(VALUE));
		}
	}

}
