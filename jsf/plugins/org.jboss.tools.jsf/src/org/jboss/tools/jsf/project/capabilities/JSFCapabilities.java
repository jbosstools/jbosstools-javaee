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
package org.jboss.tools.jsf.project.capabilities;

import java.util.Properties;
import org.jboss.tools.common.model.impl.ExtraRootImpl;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.common.model.loaders.XObjectLoader;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.common.reporting.ProblemReportingHelper;
import org.jboss.tools.jsf.messages.JSFUIMessages;

public class JSFCapabilities extends ExtraRootImpl {
	private static final long serialVersionUID = 1L;
	static JSFCapabilities instance = null;

	public static JSFCapabilities getInstance() {
		if(instance == null) {
			try {
				createInstance();
			} catch (Exception t) {
				ProblemReportingHelper.reportProblem("org.jboss.tools.jsf", JSFUIMessages.CANNOT_LOAD_JSF_CAPABILITIES, t);
			}
		}
		return instance;		
	}
	
	private static void createInstance() {
		XModelImpl model = (XModelImpl)PreferenceModelUtilities.getPreferenceModel();
		instance = (JSFCapabilities)model.getByPath("root:Capabilities");
		instance = (JSFCapabilities)model.createModelObject("JSFCapabilities", new Properties());
		XObjectLoader loader = XModelObjectLoaderUtil.getObjectLoader(instance);
		loader.load(instance);
		model.setExtraRoot(instance);
	}
	
	public String getPathPart() {
		return "root:" + getAttributeValue("name");
	}
	
	public void save() {
		if(this != getInstance()) return;
		XObjectLoader loader = XModelObjectLoaderUtil.getObjectLoader(this);
		loader.save(this);
	}
}
