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
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.batch.ui.hyperlink.BatchHyperlinkDetector;
import org.jboss.tools.common.text.ext.hyperlink.OpenJavaElementHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xml.XMLJumpToHyperlink;
import org.jboss.tools.jst.jsp.test.openon.HyperlinkTestUtil;
import org.jboss.tools.jst.jsp.test.openon.HyperlinkTestUtil.TestHyperlink;
import org.jboss.tools.jst.jsp.test.openon.HyperlinkTestUtil.TestRegion;

public class BatchHyperlinkDetectorTest  extends TestCase {
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
	
	public BatchHyperlinkDetectorTest() {
		super("BatchHyperlinkDetector Test"); //$NON-NLS-1$
	}
	
	public void testDetector() throws Exception{
		checkHyperlinkDetector("src/META-INF/batch-jobs/job-openon-1.xml"); //$NON-NLS-1$
	}
	
	public void testHyperlinks() throws Exception{
		checkHyperlinks("src/META-INF/batch-jobs/job-openon-1.xml"); //$NON-NLS-1$
	}
	
	public void testHyperlinksInExternalFile() throws Exception{
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		
		regionList.add(new TestRegion("myBatchletStep2", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(XMLJumpToHyperlink.class, "Go to '<step id=\"myBatchletStep2\">'") //$NON-NLS-1$
		}));
		
		regionList.add(new TestRegion("ref=", new TestHyperlink[]{}));

		regionList.add(new TestRegion("myBatchletStep3", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(XMLJumpToHyperlink.class, "Go to '<step id=\"myBatchletStep3\">'") //$NON-NLS-1$
		}));

		regionList.add(new TestRegion("myBatchletStep2", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(XMLJumpToHyperlink.class, "Go to '<step id=\"myBatchletStep2\">'") //$NON-NLS-1$
		}));
		
		
		HyperlinkTestUtil.checkRegionsForExternalFile("src/org/jboss/tools/batch/ui/itest/job-openon-1.xml", regionList, new BatchHyperlinkDetector());
	}
	
	private void checkHyperlinkDetector(String pageName) throws Exception{
		List<TestRegion> regionList = getTestRegionList();
		HyperlinkTestUtil.checkRegionsInTextEditor(project, pageName, regionList, new BatchHyperlinkDetector());
	}
	
	private void checkHyperlinks(String pageName) throws Exception{
		List<TestRegion> regionList = getTestRegionList();
		
		HyperlinkTestUtil.checkRegionsInTextEditor(project, pageName, regionList, new BatchHyperlinkDetector());
	}

	private List<TestRegion> getTestRegionList(){
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		
		regionList.add(new TestRegion("myBatchletStep2", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(XMLJumpToHyperlink.class, "Go to '<step id=\"myBatchletStep2\">'") //$NON-NLS-1$
		}));
		
		regionList.add(new TestRegion("ref=", new TestHyperlink[]{}));

		regionList.add(new TestRegion("batchlet1", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(OpenJavaElementHyperlink.class, "Open Class 'batch.MyBatchlet'") //$NON-NLS-1$
		}));
		
		regionList.add(new TestRegion("worktime", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(OpenJavaElementHyperlink.class, "Open Field 'batch.MyBatchlet.time'") //$NON-NLS-1$
		}));

		regionList.add(new TestRegion("myMapper", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(OpenJavaElementHyperlink.class, "Open Class 'batchlib.MyMapper'") //$NON-NLS-1$
		}));

		regionList.add(new TestRegion("myCollector", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(OpenJavaElementHyperlink.class, "Open Class 'batchlib.MyCollector'") //$NON-NLS-1$
		}));

		regionList.add(new TestRegion("myAnalyzer", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(OpenJavaElementHyperlink.class, "Open Class 'batchlib.MyAnalyzer'") //$NON-NLS-1$
		}));

		regionList.add(new TestRegion("myReducer", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(OpenJavaElementHyperlink.class, "Open Class 'batchlib.MyReducer'") //$NON-NLS-1$
		}));
		
		regionList.add(new TestRegion("myBatchletStep3", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(XMLJumpToHyperlink.class, "Go to '<step id=\"myBatchletStep3\">'") //$NON-NLS-1$
		}));

		regionList.add(new TestRegion("myBatchletStep2", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(XMLJumpToHyperlink.class, "Go to '<step id=\"myBatchletStep2\">'") //$NON-NLS-1$
		}));
		
		regionList.add(new TestRegion("batchlet1", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(OpenJavaElementHyperlink.class, "Open Class 'batch.MyBatchlet'") //$NON-NLS-1$
		}));

		regionList.add(new TestRegion("batchlet1", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(OpenJavaElementHyperlink.class, "Open Class 'batch.MyBatchlet'") //$NON-NLS-1$
		}));

		regionList.add(new TestRegion("myAnalyzer", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(OpenJavaElementHyperlink.class, "Open Class 'batchlib.MyAnalyzer'") //$NON-NLS-1$
		}));

		regionList.add(new TestRegion("myReducer", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(OpenJavaElementHyperlink.class, "Open Class 'batchlib.MyReducer'") //$NON-NLS-1$
		}));

		regionList.add(new TestRegion("myMapper", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(OpenJavaElementHyperlink.class, "Open Class 'batchlib.MyMapper'") //$NON-NLS-1$
		}));

		regionList.add(new TestRegion("myCollector", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(OpenJavaElementHyperlink.class, "Open Class 'batchlib.MyCollector'") //$NON-NLS-1$
		}));
		
		return regionList;
	}
	
	


}
