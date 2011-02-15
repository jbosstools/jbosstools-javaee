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

import java.text.MessageFormat;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ValidatorConstants;

/**
 * 
 * @author mareshkau
 *
 */
public class I18nMarkerResolution implements IMarkerResolution {
	
	private String invalidString="";
		
	public I18nMarkerResolution(IMarker marker) {
		try {
			invalidString =  (String) marker.getAttribute(JSF2ValidatorConstants.INVALID_STRING_KEY);
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
	}

	public String getLabel() {
		return  MessageFormat.format(JSFUIMessages.NonExternalizedStringMarkerLabel,invalidString); 
	}

	public void run(IMarker marker) {
	
	}
}
