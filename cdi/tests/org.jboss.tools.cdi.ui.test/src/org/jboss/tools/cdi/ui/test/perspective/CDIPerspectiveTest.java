/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.cdi.ui.test.perspective;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.WorkbenchException;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * @author Alexey Kazakov
 */
public class CDIPerspectiveTest extends TestCase {

	/**
	 * Tests JEE perspective has CDI/JSF stuff
	 * @throws WorkbenchException
	 */
	public void testPerspective() throws WorkbenchException {
		IWorkbenchPage page = WorkbenchUtils.getWorkbench().getActiveWorkbenchWindow().openPage("org.eclipse.jst.j2ee.J2EEPerspective", null);
		assertNotNull(page);
		String[] shortcuts = page.getNewWizardShortcuts();
		Set<String> shortcutSet = new HashSet<String>();
		for (String shortcut : shortcuts) {
			shortcutSet.add(shortcut);
		}
		assertTrue("Have not found org.jboss.tools.cdi.ui.wizard.NewBeanCreationWizard in org.eclipse.jst.j2ee.J2EEPerspective.", shortcutSet.contains("org.jboss.tools.cdi.ui.wizard.NewBeanCreationWizard"));
		assertTrue("Have not found org.jboss.tools.cdi.ui.wizard.NewQualifierCreationWizard in org.eclipse.jst.j2ee.J2EEPerspective.", shortcutSet.contains("org.jboss.tools.cdi.ui.wizard.NewQualifierCreationWizard"));
		assertTrue("Have not found org.jboss.tools.cdi.ui.wizard.NewStereotypeCreationWizard in org.eclipse.jst.j2ee.J2EEPerspective.", shortcutSet.contains("org.jboss.tools.cdi.ui.wizard.NewStereotypeCreationWizard"));
		assertTrue("Have not found org.jboss.tools.cdi.ui.wizard.NewScopeCreationWizard in org.eclipse.jst.j2ee.J2EEPerspective.", shortcutSet.contains("org.jboss.tools.cdi.ui.wizard.NewScopeCreationWizard"));
		assertTrue("Have not found org.jboss.tools.cdi.ui.wizard.NewInterceptorBindingCreationWizard in org.eclipse.jst.j2ee.J2EEPerspective.", shortcutSet.contains("org.jboss.tools.cdi.ui.wizard.NewInterceptorBindingCreationWizard"));
		assertTrue("Have not found org.jboss.tools.cdi.ui.wizard.NewInterceptorCreationWizard in org.eclipse.jst.j2ee.J2EEPerspective.", shortcutSet.contains("org.jboss.tools.cdi.ui.wizard.NewInterceptorCreationWizard"));
		assertTrue("Have not found org.jboss.tools.cdi.ui.wizard.NewDecoratorCreationWizard in org.eclipse.jst.j2ee.J2EEPerspective.", shortcutSet.contains("org.jboss.tools.cdi.ui.wizard.NewDecoratorCreationWizard"));
		assertTrue("Have not found org.jboss.tools.cdi.ui.wizard.NewAnnotationLiteralCreationWizard in org.eclipse.jst.j2ee.J2EEPerspective.", shortcutSet.contains("org.jboss.tools.cdi.ui.wizard.NewAnnotationLiteralCreationWizard"));
		assertTrue("Have not found org.jboss.tools.cdi.ui.wizard.NewBeansXMLCreationWizard in org.eclipse.jst.j2ee.J2EEPerspective.", shortcutSet.contains("org.jboss.tools.cdi.ui.wizard.NewBeansXMLCreationWizard"));
		IViewReference[] viewReferences = page.getViewReferences();
		Set<String> viewIds = new HashSet<String>();
		for (IViewReference viewReference : viewReferences) {
			viewIds.add(viewReference.getId());
		}
		assertTrue("Have not found org.eclipse.gef.ui.palette_view in org.eclipse.jst.j2ee.J2EEPerspective.", viewIds.contains("org.eclipse.gef.ui.palette_view"));
	}
}