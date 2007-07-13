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
package org.jboss.tools.seam.ui.widget.editor;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;

public class TaggedComboFieldEditor extends CompositeEditor implements ITaggedFieldEditor, PropertyChangeListener{

	List values = null;
	
	boolean floatStyle = true;
	ComboFieldEditor combo = null;
	
	public TaggedComboFieldEditor(String name, String label, List values,Object defaultValue, boolean floatStyle) {
		super(name, label, defaultValue);
		this.values = Collections.unmodifiableList(values);
		this.floatStyle = floatStyle;
		combo = new ComboFieldEditor(name,label,values,defaultValue.toString(),floatStyle);
		addFieldEditors(new IFieldEditor[]{new LabelFieldEditor(name,label),
				combo});
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.widget.editor.ITaggedFieldEditor#getTags()
	 */
	public String[] getTags() {
		return ((Combo)getEditorControls()[1]).getItems();
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.widget.editor.ITaggedFieldEditor#setTags(java.lang.String[])
	 */
	public void setTags(String[] tags) {
		combo.setTags(tags);
	}

}
