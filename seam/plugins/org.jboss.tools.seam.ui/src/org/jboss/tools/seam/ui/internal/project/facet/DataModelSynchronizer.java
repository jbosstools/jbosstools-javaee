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
package org.jboss.tools.seam.ui.internal.project.facet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;

public class DataModelSynchronizer implements PropertyChangeListener {
	
	IDataModel model;
	
	public DataModelSynchronizer(IDataModel model) {
		this.model = model;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		model.setProperty(evt.getPropertyName(), evt.getNewValue());
	}

	public IFieldEditor getNamedElement(String name) {
		return editors.get(name);
	}
	
	Map<String,IFieldEditor> editors = new HashMap<String,IFieldEditor>();
	
	public void register(IFieldEditor editor) {
		editors.put(editor.getName(), editor);
		model.setProperty(editor.getName(), editor.getValue());
		editor.addPropertyChangeListener(this);
	}

}
