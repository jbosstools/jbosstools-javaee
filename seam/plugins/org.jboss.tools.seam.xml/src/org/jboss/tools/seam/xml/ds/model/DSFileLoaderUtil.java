package org.jboss.tools.seam.xml.ds.model;

import java.util.Set;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.meta.constraint.impl.XAttributeConstraintAList;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.common.xml.XMLUtilities;
import org.w3c.dom.Element;

public class DSFileLoaderUtil extends XModelObjectLoaderUtil implements DSConstants {

	public DSFileLoaderUtil() {}

	protected boolean isSaveable(XModelEntity entity, String n, String v, String dv) {
		if(v == null) return false;
		if(v.length() == 0 || v.equals(dv)) {
			XAttribute attr = entity.getAttribute(n);
			return (attr != null && "always".equals(attr.getProperty("save"))); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return super.isSaveable(entity, n, v, dv);
	}

	public boolean save(Element parent, XModelObject o) {
    	if(!needToSave(o)) return true;
    	boolean b = super.save(parent, o);
    	//TODO check dtd
    	return b;
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

    public void saveAttribute(Element element, String xmlname, String value) {
    	if(ATTR_TRACK_CONN.equals(xmlname)) {
    		XMLUtilities.createElement(element, xmlname);
    	} else if(ATTR_TRANSACTION.equals(xmlname)) {
    		if(value.length() > 0) XMLUtilities.createElement(element, value);
    	} else if(ATTR_SECURITY_TYPE.equals(xmlname)) {
    		if(value.length() > 0) XMLUtilities.createElement(element, value);
    	} else if(ATTR_SECURITY_DOMAIN.equals(xmlname)) {
    		Element e = XMLUtilities.getUniqueChild(element, "security-domain");
    		if (e == null) e = XMLUtilities.getUniqueChild(element, "security-domain-and-application");
    		if(e != null) super.saveAttribute(e, "#text", value);
    	} else {
    		super.saveAttribute(element, xmlname, value);
    	}
    }

    public String getAttribute(Element element, String xmlname, XAttribute attr) {
    	if(ATTR_TRACK_CONN.equals(xmlname)) {
    		return (XMLUtilities.getUniqueChild(element, xmlname) != null) ? "true" : "false"; //$NON-NLS-1$ //$NON-NLS-2$
    	} else if(ATTR_TRANSACTION.equals(xmlname) || ATTR_SECURITY_TYPE.equals(xmlname)) {
    		String[] vs = ((XAttributeConstraintAList)attr.getConstraint()).getValues();
    		for (int i = 0; i < vs.length; i++) {
    			if(XMLUtilities.getUniqueChild(element, vs[i]) != null) return vs[i];
    		}
			return "";
    	} else if(ATTR_SECURITY_DOMAIN.equals(xmlname)) {
    		XAttribute a = attr.getModelEntity().getAttribute(ATTR_SECURITY_TYPE);
    		String[] vs = ((XAttributeConstraintAList)a.getConstraint()).getValues();
    		for (int i = 0; i < vs.length; i++) {
    			if(XMLUtilities.getUniqueChild(element, vs[i]) != null) return super.getAttribute(element, vs[i] + ".#text");
    		}
			return "";
    	} else {
    		return super.getAttribute(element, xmlname, attr);
    	}
    }

	protected Set<String> getAllowedChildren(XModelEntity entity) {
		Set<String> children = super.getAllowedChildren(entity);
		XAttribute a = entity.getAttribute(ATTR_TRACK_CONN);
		if(a != null) children.add(a.getXMLName());
		a = entity.getAttribute(ATTR_TRANSACTION);
		if(a != null) {
    		String[] vs = ((XAttributeConstraintAList)a.getConstraint()).getValues();
    		for (int i = 0; i < vs.length; i++) {
    			children.add(vs[i]);
    		}			
		}
		a = entity.getAttribute(ATTR_SECURITY_TYPE);
		if(a != null) {
    		String[] vs = ((XAttributeConstraintAList)a.getConstraint()).getValues();
    		for (int i = 0; i < vs.length; i++) {
    			children.add(vs[i]);
    		}			
		}
		return children;
	}
}
