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
package org.jboss.tools.cdi.ui.test.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.ui.wizard.OpenCDINamedBeanDialog;
import org.jboss.tools.cdi.ui.wizard.OpenCDINamedBeanDialog.CDINamedBeanWrapper;
import org.jboss.tools.test.util.JobUtils;

/**
 * JUnit Test case due to test JBIDE-7892 issue.
 * 
 * @author Victor V. Rubezhny
 *
 */
public class OpenCDINamedBeanDialogTest extends TCKTest {
	private IProject project;
	
	@Override
	protected void setUp() throws Exception {
		project = TCKTest.findTestProject();
	}
	
	public void testCDINamedBeanDialogSearch() throws CoreException {
		find("spi", "SpiderSize", "OtherSpiderProducer.java", true);
		find("bla", "blackWidow", "BlackWidowProducer.java", true);
		find("lady", "ladybirdSpider", "SpiderProducer.java", true);
	}
	
	public void testCDINamedBeanDialogSearchShortHand() throws CoreException {
		find("s*ze", "SpiderSize", "OtherSpiderProducer.java", true);
		find("b*w", "blackWidow", "BlackWidowProducer.java", true);
		find("*dSp*r", "ladybirdSpider", "SpiderProducer.java", true);
		find("foo?", "foo3", "TestNamed.java", true);
	}
	
	private void find(String pattern, String beanName, String editorName, boolean wait) throws CoreException{
		OpenCDINamedBeanDialog dialog = new OpenCDINamedBeanDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), true);
		
		dialog.setBlockOnOpen(false);
		dialog.setInitialPattern(pattern);
		dialog.open();
		IBean bean = null;
		try {
			dialog.startSearch();
			if(wait){
					JobUtils.waitForIdle();
					JobUtils.delay(2000);
			}
			dialog.stopSearchAndShowResults();
			Object[] objects = dialog.getResult();
			
			assertNotNull("Search dialog returned null when searching for " + pattern, objects);
			
			assertTrue("Component "+beanName+" not found", objects.length != 0);
		
			bean = findNamedBean(objects, beanName);
		
			assertNotNull("Component "+beanName+" not found with " + pattern, bean);
		} finally {
			dialog.okPressed();
			dialog.close();
		}

		bean.open();
		IEditorPart resultEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		assertTrue("Unexpected editor is opened for CDI Named Bean '" + bean.getName() + "': " + resultEditor.getTitle(), editorName.equals(resultEditor.getTitle()));
	}
	
	private IBean findNamedBean(Object[] objects, String beanName) {
		for (Object o: objects) {
			CDINamedBeanWrapper wrapper = (CDINamedBeanWrapper)o;
			assertNotNull(wrapper.getBean());
			if(beanName.equals(wrapper.getBeanName())) {
				return wrapper.getBean();
			}
		}
		return null;
	}
}
