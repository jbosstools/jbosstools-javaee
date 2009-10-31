package org.jboss.tools.seam.core.test.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.internal.core.refactoring.RenameComponentProcessor;
import org.jboss.tools.test.util.JobUtils;

public class SeamComponentRefactoringTest extends SeamRefactoringTest {
	

	public SeamComponentRefactoringTest(){
		super("Seam Component Refactoring Test");
	}

	

	public void testSeamComponentRename() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(ejbProject.getProject(), "/ejbModule/org/domain/"+warProjectName+"/session/TestComponent.java");
		TestTextChange change = new TestTextChange(89, 6, "\"best\"");
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(warProject, "/WebContent/WEB-INF/components.xml");
		change = new TestTextChange(1106, 4, "best");
		structure.addTextChange(change);
		
		change = new TestTextChange(1934, 4, "best");
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(ejbProject, "/ejbModule/org/domain/"+warProjectName+"/session/TestSeamComponent.java");
		change = new TestTextChange(420, 11, "@In(\"best\")");
		structure.addTextChange(change);
		
		change = new TestTextChange(389, 8, "(\"best\")");
		structure.addTextChange(change);
		
		change = new TestTextChange(455, 16, "@Factory(\"best\")");
		structure.addTextChange(change);
		
		change = new TestTextChange(529, 8, "(\"best\")");
		structure.addTextChange(change);
		
		change = new TestTextChange(589, 4, "best");
		structure.addTextChange(change);
		
		list.add(structure);

		structure = new TestChangeStructure(ejbProject, "/ejbModule/seam.properties");
		change = new TestTextChange(0, 4, "best");
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(warProject, "/WebContent/test.xhtml");
		change = new TestTextChange(1088, 4, "best");
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(warProject, "/WebContent/test.jsp");
		change = new TestTextChange(227, 4, "best");
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(warProject, "/src/test.properties");
		change = new TestTextChange(29, 4, "best");
		structure.addTextChange(change);
		list.add(structure);

		renameComponent(seamEjbProject, "test", "best", list, false);
	}
	
	public void testRemaningMailSessionDeclarationInComponentsXml_JBIDE4447() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(warProject, "/WebContent/WEB-INF/components.xml");
		TestTextChange change = new TestTextChange(2345, 41, "name=\"org.jboss.seam.mail.newMailSession\"");
		structure.addTextChange(change);
		
		list.add(structure);

		renameComponent(seamWarProject, "org.jboss.seam.mail.mailSession", "org.jboss.seam.mail.newMailSession", list, true);
	}

	private void renameComponent(ISeamProject seamProject, String componentName, String newName, List<TestChangeStructure> changeList, boolean fromJar) throws CoreException{
		JobUtils.waitForIdle();

		// Test before renaming
		ISeamComponent component = seamProject.getComponent(componentName);
		assertNotNull("Can't load component " + componentName, component);

		// This line fail the test testJBIDE4447
		// assertNotNull("Component " + component.getName() + " does not have java declaration.", component.getJavaDeclaration());

		// Rename Seam Component
		RenameComponentProcessor processor = new RenameComponentProcessor(component);
		processor.setNewName(newName);
		processor.checkFinalConditions(new NullProgressMonitor(), null);
		CompositeChange rootChange = (CompositeChange)processor.createChange(new NullProgressMonitor());
		
		assertEquals("There is unexpected number of changes",changeList.size(), rootChange.getChildren().length);

		for(int i = 0; i < rootChange.getChildren().length;i++){
			TextFileChange fileChange = (TextFileChange)rootChange.getChildren()[i];

			MultiTextEdit edit = (MultiTextEdit)fileChange.getEdit();
			
			TestChangeStructure change = findChange(changeList, fileChange.getFile());
			if(change != null){
				assertEquals(change.size(), edit.getChildrenSize());
			}
		}

		rootChange.perform(new NullProgressMonitor());
		JobUtils.waitForIdle();
		// Test results
		//if(!fromJar)
			//assertNull("There is unexpected component in seam project: " + componentName, seamProject.getComponent(componentName));
		assertNotNull("Can't load component " + newName, seamProject.getComponent(newName));
		for(TestChangeStructure changeStructure : changeList){
			IFile file = changeStructure.getProject().getFile(changeStructure.getFileName());
			String content = null;
			content = FileUtil.readStream(file.getContents());
			//System.out.println("File - "+file.getName()+" offset - "+changeStructure.getOffset()+" expected - ["+changeStructure.getText()+"] actual - ["+content.substring(changeStructure.getOffset(), changeStructure.getOffset()+changeStructure.getLength())+"]");
			for(TestTextChange change : changeStructure.getTextChanges()){
				assertEquals("There is unexpected change in resource - "+file.getName(),change.getText(), content.substring(change.getOffset(), change.getOffset()+change.getLength()));
			}
		}
	}
	
	

	
}