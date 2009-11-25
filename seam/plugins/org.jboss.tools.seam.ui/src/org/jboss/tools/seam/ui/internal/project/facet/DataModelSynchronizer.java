/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 

package org.jboss.tools.seam.ui.internal.project.facet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;

/**
 * It is intended to collect all changes from Wizard Page and put it to 
 * IDataModel instance behind it.
 * @author eskimo
 *
 */
public class DataModelSynchronizer implements PropertyChangeListener {
	/**
	 * Target IDataModel instance
	 */
	protected IDataModel model;
	
	/**
	 * Map allows get particular IFieldEditor instance by its name. 
	 * It is used by register(IFieldEditor) method.
	 */
	Map<String,IFieldEditor> editors = new HashMap<String,IFieldEditor>();
	
	/**
	 * 
	 * @param model is a target IDataModel instance
	 */
	public DataModelSynchronizer(IDataModel model) {
		this.model = model;
	}

	/**
	 * This class implements PropertyChangeListener interface from Java Beans 
	 * API to receive property change events from registered IFieldEditors.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		model.setProperty(evt.getPropertyName(), evt.getNewValue());
	}

	/**
	 * 
	 * @param name - name of property from IDataModel instance
	 * @return IFieldEditor used to edit property with given name
	 */
	public IFieldEditor getNamedElement(String name) {
		return editors.get(name);
	}
	
	/**
	 * Registers a editor used to edit IDataModel property. 
	 * The name of editor and model property should be the same.
	 * @param editor - instance of IFiedEditor placed on IWizardPage
	 */
	public void register(IFieldEditor editor) {
		editors.put(editor.getName(), editor);
		model.setProperty(editor.getName(), editor.getValue());
		editor.addPropertyChangeListener(this);
	}
}
