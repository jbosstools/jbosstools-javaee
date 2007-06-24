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

public class SeamEventFormLayoutData implements SeamComponentConstants {

	static IFormData SEAM_ACTION_LIST_DEFINITION = new FormData(
			"Actions",
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			new FormAttributeData[]{new FormAttributeData(ATTR_EXPR, 100)}, 
			new String[]{ENT_SEAM_ACTION},
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddAction") //$NON-NLS-1$
		);
			
	private final static IFormData[] SEAM_EVENT_DEFINITIONS = new IFormData[] {
		new FormData(
			"Seam EVENT",
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_EVENT)
		),
		SEAM_ACTION_LIST_DEFINITION
	};
	
	private final static IFormData[] SEAM_ACTION_DEFINITIONS = new IFormData[] {
		new FormData(
			"Seam Action",
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_ACTION)
		)
	};
	
	final static IFormData SEAM_EVENT_FORM_DEFINITION = new FormData(
		ENT_SEAM_EVENT, new String[]{null}, SEAM_EVENT_DEFINITIONS);

	final static IFormData SEAM_ACTION_FORM_DEFINITION = new FormData(
		ENT_SEAM_ACTION, new String[]{null}, SEAM_ACTION_DEFINITIONS);

}
