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
package org.jboss.tools.struts.model.pv;

import org.jboss.tools.common.model.XFilteredTreeConstraint;
import org.jboss.tools.common.model.*;

public class StrutsProjectsTreeConstraint implements XFilteredTreeConstraint {
	String hideEntities = ".FileTiles.FileTLD_1_2.FileTLD_PRO.FileTLD_2_0.FileTLD_2_1" +
	  ".StrutsConfig10.StrutsConfig11.StrutsConfig12" +
	  ".FileValidationRules.FileValidationRules11" +
	  ".";

	public void update(XModel model) {}

	public boolean accepts(XModelObject object) {
		if(object.getFileType() > XModelObject.NONE
		   && "true".equals(object.get("overlapped"))) return false;
		if(object.getFileType() == XModelObject.FILE) {
			//show only pages in web-inf
			String path = object.getPath();
			if(path == null) return false;
			if(!path.startsWith("FileSystems/WEB-INF/")) {
				String entity = "." + object.getModelEntity().getName() + ".";
				if(hideEntities.indexOf(entity) >= 0) return false;
				return true;
			}
			String extension = "." + object.getAttributeValue("extension") + ".";
			return ".jsp.html.htm.css.".indexOf(extension) >= 0;
		}
		return true;
	}

	public boolean isHidingAllChildren(XModelObject object) {
		return false;
	}

	public boolean isHidingSomeChildren(XModelObject object) {
		if(object.getModelEntity().getName().equals("FileSystemFolder")) return true;
		return false;
	}

}
