/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.ui.itest;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameFieldProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameTypeProcessor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.batch.ui.participants.BatchArtifactRenameParticipant;
import org.jboss.tools.common.base.test.AbstractRefactorTest;
import org.jboss.tools.common.base.test.RenameParticipantTestUtil;
import org.jboss.tools.common.base.test.RenameParticipantTestUtil.TestChangeStructure;
import org.jboss.tools.common.base.test.RenameParticipantTestUtil.TestTextChange;

public class BatchRenameParticipantTest extends AbstractRefactorTest {
	private static final String PROJECT_NAME = "BatchTestProject"; //$NON-NLS-1$
	
	public IProject project = null;

	protected void setUp() {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				PROJECT_NAME);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}
	
	protected void tearDown() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}

	public BatchRenameParticipantTest() {
		super("Batch Rename Participants Test");
	}
	
	public void testBatchArtifactRename() throws CoreException, BadLocationException{
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(project, "/src/META-INF/batch-jobs/job-refactor.xml");
		TestTextChange change = new TestTextChange("renamableBatchlet", 17, "abcdmableBatchlet");
		structure.addTextChange(change);
		list.add(structure);

		IType type = RenameParticipantTestUtil.getJavaType(project, "batch.RenamableBatchlet");
		RenameTypeProcessor renameProcessor = new RenameTypeProcessor(type);

		RenameParticipantTestUtil.checkRenameParticipant(type, renameProcessor, new BatchArtifactRenameParticipant(), "abcdmableBatchlet", list);
	}

	public void testBatchPropertyRename() throws CoreException, BadLocationException{
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(project, "/src/META-INF/batch-jobs/job-refactor.xml");
		TestTextChange change = new TestTextChange("otherName", 9, "abcdeName");
		structure.addTextChange(change);
		list.add(structure);

		IField field = RenameParticipantTestUtil.getJavaField(project, "batch.RenamablePropertyBatchlet", "otherName");
		RenameFieldProcessor renameProcessor = new RenameFieldProcessor(field);

		RenameParticipantTestUtil.checkRenameParticipant(field, renameProcessor, new BatchArtifactRenameParticipant(), "abcdeName", list);
	}
	
	public void testClassRename() throws CoreException, BadLocationException{
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(project, "/src/META-INF/batch-jobs/job-refactor.xml");
		TestTextChange change = new TestTextChange("batch.SecondRenamableException", 30, "batch.AbcdefRenamableException");
		structure.addTextChange(change);
		list.add(structure);

		change = new TestTextChange("batch.SecondRenamableException", 30, "batch.AbcdefRenamableException");
		structure.addTextChange(change);

		IType type = RenameParticipantTestUtil.getJavaType(project, "batch.SecondRenamableException");
		RenameTypeProcessor renameProcessor = new RenameTypeProcessor(type);

		RenameParticipantTestUtil.checkRenameParticipant(type, renameProcessor, new BatchArtifactRenameParticipant(), "AbcdefRenamableException", list);
	}

	public void testClassRename2() throws CoreException, BadLocationException{
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(project, "/src/META-INF/batch-jobs/job-refactor.xml");
		TestTextChange change = new TestTextChange("batch.RenamableException", 24, "batch.RenamableExceptio2");
		structure.addTextChange(change);
		list.add(structure);

		IType type = RenameParticipantTestUtil.getJavaType(project, "batch.RenamableException");
		RenameTypeProcessor renameProcessor = new RenameTypeProcessor(type);

		RenameParticipantTestUtil.checkRenameParticipant(type, renameProcessor, new BatchArtifactRenameParticipant(), "RenamableExceptio2", list);
	}

}
