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

import java.io.File;
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

		public File getBeanFile(Map<String, Object> vars)  {
			return new File(getSeamFolder(vars),"src/ActionJavaBean.java");
		}
		
		public File getTestClassFile(Map<String, Object> vars) {
			return new File(getSeamFolder(vars),"test/ActionTest.java");
		}
		
		public File getTestngXmlFile(Map<String, Object> vars) {
			return new File(getSeamFolder(vars),"test/testng.xml");
		}
		
		public File getPageXhtml(Map<String, Object> vars) {
			return new File(getSeamFolder(vars),"view/action.xhtml");
		}

	};

}
