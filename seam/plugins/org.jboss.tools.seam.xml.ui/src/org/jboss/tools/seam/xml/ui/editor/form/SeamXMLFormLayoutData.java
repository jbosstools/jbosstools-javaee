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

import java.util.*;
import org.jboss.tools.common.model.util.ClassLoaderUtil;
import org.jboss.tools.common.model.ui.forms.*;

public class SeamXMLFormLayoutData implements IFormLayoutData {
	static {
		ClassLoaderUtil.init();
	}
	
	public static String EMPTY_DESCRIPTION = ""; //$NON-NLS-1$

	private final static IFormData[] FORM_LAYOUT_DEFINITIONS = new IFormData[] {
		SeamComponentsFileFormLayoutData.FILE_11_FORM_DEFINITION,
		SeamComponentsFileFormLayoutData.FILE_12_FORM_DEFINITION,
		SeamComponentsFileFormLayoutData.FILE_20_FORM_DEFINITION,
		SeamComponentsFileFormLayoutData.FILE_21_FORM_DEFINITION,
		SeamComponentsFileFormLayoutData.FILE_22_FORM_DEFINITION,
		SeamComponentFormLayoutData.SEAM_COMPONENT_FILE_FORM_DEFINITION,
		SeamComponentFormLayoutData.SEAM_COMPONENT_FILE_20_FORM_DEFINITION,
		SeamComponentFormLayoutData.SEAM_COMPONENT_FILE_21_FORM_DEFINITION,
		SeamComponentFormLayoutData.SEAM_COMPONENT_FILE_22_FORM_DEFINITION,
		SeamComponentFormLayoutData.SEAM_COMPONENT_FORM_DEFINITION,
		SeamComponentFormLayoutData.SEAM_COMPONENT_20_FORM_DEFINITION,
		SeamPropertyFormLayoutData.SEAM_PROPERTY_LIST_FORM_DEFINITION,
		SeamPropertyFormLayoutData.SEAM_PROPERTY_MAP_FORM_DEFINITION,
		SeamFactoryFormLayoutData.SEAM_FACTORY_FORM_DEFINITION,
		SeamEventFormLayoutData.SEAM_EVENT_FORM_DEFINITION,
		SeamEventFormLayoutData.SEAM_ACTION_FORM_DEFINITION,
		SeamEventFormLayoutData.SEAM_EVENT_20_FORM_DEFINITION,
		SeamEventFormLayoutData.SEAM_ACTION_20_FORM_DEFINITION,
		
	};

	private static Map<String,IFormData> FORM_LAYOUT_DEFINITION_MAP = Collections.synchronizedMap(new ArrayToMap(FORM_LAYOUT_DEFINITIONS));
	
	private static SeamXMLFormLayoutData INSTANCE = new SeamXMLFormLayoutData();
	
	public static IFormLayoutData getInstance() {
		return INSTANCE;
	}
	
	private SeamXMLFormLayoutData() {}

	public IFormData getFormData(String entityName) {
		IFormData data = (IFormData)FORM_LAYOUT_DEFINITION_MAP.get(entityName);
		if(data == null) {
			data = ModelFormLayoutData.getInstance().getFormData(entityName);
		}
		return data;
	}
	
}
