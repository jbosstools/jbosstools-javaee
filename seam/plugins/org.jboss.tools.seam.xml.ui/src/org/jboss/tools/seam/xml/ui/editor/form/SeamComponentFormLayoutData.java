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

import org.jboss.tools.common.model.ui.forms.FormAttributeData;
import org.jboss.tools.common.model.ui.forms.FormData;
import org.jboss.tools.common.model.ui.forms.FormLayoutDataUtil;
import org.jboss.tools.common.model.ui.forms.IFormData;
import org.jboss.tools.seam.xml.components.model.SeamComponentConstants;
import org.jboss.tools.seam.xml.ui.SeamXMLUIMessages;

public class SeamComponentFormLayoutData implements SeamComponentConstants {

	static IFormData SEAM_PROPERTY_LIST_DEFINITION = new FormData(
		SeamXMLUIMessages.getString("SEAM_COMPONENT_FORM_LAYOUT_DATA_PROPERTIES"), //$NON-NLS-1$
		SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)}, 
		new String[]{ENT_SEAM_PROPERTY, ENT_SEAM_PROPERTY_LIST, ENT_SEAM_PROPERTY_MAP},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAnyProperty") //$NON-NLS-1$
	);
		
	private final static IFormData[] SEAM_COMPONENT_DEFINITIONS = new IFormData[] {
		new FormData(
			SeamXMLUIMessages.getString("SEAM_COMPONENT_FORM_LAYOUT_DATA_SEAM_COMPONENT"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_COMPONENT)
		),
		SEAM_PROPERTY_LIST_DEFINITION,
		new FormData(
			SeamXMLUIMessages.getString("SEAM_COMPONENT_FORM_LAYOUT_DATA_ADVANCED"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createAdvancedFormAttributeData(ENT_SEAM_COMPONENT)
		)
	};
	
	private final static IFormData[] SEAM_COMPONENT_20_DEFINITIONS = new IFormData[] {
		new FormData(
			SeamXMLUIMessages.getString("SEAM_COMPONENT_FORM_LAYOUT_DATA_SEAM_COMPONENT"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_COMPONENT_20)
		),
		SEAM_PROPERTY_LIST_DEFINITION,
		new FormData(
			SeamXMLUIMessages.getString("SEAM_COMPONENT_FORM_LAYOUT_DATA_ADVANCED"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createAdvancedFormAttributeData(ENT_SEAM_COMPONENT_20)
		)
	};
	
	final static IFormData SEAM_COMPONENT_20_FORM_DEFINITION = new FormData(
		ENT_SEAM_COMPONENT_20, new String[]{null}, SEAM_COMPONENT_20_DEFINITIONS);

	final static IFormData SEAM_COMPONENT_FORM_DEFINITION = new FormData(
		ENT_SEAM_COMPONENT, new String[]{null}, SEAM_COMPONENT_DEFINITIONS);

	private final static IFormData[] COMPONENT_FILE_12_DEFINITIONS = new IFormData[] {
		new FormData(
			SeamXMLUIMessages.getString("SEAM_COMPONENT_FORM_LAYOUT_DATA_SEAM_COMPONENT_1_2_FILE"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_COMPONENT_12)
		),
		SEAM_PROPERTY_LIST_DEFINITION,
		new FormData(
			SeamXMLUIMessages.getString("SEAM_COMPONENT_FORM_LAYOUT_DATA_ADVANCED"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createAdvancedFormAttributeData(ENT_SEAM_COMPONENT_12)
		)
	};

	private final static IFormData[] COMPONENT_FILE_20_DEFINITIONS = new IFormData[] {
		new FormData(
			SeamXMLUIMessages.getString("SEAM_COMPONENT_FORM_LAYOUT_DATA_SEAM_COMPONENT_2_0_FILE"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_COMPONENT_FILE_20)
		),
		SEAM_PROPERTY_LIST_DEFINITION,
		new FormData(
			SeamXMLUIMessages.getString("SEAM_COMPONENT_FORM_LAYOUT_DATA_ADVANCED"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createAdvancedFormAttributeData(ENT_SEAM_COMPONENT_FILE_20)
		)
	};

	private final static IFormData[] COMPONENT_FILE_21_DEFINITIONS = new IFormData[] {
		new FormData(
			SeamXMLUIMessages.getString("SEAM_COMPONENT_FORM_LAYOUT_DATA_SEAM_COMPONENT_2_1_FILE"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_COMPONENT_FILE_21)
		),
		SEAM_PROPERTY_LIST_DEFINITION,
		new FormData(
			SeamXMLUIMessages.getString("SEAM_COMPONENT_FORM_LAYOUT_DATA_ADVANCED"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createAdvancedFormAttributeData(ENT_SEAM_COMPONENT_FILE_21)
		)
	};

	private final static IFormData[] COMPONENT_FILE_22_DEFINITIONS = new IFormData[] {
		new FormData(
			SeamXMLUIMessages.getString("SEAM_COMPONENT_FORM_LAYOUT_DATA_SEAM_COMPONENT_2_2_FILE"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_COMPONENT_FILE_22)
		),
		SEAM_PROPERTY_LIST_DEFINITION,
		new FormData(
			SeamXMLUIMessages.getString("SEAM_COMPONENT_FORM_LAYOUT_DATA_ADVANCED"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createAdvancedFormAttributeData(ENT_SEAM_COMPONENT_FILE_22)
		)
	};

	final static IFormData SEAM_COMPONENT_FILE_FORM_DEFINITION = new FormData(
		ENT_SEAM_COMPONENT_12, new String[]{null}, COMPONENT_FILE_12_DEFINITIONS);

	final static IFormData SEAM_COMPONENT_FILE_20_FORM_DEFINITION = new FormData(
		ENT_SEAM_COMPONENT_FILE_20, new String[]{null}, COMPONENT_FILE_20_DEFINITIONS);

	final static IFormData SEAM_COMPONENT_FILE_21_FORM_DEFINITION = new FormData(
		ENT_SEAM_COMPONENT_FILE_21, new String[]{null}, COMPONENT_FILE_21_DEFINITIONS);


	final static IFormData SEAM_COMPONENT_FILE_22_FORM_DEFINITION = new FormData(
		ENT_SEAM_COMPONENT_FILE_22, new String[]{null}, COMPONENT_FILE_22_DEFINITIONS);

}
