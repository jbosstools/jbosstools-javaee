package org.jboss.tools.seam.xml.ds.model;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.w3c.dom.Element;

public class DSFileLoaderUtil extends XModelObjectLoaderUtil implements DSConstants {

	public DSFileLoaderUtil() {}

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

}
