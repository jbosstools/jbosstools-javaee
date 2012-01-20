/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.seam.config.core.xml;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.jboss.tools.common.CommonPlugin;
import org.jboss.tools.common.Messages;
import org.jboss.tools.common.xml.SAXValidator;
import org.jboss.tools.common.xml.XMLEntityResolverImpl;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SAXParser extends SAXValidator {
	/**
	 * If this limit is set to 0, parser will abort on the very first fatal error.
	 * In that case, the completing of parsing process is guaranteed, though parser
	 * will not be so smart as to extract maximum of correct data for the model.
	 * 
	 * If this limit is set to a positive number, parser will be smart, but is not 
	 * secured against making a mistake.
	 * 
	 */
	int supperssedFatalErrorLimit = 0;

	public void setSupperssedFatalErrorLimit(int c) {
		supperssedFatalErrorLimit = c;
	}

	/**
	 * 
	 * @param handler
	 * @return instanceof XMLReader or null if Apache SAX parser is not available.
	 */
	XMLReader createParser1(DefaultHandler handler) {
		XMLReader parserInstance = null;

		//XMLReaderFactory.createXMLReader(DEFAULT_SAX_PARSER_CLASS_NAME);
		parserInstance = new org.apache.xerces.parsers.SAXParser();

		setFeature(parserInstance, NAMESPACES_FEATURE_ID, true);
		setFeature(parserInstance, NAMESPACE_PREFIXES_FEATURE_ID, false);
		setFeature(parserInstance, VALIDATION_FEATURE_ID, true);
		setFeature(parserInstance, VALIDATION_SCHEMA_FEATURE_ID, true);
		setFeature(parserInstance, VALIDATION_SCHEMA_CHECKING_FEATURE_ID, false);
		setFeature(parserInstance, VALIDATION_DYNAMIC_FEATURE_ID, false);
		setFeature(parserInstance, FATAL_ERROR_PROCESSING_FEATURE_ID, supperssedFatalErrorLimit > 0);

		try {
			parserInstance.setProperty(ENTITY_RESOLVER_PROPERTY_ID, new XMLEntityResolverImpl());
		} catch (SAXNotRecognizedException e1) {
			CommonPlugin.getPluginLog().logError( e1.getMessage()+"", e1); //$NON-NLS-1$
		} catch (SAXNotSupportedException e1) {
			CommonPlugin.getPluginLog().logError( e1.getMessage()+"", e1); //$NON-NLS-1$
		}
        
		parserInstance.setContentHandler(handler);
		parserInstance.setErrorHandler(handler);
		return parserInstance;
	}

	private String errorMessage = null;
	List<String> errors = new ArrayList<String>();

	public SAXElement parse(InputStream input, IDocument document) {
		InputSource s = new InputSource(input);
		ConfigHanlder handler = new ConfigHanlder(document);
		XMLReader reader = createParser1(handler);
		try {
			if(reader != null) {
				reader.parse(s);
				errorMessage = null;
			} else if(errorMessage == null) {
				//Report only the first failure.
				errorMessage = MessageFormat.format(
					Messages.SAXValidator_UnableToInstantiateMessage, DEFAULT_SAX_PARSER_CLASS_NAME);
        		CommonPlugin.getDefault().logError(errorMessage);				
			}
		} catch (IOException e) {
			CommonPlugin.getDefault().logError(e);
		} catch (SAXException e) {
			//ignore, that is user data error that will be shown as error marker.
		}
		
		errors = handler.errors;
		
		return handler.getRootElement();
	}

	public List<String> getErrors() {
		return errors;
	}

	class ConfigHanlder extends DefaultHandler {
		SAXElement root = null;
		IDocument document;
		Locator locator;
		List<String> errors = new ArrayList<String>();
		SAXElement current = null;
	
		StringBuffer currentText = new StringBuffer();
		Location currentTextLocation = null;

		ConfigHanlder(IDocument document) {
			this.document = document;
		}
	
		public SAXElement getRootElement() {
			return root;
		}

		public void setDocumentLocator (Locator locator) {
			this.locator = locator;
		}
	
		private int getCurrentLocation() {
			if (locator != null) {
				int line = locator.getLineNumber() - 1;
				int lineOffset = locator.getColumnNumber() - 1;
				try {
					return document.getLineOffset(line) + lineOffset;
				} catch (BadLocationException e) {
					CommonPlugin.getDefault().logError(e);
				}
			}
			return 0;
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			int end = getCurrentLocation(), start = 0;
			if(end > 0) {
				start = document.get().lastIndexOf("<", end - 1);
			}
			SAXElement element = new SAXElement();
			element.setLocalName(localName);
			element.setURI(uri);
			element.setName(qName);
			element.setParent(current);
			if(start >= 0) {
				element.setLocation(new Location(start, end - start, getLine(start)));
				int ns = document.get().indexOf(qName, start);
				if(ns >= start) {
					element.setNameLocation(new Location(ns, qName.length(), getLine(ns)));
				}
			}
			for (int i = 0; i < attributes.getLength(); i++) {
				String n = attributes.getLocalName(i);
				String v = attributes.getValue(i);
				SAXAttribute a = new SAXAttribute();
				a.setName(n);
				a.setValue(v);
				int n_start = document.get().indexOf(n, start);
				if(n_start >= 0) {
					a.setNameLocation(new Location(n_start, n.length(), getLine(n_start)));
					int v_start = document.get().indexOf('"', n_start);
					if(v_start >= 0) {
						a.setValueLocation(new Location(v_start + 1, v.length(), getLine(v_start)));
					}
				}
				//TODO
				element.addAttribute(a);
			}
			
			current = element;
			currentText = new StringBuffer();
			currentTextLocation = null;
			if(root == null) root = element;
		}
	
		public void characters (char ch[], int start, int length) throws SAXException {
			String append = new String(ch, start, length);
			if(append.trim().length() == 0) return;
			int end = getCurrentLocation(), s = 0;
			if (end > 0) {
				s = end - length;
			}
			currentText.append(append);
			currentTextLocation = new Location(s, length, getLine(s));
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			int end = getCurrentLocation();
			if(end > 0) {
				int start = document.get().lastIndexOf("<", end);
				Location endLocation = new Location(start, end - start, getLine(start));
				//TODO
			}
			if(currentText.length() > 0) {
				SAXText text = new SAXText();
				text.setValue(currentText.toString());
				current.setTextNode(text);
				text.setLocation(currentTextLocation);
				currentText.setLength(0);
			}
		
			if(current.getParent() != null) {
				current.getParent().addChildElement(current);
			}
			
			current = current.getParent();
		}


		public void error(SAXParseException e) throws SAXException {
			String message = e.getMessage();
			errors.add(message);
		}

		public void fatalError(SAXParseException e) throws SAXException {
			String message = e.getMessage();
			errors.add(message);
			if(errors.size() > supperssedFatalErrorLimit) throw e;
		}

		/**
		 * Returns line number in text for offset 'start'; the first line has number 1.
		 * 
		 * @param start
		 * @return
		 */
		private int getLine(int start) {
			try {
				return document.getLineOfOffset(start) + 1;
			} catch (BadLocationException e) {
				CommonPlugin.getPluginLog().logError(e);
				return -1;
			}
		}
	}


}
