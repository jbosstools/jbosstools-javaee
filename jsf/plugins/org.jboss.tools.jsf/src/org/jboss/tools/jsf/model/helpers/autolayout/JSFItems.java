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
package org.jboss.tools.jsf.model.helpers.autolayout;

import java.util.*;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.model.helpers.autolayout.Items;

public class JSFItems extends Items {
	public XModelObject[] getOutput(XModelObject itemObject) {
		XModelObject[] os = itemObject.getChildren();
		if(os.length == 0) return new XModelObject[0];
		if(os.length == 1) return os[0].getChildren();
		ArrayList<XModelObject> l = new ArrayList<XModelObject>();
		for (int i = 0; i < os.length; i++) {
			XModelObject[] cs = os[i].getChildren();
			for (int j = 0; j < cs.length; j++) l.add(cs[j]);
		}
		return l.toArray(new XModelObject[0]);    	
	}
}
