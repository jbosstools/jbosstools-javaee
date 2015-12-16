/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.batch.ui.internal.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.batch.internal.core.impl.PreferredPackageManager;
import org.jboss.tools.foundation.checkup.TestEnvironmentDetector;

public class NewBatchArtifactDialog extends WizardDialog {

	public NewBatchArtifactDialog(Shell parentShell) {
		super(parentShell, new NewBatchArtifactWizard());
	}
	
	public NewBatchArtifactWizard getBatchWizard(){
		return (NewBatchArtifactWizard)getWizard();
	}
	
	public NewBatchArtifactWizardPage getBatchPage(){
		return getBatchWizard().getPage();
	}
	
	public void setTypes(List<BatchArtifactType> types){
		getBatchWizard().setTypes(types);
	}
	
	public void init(IWorkbench workbench, IStructuredSelection currentSelection){
		getBatchWizard().init(workbench, currentSelection);
	}
	
	public int open(IBatchProject batchProject, BatchArtifactType artifact, boolean artifactCanBeModified, String typeName){
		ArrayList<BatchArtifactType> list = new ArrayList<BatchArtifactType>();
		list.add(artifact);
		return open(batchProject, null, false, list, artifactCanBeModified, typeName);
	}
	
	public int open(IBatchProject batchProject, String artifactName, boolean artifactNameCanBeModified, List<BatchArtifactType> artifacts, boolean artifactCanBeModified, String typeName){
		create();
		if(artifactName != null){
			getBatchPage().setArtifactName(artifactName, artifactNameCanBeModified);
		}
		if(typeName != null){
			getBatchPage().setTypeName(typeName, true);
		}
		
		if(artifacts != null && artifacts.size() > 0){
			getBatchPage().setArtifact(artifacts.get(0), artifactCanBeModified);
			if(batchProject != null) {
				IPackageFragment pack = PreferredPackageManager.getPackageSuggestion(batchProject.getProject(), artifacts);
				if(pack != null) {
					getBatchPage().setPackageFragment(pack, true);
				}
			}
		}
		
		if(TestEnvironmentDetector.isTestEnvironment()){
			setBlockOnOpen(false);
		}
		
		int code = open();
		
		if(TestEnvironmentDetector.isTestEnvironment()){
			getBatchWizard().performFinish();
			close();
		}
		return code;
	}

}
