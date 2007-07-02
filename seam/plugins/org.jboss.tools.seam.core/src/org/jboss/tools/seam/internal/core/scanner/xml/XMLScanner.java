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
package org.jboss.tools.seam.internal.core.scanner.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.internal.core.SeamComponent;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;

public class XMLScanner implements IFileScanner {
	
	public XMLScanner() {}

	/**
	 * Returns true if file is probable component source - 
	 * has components.xml name or *.component.xml mask.
	 * @param resource
	 * @return
	 */	
	public boolean isRelevant(IFile resource) {
		if(resource.getName().equals("components.xml")) return true;
		if(resource.getName().endsWith(".component.xml")) return true;
		return false;
	}
	
	/**
	 * This method should be called only if isRelevant returns true;
	 * Makes simple check if this java file contains annotation Name. 
	 * @param resource
	 * @return
	 */
	public boolean isLikelyComponentSource(IFile f) {
		if(!f.isSynchronized(IFile.DEPTH_ZERO) || !f.exists()) return false;
		XModelObject o = EclipseResourceUtil.getObjectByResource(f);
		if(o == null) return false;
		if(o.getModelEntity().getName().startsWith("FileSeamComponents")) return true;
		//TODO Above does not include .component.xml with root element <component>
		//     and missing xmlns.
		return false;
	}

	/**
	 * Returns list of components
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public SeamComponent[] parse(IFile f) throws Exception {
		XModelObject o = EclipseResourceUtil.getObjectByResource(f);
		return parse(o);
	}
	
	static Set<String> COMMON_ATTRIBUTES = new HashSet<String>();
	
	static {
		//TODO
//		COMMON_ATTRIBUTES.add(ISeamComponent.NAME);
//		COMMON_ATTRIBUTES.add(ISeamComponent.CLASS);
//		COMMON_ATTRIBUTES.add(ISeamComponent.SCOPE);
//		COMMON_ATTRIBUTES.add(ISeamComponent.PRECEDENCE);
//		COMMON_ATTRIBUTES.add(ISeamComponent.INSTALLED);
//		COMMON_ATTRIBUTES.add(ISeamComponent.AUTO_CREATE);
//		COMMON_ATTRIBUTES.add(ISeamComponent.JNDI_NAME);
	}
	
	public SeamComponent[] parse(XModelObject o) {
		if(o == null) return null;
		ArrayList<SeamComponent> list = new ArrayList<SeamComponent>();
		XModelObject[] os = o.getChildren();
		for (int i = 0; i < os.length; i++) {
			XModelEntity componentEntity = os[i].getModelEntity();
			if(componentEntity.getAttribute("class") != null) {
				//This is a component model object
				SeamComponent component = new SeamComponent();
				//TODO
//				component.setName(os[i].getAttributeValue(ISeamComponent.NAME));
//				component.setClassName(os[i].getAttributeValue(ISeamComponent.CLASS));
//				component.setScope(os[i].getAttributeValue(ISeamComponent.SCOPE));
//				component.setPrecedence(os[i].getAttributeValue(ISeamComponent.PRECEDENCE));
//				component.setInstalled(os[i].getAttributeValue(ISeamComponent.INSTALLED));
//				component.setAutoCreate(os[i].getAttributeValue(ISeamComponent.AUTO_CREATE));
//				component.setJndiName(os[i].getAttributeValue(ISeamComponent.JNDI_NAME));
				
				XAttribute[] attributes = componentEntity.getAttributes();
				for (int ia = 0; ia < attributes.length; ia++) {
					XAttribute a = attributes[ia];
					String xml = a.getXMLName();
					if(xml == null) continue;
					if(COMMON_ATTRIBUTES.contains(xml)) continue;
					String stringValue = os[i].getAttributeValue(a.getName());
					component.setStringProperty(xml, stringValue);					
				}

				XModelObject[] properties = os[i].getChildren();
				for (int j = 0; j < properties.length; j++) {
					XModelEntity entity = properties[j].getModelEntity();
					String propertyName = properties[j].getAttributeValue("name");
					if(entity.getAttribute("value") != null) {
						//this is simple value;
						String value = properties[j].getAttributeValue("value");
						component.setStringProperty(propertyName, value);
					} else {
						XModelObject[] entries = properties[j].getChildren();
						if(entity.getChild("SeamListEntry") != null
							|| "list".equals(entity.getProperty("childrenLoader"))) {
							//this is list value
							List<String> listValues = new ArrayList<String>();
							for (int k = 0; k < entries.length; k++) {
								listValues.add(entries[k].getAttributeValue("value"));
							}
//							component.addProperty(new SeamProperty<List<String>>(propertyName, listValues));
						} else {
							//this is map value
							Map<String,String> mapValues = new HashMap<String, String>();
							for (int k = 0; k < entries.length; k++) {
								String entryKey = entries[k].getAttributeValue("key");
								String entryValue = entries[k].getAttributeValue("value");
								mapValues.put(entryKey, entryValue);
							}
//							component.addProperty(new SeamProperty<Map<String,String>>(propertyName, mapValues));
						}
					}
					//TODO assign positioning attributes to created ISeamProperty object
				}

				list.add(component);
			} else if(os[i].getModelEntity().getName().startsWith("SeamFactory")) {
				//TODO what is the best way for factory?
				SeamComponent component = new SeamComponent();
				//TODO
//				component.setName(os[i].getAttributeValue(ISeamComponent.NAME));
//				component.setScope(os[i].getAttributeValue(ISeamComponent.SCOPE));
				String value = os[i].getAttributeValue("value");
				//TODO how should we resolve value?
				if(value != null) component.setStringProperty("value", value);

				list.add(component);
			}
		}

		return list.toArray(new SeamComponent[0]);
	}
}