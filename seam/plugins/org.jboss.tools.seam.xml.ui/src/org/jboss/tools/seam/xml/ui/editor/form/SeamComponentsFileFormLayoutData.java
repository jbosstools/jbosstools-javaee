/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.xml.ui.editor.form;

import org.jboss.tools.common.model.ui.forms.*;
import org.jboss.tools.seam.xml.components.model.SeamComponentConstants;
import org.jboss.tools.seam.xml.ui.SeamXMLUIMessages;

public class SeamComponentsFileFormLayoutData implements SeamComponentConstants {

	static IFormData SEAM_COMPONENT_LIST_DEFINITION = new FormData(
		SeamXMLUIMessages.getString("SEAM_COMPONENTS_FILE_FORM_LAYOUT_DATA_COMPONENTS"), //$NON-NLS-1$
		SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 40), new FormAttributeData("class", 60)}, //$NON-NLS-1$
		new String[]{ENT_SEAM_COMPONENT},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddComponent") //$NON-NLS-1$
	);
	
	static IFormData SEAM_FACTORY_LIST_DEFINITION = new FormData(
		SeamXMLUIMessages.getString("SEAM_COMPONENTS_FILE_FORM_LAYOUT_DATA_FACTORIES"), //$NON-NLS-1$
		SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)},
		new String[]{ENT_SEAM_FACTORY},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddFactory") //$NON-NLS-1$
	);
		
	static IFormData SEAM_EVENT_LIST_DEFINITION = new FormData(
		SeamXMLUIMessages.getString("SEAM_COMPONENTS_FILE_FORM_LAYOUT_DATA_EVENTS"), //$NON-NLS-1$
		SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData(ATTR_TYPE, 100)},
		new String[]{ENT_SEAM_EVENT},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddEvent") //$NON-NLS-1$
	);
			
	private final static IFormData[] FILE_11_DEFINITIONS = new IFormData[] {
		new FormData(
			SeamXMLUIMessages.getString("SEAM_COMPONENTS_FILE_FORM_LAYOUT_DATA_SEAM_COMPONENTS_11_FILE"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_COMPONENTS_11)
		),
		SEAM_COMPONENT_LIST_DEFINITION,
		SEAM_FACTORY_LIST_DEFINITION,
		SEAM_EVENT_LIST_DEFINITION
	};

	private final static IFormData[] FILE_12_DEFINITIONS = new IFormData[] {
		new FormData(
			SeamXMLUIMessages.getString("SEAM_COMPONENTS_FILE_FORM_LAYOUT_DATA_SEAM_COMPONENTS_12_FILE"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_COMPONENTS_12)
		),
		SEAM_COMPONENT_LIST_DEFINITION,
		SEAM_FACTORY_LIST_DEFINITION,
		SEAM_EVENT_LIST_DEFINITION
	};

	private final static IFormData[] FILE_20_DEFINITIONS = new IFormData[] {
		new FormData(
			SeamXMLUIMessages.getString("SEAM_COMPONENTS_FILE_FORM_LAYOUT_DATA_SEAM_COMPONENTS_20_FILE"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_COMPONENTS_20)
		),
		SEAM_COMPONENT_LIST_DEFINITION,
		SEAM_FACTORY_LIST_DEFINITION,
		SEAM_EVENT_LIST_DEFINITION
	};

	final static IFormData FILE_12_FORM_DEFINITION = new FormData(
		ENT_SEAM_COMPONENTS_12, new String[]{null}, FILE_12_DEFINITIONS);

	final static IFormData FILE_11_FORM_DEFINITION = new FormData(
		ENT_SEAM_COMPONENTS_11, new String[]{null}, FILE_11_DEFINITIONS);

	final static IFormData FILE_20_FORM_DEFINITION = new FormData(
		ENT_SEAM_COMPONENTS_20, new String[]{null}, FILE_20_DEFINITIONS);

}
