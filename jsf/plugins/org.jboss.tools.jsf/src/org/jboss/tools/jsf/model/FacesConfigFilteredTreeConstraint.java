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
package org.jboss.tools.jsf.model;

import org.jboss.tools.common.model.*;

public class FacesConfigFilteredTreeConstraint implements XFilteredTreeConstraint, JSFConstants {
	boolean onlyHideProcess = true;
	
	public void setEditorEnvironment(boolean b) {
		onlyHideProcess = !b;
	}

	public void update(XModel model) {		
	}
	
	static String HIDING_ALL_CHILDREN_ENTITIES = ".JSFListEntries."; //$NON-NLS-1$

	public boolean isHidingAllChildren(XModelObject object) {
		String entity = object.getModelEntity().getName();
		return (HIDING_ALL_CHILDREN_ENTITIES.indexOf("." + entity + ".") >= 0); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	static String HIDING_SOME_CHILDREN_ENTITIES = "." + ENT_FACESCONFIG_10 + "." + ENT_FACESCONFIG_11 + "." + ENT_FACESCONFIG_12 + "." + "JSFApplication.JSFApplication12.";  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

	public boolean isHidingSomeChildren(XModelObject object) {
		String entity = object.getModelEntity().getName();
		return (HIDING_SOME_CHILDREN_ENTITIES.indexOf("." + entity + ".") >= 0); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	static String HIDDEN_CHILDREN_ENTITIES = ".JSFProcess.JSFLifecycle.JSFFactory." +  //$NON-NLS-1$
		"JSFLocaleConfig." + //$NON-NLS-1$
		"JSFMessageBundle."; //$NON-NLS-1$

	public boolean accepts(XModelObject object) {
		String entity = object.getModelEntity().getName();
		if(onlyHideProcess) {
			if("JSFProcess".equals(entity)) return false; //$NON-NLS-1$
		} else {
			if(HIDDEN_CHILDREN_ENTITIES.indexOf("." + entity + ".") >= 0) return false; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return true;
	}

}
