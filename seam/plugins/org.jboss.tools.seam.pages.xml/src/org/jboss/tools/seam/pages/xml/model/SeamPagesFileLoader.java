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
package org.jboss.tools.seam.pages.xml.model;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.FileAuxiliary;
import org.jboss.tools.common.model.filesystems.impl.AbstractXMLFileImpl;
import org.jboss.tools.common.model.loaders.AuxiliaryLoader;
import org.jboss.tools.common.model.loaders.impl.SimpleWebFileLoader;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.common.model.util.EntityXMLRegistration;
import org.jboss.tools.common.model.util.XMLUtil;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jst.web.model.AbstractWebDiagramLoader;
import org.jboss.tools.jst.web.model.WebProcessLoader;
import org.jboss.tools.seam.pages.xml.model.impl.SeamPagesDiagramImpl;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SeamPagesFileLoader extends AbstractWebDiagramLoader implements WebProcessLoader, SeamPagesConstants, AuxiliaryLoader {
	public static String AUXILIARY_FILE_EXTENSION = "spdia";

    public SeamPagesFileLoader() {}

    protected FileAuxiliary createFileAuxiliary() {
    	return new FileAuxiliary(AUXILIARY_FILE_EXTENSION, false);
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
		
//		((FileSeamPagesImpl)object).updateRuleIndices();
		
		setEncoding(object, body);
		if(object.getModelEntity().getAttribute("systemId") != null) {
			NodeList nl = doc.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if(n instanceof DocumentType) {
					DocumentType dt = (DocumentType)n;
					object.setAttributeValue("systemId", dt.getSystemId());
					if(dt.getPublicId() == null) {
						object.setAttributeValue("publicId", "null");
					}
				}
			}
		}
		String loadingError = util.getError();
		reloadProcess(object);

		object.set("actualBodyTimeStamp", "" + object.getTimeStamp());
		((AbstractXMLFileImpl)object).setLoaderError(loadingError);
		if(!hasErrors && loadingError != null) {
			object.setAttributeValue("isIncorrect", "yes");
			object.setAttributeValue("incorrectBody", body);
			object.set("actualBodyTimeStamp", "" + object.getTimeStamp());
		}
	}
    
	public void reloadProcess(XModelObject object) {
		SeamPagesDiagramImpl diagram = (SeamPagesDiagramImpl)object.getChildByPath(ELM_DIAGRAM);
		if(diagram == null) return;
		diagram.setReference(object);
		if(!object.isActive()) return;
		String bodyAux = (object.getParent() == null ? null : aux.read(object.getParent(), object));
		if (bodyAux != null) {
			Document doc2 = XMLUtil.getDocument(new StringReader(bodyAux));
			if (doc2 == null) {
				//JSFModelPlugin.log("Unable to parse aux body of "+object.getPath());
			} else {
				util.load(doc2.getDocumentElement(), diagram);
			}
		}
		diagram.setReference(null);
		diagram.firePrepared();
	}
    
	public boolean saveLayout(XModelObject object) {
		if(object == null || !object.isActive()) return false;
		XModelObjectLoaderUtil util = new XModelObjectLoaderUtil();
		try {
			XModelObject diagram = object.getChildByPath(ELM_DIAGRAM);
			if(diagram == null) return true;
			diagram.setModified(true);
			Element element = XMLUtil.createDocumentElement("diagram");
			util.saveAttributes(element, diagram);
			util.saveChildren(element, diagram);
			StringWriter sw = new StringWriter();
			XModelObjectLoaderUtil.serialize(element, sw);
			XModelObjectLoaderUtil.setTempBody(diagram, sw.toString());
			aux.write(object.getParent(), object, diagram);
			return true;
		} catch (IOException exc) {
			ModelPlugin.getPluginLog().logError(exc);
			return false;
		}
	}    

	public String serializeMainObject(XModelObject object) {
//		String entity = object.getModelEntity().getName();
		String systemId = object.getAttributeValue("systemId");
        String publicId = object.getAttributeValue("publicId");
        if("null".equals(publicId)) publicId = null;

        if(systemId != null) {
        	if(systemId != null && systemId.length() == 0) systemId = SYSTEM_ID_12;
        	if(publicId != null && publicId.length() == 0) publicId = PUBLIC_ID_12;
        }

        Element element = (systemId == null && publicId == null)
    	? XMLUtil.createDocumentElement(object.getModelEntity().getXMLSubPath())
        : XMLUtil.createDocumentElement(object.getModelEntity().getXMLSubPath(), DOC_QUALIFIEDNAME, publicId, systemId, null);
		
		util.setup(null, false);
        util.saveAttributes(element, object);
        util.saveChildren(element, object);
        util.saveFinalComment(element, object);
        element.removeAttribute("NAME");
        element.removeAttribute("EXTENSION");
		try {
            return SimpleWebFileLoader.serialize(element, object);
		} catch (IOException e) {
			ModelPlugin.getPluginLog().logError(e);
			return null;
		} catch (XModelException e) {
			ModelPlugin.getPluginLog().logError(e);
			return null;
		}
	}

    protected XModelObjectLoaderUtil createUtil() {
        return new SeamPagesLoaderUtil();
    }

}
