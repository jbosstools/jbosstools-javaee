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
package org.jboss.tools.struts.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.files.handlers.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.model.StrutsProcessImpl;
import org.jboss.tools.struts.webprj.model.helpers.sync.SortFileSystems;

public class CreateStrutsConfig_1_0Support extends CreateFileSupport implements StrutsConstants {

	protected void execute() throws Exception {
		Properties p = extractStepData(0);
		String path = p.getProperty("name");
		path = revalidatePath(path);
		XModelObject file = createFile(path);
		if(file == null) return;		

		StrutsProcessImpl process = (StrutsProcessImpl)file.getChildByPath("process");
		process.firePrepared();

		SortFileSystems.sort(getTarget().getModel());

		open(file);
	}

}
