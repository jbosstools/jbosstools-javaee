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

import org.jboss.tools.common.model.XModelObject;

public class StrutsProjectTiles extends StrutsProjectPlugin {
	private static final long serialVersionUID = 542432599464877227L;

	protected String getPathnames(XModelObject c) {
		return getPathnames(c, "org.apache.struts.tiles.TilesPlugin", "definitions-config");
	}
	
	public XModelObject getTreeParent(XModelObject object) {
		String entity = object.getModelEntity().getName();
		if(!entity.equals("FileTiles")) return null;
		return (isChild(object)) ? this : null;
	}

}
