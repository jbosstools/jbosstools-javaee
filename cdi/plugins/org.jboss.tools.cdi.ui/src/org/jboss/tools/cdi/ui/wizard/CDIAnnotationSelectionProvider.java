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

package org.jboss.tools.cdi.ui.wizard;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.cdi.ui.wizard.SelectCDIAnnotationDialog.CDIAnnotationWrapper;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.ui.widget.editor.ListFieldEditor.ListFieldEditorProvider;

public abstract class CDIAnnotationSelectionProvider implements ListFieldEditorProvider<ICDIAnnotation> {
	protected ICDIProject project;
	protected IFieldEditor editor;
	
	public CDIAnnotationSelectionProvider() {}
	
	public void setEditorField(IFieldEditor editor) {
		this.editor = editor;
	}

	public void setProject(ICDIProject project) {
		this.project = project;
	}

	public ICDIProject getProject() {
		return project;
	}

	public FilteredItemsSelectionDialog createSelectionDialog() {
		Shell shell = CDIUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		SelectCDIAnnotationDialog dialog = new SelectCDIAnnotationDialog(shell);
		dialog.setTitle(getDialogTitle());
		dialog.setProvider(this);
		return dialog;
	}

	protected abstract String getDialogTitle();
	
	public ICDIAnnotation getSelected(Object selected) {
		if(selected instanceof CDIAnnotationWrapper) {
			return ((CDIAnnotationWrapper)selected).getComponent();
		} else if(selected instanceof ICDIAnnotation) {
			return (ICDIAnnotation)selected;
		}
		return null;
	}

	public ILabelProvider createLabelProvider() {
		return new CDIAnnotationLabelProvider();
	}
	
}
