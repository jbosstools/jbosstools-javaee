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
import org.jboss.tools.common.model.impl.*;
import org.jboss.tools.jsf.model.helpers.*;
import org.jboss.tools.jst.web.model.*;

public class FacesProcessImpl extends OrderedObjectImpl implements WebProcess, ReferenceObject, JSFConstants {
	private static final long serialVersionUID = 1981573715076399163L;
	protected XModelObject reference;
	protected JSFProcessHelper phelper = new JSFProcessHelper(this);
	protected JSFUpdateHelper uhelper = null;
	protected boolean isPrepared = false;

	public XModelObject getReference() {
		return reference;
	}

	public void setReference(XModelObject reference) {
		this.reference = reference;
		if(reference != null) {
			String shape = get("SHAPE"); //$NON-NLS-1$
			if(shape != null && shape.length() > 0) reference.set("_shape", shape); //$NON-NLS-1$
		}
	}

	public boolean isPrepared() {
		return isPrepared;
	}
    
	public void firePrepared() {
		 isPrepared = true;
		 fireStructureChanged(3, getPath());
	}
    
	public void autolayout() {
		phelper.autolayout();
	}

	protected void loadChildren() {
		if (isPrepared && reference == null && isActive()) {
			restoreRefs();
			registerListener();
			updateProcess();
		}
	}
    
	protected void restoreRefs() {
		phelper.restoreRefs();
	}
    
	protected void updateProcess() {
		phelper.updateProcess();
	}
    
	protected void registerListener() {
		if (uhelper == null) {
			uhelper = new JSFUpdateHelper(this);
		}
	}
    
	protected void deactivate() {
		if (uhelper != null) {
			uhelper.unregister();
			uhelper = null;
		}
	}
    
	public JSFProcessHelper getHelper() {
		return phelper;
	}

	protected void changeTimeStamp() {
		boolean actualBody = false;
		String abts = null;
		XModelObject parent = (XModelObject)getParent();
		if(parent != null) {
			abts = parent.get("actualBodyTimeStamp"); //$NON-NLS-1$
			actualBody = (abts != null && (abts.equals("0") || abts.equals("" + parent.getTimeStamp()))); //$NON-NLS-1$ //$NON-NLS-2$
		}
		super.changeTimeStamp();
		if(actualBody && !abts.equals("0")) { //$NON-NLS-1$
			parent.set("actualBodyTimeStamp", "" + parent.getTimeStamp()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
    
}
