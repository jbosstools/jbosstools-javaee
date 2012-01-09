package org.jboss.tools.jsf.test.refactoring;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.TextSelection;
import org.jboss.tools.common.base.test.AbstractRefactorTest;
import org.jboss.tools.common.el.core.resolver.ELSegment;
import org.jboss.tools.common.el.core.resolver.MessagePropertyELSegment;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jsf.el.refactoring.RenameMessagePropertyProcessor;
import org.jboss.tools.jsf.ui.el.refactoring.ELRefactorContributionFactory;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class MessagePropertyRefactoringTest extends AbstractRefactorTest{
	static String projectName = "JSFKickStartOldFormat";
	static IProject project;
	
	private static final String NEW_NAME = "good______bye";
	private static final int NAME_LEN = 13;
	
	public MessagePropertyRefactoringTest(){
		super("Resource Bundle Message Refactoring Test");
	}
	
	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(projectName);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}
	
	public void testMessagePropertyRename() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		IFile sourceFile = project.getProject().getFile("/WebContent/pages/hello.jsp");
		
		String sourceFileContent = FileUtil.getContentFromEditorOrFile(sourceFile);
		
		int position = sourceFileContent.indexOf("Message.hello_message");
		position += 8;
		
		TestChangeStructure structure = new TestChangeStructure(project.getProject(), "/WebContent/pages/hello.jsp");
		TestTextChange change = new TestTextChange(position, NAME_LEN, NEW_NAME);
		structure.addTextChange(change);
		
		position = sourceFileContent.indexOf("Message.hello_message", position);
		position += 8;
		
		change = new TestTextChange(position, NAME_LEN, NEW_NAME);
		structure.addTextChange(change);
		list.add(structure);
		
		IFile propertyFile = project.getProject().getFile("/JavaSource/demo/Messages.properties");
		
		structure = new TestChangeStructure(project.getProject(), "/JavaSource/demo/Messages.properties");
		change = new TestTextChange(0, NAME_LEN, NEW_NAME);
		structure.addTextChange(change);
		list.add(structure);
		
		//MessagePropertyELSegmentImpl segment = new MessagePropertyELSegmentImpl(new LexicalToken(position,13,"hello_message",-1000));
		//segment.setMessageBundleResource(propertyFile);
		//segment.setBaseName("demo.Messages");
		//segment.setMessagePropertySourceReference(0,10);
		ELSegment segment = ELRefactorContributionFactory.findELSegment(sourceFile, new TextSelection(position, 13));
		if(segment instanceof MessagePropertyELSegment){
			RenameMessagePropertyProcessor processor = new RenameMessagePropertyProcessor(sourceFile, (MessagePropertyELSegment)segment);
			processor.setNewName(NEW_NAME);

			checkRename(processor, list);
		}else{
			fail("segment must be instance of MessagePropertyELSegment");
		}
	}

}
