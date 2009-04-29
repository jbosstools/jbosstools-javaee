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

public class SeamComponentRefactoringTest  extends TestCase {
	static String warProjectName = "RenameComponentTestProject-war";
	static String ejbProjectName = "RenameComponentTestProject-ejb";
	static IProject warProject;
	static IProject ejbProject;
	static ISeamProject seamWarProject;
	static ISeamProject seamEjbProject;
	
	protected void setUp() throws Exception {
		if(warProject==null) {
			warProject = ProjectImportTestSetup.loadProject(warProjectName);
		}
		if(ejbProject==null) {
			ejbProject = ProjectImportTestSetup.loadProject(ejbProjectName);;
		}
		if(seamWarProject==null) {
			seamWarProject = loadSeamProject(warProject);
		}
		if(seamEjbProject==null) {
			seamEjbProject = loadSeamProject(ejbProject);
		}
	}
	
	private ISeamProject loadSeamProject(IProject project) throws CoreException {
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		assertNotNull("Seam project for " + project.getName() + " is null", seamProject);
		JobUtils.waitForIdle();
		return seamProject;
	}
	
	public void testSeamComponentRename() throws CoreException {
		ISeamComponent[] components = seamWarProject.getComponents();
		for(ISeamComponent component : components){
			System.out.println("Seam Component - "+component.getName());
		}
		
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();
		renameComponent(seamWarProject, "abc", "tre", list);
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
		
		// Test results
		assertNull(seamProject.getComponent(componentName));
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
