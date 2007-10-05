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

public class SeamPropertyFormLayoutData implements SeamComponentConstants {

	public static IFormData SEAM_LIST_ENTRIES_LIST_DEFINITION = new FormData(
		SeamXMLUIMessages.getString("SEAM_PROPERTY_FORM_LAYOUT_DATA_VALUES"), //$NON-NLS-1$
		SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData(ATTR_VALUE, 100)}, 
		new String[]{ENT_SEAM_LIST_ENTRY},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddEntry") //$NON-NLS-1$
	);
		
	private final static IFormData[] SEAM_PROPERTY_LIST_DEFINITIONS = new IFormData[] {
		new FormData(
			SeamXMLUIMessages.getString("SEAM_PROPERTY_FORM_LAYOUT_DATA_SEAM_PROPERTY_LIST"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_PROPERTY_LIST)
		),
		SEAM_LIST_ENTRIES_LIST_DEFINITION,
	};
	
	final static IFormData SEAM_PROPERTY_LIST_FORM_DEFINITION = new FormData(
		ENT_SEAM_PROPERTY_LIST, new String[]{null}, SEAM_PROPERTY_LIST_DEFINITIONS);


	static IFormData SEAM_MAP_ENTRIES_LIST_DEFINITION = new FormData(
		SeamXMLUIMessages.getString("SEAM_PROPERTY_FORM_LAYOUT_DATA_ENTRIES"), //$NON-NLS-1$
		SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData(ATTR_KEY, 40), new FormAttributeData(ATTR_VALUE, 60)}, 
		new String[]{ENT_SEAM_MAP_ENTRY},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddEntry") //$NON-NLS-1$
	);
			
	private final static IFormData[] SEAM_PROPERTY_MAP_DEFINITIONS = new IFormData[] {
		new FormData(
			SeamXMLUIMessages.getString("SEAM_PROPERTY_FORM_LAYOUT_DATA_SEAM_PROPERTY_MAP"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_PROPERTY_MAP)
		),
		SEAM_MAP_ENTRIES_LIST_DEFINITION,
	};

	final static IFormData SEAM_PROPERTY_MAP_FORM_DEFINITION = new FormData(
		ENT_SEAM_PROPERTY_MAP, new String[]{null}, SEAM_PROPERTY_MAP_DEFINITIONS);

}
