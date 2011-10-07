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
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.util.MessageFormatter;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
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
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;

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
	public static final String PROBLEM_ID = JSFModelPlugin.PLUGIN_ID + "xhtmlsyntaxproblem";
	
	IProgressMonitor monitor;
	IResource resource;
	
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
	
	private void setSAXParserFeatures(XMLReader reader, String[] features, boolean set) {
		for (String feature : features) {
			try {
			    reader.setFeature(feature, set);
			} catch (SAXException e) {
				JSFModelPlugin.getDefault().logError(e);
			}
		}
	}

	private void setSAXParserProperty(XMLReader reader, String property, Object value) {
		try {
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
		XHTMLElementHandler handler = new XHTMLElementHandler(uri,
				(resource instanceof IFile ? getDocument((IFile)resource) : null),
				report);
		
		try {
			if (!handler.isWellFormedXHTML()) {
				SAXParseException ex = handler.getException();
				if (ex != null) {
					report.addError(ex.getLocalizedMessage(), ex.getLineNumber(), ex.getColumnNumber(), uri);
				}
				return report;
			}
			
			XMLReader xmlReader = new MySAXParser();

			setSAXParserFeatures(xmlReader, SAX_PARSER_FEATURES_TO_DISABLE, false);
			setSAXParserFeatures(xmlReader, SAX_PARSER_FEATURES_TO_ENABLE, true);
    	    setSAXParserProperty(xmlReader, "http://xml.org/sax/properties/lexical-handler", handler); 
    	    
			xmlReader.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new NullXMLEntityResolver());

    	    xmlReader.setContentHandler(handler);
    	    xmlReader.setDTDHandler(handler);
    	    xmlReader.setErrorHandler(handler);
    	    xmlReader.setEntityResolver(handler);
    	    
    	    handler.setCurrentReader(xmlReader);
    	    
			xmlReader.parse(uri);
		} catch (IOException e) {
			JSFModelPlugin.getDefault().logError(e);
		} catch (SAXNotRecognizedException e) {
			JSFModelPlugin.getDefault().logError(e);
		} catch (SAXNotSupportedException e) {
			JSFModelPlugin.getDefault().logError(e);
		} catch (SAXException e) {
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

	class NullXMLEntityResolver implements XMLEntityResolver {

		public XMLInputSource resolveEntity(XMLResourceIdentifier rid)
				throws XNIException, IOException {
			
			return new XMLInputSource(rid.getPublicId(), 
					rid.getBaseSystemId()==null?rid.getLiteralSystemId():rid.getExpandedSystemId(), 
							rid.getBaseSystemId(), new StringReader(""), null);
		}
		
	}
	
	class XHTMLElementHandler extends DefaultHandler implements LexicalHandler {
		private String uri;
		private Locator locator;
		private IDocument document;
		private XMLValidationInfo valinfo;
		public boolean isXHTMLDoctype = false;
		private boolean isWellFormed = false;
		private XMLReader currentReader = null;
		  
		List<ElementLocation> elements = new ArrayList<ElementLocation>();
		Stack<ElementLocation> nonPairedOpenElements = new Stack<XHTMLValidator.ElementLocation>();
		Stack<ElementLocation> nonPairedCloseElements = new Stack<XHTMLValidator.ElementLocation>();
		 		  
		public XHTMLElementHandler(String uri, IDocument document, XMLValidationInfo valinfo) {
			super();
			this.uri = uri;
			this.document = document;
			this.valinfo = valinfo;
		}
		
		public void setCurrentReader  (XMLReader reader) {
			this.currentReader = reader;
		}
		
		public XMLReader getCurrentReader() {
			return this.currentReader;
		}
		@Override
		public void setDocumentLocator(Locator locator) {
			  this.locator = locator;
		  }
		  
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
			if (!isWellFormed || !isXHTMLDoctype)
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
			if (!isWellFormed || !isXHTMLDoctype)
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
		public void fatalError(SAXParseException e) throws SAXException {
			if (!isWellFormed) {
				if (isNonWellFormedException(e, getCurrentReader() instanceof MySAXParser ? ((MySAXParser)getCurrentReader()).getConfiguration() : null)) {
					super.fatalError(e);
				}
			}
			// We do not need to throw any exceptions here in case of well-formed xhtml (opposite to what super method does)!
		}

		@Override
		public InputSource resolveEntity(String publicId, String systemId)
				throws IOException, SAXException {
			return new InputSource(new StringReader("")); //$NON-NLS-1$
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
			if (!isWellFormed) {
	    		valinfo.setGrammarEncountered(true);
	    		valinfo.setDTDEncountered(true);
	    		if (publicId != null && publicId.indexOf("W3C") != -1 && 
	    				publicId.indexOf("DTD") != -1 && publicId.indexOf("XHTML") != -1) {
	    			this.isXHTMLDoctype = true;
	    		}
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
		
		private SAXParseException exception = null;
		
		SAXParseException getException() {
			return exception;
		}
	
		public boolean isWellFormedXHTML() throws IOException, SAXException {
			exception = null;
			isXHTMLDoctype = false;
			isWellFormed = false;
			
		    MySAXParser reader = new MySAXParser();     
			try {
				setSAXParserFeatures(reader, SAX_PARSER_FEATURES_TO_DISABLE, false);
				setSAXParserFeatures(reader, SAX_PARSER_FEATURES_TO_ENABLE, true); // Disabling this feature 
																					// due to prevent validation 
																					// on non-well-formed xhtml files
	    	    setSAXParserProperty(reader, "http://xml.org/sax/properties/lexical-handler", this); 
    			reader.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new NullXMLEntityResolver());

    			reader.setContentHandler(this);
	    	    reader.setDTDHandler(this);
	    	    reader.setErrorHandler(this);
	    	    reader.setEntityResolver(this);

	    	    this.setCurrentReader(reader);

	    	    isXHTMLDoctype = false;
				reader.parse(uri);
			} catch (SAXParseException e) {
				// We have to prevent further validation in case we've met the following exception:
				// "The markup in the document preceding the root element must be well-formed"
				// If the markup is not well-formed, not doing this may cause the validation to stuck 
				// in Throwable.fillInStackTrace() method (it may come into an infinite loop) for some reason
				//
				if (isNonWellFormedException(e, reader.getConfiguration())) {
					exception = e;
					isXHTMLDoctype = false;
					return false;
				}
			}
			isWellFormed = true;
			return this.isXHTMLDoctype;
		}
		
		private static final String MARKUP_NOT_RECOGNIZED_ERROR_MESSAGE_ID = "MarkupNotRecognizedInProlog";
		private static final String ERROR_REPORTER_PROPERTY = Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;
		
		private boolean isNonWellFormedException (SAXParseException e, XMLParserConfiguration parserConfiguration) {
			if (parserConfiguration == null) return true;
			
			XMLErrorReporter errorReporter = (XMLErrorReporter)parserConfiguration.getProperty(ERROR_REPORTER_PROPERTY);
			if (errorReporter == null) return true;

			MessageFormatter messageFormatter = errorReporter.getMessageFormatter(XMLMessageFormatter.XML_DOMAIN);
	        String templateMessage = null;
	        if (messageFormatter != null) {
	            templateMessage = messageFormatter.formatMessage(parserConfiguration.getLocale(), MARKUP_NOT_RECOGNIZED_ERROR_MESSAGE_ID, null);
	        }
			String message = e.getMessage() == null ? null : e.getMessage().toLowerCase();
			if (templateMessage == null) {
				return (message != null && message.contains(XMLMessageFormatter.XML_DOMAIN) &&
						message.contains("MarkupNotRecognizedInProlog"));
			}
	        
			return (message != null && message.equals(templateMessage.toLowerCase()));
		}
	}
	
	class FilteredInputStream extends InputStream {
		InputStream base;
		
		public FilteredInputStream(InputStream base) {
			this.base = base;
		}
		
		@Override
		public int read() throws IOException {
			int ch = base.read();
			return (ch == '&' ? ' ' : ch); 
		}

		@Override
		public int read(byte[] b) throws IOException {
			return base.read(b);
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int length = base.read(b, off, len);
			for (int i = 0; i < length; i++) {
				if (b[i] == '&') b[i] = ' ';
			}
			return length;
		}

		@Override
		public long skip(long n) throws IOException {
			return base.skip(n);
		}

		@Override
		public int available() throws IOException {
			return base.available();
		}

		@Override
		public void close() throws IOException {
			base.close();
		}

		@Override
		public synchronized void mark(int readlimit) {
			base.mark(readlimit);
		}

		@Override
		public synchronized void reset() throws IOException {
			base.reset();
		}

		@Override
		public boolean markSupported() {
			return base.markSupported();
		}
	}
	
	class MyXMLInputSource extends XMLInputSource {

		public MyXMLInputSource(String publicId, String systemId,
				String baseSystemId) {
			super(publicId, systemId, baseSystemId);
		}

		@Override
		public InputStream getByteStream() {
			InputStream stream = null;
			try {
				URL location = new URL(getSystemId());
                URLConnection connect = location.openConnection();
                if (!(connect instanceof HttpURLConnection)) {
                    stream = new FilteredInputStream(connect.getInputStream());
                }
			} catch (MalformedURLException e) {
				// Ignore (null will be returned as result)
				JSFModelPlugin.getPluginLog().logError(e);
			} catch (IOException e) {
				// Ignore (null will be returned as result)
				JSFModelPlugin.getPluginLog().logError(e);
			}
            return stream;
		}
	}
	
	class MySAXParser extends org.apache.xerces.parsers.SAXParser {
    	public XMLParserConfiguration getConfiguration() {
    		return fConfiguration;
    	}
    	
    	

		@Override
		public void parse(String systemId) throws SAXException, IOException {
	        // parse document
	        XMLInputSource source = new MyXMLInputSource(null, systemId, null);
	        try {
	            parse(source);
	        }

	        // wrap XNI exceptions as SAX exceptions
	        catch (XMLParseException e) {
	            Exception ex = e.getException();
	            if (ex == null) {
	                // must be a parser exception; mine it for locator info and throw
	                // a SAXParseException
	                LocatorImpl locatorImpl = new LocatorImpl(){
	                    public String getXMLVersion() {
	                        return fVersion;
	                    }
	                    // since XMLParseExceptions know nothing about encoding,
	                    // we cannot return anything meaningful in this context.
	                    // We *could* consult the LocatorProxy, but the
	                    // application can do this itself if it wishes to possibly
	                    // be mislead.
	                    public String getEncoding() {
	                        return null;
	                    }
	                };
	                locatorImpl.setPublicId(e.getPublicId());
	                locatorImpl.setSystemId(e.getExpandedSystemId());
	                locatorImpl.setLineNumber(e.getLineNumber());
	                locatorImpl.setColumnNumber(e.getColumnNumber());
	                throw new SAXParseException(e.getMessage(), locatorImpl);
	            }
	            if (ex instanceof SAXException) {
	                // why did we create an XMLParseException?
	                throw (SAXException)ex;
	            }
	            if (ex instanceof IOException) {
	                throw (IOException)ex;
	            }
	            throw new SAXException(ex);
	        }
	        catch (XNIException e) {
	            Exception ex = e.getException();
	            if (ex == null) {
	                throw new SAXException(e.getMessage());
	            }
	            if (ex instanceof SAXException) {
	                throw (SAXException)ex;
	            }
	            if (ex instanceof IOException) {
	                throw (IOException)ex;
	            }
	            throw new SAXException(ex);
	        }

		}



		@Override
		public void parse(InputSource inputSource) throws SAXException,
				IOException {
	        // parse document
	        try {
	            XMLInputSource xmlInputSource =
	                new XMLInputSource(inputSource.getPublicId(),
	                                   inputSource.getSystemId(),
	                                   null);
	            xmlInputSource.setByteStream(inputSource.getByteStream());
	            xmlInputSource.setCharacterStream(inputSource.getCharacterStream());
	            xmlInputSource.setEncoding(inputSource.getEncoding());
	            parse(xmlInputSource);
	        }

	        // wrap XNI exceptions as SAX exceptions
	        catch (XMLParseException e) {
	            Exception ex = e.getException();
	            if (ex == null) {
	                // must be a parser exception; mine it for locator info and throw
	                // a SAXParseException
	                LocatorImpl locatorImpl = new LocatorImpl() {
	                    public String getXMLVersion() {
	                        return fVersion;
	                    }
	                    // since XMLParseExceptions know nothing about encoding,
	                    // we cannot return anything meaningful in this context.
	                    // We *could* consult the LocatorProxy, but the
	                    // application can do this itself if it wishes to possibly
	                    // be mislead.
	                    public String getEncoding() {
	                        return null;
	                    }
	                };
	                locatorImpl.setPublicId(e.getPublicId());
	                locatorImpl.setSystemId(e.getExpandedSystemId());
	                locatorImpl.setLineNumber(e.getLineNumber());
	                locatorImpl.setColumnNumber(e.getColumnNumber());
	                throw new SAXParseException(e.getMessage(), locatorImpl);
	            }
	            if (ex instanceof SAXException) {
	                // why did we create an XMLParseException?
	                throw (SAXException)ex;
	            }
	            if (ex instanceof IOException) {
	                throw (IOException)ex;
	            }
	            throw new SAXException(ex);
	        }
	        catch (XNIException e) {
	            Exception ex = e.getException();
	            if (ex == null) {
	                throw new SAXException(e.getMessage());
	            }
	            if (ex instanceof SAXException) {
	                throw (SAXException)ex;
	            }
	            if (ex instanceof IOException) {
	                throw (IOException)ex;
	            }
	            throw new SAXException(ex);
	        }
		}

    	
    }  

}
