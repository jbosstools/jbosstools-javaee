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
package org.jboss.tools.cdi.core.test;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 *   
 * @author V.Kabanovich
 *
 */
public class BeansXMLTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	IProject project = null;

	public BeansXMLTest() {}

	public void setUp() throws Exception {
		project = ResourcesUtils.importProject(PLUGIN_ID, "/projects/CDITest1");
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}

	public void testBeansXML() throws CoreException, IOException {
		IFile file = project.getFile(new Path("META-INF/beans.xml"));
		assertNotNull(file);
		XModelObject beansXML = EclipseResourceUtil.createObjectForResource(file);
		assertNotNull(beansXML);

		assertEquals("FileCDIBeans", beansXML.getModelEntity().getName());

		XModelObject o = findTag(beansXML, "drools:RuleResources");
		assertNotNull(o);

		o = findTag(beansXML, "drools:DroolsConfig/drools:ruleResources/s:Inject");
		assertNotNull(o);
	}

	public void testWeldBeansXML() throws CoreException, IOException {
		IFile file = project.getFile(new Path("META-INF/weld-beans.xml"));
		assertNotNull(file);
		XModelObject beansXML = EclipseResourceUtil.createObjectForResource(file);
		assertNotNull(beansXML);

		assertEquals("FileCDIBeans", beansXML.getModelEntity().getName());

		XModelObject scan = beansXML.getChildByPath("Scan");
		assertNotNull(scan);

		XModelObject include1 = scan.getChildByPath("cls1");
		assertNotNull(include1);
		assertEquals("CDIWeldInclude", include1.getModelEntity().getName());
		String pattern1 = include1.getAttributeValue("name");
		assertEquals("cls1", pattern1);
		assertEquals("true", include1.getAttributeValue("is regular expression"));

		XModelObject include2 = scan.getChildByPath("cls2");
		assertNotNull(include2);
		assertEquals("CDIWeldInclude", include2.getModelEntity().getName());
		String name2 = include2.getAttributeValue("name");
		assertEquals("cls2", name2);
		assertEquals("false", include2.getAttributeValue("is regular expression"));

		XModelObject exclude3 = scan.getChildByPath("cls3");
		assertNotNull(exclude3);
		assertEquals("CDIWeldExclude", exclude3.getModelEntity().getName());
		String name3 = exclude3.getAttributeValue("name");
		assertEquals("cls3", name3);
		assertEquals("false", exclude3.getAttributeValue("is regular expression"));

		XModelObject exclude4 = scan.getChildByPath("cls4");
		assertNotNull(exclude4);
		assertEquals("CDIWeldExclude", exclude4.getModelEntity().getName());
		String pattern4 = exclude4.getAttributeValue("name");
		assertEquals("cls4", pattern4);
		assertEquals("true", exclude4.getAttributeValue("is regular expression"));
	}

	public void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		project.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

	XModelObject findTag(XModelObject parent, String path) {
		XModelObject[] cs = parent.getChildren(XModelObjectLoaderUtil.ENT_ANY_ELEMENT);
		for (XModelObject o: cs) {
			String name = o.getAttributeValue("tag");
			if(name == null) continue;
			if(path.equals(name)) return o;
			if(path.startsWith(name + "/")) {
				return findTag(o, path.substring(name.length() + 1));
			}
		}
		return null;
	}

	public void testBeans11XML() throws CoreException, IOException {
		IFile file = project.getFile(new Path("META-INF/beans11.xml"));
		assertNotNull(file);
		XModelObject beansXML = EclipseResourceUtil.createObjectForResource(file);
		assertNotNull(beansXML);

		assertEquals("FileCDIBeans11", beansXML.getModelEntity().getName());
		assertEquals("annotated", beansXML.getAttributeValue("bean-discovery-mode"));
		assertEquals("1.1", beansXML.getAttributeValue("version"));
		
		assertNotNull(beansXML.getChildByPath("Interceptors/test.MyInterceptor"));
		assertNotNull(beansXML.getChildByPath("Decorators/test.MyDecorator"));
		assertNotNull(beansXML.getChildByPath("Alternatives/test.MyAlternative"));
		assertNotNull(beansXML.getChildByPath("Alternatives/test.MyStereotypeAlternative"));
		
		assertNotNull(beansXML.getChildByPath("Scan"));
		assertNotNull(beansXML.getChildByPath("Scan/test.ExcludedType"));
		assertNotNull(beansXML.getChildByPath("Scan/test.excluded.*"));
		assertEquals(4, beansXML.getChildByPath("Scan/test.ExcludedType").getChildren().length);

	}
}