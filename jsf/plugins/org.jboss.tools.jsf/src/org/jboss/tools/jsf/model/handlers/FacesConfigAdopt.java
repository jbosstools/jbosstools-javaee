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
package org.jboss.tools.jsf.model.handlers;

import java.util.Properties;
import org.jboss.tools.common.meta.XAdoptManager;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

public class FacesConfigAdopt implements XAdoptManager {
	JSPAdopt jspAdopt = new JSPAdopt();

	public boolean isAdoptable(XModelObject target, XModelObject object) {
		if(isAdoptableBundle(object)) return true;
		if(jspAdopt.isAdoptablePage(object)) return true;
		return false;
	}

	public void adopt(XModelObject target, XModelObject object, Properties p) throws XModelException {
		if(jspAdopt.isAdoptablePage(object)) adoptPage(target, object, p);
		else if(isAdoptableBundle(object)) adoptBundle(target, object, p);
	}

    protected boolean isAdoptableBundle(XModelObject object) {
        return "FilePROPERTIES".equals(object.getModelEntity().getName()); //$NON-NLS-1$
    }

	public void adoptPage(XModelObject target, XModelObject object, Properties p) {
		if(p == null) return;
		String res = XModelObjectLoaderUtil.getResourcePath(object);
		if(res == null) return;
		p.setProperty("start text", res); //$NON-NLS-1$
		p.setProperty("end text", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void adoptBundle(XModelObject target, XModelObject object, Properties p) {
		if(p == null) return;
		String res = XModelObjectLoaderUtil.getResourcePath(object);
		if(res == null) return;
		if(res != null && res.endsWith(".properties")) { //$NON-NLS-1$
			res = res.substring(1, res.length() - 11).replace('/', '.');
		}
		p.setProperty("start text", res); //$NON-NLS-1$
		p.setProperty("end text", ""); //$NON-NLS-1$ //$NON-NLS-2$
		
	}

}
