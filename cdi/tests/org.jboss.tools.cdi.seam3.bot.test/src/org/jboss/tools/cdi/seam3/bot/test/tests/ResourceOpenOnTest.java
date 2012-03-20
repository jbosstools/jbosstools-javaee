/*******************************************************************************
 * Copyright (c) 2010-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.seam3.bot.test.tests;


import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.seam3.bot.test.Seam3TestBase;
import org.junit.Test;

/**
 * Test operates on resource openOn in Seam3 using CDI tools
 * 
 * @author Jaroslav Jankovic
 */

public class ResourceOpenOnTest extends Seam3TestBase {

	@Override
	public String getProjectName() {
		return "resource";
	}

	/**
	 * https://issues.jboss.org/browse/JBIDE-8202
	 */	
	@Test
	public void testResourceOpenOn() {
			
		String className = "MyBean.java";
		
		packageExplorer.openFile(getProjectName(), CDIConstants.SRC, 
				"cdi.seam", className);

		openOnUtil.openOnByOption(CDIConstants.RESOURCE_ANNOTATION, className, "Open Resource");
		
		String destinationFile = getEd().getTitle();		
		assertTrue("ERROR: redirected to " + destinationFile,
					destinationFile.equals(CDIConstants.BEANS_XML));

		editResourceUtil.moveFileInProjectExplorer(CDIConstants.BEANS_XML, 
				getProjectName() + "/" + CDIConstants.WEBCONTENT + "/" + CDIConstants.WEB_INF,
				getProjectName() + "/" + CDIConstants.WEBCONTENT + "/" + CDIConstants.META_INF);
		LOGGER.info("bean.xml was moved to META-INF");
		
		setEd(bot.swtBotEditorExtByTitle(className));
		editResourceUtil.replaceInEditor("WEB", "META");
		openOnUtil.openOnByOption(CDIConstants.RESOURCE_ANNOTATION, className, "Open Resource");
		
		destinationFile = getEd().getTitle();
		assertTrue("ERROR: redirected to " + destinationFile,
				   destinationFile.equals(CDIConstants.BEANS_XML));

	}
	
}
	
