package org.jboss.tools.jsf.test.refactoring;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.internal.core.refactoring.resource.MoveResourcesProcessor;
import org.eclipse.ltk.internal.core.refactoring.resource.RenameResourceProcessor;
import org.jboss.tools.jsf.jsf2.refactoring.JSF2RenameParticipant;
import org.jboss.tools.jsf.jsf2.refactoring.JSf2MoveParticipant;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.tests.AbstractRefactorTest;

public class JSF2RefactoringTest extends AbstractRefactorTest  {
	static String projectName = "JSF2ComponentsValidator";
	static IProject project;
	
	public JSF2RefactoringTest(){
		super("Refactor JSF2 Composite Components Test");
	}
	
	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(projectName);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		JobUtils.waitForIdle(2000);
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

		TestChangeStructure structure = new TestChangeStructure(project.getProject(), "/WebContent/pages/inputname.xhtml");
		TestTextChange change = new TestTextChange(382, 5, "demo2");
		structure.addTextChange(change);
		list.add(structure);

		IFolder sourceFolder = project.getProject().getFolder("/WebContent/resources/demo");
		
		RenameResourceProcessor processor = new RenameResourceProcessor(sourceFolder);
		processor.setNewResourceName("demo2");
		
		JSF2RenameParticipant participant = new JSF2RenameParticipant();

		checkRename(processor, sourceFolder, "demo2", participant, list);
	}
	
	public void testMoveCompositeComponentFile() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(project.getProject(), "/WebContent/pages/inputname.xhtml");
		TestTextChange change = new TestTextChange(382, 3, "new");
		structure.addTextChange(change);
		list.add(structure);

		IFile sourceFile = project.getProject().getFile("/WebContent/resources/demo2/input2.xhtml");
		IFolder destinationFolder = project.getProject().getFolder("/WebContent/resources/new");
		
		MoveResourcesProcessor processor = new MoveResourcesProcessor(new IResource[]{sourceFile});
		processor.setDestination(destinationFolder);
		
		JSf2MoveParticipant participant = new JSf2MoveParticipant();

		checkMove(processor, sourceFile, destinationFolder, participant, list);
	}
	
	public void testMoveCompositeComponentFolder() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(project.getProject(), "/WebContent/pages/inputname.xhtml");
		TestTextChange change = new TestTextChange(382, 9, "demo2/new");
		structure.addTextChange(change);
		list.add(structure);

		IFolder sourceFolder = project.getProject().getFolder("/WebContent/resources/new");
		IFolder destinationFolder = project.getProject().getFolder("/WebContent/resources/demo2");
		
		MoveResourcesProcessor processor = new MoveResourcesProcessor(new IResource[]{sourceFolder});
		processor.setDestination(destinationFolder);
		
		JSf2MoveParticipant participant = new JSf2MoveParticipant();

		checkMove(processor, sourceFolder, destinationFolder, participant, list);
	}

}
