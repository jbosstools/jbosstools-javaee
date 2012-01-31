/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
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
import org.jboss.tools.common.base.test.AbstractRefactorTest;
import org.jboss.tools.common.base.test.AbstractRefactorTest.TestChangeStructure;
import org.jboss.tools.common.base.test.AbstractRefactorTest.TestTextChange;

public class NamedBeanRefactoringTest extends TCKTest {
	private static final String FILE_NAME1 = "JavaSource/org/jboss/jsr299/tck/tests/jbt/refactoring/Gamme.java";
	private static final String FILE_NAME2 = "JavaSource/org/jboss/jsr299/tck/tests/jbt/refactoring/Generator.java";
	private static final String FILE_NAME3 = "WebContent/tests/jbt/refactoring/HomePage.xhtml";
	private static final String FILE_NAME4 = "WebContent/tests/jbt/refactoring/index.jsp";
	
	private static final String newName = "abcde";
	private static final int NUM_OF_CHAR = 5;

	public void testNamedBeanClassRename() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(tckProject, FILE_NAME1);
		TestTextChange change = new TestTextChange(328, NUM_OF_CHAR, newName);
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(tckProject, FILE_NAME2);
		change = new TestTextChange(/*526*/"gamme}", NUM_OF_CHAR, newName);
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(tckProject, FILE_NAME3);
		change = new TestTextChange(/*789*/"gamme.check}", NUM_OF_CHAR, newName);
		structure.addTextChange(change);
		change = new TestTextChange(/*924*/"gamme.reset}", NUM_OF_CHAR, newName);
		structure.addTextChange(change);
		list.add(structure);

		structure = new TestChangeStructure(tckProject, FILE_NAME4);
		change = new TestTextChange(/*293*/"gamme.biggest}", NUM_OF_CHAR, newName);
		structure.addTextChange(change);
		list.add(structure);

		IFile sourceFile = tckProject.getProject().getFile(FILE_NAME1);

		IBean bean = getBean(sourceFile, "gamme");
		assertNotNull("Can't get the bean.", bean);

		RenameNamedBeanProcessor processor = new RenameNamedBeanProcessor(bean);
		processor.setNewName(newName);

		AbstractRefactorTest.checkRename(processor, list);
	}
	
	public void testNamedBeanProducerFieldRename() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(tckProject, "JavaSource/org/jboss/jsr299/tck/tests/jbt/refactoring/ProducerFieldBean.java");
		TestTextChange change = new TestTextChange("sField\")", 6, "uField");
		structure.addTextChange(change);
		change = new TestTextChange("sField.charAt(0)}", 6, "uField");
		structure.addTextChange(change);
		list.add(structure);

		IFile sourceFile = tckProject.getProject().getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/refactoring/ProducerFieldBean.java");

		IBean bean = getBean(sourceFile, "sField");
		assertNotNull("Can't get the bean.", bean);

		RenameNamedBeanProcessor processor = new RenameNamedBeanProcessor(bean);
		processor.setNewName("uField");

		AbstractRefactorTest.checkRename(processor, list);
	}

	public void testNamedBeanProducerMethodRename() throws CoreException {
		ArrayList<TestChangeStructure> list = new ArrayList<TestChangeStructure>();

		TestChangeStructure structure = new TestChangeStructure(tckProject, "JavaSource/org/jboss/jsr299/tck/tests/jbt/refactoring/ProducerMethodBean.java");
		TestTextChange change = new TestTextChange("infoMethod\")", 10, "memoMethod");
		structure.addTextChange(change);
		change = new TestTextChange("infoMethod.charAt(0)}", 10, "memoMethod");
		structure.addTextChange(change);
		list.add(structure);

		IFile sourceFile = tckProject.getProject().getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/refactoring/ProducerMethodBean.java");

		IBean bean = getBean(sourceFile, "infoMethod");
		assertNotNull("Can't get the bean.", bean);

		RenameNamedBeanProcessor processor = new RenameNamedBeanProcessor(bean);
		processor.setNewName("memoMethod");

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