/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.struts.plugins.model.handlers;

import org.jboss.tools.common.model.XModelObject;

public class TilesFileRegistration extends PluginRegistration {

	protected String getPluginClassName() {
		return "org.apache.struts.tiles.TilesPlugin";
	}

	protected String getSetPropertyName() {
		return "definitions-config";
	}
	
	protected void modifyProperties(XModelObject plugin) {
		setModuleAwareIfNeeded(plugin);
	}
	
	private void setModuleAwareIfNeeded(XModelObject plugin) {
		XModelObject property = getSetProperty(plugin, "moduleAware", true, null);
		String value = property.getAttributeValue("value");
		if(value.length() == 0) {
			property.getModel().changeObjectAttribute(property, "value", "true");
		}
	}

}
