package org.jboss.tools.seam.core.test.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.refactoring.RenameComponentProcessor;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;

import junit.framework.TestCase;

public class SeamComponentRefactoringTest extends TestCase {
	static String warProjectName = "Test1";
	static String earProjectName = "Test1-ear";
	static String ejbProjectName = "Test1-ejb";
	static IProject warProject;
	static IProject earProject;
	static IProject ejbProject;
	static ISeamProject seamWarProject;
	static ISeamProject seamEjbProject;
	
	public SeamComponentRefactoringTest(){
		super("Seam Component Refactoring Test");
	}
	
	protected void setUp() throws Exception {
		if(warProject==null) {
			warProject = ProjectImportTestSetup.loadProject(warProjectName);
		}
		if(seamWarProject==null) {
			seamWarProject = loadSeamProject(warProject);
		}
		
		if(earProject==null) {
			earProject = ProjectImportTestSetup.loadProject(earProjectName);
		}
		
		if(ejbProject==null) {
			ejbProject = ProjectImportTestSetup.loadProject(ejbProjectName);
		}
		if(seamEjbProject==null) {
			seamEjbProject = loadSeamProject(ejbProject);
		}
	}
	
	private ISeamProject loadSeamProject(IProject project) throws CoreException {
		JobUtils.waitForIdle();
		System.out.println("Project - "+project);
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		assertNotNull("Seam project for " + project.getName() + " is null", seamProject);
		
		return seamProject;
	}
	
	public void testSeamComponentRename() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();
		
		TestChangeStructure structure = new TestChangeStructure(ejbProject.getProject(), "/ejbModule/org/domain/"+warProjectName+"/session/TestComponent.java",
				89, 6, "\"best\"");
		list.add(structure);
		
		structure = new TestChangeStructure(warProject, "/WebContent/WEB-INF/components.xml",
				1106, 4, "best");
		list.add(structure);
		structure = new TestChangeStructure(warProject, "/WebContent/WEB-INF/components.xml",
				1934, 4, "best");
		list.add(structure);
		
		structure = new TestChangeStructure(ejbProject, "/ejbModule/org/domain/"+warProjectName+"/session/TestSeamComponent.java",
				420, 11, "@In(\"best\")");
		list.add(structure);
		structure = new TestChangeStructure(ejbProject, "/ejbModule/org/domain/"+warProjectName+"/session/TestSeamComponent.java",
				389, 8, "(\"best\")");
		list.add(structure);
		structure = new TestChangeStructure(ejbProject, "/ejbModule/org/domain/"+warProjectName+"/session/TestSeamComponent.java",
				455, 16, "@Factory(\"best\")");
		list.add(structure);
		structure = new TestChangeStructure(ejbProject, "/ejbModule/org/domain/"+warProjectName+"/session/TestSeamComponent.java",
				529, 8, "(\"best\")");
		list.add(structure);
		structure = new TestChangeStructure(ejbProject, "/ejbModule/org/domain/"+warProjectName+"/session/TestSeamComponent.java",
				589, 4, "best");
		list.add(structure);
		
		structure = new TestChangeStructure(ejbProject, "/ejbModule/seam.properties",
				0, 4, "best");
		list.add(structure);
		
		structure = new TestChangeStructure(warProject, "/WebContent/test.xhtml",
				1088, 4, "best");
		list.add(structure);
		
		structure = new TestChangeStructure(warProject, "/WebContent/test.jsp",
				227, 4, "best");
		list.add(structure);
		
		structure = new TestChangeStructure(warProject, "/WebContent/test.properties",
				29, 4, "best");
		list.add(structure);
		
		renameComponent(seamEjbProject, "test", "best", list);
	}
	
	private void renameComponent(ISeamProject seamProject, String componentName, String newName, List<TestChangeStructure> changeList) throws CoreException{
		
		// Test before renaming
		ISeamComponent component = seamProject.getComponent(componentName);
		assertNotNull(component);
		assertNull(seamProject.getComponent(newName));
		for(TestChangeStructure changeStructure : changeList){
			IFile file = changeStructure.getProject().getFile(changeStructure.getFileName());
			String content = null;
			content = FileUtil.readStream(file.getContents());
			assertNotSame(changeStructure.getText(), content.substring(changeStructure.getOffset(), changeStructure.getOffset()+changeStructure.getLength()));
		}
		
		// Rename Seam Component
		RenameComponentProcessor processor = new RenameComponentProcessor(component);
		processor.setNewComponentName(newName);
		Change change = processor.createChange(new NullProgressMonitor());
		change.perform(new NullProgressMonitor());
		JobUtils.waitForIdle();
		
		// Test results
		assertNull(seamProject.getComponent(componentName));
		assertNotNull(seamProject.getComponent(newName));
		for(TestChangeStructure changeStructure : changeList){
			IFile file = changeStructure.getProject().getFile(changeStructure.getFileName());
			String content = null;
			content = FileUtil.readStream(file.getContents());
			System.out.println("File - "+file.getName()+" offset - "+changeStructure.getOffset()+" expected - ["+changeStructure.getText()+"] actual - ["+content.substring(changeStructure.getOffset(), changeStructure.getOffset()+changeStructure.getLength())+"]");
			assertEquals(changeStructure.getText(), content.substring(changeStructure.getOffset(), changeStructure.getOffset()+changeStructure.getLength()));
		}
	}
	
	class TestChangeStructure{
		private IProject project;
		private String fileName;
		private int offset;
		private int length;
		private String text;
		
		public TestChangeStructure(IProject project, String fileName, int offset, int length, String text){
			this.project = project;
			this.fileName = fileName;
			this.offset = offset;
			this.length = length;
			this.text = text;
		}
		
		public IProject getProject(){
			return project;
		}
		
		public String getFileName(){
			return fileName;
		}
		
		public int getOffset(){
			return offset;
		}
		
		public int getLength(){
			return length;
		}
		
		public String getText(){
			return text;
		}
	}
}
