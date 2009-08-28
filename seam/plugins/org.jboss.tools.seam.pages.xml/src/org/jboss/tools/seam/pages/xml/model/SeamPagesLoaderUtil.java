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

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.common.xml.XMLUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SeamPagesLoaderUtil extends XModelObjectLoaderUtil implements SeamPagesConstants {
	static String[] folders = new String[]{"Conversations", "Pages", "Exceptions"};
	
	public SeamPagesLoaderUtil() {}

	protected boolean isSaveable(XModelEntity entity, String n, String v, String dv) {
		if(v == null) return false;
		if(v.length() == 0 || v.equals(dv)) {
			XAttribute attr = entity.getAttribute(n);
			return (attr != null && "always".equals(attr.getProperty("save"))); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return super.isSaveable(entity, n, v, dv);
	}

    public String getAttribute(Element element, String xmlname) {
        int i = xmlname.indexOf('|');
        if(i < 0) return super.getAttribute(element, xmlname);
        String v = super.getAttribute(element, xmlname.substring(0, i));
        return (v != null && v.length() > 0) ? v :
               super.getAttribute(element, xmlname.substring(i + 1));
    }

    public void saveAttribute(Element element, String xmlname, String value) {
        int i = xmlname.indexOf('|');
        if(i >= 0) xmlname = xmlname.substring(0, i);
        super.saveAttribute(element, xmlname, value);
    }

    public boolean save(Element parent, XModelObject o) {
    	if(!needToSave(o)) return true;
    	return super.save(parent, o);
    }

    protected boolean needToSave(XModelObject o) {
    	String s = o.getModelEntity().getProperty("saveDefault"); //$NON-NLS-1$
    	if(!"false".equals(s)) return true; //$NON-NLS-1$
    	if(hasSetAttributes(o)) return true;
    	if(o.getChildren().length > 0) return true;
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
    	String childrenLoader = o.getModelEntity().getProperty("childrenLoader"); //$NON-NLS-1$
    	if(o.getFileType() == XModelObject.FILE) {
			for (int i = 0; i < folders.length; i++) {
				XModelObject c = o.getChildByPath(folders[i]);
				if(c != null) super.saveChildren(element, c);
			}
			super.saveChildren(element, o);
			return true;
		} else {
			return super.saveChildren(element, o);
		}
	}
    
    private boolean savePropertyMapChildren(Element element, XModelObject o) {
    	XModelObject[] cs = o.getChildren();
    	for (int i = 0; i < cs.length; i++) {
    		Element k = XMLUtilities.createElement(element, "key"); //$NON-NLS-1$
    		saveAttribute(k, "#text", cs[i].getAttributeValue("key")); //$NON-NLS-1$ //$NON-NLS-2$
    		Element v = XMLUtilities.createElement(element, "value"); //$NON-NLS-1$
    		saveAttribute(v, "#text", cs[i].getAttributeValue("value")); //$NON-NLS-1$ //$NON-NLS-2$
    	}
    	return true;
    }

    public void loadChildren(Element element, XModelObject o) {
//    	String entity = o.getModelEntity().getName();
    	if(o.getFileType() == XModelObject.FILE) {
			super.loadChildren(element, o);
			for (int i = 0; i < folders.length; i++) {
				XModelObject c = o.getChildByPath(folders[i]);
				if(c != null) super.loadChildren(element, c);
			}
		} else {
			super.loadChildren(element, o);
		}
    }
    
    protected String getChildEntity(XModelEntity entity, Element e) {
    	String n = e.getNodeName();
    	if("page".equals(entity.getXMLSubPath())) {
    		String suff = (entity.getName().endsWith(SUFF_12))
    			? SUFF_12
    			: (entity.getName().endsWith(SUFF_20))
    			? SUFF_20
    			: (entity.getName().endsWith(SUFF_21))
    			? SUFF_21
    			: (entity.getName().endsWith(SUFF_22))
    			? SUFF_22
    			: null;
    		if(suff == null) {
    			System.out.println("Unknown suffix in seam page entity " + entity.getName());
    			suff = SUFF_20;
    		}
    		if("navigation".equals(n)) {
    			NodeList nl = e.getChildNodes();
    			for (int i = 0; i < nl.getLength(); i++) {
    				Node ni = nl.item(i);
    				if(ni.getNodeType() == Node.ELEMENT_NODE) {
    					if(ni.getNodeName().equals("rule")) {
    						return ENT_NAVIGATION + suff;
    					} else {
    						return ENT_NAVIGATION_RULE + suff;
    					}
    				}
    			}
   				return ENT_NAVIGATION + suff;
    		}
    	}

    	return super.getChildEntity(entity, e);
    }

    public void load(Element element, XModelObject o) {
    	String entity = o.getModelEntity().getName();
    	String childrenLoader = o.getModelEntity().getProperty("childrenLoader"); //$NON-NLS-1$

   		super.load(element, o);
    }
    
    void loadPropertyMap(Element element, XModelObject o) {
    	loadAttributes(element, o);
    	XModelObject last = null;
    	NodeList nl = element.getChildNodes();
    	for (int i = 0; i < nl.getLength(); i++) {
    		Node n = nl.item(i);
    		if(n.getNodeType() != Node.ELEMENT_NODE) continue;
    		if(n.getNodeName().equals("key")) { //$NON-NLS-1$
    			last = o.getModel().createModelObject("SeamMapEntry", null); //$NON-NLS-1$
    			last.setAttributeValue("key", getAttribute((Element)n, "#text")); //$NON-NLS-1$ //$NON-NLS-2$
    			o.addChild(last);
    		} else if(n.getNodeName().equals("value")) { //$NON-NLS-1$
    			if(last == null) {
        			last = o.getModel().createModelObject("SeamMapEntry", null); //$NON-NLS-1$
        			o.addChild(last);
    			}
    			last.setAttributeValue("value", getAttribute((Element)n, "#text")); //$NON-NLS-1$ //$NON-NLS-2$
    			last = null;
    		}
    	}
    }

}
