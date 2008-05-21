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
package org.jboss.tools.struts.model;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;
import org.jboss.tools.jst.web.model.*;

public class FileStrutsConfigImpl extends AbstractWebFileImpl {
	private static final long serialVersionUID = 2891583695825891163L;
	static StrutsProcessStructureHelper helper = new StrutsProcessStructureHelper();
	
	protected String getProcessEntity() {
		return "StrutsProcess";
	}
    
	public boolean isProcessLoaded() {
		String entity = getProcessEntity();
		if(entity == null) return false;
		XModelObject[] os = children.getObjects();
		for (int i = 0; i < os.length; i++) {
			if(!entity.equals(os[i].getModelEntity().getName())) continue;
			return ((WebProcess)os[i]).isPrepared();
		}
		return false;
	}

}
