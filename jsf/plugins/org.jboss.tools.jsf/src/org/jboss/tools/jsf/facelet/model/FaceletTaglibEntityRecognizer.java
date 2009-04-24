package org.jboss.tools.jsf.facelet.model;

import org.jboss.tools.common.model.loaders.EntityRecognizer;

public class FaceletTaglibEntityRecognizer implements EntityRecognizer, FaceletTaglibConstants {

    public String getEntityName(String ext, String body) {
        if (body == null) return null;
        if (body.indexOf(DOC_PUBLICID) > 0) return ENT_FACELET_TAGLIB;
        return null;
    }

}
