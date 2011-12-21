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

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.preferences.CDIPreferences;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.common.el.core.ELReference;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.kb.internal.validation.ELValidationMessages;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Viacheslav Kabanovich
 * See https://issues.jboss.org/browse/JBIDE-10545
 */
public class ELReferenceTest extends TCKTest {

	public void testELReference() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		checkFile(file);
		file = tckProject.getFile("WebContent/tests/jbt/refactoring/HomePage.xhtml");
		checkFile(file);
		file = tckProject.getFile("WebContent/tests/lookup/injection/non/contextual/ManagedBeanTestPage.jsp");
		checkFile(file);
	}

	void checkFile(IFile file) throws Exception {
		assertTrue(file.exists());
		ELContext context = PageContextFactory.getInstance().createPageContext(file);
		ELReference[] els = context.getELReferences();
		assertTrue(els.length > 0);
		for (ELReference el: els) {
			int start = el.getStartPosition();
			int length = el.getLength();
			ELExpression[] exs = el.getEl();
			
			ELReference el1 = new ELReference();
			el1.setStartPosition(start);
			el1.setLength(length);
			el1.setResource(file);
			ELExpression[] exs1 = el1.getEl();
			
			assertEquals(exs.length, exs1.length);
			for (int i = 0; i < exs.length; i++) {
				assertEquals(exs[i].toString(), exs1[i].toString());
			}
		}
	}

}