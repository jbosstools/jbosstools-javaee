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
package org.jboss.tools.jsf.ui.editor.wizard;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jst.web.ui.wizards.process.SelectWebProcessItemWizardView;

public class SelectItemWizardView extends SelectWebProcessItemWizardView {
	
	protected String getItemEntity() {
		return JSFConstants.ENT_PROCESS_GROUP;
	}

	protected String getKey(XModelObject o) {
		String entity = o.getModelEntity().getName();
		if(entity.equals(JSFConstants.ENT_PROCESS_GROUP) || entity.equals(JSFConstants.ENT_PROCESS_ITEM))
			return o.getAttributeValue(JSFConstants.ATT_PATH);
		String key = o.getParent().getAttributeValue(JSFConstants.ATT_PATH) + ":" + o.getPresentationString(); //$NON-NLS-1$
		return key;
	}
	
}
