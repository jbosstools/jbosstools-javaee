 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.validation;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.el.SeamELCompletionEngine;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * EL Validator
 * @author Alexey Kazakov
 */
public class SeamELValidator extends SeamValidator {

	private SeamELCompletionEngine fEngine= new SeamELCompletionEngine();

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.validation.SeamValidator#validate(java.util.Set)
	 */
	@Override
	public IStatus validate(Set<IFile> changedFiles) throws ValidationException {
		// TODO
		validateAll();
		return OK_STATUS;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.validation.SeamValidator#validateAll()
	 */
	@Override
	public IStatus validateAll() throws ValidationException {
		// TODO
		/*
		Set<IFile> files = validationContext.getRegisteredFiles();
		for (IFile file : files) {
			validateFile(file);
		}
		*/

		return OK_STATUS;
	}

	private void validateFile(IFile file) {
		String ext = file.getFileExtension();
		String content = null;
		try {
			content = FileUtil.readStream(file.getContents());
		} catch (CoreException e) {
			SeamCorePlugin.getDefault().logError(e);
			return;
		}
		if(ext.equalsIgnoreCase("xml")) {
			validateXml(file, content);
		} else if(ext.equalsIgnoreCase("java")) {
			validateJava(file);
		} else {
			validateText(file);
		}
	}

	private void validateXml(IFile file, String content) {
		Document document = new Document(content);
		SeamSaxHandler handler = new SeamSaxHandler(file, document);
		try {
			SAXParserFactory.newInstance().newSAXParser().parse(file.getContents(), handler);
		} catch (SAXException e) {
			SeamCorePlugin.getDefault().logError(e);
			return;
		} catch (IOException e) {
			SeamCorePlugin.getDefault().logError(e);
			return;
		} catch (ParserConfigurationException e) {
			SeamCorePlugin.getDefault().logError(e);
			return;
		} catch (CoreException e) {
			SeamCorePlugin.getDefault().logError(e);
		}

		return;
	}

	private void validateJava(IFile file) {
		
	}

	private void validateText(IFile file) {
		
	}

	/**
	 * @param offset - offset of string in file
	 * @param length - length of string in file
	 */
	private void validateString(IFile file, String string, int offset, int length) {
		if((string.startsWith("#{") || string.startsWith("${")) && !validateEl(file, string)) {
			// Mark
			System.out.println("Error: " + string);
		}
	}

	private boolean validateEl(IFile file, String el) {
		try {
			String exp = el;
			int offset = exp.length()-1;
			String prefix= SeamELCompletionEngine.getPrefix(el, offset);
			prefix = (prefix == null ? "" : prefix);

			// TODO ?
			List<String> suggestions = fEngine.getCompletions(project, file, el, prefix, offset - prefix.length(), true);

			if (suggestions != null && suggestions.size() > 0) {
				return true;
			}
		} catch (BadLocationException e) {
			SeamCorePlugin.getDefault().logError(e);
		} catch (StringIndexOutOfBoundsException e) {
			SeamCorePlugin.getDefault().logError(e);
		}
		return false;
	}

	public class SeamSaxHandler extends DefaultHandler {

		private IFile source;
		private Locator locator;
		private Document document;

		public SeamSaxHandler(IFile source, Document document) {
			super();
			this.source = source;
			this.document = document;
		}

		private int[] getAttributeRange(int attributeIndex, String attributeValue) {
			try {
				int lineOffset = document.getLineOffset(locator.getLineNumber());
				String line = document.get(lineOffset, locator.getColumnNumber()-1);
			} catch (BadLocationException e) {
				throw new RuntimeException(e);
			}
			int[] result = new int[2];
			return result;
		}

		@Override
		public void setDocumentLocator(Locator locator) {
			this.locator = locator;
		}

		@Override
		public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
			for(int i=0; i<attributes.getLength(); i++) {
				String value = attributes.getValue(i);
				SeamELValidator.this.validateString(source, value, 0, 0);
			}
		}

		@Override
	    public void characters (char[] ch, int start, int length) throws SAXException {
			String value = new String(ch, start, length).trim();
			SeamELValidator.this.validateString(source, value, start, length);
	    }
	}
}