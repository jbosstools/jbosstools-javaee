/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.tck11;

import org.jboss.tools.cdi.core.test.tck.EnterpriseStereotypeDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.ITCKProjectNameProvider;

/**
 * @author Alexey Kazakov
 */
public class EnterpriseStereotypeDefinitionCDI11Test extends EnterpriseStereotypeDefinitionTest {

	@Override
	public ITCKProjectNameProvider getProjectNameProvider() {
		return new TCK11ProjectNameProvider();
	}
}