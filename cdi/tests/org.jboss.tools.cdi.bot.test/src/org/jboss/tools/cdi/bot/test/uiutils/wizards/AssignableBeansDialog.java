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

package org.jboss.tools.cdi.bot.test.uiutils.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.helper.TreeHelper;

public class AssignableBeansDialog {

	private SWTBot bot = null;
	
	private final String UNAVAILABLE_BEANS = "Unavailable Beans";
	
	private final String DECORATOR = "@Decorator";
	
	private final String INTERCEPTOR = "@Interceptor";
	
	private final String ELIMINATED_AMBIGUOUS = "Eliminated ambiguous";
	
	private final String UNSELECTED_ALTERNATIVE = "Unselected @Alternative";
	
	private final String UNAVAILABLE_PRODUCER = "@Produces in unavailable bean"; 
	
	private final String SPECIALIZED_BEANS = "Specialized beans";
			
	public AssignableBeansDialog(SWTBotShell shell) {
		this.bot = shell.bot();
		showAmbiguousBeans().showUnavailableBeans().
		showDecorators().showInterceptors().showUnselectedAlternatives().
		showUnavailableProducers().showSpecializedBeans();
	}
	
	public List<String> getAllBeans() {
		List<String> allBeans = new ArrayList<String>();
		for (int i = 0; i < bot.table().rowCount(); i++) {
			allBeans.add(bot.table().getTableItem(i).getText());
		}
		return allBeans;
	}
	
	public AssignableBeansDialog typeInFilter(String text) {
		bot.text().setText(""); // clear filter textbox
		bot.text().typeText(text);
		return this;
	}
	
	public AssignableBeansDialog hideUnavailableBeans() {
		getTreeItem(UNAVAILABLE_BEANS).uncheck();
		return this;
	}
	
	public AssignableBeansDialog showUnavailableBeans() {
		getTreeItem(UNAVAILABLE_BEANS).check();
		return this;
	}
	
	public AssignableBeansDialog hideDecorators() {
		getTreeItem(UNAVAILABLE_BEANS, DECORATOR).uncheck();
		return this;
	}
	
	public AssignableBeansDialog showDecorators() {
		getTreeItem(UNAVAILABLE_BEANS, DECORATOR).check();
		return this;
	}
	
	public AssignableBeansDialog hideInterceptors() {
		getTreeItem(UNAVAILABLE_BEANS, INTERCEPTOR).uncheck();
		return this;
	}
	
	public AssignableBeansDialog showInterceptors() {
		getTreeItem(UNAVAILABLE_BEANS, INTERCEPTOR).check();
		return this;
	}
	
	public AssignableBeansDialog hideUnselectedAlternatives() {
		getTreeItem(UNAVAILABLE_BEANS, UNSELECTED_ALTERNATIVE).uncheck();
		return this;
	}
	
	public AssignableBeansDialog showUnselectedAlternatives() {
		getTreeItem(UNAVAILABLE_BEANS, UNSELECTED_ALTERNATIVE).check();
		return this;
	}
	
	public AssignableBeansDialog hideUnavailableProducers() {
		getTreeItem(UNAVAILABLE_BEANS, UNAVAILABLE_PRODUCER).uncheck();
		return this;
	}
	
	public AssignableBeansDialog showUnavailableProducers() {
		getTreeItem(UNAVAILABLE_BEANS, UNAVAILABLE_PRODUCER).check();
		return this;
	}
	
	public AssignableBeansDialog hideSpecializedBeans() {
		getTreeItem(UNAVAILABLE_BEANS, SPECIALIZED_BEANS).uncheck();
		return this;
	}
	
	public AssignableBeansDialog showSpecializedBeans() {
		getTreeItem(UNAVAILABLE_BEANS, SPECIALIZED_BEANS).check();
		return this;
	}
	
	public AssignableBeansDialog hideAmbiguousBeans() {
		getTreeItem(ELIMINATED_AMBIGUOUS).uncheck();
		return this;
	}
	
	public AssignableBeansDialog showAmbiguousBeans() {
		getTreeItem(ELIMINATED_AMBIGUOUS).check();
		return this;
	}
	
	protected SWTBotTreeItem getTreeItem(String... path) {
		return TreeHelper.expandNode(bot, path);
	}
	
}
