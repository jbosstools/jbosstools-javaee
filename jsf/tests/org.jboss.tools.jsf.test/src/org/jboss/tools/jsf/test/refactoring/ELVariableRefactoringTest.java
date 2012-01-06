package org.jboss.tools.jsf.test.refactoring;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.common.base.test.AbstractRefactorTest;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jsf.el.refactoring.RenameELVariableProcessor;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class ELVariableRefactoringTest extends AbstractRefactorTest {
	static String projectName = "JSFKickStartOldFormat";
	static IProject project;

	private static final String NEW_NAME = "cust";
	private static final int NAME_LEN = 4;
	
	public ELVariableRefactoringTest(){
		super("EL Variable Refactoring Test");
	}
	
	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(projectName);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}
	
	public void testELVariableRename() throws CoreException {
		
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();
		
		IFile sourceFile = project.getProject().getFile("/WebContent/pages/hello.jsp");
		
		String sourceFileContent = FileUtil.getContentFromEditorOrFile(sourceFile);
		
		int position = sourceFileContent.indexOf("user.name");
		
		TestChangeStructure structure = new TestChangeStructure(project.getProject(), "/WebContent/pages/hello.jsp");
		TestTextChange change = new TestTextChange(position, NAME_LEN, NEW_NAME);
		structure.addTextChange(change);
		
		position = sourceFileContent.indexOf("user.name", position+1);
		
		change = new TestTextChange(position, NAME_LEN, NEW_NAME);
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(project, "/WebContent/WEB-INF/faces-config.xml");
		change = new TestTextChange(1815, NAME_LEN, NEW_NAME);
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(project, "/WebContent/pages/inputUserName.jsp");
		change = new TestTextChange(494, NAME_LEN, NEW_NAME);
		structure.addTextChange(change);
		list.add(structure);
		
		structure = new TestChangeStructure(project, "/WebContent/pages/el.jsp");
		change = new TestTextChange(83, NAME_LEN, NEW_NAME);
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(project, "/WebContent/testElRevalidation.xhtml");
		change = new TestTextChange(601, NAME_LEN, NEW_NAME);
		structure.addTextChange(change);
		list.add(structure);
		
		RenameELVariableProcessor processor = new RenameELVariableProcessor(sourceFile, "user");
		processor.setNewName(NEW_NAME);

		checkRename(processor, list);
	}

}
