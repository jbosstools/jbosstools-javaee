/*******************************************************************************
 * Copyright (c) 2010-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.bot.test;

import org.jboss.tools.cdi.bot.test.uiutils.SWTEclipseCDIExtUtil;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.junit.BeforeClass;

public abstract class AbstractTestSuite extends SWTTestExt {

	/*
	 * init method "setup()" shows a project explorer view as default, disable
	 * folding (to easier source code editing)
	 */
	@BeforeClass
	public static void setUpSuite() {
		eclipse.showView(ViewType.PROJECT_EXPLORER);
		SWTEclipseCDIExtUtil.disableFolding(bot, util);
	}

}
