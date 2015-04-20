/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.batch.ui.itest;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.ui.internal.wizard.NewBatchArtifactWizard;
import org.jboss.tools.batch.ui.internal.wizard.NewBatchArtifactWizardPage;
import org.jboss.tools.batch.ui.internal.wizard.NewJobXMLCreationWizard;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.test.util.JUnitUtils;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * @author Viacheslav Kabanovich
 *
 */
public class NewBatchWizardTest extends TestCase {	
	
	static class NewWizardContext<W extends Wizard> {
		IProject project;
		IJavaProject jp;
		WizardDialog dialog;
		W wizard;

		public NewWizardContext() {
			project = ResourcesPlugin.getWorkspace().getRoot().getProject("BatchTestProject");
			jp = EclipseUtil.getJavaProject(project);
		}

		public void open() {
			dialog = new WizardDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					wizard);
			dialog.setBlockOnOpen(false);
			dialog.open();
		}
		
		public void close() {
			dialog.close();
		}
	}
	
	static class NewBeansXMLWizardContext extends NewWizardContext<NewJobXMLCreationWizard> {		

		public void init(String wizardId) {
			wizard = (NewJobXMLCreationWizard)WorkbenchUtils.findWizardByDefId(wizardId);
			wizard.init(BatchCorePlugin.getDefault().getWorkbench(), new StructuredSelection(jp));
			wizard.setOpenEditorAfterFinish(false);
			open();
		}
	}

	public void testNewBeansXMLWizard() throws CoreException {
		NewBeansXMLWizardContext context = new NewBeansXMLWizardContext();
		context.init(NewJobXMLCreationWizard.WIZARD_ID);
		
		try {
			
			WizardNewFileCreationPage page = (WizardNewFileCreationPage)context.wizard.getPage(NewJobXMLCreationWizard.PAGE_NAME);
			String s = page.getFileName();
			assertEquals("job.xml", s);
			assertFalse(context.wizard.canFinish());
			page.setFileName("job222.xml");
			assertTrue(context.wizard.canFinish());
			String c = page.getContainerFullPath().toString();
			assertEquals("/BatchTestProject/src/META-INF/batch-jobs", c);

			assertEquals("1.0", context.wizard.getVersion());
			
			context.wizard.setID("myNewJob");
			
			context.wizard.performFinish();
		
			IFile f = context.project.getParent().getFile(page.getContainerFullPath().append(page.getFileName()));
			assertTrue(f.exists());
			
			String text = FileUtil.readStream(f.getContents());
			assertTrue(text.indexOf("http://xmlns.jcp.org/xml/ns/javaee") > 0);
			assertTrue(text.indexOf("id=\"myNewJob\"") > 0);

		} finally {
			context.close();
		}
	}

	static class NewBatchArtifactWizardContext extends NewWizardContext<NewBatchArtifactWizard> {		
		String packName;
		String typeName;
		NewTypeWizardPage page;

		public void init(String wizardId, String packName, String typeName) {
			wizard = (NewBatchArtifactWizard)WorkbenchUtils.findWizardByDefId(wizardId);
			wizard.init(BatchCorePlugin.getDefault().getWorkbench(), new StructuredSelection(jp));
			wizard.setOpenEditorAfterFinish(false);
			open();
			page = (NewTypeWizardPage)dialog.getSelectedPage();
			setTypeName(packName, typeName);
		}

		public void setTypeName(String packName, String typeName) {
			this.packName = packName;
			this.typeName = typeName;
			page.setTypeName(typeName, true);
			IPackageFragment pack = page.getPackageFragmentRoot().getPackageFragment(packName);
			page.setPackageFragment(pack, true);
		}

		public String getNewTypeContent() {
			IType type = findType();
			
			IFile file = (IFile)type.getResource();
			assertNotNull(file);
			String text = null;
			try {
				text = FileUtil.readStream(file.getContents());
			} catch (CoreException e) {
				JUnitUtils.fail("Cannot read from " + file, e);
			}
			return text;
		}

		IType findType() {
			IType type = null;
			try {
				String tn = typeName;
				int q = tn.indexOf("<");
				if(q >= 0) tn = tn.substring(0, q);
				type = jp.findType(packName + "." + tn);
			} catch (JavaModelException e) {
				JUnitUtils.fail("Cannot find type " + typeName, e);
			}
			return type;
		}
	}

	public void testNewBatchletWizard1() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.BATCHLET, 
			"SomeBatchlet1", 
			false,
			null, 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewBatchletWizard2() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.BATCHLET, 
			"SomeBatchlet2", 
			true,
			null, 
			Arrays.asList("prop1"));
	}

	public void testNewDeciderWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.DECIDER, 
			"SomeDecider", 
			false,
			null, 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewItemReaderWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.ITEM_READER, 
			"SomeReader", 
			false,
			"aReader", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewItemWriterWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.ITEM_WRITER, 
			"SomeWriter",
			false,
			"someGoodWriter", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewItemProcessorWizard1() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.ITEM_PROCESSOR, 
			"SomeProcessor1", 
			false,
			"fastProcessor1", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewItemProcessorWizard2() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.ITEM_PROCESSOR, 
			"SomeProcessor2", 
			true,	//should be interface because there is no class
			"fastProcessor2", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewCheckpointAlgorithmWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.CHECKPOINT_ALGORITHM, 
			"SomeCheckpointAlgorithm", 
			false,
			"efficient", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewPartitionMapperWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.PARTITION_MAPPER, 
			"SomeMapper", 
			false,
			"aGoodMapper", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewPartitionReducerWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.PARTITION_REDUCER, 
			"SomeMapper", 
			false,
			"aGoodReducer", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewPartitionCollectorWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.PARTITION_COLLECTOR, 
			"SomeCollector", 
			false,
			"aGoodCollector", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewPartitionAnalyzerWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.PARTITION_ANALYZER, 
			"SomeAnalyzer", 
			false,
			"aGoodAnalyzer", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewJobListenerWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.JOB_LISTENER, 
			"SomeJobListener", 
			false,
			"aGoodJobListener", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewStepListenerWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.STEP_LISTENER, 
			"SomeStepListener", 
			false,
			"aGoodStepListener", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewChunkListenerWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.CHUNK_LISTENER, 
			"SomeChunkListener", 
			false,
			"aGoodChunkListener", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewItemReadListenerWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.ITEM_READ_LISTENER, 
			"SomeItemReadListener", 
			false,
			"aGoodItemReadListener", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewItemWriteListenerWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.ITEM_WRITE_LISTENER, 
			"SomeItemWriteListener", 
			false,
			"aGoodItemWriteListener", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewItemProcessListenerWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.ITEM_PROCESS_LISTENER, 
			"SomeItemProcessListener", 
			false,
			"aGoodItemProcessListener", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewSkipReadListenerWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.SKIP_READ_LISTENER, 
			"SomeSkipReadListener", 
			false,
			"aGoodSkipReadListener", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewSkipWriteListenerWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.SKIP_WRITE_LISTENER, 
			"SomeSkipWriteListener", 
			false,
			"aGoodSkipWriteListener", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewSkipProcessListenerWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.SKIP_PROCESS_LISTENER, 
			"SomeSkipProcessListener", 
			false,
			"aGoodSkipProcessListener", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewRetryReadListenerWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.RETRY_READ_LISTENER, 
			"SomeRetryReadListener", 
			false,
			"aGoodRetryReadListener", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewRetryWriteListenerWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.RETRY_WRITE_LISTENER, 
			"SomeRetryWriteListener", 
			false,
			"aGoodRetryWriteListener", 
			Arrays.asList("prop1", "prop2"));
	}

	public void testNewRetryProcessListenerWizard() throws CoreException {
		doTestNewBatchArtifactWizard(BatchArtifactType.RETRY_PROCESS_LISTENER, 
			"SomeRetryProcessListener", 
			false,
			"aGoodRetryProcessListener", 
			Arrays.asList("prop1", "prop2"));
	}

	private void doTestNewBatchArtifactWizard(BatchArtifactType type, 
			String typeName,
			boolean deriveFromInterface,
			String artifactName, 
			List<String> properties) throws CoreException {
		NewBatchArtifactWizardContext context = new NewBatchArtifactWizardContext();
		context.init(NewBatchArtifactWizard.WIZARD_ID, "batch", typeName);

		try {
			NewBatchArtifactWizardPage page = context.wizard.getPage();
			page.setArtifact(type, true);
			if(deriveFromInterface) {
				page.setDeriveFromInterface();
			}
			if(artifactName != null) {
				page.setArtifactName(artifactName);
			}
			if(properties != null && !properties.isEmpty()) {
				page.setProperties(properties);
			}
			
			context.wizard.performFinish();
			
			String text = context.getNewTypeContent();

			if(type.getClassName() != null && !deriveFromInterface) {
				assertTrue(text.contains("extends " + getElememtName(type.getClassName())));
			} else {
				assertTrue(text.contains("implements " + getElememtName(type.getInterfaceName())));
			}
			assertTrue(text.contains("@Named"));
			if(artifactName != null) {
				assertTrue(text.contains("@Named(\"" + artifactName + "\")"));
			}
			if(properties != null && !properties.isEmpty()) {
				assertTrue(text.contains("@Inject"));
				assertTrue(text.contains("@BatchProperty"));
				for (String s: properties) {
					assertTrue(text.contains("String " + s + ";"));
				}
			}
			
			context.findType().getResource().delete(true, new NullProgressMonitor());
		} finally {
			context.close();
		}
	}

	private String getElememtName(String qName) {
		return qName.substring(qName.lastIndexOf('.') + 1);
	}

}