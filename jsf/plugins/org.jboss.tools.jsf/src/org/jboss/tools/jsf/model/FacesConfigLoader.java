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
import java.util.Set;

import org.w3c.dom.*;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.*;
import org.jboss.tools.common.model.filesystems.impl.*;
import org.jboss.tools.common.model.loaders.impl.SimpleWebFileLoader;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jst.web.model.*;

public class FacesConfigLoader extends AbstractWebDiagramLoader implements WebProcessLoader, JSFConstants {
	public static String AUXILIARY_FILE_EXTENSION = "jsfdia"; //$NON-NLS-1$

    protected FileAuxiliary createFileAuxiliary() {
    	return new FileAuxiliary(AUXILIARY_FILE_EXTENSION, false);
    }

    protected XModelObjectLoaderUtil createUtil() {
    	return new SFUtil();
    }

	public void load(XModelObject object) {
//		String entity = object.getModelEntity().getName();
        
		String body = XModelObjectLoaderUtil.getTempBody(object);
		
        int resolution = EntityXMLRegistration.getInstance().resolve(object.getModelEntity());
        if(EntityXMLRegistration.isSystemId(body)) resolution = EntityXMLRegistration.UNRESOLVED;
		String[] errors = 
			XMLUtil.getXMLErrors(new StringReader(body), resolution == EntityXMLRegistration.DTD, resolution == EntityXMLRegistration.SCHEMA);
		boolean hasErrors = (errors != null && errors.length > 0);
		if(hasErrors) {
			object.setAttributeValue("isIncorrect", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			object.setAttributeValue("incorrectBody", body); //$NON-NLS-1$
			object.set("actualBodyTimeStamp", "-1"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			object.setAttributeValue("isIncorrect", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			object.set("correctBody", body); //$NON-NLS-1$
			object.set("actualBodyTimeStamp", "0"); //$NON-NLS-1$ //$NON-NLS-2$
			object.setAttributeValue("incorrectBody", ""); //$NON-NLS-1$ //$NON-NLS-2$
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
		if(object.getModelEntity().getAttribute("systemId") != null) { //$NON-NLS-1$
			NodeList nl = doc.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if(n instanceof DocumentType) {
					DocumentType dt = (DocumentType)n;
					object.setAttributeValue("systemId", dt.getSystemId()); //$NON-NLS-1$
					if(dt.getPublicId() == null) {
						object.setAttributeValue("publicId", "null"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
		}
		String loadingError = util.getError();
		reloadProcess(object);

		object.set("actualBodyTimeStamp", "" + object.getTimeStamp()); //$NON-NLS-1$ //$NON-NLS-2$
		((AbstractXMLFileImpl)object).setLoaderError(loadingError);
		if(!hasErrors && loadingError != null) {
			object.setAttributeValue("isIncorrect", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			object.setAttributeValue("incorrectBody", body); //$NON-NLS-1$
			object.set("actualBodyTimeStamp", "" + object.getTimeStamp()); //$NON-NLS-1$ //$NON-NLS-2$
		}
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
    
	public boolean saveLayout(XModelObject object) {
		if(object == null || !object.isActive()) return false;
		XModelObjectLoaderUtil util = new XModelObjectLoaderUtil();
		try {
			XModelObject process = object.getChildByPath(ELM_PROCESS);
			if(process == null) return true;
			process.setModified(true);
			Element element = XMLUtil.createDocumentElement("PROCESS"); //$NON-NLS-1$
			util.saveAttributes(element, process);
			util.saveChildren(element, process);
			StringWriter sw = new StringWriter();
			XModelObjectLoaderUtil.serialize(element, sw);
			XModelObjectLoaderUtil.setTempBody(process, sw.toString());
			aux.write(object.getParent(), object, process);
			return true;
		} catch (IOException exc) {
			JSFModelPlugin.getPluginLog().logError(exc);
			return false;
		}
	}    

	public String serializeMainObject(XModelObject object) {
		String entity = object.getModelEntity().getName();
		String systemId = object.getAttributeValue("systemId"); //$NON-NLS-1$
        String publicId = object.getAttributeValue("publicId"); //$NON-NLS-1$
        if("null".equals(publicId)) publicId = null; //$NON-NLS-1$

        if(systemId != null) {
        	int version = entity.equals(ENT_FACESCONFIG_11) ? 11 : 10;
        	if(systemId != null && systemId.length() == 0) systemId = (version == 10) ? DOC_EXTDTD : DOC_EXTDTD_11;
        	if(publicId != null && publicId.length() == 0) publicId = (version == 10) ? DOC_PUBLICID : DOC_PUBLICID_11;
        }

        Element element = (systemId == null && publicId == null)
    	? XMLUtil.createDocumentElement(object.getModelEntity().getXMLSubPath())
        : XMLUtil.createDocumentElement(object.getModelEntity().getXMLSubPath(), DOC_QUALIFIEDNAME, publicId, systemId, null);
		
		util.setup(null, false);
        util.saveAttributes(element, object);
        util.saveChildren(element, object);
        util.saveFinalComment(element, object);
        element.removeAttribute("NAME"); //$NON-NLS-1$
        element.removeAttribute("EXTENSION"); //$NON-NLS-1$
		try {
            return SimpleWebFileLoader.serialize(element, object);
		} catch (IOException e) {
			JSFModelPlugin.getPluginLog().logError(e);
			return null;
		} catch (XModelException e) {
			JSFModelPlugin.getPluginLog().logError(e);
			return null;
		}
	}

}

class SFUtil extends XModelObjectLoaderUtil {

	static String[] folders = new String[]{
		JSFConstants.FOLDER_COMPONENTS, 
		JSFConstants.FOLDER_CONVERTERS, 
		JSFConstants.FOLDER_MANAGED_BEANS, 
		JSFConstants.FOLDER_NAVIGATION_RULES, 
		JSFConstants.FOLDER_REFENCED_BEANS, 
		JSFConstants.FOLDER_RENDER_KITS, 
		JSFConstants.FOLDER_VALIDATORS, 
		JSFConstants.FOLDER_EXTENSIONS};

	protected Set<String> getAllowedChildren(XModelEntity entity) {
		Set<String> children = super.getAllowedChildren(entity);
		if("JSFManagedProperty".equals(entity.getName()) //$NON-NLS-1$
			|| "JSFListEntries".equals(entity.getName()) //$NON-NLS-1$
			|| "JSFMapEntry".equals(entity.getName())) { //$NON-NLS-1$
			children.add("value"); //$NON-NLS-1$
			children.add("null-value"); //$NON-NLS-1$
		} else if("JSFNavigationCase".equals(entity.getName())) { //$NON-NLS-1$
			children.add("redirect"); //$NON-NLS-1$
		}
		return children;
	}

	protected Set<String> getAllowedAttributes(XModelEntity entity) {
		Set<String> attributes = super.getAllowedAttributes(entity);
		return attributes;
	}


	public void loadChildren(Element element, XModelObject o) {
		String entity = o.getModelEntity().getName();
		if(o.getFileType() == XModelObject.FILE) {
			super.loadChildren(element, o);
			for (int i = 0; i < folders.length; i++) {
				XModelObject c = o.getChildByPath(folders[i]);
				if(c != null) super.loadChildren(element, c);
			}
		} else if("JSFManagedBean".equals(entity) || "JSFManagedBean20".equals(entity)) { //$NON-NLS-1$
			loadManagedBeanChildren(element, o);
		} else if("JSFManagedProperty".equals(entity)) { //$NON-NLS-1$
			loadManagedPropertyChildren(element, o);
		} else if("JSFListEntries".equals(entity)) { //$NON-NLS-1$
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
    	
    	String s = o.getModelEntity().getProperty("saveDefault"); //$NON-NLS-1$
    	if(!"false".equals(s)) return true; //$NON-NLS-1$
    	if(hasSetAttributes(o)) return true;
    	XModelObject[] cs = o.getChildren();
    	if(cs.length > 2) return true;
    	for (int i = 0; i < cs.length; i++) {
    		if(needToSave(cs[i])) return true;
    	}

    	return false;
    }
    
    private boolean hasSetAttributes(XModelObject o) {
    	XAttribute[] as = o.getModelEntity().getAttributes();
    	for (int i = 0; i < as.length; i++) {
    		String xml = as[i].getXMLName();
    		// it would be more safe to check isSavable
    		if(xml == null || xml.length() == 0 || "NAME".equals(xml)) continue; //$NON-NLS-1$
    		String v = o.getAttributeValue(as[i].getName());
    		if(v != null && v.length() > 0) return true;
    	}
    	String finalComment = o.get("#final-comment"); //$NON-NLS-1$
    	if(finalComment != null && finalComment.length() > 0) return true;
    	return false;
    }

	public boolean saveChildren(Element element, XModelObject o) {
		String entity = o.getModelEntity().getName();
		if(o.getFileType() == XModelObject.FILE) {
			for (int i = 0; i < folders.length; i++) {
				XModelObject c = o.getChildByPath(folders[i]);
				if(c != null) super.saveChildren(element, c);
			}
			super.saveChildren(element, o);
			return true;
		} else if("JSFManagedBean".equals(entity) || "JSFManagedBean20".equals(entity)) { //$NON-NLS-1$
			saveManagedBeanChildren(element, o);
			return true;
		} else if("JSFManagedProperty".equals(entity)) { //$NON-NLS-1$
			saveManagedPropertyChildren(element, o);
			return true;
		} else if("JSFListEntries".equals(entity)) { //$NON-NLS-1$
			saveListEntriesChildren(element, o);
			return true;
		} else {
			return super.saveChildren(element, o);
		}
	}

	public void loadAttributes(Element element, XModelObject o) {
		super.loadAttributes(element, o);
		String entity = o.getModelEntity().getName();
		if("JSFListEntry".equals(entity) || "JSFMapEntry".equals(entity)) { //$NON-NLS-1$ //$NON-NLS-2$
			Element ce = XMLUtil.getUniqueChild(element, "null-value"); //$NON-NLS-1$
			if(ce != null) {
				o.setAttributeValue("null-value", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				o.setAttributeValue("value", ""); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				o.setAttributeValue("null-value", "false"); //$NON-NLS-1$ //$NON-NLS-2$
				o.setAttributeValue("value", super.getAttribute(element, "value.#text")); //$NON-NLS-1$ //$NON-NLS-2$
			}			
		}	
	}

	protected boolean isSaveable(XModelEntity entity, String n, String v, String dv) {
		if(v == null) return false;
		if(v.length() == 0 || v.equals(dv)) {
			XAttribute attr = entity.getAttribute(n);
			return (attr != null && "always".equals(attr.getProperty("save"))); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return super.isSaveable(entity, n, v, dv);
	}

	public String getAttribute(Element element, String xmlname, XAttribute attr) {
		if("attribute-names".equals(xmlname)) //$NON-NLS-1$
			return loadArray(element, "attribute-name"); //$NON-NLS-1$
		if("redirect".equals(xmlname)) //$NON-NLS-1$
			return (XMLUtil.getUniqueChild(element, "redirect") != null) ? "yes" : "no"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if("value.#text".equals(xmlname)) { //$NON-NLS-1$
		   if(XMLUtil.getUniqueChild(element, "null-value") != null) return "null-value"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return super.getAttribute(element, xmlname, attr);
	}

	public void saveAttribute(Element element, String xmlname, String value) {
		if("redirect".equals(xmlname)) { //$NON-NLS-1$
			if("yes".equals(value)) XMLUtil.createElement(element, "redirect"); //$NON-NLS-1$ //$NON-NLS-2$
		} else if("value.#text".equals(xmlname) && "null-value".equals(value)) { //$NON-NLS-1$ //$NON-NLS-2$
			XMLUtil.createElement(element, "null-value"); //$NON-NLS-1$
		} else if("attribute-names".equals(xmlname)) { //$NON-NLS-1$
			saveArray(element, "attribute-name", value); //$NON-NLS-1$
		} else {
			super.saveAttribute(element, xmlname, value);
		}
	}

	public void saveAttributes(Element element, XModelObject o) {
		super.saveAttributes(element, o);
		String entity = o.getModelEntity().getName();
		if("JSFConverter".equals(entity)) { //$NON-NLS-1$
			eitherOr(element, "converter-for-class", "converter-id"); //$NON-NLS-1$ //$NON-NLS-2$
		} else if("JSFListEntry".equals(entity) || "JSFMapEntry".equals(entity)) { //$NON-NLS-1$ //$NON-NLS-2$
			if("true".equals(o.getAttributeValue("null-value"))) { //$NON-NLS-1$ //$NON-NLS-2$
				XMLUtil.createElement(element, "null-value"); //$NON-NLS-1$
			} else {
				super.saveAttribute(element, "value.#text", o.getAttributeValue("value")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	void loadManagedBeanChildren(Element element, XModelObject o) {
		Element ce = XMLUtil.getUniqueChild(element, "list-entries"); //$NON-NLS-1$
		if(ce != null) {
			o.setAttributeValue("content-kind", "list-entries"); //$NON-NLS-1$ //$NON-NLS-2$
			XModelObject c = o.getChildByPath("Entries"); //$NON-NLS-1$
			if(c != null) load(ce, c);
			return;
		}
		ce = XMLUtil.getUniqueChild(element, "map-entries"); //$NON-NLS-1$
		if(ce != null) {
			o.setAttributeValue("content-kind", "map-entries"); //$NON-NLS-1$ //$NON-NLS-2$
			XModelObject c = o.getChildByPath("Entries"); //$NON-NLS-1$
			if(c != null) load(ce, c);
			return;
		}
		o.setAttributeValue("content-kind", "properties"); //$NON-NLS-1$ //$NON-NLS-2$
		super.loadChildren(element, o);
	}

	void loadManagedPropertyChildren(Element element, XModelObject o) {
		Element ce = XMLUtil.getUniqueChild(element, "null-value"); //$NON-NLS-1$
		if(ce != null) {
			o.setAttributeValue("value-kind", "null-value"); //$NON-NLS-1$ //$NON-NLS-2$
			o.setAttributeValue("value", ""); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		ce = XMLUtil.getUniqueChild(element, "list-entries"); //$NON-NLS-1$
		if(ce != null) {
			o.setAttributeValue("value-kind", "list-entries"); //$NON-NLS-1$ //$NON-NLS-2$
			o.setAttributeValue("value", ""); //$NON-NLS-1$ //$NON-NLS-2$
			XModelObject c = o.getChildByPath("Entries"); //$NON-NLS-1$
			if(c != null) load(ce, c);
			return;
		}
		ce = XMLUtil.getUniqueChild(element, "map-entries"); //$NON-NLS-1$
		if(ce != null) {
			o.setAttributeValue("value-kind", "map-entries"); //$NON-NLS-1$ //$NON-NLS-2$
			o.setAttributeValue("value", ""); //$NON-NLS-1$ //$NON-NLS-2$
			XModelObject c = o.getChildByPath("Entries"); //$NON-NLS-1$
			if(c != null) load(ce, c);
			return;
		}
		String v = getAttribute(element, "value.#text"); //$NON-NLS-1$
		o.setAttributeValue("value-kind", "value"); //$NON-NLS-1$ //$NON-NLS-2$
		o.setAttributeValue("value", v); //$NON-NLS-1$
	}

	void loadListEntriesChildren(Element element, XModelObject o) {
		NodeList nl = element.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if(n.getNodeType() != Node.ELEMENT_NODE) continue;
			String name = n.getNodeName();
			if(!"null-value".equals(name) && !"value".equals(name)) continue; //$NON-NLS-1$ //$NON-NLS-2$
			XModelObject c = o.getModel().createModelObject("JSFListEntry", null); //$NON-NLS-1$
			if("null-value".equals(name)) { //$NON-NLS-1$
				c.setAttributeValue("null-value", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				c.setAttributeValue("null-value", "false"); //$NON-NLS-1$ //$NON-NLS-2$
				c.setAttributeValue("value", getAttribute((Element)n, "#text")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			o.addChild(c);
		}
	}

	void saveManagedBeanChildren(Element element, XModelObject o) {
		super.saveChildren(element, o);
	}

	void saveManagedPropertyChildren(Element element, XModelObject o) {
		String kind = o.getAttributeValue("value-kind"); //$NON-NLS-1$
		if("null-value".equals(kind)) { //$NON-NLS-1$
			XMLUtil.createElement(element, "null-value"); //$NON-NLS-1$
		} else if("value".equals(kind)) { //$NON-NLS-1$
			super.saveAttribute(element, "value.#text", o.getAttributeValue("value")); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			XModelObject c = o.getChildByPath("Entries"); //$NON-NLS-1$
			if(c != null) {
				super.saveChildren(element, o);
			} else {
				XMLUtil.createElement(element, "null-value"); //$NON-NLS-1$
			}
		}		
	}

	void saveListEntriesChildren(Element element, XModelObject o) {
		XModelObject[] cs = o.getChildren();
		for (int i = 0; i < cs.length; i++) {
			boolean isNullValue = "true".equals(cs[i].getAttributeValue("null-value")); //$NON-NLS-1$ //$NON-NLS-2$
			if(isNullValue) {
				XMLUtil.createElement(element, "null-value"); //$NON-NLS-1$
			} else {
				Element ce = XMLUtil.createElement(element, "value"); //$NON-NLS-1$
				saveAttribute(ce, "#text", cs[i].getAttributeValue("value")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} 
	}

}
