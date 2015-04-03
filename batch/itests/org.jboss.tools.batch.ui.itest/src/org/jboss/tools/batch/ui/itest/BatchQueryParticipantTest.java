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

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.batch.ui.participants.BatchArtifactSearchParticipant;
import org.jboss.tools.common.base.test.kb.QueryParticipantTestUtils;
import org.jboss.tools.common.base.test.kb.QueryParticipantTestUtils.MatchStructure;

public class BatchQueryParticipantTest extends TestCase {
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
	
	public BatchQueryParticipantTest() {
		super("Batch Search Participants Test"); //$NON-NLS-1$
	}

	public void testBatchArtifactSearch() throws CoreException{
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure("/"+PROJECT_NAME+"/src/META-INF/batch-jobs/job-search.xml", "searchableBatchlet"));
		matches.add(new MatchStructure("/"+PROJECT_NAME+"/src/META-INF/batch-jobs/job-search.xml", "searchableBatchlet"));
		
		QueryParticipantTestUtils.testSearchParticipant(project,
				"src/batch/SearchableBatchlet.java",
				QueryParticipantTestUtils.TYPE_SEARCH,
				"SearchableBatchlet",
				"",
				new BatchArtifactSearchParticipant(),
				matches);
	}

	public void testWriterSearch() throws CoreException{
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure("/"+PROJECT_NAME+"/src/META-INF/batch-jobs/job-search.xml", "rewriter"));
		
		QueryParticipantTestUtils.testSearchParticipant(project,
				"src/batch/SearchableWriter.java",
				QueryParticipantTestUtils.TYPE_SEARCH,
				"SearchableWriter",
				"",
				new BatchArtifactSearchParticipant(),
				matches);
	}
	
	public void testBatchArtifactPropertySearch() throws CoreException{
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure("/"+PROJECT_NAME+"/src/META-INF/batch-jobs/job-search.xml", "secondName"));
		
		QueryParticipantTestUtils.testSearchParticipant(project,
				"src/batch/SearchableBatchlet.java",
				QueryParticipantTestUtils.FIELD_SEARCH,
				"otherName",
				"",
				new BatchArtifactSearchParticipant(),
				matches);
	}
	
	public void testClassSearch() throws CoreException{
		ArrayList<MatchStructure> matches = new ArrayList<MatchStructure>();
		
		matches.add(new MatchStructure("/"+PROJECT_NAME+"/src/META-INF/batch-jobs/job-search.xml", "batch.SearchableException"));
		matches.add(new MatchStructure("/"+PROJECT_NAME+"/src/META-INF/batch-jobs/job-search.xml", "batch.SearchableException"));
		matches.add(new MatchStructure("/"+PROJECT_NAME+"/src/META-INF/batch-jobs/job-search.xml", "batch.SearchableException"));
		
		QueryParticipantTestUtils.testSearchParticipant(project,
				"src/batch/SearchableException.java",
				QueryParticipantTestUtils.TYPE_SEARCH,
				"SearchableException",
				"",
				new BatchArtifactSearchParticipant(),
				matches);
	}
	
	
}
