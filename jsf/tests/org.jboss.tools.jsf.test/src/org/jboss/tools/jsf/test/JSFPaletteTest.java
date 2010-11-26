/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.test;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;

import junit.framework.TestCase;

public class JSFPaletteTest extends TestCase {
	
	public void testJSFPalette() {
		XModel model = PreferenceModelUtilities.getPreferenceModel();
		assertNotNull("Cannot find preference model.", model);
		
		XModelObject palette = model.getRoot("Palette");
		assertNotNull("Cannot find Palette model object.", palette);
		
		XModelObject jsfTab = palette.getChildByPath("JSF");
		assertNotNull("Cannot find JSF Palette model object.", jsfTab);
		
		XModelObject htmlGroup = jsfTab.getChildByPath("HTML");
		assertNotNull("Cannot find JSF HTML Group model object.", htmlGroup);
		String htmlGroupHiffen = htmlGroup.getAttributeValue("hidden");
		assertEquals("JSF HTML Group should not be hidden", "no", htmlGroupHiffen);

		XModelObject coreGroup = jsfTab.getChildByPath("Core");
		assertNotNull("Cannot find JSF Core Group model object.", coreGroup);
		String coreGroupHiffen = coreGroup.getAttributeValue("hidden");
		assertEquals("JSF Core Group should be hidden", "yes", coreGroupHiffen);
	}

}
