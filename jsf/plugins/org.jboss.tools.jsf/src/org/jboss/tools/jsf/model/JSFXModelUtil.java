/*******************************************************************************
 * Copyright (c) 2008 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.model;

import java.util.Properties;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModelObject;

public class JSFXModelUtil {
	private static final String ADD_RULE_ACTION = "CreateActions.AddRule"; //$NON-NLS-1$
	
	public static void addRule(XModelObject object, Properties properties){
		XActionInvoker.invoke(ADD_RULE_ACTION, object, properties);
	}
}
