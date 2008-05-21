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
package org.jboss.tools.struts.model.handlers.page;

import org.jboss.tools.jst.web.browser.*;
import org.jboss.tools.jst.web.browser.wtp.RunOnServerContext;

public class RunOnServerHandler extends RunHandler {
	static AbstractBrowserContext context = RunOnServerContext.getInstance();
	
	static {
		IPathSourceImpl pathSource = new IPathSourceImpl();
		pathSource.setBrowserContext(context);
		context.addPathSource(pathSource);
	}
	
    public RunOnServerHandler() {}

	protected AbstractBrowserContext getContext() {
		return context;
	}

}
