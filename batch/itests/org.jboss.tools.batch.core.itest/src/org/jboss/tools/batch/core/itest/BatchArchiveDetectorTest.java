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
package org.jboss.tools.batch.core.itest;


import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.batch.internal.core.impl.BatchProject;
import org.jboss.tools.batch.internal.core.scanner.BatchArchiveDetector;

/**
 * @author Viacheslav Kabanovich
 */
public class BatchArchiveDetectorTest extends TestCase {
	protected IProject project;
	protected IBatchProject batchProject;

	@Override
	public void setUp() {
		project =  ResourcesPlugin.getWorkspace().getRoot().getProject("BatchTestProject");
		assertNotNull(project);
		batchProject = BatchCorePlugin.getBatchProject(project, true);
		assertNotNull(batchProject);
		assertTrue(((BatchProject)batchProject).exists());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testBatcArchiveFolder() throws Exception {
		IFolder batchfolder = project.getFolder("lib1/batchfolder");
		assertTrue(batchfolder.exists());
		String path = batchfolder.getLocation().toFile().getCanonicalPath();
		BatchArchiveDetector detector = BatchArchiveDetector.getInstance();
		IBatchProject bp = BatchCorePlugin.getBatchProject(project, false);
		int result = detector.resolve(path, bp);
		assertEquals(BatchArchiveDetector.ARCHIVE, result);
	}

	public void testNotBatcArchiveFolder() throws Exception {
		IFolder batchfolder = project.getFolder("lib1/nobatchfolder");
		assertTrue(batchfolder.exists());
		String path = batchfolder.getLocation().toFile().getCanonicalPath();
		BatchArchiveDetector detector = BatchArchiveDetector.getInstance();
		IBatchProject bp = BatchCorePlugin.getBatchProject(project, false);
		int result = detector.resolve(path, bp);
		assertEquals(BatchArchiveDetector.NOT_ARCHIVE, result);
	}

}
