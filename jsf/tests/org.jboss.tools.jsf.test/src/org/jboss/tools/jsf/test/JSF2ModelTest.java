/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.test;

import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.jsf.jsf2.bean.model.IJSF2ManagedBean;
import org.jboss.tools.jsf.jsf2.bean.model.IJSF2Project;
import org.jboss.tools.jsf.jsf2.bean.model.JSF2ProjectFactory;
import org.jboss.tools.test.util.TestProjectProvider;

public class JSF2ModelTest extends TestCase {
	TestProjectProvider provider = null;
	IProject project = null;
	
	public JSF2ModelTest() {}
	
	public void setUp() throws Exception {
		project = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember("JSF2Beans");
		if(project==null) {
			provider = new TestProjectProvider("org.jboss.tools.jsf.test", null, "JSF2Beans", false);
			project = provider.getProject();
		}
		
	}
	
	public void testModel() {
		IJSF2Project jsf2 = JSF2ProjectFactory.getJSF2Project(project, true);
		assertNotNull(jsf2);
		Set<IJSF2ManagedBean> beans = jsf2.getManagedBeans("mybean1");
		assertEquals(1, beans.size());
		beans = jsf2.getManagedBeans("mybean2");
		assertEquals(2, beans.size());
	}
	
	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
			provider=null;
		}
	}

}
