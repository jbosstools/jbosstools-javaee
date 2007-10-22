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

package org.jboss.tools.seam.ui.preferences;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.ui.widget.editor.SeamRuntimeListFieldEditor;

/**
 * @author eskimo
 *
 */
public class SeamPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage, PropertyChangeListener {
	
	/**
	 * 
	 */
	SeamRuntimeListFieldEditor seamRuntimes
		= new SeamRuntimeListFieldEditor("rtlist",SeamPreferencesMessages.SEAM_PREFERENCE_PAGE_SEAM_RUNTIMES,new ArrayList<SeamRuntime>(Arrays.asList(SeamRuntimeManager.getInstance().getRuntimes()))); //$NON-NLS-1$
	
	SeamRuntime initialDefault;

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
	@Override
	protected Control createContents(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(3,false);
		root.setLayout(gl);	
		seamRuntimes.doFillIntoGrid(root);

		initialDefault = SeamRuntimeManager.getInstance().getDefaultRuntime();
		
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
	}
	
	/**
	 * 
	 */
	@Override
	protected void performApply() {
		for (SeamRuntime rt : seamRuntimes.getAddedSeamRuntimes()) {
			SeamRuntimeManager.getInstance().addRuntime(rt);
		}
		seamRuntimes.getAddedSeamRuntimes().clear();
		for (SeamRuntime rt : seamRuntimes.getRemoved()) {
			SeamRuntimeManager.getInstance().removeRuntime(rt);
		}
		seamRuntimes.getRemoved().clear();
		if(initialDefault != null && seamRuntimes.getDefaultSeamRuntime() != initialDefault) {
			initialDefault.setDefault(false);
		}
		seamRuntimes.getDefaultSeamRuntime().setDefault(true);
		SeamRuntimeManager.getInstance().save();
	}

	/**
	 * 
	 */
	@Override
	protected void performDefaults() {
		setValid(true);
		setMessage(null);
		performApply();
	}

	@Override
	public boolean performOk() {
		performApply();
		return super.performOk();
	}
}
