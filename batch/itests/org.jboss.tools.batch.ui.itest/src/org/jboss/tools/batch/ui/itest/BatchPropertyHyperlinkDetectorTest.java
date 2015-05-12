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
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.batch.core.IBatchArtifact;
import org.jboss.tools.batch.core.IBatchProperty;
import org.jboss.tools.batch.internal.core.impl.BatchProject;
import org.jboss.tools.batch.internal.core.impl.BatchProjectFactory;
import org.jboss.tools.batch.internal.core.impl.BatchUtil.NodePathTextSourceReference;
import org.jboss.tools.batch.ui.hyperlink.BatchHyperlinkDetector;
import org.jboss.tools.batch.ui.hyperlink.BatchHyperlinkMessages;
import org.jboss.tools.batch.ui.hyperlink.BatchPropertyDialog;
import org.jboss.tools.batch.ui.hyperlink.BatchPropertyHyperlink;
import org.jboss.tools.batch.ui.hyperlink.BatchPropertyHyperlinkDetector;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.text.ext.hyperlink.OpenJavaElementHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xml.XMLJumpToHyperlink;
import org.jboss.tools.common.util.EclipseJavaUtil;
import org.jboss.tools.jst.jsp.test.openon.HyperlinkTestUtil;
import org.jboss.tools.jst.jsp.test.openon.HyperlinkTestUtil.TestHyperlink;
import org.jboss.tools.jst.jsp.test.openon.HyperlinkTestUtil.TestRegion;

public class BatchPropertyHyperlinkDetectorTest  extends TestCase {
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
	
	public BatchPropertyHyperlinkDetectorTest() {
		super("BatchHyperlinkDetector Test"); //$NON-NLS-1$
	}
	
	public void testDetector() throws Exception{
		checkHyperlinkDetector("src/batch/SearchableBatchlet.java"); //$NON-NLS-1$
	}
	
	public void testBatchPropertyDialog() throws CoreException {
		String path = "src/batch/SearchableBatchlet.java";
		IFile file = project.getFile(path);
		assertTrue(file.exists());
		
		BatchProject batchProject = (BatchProject) BatchProjectFactory.getBatchProjectWithProgress(project);
		
		assertNotNull(batchProject);
		
		IJavaProject javaProject = EclipseResourceUtil.getJavaProject(project);
		assertNotNull(javaProject);
		
		IType type = EclipseJavaUtil.findType(javaProject, "batch.SearchableBatchlet");
		assertNotNull(type);
		
		IField field = EclipseJavaUtil.findField(type, "otherName");
		assertNotNull(field);
		
		IBatchArtifact artifact = batchProject.getArtifact(type);
		assertNotNull(artifact);
		
		IBatchProperty batchProperty = artifact.getProperty(field);
		assertNotNull(batchProperty);
		
		Display display = Display.getCurrent();
		if(display == null) {
			display = Display.getDefault();
		}
		BatchPropertyDialog dialog = new BatchPropertyDialog(display.getActiveShell(), batchProperty);
		dialog.setBlockOnOpen(false);
		dialog.open();
		
		HashMap<IFile, List<NodePathTextSourceReference>> references = dialog.getDisplayedReferences();
		
		IFile jobXMLFile = project.getFile("src/META-INF/batch-jobs/job-search.xml");
		assertTrue(jobXMLFile.exists());
		
		List<NodePathTextSourceReference> list = references.get(jobXMLFile);
		assertNotNull(list);
		
		assertEquals(1, list.size());
		
		NodePathTextSourceReference reference = list.get(0);
		assertNotNull(reference);
	}
	
	private void checkHyperlinkDetector(String pageName) throws Exception{
		List<TestRegion> regionList = getTestRegionList();
		HyperlinkTestUtil.checkRegions(project, pageName, regionList, new BatchPropertyHyperlinkDetector());
	}
	
	private List<TestRegion> getTestRegionList(){
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		
		regionList.add(new TestRegion("SearchableBatchlet", new TestHyperlink[]{}));
		
		regionList.add(new TestRegion("Inject", new TestHyperlink[]{ //$NON-NLS-1$
				new TestHyperlink(BatchPropertyHyperlink.class, "Show All Batch Property References") //$NON-NLS-1$
			}));
		
		regionList.add(new TestRegion("BatchProperty", new TestHyperlink[]{ //$NON-NLS-1$
			new TestHyperlink(BatchPropertyHyperlink.class, "Show All Batch Property References") //$NON-NLS-1$
		}));
		
		regionList.add(new TestRegion("String", new TestHyperlink[]{ //$NON-NLS-1$
				new TestHyperlink(BatchPropertyHyperlink.class, BatchHyperlinkMessages.SHOW_ALL_BATCH_PROPERTY_REFERENCES) //$NON-NLS-1$
		}));
		
		regionList.add(new TestRegion("otherName", new TestHyperlink[]{ //$NON-NLS-1$
				new TestHyperlink(BatchPropertyHyperlink.class, BatchHyperlinkMessages.SHOW_ALL_BATCH_PROPERTY_REFERENCES) //$NON-NLS-1$
		}));
		
		return regionList;
	}
	
	


}
