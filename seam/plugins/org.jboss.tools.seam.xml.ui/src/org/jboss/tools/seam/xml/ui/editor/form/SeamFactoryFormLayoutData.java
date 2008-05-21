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

import org.jboss.tools.common.model.ui.forms.FormData;
import org.jboss.tools.common.model.ui.forms.FormLayoutDataUtil;
import org.jboss.tools.common.model.ui.forms.IFormData;
import org.jboss.tools.seam.xml.components.model.SeamComponentConstants;
import org.jboss.tools.seam.xml.ui.SeamXMLUIMessages;

public class SeamFactoryFormLayoutData implements SeamComponentConstants {

	private final static IFormData[] SEAM_FACTORY_DEFINITIONS = new IFormData[] {
		new FormData(
			SeamXMLUIMessages.getString("SEAM_FACTORY_FORM_LAYOUT_DATA_SEAM_FACTORY"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_FACTORY)
		),
		new FormData(
			SeamXMLUIMessages.getString("SEAM_FACTORY_FORM_LAYOUT_DATA_ADVANCED"), //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createAdvancedFormAttributeData(ENT_SEAM_FACTORY)
		)
	};
	
	final static IFormData SEAM_FACTORY_FORM_DEFINITION = new FormData(
		ENT_SEAM_FACTORY, new String[]{null}, SEAM_FACTORY_DEFINITIONS);

}
