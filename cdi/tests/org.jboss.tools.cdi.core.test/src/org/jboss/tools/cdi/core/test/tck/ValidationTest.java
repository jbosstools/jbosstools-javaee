/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.tck;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class ValidationTest extends TCKTest {

	public void testLegalTypesInTyped() throws Exception {
		IProject p = importPreparedProject("/lookup/typesafe/resolution");
		IFile petShopFile = p.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/PetShop.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(petShopFile, AbstractResourceMarkerTest.MARKER_TYPE, "Bean class or producer method or field specifies a @Typed annotation, and the value member specifies a class which does not correspond to a type in the unrestricted set of bean types of a bean", 9);
		int markerNumbers = getMarkersNumber(petShopFile);
		assertEquals("PetShop.java should has the only error marker.", markerNumbers, 1);
		// TODO
		cleanProject("/lookup/typesafe/resolution");
	}

	public static int getMarkersNumber(IResource resource) {
		return AbstractResourceMarkerTest.getMarkersNumberByGroupName(resource, null);
	}
}