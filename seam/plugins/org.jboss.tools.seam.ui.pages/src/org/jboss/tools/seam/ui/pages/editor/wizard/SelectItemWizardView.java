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
package org.jboss.tools.seam.ui.pages.editor.wizard;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.ui.wizards.process.SelectWebProcessItemWizardView;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;

public class SelectItemWizardView extends SelectWebProcessItemWizardView {
	
	protected String getItemEntity() {
		return SeamPagesConstants.ENT_DIAGRAM_ITEM;
	}

	protected String getKey(XModelObject o) {
		String entity = o.getModelEntity().getName();
		if(entity.equals(SeamPagesConstants.ENT_DIAGRAM_ITEM))
			return o.getAttributeValue(SeamPagesConstants.ATTR_PATH);
		String key = o.getParent().getAttributeValue(SeamPagesConstants.ATTR_PATH) + ":" + o.getPresentationString();
		return key;
	}
	
}
