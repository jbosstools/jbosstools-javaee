package org.jboss.tools.batch.ui.itest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.batch.internal.core.validation.BatchValidator;
import org.jboss.tools.batch.ui.quickfixes.CreateBatchArtifactQuickFix;
import org.jboss.tools.common.base.test.MarkerResolutionTestUtil;
import org.jboss.tools.common.validation.ValidationErrorManager;

import junit.framework.TestCase;

public class BatchQuickFixTest extends TestCase {
	public static final String PROBLEM_TYPE = "org.jboss.tools.batch.core.batchproblem"; //$NON-NLS-1$
	private static final String PROJECT_NAME = "BatchTestProject"; //$NON-NLS-1$
	
	public IProject project = null;

	@Override
	protected void setUp() {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				PROJECT_NAME);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
	}
	
	@Override
	protected void tearDown() throws CoreException{
		org.jboss.tools.test.util.JobUtils.runDeferredEvents();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		String[] filesToDelete = new String[]{
				"src/batch/AnyJobListener.java",
				"src/batch/AnyStepListener.java",
				"src/batch/AnyBatchlet.java",
				"src/batchlib/AnyPartitionMapper.java",
				"src/batchlib/AnyPartitionCollector.java",
				"src/batchlib/AnyPartitionAnalyzer.java",
				"src/batchlib/AnyPartitionReducer.java",
				"src/batch/AnyItemReader.java",
				"src/batch/AnyItemProcessor.java",
				"src/batch/AnyItemWriter.java",
				"src/batch/AnyCheckpointAlgorithm.java",
				"src/batch/AnyDecider.java"
		};
		for(String fileName : filesToDelete){
			IFile file = project.getFile(fileName);
		
			file.delete(true, new NullProgressMonitor());
		
		}
	}
	
	public BatchQuickFixTest() {
		super("Batch Quick Fix Test"); //$NON-NLS-1$
	}
	
	private void checkBatchQuickFix(int quickFixId) throws CoreException{
		MarkerResolutionTestUtil.checkResolution(project,
				new String[]{
					"src/META-INF/batch-jobs/job-quickfix.xml"
				},
				PROBLEM_TYPE,
				ValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				quickFixId,
				CreateBatchArtifactQuickFix.class);
	}

	public void testCreateBatchletQuickFix() throws CoreException{
		checkBatchQuickFix(BatchValidator.BATCHLET_IS_NOT_FOUND_ID);
	}

	public void testCreateJobListenerQuickFix() throws CoreException{
		checkBatchQuickFix(BatchValidator.JOB_LISTENER_IS_NOT_FOUND_ID);
	}

	public void testCreateStepListenerQuickFix() throws CoreException{
		checkBatchQuickFix(BatchValidator.STEP_LISTENER_IS_NOT_FOUND_ID);
	}

	public void testCreateDeciderQuickFix() throws CoreException{
		checkBatchQuickFix(BatchValidator.DECIDER_IS_NOT_FOUND_ID);
	}
	
	public void testCreateCheckpointAlgorithmQuickFix() throws CoreException{
		checkBatchQuickFix(BatchValidator.CHECKPOINT_ALGORITHM_IS_NOT_FOUND_ID);
	}

	public void testCreateItemReaderQuickFix() throws CoreException{
		checkBatchQuickFix(BatchValidator.ITEM_READER_IS_NOT_FOUND_ID);
	}

	public void testCreateItemWriterQuickFix() throws CoreException{
		checkBatchQuickFix(BatchValidator.ITEM_WRITER_IS_NOT_FOUND_ID);
	}

	public void testCreateItemProcessorQuickFix() throws CoreException{
		checkBatchQuickFix(BatchValidator.ITEM_PROCESSOR_IS_NOT_FOUND_ID);
	}

	public void testCreatePartitionMapperQuickFix() throws CoreException{
		checkBatchQuickFix(BatchValidator.MAPPER_IS_NOT_FOUND_ID);
	}

	public void testCreatePartitionAnalyzerQuickFix() throws CoreException{
		checkBatchQuickFix(BatchValidator.ANALYZER_IS_NOT_FOUND_ID);
	}

	public void testCreatePartitionCollectorQuickFix() throws CoreException{
		checkBatchQuickFix(BatchValidator.COLLECTOR_IS_NOT_FOUND_ID);
	}

	public void testCreatePartitionReducerQuickFix() throws CoreException{
		checkBatchQuickFix(BatchValidator.REDUCER_IS_NOT_FOUND_ID);
	}
}
