package org.jboss.tools.jsf.test.refactoring;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameNonVirtualMethodProcessor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.model.handlers.bean.JSFRenameFieldParticipant;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class MethodRefactoringTest extends TestCase {
	static String projectName = "JSFKickStartOldFormat";
	static IProject project;
	
	public MethodRefactoringTest() {
		super("");
	}

	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(projectName);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}

	public void testMethodRename() throws CoreException, InvocationTargetException, InterruptedException {
		IJavaProject jp = EclipseResourceUtil.getJavaProject(project);
		IType t = jp.findType("demo.User");
		assertNotNull(t);
		IMethod m = t.getMethod("getAge", new String[0]);
		assertNotNull(m);
		
		RenameNonVirtualMethodProcessor processor = new RenameNonVirtualMethodProcessor(m);
		processor.setNewElementName("getAge2");

		JSFRenameFieldParticipant participant = new JSFRenameFieldParticipant();
		participant.initialize(processor, m, new RenameArguments("getAge2", true));

		Change change = participant.createChange(new NullProgressMonitor());
		assertTrue(change instanceof TextFileChange);
		TextFileChange tc = (TextFileChange)change;
		TextEdit e = tc.getEdit();
		if(e instanceof MultiTextEdit) {
			TextEdit[] cs = ((MultiTextEdit)e).getChildren();
			assertEquals(1, cs.length);
			e = cs[0];
		}
		assertTrue(e instanceof ReplaceEdit);
		ReplaceEdit re = (ReplaceEdit)e;
		String text = re.getText();
		assertEquals("age2", text);
	}
	

}
