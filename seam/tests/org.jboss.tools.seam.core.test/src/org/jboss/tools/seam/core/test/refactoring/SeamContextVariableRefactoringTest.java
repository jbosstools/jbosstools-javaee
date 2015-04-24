package org.jboss.tools.seam.core.test.refactoring;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.jboss.tools.common.base.test.RenameParticipantTestUtil.TestChangeStructure;
import org.jboss.tools.common.base.test.RenameParticipantTestUtil.TestTextChange;
import org.jboss.tools.seam.internal.core.refactoring.RenameSeamContextVariableProcessor;

public class SeamContextVariableRefactoringTest extends SeamRefactoringTest {
	public SeamContextVariableRefactoringTest(){
		super("Seam Context Variable Refactoring Test");
	}
	
	public void testSeamContextVariable_Component_Rename() throws CoreException, BadLocationException {
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
		change = new TestTextChange("@In(\"test\")"/*420*/, 11, "@In(\"best\")");
		structure.addTextChange(change);
		
		change = new TestTextChange(389, 8, "(\"best\")");
		structure.addTextChange(change);
		
		change = new TestTextChange("@Factory"/*455*/, 16, "@Factory(\"best\")");
		structure.addTextChange(change);
		
		change = new TestTextChange(521, 8, "(\"best\")");
		structure.addTextChange(change);
		
		change = new TestTextChange(573, 4, "best");
		structure.addTextChange(change);
		
		list.add(structure);

		structure = new TestChangeStructure(ejbProject, "/ejbModule/seam.properties");
		change = new TestTextChange("test", 4, "best");
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(warProject, "/WebContent/test.xhtml");
		change = new TestTextChange("test", 4, "best");
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(warProject, "/WebContent/test.jsp");
		change = new TestTextChange("test", 4, "best");
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(warProject, "/src/test.properties");
		change = new TestTextChange("test", 4, "best");
		structure.addTextChange(change);
		list.add(structure);
		
		IFile sourceFile = seamEjbProject.getProject().getFile("/WebContent/test.jsp");
		
		RenameSeamContextVariableProcessor processor = new RenameSeamContextVariableProcessor(sourceFile, "test");
		processor.setNewName("best");

		checkRename(processor, list);
	}
	
	public void testSeamContextVariable_Factory1_Rename() throws CoreException, BadLocationException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(ejbProject.getProject(), "/ejbModule/org/domain/"+warProjectName+"/session/TestContextVariableFactory.java");
		TestTextChange change = new TestTextChange("@Factory(\"abc\")", 15, "@Factory(\"bbc\")");
		structure.addTextChange(change);
		list.add(structure);
		
		structure = new TestChangeStructure(warProject, "/WebContent/factory.jsp");
		change = new TestTextChange(227, 3, "bbc");
		structure.addTextChange(change);
		list.add(structure);
		
		IFile sourceFile = seamEjbProject.getProject().getFile("/WebContent/factory.jsp");
		
		RenameSeamContextVariableProcessor processor = new RenameSeamContextVariableProcessor(sourceFile, "abc");
		processor.setNewName("bbc");
		
		checkRename(processor, list);
	}
	
	public void testSeamContextVariable_Factory2_Rename() throws CoreException, BadLocationException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(ejbProject.getProject(), "/ejbModule/org/domain/"+warProjectName+"/session/TestContextVariableFactory.java");
		TestTextChange change = new TestTextChange(528, 7, "(\"ccc\")");
		structure.addTextChange(change);
		list.add(structure);
		
		structure = new TestChangeStructure(warProject, "/WebContent/factory.jsp");
		change = new TestTextChange(283, 3, "ccc");
		structure.addTextChange(change);
		list.add(structure);
		
		IFile sourceFile = seamEjbProject.getProject().getFile("/WebContent/factory.jsp");
		
		RenameSeamContextVariableProcessor processor = new RenameSeamContextVariableProcessor(sourceFile, "cba");
		processor.setNewName("ccc");
		
		checkRename(processor, list);
	}

	public void testSeamContextVariable_Out1_Rename() throws CoreException, BadLocationException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(ejbProject.getProject(), "/ejbModule/org/domain/"+warProjectName+"/session/TestContextVariableOut.java");
		TestTextChange change = new TestTextChange("@Out(\"aaa\")", 11, "@Out(\"bbb\")");
		structure.addTextChange(change);
		list.add(structure);
		
		structure = new TestChangeStructure(warProject, "/WebContent/out.jsp");
		change = new TestTextChange(227, 3, "bbb");
		structure.addTextChange(change);
		list.add(structure);

		IFile sourceFile = seamEjbProject.getProject().getFile("/WebContent/out.jsp");
		
		RenameSeamContextVariableProcessor processor = new RenameSeamContextVariableProcessor(sourceFile, "aaa");
		processor.setNewName("bbb");
		
		checkRename(processor, list);
	}
	
	public void testSeamContextVariable_Out2_Rename() throws CoreException, BadLocationException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(ejbProject.getProject(), "/ejbModule/org/domain/"+warProjectName+"/session/TestContextVariableOut.java");
		TestTextChange change = new TestTextChange(515, 7, "(\"eee\")");
		structure.addTextChange(change);
		list.add(structure);
		
		structure = new TestChangeStructure(warProject, "/WebContent/out.jsp");
		change = new TestTextChange(283, 3, "eee");
		structure.addTextChange(change);
		list.add(structure);

		IFile sourceFile = seamEjbProject.getProject().getFile("/WebContent/out.jsp");
		
		RenameSeamContextVariableProcessor processor = new RenameSeamContextVariableProcessor(sourceFile, "ddd");
		processor.setNewName("eee");
		
		checkRename(processor, list);
	}

	public void testSeamContextVariable_DataModel1_Rename() throws CoreException, BadLocationException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(ejbProject.getProject(), "/ejbModule/org/domain/"+warProjectName+"/session/TestContextVariableDataModel.java");
		TestTextChange change = new TestTextChange("@DataModel(\"data\")", 18, "@DataModel(\"dada\")");
		structure.addTextChange(change);
		list.add(structure);
		
		structure = new TestChangeStructure(warProject, "/WebContent/datamodel.jsp");
		change = new TestTextChange(227, 4, "dada");
		structure.addTextChange(change);
		list.add(structure);
		
		IFile sourceFile = seamEjbProject.getProject().getFile("/WebContent/datamodel.jsp");
		
		RenameSeamContextVariableProcessor processor = new RenameSeamContextVariableProcessor(sourceFile, "data");
		processor.setNewName("dada");
		
		checkRename(processor, list);
	}
	
	public void testSeamContextVariable_DataModel2_Rename() throws CoreException, BadLocationException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(ejbProject.getProject(), "/ejbModule/org/domain/"+warProjectName+"/session/TestContextVariableDataModel.java");
		TestTextChange change = new TestTextChange(550, 9, "(\"modal\")");
		structure.addTextChange(change);
		list.add(structure);
		
		structure = new TestChangeStructure(warProject, "/WebContent/datamodel.jsp");
		change = new TestTextChange(284, 5, "modal");
		structure.addTextChange(change);
		list.add(structure);
		
		IFile sourceFile = seamEjbProject.getProject().getFile("/WebContent/datamodel.jsp");
		
		RenameSeamContextVariableProcessor processor = new RenameSeamContextVariableProcessor(sourceFile, "model");
		processor.setNewName("modal");
		
		checkRename(processor, list);
	}
}
