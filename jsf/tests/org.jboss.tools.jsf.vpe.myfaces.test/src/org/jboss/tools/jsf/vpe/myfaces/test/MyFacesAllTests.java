package org.jboss.tools.jsf.vpe.myfaces.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.tools.tests.ImportBean;
import org.jboss.tools.vpe.ui.test.VpeTestSetup;

/**
 * The Class MyFacesAllTests.
 */
public class MyFacesAllTests {

	/**
	 * Suite.
	 * 
	 * @return the test
	 */
	public static Test suite() {

		TestSuite suite = new TestSuite("Tests for Vpe MyFaces components"); //$NON-NLS-1$

		// $JUnit-BEGIN$
		suite.addTestSuite(MyFacesComponentTest.class);
		// $JUnit-END$

		List<ImportBean> projectToImport = new ArrayList<ImportBean>();
		ImportBean importBean = new ImportBean();
		importBean
				.setImportProjectName(MyFacesComponentTest.IMPORT_PROJECT_NAME);
		importBean.setImportProjectPath(MyFacesTestPlugin
				.getPluginResourcePath());
		projectToImport.add(importBean);

		return new VpeTestSetup(suite, projectToImport);
	}
}
