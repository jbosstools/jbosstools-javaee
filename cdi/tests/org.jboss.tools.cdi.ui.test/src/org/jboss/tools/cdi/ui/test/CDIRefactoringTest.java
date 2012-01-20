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
package org.jboss.tools.cdi.ui.test;

import junit.framework.TestCase;

import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.jboss.tools.cdi.internal.core.refactoring.RenameNamedBeanProcessor;
import org.jboss.tools.cdi.ui.refactoring.CDIRefactorContributionFactory;
import org.jboss.tools.cdi.ui.refactoring.RenameNamedBeanWizard;
import org.jboss.tools.cdi.ui.test.testmodel.CDIBean;
import org.jboss.tools.cdi.ui.test.testmodel.CDIProject;

public class CDIRefactoringTest extends TestCase {
	public void testCDIRefactorContributionFactory(){
		String location = "location";
		String namespace = "namespace";
		CDIRefactorContributionFactory factory = new CDIRefactorContributionFactory(location, namespace);
		
		assertEquals("CDIRefactorContributionFactory returns wrong location", location, factory.getLocation());
		
		assertEquals("CDIRefactorContributionFactory returns wrong namespace", namespace, factory.getNamespace());
	}
	
	public void testRenameNamedBeanWizard(){
		CDIProject project = new CDIProject();
		CDIBean bean = new CDIBean(project, "org.test.CustomBean");
		RenameNamedBeanProcessor processor = new RenameNamedBeanProcessor(bean);
		//processor.setNewName("CustomMyBean");
		RenameRefactoring refactoring = new RenameRefactoring(processor);
		
		RenameNamedBeanWizard wizard = new RenameNamedBeanWizard(refactoring, bean);
		
		assertEquals("RenameNamedBeanWizard returns wrong refactoring",refactoring, wizard.getRefactoring());

		String title = "Title";
		wizard.setWindowTitle(title);
		assertEquals("RenameNamedBeanWizard returns wrong title",title, wizard.getWindowTitle());
	}
}
