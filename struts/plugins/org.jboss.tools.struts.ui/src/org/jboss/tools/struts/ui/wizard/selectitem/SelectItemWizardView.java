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
package org.jboss.tools.struts.ui.wizard.selectitem;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.jst.web.ui.wizards.process.SelectWebProcessItemWizardView;

public class SelectItemWizardView extends SelectWebProcessItemWizardView {
	
	protected String getItemEntity() {
		return "StrutsProcessItem";
	}

	protected String getKey(XModelObject o) {
		String type = o.getAttributeValue(StrutsConstants.ATT_TYPE);
		return (StrutsConstants.TYPE_EXCEPTION.equals(type)) ? 
		       o.getAttributeValue(StrutsConstants.ATT_ID)
		       : (StrutsConstants.TYPE_FORWARD.equals(type)) ?
		       o.getAttributeValue(StrutsConstants.ATT_TITLE)
			   : o.getAttributeValue(StrutsConstants.ATT_PATH);
	}
	
}
