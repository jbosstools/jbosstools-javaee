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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IField;
import org.jboss.tools.cdi.seam.config.core.xml.SAXAttribute;
import org.jboss.tools.cdi.seam.config.core.xml.SAXElement;
import org.jboss.tools.cdi.seam.config.core.xml.SAXText;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class AbstractSeamFieldDefinition extends SeamMemberDefinition {
	protected List<SAXText> listValue = null;
	protected Map<SAXText, SAXText> mapValue = null;
	
	protected List<SeamFieldValueDefinition> valueDefinitions = new ArrayList<SeamFieldValueDefinition>();

	public AbstractSeamFieldDefinition() {}

	public String getValue() {
		return listValue == null || listValue.size() == 0 ? null : listValue.get(0).getValue();
	}

	public void addValue(SAXText value) {
		if(listValue == null) listValue = new ArrayList<SAXText>();
		listValue.add(value);
	}

	public List<String> getListValue() {
		List<String> result = new ArrayList<String>();
		if(listValue != null) for (SAXText t: listValue) {
			result.add(t.getValue());
		}
		return result;
	}

	public void addValue(SAXText key, SAXText value) {
		if(mapValue == null) mapValue = new HashMap<SAXText, SAXText>();
		mapValue.put(key, value);
	}

	public Map<String, String> getMapValue() {
		Map<String, String> result = new HashMap<String, String>();
		if(mapValue != null) for (SAXText t: mapValue.keySet()) {
			result.put(t.getValue(), mapValue.get(t).getValue());
		}
		return result;
	}

	public void addValueDefinition(SeamFieldValueDefinition def) {
		valueDefinitions.add(def);
		def.setParent(this);
	}

	public List<SeamFieldValueDefinition> getValueDefinitions() {
		return valueDefinitions;
	}
}
