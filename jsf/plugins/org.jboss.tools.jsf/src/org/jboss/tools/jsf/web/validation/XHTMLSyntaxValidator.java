/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.jsf.web.validation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.xml.core.internal.validation.ValidatorHelper;
import org.eclipse.wst.xml.core.internal.validation.XMLValidationConfiguration;
import org.eclipse.wst.xml.core.internal.validation.XMLValidationInfo;
import org.eclipse.wst.xml.core.internal.validation.XMLValidationReport;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xml.core.internal.validation.eclipse.ErrorCustomizationPluginRegistryReader;
import org.eclipse.wst.xml.core.internal.validation.eclipse.Validator;
import org.jboss.tools.common.xml.XMLEntityResolverImpl;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/**
 * Syntax Validator for XHTML files.
 * @author Victor Rubezhny
 */
public class XHTMLSyntaxValidator extends Validator {
	/**
	 * The method is overridden to setup our own XMLValidator to be used
	 */
	@Override
	public ValidationReport validate(String uri, InputStream inputstream,
		NestedValidatorContext context, ValidationResult result) {
		
		long ct = 0;
		if (JSFModelPlugin.getDefault().isDebugging()) {
			ct = System.currentTimeMillis(); 
		}
			
		XMLValidator validator = XMLValidator.getInstance();
	
		XMLValidationConfiguration configuration = new XMLValidationConfiguration();
		try {
			configuration.setFeature(
					XMLValidationConfiguration.INDICATE_NO_GRAMMAR, 0); // None.
																		// No
																		// grammar
																		// indication
																		// is
																		// needed
		} catch (Exception e) {
			// Unable to set the preference. Log this problem.
			JSFModelPlugin.log("XHTMLSyntaxValidator was unable to set the preference", e);
		}
	
		XMLValidationReport valreport = null;
		if (inputstream != null) {
			valreport = validator.validate(uri, inputstream, configuration,
					context, result);
		} else {
			valreport = validator.validate(uri, null, configuration, context, result);
		}
	
		if (JSFModelPlugin.getDefault().isDebugging()) {
			long et = System.currentTimeMillis() - ct;
			System.out.println("XHTMLSyntaxValidator: Elapsed time = " + (et) + " ms for " + uri);
		}		
		return valreport;
	}

	  
	/**
	 * An XML validator specific to XHTML-files validation. This validator will wrap the internal
	 * XML syntax validator.
	 */
	public static class XMLValidator extends org.eclipse.wst.xml.core.internal.validation.XMLValidator {
		private static XMLValidator instance = null;
	    
	    /**
	     * Return the one and only instance of the XML validator. The validator
	     * can be reused and cannot be customized so there should only be one instance of it.
	     * 
	     * @return The one and only instance of the XML validator.
	     */
	    public static XMLValidator getInstance() {
	    	if(instance == null) {
	    		instance = new XMLValidator();
	    	}
	    	return instance;
	    }
	    
	    /**
	     * Constructor. Create the XML validator, set the URI resolver and
	     * get the extension error customizers from the registry.
	     */
	    protected XMLValidator() {
	    	setURIResolver(URIResolverPlugin.createResolver());
	    	new ErrorCustomizationPluginRegistryReader().readRegistry();
	    }

	    /**
	     * Validate the inputStream
	     * 
	     * @param uri 
	     *    The URI of the file to validate.
	     * @param inputstream
	     *    The inputStream of the file to validate
	     * @param configuration
	     *    A configuration for this validation session.
	     * @param result
	     *    The validation result
	     * @return 
	     *    Returns an XML validation report.
	     */
	    public XMLValidationReport validate(String uri, InputStream inputStream, 
	    		XMLValidationConfiguration configuration, NestedValidatorContext context, ValidationResult result) {
	    	String grammarFile = "";
	    	Reader reader1 = null; // Used for the preparse.
	    	Reader reader2 = null; // Used for validation parse.
	    
	    	if (inputStream != null) {  
	    		String string = createStringForInputStream(inputStream);
	    		reader1 = new StringReader(string);
	    		reader2 = new StringReader(string);
	    	} 
	        
	    	XMLValidationInfo valinfo = new XMLValidationInfo(uri);
	    	XHTMLEntityResolver entityResolver = new XHTMLEntityResolver(uriResolver, context);
	    	XHTMLValidatorHelper helper = new XHTMLValidatorHelper(entityResolver);
	    	
	    	try {  
	    		helper.computeValidationInformation(uri, reader1, uriResolver);
	        
	    		// The syntax validation is to be performed
	    		valinfo.setDTDEncountered(false);  
		        valinfo.setElementDeclarationCount(0);
		        valinfo.setNamespaceEncountered(false);
		        valinfo.setGrammarEncountered(false);
		
		        // No validation needed for native HTML files
		        // The only XHTML files are to be validated here
		        if (!helper.isXHTMLDoctype) {
		        	return valinfo;
		        }
	        
		        XMLReader reader = createXMLReader(valinfo, entityResolver);
		        XMLErrorHandler errorhandler = new XMLErrorHandler(valinfo);
		        reader.setErrorHandler(errorhandler);
	        
		        InputSource inputSource = new InputSource(uri);
		        inputSource.setCharacterStream(reader2);
		        reader.parse(inputSource);   
		    } catch (SAXParseException saxParseException) {
			      // These errors are caught by the error handler.
			      //addValidationMessage(valinfo, saxParseException);
		    } catch (IOException ioException) {
		    	addValidationMessage(valinfo, ioException);
		    } catch (Exception exception) {  
				JSFModelPlugin.log(exception.getLocalizedMessage(), exception);
		    }

		    // Now set up the dependencies
		    // Wrap with try catch so that if something wrong happens, validation can
		    // still proceed as before
		    if (result != null) {
		    	try {
		    		IResource resource = getWorkspaceFileFromLocation(grammarFile);
		    		ArrayList resources = new ArrayList();
		    		if (resource != null)
		    			resources.add(resource);
		    		result.setDependsOn((IResource [])resources.toArray(new IResource [0]));
		    	} catch (Exception e) {
					JSFModelPlugin.log(e.getLocalizedMessage(), e);
		    	}
		    }
		    return valinfo;
	    }

	    final String createStringForInputStream(InputStream inputStream) {
		    // Here we are reading the file and storing to a stringbuffer.
		    StringBuffer fileString = new StringBuffer();
		    try {
				InputStreamReader inputReader = new InputStreamReader(inputStream, "UTF-8");
				BufferedReader reader = new BufferedReader(inputReader);
				char[] chars = new char[1024];
				int numberRead = reader.read(chars);
				while (numberRead != -1) {
					fileString.append(chars, 0, numberRead);
					numberRead = reader.read(chars);
				}
		    } catch (Exception e) {
				JSFModelPlugin.log(e.getLocalizedMessage(), e);
		    }
		    return fileString.toString();
	    }

		/* 
		 * Custom validation helper to be used
		 */
		class XHTMLValidatorHelper extends ValidatorHelper {
			public boolean isXHTMLDoctype = false;
			private XHTMLEntityResolver entityResolver;
	
			public XHTMLValidatorHelper(XHTMLEntityResolver entityResolver) {
				this.entityResolver = entityResolver;
			}

			protected XMLReader createXMLReader(String uri) throws Exception
			{     
				XMLReader reader = super.createXMLReader(uri);
	
				reader.setFeature("http://xml.org/sax/features/namespaces", true);
				reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
				reader.setFeature("http://xml.org/sax/features/validation", true);
				reader.setFeature("http://apache.org/xml/features/validation/schema", true);
				reader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
				reader.setFeature("http://apache.org/xml/features/validation/dynamic", false);
				reader.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);

		        LexicalHandler lexicalHandler = new LexicalHandler()
	    	    {      
	    	    	public void startDTD (String name, String publicId, String systemId) {
	    	    		isGrammarEncountered = true;   
	    	    		isDTDEncountered = true;
	    	    		if (publicId != null && publicId.indexOf("W3C") != -1 && 
	    	    				publicId.indexOf("DTD") != -1 && publicId.indexOf("XHTML") != -1) {
	    	    			isXHTMLDoctype = true;
	    	    		}
	    	    	}
	
	    	    	public void endDTD() throws SAXException {
	    	    	}
	
	    	    	public void startEntity(String name) throws SAXException {
	    	    	}
	
	    	    	public void endEntity(String name) throws SAXException {
	    	    	}
	
	    	    	public void startCDATA() throws SAXException {
	    	    	}
	
	    	    	public void endCDATA() throws SAXException {
	    	    	}
	    	 
	    	    	public void comment (char ch[], int start, int length) throws SAXException {
	    	    	}
	    	    };
	    	    reader.setProperty("http://xml.org/sax/properties/lexical-handler", lexicalHandler); //$NON-NLS-1$
	    	    if (entityResolver != null) {
	    	    	reader.setProperty("http://apache.org/xml/properties/internal/entity-resolver", entityResolver); //$NON-NLS-1$
	    	    }
	    	    return reader;
			}  
		}
	}
}
