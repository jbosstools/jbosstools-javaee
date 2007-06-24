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
package org.jboss.tools.jsf.model;

import java.io.*;
import org.w3c.dom.*;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.*;
import org.jboss.tools.common.model.filesystems.impl.*;
import org.jboss.tools.common.model.loaders.impl.SimpleWebFileLoader;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.common.xml.SAXValidator;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jst.web.model.*;

public class FacesConfigLoader implements WebProcessLoader, JSFConstants {
	public static String AUXILIARY_FILE_EXTENSION = "jsfdia";
	private FileAuxiliary aux = new FileAuxiliary(AUXILIARY_FILE_EXTENSION, false);
	XModelObjectLoaderUtil util = new SFUtil();
	boolean isLight = false;

	public void load(XModelObject object) {
		String entity = object.getModelEntity().getName();
        
		String body = XModelObjectLoaderUtil.getTempBody(object);
		
		String[] errors = (entity.equals(ENT_FACESCONFIG_12))
			? new SAXValidator().getXMLErrors(new StringReader(body))
			: XMLUtil.getXMLErrors(new StringReader(body));
		if(errors != null && errors.length > 0) {
			object.setAttributeValue("isIncorrect", "yes");
			object.setAttributeValue("incorrectBody", body);
			object.set("actualBodyTimeStamp", "-1");
		} else {
			object.setAttributeValue("isIncorrect", "no");
			object.set("correctBody", body);
			object.set("actualBodyTimeStamp", "0");
			object.setAttributeValue("incorrectBody", "");
		}
		Document doc = XMLUtil.getDocument(new StringReader(body));
		if(doc == null) {
			XModelObjectLoaderUtil.addRequiredChildren(object);
			return;
		}
		Element element = doc.getDocumentElement();
		util.load(element, object);
		
		((FileFacesConfigImpl)object).updateRuleIndices();
		
		setEncoding(object, body);
		if(object.getModelEntity().getAttribute("systemId") != null) {
			NodeList nl = doc.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if(n instanceof DocumentType) {
					DocumentType dt = (DocumentType)n;
					object.setAttributeValue("systemId", dt.getSystemId());
				}
			}
		}
		reloadProcess(object);
		object.set("actualBodyTimeStamp", "" + object.getTimeStamp());
	}
    
	protected void setEncoding(XModelObject object, String body) {
		String encoding = XModelObjectLoaderUtil.getEncoding(body);
		if(encoding == null) encoding = "";
		object.setAttributeValue(XModelObjectConstants.ATTR_NAME_ENCODING, encoding);
	}
    
	public void reloadProcess(XModelObject object) {
		FacesProcessImpl process = (FacesProcessImpl)object.getChildByPath(ELM_PROCESS);
		if(process == null) return;
		process.setReference(object);
		if(!object.isActive()) return;
		String bodyAux = (object.getParent() == null ? null : aux.read(object.getParent(), object));
		if (bodyAux != null) {
			Document doc2 = XMLUtil.getDocument(new StringReader(bodyAux));
			if (doc2 == null) {
				//JSFModelPlugin.log("Unable to parse aux body of "+object.getPath());
			} else {
				util.load(doc2.getDocumentElement(), process);
			}
		}
		process.setReference(null);
		process.firePrepared();
	}
    
	public boolean update(XModelObject object) {
		XModelObject p = object.getParent();
		if (p == null) return true;
		FolderLoader fl = (FolderLoader)p;
		String body = fl.getBodySource(FileAnyImpl.toFileName(object)).get();
		AbstractExtendedXMLFileImpl f = (AbstractExtendedXMLFileImpl)object;
		f.setUpdateLock();
		try {
			f.edit(body, true);
		} finally {
			f.releaseUpdateLock();
		}
		object.setModified(false);
		XModelObjectLoaderUtil.updateModifiedOnSave(object);
		return true;
	}

	public boolean save(XModelObject object) {
		if (!object.isModified()) return true;
		FileAnyImpl file = (FileAnyImpl)object;
		String text = file.getAsText();
		XModelObjectLoaderUtil.setTempBody(object, text);
		if("yes".equals(object.get("isIncorrect"))) {
			return true;
		}
		return saveLayout(object);
	}
    
	public boolean saveLayout(XModelObject object) {
		if(isLight) return true;
		XModelObjectLoaderUtil util = new XModelObjectLoaderUtil();
		try {
			XModelObject process = object.getChildByPath(ELM_PROCESS);
			if(process == null) return true;
			process.setModified(true);
			Element element = XMLUtil.createDocumentElement("PROCESS");
			util.saveAttributes(element, process);
			util.saveChildren(element, process);
			StringWriter sw = new StringWriter();
			XModelObjectLoaderUtil.serialize(element, sw);
			XModelObjectLoaderUtil.setTempBody(process, sw.toString());
			aux.write(object.getParent(), object, process);
			return true;
		} catch (Exception exc) {
			JSFModelPlugin.log(exc);
			return false;
		}
	}    

	public String serializeMainObject(XModelObject object) {
		String entity = object.getModelEntity().getName();
		String systemId = object.getAttributeValue("systemId");
        String publicId = object.getAttributeValue("publicId");

        if(systemId != null) {
        	int version = entity.equals(ENT_FACESCONFIG_11) ? 11 : 10;
        	if(systemId != null && systemId.length() == 0) systemId = (version == 10) ? DOC_EXTDTD : DOC_EXTDTD_11;
        	if(publicId != null && publicId.length() == 0) publicId = (version == 10) ? DOC_PUBLICID : DOC_PUBLICID_11;
        }

        Element element = (systemId == null || publicId == null)
    	? XMLUtil.createDocumentElement(object.getModelEntity().getXMLSubPath())
        : XMLUtil.createDocumentElement(object.getModelEntity().getXMLSubPath(), DOC_QUALIFIEDNAME, publicId, systemId, null);
		
		try {
			util.setup(null, false);
            util.saveAttributes(element, object);
            util.saveChildren(element, object);
            util.saveFinalComment(element, object);
            element.removeAttribute("NAME");
            element.removeAttribute("EXTENSION");
            return SimpleWebFileLoader.serialize(element, object);
		} catch (Exception e) {
			JSFModelPlugin.log(e);
			return null;
		}
	}

	public String mainObjectToString(XModelObject object) {
		return "" + serializeMainObject(object);
	}

	public String serializeObject(XModelObject object) {
		return serializeMainObject(object);
	}

	public void loadFragment(XModelObject object, Element element) {
		util.load(element, object);		
	}

}

class SFUtil extends XModelObjectLoaderUtil {

	static String[] folders = new String[]{"Components", "Converters", "Managed Beans", "Navigation Rules", "Referenced Beans", "Render Kits", "Validators", "Extensions"};

	public void loadChildren(Element element, XModelObject o) {
		if(o.getFileType() == XModelObject.FILE) {
			super.loadChildren(element, o);
			for (int i = 0; i < folders.length; i++) {
				XModelObject c = o.getChildByPath(folders[i]);
				if(c != null) super.loadChildren(element, c);
			}
		} else if("JSFManagedBean".equals(o.getModelEntity().getName())) {
			loadManagedBeanChildren(element, o);
		} else if("JSFManagedProperty".equals(o.getModelEntity().getName())) {
			loadManagedPropertyChildren(element, o);
		} else if("JSFListEntries".equals(o.getModelEntity().getName())) {
			loadListEntriesChildren(element, o);
		} else {
			super.loadChildren(element, o);
		}
	}
	
    public boolean save(Element parent, XModelObject o) {
    	if(!needToSave(o)) return true;
    	return super.save(parent, o);
    }
    boolean needToSave(XModelObject o) {
    	if(o == null) return false;
    	String entity = o.getModelEntity().getName();
    	if("JSFApplication".equals(entity) || "JSFApplication12".equals(entity)) {
    		return (hasSetAttributes(o) 
    				|| o.getChildren().length > 1
    				|| needToSave(o.getChildByPath("Locale Config")));
    	} else if("JSFLifecycle".equals(entity) || "JSFLocaleConfig".equals(entity)) {
    		return (hasSetAttributes(o) || o.getChildren().length > 0);
    	} else if("JSFFactory".equals(entity)) {
    		return (hasSetAttributes(o));
    	}
    	return true;
    }
    
    private boolean hasSetAttributes(XModelObject o) {
    	XAttribute[] as = o.getModelEntity().getAttributes();
    	for (int i = 0; i < as.length; i++) {
    		String xml = as[i].getXMLName();
    		// it would be more safe to check isSavable
    		if(xml == null || xml.length() == 0 || "NAME".equals(xml)) continue;
    		String v = o.getAttributeValue(as[i].getName());
    		if(v != null && v.length() > 0) return true;
    	}
    	String finalComment = o.get("#final-comment");
    	if(finalComment != null && finalComment.length() > 0) return true;
    	return false;
    }

	public boolean saveChildren(Element element, XModelObject o) {
		if(o.getFileType() == XModelObject.FILE) {
			for (int i = 0; i < folders.length; i++) {
				XModelObject c = o.getChildByPath(folders[i]);
				if(c != null) super.saveChildren(element, c);
			}
			super.saveChildren(element, o);
			return true;
		} else if("JSFManagedBean".equals(o.getModelEntity().getName())) {
			saveManagedBeanChildren(element, o);
			return true;
		} else if("JSFManagedProperty".equals(o.getModelEntity().getName())) {
			saveManagedPropertyChildren(element, o);
			return true;
		} else if("JSFListEntries".equals(o.getModelEntity().getName())) {
			saveListEntriesChildren(element, o);
			return true;
		} else {
			return super.saveChildren(element, o);
		}
	}

	public void loadAttributes(Element element, XModelObject o) {
		super.loadAttributes(element, o);
		String entity = o.getModelEntity().getName();
		if("JSFListEntry".equals(entity) || "JSFMapEntry".equals(entity)) {
			Element ce = XMLUtil.getUniqueChild(element, "null-value");
			if(ce != null) {
				o.setAttributeValue("null-value", "true");
				o.setAttributeValue("value", "");
			} else {
				o.setAttributeValue("null-value", "false");
				o.setAttributeValue("value", super.getAttribute(element, "value.#text"));
			}			
		}	
	}

	protected boolean isSaveable(XModelEntity entity, String n, String v, String dv) {
		if(v == null) return false;
		if(v.length() == 0 || v.equals(dv)) {
			XAttribute attr = entity.getAttribute(n);
			return (attr != null && "always".equals(attr.getProperty("save")));
		}
		return super.isSaveable(entity, n, v, dv);
	}

	public String getAttribute(Element element, String xmlname) {
		if("attribute-names".equals(xmlname))
			return loadArray(element, "attribute-name");
		if("redirect".equals(xmlname))
			return (XMLUtil.getUniqueChild(element, "redirect") != null) ? "yes" : "no";
		if("value.#text".equals(xmlname)) {
		   if(XMLUtil.getUniqueChild(element, "null-value") != null) return "null-value";
		}
		return super.getAttribute(element, xmlname);
	}

	public void saveAttribute(Element element, String xmlname, String value) {
		if("redirect".equals(xmlname)) {
			if("yes".equals(value)) XMLUtil.createElement(element, "redirect");
		} else if("value.#text".equals(xmlname) && "null-value".equals(value)) {
			XMLUtil.createElement(element, "null-value");
		} else if("attribute-names".equals(xmlname)) {
			saveArray(element, "attribute-name", value);
		} else {
			super.saveAttribute(element, xmlname, value);
		}
	}

	public void saveAttributes(Element element, XModelObject o) {
		super.saveAttributes(element, o);
		String entity = o.getModelEntity().getName();
		if("JSFConverter".equals(entity)) {
			eitherOr(element, "converter-for-class", "converter-id");
		} else if("JSFListEntry".equals(entity) || "JSFMapEntry".equals(entity)) {
			if("true".equals(o.getAttributeValue("null-value"))) {
				XMLUtil.createElement(element, "null-value");
			} else {
				super.saveAttribute(element, "value.#text", o.getAttributeValue("value"));
			}
		}
	}
	
	void loadManagedBeanChildren(Element element, XModelObject o) {
		Element ce = XMLUtil.getUniqueChild(element, "list-entries");
		if(ce != null) {
			o.setAttributeValue("content-kind", "list-entries");
			XModelObject c = o.getChildByPath("Entries");
			if(c != null) load(ce, c);
			return;
		}
		ce = XMLUtil.getUniqueChild(element, "map-entries");
		if(ce != null) {
			o.setAttributeValue("content-kind", "map-entries");
			XModelObject c = o.getChildByPath("Entries");
			if(c != null) load(ce, c);
			return;
		}
		o.setAttributeValue("content-kind", "properties");
		super.loadChildren(element, o);
	}

	void loadManagedPropertyChildren(Element element, XModelObject o) {
		Element ce = XMLUtil.getUniqueChild(element, "null-value");
		if(ce != null) {
			o.setAttributeValue("value-kind", "null-value");
			o.setAttributeValue("value", "");
			return;
		}
		ce = XMLUtil.getUniqueChild(element, "list-entries");
		if(ce != null) {
			o.setAttributeValue("value-kind", "list-entries");
			o.setAttributeValue("value", "");
			XModelObject c = o.getChildByPath("Entries");
			if(c != null) load(ce, c);
			return;
		}
		ce = XMLUtil.getUniqueChild(element, "map-entries");
		if(ce != null) {
			o.setAttributeValue("value-kind", "map-entries");
			o.setAttributeValue("value", "");
			XModelObject c = o.getChildByPath("Entries");
			if(c != null) load(ce, c);
			return;
		}
		String v = getAttribute(element, "value.#text");
		o.setAttributeValue("value-kind", "value");
		o.setAttributeValue("value", v);
	}

	void loadListEntriesChildren(Element element, XModelObject o) {
		NodeList nl = element.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if(n.getNodeType() != Node.ELEMENT_NODE) continue;
			String name = n.getNodeName();
			if(!"null-value".equals(name) && !"value".equals(name)) continue;
			XModelObject c = o.getModel().createModelObject("JSFListEntry", null);
			if("null-value".equals(name)) {
				c.setAttributeValue("null-value", "true");
			} else {
				c.setAttributeValue("null-value", "false");
				c.setAttributeValue("value", getAttribute((Element)n, "#text"));
			}
			o.addChild(c);
		}
	}

	void saveManagedBeanChildren(Element element, XModelObject o) {
		super.saveChildren(element, o);
	}

	void saveManagedPropertyChildren(Element element, XModelObject o) {
		String kind = o.getAttributeValue("value-kind");
		if("null-value".equals(kind)) {
			XMLUtil.createElement(element, "null-value");
		} else if("value".equals(kind)) {
			super.saveAttribute(element, "value.#text", o.getAttributeValue("value"));
		} else {
			XModelObject c = o.getChildByPath("Entries");
			if(c != null) {
				super.saveChildren(element, o);
			} else {
				XMLUtil.createElement(element, "null-value");
			}
		}		
	}

	void saveListEntriesChildren(Element element, XModelObject o) {
		XModelObject[] cs = o.getChildren();
		for (int i = 0; i < cs.length; i++) {
			boolean isNullValue = "true".equals(cs[i].getAttributeValue("null-value"));
			if(isNullValue) {
				XMLUtil.createElement(element, "null-value");
			} else {
				Element ce = XMLUtil.createElement(element, "value");
				try {
					saveAttribute(ce, "#text", cs[i].getAttributeValue("value"));
				} catch (Exception t) {
        			JSFModelPlugin.log("Error in saving list entries", t);
				}
			}
		} 
	}

}
