/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.pages.xml.model;

import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.OrderedByEntityChildren;
import org.jboss.tools.common.model.impl.RegularChildren;
import org.jboss.tools.common.model.loaders.XObjectLoader;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jst.web.model.AbstractWebFileImpl;
import org.jboss.tools.jst.web.model.WebProcessLoader;
import org.jboss.tools.seam.pages.xml.model.impl.SeamPagesDiagramImpl;

public class FileSeamPagesImpl extends AbstractWebFileImpl implements SeamPagesConstants {
	private static final long serialVersionUID = 1L;
	
	public FileSeamPagesImpl() {}

	protected RegularChildren createChildren() {
		return new OrderedByEntityChildren();
	}

	protected String getProcessEntity() {
		return SeamPagesConstants.ENT_DIAGRAM;
	}

	protected boolean hasDTD() {
		return SeamPagesConstants.ENT_FILE_SEAM_PAGES_12.equals(getModelEntity().getName());
	}

	protected void mergeAll(XModelObject f, boolean update) throws XModelException {
		SeamPagesDiagramImpl diagram = (SeamPagesDiagramImpl)provideWebProcess();
		boolean b = (diagram != null && diagram.isPrepared());
		if(b) diagram.getHelper().addUpdateLock(this);
		merge(f, !update);
		if(b) {
			diagram.getHelper().removeUpdateLock(this);
			diagram.getHelper().updateDiagram();
		}

		if(diagram != null) {
			if(!diagram.isPrepared()/* || update*/ || isForceLoadOn()) {
				XObjectLoader loader = XModelObjectLoaderUtil.getObjectLoader(this);
				((WebProcessLoader)loader).reloadProcess(this);
			}
			if(diagram.isPrepared())
				diagram.autolayout();
		}
	}

}
