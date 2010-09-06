/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.cdi.core.test.ca;

import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.internal.core.ca.BeansXmlProcessor;
import org.jboss.tools.jst.web.kb.KbQuery;

/**
 * @author Alexey Kazakov
 */
public class BeansXmlCATest extends TCKTest {

	public void testAlternativeBeans() {
		KbQuery query = new KbQuery();
		query.setParentTags(new String[]{"alternatives", "class"});
		query.setValue("");

		BeansXmlProcessor.getInstance().getProposals(query, tckProject);
	}

	public void testAlternativeStereotypes() {
		
	}

	public void testDecorators() {
		
	}

	public void testInterceptors() {
		
	}
}