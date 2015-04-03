/*******************************************************************************
 * Copyright (c) 2007-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.test.refactoring;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.internal.core.refactoring.resource.MoveResourcesProcessor;
import org.eclipse.ltk.internal.core.refactoring.resource.RenameResourceProcessor;
import org.jboss.tools.common.base.test.AbstractRefactorTest;
import org.jboss.tools.common.base.test.RenameParticipantTestUtil.TestChangeStructure;
import org.jboss.tools.common.base.test.RenameParticipantTestUtil.TestTextChange;
import org.jboss.tools.jsf.jsf2.refactoring.JSF2RenameParticipant;
import org.jboss.tools.jsf.jsf2.refactoring.JSf2MoveParticipant;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class JSF2RefactoringTest extends AbstractRefactorTest  {
	static String PROJECT_NAME = "JSF2ComponentsValidator";
	IProject project;
	
	public JSF2RefactoringTest(){
		super("Refactor JSF2 Composite Components Test");
	}

	@Override
	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
	}

	public void testRenameCompositeComponentFile() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(project.getProject(), "/WebContent/pages/inputname.xhtml");
		TestTextChange change = new TestTextChange(776, 6, "input2");
		structure.addTextChange(change);
		list.add(structure);

		IFile sourceFile = project.getProject().getFile("/WebContent/resources/demo/input.xhtml");

		RenameResourceProcessor processor = new RenameResourceProcessor(sourceFile);
		processor.setNewResourceName("input2.xhtml");

		JSF2RenameParticipant participant = new JSF2RenameParticipant();

		checkRename(processor, sourceFile, "input2.xhtml", participant, list);
	}

	public void testRenameCompositeComponentFolder() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(project.getProject(), "/WebContent/pages/inputtype.xhtml");
		TestTextChange change = new TestTextChange(382, 5, "type2");
		structure.addTextChange(change);
		list.add(structure);

		IFolder sourceFolder = project.getProject().getFolder("/WebContent/resources/type");

		RenameResourceProcessor processor = new RenameResourceProcessor(sourceFolder);
		processor.setNewResourceName("type2");

		JSF2RenameParticipant participant = new JSF2RenameParticipant();

		checkRename(processor, sourceFolder, "type2", participant, list);
	}

	public void testMoveCompositeComponentFile() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(project.getProject(), "/WebContent/pages/inputdata.xhtml");
		TestTextChange change = new TestTextChange(382, 3, "new");
		structure.addTextChange(change);
		list.add(structure);

		IFile sourceFile = project.getProject().getFile("/WebContent/resources/data/data.xhtml");
		IFolder destinationFolder = project.getProject().getFolder("/WebContent/resources/new");
		
		MoveResourcesProcessor processor = new MoveResourcesProcessor(new IResource[]{sourceFile});
		processor.setDestination(destinationFolder);
		
		JSf2MoveParticipant participant = new JSf2MoveParticipant();

		checkMove(processor, sourceFile, destinationFolder, participant, list);
	}
	
	public void testMoveCompositeComponentFolder() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(project.getProject(), "/WebContent/pages/inputnmbr.xhtml");
		TestTextChange change = new TestTextChange(382, 8, "new/nmbr");
		structure.addTextChange(change);
		list.add(structure);

		IFolder sourceFolder = project.getProject().getFolder("/WebContent/resources/nmbr");
		IFolder destinationFolder = project.getProject().getFolder("/WebContent/resources/new");
		
		MoveResourcesProcessor processor = new MoveResourcesProcessor(new IResource[]{sourceFolder});
		processor.setDestination(destinationFolder);
		
		JSf2MoveParticipant participant = new JSf2MoveParticipant();

		checkMove(processor, sourceFolder, destinationFolder, participant, list);
	}
}