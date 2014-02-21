/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.facelet.model;

import org.jboss.tools.common.model.loaders.EntityRecognizer;
import org.jboss.tools.common.model.loaders.EntityRecognizerContext;
import org.jboss.tools.common.model.loaders.XMLRecognizerContext;

/**
 * @author Viacheslav Kabanovich
 */
public class FaceletTaglibEntityRecognizer implements EntityRecognizer,
		FaceletTaglibConstants {

    public String getEntityName(EntityRecognizerContext context) {
    	String body = context.getBody();
		if (body == null)
			return null;
		XMLRecognizerContext xml = context.getXMLContext();
		if(xml.isDTD()) {
			String publicId = xml.getPublicId();
			if(DOC_PUBLICID.equals(publicId)) {
				return ENT_FACELET_TAGLIB;
			}
		} else {
			int version = getVersion(body);
			if(version == 20) return ENT_FACELET_TAGLIB_20;
			if(version == 22) return ENT_FACELET_TAGLIB_22;
		}
		return null;
	}

	private int getVersion(String body) {
		int i = body.indexOf("<facelet-taglib"); //$NON-NLS-1$
		if (i < 0)
			return -1;
		int j = body.indexOf(">", i); //$NON-NLS-1$
		if (j < 0)
			return -1;
		String s = body.substring(i, j+1);
		if(s.indexOf("version=\"2.0\"") > 0 && //$NON-NLS-1$
				s.indexOf("\"http://java.sun.com/xml/ns/javaee\"") > 0) { //$NON-NLS-1$
			return 20;
		}
		if(s.indexOf("version=\"2.2\"") > 0 && //$NON-NLS-1$
				s.indexOf("\"http://xmlns.jcp.org/xml/ns/javaee\"") > 0) { //$NON-NLS-1$
			return 22;
		}
		return -1;
	}

}
