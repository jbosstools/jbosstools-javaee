/*******************************************************************************
 * Copyright (c) 2011-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.test.refactoring;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
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
		change = new TestTextChange(1875, NAME_LEN, NEW_NAME);
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

		sourceFile = project.getProject().getFile("/WebContent/pages/syntaxErrors.xhtml");
		sourceFileContent = FileUtil.getContentFromEditorOrFile(sourceFile);
		position = sourceFileContent.indexOf("user");
		structure = new TestChangeStructure(project, "/WebContent/pages/syntaxErrors.xhtml");
		change = new TestTextChange(position, NAME_LEN, NEW_NAME);
		structure.addTextChange(change);
		list.add(structure);

		RenameELVariableProcessor processor = new RenameELVariableProcessor(sourceFile, "user");
		processor.setNewName(NEW_NAME);

		checkRename(processor, list);
	}
}