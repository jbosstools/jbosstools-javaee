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

import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.parser.ELParser;
import org.jboss.tools.common.el.core.parser.ELParserFactory;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.validation.ValidatorManager;
import org.jboss.tools.jsf.jsf2.bean.el.JSF2ElResolver;
import org.jboss.tools.jsf.jsf2.bean.model.IJSF2ManagedBean;
import org.jboss.tools.jsf.jsf2.bean.model.IJSF2Project;
import org.jboss.tools.jsf.jsf2.bean.model.JSF2ProjectFactory;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

public class JSF2ModelTest extends TestCase {
	IProject project = null;
	IProject webproject = null;
	
	public JSF2ModelTest() {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("JSF2Beans");
		webproject = ResourcesPlugin.getWorkspace().getRoot().getProject("JSF2Web");
	}
	
	public void testModel() {
		IJSF2Project jsf2 = JSF2ProjectFactory.getJSF2Project(project, true);
		assertNotNull(jsf2);
		Set<IJSF2ManagedBean> beans = jsf2.getManagedBeans("mybean1");
		assertEquals(1, beans.size());
		beans = jsf2.getManagedBeans("mybean2");
		assertEquals(2, beans.size());
	}
	
	/**
	 * Modifies metadata-complete flag in /WEB-INF/faces-config.xml.
	 * Checks that the flag is loaded correctly into model.
	 * Checks that JSF2ElResolver takes into account that flag.
	 *  
	 * @throws CoreException
	 */
	public void testMetadataCompleteAndElResolver() throws CoreException {
		IJSF2Project jsf2 = JSF2ProjectFactory.getJSF2Project(webproject, true);
		assertNotNull(jsf2);

		Set<IJSF2ManagedBean> beans = jsf2.getManagedBeans("mybean1");
		assertEquals(1, beans.size());

		IFile f = webproject.getFile("src/test/beans/inputname.xhtml");
		assertTrue(f.exists());
		
		JSF2ElResolver resolver = new JSF2ElResolver();
		ELParser p = ELParserUtil.getJbossFactory().createParser();
		ELInvocationExpression exp = (ELInvocationExpression)p.parse("#{myb}").getInstances().get(0).getExpression();

		assertFalse(jsf2.isMetadataComplete());
		List<IJSF2ManagedBean> bs = resolver.resolveVariables(f, exp, true, false, 5);
		assertFalse(bs.isEmpty());

		replaceFile(webproject, "WebContent/WEB-INF/faces-config.complete", "WebContent/WEB-INF/faces-config.xml");
		assertTrue(jsf2.isMetadataComplete());
		bs = resolver.resolveVariables(f, exp, true, false, 5);
		assertTrue(bs.isEmpty());
		beans = jsf2.getManagedBeans("mybean1");
		assertTrue(beans.isEmpty());

		replaceFile(webproject, "WebContent/WEB-INF/faces-config.original", "WebContent/WEB-INF/faces-config.xml");
		assertFalse(jsf2.isMetadataComplete());
		bs = resolver.resolveVariables(f, exp, true, false, 5);
		assertFalse(bs.isEmpty());
		beans = jsf2.getManagedBeans("mybean1");
		assertEquals(1, beans.size());
	}
	
	public static void replaceFile(IProject project, String sourcePath, String targetPath) throws CoreException {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		try {
			IFile target = project.getFile(new Path(targetPath));
			IFile source = project.getFile(new Path(sourcePath));
			assertTrue(source.exists());
			ValidatorManager.setStatus(ValidatorManager.RUNNING);
			if(!target.exists()) {
				target.create(source.getContents(), true, new NullProgressMonitor());
			} else {
				target.setContents(source.getContents(), true, false, new NullProgressMonitor());
			}
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
			JobUtils.waitForIdle();
		}
	}
}
