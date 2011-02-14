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

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMText;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.web.validation.JSFAbstractValidationComponent;

/**
 * @author mareshkau
 *
 */
public class I18nValidationComponent extends JSFAbstractValidationComponent{
	public static String PROBLEM_ID = JSFModelPlugin.PLUGIN_ID
	+ ".i18nproblemmarker"; //$NON-NLS-1$
	
	//component creating usung factory method
	private I18nValidationComponent(){}
	
	public static I18nValidationComponent createI18nValidationComponent(IDOMText element){
		I18nValidationComponent component =  new I18nValidationComponent();
		component.setStartOffSet(element.getStartOffset());
		component.setLength(element.getLength());
		component.setLine(element.getStructuredDocument().getLineOfOffset(
				component.getStartOffSet()) + 1);
		component.createValidationMessage();
		component.createMessageParams();
		return component;
	}
	

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getComponentResourceLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createValidationMessage() {
		setValidationMessage("Non externalized String literal; It should be moved to resource bundles.");
	}

}
