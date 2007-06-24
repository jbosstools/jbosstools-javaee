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
package org.jboss.tools.struts.text.ext.hyperlink;

import org.eclipse.jface.text.IRegion;

import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.common.text.ext.hyperlink.ClassMethodHyperlink;
import org.jboss.tools.struts.text.ext.StrutsExtensionsPlugin;

/**
 * @author Jeremy
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StrutsValidationMethodHyperlink extends ClassMethodHyperlink {
	private static final String CLASSNAME_ATTRNAME = "classname";
	private static final String METHODPARAMS_ATTRNAME = "methodParams";


	protected String getMethodName(IRegion region) {
		try {
			return Utils.trimQuotes(getDocument().get(region.getOffset(), region.getLength()));
		} catch (Exception x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
		}
	}
	
	protected String getClassName(IRegion region) {
		return getAttributeValue(region, CLASSNAME_ATTRNAME);
	}
	
	protected String getMethodParams(IRegion region) {
		return getAttributeValue(region, METHODPARAMS_ATTRNAME);
	}
}
