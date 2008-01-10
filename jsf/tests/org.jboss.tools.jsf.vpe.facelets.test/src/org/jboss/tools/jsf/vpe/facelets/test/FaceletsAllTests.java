package org.jboss.tools.jsf.vpe.facelets.test;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.vpe.ui.test.VpeTestSetup;
import org.jboss.tools.vpe.ui.test.beans.ImportBean;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FaceletsAllTests {

    public static Test suite() {

	TestSuite suite = new TestSuite("Tests for Vpe Facelets components");
	// $JUnit-BEGIN$

	suite.addTestSuite(FaceletsComponentTest.class);

	// $JUnit-END$
	List<ImportBean> importProjects = new ArrayList<ImportBean>();
	ImportBean importBean = new ImportBean();
	importBean.setImportProjectName(FaceletsComponentTest.IMPORT_PROJECT_NAME);
	importBean.setImportProjectPath(FaceletsTestPlugin.getPluginResourcePath());
	importProjects.add(importBean);
	return new VpeTestSetup(suite,importProjects);

    }
}
