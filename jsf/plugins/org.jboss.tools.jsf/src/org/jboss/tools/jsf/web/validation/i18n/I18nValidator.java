/*******************************************************************************
 * Copyright (c) 2007-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.web.validation.i18n;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.jboss.tools.jsf.jsf2.model.JSF2ComponentModelManager;
import org.jboss.tools.jst.jsp.bundle.BundleMapUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Validator which looks for non externalized strings
 * 
 * @author mareshkau
 *
 */
@SuppressWarnings("restriction")
public class I18nValidator implements ISourceValidator, IValidator{
	private IDOMDocument document;
	public void connect(IDocument document) {
		this.document=JSF2ComponentModelManager
		.getReadableDOMDocument(document);
	}

	public void disconnect(IDocument document) {
		this.document=null;
	}

	public void validate(IRegion dirtyRegion, IValidationContext helper,
			IReporter reporter) {
		// TODO Auto-generated method stub
	}

	public void cleanup(IReporter reporter) {
		// TODO Auto-generated method stub
		
	}

	public void validate(IValidationContext helper, IReporter reporter)
			throws ValidationException {
			List<Node> notValidNodes = new ArrayList<Node>();
			validateDOM(document, notValidNodes);
			
	}
	
	
	private void validateDOM(Node node, List<Node> nonExtStings){
		NodeList childNodes = node.getChildNodes();
		for(int i=0;i<childNodes.getLength();i++) {
			Node childNode = childNodes.item(i);
			if(childNode instanceof Text){
				if(!validateTextNode(((Text)childNode).getNodeValue())){
					nonExtStings.add(childNode);
				}
			}else {
				validateDOM(childNode, nonExtStings);
			}			
		}
	}
	/**
	 * Return false if not not valid
	 * @param String to validate
	 * @return true is string ext and valid, false otherwise
	 *
	 */
	private boolean validateTextNode(String stringToValidate){
		if(stringToValidate==null) return true;
		if(stringToValidate.trim().length()<1) return true;
		return BundleMapUtil.isContainsEl(stringToValidate);
	}
}
