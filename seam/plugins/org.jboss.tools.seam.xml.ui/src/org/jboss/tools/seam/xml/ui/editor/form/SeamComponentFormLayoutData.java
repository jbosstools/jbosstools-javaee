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
package org.jboss.tools.seam.xml.ui.editor.form;

import org.jboss.tools.common.model.ui.forms.FormAttributeData;
import org.jboss.tools.common.model.ui.forms.FormData;
import org.jboss.tools.common.model.ui.forms.FormLayoutDataUtil;
import org.jboss.tools.common.model.ui.forms.IFormData;
import org.jboss.tools.seam.xml.components.model.SeamComponentConstants;

public class SeamComponentFormLayoutData implements SeamComponentConstants {

	static IFormData SEAM_PROPERTY_LIST_DEFINITION = new FormData(
		"Properties",
		SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)}, 
		new String[]{ENT_SEAM_PROPERTY, ENT_SEAM_PROPERTY_LIST, ENT_SEAM_PROPERTY_MAP},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyProperty") //$NON-NLS-1$
	);
		
	private final static IFormData[] SEAM_COMPONENT_DEFINITIONS = new IFormData[] {
		new FormData(
			"Seam Component",
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_COMPONENT)
		),
		SEAM_PROPERTY_LIST_DEFINITION,
		new FormData(
			"Advanced",
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createAdvancedFormAttributeData(ENT_SEAM_COMPONENT)
		)
	};
	
	final static IFormData SEAM_COMPONENT_FORM_DEFINITION = new FormData(
		ENT_SEAM_COMPONENT, new String[]{null}, SEAM_COMPONENT_DEFINITIONS);

}
