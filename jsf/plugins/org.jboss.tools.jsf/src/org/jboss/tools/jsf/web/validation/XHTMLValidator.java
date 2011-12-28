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

import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;
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
 * @author Victor V. Rubezhny
 */
@SuppressWarnings("restriction")
public class XHTMLValidator extends Validator {
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
		if (file == null) {
			return null;
		}

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
			XMLReader xmlReader = new MySAXParser();

			setSAXParserFeatures(xmlReader, SAX_PARSER_FEATURES_TO_DISABLE, false);
    	    setSAXParserProperty(xmlReader, "http://xml.org/sax/properties/lexical-handler", handler); 

			xmlReader.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new NullXMLEntityResolver());

    	    xmlReader.setContentHandler(handler);
    	    xmlReader.setDTDHandler(handler);
    	    xmlReader.setErrorHandler(handler);
    	    xmlReader.setEntityResolver(handler);

			xmlReader.parse(uri);
		} catch (IOException e) {
			JSFModelPlugin.getDefault().logError(e);
		} catch (SAXNotRecognizedException e) {
			JSFModelPlugin.getDefault().logError(e);
		} catch (SAXNotSupportedException e) {
			JSFModelPlugin.getDefault().logError(e);
		} catch (SAXException e) {
			int max = handler.document.getLength();
			int currentLocation = handler.getCurrentLocation();
			int length = 0;
			if(max>0) {
				if(currentLocation+1>max) {
					currentLocation--;
				} else {
					length = 1;
				}
			}
			ElementLocation location = new ElementLocation(handler.locator.getLineNumber(), handler.locator.getColumnNumber(), currentLocation, length);
			report.addError(e.getLocalizedMessage(), handler.locator.getLineNumber(), handler.locator.getColumnNumber(), uri, null, new Object[] {location});
		}

		return report;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.xml.core.internal.validation.eclipse.Validator#addInfoToMessage(org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage, org.eclipse.wst.validation.internal.provisional.core.IMessage)
	 */
	@Override
	protected void addInfoToMessage(ValidationMessage validationMessage, IMessage message) {
		ElementLocation location = validationMessage.getMessageArguments() == null || validationMessage.getMessageArguments().length < 1 ? 
					null: (ElementLocation)validationMessage.getMessageArguments()[0];
		if (location != null) {
			message.setLineNo(location.getLine());
			message.setOffset(location.getStart());
			message.setLength(location.getLength());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.xml.core.internal.validation.eclipse.Validator#validationStarting(org.eclipse.core.resources.IProject, org.eclipse.wst.validation.ValidationState, org.eclipse.core.runtime.IProgressMonitor)
	 */
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
		int line;
		int column;
		int start;
		int length;

		ElementLocation (int line, int column, int start, int length) {
			this.line = line;
			this.column = column;
			this.start = start;
			this.length = length;
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
			sb.append("Line: ");
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
		public XMLInputSource resolveEntity(XMLResourceIdentifier rid) throws XNIException, IOException {
			return new XMLInputSource(rid.getPublicId(), 
					rid.getBaseSystemId()==null?rid.getLiteralSystemId():rid.getExpandedSystemId(), 
							rid.getBaseSystemId(), new StringReader(""), null);
		}
	}

	class XHTMLElementHandler extends DefaultHandler implements LexicalHandler {
		private Locator locator;
		private IDocument document;

		public XHTMLElementHandler(String uri, IDocument document, XMLValidationInfo valinfo) {
			super();
			this.document = document;
		}

		@Override
		public void setDocumentLocator(Locator locator) {
			  this.locator = locator;
		}

		@Override
		public InputSource resolveEntity(String publicId, String systemId)
				throws IOException, SAXException {
			return new InputSource(new StringReader("")); //$NON-NLS-1$
		}

		private int getCurrentLocation() {
			if (locator == null) { 
				return 0;
			}

			int line = locator.getLineNumber() - 1;
			int lineOffset = locator.getColumnNumber() - 1;
			try {
				return document.getLineOffset(line) + lineOffset;
			} catch (BadLocationException e) {
				JSFModelPlugin.getDefault().logError(e);
			}
			return 0;
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
		public void comment(char[] ch, int start, int length) throws SAXException {
		}

		@Override
		public void startDTD(String name, String publicId, String systemId)
				throws SAXException {
		}
	}

	class MyXMLInputSource extends XMLInputSource {

		public MyXMLInputSource(String publicId, String systemId,
				String baseSystemId) {
			super(publicId, systemId, baseSystemId);
		}

		/*
		 * (non-Javadoc)
		 * @see org.apache.xerces.xni.parser.XMLInputSource#getByteStream()
		 */
		@Override
		public InputStream getByteStream() {
			InputStream stream = null;
			try {
				URL location = new URL(getSystemId());
				String protocal = location.getProtocol();
				if(!"http".equalsIgnoreCase(protocal) && !"https".equalsIgnoreCase(protocal)) {
	                URLConnection connect = location.openConnection();
	                if (!(connect instanceof HttpURLConnection)) {
	                    stream = connect.getInputStream();
	                }
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

		/*
		 * (non-Javadoc)
		 * @see org.apache.xerces.parsers.AbstractSAXParser#parse(java.lang.String)
		 */
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
	        } catch (XNIException e) {
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
	        } catch (XNIException e) {
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