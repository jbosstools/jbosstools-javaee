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
package org.jboss.tools.cdi.test.launcher;

import org.jboss.tools.cdi.core.test.CDICoreAllTests;
import org.jboss.tools.cdi.seam.config.core.test.CDISeamConfigCoreAllTests;
import org.jboss.tools.cdi.seam.config.ui.test.CdiSeamConfigUIAllTests;
import org.jboss.tools.cdi.seam.core.test.CDISeamCoreAllTests;
import org.jboss.tools.cdi.seam.solder.core.test.CDISeamSolderCoreAllTests;
import org.jboss.tools.cdi.seam.text.ext.test.CdiSeamTextExtAllTests;
import org.jboss.tools.cdi.text.ext.test.CdiTextExtAllTests;
import org.jboss.tools.cdi.ui.test.CDIUIAllTests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CDIAllTests {

	public static Test suite() {
		TestSuite suiteAll = new TestSuite("CDI Core Tests");

		suiteAll.addTest(CdiSeamConfigUIAllTests.suite());
		suiteAll.addTest(CDISeamConfigCoreAllTests.suite());
		suiteAll.addTest(CDISeamCoreAllTests.suite());
		suiteAll.addTest(CDISeamSolderCoreAllTests.suite());
		suiteAll.addTest(CdiSeamTextExtAllTests.suite());
		suiteAll.addTest(CdiTextExtAllTests.suite());
		suiteAll.addTest(CDICoreAllTests.suite());
		suiteAll.addTest(CDIUIAllTests.suite());
		return suiteAll;
	}

}
