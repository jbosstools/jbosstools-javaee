package org.jboss.tools.seam.xml.components.model.constraint;

import org.jboss.tools.common.meta.constraint.impl.XAttributeConstraintInt;

public class IntELConstraint extends XAttributeConstraintInt {

    public boolean accepts(String value) {
    	if(ELConstraint.getInstance().accepts(value)) {
    		return true;
    	}
        return super.accepts(value);
    }

    public String getError(String value) {
    	if(accepts(value)) return null;
    	String error = ELConstraint.getInstance().getError(value);
    	if(error != null) {
    		return error;
    	}
        return super.getError(value);
    }

}
