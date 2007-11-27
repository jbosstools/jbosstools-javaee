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
package org.jboss.tools.jsf.project.capabilities;

import java.io.*;
import org.w3c.dom.*;
import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.engines.impl.EnginesLoader;
import org.jboss.tools.common.model.loaders.XObjectLoader;
import org.jboss.tools.common.model.loaders.impl.SerializingLoader;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.common.xml.*;
import org.jboss.tools.common.xml.XMLEntityResolver;
import org.jboss.tools.jsf.messages.JSFUIMessages;

public class FileAdditionPerformer extends PerformerItem {
	XModelObject fileAddition;
	XModelObject configFile;
	String xPath;
	String preferredPosition;
	String text;
	
	Element element;

	XModelObject targetObject;
	String targetAttribute;
	String attributePath;
	
	XModelObject updateObject = null;
	String attributeValue = null;

	public String getDisplayName() {
		return fileAddition.getAttributeValue("label");
	}

	public void init(XModelObject configFile, XModelObject fileAddition) {
		this.fileAddition = fileAddition;
		this.configFile = configFile;
		xPath = fileAddition.getAttributeValue("x-path");
		preferredPosition = fileAddition.getAttributeValue("preferred position");
		text = fileAddition.getAttributeValue("text");
		targetObject = configFile;
	}
	
	void setConfigFile(XModelObject configFile) {
		this.configFile = configFile;
		targetObject = configFile;
	}
	
	public boolean check() {
		if(!isSelected()) return true;
		if(!scan(configFile, "")) {
			String path = XModelObjectLoaderUtil.getResourcePath(configFile);
			String message = "Cannot find x-path " + xPath + " in config file" + path + ".\n"
						   + "Please correct file addition " + fileAddition.getAttributeValue("label") + ".";
			report(message);
			return false;
		}
		if(!parseText()) return false;
		
		return true;
	}
	
	public boolean execute(PerformerContext context) throws Exception {
		if(!isSelected()) return true;
		if(targetAttribute != null && attributeValue != null) {
			targetObject.getModel().editObjectAttribute(targetObject, targetAttribute, attributeValue);
		} else if(updateObject != null) {
			mergeAttributes(targetObject, updateObject);
			mergeChildren(targetObject, updateObject.getChildren());
		}
		String capability = fileAddition.getParent().getAttributeValue("name");
		context.changeList.add("" + capability + ": " + "Added \"" + getDisplayName() + "\" to " + fileAddition.getAttributeValue("file name"));
		context.monitor.worked(1);
		return true;
	}
	
	private boolean scan(XModelObject object, String xp) {
		String xml = object.getModelEntity().getXMLSubPath();
		if(xml == null || xml.length() == 0) {
			return scanChildren(object, xp);
		}
		xp += "/" + xml;
		if(!xPath.startsWith(xp)) return false;
		if(xPath.equals(xp)) {
			targetObject = object;
			targetAttribute = null;
			return true;
		}
		if(scanAttributes(object, xp)) return true;
		return scanChildren(object, xp);
	}
	
	private boolean scanAttributes(XModelObject object, String xp) {
		XAttribute[] as = object.getModelEntity().getAttributes();
		for (int i = 0; i < as.length; i++) {
			String xml = as[i].getXMLName();
			if(xml == null || xml.length() == 0) continue;
			xml = xml.replace('.', '/');
			if(xml.endsWith("#text")) {
				xml = xml.substring(0, xml.length() - 5) + "text()";
			} else {
				int k = xml.lastIndexOf("/");
				if(k < 0) xml = "@" + xml; else xml = xml.substring(0, k + 1) + "@" + xml.substring(k + 1);
			}
			xp = xp + "/" + xml;
			if(xp.equals(xPath) || xp.startsWith(xPath)) {
				targetObject = object;
				targetAttribute = as[i].getName();
				attributePath = null;
				if(!xp.equals(xPath)) {
					attributePath = xp.substring(xPath.length() + 1);
					attributePath = attributePath.replace('/', '.');
					if(attributePath.endsWith("text()")) {
						attributePath = attributePath.substring(0, attributePath.length() - 6) + "#text";
					}
				}
				return true;
			}
		}
		return false;
	}
	
	private boolean scanChildren(XModelObject object, String xp) {
		XModelObject[] cs = object.getChildrenForSave();
		for (int i = 0; i < cs.length; i++) {
			if(scan(cs[i], xp)) return true;
		}
		return false;
	}
	
	private boolean parseText() {
		if(text == null) return false;
		if(text.length() == 0) return true;
		if(text.trim().startsWith("<") || targetAttribute == null) {
			String tag = targetObject.getModelEntity().getXMLSubPath();
			String body = "<" + tag + ">\n" + text + "</" + tag + ">\n";
			element = XMLUtilities.getElement(new StringReader(body), XMLEntityResolver.getInstance());
			if(element == null) {
				String[] errors = XMLUtilities.getXMLErrors(new StringReader(body), false, XMLEntityResolver.getInstance());
				String message = "Cannot load xml";
				if(errors.length > 0) message += ": " + errors[0];
				message += "\nPlease correct file addition " + fileAddition.getAttributeValue("label") + ".";
				report(message);
				return false;
			}
			if(targetAttribute == null) {
				updateObject = XModelObjectLoaderUtil.createValidObject(targetObject.getModel(), targetObject.getModelEntity().getName());
				XObjectLoader loader = XModelObjectLoaderUtil.getObjectLoader(configFile);
				if(loader instanceof SerializingLoader) {
					SerializingLoader sl = (SerializingLoader)loader;
					sl.loadFragment(updateObject, element);
				} else {
					new XModelObjectLoaderUtil().load(element, updateObject);
				}
			} else {
				attributeValue = readAttributeValue(element);
			}
		} else {
			attributeValue = text;
		}
		return true;
	}

	private void report(String message) {
		ServiceDialog d = configFile.getModel().getService();
		d.showDialog(JSFUIMessages.ERROR, message, new String[]{JSFUIMessages.CLOSE}, null, ServiceDialog.ERROR);
	}
	
	private String readAttributeValue(Element element) {
		XModelObjectLoaderUtil util = new XModelObjectLoaderUtil();
		if(attributePath != null) {
			return util.getAttribute(element, attributePath);
		} else {
			return XMLUtilities.getCDATA(element);
		}
	}
	
	private void mergeAttributes(XModelObject object, XModelObject update) {
		XAttribute[] as = update.getModelEntity().getAttributes();
		for (int i = 0; i < as.length; i++) {
			String xml = as[i].getXMLName();
			if(xml == null || xml.indexOf('.') < 0) continue;
			String v = update.getAttributeValue(as[i].getName());
			if(v == null || v.length() == 0) continue;
			object.getModel().editObjectAttribute(object, as[i].getName(), v);
		}
	}
	
	private void mergeChildren(XModelObject object, XModelObject[] update) {
		for (int i = 0; i < update.length; i++) {
			String pp = update[i].getPathPart();
			XModelObject c = object.getChildByPath(pp);
			if(c == null) {
				DefaultCreateHandler.addCreatedObject(object, update[i], -1);
				if("0".equals(preferredPosition) || "top".equals(preferredPosition)) {
					moveToPotition(object, update[i], 0);
				}
			} else {
				String xml = update[i].getModelEntity().getXMLSubPath();
				if(xml == null || xml.length() == 0) {
					mergeChildren(c, update[i].getChildren());
				} else {
					EnginesLoader.merge(c, update[i], true);
				}
			}
		}
	}
	
	private void moveToPotition(XModelObject parent, XModelObject child, int pos) {
		if(!(parent instanceof XOrderedObject)) return;
		XModelObject[] cs = parent.getChildren();
		for (int i = 0; i < cs.length; i++) {
			if(cs[i] != child) continue;
			if(i == 0) return;
			((XOrderedObject)parent).move(i, 0, true);
			return;
		}
	}

}
