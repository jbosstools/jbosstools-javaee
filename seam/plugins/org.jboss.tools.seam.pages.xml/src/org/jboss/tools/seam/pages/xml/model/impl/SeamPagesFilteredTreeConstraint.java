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
package org.jboss.tools.seam.pages.xml.model.impl;

import org.jboss.tools.common.model.*;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SeamPagesFilteredTreeConstraint implements XFilteredTreeConstraint, SeamPagesConstants {

	public SeamPagesFilteredTreeConstraint() {}

	public void update(XModel model) {		
	}
	
	public boolean isHidingAllChildren(XModelObject object) {
		return false;
	}
	
	public boolean isHidingSomeChildren(XModelObject object) {
		String entity = object.getModelEntity().getName();
		return entity.startsWith(ENT_FILE_SEAM_PAGE);
	}
	
	public boolean accepts(XModelObject object) {
		String entity = object.getModelEntity().getName();
		if(ENT_DIAGRAM.equals(entity)) return false;
		return true;
	}

}
