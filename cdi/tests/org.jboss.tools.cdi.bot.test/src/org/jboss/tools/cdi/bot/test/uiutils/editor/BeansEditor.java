/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.bot.test.uiutils.editor;

import org.apache.log4j.Level;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.IEditorReference;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.widgets.SWTBotMultiPageEditor;

/**
 * @author Lukas Jungmann
 */
public class BeansEditor extends SWTBotMultiPageEditor {
	
	public enum Item {
		INTERCEPTOR("Interceptors"), DECORATOR("Decorators"),
		CLASS("Alternatives"), STEREOTYPE("Alternatives");
		
		private final String node;
		
		private Item(String node) {
			this.node = node;
		}
		
		private String getNode() {
			return node;
		}
		
		public String getElementName() {
			switch (this) {
			case STEREOTYPE:
				return "stereotype";
			default:
				return "class";
			}
		}
	}
	
	private SWTBotExt bot = new SWTBotExt();
	private static final String ROOT_NODE = "beans.xml";

	public BeansEditor(IEditorReference editorReference, SWTWorkbenchBot bot) {
		super(editorReference, bot);
	}

	public BeansEditor add(Item item, String name) {
		return modify(item, name, "Add...", new AddDialogHandler(item, name));
	}
	
	public BeansEditor remove(Item item, String name) {
		return modify(item, name, "Remove...", new DeleteDialogHandler());
	}
	
	public String getSelectedItem() {
		return bot().tree().selection().get(0, 0);
	}
	
	private BeansEditor modify(Item item, String name, String actionLabel, DialogHandler h) {
		SWTBotTree tree = bot.tree(1);		
		for (SWTBotTreeItem ti:tree.getAllItems()) {
			log.setLevel(Level.FATAL);
			log.fatal(ti.getText());
		}
		tree.expandNode(ROOT_NODE, item.getNode()).select().click();
		selectItem(item, name);
		getItemButton(item, actionLabel).click();
		h.handle(bot.activeShell());
		bot.sleep(500);
		this.setFocus();
		return this;
	}
	
	private void selectItem(Item item, String name) {
		SWTBotTable t = item == Item.STEREOTYPE ? bot.table(1) : bot.table(0);
		if (t.containsItem(name)) {
			t.select(name);
		}
	}
	
	private SWTBotButton getItemButton(Item i, String label) {
		return i == Item.STEREOTYPE ? bot.button(label, 1) : bot.button(label, 0);
	}

	private interface DialogHandler {
		void handle(SWTBotShell dialog);
	}
	
	private class AddDialogHandler implements DialogHandler {
		
		private final Item type;
		private final String name;
		
		public AddDialogHandler(Item type, String name) {
			this.type = type;
			this.name = name;
		}
		
		public void handle(SWTBotShell dialog) {
			SWTBot sh = dialog.bot();
			SWTBotText t = type == Item.STEREOTYPE
				? sh.textWithLabel("Stereotype:*")
				: sh.textWithLabel("Class:*");
			t.setText(name);
			sh.button("Finish").click();
		}
	}
	
	private class DeleteDialogHandler implements DialogHandler {

		public void handle(SWTBotShell dialog) {
			dialog.bot().button("OK").click();
		}
		
	}
}
