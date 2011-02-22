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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMText;
import org.eclipse.wst.xml.core.internal.validation.XMLValidationInfo;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xml.core.internal.validation.eclipse.Validator;
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
	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor){
	if(resource instanceof IFile) 
		this.document= JSF2ComponentModelManager
			.getReadableDOMDocument((IFile) resource);
		return super.validate(resource, kind, state, monitor);
	}
	
	@Override
	public ValidationReport validate(String uri, InputStream inputstream,
			NestedValidatorContext context, ValidationResult result) {
	XMLValidationInfo xmlValidationInfo = new XMLValidationInfo(uri);
    List<IJSFValidationComponent> jsfnonValComponents =  new ArrayList<IJSFValidationComponent>();
	validateDOM(document, jsfnonValComponents);
	for (IJSFValidationComponent ijsfValidationComponent : jsfnonValComponents) {
		xmlValidationInfo.addWarning(ijsfValidationComponent.getValidationMessage(),
				ijsfValidationComponent.getLine(), 0, uri, null, ijsfValidationComponent.getMessageParams());
	}
	return xmlValidationInfo;
	}
	@Override
	public void validate(IValidationContext helper, IReporter reporter)
			throws ValidationException {
		    List<IJSFValidationComponent> jsfnonValComponents =  new ArrayList<IJSFValidationComponent>();
			validateDOM(document, jsfnonValComponents);
			IResource resource = JSF2ResourceUtil.getValidatingResource(helper);
			reportProblems(resource, reporter, jsfnonValComponents);
	}
	
	private void reportProblems(IResource resource, IReporter reporter,
			List<IJSFValidationComponent> jsfValComponents ) {
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
	
	
	
	private void validateDOM(Node node, List<IJSFValidationComponent> jsfnonValComponents){
		NodeList childNodes = node.getChildNodes();
		for(int i=0;i<childNodes.getLength();i++) {
			Node childNode = childNodes.item(i);
			if(childNode instanceof Text){
				if(!validateTextNode(((Text)childNode).getNodeValue())){
					jsfnonValComponents.add(I18nValidationComponent.createI18nValidationComponent((IDOMText)childNode));
				}
			}else {
				validateDOM(childNode, jsfnonValComponents);
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
