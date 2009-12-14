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
package org.jboss.tools.cdi.xml.ui.editor.form;

import org.jboss.tools.cdi.xml.beans.model.CDIBeansConstants;
import org.jboss.tools.common.model.ui.forms.*;

public class CDIBeansFileFormLayoutData implements CDIBeansConstants {

	static IFormData getClassListDefinition(String title) {
		return new FormData(
			title,
			CDIXMLFormLayoutData.EMPTY_DESCRIPTION,
			new FormAttributeData[]{new FormAttributeData("class", 100)}, //$NON-NLS-1$
			new String[]{ENT_CDI_CLASS},
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.CreateClass") //$NON-NLS-1$
		);
	}
		
	static IFormData CDI_STEREOTYPE_LIST_DEFINITION = new FormData(
		"Stereotypes", //$NON-NLS-1$
		CDIXMLFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData("stereotype", 100)}, //$NON-NLS-1$
		new String[]{ENT_CDI_STEREOTYPE},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.CreateStereotype") //$NON-NLS-1$
	);
			
	private final static IFormData[] DECORATORS_DEFINITIONS = new IFormData[] {
		getClassListDefinition("Decorators") //$NON-NLS-1$
	};

	private final static IFormData[] INTERCEPTORS_DEFINITIONS = new IFormData[] {
		getClassListDefinition("Interceptors") //$NON-NLS-1$
	};

	private final static IFormData[] ALTERNATIVES_DEFINITIONS = new IFormData[] {
		new FormData(
			"Alternatives", //$NON-NLS-1$
			CDIXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_ALTERNATIVES)
		),
		getClassListDefinition("Classes"), //$NON-NLS-1$
		CDI_STEREOTYPE_LIST_DEFINITION
	};

	private final static IFormData[] FILE_10_DEFINITIONS = new IFormData[] {
		new FormData(
			"CDI Beans 1.0 File", //$NON-NLS-1$
			CDIXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_CDI_BEANS)
		),
	};

	final static IFormData FILE_20_FORM_DEFINITION = new FormData(
		ENT_CDI_BEANS, new String[]{null}, FILE_10_DEFINITIONS);

	final static IFormData DECORATORS_DEFINITION = new FormData(
		ENT_DECORATORS, new String[]{null}, DECORATORS_DEFINITIONS);

	final static IFormData INTERCEPTORS_DEFINITION = new FormData(
		ENT_INTERCEPTORS, new String[]{null}, INTERCEPTORS_DEFINITIONS);

	final static IFormData ALTERNATIVES_DEFINITION = new FormData(
		ENT_ALTERNATIVES, new String[]{null}, ALTERNATIVES_DEFINITIONS);

}
