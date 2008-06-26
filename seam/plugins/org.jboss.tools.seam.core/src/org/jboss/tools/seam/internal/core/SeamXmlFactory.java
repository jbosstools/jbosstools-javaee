/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core;

import java.util.List;
import java.util.Properties;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamXmlFactory;
import org.jboss.tools.seam.core.IValueInfo;
import org.jboss.tools.seam.core.event.Change;
import org.w3c.dom.Element;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamXmlFactory extends AbstractContextVariable implements ISeamXmlFactory {
	static String METHOD = "method";
	static String VALUE = "value";

	String method = null;
	String value = null;

	public String getMethod() {
		return method;
	}

	public String getValue() {
		return value;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setMethod(IValueInfo value) {
		attributes.put(METHOD, value); //$NON-NLS-1$
		setMethod(value == null ? null : value.getValue());
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public void setValue(IValueInfo value) {
		attributes.put(VALUE, value); //$NON-NLS-1$
		setValue(value == null ? null : value.getValue());
	}

	@Override
	public List<Change> merge(ISeamElement s) {
		List<Change> changes = super.merge(s);
		SeamXmlFactory xf = (SeamXmlFactory)s;

		if(!stringsEqual(value, xf.value)) {
			changes = Change.addChange(changes, new Change(this, VALUE, value, xf.value)); //$NON-NLS-1$
			value = xf.value;
		}
		if(!stringsEqual(method, xf.method)) {
			changes = Change.addChange(changes, new Change(this, METHOD, method, xf.method)); //$NON-NLS-1$
			method = xf.method;
		}
	
		return changes;
	}

	public SeamXmlFactory clone() throws CloneNotSupportedException {
		SeamXmlFactory c = (SeamXmlFactory)super.clone();
		return c;
	}
	
	public String getXMLName() {
		return SeamXMLConstants.TAG_FACTORY;
	}

	public String getXMLClass() {
		return SeamXMLConstants.CLS_XML;
	}

	public Element toXML(Element parent, Properties context) {
		Element element = super.toXML(parent, context);
		
		return element;
	}
	
	public void loadXML(Element element, Properties context) {
		super.loadXML(element, context);
		
		if(attributes.get(METHOD) != null) {
			setMethod(attributes.get(METHOD));
		}
		
		if(attributes.get(VALUE) != null) {
			setValue(attributes.get(VALUE));
		}
	}
	
	public void open() {
		if(id instanceof XModelObject) {
			XModelObject o = (XModelObject)id;
			FindObjectHelper.findModelObject(o, FindObjectHelper.IN_EDITOR_ONLY);
		}
	}

}
