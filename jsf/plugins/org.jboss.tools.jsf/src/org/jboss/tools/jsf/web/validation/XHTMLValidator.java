/******************************************************************************* 
 * Copyright (c) 2009-2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.jsf.web.validation;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.validation.XMLValidationInfo;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xml.core.internal.validation.eclipse.Validator;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Victor V. Rubezhny
 *
 */
@SuppressWarnings("restriction")
public class XHTMLValidator extends Validator {
	private static final String END_TAG_STRATEGY = "END_TAG";
	private static final String START_TAG_STRATEGY = "START_TAG";
	private static final String NO_START_TAG = "NO_START_TAG";
	private static final String NO_END_TAG = "NO_END_TAG";
	
	IProgressMonitor monitor;
	IResource resource;
	
	public boolean isXHTMLDoctype = false;

	private String[] SAX_PARSER_FEATURES_TO_DISABLE = {
			"http://xml.org/sax/features/namespaces", 
	    	"http://xml.org/sax/features/use-entity-resolver2",
			"http://xml.org/sax/features/validation",
			"http://apache.org/xml/features/validation/dynamic",
			"http://apache.org/xml/features/validation/schema",
			"http://apache.org/xml/features/validation/schema-full-checking",		
			"http://apache.org/xml/features/nonvalidating/load-external-dtd",
			"http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
			"http://apache.org/xml/features/xinclude",
			"http://xml.org/sax/features/resolve-dtd-uris"
		};  
	private String[] SAX_PARSER_FEATURES_TO_ENABLE = {
			"http://apache.org/xml/features/continue-after-fatal-error"
		};
	
	private void setSAXParserFeatures(SAXParser saxParser, String[] features, boolean set) {
		XMLReader reader;
		try {
			reader = saxParser.getXMLReader();
		} catch (SAXException e) {
			JSFModelPlugin.getDefault().logError(e);
			return;
		}
		for (String feature : features) {
			try {
			    reader.setFeature(feature, set);
			} catch (SAXException e) {
				JSFModelPlugin.getDefault().logError(e);
			}
		}
	}

	private void setSAXParserProperty(SAXParser saxParser, String property, Object value) {
		XMLReader reader;
		try {
			reader = saxParser.getXMLReader();
    	    reader.setProperty(property, value);
		} catch (SAXException e) {
			JSFModelPlugin.getDefault().logError(e);
		}
	}

	private IDocument getDocument(IFile file) {
		if (file == null)
			return null;

		String content;
		try {
			content = FileUtil.readStream(file);
		} catch (CoreException e) {
			JSFModelPlugin.getDefault().logError(e);
			return null;
		}
		
		return (content == null ? null : new Document(content));
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator#validate(org.eclipse.core.resources.IResource, int, org.eclipse.wst.validation.ValidationState, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		displaySubtask(monitor, JSFValidationMessage.XHTML_VALIDATION, resource.getFullPath());
		this.resource = resource; 
		return super.validate(resource, kind, state, monitor);
	}

	@Override
	public ValidationReport validate(String uri, InputStream inputstream,
			NestedValidatorContext context) {
		displaySubtask(JSFValidationMessage.XHTML_VALIDATION, uri);
		this.resource = null; 
		return super.validate(uri, inputstream, context);
	}

	@Override
	public ValidationReport validate(String uri, InputStream inputstream,
			NestedValidatorContext context, ValidationResult result) {
		displaySubtask(JSFValidationMessage.XHTML_VALIDATION, uri);
		XMLValidationInfo report = new XMLValidationInfo(uri);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		XHTMLElementHandler handler = new XHTMLElementHandler(
				(resource instanceof IFile ? getDocument((IFile)resource) : null),
				report);
		SAXParser saxParser = null;
		try {
			saxParser = factory.newSAXParser();
			setSAXParserFeatures(saxParser, SAX_PARSER_FEATURES_TO_DISABLE, false);
			setSAXParserFeatures(saxParser, SAX_PARSER_FEATURES_TO_ENABLE, true);
    	    setSAXParserProperty(saxParser, "http://xml.org/sax/properties/lexical-handler", handler); 
    	    isXHTMLDoctype = false;
			if (inputstream != null) {
				saxParser.parse(inputstream, handler);
			} else {
				saxParser.parse(uri, handler);
			}
		} catch (ParserConfigurationException e) {
			JSFModelPlugin.getDefault().logError(e);
			report.addError(e.getLocalizedMessage(), 0, 0, uri);
		} catch (SAXException e) {
			JSFModelPlugin.getDefault().logError(e);
			report.addError(e.getLocalizedMessage(), 0, 0, uri);
		} catch (IOException e) {
			JSFModelPlugin.getDefault().logError(e);
			report.addError(e.getLocalizedMessage(), 0, 0, uri);
		} 
	 
		List<ElementLocation> locations = handler.getNonPairedOpenElements();
		if (!locations.isEmpty()) {
			for (ElementLocation location : locations) {
				String messageText = MessageFormat.format(JSFValidationMessage.XHTML_VALIDATION_NO_END_TAG, location.getName());
				report.addError(messageText, location.getLine(), location.getColumn(), uri, NO_END_TAG, new Object[] {location});
			}
		}
		
		locations = handler.getNonPairedCloseElements();
		if (!locations.isEmpty()) {
			for (ElementLocation location : locations) {
				String messageText = MessageFormat.format(JSFValidationMessage.XHTML_VALIDATION_NO_START_TAG, location.getName());
				report.addError(messageText, location.getLine(), location.getColumn(), uri, NO_START_TAG, new Object[] {location});
			}
		}
		
		return report;
	}
	
	@Override
	protected void addInfoToMessage(ValidationMessage validationMessage,
			IMessage message) {
		ElementLocation location = validationMessage.getMessageArguments() == null || validationMessage.getMessageArguments().length < 1 ? 
					null: (ElementLocation)validationMessage.getMessageArguments()[0];

		String nameOrValue = location == null ? "" : location.getName();
		String key = validationMessage.getKey();
		if(key != null && (NO_START_TAG.equals(key) || NO_END_TAG.equals(key)) )
		{
			String selectionStrategy = START_TAG_STRATEGY;
			if (NO_START_TAG.equals(key)) {
		        selectionStrategy = START_TAG_STRATEGY; 
		    } else if (NO_END_TAG.equals(key)) {
		        selectionStrategy = END_TAG_STRATEGY;
		    } 
			message.setAttribute(COLUMN_NUMBER_ATTRIBUTE, new Integer(validationMessage.getColumnNumber()));
			message.setAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE, selectionStrategy);
			message.setAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE, nameOrValue);
			if (location != null) {
				message.setLineNo(location.getLine());
				message.setOffset(location.getStart());
				message.setLength(location.getLength());
			}
		} else {
			super.addInfoToMessage(validationMessage, message);
		}
	}

	@Override
	public void validationStarting(IProject project, ValidationState state,
			IProgressMonitor monitor) {
		super.validationStarting(project, state, monitor);
		this.monitor = monitor;
	}

	private void displaySubtask(String message, Object... arguments) {
		displaySubtask(monitor, MessageFormat.format(message, arguments));
	}

	private void displaySubtask(IProgressMonitor monitor, String message, Object... arguments) {
		if(monitor!=null) {
			monitor.subTask(MessageFormat.format(message, arguments));
		}
	}
	
	class ElementLocation {
		String name;
		int line;
		int column;
		int start;
		int length;
		
		ElementLocation (String name, int line, int column, int start, int length) {
			this.name = name;
			this.line = line;
			this.column = column;
			this.start = start;
			this.length = length;
		}
		
		String getName() {
			return name;
		}
		
		int getLine() {
			return line;
		}
		
		int getColumn() {
			return column;
		}

		int getStart() {
			return start;
		}
		
		int getLength() {
			return length;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("'");
			sb.append(name);
			sb.append("', Line: ");
			sb.append(line);
			sb.append(", Column: ");
			sb.append(column);
			sb.append(", start: ");
			sb.append(start);
			sb.append(", end: ");
			sb.append((start + length));
			sb.append(", length: ");
			sb.append(length);
			return sb.toString();
		}
	}

	class XHTMLElementHandler extends DefaultHandler implements LexicalHandler {
		private Locator locator;
		private IDocument document;
		private XMLValidationInfo valinfo;
		  
		List<ElementLocation> elements = new ArrayList<ElementLocation>();
		Stack<ElementLocation> nonPairedOpenElements = new Stack<XHTMLValidator.ElementLocation>();
		Stack<ElementLocation> nonPairedCloseElements = new Stack<XHTMLValidator.ElementLocation>();
		 		  
		public XHTMLElementHandler(IDocument document, XMLValidationInfo valinfo) {
			super();
			this.document = document;
			this.valinfo = valinfo;
		}
		
		@Override
		public void setDocumentLocator(Locator locator) {
			  this.locator = locator;
		  }
		  
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
			if (!isXHTMLDoctype)
				return;

			int end = getCurrentLocation(), start = 0;
			if(end > 0) {
				start = document.get().lastIndexOf("<", end - 1);
			}
			currentElementLocation = new ElementLocation(qName, getLine(start), getColumn(start), start, end - start);
			nonPairedOpenElements.push(currentElementLocation);
		}
		
		ElementLocation currentElementLocation = null;
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (!isXHTMLDoctype)
				return;

			int end = getCurrentLocation(), start = 0;
			if(end > 0) {
				start = document.get().lastIndexOf('<', end - 1);
			}
			if (document.get().charAt(end - 1) != '>') {
				int newEnd = document.get().indexOf('>', end);
				if (newEnd > 0) {
					qName = getName(document.get().substring(start, newEnd));
					end = newEnd+1;
				}
			}
			currentElementLocation = new ElementLocation(qName, getLine(start), getColumn(start), start, end - start);
			// Try to find according pair open element
			ElementLocation pairOpenElement = null;
			for (int i = nonPairedOpenElements.size() - 1; i >= 0 && pairOpenElement == null; i--) {
				ElementLocation openedElement = nonPairedOpenElements.get(i);
				if (openedElement != null && openedElement.getName().equals(qName)) {
					pairOpenElement = openedElement;
				}
			}
			if (pairOpenElement == null) {
				// There is no open element for the current closing element
				nonPairedCloseElements.push(currentElementLocation);
			} else {
				// The pair open element is found for the current closing element
				nonPairedOpenElements.remove(pairOpenElement);
			}
		}
		
		private String getName(String text) {
			if (text.startsWith("<"))
				text = text.substring(1);
			if (text.startsWith("/"))
				text = text.substring(1);
			StringBuilder qName = new StringBuilder();
			int i = 0;
			while(i < text.length() && (Character.isJavaIdentifierPart(text.charAt(i)) || text.charAt(i) == ':')) {
				qName.append(text.charAt(i));
				i++;
			}
			return qName.toString();
		}
		
		@Override
		public void error(SAXParseException e) throws SAXException {
		}

		@Override
		public void fatalError(SAXParseException e) throws SAXException {
		}

		List<ElementLocation> getNonPairedOpenElements() {
			return nonPairedOpenElements;
		}
		
		List<ElementLocation> getNonPairedCloseElements() {
			return nonPairedCloseElements;
		}

		private int getCurrentLocation() {
			if (locator == null) 
				return 0;
				
			int line = locator.getLineNumber() - 1;
			int lineOffset = locator.getColumnNumber() - 1;
			try {
				return document.getLineOffset(line) + lineOffset;
			} catch (BadLocationException e) {
				JSFModelPlugin.getDefault().logError(e);
			}
			return 0;
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
				JSFModelPlugin.getPluginLog().logError(e);
				return -1;
			}
		}
		
		/**
		 * Returns line number in text for offset 'start'; the first line has number 1.
		 * 
		 * @param start
		 * @return
		 */
		private int getColumn(int start) {
			try {
				int line = getLine(start);
				int lineStart = document.getLineOffset(line - 1);
				return (start - lineStart) + 1;
			} catch (BadLocationException e) {
				JSFModelPlugin.getPluginLog().logError(e);
				return -1;
			}
		}

		@Override
		public void startDTD(String name, String publicId, String systemId)
				throws SAXException {
    		valinfo.setGrammarEncountered(true);
    		valinfo.setDTDEncountered(true);
    		if (publicId != null && publicId.indexOf("W3C") != -1 && 
    				publicId.indexOf("DTD") != -1 && publicId.indexOf("XHTML") != -1) {
    			isXHTMLDoctype = true;
    		}
		}

		@Override
		public void endDTD() throws SAXException {
		}

		@Override
		public void startEntity(String name) throws SAXException {
		}

		@Override
		public void endEntity(String name) throws SAXException {
		}

		@Override
		public void startCDATA() throws SAXException {
		}

		@Override
		public void endCDATA() throws SAXException {
		}

		@Override
		public void comment(char[] ch, int start, int length)
				throws SAXException {
		}
	}
}
