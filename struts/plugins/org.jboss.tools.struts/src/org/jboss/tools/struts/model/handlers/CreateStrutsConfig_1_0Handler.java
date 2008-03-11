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

import java.util.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.model.StrutsProcessImpl;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.impl.CreateFileHandler;

public class CreateStrutsConfig_1_0Handler extends CreateFileHandler implements StrutsConstants {
	private XModelObject created = null;

	public void executeHandler(XModelObject object, Properties prop) throws Exception {
		try {
			super.executeHandler(object, prop);
			if(created != null) {
				StrutsProcessImpl process = (StrutsProcessImpl)created.getChildByPath("process");
				process.firePrepared();
			}
		} finally {
			created = null;
		}
	}
	            
	protected XModelObject modifyCreatedObject(XModelObject o) {
		return created = o;
	}

}
