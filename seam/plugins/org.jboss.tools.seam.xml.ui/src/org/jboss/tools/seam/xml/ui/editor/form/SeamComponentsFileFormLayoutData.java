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

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.common.meta.XChild;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.meta.XModelMetaData;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.model.ui.forms.*;
import org.jboss.tools.seam.xml.components.model.SeamComponentConstants;

public class SeamComponentsFileFormLayoutData implements SeamComponentConstants {

	static IFormData createSeamComponentListDefinition(String parentEntity) {
		XModelMetaData meta = PreferenceModelUtilities.getPreferenceModel().getMetaData();
		XModelEntity entity = meta.getEntity(parentEntity);
		List<String> childEntities = new ArrayList<String>();
		if(entity != null) {
			XChild[] cs = entity.getChildren();
			for (XChild c: cs) {
				XModelEntity e = meta.getEntity(c.getName());
				if(e != null && e.getAttribute(ATTR_NAME) != null && e.getAttribute(ATTR_CLASS) != null) {
					childEntities.add(c.getName());
				}
			}
		}
		
		IFormData result = new FormData(
			"Components", //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 40), new FormAttributeData(ATTR_CLASS, 60)}, //$NON-NLS-1$
			childEntities.toArray(new String[0]),
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddComponent") //$NON-NLS-1$
		);
		return result;
	}
		
	static IFormData getFactoryList(String entity) {
		return new FormData(	
			"Factories", //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			new FormAttributeData[]{new FormAttributeData(ATTR_NAME, 100)},
			new String[]{entity},
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddFactory") //$NON-NLS-1$
		);
	}
		
	static IFormData getEventList(String entity) {
		return new FormData(
			"Events", //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			new FormAttributeData[]{new FormAttributeData(ATTR_TYPE, 100)},
			new String[]{entity},
			FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddEvent") //$NON-NLS-1$
		);
	}
			
	static IFormData SEAM_IMPORT_LIST_DEFINITION = new FormData(
		"Imports", //$NON-NLS-1$
		SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
		new FormAttributeData[]{new FormAttributeData("import", 100)}, //$NON-NLS-1$
		new String[]{ENT_SEAM_IMPORT},
		FormLayoutDataUtil.createDefaultFormActionData("CreateActions.AddImport") //$NON-NLS-1$
	);
		
	private final static IFormData[] FILE_11_DEFINITIONS = new IFormData[] {
		new FormData(
			"Seam Components 1.1 File", //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_COMPONENTS_11)
		),
		createSeamComponentListDefinition(ENT_SEAM_COMPONENTS_11),
		getFactoryList(ENT_SEAM_FACTORY),
		getEventList(ENT_SEAM_EVENT)
	};

	private final static IFormData[] FILE_12_DEFINITIONS = new IFormData[] {
		new FormData(
			"Seam Components 1.2 File", //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_COMPONENTS_12)
		),
		createSeamComponentListDefinition(ENT_SEAM_COMPONENTS_12),
		getFactoryList(ENT_SEAM_FACTORY),
		getEventList(ENT_SEAM_EVENT)
	};

	private final static IFormData[] FILE_20_DEFINITIONS = new IFormData[] {
		new FormData(
			"Seam Components 2.0 File", //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_COMPONENTS_20)
		),
		createSeamComponentListDefinition(ENT_SEAM_COMPONENTS_20),
		getFactoryList(ENT_SEAM_FACTORY_20),
		getEventList(ENT_SEAM_EVENT_20),
		SEAM_IMPORT_LIST_DEFINITION
	};

	private final static IFormData[] FILE_21_DEFINITIONS = new IFormData[] {
		new FormData(
			"Seam Components 2.1 File", //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_COMPONENTS_21)
		),
		createSeamComponentListDefinition(ENT_SEAM_COMPONENTS_21),
		getFactoryList(ENT_SEAM_FACTORY_20),
		getEventList(ENT_SEAM_EVENT_20),
		SEAM_IMPORT_LIST_DEFINITION
	};

	private final static IFormData[] FILE_22_DEFINITIONS = new IFormData[] {
		new FormData(
			"Seam Components 2.2 File", //$NON-NLS-1$
			SeamXMLFormLayoutData.EMPTY_DESCRIPTION,
			FormLayoutDataUtil.createGeneralFormAttributeData(ENT_SEAM_COMPONENTS_22)
		),
		createSeamComponentListDefinition(ENT_SEAM_COMPONENTS_22),
		getFactoryList(ENT_SEAM_FACTORY_20),
		getEventList(ENT_SEAM_EVENT_20),
		SEAM_IMPORT_LIST_DEFINITION
	};

	final static IFormData FILE_12_FORM_DEFINITION = new FormData(
		ENT_SEAM_COMPONENTS_12, new String[]{null}, FILE_12_DEFINITIONS);

	final static IFormData FILE_11_FORM_DEFINITION = new FormData(
		ENT_SEAM_COMPONENTS_11, new String[]{null}, FILE_11_DEFINITIONS);

	final static IFormData FILE_20_FORM_DEFINITION = new FormData(
		ENT_SEAM_COMPONENTS_20, new String[]{null}, FILE_20_DEFINITIONS);

	final static IFormData FILE_21_FORM_DEFINITION = new FormData(
		ENT_SEAM_COMPONENTS_21, new String[]{null}, FILE_21_DEFINITIONS);

	final static IFormData FILE_22_FORM_DEFINITION = new FormData(
		ENT_SEAM_COMPONENTS_22, new String[]{null}, FILE_22_DEFINITIONS);
}
