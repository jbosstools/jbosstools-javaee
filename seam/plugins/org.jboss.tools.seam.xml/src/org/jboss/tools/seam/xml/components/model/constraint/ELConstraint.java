package org.jboss.tools.seam.xml.components.model.constraint;

import org.jboss.tools.common.meta.constraint.XAttributeConstraint;
import org.jboss.tools.common.meta.constraint.impl.XAttributeConstraintImpl;

public class ELConstraint extends XAttributeConstraintImpl {
	public static ELConstraint INSTANCE = new ELConstraint();

	public static XAttributeConstraint getInstance() {
		return INSTANCE;
	}

    public boolean accepts(String value) {
    	if(value != null) {
    		if((value.startsWith("#{") || value.startsWith("${")) && value.endsWith("}")) {
    			return true;
    		}
    		if(value.length() >= 2 && value.startsWith("@") && value.endsWith("@")) {
    			return true;
    		}
    	}
        return false;
    }

    /**
     * Returns not null only if value starts with EL tokens.
     */
    public String getError(String value) {
    	if(accepts(value)) return null;
    	if(value.startsWith("#{") || value.startsWith("${")) {
    		return "value is not a correct EL."; 
    	}
    	if(value.startsWith("@")) {
    		return "value is not a correct property."; 
    	}
    	
        return super.getError(value);
    }

}
