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
package org.jboss.tools.jsf.web.validation;

import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.wst.validation.ValidatorMessage;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jsf.web.validation.i18n.I18nValidationComponent;
import org.jboss.tools.jsf.web.validation.jsf2.JSF2XMLValidator;
import org.jboss.tools.jsf.web.validation.jsf2.components.JSF2AttrTempComponent;
import org.jboss.tools.jsf.web.validation.jsf2.components.JSF2CompositeTempComponent;
import org.jboss.tools.jsf.web.validation.jsf2.components.JSF2URITempComponent;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ValidatorConstants;
/**
 * @author mareshkau
 *
 */
@SuppressWarnings("restriction")
public class LocalizedMessage extends Message {
	private IJSFValidationComponent component;

	private LocalizedMessage(){}

	public static LocalizedMessage createJSF2LocalizedMessage(IJSFValidationComponent component,
			IFile validateFile){
		LocalizedMessage jsf2LocMessage = new LocalizedMessage();
		jsf2LocMessage.component = component;
		jsf2LocMessage.setAttribute("problemType", JSF2XMLValidator.JSF2_PROBLEM_ID); //$NON-NLS-1$
		jsf2LocMessage.setAttribute(JSF2ValidatorConstants.JSF2_TYPE_KEY, component
				.getType());
		jsf2LocMessage.setAttribute(
				"validateResourcePath", validateFile == null ? "" : validateFile.getFullPath().toString()); //$NON-NLS-1$//$NON-NLS-2$
		jsf2LocMessage.setAttribute(JSF2ResourceUtil.COMPONENT_RESOURCE_PATH_KEY,
				component.getComponentResourceLocation());
		jsf2LocMessage.setAttribute(IMarker.LINE_NUMBER, jsf2LocMessage.getLineNumber());
		jsf2LocMessage.setAttribute(IMarker.SEVERITY, 1);
		jsf2LocMessage.setAttribute(ValidatorMessage.ValidationId, "org.jboss.tools.jsf.jsf2.source"); //$NON-NLS-1$
		if (component instanceof JSF2URITempComponent) {
			jsf2LocMessage.setAttribute(JSF2ValidatorConstants.JSF2_URI_NAME_KEY,
					((JSF2URITempComponent) component).getURI());
		} else if (component instanceof JSF2AttrTempComponent) {
			jsf2LocMessage.setAttribute(JSF2ValidatorConstants.JSF2_ATTR_NAME_KEY,
					((JSF2AttrTempComponent) component).getName());
			jsf2LocMessage.setAttribute(JSF2ResourceUtil.JSF2_COMPONENT_NAME, ((JSF2AttrTempComponent) component).getElementName());
		} else if (component instanceof JSF2CompositeTempComponent) {
			String[] attrNames = ((JSF2CompositeTempComponent) component)
					.getAttrNames();
			if (attrNames != null) {
				for (int i = 0; i < attrNames.length; i++) {
					jsf2LocMessage.setAttribute(
							JSF2ValidatorConstants.JSF2_ATTR_NAME_KEY
									+ String.valueOf(i), attrNames[i]);
				}
				jsf2LocMessage.setAttribute(JSF2ResourceUtil.JSF2_COMPONENT_NAME, ((JSF2CompositeTempComponent) component).getElement().getLocalName());
			}
		}
		jsf2LocMessage.setAttribute(IMarker.MESSAGE,jsf2LocMessage.getText());
		return jsf2LocMessage;
	}
	
	public static Message createJSFLocalizedMessage(
			IJSFValidationComponent ijsfValidationComponent, int severity) {
		LocalizedMessage i18nLocMessage = new LocalizedMessage();
		i18nLocMessage.component = ijsfValidationComponent;
		i18nLocMessage.setAttribute("problemType", I18nValidationComponent.PROBLEM_ID); //$NON-NLS-1$
		i18nLocMessage.setAttribute(IMarker.LINE_NUMBER, i18nLocMessage.getLineNumber());
		i18nLocMessage.setAttribute(IMarker.SEVERITY, severity);
		i18nLocMessage.setAttribute(ValidatorMessage.ValidationId, "org.jboss.tools.jsf.i18n.source"); //$NON-NLS-1$
		i18nLocMessage.setAttribute(IMarker.MESSAGE,i18nLocMessage.getText());
		i18nLocMessage.setAttribute(JSF2ValidatorConstants.PROBLEM_LENGHT, i18nLocMessage.getLength());
		i18nLocMessage.setAttribute(JSF2ValidatorConstants.PROBLEM_OFFSET, i18nLocMessage.getOffset());
		if(ijsfValidationComponent instanceof I18nValidationComponent){
			i18nLocMessage.setAttribute(JSF2ValidatorConstants.INVALID_STRING_KEY, 
					((I18nValidationComponent)ijsfValidationComponent).getInValidString());
		}
		return i18nLocMessage;
	}
	
	@Override
	public int getLineNumber() {
		return component.getLine();
	}

	@Override
	public int getLength() {
		return component.getLength();
	}

	@Override
	public int getOffset() {
		return component.getStartOffSet();
	}

	@Override
	public String getText() {
		return component.getValidationMessage();
	}

	@Override
	public String getText(Locale locale) {
		return component.getValidationMessage();
	}

	@Override
	public String getText(Locale locale, ClassLoader classLoader) {
		return component.getValidationMessage();
	}

	@Override
	public String getText(ClassLoader classLoader) {
		return component.getValidationMessage();
	}

	@Override
	public int getSeverity() {
		return IMessage.NORMAL_SEVERITY;
	}

}
