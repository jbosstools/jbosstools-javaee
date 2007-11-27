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
package org.jboss.tools.jsf.project.capabilities;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;

public class CapabilitiesPerformer extends PerformerItem {
	CapabilityPerformer[] capabilities;
	
	public CapabilitiesPerformer() {}

	public void init(XModel model) {
		XModelObject[] os = JSFCapabilities.getInstance().getChildren();
		capabilities = new CapabilityPerformer[os.length];
		for (int i = 0; i < os.length; i++) {
			capabilities[i] = new CapabilityPerformer();
			capabilities[i].setParent(this);
			capabilities[i].setSelected(false);
			capabilities[i].init(model, os[i]);
		}		
	}
	
	public String getDisplayName() {
		return "Custom Capabilities";
	}

	public IPerformerItem[] getChildren() {
		return capabilities;
	}

	public boolean execute(PerformerContext context) throws Exception {
		boolean b = true;
		for (int i = 0; i < capabilities.length; i++) {
			if(!capabilities[i].isSelected()) continue;
			if(!capabilities[i].execute(context)) b = false;
		}
		return b;
		
	}
	
}
