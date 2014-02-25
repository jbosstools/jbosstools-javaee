/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.jsf.kb.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.tools.common.base.test.model.XProjectImportTestSetUp;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * @author Alexey Kazakov
 */
public class JsfWebKbAllTests {

	public static Test suite() {
		TestSuite suiteAll = new TestSuite("KB Tests");
		TestSuite suite = new TestSuite(JsfWebKbAllTests.class.getName());
		suite.addTestSuite(MyFacesKbModelTest.class);
		suite.addTestSuite(MyFacesKbModelWithMetadataInSourcesTest.class);
		suite.addTestSuite(WebKbTest.class);
		suite.addTestSuite(XMLCatalogTest.class);
		ProjectImportTestSetup testSetup = new XProjectImportTestSetUp(suite,
				"org.jboss.tools.jst.web.kb.test",
				new String[]{"projects/TestKbModel", "projects/MyFaces", "projects/MyFaces2", "projects/TestKbModel3", "projects/TestKbModel4"},
				new String[]{"TestKbModel", "MyFaces", "MyFaces2", "TestKbModel3", "TestKbModel4"});
		suiteAll.addTest(testSetup);
		suite = new TestSuite(WebWithModuleTest.class.getName());
		suite.addTestSuite(WebWithModuleTest.class);
		testSetup = new XProjectImportTestSetUp(suite,
				"org.jboss.tools.jst.web.kb.test",
				new String[]{"projects/utility", "projects/webapp"},
				new String[]{"utility", "webapp"});
		suiteAll.addTest(testSetup);
		return suiteAll;
	}
}