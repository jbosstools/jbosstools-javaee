/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.core.test.project.facet;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexey Kazakov
 */
public class Seam230FacetInstallDelegateTest extends Seam220CR1FacetInstallDelegateTest {

	public Seam230FacetInstallDelegateTest(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.core.test.project.facet.Seam2FacetInstallDelegateTest#getTestLibs()
	 */
	@Override
	protected Set<String> getTestLibs() {
		Set<String> libs = new HashSet<String>();

		libs.add("testng-jdk15.jar");
		libs.add("hibernate-all.jar");
		libs.add("jboss-embedded-all.jar");
		libs.add("thirdparty-all.jar");
		libs.add("jboss-embedded-api.jar");
		libs.add("core.jar");

		return libs;
	}
}