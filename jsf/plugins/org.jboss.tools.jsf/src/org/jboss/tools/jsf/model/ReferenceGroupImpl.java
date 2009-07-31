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
import org.jboss.tools.common.model.impl.OrderedObjectImpl;
import org.jboss.tools.jst.web.model.ReferenceObject;

public class ReferenceGroupImpl extends OrderedObjectImpl implements ReferenceObject, JSFConstants {
    private static final long serialVersionUID = 6904636256636930824L;
	private XModelObject[] reference = new XModelObject[0];
	private long[] referenceTimeStamp = new long[0];
	private boolean isUpToDate = true;
	
	public void setReference(XModelObject[] rs) {
		if(isReferenceEqual(rs)) return;
		isUpToDate = false;
		long[] rts = new long[rs.length];
		for (int i = 0; i < rs.length; i++) {
			rts[i] = (i < reference.length && reference[i] == rs[i]) ? referenceTimeStamp[i] : -1;
		}
		reference = rs;
		referenceTimeStamp = rts;
		saveShapeToReference(get("SHAPE"));		 //$NON-NLS-1$
	}
	
	void saveShapeToReference(String shape) {
		if(shape != null && shape.length() > 0) {
			for (int i = 0; i < reference.length; i++)
				reference[i].set("_shape_g", shape); //$NON-NLS-1$
		}
	}
	
	public XModelObject[] getReferences() {
		return reference;
	}
	
	public boolean isUpToDate() {
		if(!isUpToDate) return false;
		for (int i = 0; i < reference.length; i++) if(!isUpToDate(i)) return false;
		return true;
	}

	public void notifyUpdate() {
		isUpToDate = true;
		for (int i = 0; i < reference.length; i++) notifyUpdate(i);
	}

	public boolean isUpToDate(int i) {
		return (reference[i].getTimeStamp() == referenceTimeStamp[i]);
	}
    
	public void notifyUpdate(int i) {
		referenceTimeStamp[i] = reference[i].getTimeStamp();
	}

	boolean isReferenceEqual(XModelObject[] rs) {
		if(rs.length != reference.length) return false;
		for (int i = 0; i < rs.length; i++) {
			if(rs[i] != reference[i]) return false;
		}
		return true;
	}

	public String getPresentationString() {
		String s = "" + getAttributeValue(ATT_PATH); //$NON-NLS-1$
		if(s.length() == 0) s = JSFConstants.EMPTY_NAVIGATION_RULE_NAME;
		return s;
	}

	public void set(String name, String value) {
		if("SHAPE".equals(name)) { //$NON-NLS-1$
			saveShapeToReference(value);
		}
		super.set(name, value);
	}
	
	public String getMainIconName() {
		String path = getAttributeValue(ATT_PATH);
		if(path == null || path.length() == 0 || path.indexOf('*') > 0) return "main.file.unknow_file"; //$NON-NLS-1$
		if(path.endsWith(".jsp")) return "main.file.jsp_file"; //$NON-NLS-1$ //$NON-NLS-2$
		if(path.endsWith(".html") || path.endsWith(".htm")) return "main.file.html_file"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return "main.file.unknow_file";  //$NON-NLS-1$
	}

	public XModelObject getReference() {
		return reference.length == 0 ? null : reference[0];
	}

}
