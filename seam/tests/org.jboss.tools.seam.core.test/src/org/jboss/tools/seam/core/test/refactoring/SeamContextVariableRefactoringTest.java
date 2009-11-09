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
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.internal.core.refactoring.RenameSeamContextVariableProcessor;
import org.jboss.tools.test.util.JobUtils;

public class SeamContextVariableRefactoringTest extends SeamRefactoringTest {
	public SeamContextVariableRefactoringTest(){
		super("Seam Context Variable Refactoring Test");
	}
	
	private void renameContextVariable(ISeamProject seamProject, String fileName, String variableName, String newName, List<TestChangeStructure> changeList) throws CoreException{
		JobUtils.waitForIdle(2000);

		// Test before renaming
		for(TestChangeStructure changeStructure : changeList){
			IFile file = changeStructure.getProject().getFile(changeStructure.getFileName());
			String content = null;
			try {
				content = FileUtil.readStream(file);
			} catch (CoreException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}

			for(TestTextChange change : changeStructure.getTextChanges()){
				assertNotSame(change.getText(), content.substring(change.getOffset(), change.getOffset()+change.getLength()));
			}
		}

		IFile sourceFile = seamProject.getProject().getFile(fileName);

		// Rename Seam Context Variable
		RenameSeamContextVariableProcessor processor = new RenameSeamContextVariableProcessor(sourceFile, variableName);
		processor.setNewName(newName);
		processor.checkInitialConditions(new NullProgressMonitor());
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
		JobUtils.waitForIdle(2000);
		

		// Test results
		for(TestChangeStructure changeStructure : changeList){
			IFile file = changeStructure.getProject().getFile(changeStructure.getFileName());
			String content = null;
			content = FileUtil.readStream(file.getContents());
			for(TestTextChange change : changeStructure.getTextChanges()){
				assertEquals("There is unexpected change in resource - "+file.getName(),change.getText(), content.substring(change.getOffset(), change.getOffset()+change.getLength()));
			}
		}
	}
	
	public void testSeamContextVariable_Component_Rename() throws CoreException {
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

		renameContextVariable(seamEjbProject, "/WebContent/test.jsp", "test", "best", list);
	}
	
	public void testSeamContextVariable_Factory1_Rename() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(ejbProject.getProject(), "/ejbModule/org/domain/"+warProjectName+"/session/TestContextVariableFactory.java");
		TestTextChange change = new TestTextChange(464, 5, "\"bbc\"");
		structure.addTextChange(change);
		list.add(structure);
		
		structure = new TestChangeStructure(warProject, "/WebContent/factory.jsp");
		change = new TestTextChange(227, 3, "bbc");
		structure.addTextChange(change);
		list.add(structure);
		
		renameContextVariable(seamEjbProject, "/WebContent/factory.jsp", "abc", "bbc", list);
	}
	
	public void testSeamContextVariable_Factory2_Rename() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(ejbProject.getProject(), "/ejbModule/org/domain/"+warProjectName+"/session/TestContextVariableFactory.java");
		TestTextChange change = new TestTextChange(528, 7, "(\"ccc\")");
		structure.addTextChange(change);
		list.add(structure);
		
		structure = new TestChangeStructure(warProject, "/WebContent/factory.jsp");
		change = new TestTextChange(283, 3, "ccc");
		structure.addTextChange(change);
		list.add(structure);
		
		renameContextVariable(seamEjbProject, "/WebContent/factory.jsp", "cba", "ccc", list);
	}

	public void testSeamContextVariable_Out1_Rename() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(ejbProject.getProject(), "/ejbModule/org/domain/"+warProjectName+"/session/TestContextVariableOut.java");
		TestTextChange change = new TestTextChange(455, 5, "\"bbb\"");
		structure.addTextChange(change);
		list.add(structure);
		
		structure = new TestChangeStructure(warProject, "/WebContent/out.jsp");
		change = new TestTextChange(227, 3, "bbb");
		structure.addTextChange(change);
		list.add(structure);
		
		renameContextVariable(seamEjbProject, "/WebContent/out.jsp", "aaa", "bbb", list);
	}
	
	public void testSeamContextVariable_Out2_Rename() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(ejbProject.getProject(), "/ejbModule/org/domain/"+warProjectName+"/session/TestContextVariableOut.java");
		TestTextChange change = new TestTextChange(516, 5, "\"eee\"");
		structure.addTextChange(change);
		list.add(structure);
		
		structure = new TestChangeStructure(warProject, "/WebContent/out.jsp");
		change = new TestTextChange(283, 3, "eee");
		structure.addTextChange(change);
		list.add(structure);
		
		renameContextVariable(seamEjbProject, "/WebContent/out.jsp", "ddd", "eee", list);
	}

	public void testSeamContextVariable_DataModel1_Rename() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(ejbProject.getProject(), "/ejbModule/org/domain/"+warProjectName+"/session/TestContextVariableDataModel.java");
		TestTextChange change = new TestTextChange(483, 6, "\"dada\"");
		structure.addTextChange(change);
		list.add(structure);
		
		structure = new TestChangeStructure(warProject, "/WebContent/datamodel.jsp");
		change = new TestTextChange(227, 4, "dada");
		structure.addTextChange(change);
		list.add(structure);
		
		renameContextVariable(seamEjbProject, "/WebContent/datamodel.jsp", "data", "dada", list);
	}
	
	public void testSeamContextVariable_DataModel2_Rename() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(ejbProject.getProject(), "/ejbModule/org/domain/"+warProjectName+"/session/TestContextVariableDataModel.java");
		TestTextChange change = new TestTextChange(551, 7, "\"modal\"");
		structure.addTextChange(change);
		list.add(structure);
		
		structure = new TestChangeStructure(warProject, "/WebContent/datamodel.jsp");
		change = new TestTextChange(284, 5, "modal");
		structure.addTextChange(change);
		list.add(structure);
		
		renameContextVariable(seamEjbProject, "/WebContent/datamodel.jsp", "model", "modal", list);
	}
}
