package org.jboss.tools.seam.xml.ds.model;

import java.io.IOException;

import org.jboss.tools.common.model.loaders.EntityRecognizer;
import org.jboss.tools.common.xml.XMLEntityResolver;
import org.jboss.tools.seam.xml.SeamXMLPlugin;

public class DSEntityRecognizer implements EntityRecognizer, DSConstants {
    static {
        try {
            Class<?> c = DSEntityRecognizer.class;
            XMLEntityResolver.registerPublicEntity(PUBLIC_ID_1_5, c, "/meta/jboss-ds_1_5.dtd"); //$NON-NLS-1$
            XMLEntityResolver.registerPublicEntity(PUBLIC_ID_5_0, c, "/meta/jboss-ds_5_0.dtd"); //$NON-NLS-1$
        } catch (IOException e) {
			SeamXMLPlugin.log(e);
        }
    }

    public DSEntityRecognizer() {}

	public String getEntityName(String ext, String body) {
        if(body == null) return null;
    	if(body.indexOf(PUBLIC_ID_1_5) >= 0) {
			if (body.indexOf("DOCTYPE datasources") >= 0) {
				return ENT_DATASOURCES_FILE;
			}
			if (body.indexOf("DOCTYPE connection-factories") >= 0) {
				return ENT_CONNECTION_FACTORIES_FILE;
			}
    	} else if(body.indexOf(PUBLIC_ID_5_0) >= 0) {
			if (body.indexOf("DOCTYPE datasources") >= 0) {
				return ENT_DATASOURCES_FILE_50_DTD;
			}
			if (body.indexOf("DOCTYPE connection-factories") >= 0) {
				return ENT_CONNECTION_FACTORIES_FILE_50_DTD;
			}
    	}
		return null;
	}

}
