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
		
		IFile sourceFile = seamEjbProject.getProject().getFile("/WebContent/test.jsp");
		
		RenameSeamContextVariableProcessor processor = new RenameSeamContextVariableProcessor(sourceFile, "test");
		processor.setNewName("best");

		checkRename(processor, list);
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
		
		IFile sourceFile = seamEjbProject.getProject().getFile("/WebContent/factory.jsp");
		
		RenameSeamContextVariableProcessor processor = new RenameSeamContextVariableProcessor(sourceFile, "abc");
		processor.setNewName("bbc");
		
		checkRename(processor, list);
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
		
		IFile sourceFile = seamEjbProject.getProject().getFile("/WebContent/factory.jsp");
		
		RenameSeamContextVariableProcessor processor = new RenameSeamContextVariableProcessor(sourceFile, "cba");
		processor.setNewName("ccc");
		
		checkRename(processor, list);
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

		IFile sourceFile = seamEjbProject.getProject().getFile("/WebContent/out.jsp");
		
		RenameSeamContextVariableProcessor processor = new RenameSeamContextVariableProcessor(sourceFile, "aaa");
		processor.setNewName("bbb");
		
		checkRename(processor, list);
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

		IFile sourceFile = seamEjbProject.getProject().getFile("/WebContent/out.jsp");
		
		RenameSeamContextVariableProcessor processor = new RenameSeamContextVariableProcessor(sourceFile, "ddd");
		processor.setNewName("eee");
		
		checkRename(processor, list);
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
		
		IFile sourceFile = seamEjbProject.getProject().getFile("/WebContent/datamodel.jsp");
		
		RenameSeamContextVariableProcessor processor = new RenameSeamContextVariableProcessor(sourceFile, "data");
		processor.setNewName("dada");
		
		checkRename(processor, list);
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
		
		IFile sourceFile = seamEjbProject.getProject().getFile("/WebContent/datamodel.jsp");
		
		RenameSeamContextVariableProcessor processor = new RenameSeamContextVariableProcessor(sourceFile, "model");
		processor.setNewName("modal");
		
		checkRename(processor, list);
	}
}
