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
package org.jboss.tools.jsf.verification.vrules.toview;

import org.jboss.tools.jsf.model.*;
import org.jboss.tools.jsf.verification.vrules.JSFDefaultCheck;
import org.jboss.tools.common.verification.vrules.*;

public class JSFCheckToViewIdStar extends JSFDefaultCheck implements JSFConstants {

	public VResult[] check(VObject object) {
		String attr = (String)object.getAttribute(ATT_TO_VIEW_ID);
		if(attr == null || attr.indexOf("*") < 0) return null; //$NON-NLS-1$
		return fire(object, ATT_TO_VIEW_ID, ATT_TO_VIEW_ID, null);
	}

}
