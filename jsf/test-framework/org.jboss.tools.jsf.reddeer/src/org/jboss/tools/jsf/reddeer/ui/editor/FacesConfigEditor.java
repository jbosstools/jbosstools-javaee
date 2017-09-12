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
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.swt.api.Tree;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.FinishButton;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.text.DefaultText;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.eclipse.reddeer.workbench.impl.editor.DefaultEditor;
import org.jboss.tools.common.model.ui.editor.EditorPartWrapper;
import org.jboss.tools.jsf.reddeer.ProjectType;
import org.eclipse.reddeer.workbench.core.lookup.EditorPartLookup;
import org.eclipse.reddeer.common.util.Display;
import org.eclipse.reddeer.common.util.ResultRunnable;

public class FacesConfigEditor extends DefaultEditor {

	public FacesConfigEditor(String title) {
		super(title);
	}

	public void activateSourceTab() {
		activateEditorCTabItem("Source");
	}

	public void activateTreeTab() {
		activateEditorCTabItem("Tree");
	}

	public void activateDiagramTab() {
		activateEditorCTabItem("Source");
	}

	private void activateEditorCTabItem(String tabItemLabel) {
		activate();
		new DefaultCTabItem(tabItemLabel).activate();
	}

	public FacesConfigSourceEditor getFacesConfigSourceEditor() {
		activateSourceTab();
		IEditorPart editorPart = EditorPartLookup.getInstance().getActiveEditor();
		final org.jboss.tools.jsf.ui.editor.FacesConfigEditor fce = ((org.jboss.tools.jsf.ui.editor.FacesConfigEditor) ((EditorPartWrapper) editorPart)
				.getEditor());
		ITextEditor iTextEditor = (ITextEditor) Display.syncExec(new ResultRunnable<IEditorPart>() {
			@Override
			public IEditorPart run() {
				return fce.getSourceEditor();
			}
		});
		return new FacesConfigSourceEditor(iTextEditor);
	}

	public void addManagedBean(ProjectType testProjectType, String facesConfigFileName, String beanName,
			String beanClass) {
		selectManagedBeansNode(facesConfigFileName);
		// Add managed bean
		new PushButton("Add...").click();
		new DefaultShell(FacesConfigEditor.getAddManagedBeanDialogTitle(testProjectType));
		new DefaultText(0).setText(beanClass);
		new DefaultText(1).setText(beanName);
		new FinishButton().click();
		new WaitWhile(new ShellIsAvailable(FacesConfigEditor.getAddManagedBeanDialogTitle(testProjectType)));
	}
	
	public void addComponent(String facesConfigFileName, String componentType,	String componentClass) {
		selectComponentsNode(facesConfigFileName);
		// Add Component
		new PushButton("Add...").click();
		new DefaultShell("Add Component");
		new DefaultText(0).setText(componentType);
		new DefaultText(1).setText(componentClass);
		new FinishButton().click();
		new WaitWhile(new ShellIsAvailable("Add Component"));
	}
	
	public void addConverter(String facesConfigFileName, String converterId,	String converterClass) {
		selectConvertersNode(facesConfigFileName);
		// Add Converter
		new PushButton("Add...").click();
		new DefaultShell("Add Converter");
		new DefaultText(0).setText(converterId);
		new DefaultText(1).setText(converterClass);
		new FinishButton().click();
		new WaitWhile(new ShellIsAvailable("Add Converter"));
	}
	
	public void addReferencedBean(String facesConfigFileName, String referencedBeanName, String referecnedBeanClass) {
		selectReferencedBeansNode(facesConfigFileName);
		// Add Referenced Bean
		new PushButton("Add...").click();
		new DefaultShell("Add Referenced Bean");
		new DefaultText(0).setText(referencedBeanName);
		new DefaultText(1).setText(referecnedBeanClass);
		new FinishButton().click();
		new WaitWhile(new ShellIsAvailable("Add Referenced Bean"));
	}
	
	public void addRenderKit(ProjectType testProjectType,String facesConfigFileName, String renderKitId, String renderKitClass) {
		selectRenderKitsNode(facesConfigFileName);
		// Add Render Kit
		new PushButton("Add...").click();
		new DefaultShell(FacesConfigEditor.getAddRenderKitDialogTitle(testProjectType));
		new DefaultText(0).setText(renderKitId);
		new DefaultText(1).setText(renderKitClass);
		new FinishButton().click();
		new WaitWhile(new ShellIsAvailable(FacesConfigEditor.getAddRenderKitDialogTitle(testProjectType)));
	}	

	public void addValidator(String facesConfigFileName, String renderKitId, String renderKitClass) {
		selectValidatorsNode(facesConfigFileName);
		// Add Validator
		new PushButton("Add...").click();
		new DefaultShell("Add Validator");
		new DefaultText(0).setText(renderKitId);
		new DefaultText(1).setText(renderKitClass);
		new FinishButton().click();
		new WaitWhile(new ShellIsAvailable("Add Validator"));
	}
	
	
	public void selectManagedBeansNode(String facesConfigFileName) {
		getFacesConfigTreeItem(facesConfigFileName, "Managed Beans").select();
	}
	
	public void selectComponentsNode(String facesConfigFileName) {
		getFacesConfigTreeItem(facesConfigFileName, "Components").select();
	}

	public void selectConvertersNode(String facesConfigFileName) {
		getFacesConfigTreeItem(facesConfigFileName, "Converters").select();
	}
	
	public void selectReferencedBeansNode(String facesConfigFileName) {
		getFacesConfigTreeItem(facesConfigFileName, "Referenced Beans").select();
	}
	
	public void selectRenderKitsNode(String facesConfigFileName) {
		getFacesConfigTreeItem(facesConfigFileName, "Render Kits").select();
	}
	
	public void selectValidatorsNode(String facesConfigFileName) {
		getFacesConfigTreeItem(facesConfigFileName, "Validators").select();
	}
	
	public TreeItem getFacesConfigTreeItem(String... path) {
		return new DefaultTreeItem(getFacesConfigTree(), path);
	}

	private Tree getFacesConfigTree() {
		activateTreeTab();
		return new DefaultTree();
	}

	/**
	 * Returns proper Add Managed Bean Dialog Title
	 * 
	 * @param testProjectType
	 * @return
	 */
	public static String getAddManagedBeanDialogTitle(ProjectType testProjectType) {
		String result;
		if (testProjectType.equals(ProjectType.JSF)) {
			result = "New Managed Bean";
		} else if (testProjectType.equals(ProjectType.JSF2)) {
			result = "Managed Bean...";
		} else {
			throw new IllegalArgumentException("Not supported TestProjectType " + testProjectType);
		}
		return result;
	}

	/**
	 * Returns proper Add Render Kit Dialog Title
	 * 
	 * @param testProjectType
	 * @return
	 */
	private static String getAddRenderKitDialogTitle(ProjectType testProjectType) {
		String result;
		if (testProjectType.equals(ProjectType.JSF)) {
			result = "Add Render Kit";
		} else if (testProjectType.equals(ProjectType.JSF2)) {
			result = "New Render Kit";
		} else {
			throw new IllegalArgumentException("Not supported TestProjectType " + testProjectType);
		}

		return result;

	}
}