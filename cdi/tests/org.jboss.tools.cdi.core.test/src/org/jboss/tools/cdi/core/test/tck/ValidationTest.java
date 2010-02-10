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
		cleanProject("/lookup/typesafe/resolution");
	}

	/*
	 * 	 2.7.1.3. Stereotype declares a non-empty @Named annotation (Non-Portable behavior)
	 */
	public void testNonEmptyNamedForStereotype() throws Exception {
		IProject p = importPreparedProject("/definition/stereotype");
		IFile file = p.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/stereotype/broken/nonEmptyNamed/StereotypeWithNonEmptyNamed_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, "Stereotype declares a non-empty @Named annotation", 15);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("StereotypeWithNonEmptyNamed_Broken.java should has the only error marker.", markerNumbers, 1);
		cleanProject("/definition/stereotype");
	}

	/*
	 * 	 2.7.1.3. Stereotype declares any other qualifier annotation
	 */
	public void testAnnotatedStereotype() throws Exception {
		IProject p = importPreparedProject("/definition/stereotype");
		IFile file = p.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/stereotype/broken/withBindingType/StereotypeWithBindingTypes_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, "Stereotype declares any qualifier annotation other than @Named", 14);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("StereotypeWithBindingTypes_Broken.java should has the only error marker.", markerNumbers, 1);
		cleanProject("/definition/stereotype");
	}

	/*
	 * 	 2.7.1.3. Stereotype is annotated @Typed
	 */
	public void testTypedStereotype() throws Exception {
		IProject p = importPreparedProject("/definition/stereotype");
		IFile file = p.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/stereotype/broken/withBindingType/StereotypeWithTyped_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, "Stereotype is annotated @Typed", 15);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("StereotypeWithTyped_Broken.java should has the only error marker.", markerNumbers, 1);
		cleanProject("/definition/stereotype");
	}

	/*
	 *  2.4.3. Declaring the bean scope
	 *         - bean class or producer method or field specifies multiple scope type annotations
	 */
	public void testBeanScope() throws Exception {
		IProject p = importPreparedProject("/definition/scope");
		IFile file = p.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/scope/broken/tooManyScopes/BeanWithTooManyScopeTypes_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, "Bean class or producer method or field specifies multiple scope type annotations", 6, 7);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("StereotypeWithTyped_Broken.java should has two error markers.", markerNumbers, 2);
		cleanProject("/definition/scope");
	}

	public static int getMarkersNumber(IResource resource) {
		return AbstractResourceMarkerTest.getMarkersNumberByGroupName(resource, null);
	}
}