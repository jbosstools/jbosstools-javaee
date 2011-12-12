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
package org.jboss.tools.cdi.core.test.tck.validation;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 * See https://issues.jboss.org/browse/JBIDE-10187
 */
public class SuppressWarningsTests extends ValidationTest {

	public void testLegalTypesInTyped() throws Exception {
		IFile petShopFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/PetShop.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(petShopFile, CDIValidationMessages.ILLEGAL_TYPE_IN_TYPED_DECLARATION, 25);
	}
}