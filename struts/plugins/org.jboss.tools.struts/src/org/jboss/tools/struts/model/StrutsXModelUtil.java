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
package org.jboss.tools.struts.model;

import java.util.Properties;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModelObject;

public class StrutsXModelUtil {
	private static final String ADD_RULE_ACTION = "CreateActions.AddRule";
	private static final String CREATE_ACTION_ACTION = "CreateActions.CreateAction";
	private static final String CREATE_EXCEPTION_ACTION = "CreateActions.CreateException";
	private static final String CREATE_FORWARD_ACTION = "CreateActions.CreateForward";
	private static final String CREATE_PAGE_ACTION = "CreateActions.CreatePage";
	
	public static void addRule(XModelObject object, Properties properties){
		XActionInvoker.invoke(ADD_RULE_ACTION, object, properties);
	}
	
	public static void addAction(XModelObject object, Properties properties){
		XActionInvoker.invoke(CREATE_ACTION_ACTION, object, properties);
	}
	
	public static void addException(XModelObject object, Properties properties){
		XActionInvoker.invoke(CREATE_EXCEPTION_ACTION, object, properties);
	}
	
	public static void addForward(XModelObject object, Properties properties){
		XActionInvoker.invoke(CREATE_FORWARD_ACTION, object, properties);
	}
	
	public static void addPage(XModelObject object, Properties properties){
		XActionInvoker.invoke(CREATE_PAGE_ACTION, object, properties);
	}
}
