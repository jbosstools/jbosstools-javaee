package org.jboss.tools.cdi.core.test.tck;

import java.util.ArrayList;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.internal.core.refactoring.RenameNamedBeanProcessor;
import org.jboss.tools.tests.AbstractRefactorTest;

public class NamedBeanRefactoringTest extends AbstractRefactorTest {
	private static final String PROJECT_NAME = "/tests/jbt/refactoring";
	private static final String FILE_NAME1 = "JavaSource/org/jboss/jsr299/tck/tests/jbt/refactoring/Game.java";
	private static final String FILE_NAME2 = "JavaSource/org/jboss/jsr299/tck/tests/jbt/refactoring/Generator.java";
	private static final String FILE_NAME3 = "WebContent/HomePage.xhtml";
	private static final String FILE_NAME4 = "WebContent/index.jsp";
	static IProject project;

	public NamedBeanRefactoringTest() {
		super("Named Bean Refactoring Test");
	}

	public static Test suite() {
		return new TestSuite(NamedBeanRefactoringTest.class);
	}

	public void testNamedBeanRename() throws Exception {
		IProject project = TCKTest.importPreparedProject(PROJECT_NAME);
		doTest(project);
		TCKTest.cleanProject(PROJECT_NAME);
	}

	public void doTest(IProject project) throws CoreException {
		final String newName = "abcd";
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(project
				.getProject(), FILE_NAME1);
		TestTextChange change = new TestTextChange(328, 4, newName);
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(project, FILE_NAME2);
		change = new TestTextChange(526, 4, newName);
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(project, FILE_NAME3);
		change = new TestTextChange(789, 4, newName);
		structure.addTextChange(change);
		change = new TestTextChange(923, 4, newName);
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(project, FILE_NAME4);
		change = new TestTextChange(293, 4, newName);
		structure.addTextChange(change);
		list.add(structure);

		IFile sourceFile = project.getProject().getFile(FILE_NAME1);

		IBean bean = getBean(sourceFile, "game");
		assertNotNull("Can't get the bean.", bean);

		RenameNamedBeanProcessor processor = new RenameNamedBeanProcessor(bean);
		processor.setNewName(newName);

		checkRename(processor, list);
	}

	private IBean getBean(IFile file, String name) {
		CDICoreNature cdiNature = CDICorePlugin.getCDI(file.getProject(), true);
		assertNotNull("Can't get CDI nature.", cdiNature);

		ICDIProject cdiProject = cdiNature.getDelegate();

		assertNotNull("Can't get CDI project.", cdiProject);

		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());

		for (IBean bean : beans) {
			if (bean.getName() != null && name.equals(bean.getName())) {
				return bean;
			}
		}
		return null;
	}
}