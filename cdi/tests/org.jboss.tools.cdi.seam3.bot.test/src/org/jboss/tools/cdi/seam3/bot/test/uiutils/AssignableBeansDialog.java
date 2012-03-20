/*******************************************************************************
 * Copyright (c) 2010-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.seam3.bot.test.uiutils;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;

public class AssignableBeansDialog {

	private SWTBotShell shell = null; 
	
	public AssignableBeansDialog(SWTBotShell shell) {
		this.shell = shell;
	}
	
	public SWTBotTable getAllBeans() {
		return shell.bot().table();
	}
	
	public AssignableBeansDialog hideUnavailableBeans() {
		getAllOptions().getAllItems()[0].uncheck();
		return this;
	}
	
	public AssignableBeansDialog showUnavailableBeans() {
		getAllOptions().getAllItems()[0].check();
		return this;
	}
	
	public AssignableBeansDialog hideAmbiguousBeans() {
		getAllOptions().getAllItems()[1].uncheck();
		return this;
	}
	
	public AssignableBeansDialog showAmbiguousBeans() {
		getAllOptions().getAllItems()[1].check();
		return this;
	}
	
	public AssignableBeansDialog hideDefaultBeans() {
		getAllOptions().getAllItems()[2].uncheck();
		return this;
	}
	
	public AssignableBeansDialog showDefaultBeans() {
		getAllOptions().getAllItems()[2].check();
		return this;
	}
	
	private SWTBotTree getAllOptions() {
		return shell.bot().tree();
	}
	
}
