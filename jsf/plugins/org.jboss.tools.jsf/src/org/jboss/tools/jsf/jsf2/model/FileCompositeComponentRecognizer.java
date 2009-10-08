package org.jboss.tools.jsf.jsf2.model;

import org.jboss.tools.common.model.loaders.EntityRecognizer;

public class FileCompositeComponentRecognizer implements EntityRecognizer, CompositeComponentConstants {

	public FileCompositeComponentRecognizer() {}

	public String getEntityName(String ext, String body) {
		if(body == null) return null;
		if(isComponents(body)) {
			return ENT_FILE_COMPONENT;
		}
		return null;
	}

    private boolean isComponents(String body) {
    	int i = body.indexOf("<html"); //$NON-NLS-1$
    	if(i < 0) return false;
    	int j = body.indexOf(">", i); //$NON-NLS-1$
    	if(j < 0) return false;
    	String s = body.substring(i, j);
    	return s.indexOf(COMPOSITE_XMLNS) > 0;
    }
    
}
