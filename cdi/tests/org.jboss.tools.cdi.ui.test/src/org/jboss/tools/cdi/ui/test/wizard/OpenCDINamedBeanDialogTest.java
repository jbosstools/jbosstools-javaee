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
import org.eclipse.core.resources.ResourcesPlugin;
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
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(TCKTest.PROJECT_NAME);
	}
	
	public void testCDINamedBeanDialogSearch() {
		find("spi", "SpiderSize", true);
		find("bla", "blackWidow", false);
		find("lady", "ladybirdSpider", false);
	}
	
	public void testCDINamedBeanDialogSearchShortHand() {
		find("s*ze", "SpiderSize", true);
		find("b*w", "blackWidow", false);
		find("*dSp*r", "ladybirdSpider", false);
		find("foo?", "foo3", false);
	}
	
	private void find(String pattern, String beanName, boolean wait){
		OpenCDINamedBeanDialog dialog = new OpenCDINamedBeanDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		
		dialog.setBlockOnOpen(false);
		dialog.setInitialPattern(pattern);
		dialog.open();
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
		
			IBean bean = findNamedBean(objects, beanName);
		
			assertNotNull("Component "+beanName+" not found with " + pattern, bean);
		} finally {
			dialog.okPressed();
			dialog.close();
		}
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
