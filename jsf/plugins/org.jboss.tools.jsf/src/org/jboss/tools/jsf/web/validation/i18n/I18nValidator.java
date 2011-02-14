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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.validation.eclipse.Validator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMText;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.jsf2.model.JSF2ComponentModelManager;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jsf.web.validation.IJSFValidationComponent;
import org.jboss.tools.jsf.web.validation.LocalizedMessage;
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
public class I18nValidator extends Validator implements ISourceValidator, IValidator{
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
			List<IJSFValidationComponent> jsfValComponents = new ArrayList<IJSFValidationComponent>();
			for (Node node : notValidNodes) {
				jsfValComponents.add(I18nValidationComponent.createI18nValidationComponent((IDOMText)node));
			}
			reportProblems(helper, reporter, jsfValComponents);
	}
	
	private void reportProblems(IValidationContext helper, IReporter reporter,
			List<IJSFValidationComponent> jsfValComponents ) {
		IResource resource = JSF2ResourceUtil.getValidatingResource(helper);
		if(resource==null) return;
		try {
			resource.deleteMarkers(I18nValidationComponent.PROBLEM_ID, false, IResource.DEPTH_INFINITE);
			for (IJSFValidationComponent ijsfValidationComponent : jsfValComponents) {
				Message locMessage = LocalizedMessage.createJSFLocalizedMessage(ijsfValidationComponent);
				reporter.addMessage(this, locMessage);
					IMarker marker = resource.createMarker(I18nValidationComponent.PROBLEM_ID);
					marker.setAttributes(locMessage.getAttributes());
			}
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
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
