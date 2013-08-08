/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.ui.test;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.common.ui.CommonUIPlugin;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;

/**
 * @author Alexey Kazakov
 */
public class CATest extends TCKTest {

	private IProject project;
	private ContentAssistantTestCase caTest = new ContentAssistantTestCase();
	private static final String PAGE_NAME = "WebContent/test.jsp";
	private String[] beanProposals = new String[] {"example", "example.com", "fishJBT", "game", "haddock", "salmon", "sheep", "tunaFarm", "whitefishJBT", "wolf"};
	Image beanClassImage = CommonUIPlugin.getImageDescriptorRegistry().get(CDIImages.BEAN_CLASS_IMAGE);
	private Image[] beanImages = new Image[] {beanClassImage, beanClassImage, beanClassImage, beanClassImage, beanClassImage, beanClassImage, beanClassImage, beanClassImage, beanClassImage, beanClassImage};
	private String[] propertyProposals = new String[] {"game.value", "game.initialize()"};
	private Image[] propertyImages = new Image[] {CommonUIPlugin.getImageDescriptorRegistry().get(CDIImages.BEAN_FIELD_IMAGE), CommonUIPlugin.getImageDescriptorRegistry().get(CDIImages.BEAN_METHOD_IMAGE)};

	public void setUp() {
		project = findTestProject();
		caTest.setProject(project);
	}

	public void testEL() {
//		System.out.println("BEAN_CLASS_IMAGE - "+CDIImages.BEAN_CLASS_IMAGE);
//		System.out.println("BEAN_METHOD_IMAGE - "+CDIImages.BEAN_METHOD_IMAGE);
//		System.out.println("BEAN_FIELD_IMAGE - "+CDIImages.BEAN_FIELD_IMAGE);
//		
//		System.out.println("INJECTION_POINT_IMAGE - "+CDIImages.INJECTION_POINT_IMAGE);
//		System.out.println("ANNOTATION_IMAGE - "+CDIImages.ANNOTATION_IMAGE);
//		System.out.println("CDI_EVENT_IMAGE - "+CDIImages.CDI_EVENT_IMAGE);
//		System.out.println("MESSAGE_BUNDLE_IMAGE - "+CDIImages.MESSAGE_BUNDLE_IMAGE);
		
		caTest.checkProposals(PAGE_NAME, "value=\"#{", 9, beanProposals, beanImages, false);
		caTest.checkProposals(PAGE_NAME, "rendered=\"#{(game.", 18, propertyProposals, propertyImages, false);
	}
}