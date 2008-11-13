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
package org.jboss.tools.seam.pages.xml.model;

import java.util.Properties;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModelObject;

public class SeamPagesXModelUtil {
	private static final String ADD_PAGE_ACTION = "CreateActions.AddPage";
	private static final String ADD_EXCEPTION_ACTION = "CreateActions.AddException";
	
	public static void addPage(XModelObject object, Properties properties){
		XActionInvoker.invoke(ADD_PAGE_ACTION, object, properties);
	}
	
	public static void addException(XModelObject object, Properties properties){
		XActionInvoker.invoke(ADD_EXCEPTION_ACTION, object, properties);
	}
}
