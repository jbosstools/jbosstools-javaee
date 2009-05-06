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
	static String warProjectName = "RenameComponentWarTestProject";
	static IProject warProject;
	static ISeamProject seamWarProject;
	
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
	}
	
	private ISeamProject loadSeamProject(IProject project) throws CoreException {
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		assertNotNull("Seam project for " + project.getName() + " is null", seamProject);
		JobUtils.waitForIdle();
		return seamProject;
	}
	
	public void testSeamComponentRename() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();
		
		TestChangeStructure structure = new TestChangeStructure("/src/hot/org/domain/renamecomponentwartestproject/session/TestComponent.java",
				113, 6, "\"best\"");
		list.add(structure);
		
		structure = new TestChangeStructure("/WebContent/WEB-INF/components.xml",
				1455, 6, "best");
		list.add(structure);
		structure = new TestChangeStructure("/WebContent/WEB-INF/components.xml",
				2530, 4, "best");
		list.add(structure);
		
		structure = new TestChangeStructure("/src/hot/org/domain/renamecomponentwartestproject/session/Authenticator.java",
				413, 0, "(\"best\")");
		list.add(structure);
		structure = new TestChangeStructure("/src/hot/org/domain/renamecomponentwartestproject/session/Authenticator.java",
				436, 11, "@In(\"best\")");
		list.add(structure);
		structure = new TestChangeStructure("/src/hot/org/domain/renamecomponentwartestproject/session/Authenticator.java",
				471, 16, "@Factory(\"best\")");
		list.add(structure);
		structure = new TestChangeStructure("/src/hot/org/domain/renamecomponentwartestproject/session/Authenticator.java",
				545, 0, "(\"best\")");
		list.add(structure);
		structure = new TestChangeStructure("/src/hot/org/domain/renamecomponentwartestproject/session/Authenticator.java",
				597, 4, "best");
		list.add(structure);
		
		structure = new TestChangeStructure("/RenameComponentWarTestProject/src/hot/seam.properties",
				0, 4, "best");
		list.add(structure);
		
		structure = new TestChangeStructure("/WebContent/login.xhtml",
				1033, 4, "best");
		list.add(structure);
		
		structure = new TestChangeStructure("/WebContent/test.jsp",
				227, 4, "best");
		list.add(structure);
		
		structure = new TestChangeStructure("/WebContent/test.properties",
				29, 4, "best");
		list.add(structure);
		
		renameComponent(seamWarProject, "test", "best", list);
	}
	
	private void renameComponent(ISeamProject seamProject, String componentName, String newName, List<TestChangeStructure> changeList) throws CoreException{
		
		// Test before renaming
		ISeamComponent component = seamProject.getComponent(componentName);
		assertNotNull(component);
		assertNull(seamProject.getComponent(newName));
		for(TestChangeStructure changeStructure : changeList){
			IFile file = seamProject.getProject().getFile(changeStructure.getFileName());
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
		//assertNull(seamProject.getComponent(componentName));
		assertNotNull(seamProject.getComponent(newName));
		for(TestChangeStructure changeStructure : changeList){
			IFile file = seamProject.getProject().getFile(changeStructure.getFileName());
			String content = null;
			content = FileUtil.readStream(file.getContents());
			assertEquals(changeStructure.getText(), content.substring(changeStructure.getOffset(), changeStructure.getOffset()+changeStructure.getLength()));
		}
	}
	
	class TestChangeStructure{
		private String fileName;
		private int offset;
		private int length;
		private String text;
		
		public TestChangeStructure(String fileName, int offset, int length, String text){
			this.fileName = fileName;
			this.offset = offset;
			this.length = length;
			this.text = text;
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
