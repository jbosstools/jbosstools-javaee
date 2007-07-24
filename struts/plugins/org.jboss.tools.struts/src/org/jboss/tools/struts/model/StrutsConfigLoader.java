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
package org.jboss.tools.struts.model;

import org.jboss.tools.struts.*;
import org.jboss.tools.jst.web.model.WebProcessLoader;
import org.jboss.tools.common.meta.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.*;
import org.jboss.tools.common.model.filesystems.impl.*;
import org.jboss.tools.common.model.loaders.impl.SimpleWebFileLoader;
import org.jboss.tools.common.model.util.*;

import java.io.*;
import java.util.Set;

import org.w3c.dom.*;

public class StrutsConfigLoader implements WebProcessLoader, StrutsConstants {
	public static String LAYOUT_FILE_EXTENSION = "strutsdia";
    private FileAuxiliary aux = new FileAuxiliary(LAYOUT_FILE_EXTENSION, false);
    private SPUtil sputil = new SPUtil();
	XModelObjectLoaderUtil util = new SCUtil();
	boolean isLight = false;

    public StrutsConfigLoader() {}
    
    int getVersion(String entity) {
    	if(entity.endsWith(VER_SUFFIX_10)) return 10;
    	if(entity.endsWith(VER_SUFFIX_11)) return 11;
    	if(entity.endsWith(VER_SUFFIX_12)) return 12;
    	return 11;
    }

    public void load(XModelObject object) {
        String entity = object.getModelEntity().getName();
        int version = getVersion(entity);
        
        String body = XModelObjectLoaderUtil.getTempBody(object);
//		String encoding = XModelObjectLoaderUtil.getEncoding(body);
//		body = FileUtil.encode(body, encoding);

        int resolution = EntityXMLRegistration.getInstance().resolve(object.getModelEntity());
		String[] errors = 
			//XMLUtil.getXMLErrors(new StringReader(body));
			XMLUtil.getXMLErrors(new StringReader(body), resolution == EntityXMLRegistration.DTD, resolution == EntityXMLRegistration.SCHEMA);
		boolean hasErrors = (errors != null && errors.length > 0);
		if(hasErrors) {
            object.setAttributeValue("isIncorrect", "yes");
            object.setAttributeValue("incorrectBody", body);
			object.set("actualBodyTimeStamp", "-1");
//			return;
        } else {
            object.setAttributeValue("isIncorrect", "no");
			object.set("correctBody", body);
			object.set("actualBodyTimeStamp", "0");
            object.setAttributeValue("incorrectBody", "");
        }
        Document doc = XMLUtil.getDocument(new StringReader(body));
		if(doc == null) {
//			XModelObjectLoaderUtil.addRequiredChildren(object);
			return;
		}
        Element element = doc.getDocumentElement();
        util.load(element, object);
		setEncoding(object, body);
        NodeList nl = doc.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if(n instanceof DocumentType) {
                DocumentType dt = (DocumentType)n;
                object.setAttributeValue("systemId", dt.getSystemId());
///                object.setObject("DocumentType", dt);
            }
        }

        if (version != 10) {
            util.loadChildren(element, object.getChildren(ENT_MSGRES_FOLDER + VER_SUFFIX_11)[0]);
            util.loadChildren(element, object.getChildren(ENT_PLUGIN_FOLDER + VER_SUFFIX_11)[0]);
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
    
	protected void setEncoding(XModelObject object, String body) {
		String encoding = XModelObjectLoaderUtil.getEncoding(body);
		if(encoding == null) encoding = "";
		object.setAttributeValue(XModelObjectConstants.ATTR_NAME_ENCODING, encoding);
	}
    
//    private DocumentType getDocumentType(Element element) {
//		NodeList nl = element.getOwnerDocument().getChildNodes();
//		for (int i = 0; i < nl.getLength(); i++) {
//			Node n = nl.item(i);
//			if(n instanceof DocumentType) return (DocumentType)n;
//		}
//		return null;
//    }
    
    public void reloadProcess(XModelObject object) {
		StrutsProcessImpl process = (StrutsProcessImpl)object.getChildByPath(ELM_PROCESS);
		if(process == null) return;
		process.setReference(object);
		if(!object.isActive()) return;
		String bodyAux = (object.getParent() == null ?null:aux.read(object.getParent(), object));
		if (bodyAux != null) {
			Document doc2 = XMLUtil.getDocument(new StringReader(bodyAux));
			if (doc2 == null) {
				//S//ystem.out.println("Unable to parse aux body of "+object.getPath());
			} else {
				sputil.load(doc2.getDocumentElement(), process);
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
//		String encoding = XModelObjectLoaderUtil.getEncoding(body);
//		body = FileUtil.encode(body, encoding);
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
		try {
			XModelObject process = object.getChildByPath(ELM_PROCESS);
			if(process == null) return true;
			process.setModified(true);
			Element element = XMLUtil.createDocumentElement("PROCESS");
			sputil.saveAttributes(element, process);
			sputil.saveChildren(element, process);
			StringWriter sw = new StringWriter();
			XModelObjectLoaderUtil.serialize(element, sw);
			XModelObjectLoaderUtil.setTempBody(process, sw.toString());
			aux.write(object.getParent(), object, process);
			return true;
		} catch (Exception exc) {
            StrutsModelPlugin.getPluginLog().logError(exc);
			return false;
		}
    }    

    public String serializeMainObject(XModelObject object) {
        String entity = object.getModelEntity().getName();
        int version = getVersion(entity);
        String systemId = object.getAttributeValue("systemId");
        if(systemId == null || systemId.length() == 0) {
        	systemId = (version == 10) ? DOC_EXTDTD_10 : (version == 12) ? DOC_EXTDTD_12 : DOC_EXTDTD_11;
        }
        String xmlname = object.getModelEntity().getXMLSubPath();
        String publicId = (version == 10) ? DOC_PUBLICID_10 : (version == 12) ? DOC_PUBLICID_12 : DOC_PUBLICID_11;
        Element element = XMLUtil.createDocumentElement(xmlname, DOC_QUALIFIEDNAME, publicId, systemId, null);;
        
        try {
            util.setup(null, false);
            String att = object.getAttributeValue(ATT_ID);
            if (att.length() > 0) util.saveAttribute(element, "id", att);
            att = object.getAttributeValue("comment");
            if (att.length() > 0) util.saveAttribute(element, "#comment", att);
            util.saveChildren(element, object);
            if (version != 10) {
                util.saveChildren(element, object.getChildren(ENT_MSGRES_FOLDER + VER_SUFFIX_11)[0]);
                util.saveChildren(element, object.getChildren(ENT_PLUGIN_FOLDER + VER_SUFFIX_11)[0]);
            }
            return SimpleWebFileLoader.serialize(element, object);
        } catch (Exception e) {
            StrutsModelPlugin.getPluginLog().logError(e);
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

class SPUtil extends XModelObjectLoaderUtil implements StrutsConstants {
    static String CODE = "UTF-8";

    public void loadAttributes(Element element, XModelObject o) {
        super.loadAttributes(element, o);
        if(o.getModelEntity().getName().equals("StrutsProcessComment")) {
            String c = o.getAttributeValue("comment");
            if(c != null && c.length() > 0) {
                try {
//                    byte[] b2 = 
                    	c.getBytes();
                    byte[] b = new byte[c.length() / 2];
                    for (int i = 0, j = 0; i < b.length; i++, j += 2) {
                        char c2 = c.charAt(j), c1 = c.charAt(j + 1);
                        int i1 = HEX.indexOf(c1), i2 = HEX.indexOf(c2);
                        b[i] = (byte)(i2 * 16 + i1);
                    }
                    c = new String(b, CODE);
                    o.setAttributeValue("comment", c);
                } catch (Exception e) {
                    StrutsModelPlugin.getPluginLog().logError(e);
                }
            }
        }
    }
    static String HEX = "0123456789abcdef";
    static char[] HEX_c = HEX.toCharArray();

    public void saveAttribute(Element element, String xmlname, String value) {
        if(element.getNodeName().equals("COMMENT") && xmlname.equals("CDATA") && value.length() > 0) {
            try {
                byte[] b = value.getBytes(CODE);
                StringBuffer sb = new StringBuffer();
                for (int i = 0, j = 0; i < b.length; i++, j += 2) {
                    int i2 = ((256 + b[i]) / 16) % 16;
                    int i1 = (256 + b[i]) % 16;
                    sb.append(HEX_c[i2]).append(HEX_c[i1]);
                }
                value = sb.toString();
            } catch (Exception e) {
                StrutsModelPlugin.getPluginLog().logError(e);
            }
        }
        super.saveAttribute(element, xmlname, value);
    }

}

class SCUtil extends XModelObjectLoaderUtil implements StrutsConstants {
	protected Set<String> getAllowedChildren(XModelEntity entity) {
		Set<String> children = super.getAllowedChildren(entity);
		return children;
	}

	protected boolean isSaveable(XModelEntity entity, String n, String v, String dv) {
		if(v == null) return false;
		if(v.length() == 0 || v.equals(dv)) {
			XAttribute attr = entity.getAttribute(n);
			return (attr != null && "always".equals(attr.getProperty("save")));
		}
		return super.isSaveable(entity, n, v, dv);
	}
}