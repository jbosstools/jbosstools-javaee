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

import org.jboss.tools.common.model.ui.forms.FormData;
import org.jboss.tools.common.model.ui.forms.FormLayoutDataUtil;
import org.jboss.tools.common.model.ui.forms.IFormData;
import org.jboss.tools.seam.xml.components.model.SeamComponentConstants;

public class SeamFactoryFormLayoutData implements SeamComponentConstants {

	private final static IFormData[] SEAM_FACTORY_DEFINITIONS = new IFormData[] {
		new FormData(
			"Seam Factory",
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_FACTORY)
		),
		new FormData(
			"Advanced",
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createAdvancedFormAttributeData(ENT_SEAM_FACTORY)
		)
	};
	
	final static IFormData SEAM_FACTORY_FORM_DEFINITION = new FormData(
		ENT_SEAM_FACTORY, new String[]{null}, SEAM_FACTORY_DEFINITIONS);

}
