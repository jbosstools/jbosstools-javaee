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
public class Seam211GAFacetInstallDelegateTest extends Seam2FacetInstallDelegateTest {

	public Seam211GAFacetInstallDelegateTest(String name) {
		super(name);
	}

	@Override
	protected Set<String> getEarWarLibs() {
		Set<String> onlyInWar = new HashSet<String>();

		onlyInWar.add("commons-digester.jar");
		onlyInWar.add("jboss-seam-debug.jar");
		onlyInWar.add("jboss-seam-ioc.jar");
		onlyInWar.add("jboss-seam-mail.jar");
		onlyInWar.add("jboss-seam-pdf.jar");
		onlyInWar.add("jboss-seam-ui.jar");
		onlyInWar.add("jsf-facelets.jar");
		onlyInWar.add("richfaces-impl.jar");
		onlyInWar.add("richfaces-ui.jar");
		onlyInWar.add("itext.jar");
		onlyInWar.add("jfreechart.jar");
		onlyInWar.add("jcommon.jar");
		onlyInWar.add("jboss-seam-excel.jar");
		onlyInWar.add("itext-rtf.jar");
		onlyInWar.add("jxl.jar");

		return onlyInWar;
	}

	@Override
	protected Set<String> getWarLibs() {
		Set<String> seamgenlibs = new HashSet<String>();

		seamgenlibs.add("antlr-runtime.jar");
		seamgenlibs.add("commons-beanutils.jar");
		seamgenlibs.add("commons-digester.jar");
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
		seamgenlibs.add("mvel14.jar");
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