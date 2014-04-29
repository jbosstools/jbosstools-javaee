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
package org.jboss.tools.cdi.seam.text.ext.test;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.seam.text.ext.CDISeamExtMessages;
import org.jboss.tools.cdi.seam.text.ext.hyperlink.CDISeamResourceLoadingHyperlink;
import org.jboss.tools.cdi.seam.text.ext.hyperlink.CDISeamResourceLoadingHyperlinkDetector;
import org.jboss.tools.cdi.text.ext.test.CDIHyperlinkTestUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.cdi.text.ext.test.CDIHyperlinkTestUtil.TestHyperlink;
import org.jboss.tools.cdi.text.ext.test.CDIHyperlinkTestUtil.TestRegion;

public class CDISeamResourceLoadingHyperlinkDetectorTest extends TCKTest{
	private static final String INJECT_STRING = "@Inject";
	private static final String BEANS_XML_STRING = "beansXml;";
	private static final String PROPERTIES1_STRING = "properties1;";
	private static final String PROPERTIES2_STRING = "properties2;";
	private static final String PROPERTIES3_STRING = "properties3;";
	private static final String PROPERTIES4_STRING = "properties4;";
	
	
	public void testCDISeamResourceLoadingHyperlinkDetector_Solder30() throws Exception {
		checkFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/ResourceLoader30.java");
	}

	public void testCDISeamResourceLoadingHyperlinkDetector_Solder31() throws Exception {
		checkFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/ResourceLoader31.java");
	}
	
	private void checkFile(String fileName) throws Exception{
		IFile file = tckProject.getFile(fileName);
		String text = FileUtil.readStream(file);
		
		
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		
		int injectPosition = text.indexOf(INJECT_STRING);
		int length = text.indexOf(BEANS_XML_STRING, injectPosition)+BEANS_XML_STRING.length()-injectPosition;
		regionList.add(new TestRegion(injectPosition, length,
			new TestHyperlink[]{new TestHyperlink(CDISeamResourceLoadingHyperlink.class, NLS.bind(CDISeamExtMessages.CDI_SEAM_RESOURCE_LOADING_HYPERLINK,
							"WEB-INF/beans.xml",
							"WebContent/WEB-INF/beans.xml"))}));
		
		injectPosition = text.indexOf(INJECT_STRING,injectPosition+1);
		length = text.indexOf(PROPERTIES1_STRING, injectPosition)+PROPERTIES1_STRING.length()-injectPosition;
		regionList.add(new TestRegion(injectPosition, length,
			new TestHyperlink[]{new TestHyperlink(CDISeamResourceLoadingHyperlink.class, NLS.bind(CDISeamExtMessages.CDI_SEAM_RESOURCE_LOADING_HYPERLINK,
							"org/jboss/jsr299/tck/tests/jbt/openon/test.properties",
							"JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/test.properties"))}));
		
		injectPosition = text.indexOf(INJECT_STRING,injectPosition+1);
		length = text.indexOf(PROPERTIES2_STRING, injectPosition)+PROPERTIES2_STRING.length()-injectPosition;
		regionList.add(new TestRegion(injectPosition, length,
			new TestHyperlink[]{new TestHyperlink(CDISeamResourceLoadingHyperlink.class, NLS.bind(CDISeamExtMessages.CDI_SEAM_RESOURCE_LOADING_HYPERLINK,
							"org/jboss/jsr299/tck/tests/jbt/openon/test",
							"JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/test.properties"))}));
		
		injectPosition = text.indexOf(INJECT_STRING,injectPosition+1);
		length = text.indexOf(PROPERTIES3_STRING, injectPosition)+PROPERTIES3_STRING.length()-injectPosition;
		regionList.add(new TestRegion(injectPosition, length,
			new TestHyperlink[]{new TestHyperlink(CDISeamResourceLoadingHyperlink.class, NLS.bind(CDISeamExtMessages.CDI_SEAM_RESOURCE_LOADING_HYPERLINK,
							"org.jboss.jsr299.tck.tests.jbt.openon.test.properties",
							"JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/test.properties"))}));
		
		injectPosition = text.indexOf(INJECT_STRING,injectPosition+1);
		length = text.indexOf(PROPERTIES4_STRING, injectPosition)+PROPERTIES4_STRING.length()-injectPosition;
		regionList.add(new TestRegion(injectPosition, length,
			new TestHyperlink[]{new TestHyperlink(CDISeamResourceLoadingHyperlink.class, NLS.bind(CDISeamExtMessages.CDI_SEAM_RESOURCE_LOADING_HYPERLINK,
							"org.jboss.jsr299.tck.tests.jbt.openon.test",
							"JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/test.properties"))}));
		 
		CDIHyperlinkTestUtil.checkRegions(tckProject, fileName, regionList, new CDISeamResourceLoadingHyperlinkDetector());
	}

}
