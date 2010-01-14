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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jboss.tools.common.model.loaders.EntityRecognizer;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Viacheslav Kabanovich
 */
public class FaceletTaglibEntityRecognizer implements EntityRecognizer,
		FaceletTaglibConstants {

	public String getEntityName(String ext, String body) {
		if (body == null)
			return null;
		String doctype = getUnforamtedDoctypeFromBody(body);
		if (doctype != null && !doctype.equals("")) { //$NON-NLS-1$
			doctype = checkDocType(doctype);
			if (doctype.indexOf(DOC_PUBLICID) > -1)
				return ENT_FACELET_TAGLIB;
		}
		if (is20(body))
			return ENT_FACELET_TAGLIB_20;
		return null;
	}

	private boolean is20(String body) {
		int i = body.indexOf("<facelet-taglib"); //$NON-NLS-1$
		if (i < 0)
			return false;
		int j = body.indexOf(">", i); //$NON-NLS-1$
		if (j < 0)
			return false;
		String s = body.substring(i, j+1);
		return s.indexOf("version=\"2.0\"") > 0 && //$NON-NLS-1$
				s.indexOf("\"http://java.sun.com/xml/ns/javaee\"") > 0; //$NON-NLS-1$
	}

	private String checkDocType(String docTypeString) {
		final StringBuffer docTypeBuffer = new StringBuffer(""); //$NON-NLS-1$
		Reader xml = new StringReader(docTypeString + "<root></root>"); //$NON-NLS-1$
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setValidating(true);
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			db.setErrorHandler(new ErrorHandler() {
				public void warning(SAXParseException exception)
						throws SAXException {
				}

				public void fatalError(SAXParseException exception)
						throws SAXException {
				}

				public void error(SAXParseException exception)
						throws SAXException {
				}
			});
			db.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId,
						String systemId) throws SAXException, IOException {
					docTypeBuffer.append(publicId);
					return new InputSource(new StringReader("")); //$NON-NLS-1$
				}
			});
			@SuppressWarnings("unused")
			Document dom = db.parse(new InputSource(xml));
		} catch (Exception e) {
			return docTypeBuffer.toString();
		} finally {
			try {
				xml.close();
			} catch (IOException e) {
			}
		}
		return docTypeBuffer.toString();
	}

	private String getUnforamtedDoctypeFromBody(String body) {
		int i = body.indexOf("<!DOCTYPE"); //$NON-NLS-1$
		if (i < 0)
			return null;
		int j = body.indexOf(">", i); //$NON-NLS-1$
		if (j < 0)
			return null;
		return body.substring(i, j+1);
	}

}
