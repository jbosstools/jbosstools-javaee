/*******************************************************************************
 * Copyright (c) 2015-2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.reddeer.ui.editor;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.core.lookup.EditorPartLookup;
import org.jboss.reddeer.core.matcher.WithMnemonicTextMatcher;
import org.jboss.reddeer.core.util.Display;
import org.jboss.reddeer.core.util.ResultRunnable;
import org.jboss.reddeer.swt.api.Tree;
import org.jboss.reddeer.swt.api.TreeItem;
import org.jboss.reddeer.swt.impl.button.FinishButton;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.combo.DefaultCombo;
import org.jboss.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.text.DefaultText;
import org.jboss.reddeer.swt.impl.tree.DefaultTree;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.workbench.impl.editor.DefaultEditor;
import org.jboss.tools.common.model.ui.editor.EditorPartWrapper;

/**
 * RedDeer implementation of web.xml file editor
 * 
 * @author vlado pakan
 *
 */
public class WebXmlEditor extends DefaultEditor {

	private static final String SESSION_CONFIG_NODE = "session-config";
	private static final String WELCOME_FILE_LIST_NODE = "welcome-file-list";
	private static final String JSP_CONFIG_NODE = "JSP Config";
	private static final String LOGIN_CONFIG_NODE = "login-config";
	private static final String LOCALE_ENCODING_MAPPING_LIST = "locale-encoding-mapping-list";
	private static final String SERVLETS_NODE = "Servlets";

	private String title;

	public WebXmlEditor(String title) {
		super(title);
		this.title = title;
	}

	public void activateSourceTab() {
		activateEditorCTabItem("Source");
	}

	public void activateTreeTab() {
		activateEditorCTabItem("Tree");
	}

	private void activateEditorCTabItem(String tabItemLabel) {
		activate();
		new DefaultCTabItem(tabItemLabel).activate();
	}

	public WebXmlSourceEditor getWebXmlSourceEditor() {
		activateSourceTab();
		IEditorPart editorPart = EditorPartLookup.getInstance().getActiveEditor();
		final org.jboss.tools.jst.web.ui.editors.WebCompoundEditor wxe = ((org.jboss.tools.jst.web.ui.editors.WebCompoundEditor) ((EditorPartWrapper) editorPart)
				.getEditor());
		ITextEditor iTextEditor = (ITextEditor) Display.syncExec(new ResultRunnable<IEditorPart>() {
			@Override
			public IEditorPart run() {
				return wxe.getSourceEditor();
			}
		});
		return new WebXmlSourceEditor(iTextEditor);
	}

	public TreeItem selectSessionConfigNode() {
		TreeItem tiToSelect = getWebXmlTreeItem(title, WebXmlEditor.SESSION_CONFIG_NODE);
		tiToSelect.select();
		return tiToSelect;
	}

	public TreeItem selectWelcomeFileListNode() {
		TreeItem tiToSelect = getWebXmlTreeItem(title, WebXmlEditor.WELCOME_FILE_LIST_NODE);
		tiToSelect.select();
		return tiToSelect;
	}

	public TreeItem selectJspConfigNode() {
		TreeItem tiToSelect = getWebXmlTreeItem(title, WebXmlEditor.JSP_CONFIG_NODE);
		tiToSelect.select();
		return tiToSelect;
	}

	public TreeItem selectLoginConfigNode() {
		TreeItem tiToSelect = getWebXmlTreeItem(title, WebXmlEditor.LOGIN_CONFIG_NODE);
		tiToSelect.select();
		return tiToSelect;
	}

	public TreeItem selectLocaleEncodingMappingListNode() {
		TreeItem tiToSelect = getWebXmlTreeItem(title, WebXmlEditor.LOCALE_ENCODING_MAPPING_LIST);
		tiToSelect.select();
		return tiToSelect;
	}

	public TreeItem selectServletsNode() {
		TreeItem tiToSelect = getWebXmlTreeItem(title, WebXmlEditor.SERVLETS_NODE);
		tiToSelect.select();
		return tiToSelect;
	}

	public TreeItem getWebXmlTreeItem(String... path) {
		return new DefaultTreeItem(getWebXmlTree(), path);
	}

	private Tree getWebXmlTree() {
		activateTreeTab();
		return new DefaultTree();
	}

	public void addServlet(String servletName, String displayName, String servleteClass, String servletDescription) {
		selectServletsNode();
		new PushButton("Add...").click();
		new DefaultShell("Add Servlet");
		new DefaultText(0).setText(servletName);
		new DefaultText(1).setText(displayName);
		new DefaultText(2).setText(servleteClass);
		new DefaultText(3).setText(servletDescription);
		new FinishButton().click();
		new WaitWhile(new ShellWithTextIsAvailable("Add Servlet"));
	}

	public void addServletMapping(String servletName, String urlPattern) {
		selectServletsNode();
		new PushButton(1, new WithMnemonicTextMatcher("Add...")).click();
		new DefaultShell("Add Servlet Mapping");
		new DefaultCombo(0).setText(servletName);
		new DefaultText(0).setText(urlPattern);
		new FinishButton().click();
		new WaitWhile(new ShellWithTextIsAvailable("Add Servlet Mapping"));
	}

}