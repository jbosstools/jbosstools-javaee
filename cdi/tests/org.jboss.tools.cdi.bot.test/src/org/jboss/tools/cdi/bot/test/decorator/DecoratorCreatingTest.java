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
package org.jboss.tools.cdi.bot.test.decorator;

import org.jboss.tools.cdi.bot.test.CDITestBase;

/**
 * Test operates on creating new decorator 
 * 
 * @author Jaroslav Jankovic
 * 
 */

public class DecoratorCreatingTest extends CDITestBase {
	
	//https://issues.jboss.org/browse/JBIDE-3136
	
	@Override
	public String getProjectName() {
		return "CDIDecoratorCreating";
	}
	
	

}
