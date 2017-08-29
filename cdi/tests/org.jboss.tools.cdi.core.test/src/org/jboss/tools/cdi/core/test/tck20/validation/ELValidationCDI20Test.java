/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.tck20.validation;

import org.jboss.tools.cdi.core.test.tck.ITCKProjectNameProvider;
import org.jboss.tools.cdi.core.test.tck.validation.ELValidationTest;
import org.jboss.tools.cdi.core.test.tck20.TCK20ProjectNameProvider;


public class ELValidationCDI20Test extends ELValidationTest {

	@Override
	public ITCKProjectNameProvider getProjectNameProvider() {
		return new TCK20ProjectNameProvider();
	}
}