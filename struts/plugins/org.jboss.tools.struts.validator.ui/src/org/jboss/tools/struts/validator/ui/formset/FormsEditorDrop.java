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
package org.jboss.tools.struts.validator.ui.formset;

import java.util.*;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.validator.ui.formset.model.FModel;
import org.jboss.tools.common.model.ui.dnd.IControlDragDropProvider;

public class FormsEditorDrop implements IControlDragDropProvider {
	TreeViewer treeViewer;

	public void setTreeViewer(TreeViewer viewer) {
		this.treeViewer = viewer;
	}
	
	public XModelObject getModelObjectForWidget(Widget widget) {
		return getObjectByFModel(MenuInvoker.getFModelByItem((TreeItem)widget));
	}

	public XModelObject getObjectByFModel(FModel f) {
		if(f == null) return null;
		XModelObject[] os = f.getModelObjects();
		return (os == null || os.length == 0) ? null : os[0];
	}

	public Control getControl() {
		return treeViewer.getControl();
	}

	public Widget[] getSelection() {
		return treeViewer.getTree().getSelection();
	}

	public Properties getDropProperties(int x, int y) {
		return new Properties();
	}
}
