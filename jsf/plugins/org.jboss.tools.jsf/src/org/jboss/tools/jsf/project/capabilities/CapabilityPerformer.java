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

import org.jboss.tools.common.model.*;

public class CapabilityPerformer extends PerformerItem {
	XModel model;
	XModelObject capability;
	
	FileAdditionsPerformer fileAdditionsPerformer = new FileAdditionsPerformer();
	LibrariesPerformer librariesPerformer = new LibrariesPerformer();
	
	public CapabilityPerformer() {
		fileAdditionsPerformer.setParent(this);
		librariesPerformer.setParent(this);
	}

	public String getDisplayName() {
		return capability.getAttributeValue("name");
	}

	public IPerformerItem[] getChildren() {
		return new IPerformerItem[]{librariesPerformer, fileAdditionsPerformer};
	}

	public void init(XModel model, XModelObject capability) {
		this.model = model;
		this.capability = capability;
		librariesPerformer.init(model, capability.getChildren("JSFLibraryReference"));
		fileAdditionsPerformer.init(model, capability);
	}
	
	public boolean execute(PerformerContext context) throws Exception {
		if(!isSelected()) return true;
		context.monitor.worked(1);
		context.monitor.subTask(getDisplayName());
		if(!librariesPerformer.check()) return false;
		if(!fileAdditionsPerformer.check()) return false;		
		if(!fileAdditionsPerformer.execute(context)) return true;
		librariesPerformer.execute(context);
		return true;
	}

}
