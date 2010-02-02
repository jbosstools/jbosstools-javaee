/*
 * StrutsAuxEntityRecognizer.java
 *
 * Created on February 24, 2003, 10:03 AM
 */

package org.jboss.tools.struts.model;

import org.jboss.tools.common.model.loaders.*;

/**
 *
 * @author  valera
 */
public class StrutsAuxEntityRecognizer implements EntityRecognizer {
    
    /** Creates a new instance of StrutsAuxEntityRecognizer */
    public StrutsAuxEntityRecognizer() {
    }
    
    public String getEntityName(EntityRecognizerContext context) {
    	String body = context.getBody();
        if (body == null) return null;
        return StrutsConfigLoader.LAYOUT_FILE_EXTENSION.equals(context.getExtension()) ? "FileAnyAuxiliary" : null;
    }
    
}
