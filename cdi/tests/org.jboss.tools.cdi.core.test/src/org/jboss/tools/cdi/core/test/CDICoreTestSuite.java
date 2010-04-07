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
package org.jboss.tools.cdi.core.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.tools.cdi.core.test.tck.BeanDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.DefaultNamedTest;
import org.jboss.tools.cdi.core.test.tck.EnterpriseQualifierDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.EnterpriseScopeDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.NameDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.QualifierDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.ScopeDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.StereotypeDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.ValidationTest;

/**
 * @author Alexey Kazakov
 */
public class CDICoreTestSuite extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("CDI Core Tests");
		suite.addTestSuite(BeanDefinitionTest.class);
		suite.addTestSuite(NameDefinitionTest.class);
		suite.addTestSuite(QualifierDefinitionTest.class);
		suite.addTestSuite(EnterpriseQualifierDefinitionTest.class);
		suite.addTestSuite(ScopeDefinitionTest.class);
		suite.addTestSuite(EnterpriseScopeDefinitionTest.class);
		suite.addTestSuite(StereotypeDefinitionTest.class);
		suite.addTestSuite(DefaultNamedTest.class);
		suite.addTestSuite(ValidationTest.class);
		return suite;
	}
}