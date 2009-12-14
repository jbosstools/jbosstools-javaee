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

import java.util.*;
import org.jboss.tools.common.model.util.ClassLoaderUtil;
import org.jboss.tools.common.model.ui.forms.*;

public class CDIXMLFormLayoutData implements IFormLayoutData {
	static {
		ClassLoaderUtil.init();
	}
	
	public static String EMPTY_DESCRIPTION = ""; //$NON-NLS-1$

	private final static IFormData[] FORM_LAYOUT_DEFINITIONS = new IFormData[] {
		CDIBeansFileFormLayoutData.FILE_20_FORM_DEFINITION,
		CDIBeansFileFormLayoutData.DECORATORS_DEFINITION,
		CDIBeansFileFormLayoutData.INTERCEPTORS_DEFINITION,
		CDIBeansFileFormLayoutData.ALTERNATIVES_DEFINITION
	};

	private static Map<String,IFormData> FORM_LAYOUT_DEFINITION_MAP = Collections.synchronizedMap(new ArrayToMap(FORM_LAYOUT_DEFINITIONS));
	
	private static CDIXMLFormLayoutData INSTANCE = new CDIXMLFormLayoutData();
	
	public static IFormLayoutData getInstance() {
		return INSTANCE;
	}
	
	private CDIXMLFormLayoutData() {}

	public IFormData getFormData(String entityName) {
		IFormData data = (IFormData)FORM_LAYOUT_DEFINITION_MAP.get(entityName);
		if(data == null) {
			data = ModelFormLayoutData.getInstance().getFormData(entityName);
		}
		return data;
	}
	
}
