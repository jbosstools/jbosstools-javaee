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
package org.jboss.tools.struts.validator.ui.adapter;

import java.util.Map;

import org.jboss.tools.common.model.impl.bundle.CountriesHelper;
import org.jboss.tools.common.model.ui.attribute.adapter.DefaultXAttributeListContentProvider;
import org.jboss.tools.struts.validator.ui.XStudioValidatorPlugin;

public class CountryListContentProvider extends DefaultXAttributeListContentProvider {

	protected void loadTags() {
		try { 
			Map<String,String> map = CountriesHelper.countries;
			tags = map.keySet().toArray(new String[0]);
		} catch (Exception t) {
			XStudioValidatorPlugin.getPluginLog().logError(t);			
			super.loadTags();
		}
	}

}
