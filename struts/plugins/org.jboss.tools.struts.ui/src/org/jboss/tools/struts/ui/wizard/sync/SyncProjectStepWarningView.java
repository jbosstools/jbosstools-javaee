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
package org.jboss.tools.struts.ui.wizard.sync;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.model.ui.wizards.special.AbstractSpecialWizardStep;

public class SyncProjectStepWarningView extends AbstractSpecialWizardStep {
	protected Label label;

	public Control createControl(Composite parent) {
		label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL)); 
		return label;
	}

	public void update() {
		if(label == null || label.isDisposed()) return;
		label.setText("" + support.getMessage(id));
		label.pack();
	}
		public void dispose() {
			super.dispose();
		}

	public Point getMaximumSize() {
		return null;
	}

	public Point getMinimumSize() {
		return null;
	}

}
