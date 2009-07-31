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
package org.jboss.tools.jsf.ui.wizard.capabilities;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.jsf.project.capabilities.*;

public class CapabilityPerformersProvider extends LabelProvider implements ITreeContentProvider, ILabelProvider, IColorProvider {
	public Image IMAGE_ENABLED = EclipseResourceUtil.getImage("images/common/check.gif"); //$NON-NLS-1$
	public Image IMAGE_DISABLED = EclipseResourceUtil.getImage("images/common/uncheck.gif"); //$NON-NLS-1$
	protected IPerformerItem[] items = new IPerformerItem[0];
	
	public IPerformerItem[] getItems() {
		return items;
	}

	public void setItems(IPerformerItem[] items) {
		this.items = items;
	}

	public Object[] getChildren(Object parentElement) {
		return (parentElement instanceof IPerformerItem) ? ((IPerformerItem)parentElement).getChildren() : new Object[0];
	}

	public Object getParent(Object element) {
		return (element instanceof IPerformerItem) ? ((IPerformerItem)element).getParent() : null;
	}

	public boolean hasChildren(Object element) {
		return (element instanceof IPerformerItem) && ((IPerformerItem)element).getChildren().length > 0;
	}

	public Object[] getElements(Object inputElement) {
		return items;
	}

	public void dispose() {}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	public Image getImage(Object element) {
		return null;
//		if(!(element instanceof IPerformerItem)) return null;
//		IPerformerItem w = (IPerformerItem)element;
//		return (w.isSelected() && w.isEnabled()) ? IMAGE_ENABLED : IMAGE_DISABLED;
	}

	public Color getForeground(Object element) {
		if(!(element instanceof IPerformerItem)) return null;
		IPerformerItem w = (IPerformerItem)element;
		return (w.isEnabled()) ? null : Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
	}

	public Color getBackground(Object element) {
		return null;
	}
	
    public String getText(Object element) {
    	if(element instanceof IPerformerItem) {
    		return ((IPerformerItem)element).getDisplayName();
    	}
    	return super.getText(element);
    }
}
