/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.ui.itest;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.batch.internal.core.el.JobPropertiesELCompletionEngine;
import org.jboss.tools.batch.ui.hyperlink.BatchHyperlinkDetector;
import org.jboss.tools.common.text.ext.hyperlink.OpenJavaElementHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xml.XMLJumpToHyperlink;
import org.jboss.tools.jst.jsp.test.openon.HyperlinkTestUtil;
import org.jboss.tools.jst.jsp.test.openon.HyperlinkTestUtil.TestHyperlink;
import org.jboss.tools.jst.jsp.test.openon.HyperlinkTestUtil.TestRegion;
import org.jboss.tools.jst.web.ui.internal.text.ext.hyperlink.ELHyperlink;
import org.jboss.tools.jst.web.ui.internal.text.ext.hyperlink.ELHyperlinkDetector;

public class BatchELHyperlinkTest  extends TestCase {
	private static final String PROJECT_NAME = "BatchTestProject"; //$NON-NLS-1$
	
	public IProject project = null;

	protected void setUp() {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				PROJECT_NAME);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}
	
	protected void tearDown() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}
	
	public BatchELHyperlinkTest() {
		super("Batch ELHyperlinkDetector Test"); //$NON-NLS-1$
	}
	
	public void testDetector() throws Exception{
		checkHyperlinkDetector("src/META-INF/batch-jobs/job-ca-4.xml"); //$NON-NLS-1$
	}
	
	public void testHyperlinks() throws Exception{
		checkHyperlinks("src/META-INF/batch-jobs/job-ca-4.xml"); //$NON-NLS-1$
	}
	
	private void checkHyperlinkDetector(String pageName) throws Exception{
		List<TestRegion> regionList = getTestRegionList();
		HyperlinkTestUtil.checkRegionsInTextEditor(project, pageName, regionList, new ELHyperlinkDetector());
	}
	
	private void checkHyperlinks(String pageName) throws Exception{
		List<TestRegion> regionList = getTestRegionList();
		
		HyperlinkTestUtil.checkHyperlinksInTextEditor(project, pageName, regionList, new ELHyperlinkDetector());
	}

	private List<TestRegion> getTestRegionList(){
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		
		String message = NLS.bind(JobPropertiesELCompletionEngine.GO_TO_PROPERTY_AT, new Object[]{"p1", 2, 18});
		regionList.add(new TestRegion("'p1'", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(ELHyperlink.class, message) //$NON-NLS-1$
		}));
		
		
		return regionList;
	}
	
	


}
