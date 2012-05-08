/*******************************************************************************
 * Copyright (c) 2010-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.seam3.bot.test;

import org.jboss.tools.cdi.bot.test.AbstractTestSuite;
import org.jboss.tools.cdi.seam3.bot.test.tests.DefaultBeansTest;
import org.jboss.tools.cdi.seam3.bot.test.tests.ExactAnnotationTest;
import org.jboss.tools.cdi.seam3.bot.test.tests.FullyQualifiedTest;
import org.jboss.tools.cdi.seam3.bot.test.tests.GenericOpenOnTest;
import org.jboss.tools.cdi.seam3.bot.test.tests.NamedPackagesTest;
import org.jboss.tools.cdi.seam3.bot.test.tests.RequiresAnnotationTest;
import org.jboss.tools.cdi.seam3.bot.test.tests.ResourceOpenOnTest;
import org.jboss.tools.cdi.seam3.bot.test.tests.VetoAnnotationTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Suite duration: aprox. 18min
 * 
 * @author Jaroslav Jankovic
 */
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({	
	ResourceOpenOnTest.class,
	GenericOpenOnTest.class,
	DefaultBeansTest.class,
	ExactAnnotationTest.class,
	VetoAnnotationTest.class,
	RequiresAnnotationTest.class,
	NamedPackagesTest.class,
	FullyQualifiedTest.class
	})
public class CDISeam3AllBotTests extends AbstractTestSuite {
	
}
