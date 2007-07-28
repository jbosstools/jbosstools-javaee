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

package org.jboss.tools.seam.ui.preferences;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamFacetPreference;
import org.jboss.tools.seam.ui.internal.project.facet.IValidator;
import org.jboss.tools.seam.ui.internal.project.facet.ValidatorFactory;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.SwtFieldEditorFactory;

/**
 * @author eskimo
 *
 */
public class SeamPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage, PropertyChangeListener {
	
	/**
	 * 
	 */
	IFieldEditor editor 
		= SwtFieldEditorFactory.INSTANCE.createBrowseFolderEditor(
				"seam.home.folder", "Seam Home Folder:", 
				SeamFacetPreference.getStringPreference(SeamFacetPreference.SEAM_HOME_FOLDER));
	
	/**
	 * 
	 */
	public SeamPreferencePage() {
	}

	/**
	 * @param title
	 */
	public SeamPreferencePage(String title) {
		super(title);
	}

	/**
	 * @param title
	 * @param image
	 */
	public SeamPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}
	
	/**
	 * 
	 */
	protected Control createContents(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(3,false);
		root.setLayout(gl);	
		editor.doFillIntoGrid(root);
		editor.addPropertyChangeListener(this);
		return root;
	}

	/**
	 * 
	 */
	public void init(IWorkbench workbench) {
	}

	/**
	 * 
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		Map errors 
			= ValidatorFactory.JBOSS_SEAM_HOME_FOLDER_VALIDATOR.validate(editor.getValue(), null);
		if(errors.size()>0) {
			setValid(false);
			setMessage(errors.get(errors.keySet().iterator().next()).toString(), IMessageProvider.ERROR);
		} else {
			setMessage(null);
			setValid(true);
		}
	}
	
	/**
	 * 
	 */
	@Override
	protected void performApply() {
		SeamCorePlugin.getDefault().getPluginPreferences().setValue(
				SeamFacetPreference.SEAM_HOME_FOLDER, editor.getValueAsString());
	}

	/**
	 * 
	 */
	@Override
	protected void performDefaults() {
		editor.removePropertyChangeListener(this);
		editor.setValue(
				SeamCorePlugin.getDefault().getPluginPreferences().getDefaultString(
						SeamFacetPreference.SEAM_HOME_FOLDER));
		editor.addPropertyChangeListener(this);
		setValid(true);
		setMessage(null);
		performApply();
	}
}
