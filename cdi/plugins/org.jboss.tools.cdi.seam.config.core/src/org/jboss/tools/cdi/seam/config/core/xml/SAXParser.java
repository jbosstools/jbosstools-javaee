package org.jboss.tools.cdi.seam.config.core.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.jboss.tools.common.CommonPlugin;
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

	XMLReader createParser1(DefaultHandler handler) {
		XMLReader parserInstance = null;

		try {
			parserInstance = XMLReaderFactory.createXMLReader(DEFAULT_SAX_PARSER_CLASS_NAME);
		} catch (SAXException e) {
			return null;
		}

		setFeature(parserInstance, NAMESPACES_FEATURE_ID, true);
		setFeature(parserInstance, NAMESPACE_PREFIXES_FEATURE_ID, false);
		setFeature(parserInstance, VALIDATION_FEATURE_ID, true);
		setFeature(parserInstance, VALIDATION_SCHEMA_FEATURE_ID, true);
		setFeature(parserInstance, VALIDATION_SCHEMA_CHECKING_FEATURE_ID, false);
		setFeature(parserInstance, VALIDATION_DYNAMIC_FEATURE_ID, false);
		setFeature(parserInstance, FATAL_ERROR_PROCESSING_FEATURE_ID, true);

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

	public SAXElement parse(InputStream input, IDocument document) {
		InputSource s = new InputSource(input);
		ConfigHanlder handler = new ConfigHanlder(document);
		XMLReader reader = createParser1(handler);
		try {
			reader.parse(s);
		} catch (IOException e) {
			CommonPlugin.getDefault().logError(e);
		} catch (SAXException e) {
			CommonPlugin.getDefault().logError(e);
		}
		
		return handler.getRootElement();
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
				element.setLocation(new Location(start, end - start));
				int ns = document.get().indexOf(qName, start);
				if(ns >= start) {
					element.setNameLocation(new Location(ns, qName.length()));
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
					a.setNameLocation(new Location(n_start, n.length()));
					int v_start = document.get().indexOf('"', n_start);
					if(v_start >= 0) {
						a.setValueLocation(new Location(v_start + 1, v.length()));
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
			currentTextLocation = new Location(s, length);
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			int end = getCurrentLocation();
			if(end > 0) {
				int start = document.get().lastIndexOf("<", end);
				Location endLocation = new Location(start, end - start);
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
		}
	}


}
