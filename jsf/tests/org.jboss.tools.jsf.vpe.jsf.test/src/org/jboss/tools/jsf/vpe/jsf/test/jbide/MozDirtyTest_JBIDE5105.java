/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;

/**
 * Tests for _MOZ_DIRTY tags.
 * See <a href="https://jira.jboss.org/jira/browse/JBIDE-5105">JBIDE-5105</a>.
 * 
 * @author yradtsevich
 *
 */
public class MozDirtyTest_JBIDE5105 extends VpeTest {
	private static final String MOZ_DIRTY = "_MOZ_DIRTY";		//$NON-NLS-1$
	private static final String TEST_PAGE_NAME
			= "JBIDE/5105/BrMozDirty.html";						//$NON-NLS-1$

	public MozDirtyTest_JBIDE5105(String name) {
		super(name);
	}

	/**
	 * Test if there are no BR tags with _MOZ_DIRTY attribute.
	 * See
	 * <a href="https://jira.jboss.org/jira/browse/JBIDE-5105">JBIDE-5105</a>.
	 * 
	 * @throws Throwable
	 */
	public void testBrMozDirty() throws Throwable {
		VpeController vpeController = openInVpe(JsfAllTests.IMPORT_PROJECT_NAME,
				TEST_PAGE_NAME);

		TestUtil.waitForIdle();
		assertFalse(MOZ_DIRTY + " element detected before refresh.",//$NON-NLS-1$
				hasBrMozDirty(vpeController));

		vpeController.visualRefresh();
		TestUtil.waitForIdle();
		assertFalse(MOZ_DIRTY + " element detected after refresh.", //$NON-NLS-1$
				hasBrMozDirty(vpeController));
	}

	private boolean hasBrMozDirty(VpeController vpeController) {
		nsIDOMNodeList brNodeList = vpeController.getXulRunnerEditor()
				.getDOMDocument().getElementsByTagName(HTML.TAG_BR);
		for (int i = 0; i < brNodeList.getLength(); i++) {
			nsIDOMNode brNode = brNodeList.item(i);
			if (brNode.getAttributes().getNamedItem(MOZ_DIRTY) != null) {
				return true;
			}
		}
		return false;
	}
}
