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
package org.jboss.tools.struts.debug.internal.step;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.debug.internal.StrutsDebugPlugin;
import org.jboss.tools.struts.debug.internal.condition.GlobalForwardCondition;
import org.jboss.tools.struts.debug.internal.condition.ICondition;

/**
 * @author igels
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class GlobalForwardCheckElement extends BaseCheckElement {

	private ICondition condition;

	public GlobalForwardCheckElement(XModelObject xObject) {
		super(xObject);
		String forwardName = getAttributeValue(xObject, StrutsConstants.ATT_NAME);
		try {
			condition = new GlobalForwardCondition(forwardName);
		} catch(Exception e) {
            StrutsDebugPlugin.log(e);
		}
	}

	public ICondition getCondition() {
		return condition;
	}
}