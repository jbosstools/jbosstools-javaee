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
package org.jboss.tools.cdi.seam.faces.core.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jdt.internal.core.JavaModelManager;

/**
 * @author Alexey Kazakov
 */
public class CDISeamFacesCoreAllTests {

	public static Test suite() {
		JavaModelManager.getIndexManager().disable();

		TestSuite suite = new TestSuite("Seam Solder Faces Tests");

		suite.addTestSuite(SeamFacesTagLibTest.class);
		return suite;
	}
}