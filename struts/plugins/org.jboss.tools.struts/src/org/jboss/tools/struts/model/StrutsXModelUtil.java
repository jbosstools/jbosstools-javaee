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
	public static void addRule(XModelObject object, Properties properties){
		XActionInvoker.invoke("CreateActions.AddRule", object, properties);
	}
	
	public static void addAction(XModelObject object, Properties properties){
		XActionInvoker.invoke("CreateActions.CreateAction", object, properties);
	}
	
	public static void addException(XModelObject object, Properties properties){
		XActionInvoker.invoke("CreateActions.CreateException", object, properties);
	}
	
	public static void addForward(XModelObject object, Properties properties){
		XActionInvoker.invoke("CreateActions.CreateForward", object, properties);
	}
	
	public static void addPage(XModelObject object, Properties properties){
		XActionInvoker.invoke("CreateActions.CreatePage", object, properties);
	}
}
