/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.core.test.project.facet;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Alexey Kazakov
 */
public class Seam20XFacetTestSuite220CR1 extends Seam20XFacetTestSuite201GA {

	public static Test suite() {
		TestSuite suite = new TestSuite("Seam 2.2.* tests");
		suite.addTest(new Seam2FacetInstallDelegateTestSetup(new TestSuite(Seam220CR1FacetInstallDelegateTest.class)));
		return suite;
	}
}