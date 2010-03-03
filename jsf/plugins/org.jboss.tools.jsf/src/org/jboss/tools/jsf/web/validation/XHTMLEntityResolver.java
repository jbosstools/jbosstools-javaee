/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver - STAR - [205989] - [validation] validate XML after XInclude resolution
 *     
 *    
 * The class is partially copied from WTP because of its poor visibility 
 * 	org.eclipse.wst.xml.core.internal.validation.XMLValidator.MyEntityResolver
 *     
 *******************************************************************************/

package org.jboss.tools.jsf.web.validation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.xml.core.internal.validation.XMLNestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.LazyURLInputStream;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;

/**
 * A custom entity resolver that uses the URI resolver specified to resolve
 * entities.
 */
public class XHTMLEntityResolver implements XMLEntityResolver {
	private URIResolver uriResolver;
	private String resolvedDTDLocation;
	private NestedValidatorContext context;

	/**
	 * Constructor.
	 * 
	 * @param uriResolver
	 *            The URI resolver to use with this entity resolver.
	 * @param context
	 *            The XML validator context.
	 */
	public XHTMLEntityResolver(URIResolver uriResolver,
			NestedValidatorContext context) {
		this.uriResolver = uriResolver;
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.xerces.xni.parser.XMLEntityResolver#resolveEntity(org.apache
	 * .xerces.xni.XMLResourceIdentifier)
	 */
	public XMLInputSource resolveEntity(XMLResourceIdentifier rid)
			throws XNIException, IOException {
		XMLInputSource inputSource = _internalResolveEntity(uriResolver, rid,
				context);
		if (inputSource != null) {
			resolvedDTDLocation = inputSource.getSystemId();
		}
		return inputSource;
	}

	public String getLocation() {
		return resolvedDTDLocation;
	}

	// cs : I've refactored the common SAX based resolution code into this
	// method for use by other validators
	// (i.e. XML Schema, WSDL etc). The other approach is maintain a copy for
	// each validator that has
	// identical code. In any case we should strive to ensure that the
	// validators perform resolution consistently.
	public static XMLInputSource _internalResolveEntity(
			URIResolver uriResolver, XMLResourceIdentifier rid)
			throws IOException {
		return _internalResolveEntity(uriResolver, rid, null);
	}

	public static XMLInputSource _internalResolveEntity(
			URIResolver uriResolver, XMLResourceIdentifier rid,
			NestedValidatorContext context) throws IOException {
		XMLInputSource is = null;

		if (uriResolver != null) {
			String id = rid.getPublicId();
			if (id == null) {
				id = rid.getNamespace();
			}

			String location = null;
			if (id != null || rid.getLiteralSystemId() != null) {
				location = uriResolver.resolve(rid.getBaseSystemId(), id, rid
						.getLiteralSystemId());
			}

			if (location != null) {
				String physical = uriResolver.resolvePhysicalLocation(rid
						.getBaseSystemId(), id, location);

				// if physical is already a known bad uri, just go ahead and
				// throw an exception
				if (context instanceof XMLNestedValidatorContext) {
					XMLNestedValidatorContext xmlContext = ((XMLNestedValidatorContext) context);

					if (xmlContext.isURIMarkedInaccessible(physical)) {
						throw new FileNotFoundException(physical);
					}
				}

				is = new XMLInputSource(rid.getPublicId(), location, location);

				// This block checks that the file exists. If it doesn't we need
				// to throw
				// an exception so Xerces will report an error. note: This may
				// not be
				// necessary with all versions of Xerces but has specifically
				// been
				// experienced with the version included in IBM's 1.4.2 JDK.
				InputStream isTemp = null;
				try {
					isTemp = new URL(physical).openStream();
				} catch (IOException e) {
					// physical was a bad url, so cache it so we know next time
					if (context instanceof XMLNestedValidatorContext) {
						XMLNestedValidatorContext xmlContext = ((XMLNestedValidatorContext) context);
						xmlContext.markURIInaccessible(physical);
					}
					throw e;
				} finally {
					if (isTemp != null) {
						isTemp.close();
					}
				}
				is.setByteStream(new LazyURLInputStream(physical));
			}
		}
		return is;
	}

}
