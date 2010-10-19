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
package org.jboss.tools.seam.core.test.project.facet;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexey Kazakov
 */
public class Seam220CR1FacetInstallDelegateTest extends Seam2FacetInstallDelegateTest {

	public Seam220CR1FacetInstallDelegateTest(String name) {
		super(name);
	}

	@Override
	protected Set<String> getEarLibs() {
		Set<String> onlyInEar = new HashSet<String>();

		onlyInEar.add("commons-beanutils.jar");
		onlyInEar.add("antlr-runtime.jar");
		onlyInEar.add("drools-api.jar");
		onlyInEar.add("drools-compiler.jar");
		onlyInEar.add("drools-core.jar");
		onlyInEar.add("jboss-el.jar");
		onlyInEar.add("jboss-seam-remoting.jar");
		onlyInEar.add("mvel2.jar");
		onlyInEar.add("richfaces-api.jar");
		onlyInEar.add("jbpm-jpdl.jar");

		return onlyInEar;
	}

	@Override
	protected Set<String> getWarLibs() {
		Set<String> seamgenlibs = new HashSet<String>();

		seamgenlibs.add("antlr-runtime.jar");
		seamgenlibs.add("commons-beanutils.jar");
		seamgenlibs.add("commons-digester.jar");
		seamgenlibs.add("drools-api.jar");
		seamgenlibs.add("drools-compiler.jar");
		seamgenlibs.add("drools-core.jar");
		seamgenlibs.add("core.jar");
		seamgenlibs.add("jboss-el.jar");
		seamgenlibs.add("jboss-seam-debug.jar");
		seamgenlibs.add("jboss-seam-ioc.jar");
		seamgenlibs.add("jboss-seam.jar");
		seamgenlibs.add("jboss-seam-mail.jar");
		seamgenlibs.add("jboss-seam-pdf.jar");
		seamgenlibs.add("jboss-seam-remoting.jar");
		seamgenlibs.add("jboss-seam-ui.jar");
		seamgenlibs.add("jbpm-jpdl.jar");
		seamgenlibs.add("jsf-facelets.jar");
		seamgenlibs.add("mvel2.jar");
		seamgenlibs.add("richfaces-api.jar");
		seamgenlibs.add("richfaces-impl.jar");
		seamgenlibs.add("richfaces-ui.jar");
		seamgenlibs.add("itext.jar");
		seamgenlibs.add("jfreechart.jar");
		seamgenlibs.add("jcommon.jar");
		seamgenlibs.add("jboss-seam-excel.jar");
		seamgenlibs.add("itext-rtf.jar");
		seamgenlibs.add("jxl.jar");

		return seamgenlibs;
	}
}