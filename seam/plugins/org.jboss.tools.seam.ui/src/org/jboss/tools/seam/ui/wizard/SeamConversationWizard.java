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
package org.jboss.tools.seam.ui.wizard;

import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.INewWizard;

public class SeamConversationWizard extends SeamBaseWizard implements INewWizard {

	public SeamConversationWizard() {
		super(CREATE_SEAM_CONVERSATION);
		setWindowTitle("reate New Conversation");
		addPage(new SeamConversationVizardPage1());
	}

	public static final IUndoableOperation CREATE_SEAM_CONVERSATION = new SeamBaseOperation("Action creating operation"){

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			Map params = (Map)info.getAdapter(Map.class);
			
			return Status.OK_STATUS;
		}
		
	};

}
