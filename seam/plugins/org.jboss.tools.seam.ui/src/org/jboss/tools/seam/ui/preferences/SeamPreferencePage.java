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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.ui.widget.editor.SeamRuntimeListFieldEditor;

/**
 * Seam preference page that allows editing list of available Seam Runtimes:
 * <ul>
 * <li>define new </li>
 * <li>change exists</li>
 * <li>remove</li>
 * <li>set default ones</li>
 * </ul>
 * 
 * @author eskimo
 */
public class SeamPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public SeamPreferencePage() {
		super();
		noDefaultAndApplyButton();
	}

	/**
	 * Seam Preferences page ID
	 */
	public static final String SEAM_PREFERENCES_ID = "org.jboss.tools.common.model.ui.seam";

	private static final int COLUMNS = 3;

	SeamRuntimeListFieldEditor seamRuntimes = new SeamRuntimeListFieldEditor(
			"rtlist", SeamPreferencesMessages.SEAM_PREFERENCE_PAGE_SEAM_RUNTIMES, new ArrayList<SeamRuntime>(Arrays.asList(SeamRuntimeManager.getInstance().getRuntimes()))); //$NON-NLS-1$

	/**
	 * Create contents of Seam preferences page. SeamRuntime list editor is
	 * created
	 * 
	 * @return Control
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout(1, false);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		root.setLayoutData(layoutData);
		root.setLayout(layout);
		
		Group seamRuntimeGroup = createGroup(root, 1);
		GridLayout gl = new GridLayout();
		seamRuntimeGroup.setLayout(gl);
		seamRuntimeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		seamRuntimeGroup.setText(SeamPreferencesMessages.SEAM_PREFERENCE_PAGE_SEAM_RUNTIMES);
		
		Label seamRuntimeDescription = new Label(seamRuntimeGroup, SWT.WRAP);
		seamRuntimeDescription.setText(SeamPreferencesMessages.SEAM_PREFERENCE_PAGE_DESCRIPTION);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL) ;
		gridData.grabExcessHorizontalSpace = true ;
		gridData.widthHint = 200;
		seamRuntimeDescription.setLayoutData(gridData);
		
		Composite container = new Composite(seamRuntimeGroup, SWT.NONE);
		gl = new GridLayout(COLUMNS, false);
		container.setLayout(gl);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		container.setLayoutData(gd);
		
		seamRuntimes.doFillIntoGrid(container);
		seamRuntimes.setPreferencePage(this);
		return root;
	}
	
	private Group createGroup(Composite composite, int column) {
		GridLayout layout;
		Group group = new Group(composite, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		group.setLayoutData(gd);
		layout = new GridLayout(column, false);
		group.setLayout(layout);
		return group;
	}
	/**
	 * Inherited from IWorkbenchPreferencePage
	 * 
	 * @param workbench
	 *            {@link IWorkbench}
	 * 
	 */
	public void init(IWorkbench workbench) {
	}

	/**
	 * Save SeamRuntime list
	 */
	@Override
	protected void performApply() {
		List<SeamRuntime> defaultRuntimes = seamRuntimes.getDefaultSeamRuntimes();
		// reset all default runtimes 
		for (SeamRuntime seamRuntime : SeamRuntimeManager.getInstance().getRuntimes()) {
			seamRuntime.setDefault(false);
		}
		// set deafult runtimes
		for (SeamRuntime seamRuntime : defaultRuntimes) {
			seamRuntime.setDefault(true);
		}
		seamRuntimes.getDefaultSeamRuntimes().clear();
		SeamRuntimeManager.getInstance().save();
	}

	/**
	 * Restore original preferences values
	 */
	@Override
	protected void performDefaults() {
		setValid(true);
		setMessage(null);
		performApply();
	}

	/**
	 * See {@link PreferencePage} for details
	 * 
	 * @return Boolean
	 */
	@Override
	public boolean performOk() {
		performApply();
		return super.performOk();
	}
}