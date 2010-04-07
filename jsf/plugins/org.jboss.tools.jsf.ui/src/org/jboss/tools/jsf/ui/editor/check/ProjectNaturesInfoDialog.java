/*******************************************************************************
 * Copyright (c) 2007-2010 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.editor.check;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.jboss.tools.common.reporting.ProblemReportingHelper;
import org.jboss.tools.jst.jsp.JspEditorPlugin;
import org.jboss.tools.jst.jsp.messages.JstUIMessages;

/**
 * 
 * @author yzhishko
 * 
 */

public abstract class ProjectNaturesInfoDialog extends MessageDialog {

	private Button button;
	private Link link;
	protected boolean isRemember = false;
	private static final String QUESTION = "Do not show this dialog again!"; //$NON-NLS-1$
	private static final String TITLE = "Missing Natures"; //$NON-NLS-1$
	protected IProject project;

	protected ProjectNaturesInfoDialog(IProject project, String fixButtonLabel) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				TITLE, null, "", INFORMATION, //$NON-NLS-1$
				new String[] { fixButtonLabel, "Skip" }, 0); //$NON-NLS-1$
		this.project = project;
		message = getMessageInfo();
	}

	@Override
	protected Control createCustomArea(Composite parent) {

		GridLayout gridLayout = (GridLayout) parent.getLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
		parent.setLayout(gridLayout);
		button = new Button(parent, SWT.CHECK);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		button.setLayoutData(gridData);
		button.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				isRemember = !isRemember;
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				isRemember = !isRemember;
			}
		});
		button.setText(QUESTION);
		link = new Link(parent, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.grabExcessHorizontalSpace = true;
		link.setLayoutData(gridData);
		link.setText("<A>" + JstUIMessages.DOCS_INFO_LINK_TEXT + "</A>"); //$NON-NLS-1$ //$NON-NLS-2$
		link.setToolTipText(JstUIMessages.DOCS_INFO_LINK);
		link.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				processLink(link);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				processLink(link);
			}

		});
		return parent;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
		if (buttonId == 0) {
			fixButtonPressed();
		}
		if (buttonId == 1) {
			skipButtonPressed();
		}
	}

	private void processLink(Link link) {
		BusyIndicator.showWhile(link.getDisplay(), new Runnable() {
			public void run() {
				URL theURL = null;
				try {
					theURL = new URL(JstUIMessages.DOCS_INFO_LINK);
				} catch (MalformedURLException e) {
					ProblemReportingHelper.reportProblem(
							JspEditorPlugin.PLUGIN_ID, e);
				}
				IWorkbenchBrowserSupport support = PlatformUI.getWorkbench()
						.getBrowserSupport();
				try {
					support.getExternalBrowser().openURL(theURL);
				} catch (PartInitException e) {
					ProblemReportingHelper.reportProblem(
							JspEditorPlugin.PLUGIN_ID, e);
				}
			}
		});
	}

	protected abstract String getMessageInfo();
	
	protected abstract void fixButtonPressed();
	
	protected abstract void skipButtonPressed();

}
