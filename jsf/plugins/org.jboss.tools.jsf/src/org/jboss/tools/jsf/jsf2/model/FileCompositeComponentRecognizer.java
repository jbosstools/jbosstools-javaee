package org.jboss.tools.jsf.jsf2.model;

import org.jboss.tools.common.model.loaders.EntityRecognizer;
import org.jboss.tools.common.model.loaders.EntityRecognizerContext;

public class FileCompositeComponentRecognizer implements EntityRecognizer, CompositeComponentConstants {

	public FileCompositeComponentRecognizer() {}

    public String getEntityName(EntityRecognizerContext context) {
    	return getEntityName(context.getExtension(), context.getBody());
    }

	String getEntityName(String ext, String body) {
		if(body == null) return null;
		if(isComponents(body, COMPOSITE_XMLNS)) {
			return ENT_FILE_COMPONENT;
		}
		if(isComponents(body, COMPOSITE_XMLNS_2_2)) {
			return ENT_FILE_COMPONENT_22;
		}
		return null;
	}

    private boolean isComponents(String body, String uri) {
    	String q = "\""; //$NON-NLS-1$
    	int i = body.indexOf(q + uri + q);
    	while(i > 0) {
    		int j = body.lastIndexOf("xmlns", i); //$NON-NLS-1$
    		if(j > 0) {
    			int k = body.indexOf("=", j); //$NON-NLS-1$
    			if(k > j && k < i) {
    				int l = body.indexOf(q, k);
    				if(l == i) {
    					return true;
    				}
    			}
    		}
    		i = body.indexOf(q + uri + q, i + 1);
    	}
    	return false;
    }

}
