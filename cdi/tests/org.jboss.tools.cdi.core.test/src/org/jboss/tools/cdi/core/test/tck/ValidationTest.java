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
		IProject p = importPreparedProject("/tests/lookup/typesafe/resolution");
		IFile petShopFile = p.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/PetShop.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(petShopFile, AbstractResourceMarkerTest.MARKER_TYPE, "Bean class or producer method or field specifies a @Typed annotation, and the value member specifies a class which does not correspond to a type in the unrestricted set of bean types of a bean", 25);
		int markerNumbers = getMarkersNumber(petShopFile);
		assertEquals("PetShop.java should has the only error marker.", markerNumbers, 1);
		cleanProject("/lookup/typesafe/resolution");
	}

	/*
	 * 	 2.7.1.3. Stereotype declares a non-empty @Named annotation (Non-Portable behavior)
	 */
	public void testNonEmptyNamedForStereotype() throws Exception {
		IProject p = importPreparedProject("/tests/definition/stereotype");
		IFile file = p.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/stereotype/broken/nonEmptyNamed/StereotypeWithNonEmptyNamed_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, "Stereotype declares a non-empty @Named annotation", 31);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("StereotypeWithNonEmptyNamed_Broken.java should has the only error marker.", markerNumbers, 1);
		cleanProject("/definition/stereotype");
	}

	/*
	 * 	 2.7.1.3. Stereotype declares any other qualifier annotation
	 */
	public void testAnnotatedStereotype() throws Exception {
		IProject p = importPreparedProject("/tests/definition/stereotype");
		IFile file = p.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/stereotype/broken/withBindingType/StereotypeWithBindingTypes_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, "Stereotype declares any qualifier annotation other than @Named", 30);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("StereotypeWithBindingTypes_Broken.java should has the only error marker.", markerNumbers, 1);
		cleanProject("/definition/stereotype");
	}

	/*
	 * 	 2.7.1.3. Stereotype is annotated @Typed
	 */
	public void testTypedStereotype() throws Exception {
		IProject p = importPreparedProject("/tests/definition/stereotype");
		IFile file = p.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/stereotype/broken/withBindingType/StereotypeWithTyped_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, "Stereotype is annotated @Typed", 15);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("StereotypeWithTyped_Broken.java should has the only error marker.", markerNumbers, 1);
		cleanProject("/definition/stereotype");
	}

	/*
	 *  2.7.1.1. Declaring the default scope for a stereotype
	 *           - stereotype declares more than one scope
	 */
	public void testStereotypeScope() throws Exception {
		IProject p = importPreparedProject("/tests/definition/stereotype");
		IFile file = p.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/stereotype/broken/tooManyScopes/StereotypeWithTooManyScopeTypes_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, "Stereotype declares more than one scope", 32, 33);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("StereotypeWithTooManyScopeTypes_Broken.java should has two error markers.", markerNumbers, 2);
		cleanProject("/definition/stereotype");
	}

	/*
	 *  2.4.3. Declaring the bean scope
	 *         - bean class or producer method or field specifies multiple scope type annotations
	 */
	public void testMultipleBeanScope() throws Exception {
		IProject p = importPreparedProject("/tests/definition/scope");
		IFile file = p.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/scope/broken/tooManyScopes/BeanWithTooManyScopeTypes_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, "Bean class or producer method or field specifies multiple scope type annotations", 22, 23);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("StereotypeWithTyped_Broken.java should has two error markers.", markerNumbers, 2);
		cleanProject("/definition/scope");
	}

	/*
	 *  2.4.4. Default scope
	 *         - bean does not explicitly declare a scope when there is no default scope 
	 *         (there are two different stereotypes declared by the bean that declare different default scopes)
	 */
	public void testBeanWithMultipleScopedStereotypes() throws Exception {
		IProject p = importPreparedProject("/tests/definition/stereotype");
		IFile file = p.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/stereotype/broken/scopeConflict/Scallop_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, "Bean does not explicitly declare a scope when there is no default scope", 24, 25);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("Scallop_Broken.java should has two error markers.", markerNumbers, 2);
		cleanProject("/definition/stereotype");
	}

	public static int getMarkersNumber(IResource resource) {
		return AbstractResourceMarkerTest.getMarkersNumberByGroupName(resource, null);
	}
}