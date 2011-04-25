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
package org.jboss.tools.cdi.seam.faces.core.test;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.jboss.tools.jst.web.kb.internal.proposal.CustomProposalType;
import org.jboss.tools.jst.web.kb.internal.proposal.EnumerationProposalType;
import org.jboss.tools.jst.web.kb.internal.taglib.CustomTagLibAttribute;
import org.jboss.tools.jst.web.kb.taglib.CustomTagLibManager;
import org.jboss.tools.jst.web.kb.taglib.IAttribute;
import org.jboss.tools.jst.web.kb.taglib.IComponent;
import org.jboss.tools.jst.web.kb.taglib.ICustomTagLibrary;

/**
 * @author Alexey Kazakov
 */
public class SeamFacesTagLibTest extends TestCase {

	public void testCustomTagLibs() {
		ICustomTagLibrary[] libs = CustomTagLibManager.getInstance().getLibraries();
		boolean found = false;
		for (ICustomTagLibrary lib : libs) {
			if("http://jboss.org/seam/faces".equals(lib.getURI())) {
				found = true;
				IComponent component = lib.getComponent("viewAction");
				assertNotNull(component);
				IAttribute attribute = component.getAttribute("phase");
				assertNotNull(attribute);
				assertTrue(attribute instanceof CustomTagLibAttribute);
				CustomTagLibAttribute customAttribute = (CustomTagLibAttribute)attribute;
				CustomProposalType[] proposals = customAttribute.getProposals();
				Set<EnumerationProposalType> enums = new HashSet<EnumerationProposalType>();
				for (CustomProposalType proposal : proposals) {
					if(proposal instanceof EnumerationProposalType) {
						enums.add((EnumerationProposalType)proposal);
					}
				}
				assertEquals(1, enums.size());
				assertEquals(4, enums.iterator().next().getParams().length);
				break;
			}
		}
		assertTrue("Can't find http://jboss.org/seam/faces tag lib.", found);
	}
}