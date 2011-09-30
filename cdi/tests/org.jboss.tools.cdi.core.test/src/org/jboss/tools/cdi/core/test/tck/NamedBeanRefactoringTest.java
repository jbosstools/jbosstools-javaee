package org.jboss.tools.cdi.core.test.tck;

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.internal.core.refactoring.RenameNamedBeanProcessor;
import org.jboss.tools.tests.AbstractRefactorTest;
import org.jboss.tools.tests.AbstractRefactorTest.TestChangeStructure;
import org.jboss.tools.tests.AbstractRefactorTest.TestTextChange;

public class NamedBeanRefactoringTest extends TCKTest {
	private static final String FILE_NAME1 = "JavaSource/org/jboss/jsr299/tck/tests/jbt/refactoring/Gamme.java";
	private static final String FILE_NAME2 = "JavaSource/org/jboss/jsr299/tck/tests/jbt/refactoring/Generator.java";
	private static final String FILE_NAME3 = "WebContent/tests/jbt/refactoring/HomePage.xhtml";
	private static final String FILE_NAME4 = "WebContent/tests/jbt/refactoring/index.jsp";
	
	private static final String newName = "abcde";
	private static final int NUM_OF_CHAR = 5;

	public void testNamedBeanRename() throws CoreException {
		
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(tckProject, FILE_NAME1);
		TestTextChange change = new TestTextChange(328, NUM_OF_CHAR, newName);
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(tckProject, FILE_NAME2);
		change = new TestTextChange(526, NUM_OF_CHAR, newName);
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(tckProject, FILE_NAME3);
		change = new TestTextChange(789, NUM_OF_CHAR, newName);
		structure.addTextChange(change);
		change = new TestTextChange(924, NUM_OF_CHAR, newName);
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(tckProject, FILE_NAME4);
		change = new TestTextChange(293, NUM_OF_CHAR, newName);
		structure.addTextChange(change);
		list.add(structure);

		IFile sourceFile = tckProject.getProject().getFile(FILE_NAME1);

		IBean bean = getBean(sourceFile, "gamme");
		assertNotNull("Can't get the bean.", bean);

		RenameNamedBeanProcessor processor = new RenameNamedBeanProcessor(bean);
		processor.setNewName(newName);

		AbstractRefactorTest.checkRename(processor, list);
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