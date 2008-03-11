/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.html.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.tools.vpe.ui.test.VpeTestSetup;
import org.jboss.tools.vpe.ui.test.beans.ImportBean;


/**
 * Class for testing all Html components
 * 
 * @author sdzmitrovich
 * 
 */

public class HtmlAllTests {

	public static Test suite() {

		TestSuite suite = new TestSuite("Tests for Vpe Jsf components"); // $NON-NLS-1$
		// $JUnit-BEGIN$
		suite.addTestSuite(HtmlComponentTest.class);
		
		List<ImportBean> projectToImport = new ArrayList<ImportBean>();
		ImportBean importBean = new ImportBean();
		importBean.setImportProjectName(HtmlComponentTest.IMPORT_PROJECT_NAME);
 		importBean.setImportProjectPath(HtmlTestPlugin.getPluginResourcePath());
 		projectToImport.add(importBean);
		return new VpeTestSetup(suite,projectToImport);
	}

}
