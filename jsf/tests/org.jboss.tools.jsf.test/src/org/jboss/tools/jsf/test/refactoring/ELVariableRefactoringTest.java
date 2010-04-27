package org.jboss.tools.jsf.test.refactoring;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.jsf.el.refactoring.RenameELVariableProcessor;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.tests.AbstractRefactorTest;

public class ELVariableRefactoringTest extends AbstractRefactorTest {
	static String projectName = "JSFKickStartOldFormat";
	static IProject project;
	
	public ELVariableRefactoringTest(){
		super("EL Variable Refactoring Test");
	}
	
	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(projectName);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}
	
	public void testELVariableRename() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(project.getProject(), "/WebContent/pages/hello.jsp");
		TestTextChange change = new TestTextChange(349, 8, "customer");
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(project, "/WebContent/WEB-INF/faces-config.xml");
		change = new TestTextChange(1815, 8, "customer");
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(project, "/WebContent/pages/inputUserName.jsp");
		change = new TestTextChange(494, 8, "customer");
		structure.addTextChange(change);
		list.add(structure);
		
		IFile sourceFile = project.getProject().getFile("/WebContent/pages/hello.jsp");
		
		RenameELVariableProcessor processor = new RenameELVariableProcessor(sourceFile, "user");
		processor.setNewName("customer");

		checkRename(processor, list);
	}

}
