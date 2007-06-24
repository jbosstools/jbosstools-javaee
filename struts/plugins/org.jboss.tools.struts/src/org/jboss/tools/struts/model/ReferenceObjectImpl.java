/*
 * ReferenceObjectImpl.java
 *
 * Created on February 21, 2003, 11:57 AM
 */

package org.jboss.tools.struts.model;

import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.*;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.jst.web.model.ReferenceObject;

/**
 *
 * @author  valera
 */
public class ReferenceObjectImpl extends OrderedObjectImpl implements ReferenceObject, StrutsConstants {
	private static final long serialVersionUID = 372131305519215868L;

    protected XModelObject reference;

    /** Creates a new instance of ReferenceObjectImpl */
    public ReferenceObjectImpl() {
    }

    public XModelObject getReference() {
        return reference;
    }

    public void setReference(XModelObject reference) {
        this.reference = reference;
        if(reference != null) {
            String shape = get("SHAPE");
            if(shape != null && shape.length() > 0) reference.set("_shape", shape);
        }
    }

    public String getPresentationString() {
        String title = (reference != null) ? reference.getPresentationString() :
                  (TYPE_ACTION.equals(getAttributeValue(ATT_TYPE)) ||
                   TYPE_PAGE.equals(getAttributeValue(ATT_TYPE))
                  ) ? getAttributeValue(ATT_PATH)
                  : getAttributeValue("name");
        if(title == null) title = getAttributeValue("title");
        return title;
    }

    public String getMainIconName() {
        String type = getAttributeValue(ATT_TYPE);
        if(type == null || type.length() == 0) return super.getMainIconName();
        if(type.equals(TYPE_ACTION)) {
//            String subtype = getAttributeValue(ATT_SUBTYPE);
//            if(SUBTYPE_FORWARD.equals(subtype)) return "main.struts.forward";
//            if(SUBTYPE_FORWARDACTION.equals(subtype)) return "main.struts.forward";
//            if(SUBTYPE_SWITCH.equals(subtype)) return "main.struts.forward";
            return "main.struts.action";
        }
        if(type.equals(TYPE_FORWARD)) return "main.struts.forward";
        if(type.equals(TYPE_EXCEPTION)) return "main.struts.exception";
        if(type.equals(TYPE_PAGE)) {
            String subtype = getAttributeValue(ATT_SUBTYPE);
            if(subtype == null) {}
            else if(subtype.equals(SUBTYPE_JSP)) return "main.file.jsp_file";
            else if(subtype.equals(SUBTYPE_HTML)) return "main.file.html_file";
            else if(subtype.equals(SUBTYPE_TILE)) return "main.web.tiles.definition";
            else return "main.file.unknow_file";
        } else if(type.equals(TYPE_LINK)) {
            String subtype = getAttributeValue(ATT_SUBTYPE);
            if(subtype == null) {}
            else if(subtype.equals(SUBTYPE_FORWARD)) return "main.struts.forward";
            else if(subtype.equals(TYPE_EXCEPTION)) return "main.struts.exception";
        }
        return super.getMainIconName();
    }

    public Image getIcon() {
        return (reference != null) ? reference.getImage() : super.getImage();
    }

    public void set(String name, String value) {
        if("SHAPE".equals(name) && reference != null) {
            reference.set("_shape", value);
        }
        super.set(name, value);
    }

}

