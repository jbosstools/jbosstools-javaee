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
package org.jboss.tools.jsf.verification.vrules;

import org.jboss.tools.jsf.model.*;
import org.jboss.tools.common.verification.vrules.*;

public class JSFCheckFromViewId extends JSFDefaultCheck implements JSFConstants {

	protected boolean isRelevant(VObject object) {
		return object.getParent() != null;
	}

	public VResult[] check(VObject object) {
		String attr = (String)object.getAttribute(ATT_FROM_VIEW_ID);
		if(attr == null || attr.length() == 0 || attr.startsWith("*")) return null; //$NON-NLS-1$
		if(attr.startsWith("/")) return null; //$NON-NLS-1$
		return fire(object, ATT_FROM_VIEW_ID, ATT_FROM_VIEW_ID, null);
	}

}
