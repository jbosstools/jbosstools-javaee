package org.jboss.tools.seam.ui.test.hyperlink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.DocumentProviderRegistry;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.internal.editor.EditorModelUtil;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkDetector;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.AxisUtil;
import org.jboss.tools.seam.text.ext.hyperlink.SeamViewHyperlinkPartitioner;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

public class SeamViewHyperlinkPartitionerTest  extends TestCase {

	IProject project = null;
	boolean makeCopy = false;
	private static final String PROJECT_NAME = "TestSeamELContentAssist";
	private static final String PAGE_NAME = "/WebContent/login.xhtml";

	public static Test suite() {
		return new TestSuite(SeamViewHyperlinkPartitionerTest.class);
	}

	public void setUp() throws Exception {
		project = ResourcesUtils.importProject(Platform.getBundle("org.jboss.tools.seam.ui.test"), "/projects/TestSeamELContentAssist", new NullProgressMonitor());
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
	}

	protected void tearDown() throws Exception {
		if(project != null) {
			boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
			try {
				project.delete(true,true, null);
			} finally {
				ResourcesUtils.setBuildAutomatically(saveAutoBuild);
			}
		}
	}

	FileEditorInput editorInput = null;
	IDocumentProvider documentProvider = null;
	IStructuredModel model = null;
	
	public void testSeamViewPartitioner() throws CoreException {

		JobUtils.waitForIdle();

		assertTrue("Test project \"" + PROJECT_NAME + "\" is not loaded", (project != null));

		IFile jspFile = project.getFile(PAGE_NAME);

		assertTrue("The file \"" + PAGE_NAME + "\" is not found", (jspFile != null));
		assertTrue("The file \"" + PAGE_NAME + "\" is not found", (jspFile.exists()));

		editorInput = new FileEditorInput(jspFile);
		
		Throwable exception = null;
		
		try {
			documentProvider = DocumentProviderRegistry.getDefault().getDocumentProvider(editorInput);
			assertNotNull("The document provider for the file \"" + PAGE_NAME + "\" is not loaded", documentProvider);
	
	
			documentProvider.connect(editorInput);
			
			IDocument document = documentProvider.getDocument(editorInput);
	
			assertTrue("The document for the file \"" + PAGE_NAME + "\" is not loaded", (document != null));
			
			assertTrue("Document should be instance of IStructuredDocument",document instanceof IStructuredDocument);
			model = StructuredModelManager.getModelManager().getModelForEdit((IStructuredDocument) document);
			assertTrue("The document model for the file \"" + PAGE_NAME + "\" is not loaded", (model != null));
			
			EditorModelUtil.addFactoriesTo(model);
			
			SeamViewHyperlinkPartitioner seamViewPartitioner = new SeamViewHyperlinkPartitioner();
	
			TestHyperlinkDetector detector = new TestHyperlinkDetector();
			HashMap<Object, ArrayList> recognitionTest = new HashMap<Object, ArrayList>();
			
			ArrayList<Region> regionList = new ArrayList<Region>();
			regionList.add(new Region(1888, 11));
			regionList.add(new Region(1943, 11));
			recognitionTest.put("org.jboss.tools.seam.text.ext.SEAM_VIEW_LINK", regionList);
			
			int counter = 0;
			for (int i = 0; i < document.getLength(); i++) {
				TestData testData = new TestData(document, i);
				
				String[] partitionTypes = detector.getPartitionTypes(document, i);
	
				boolean recognized = false;
				
				if (partitionTypes != null && partitionTypes.length > 0) {
					recognized = ("org.jboss.tools.seam.text.ext.SEAM_VIEW_LINK".equals(partitionTypes[0]));
				}
	
				if (recognized) {
					recognized &= seamViewPartitioner.recognize(testData.document, testData.getHyperlinkRegion());
				}
				
				if (recognized) {
					String childPartitionType = seamViewPartitioner.getChildPartitionType(testData.document, testData.getHyperlinkRegion());
	//				if (childPartitionType != null)
	//					System.out.println("position #" + i + " partitionType: " + childPartitionType);
	
					if (childPartitionType != null) {
						ArrayList test = (ArrayList)recognitionTest.get(childPartitionType);
						boolean testResult = false;
						Iterator regions = test.iterator();
						Region r = null;
						while (!testResult && regions.hasNext()) {
							r = (Region)regions.next();
							if (r.getOffset() <= testData.offset && testData.offset < (r.getOffset() + r.getLength()))
								testResult = true;
						}
						assertTrue("Wrong recognition for the region: " + testData.getHyperlinkRegion().toString() 
								+ " doesn't matches the region [" + r.getOffset() + "-" + (r.getOffset() + r.getLength()) + "]" , testResult);
						counter++;
					} else {
						recognized = false;
					}
	
				}
	
				if (!recognized) {
	
					boolean testResult = false;
					Iterator keys = recognitionTest.keySet().iterator();
					Region r = null;
					while (keys != null && keys.hasNext()) {
						Object key = keys.next();
						ArrayList test = (ArrayList)recognitionTest.get(key);
						Iterator regions = test.iterator();
						while (!testResult && regions.hasNext()) {
							r = (Region)regions.next();
							if (r.getOffset() <= testData.offset && testData.offset < (r.getOffset() + r.getLength()))
								testResult = true;
	//						System.out.println(testData.getHyperlinkRegion().toString());
						}
					}
					assertTrue("Wrong recognition for the region: " + testData.getHyperlinkRegion().toString() 
							+ " matches the wrong region [" + r.getOffset() + "-" + (r.getOffset() + r.getLength()) + "] in file \"" + PAGE_NAME + "\"" , (testResult == false));
				}
			}
			
			assertEquals("Wrong recognized region count", 22 , counter);
		} finally {
			if (model != null)
				model.releaseFromEdit();

			if (editorInput != null && documentProvider != null)
				documentProvider.disconnect(editorInput);
		}
	}
	
	class TestData {
		IDocument document;
		int offset;
		ITypedRegion region;
		String contentType;
		private IHyperlinkRegion hyperlinkRegion = null;
	
		TestData (IDocument document, int offset) {
			this.document = document;
			this.offset = offset;
			init();
		}
		
		private void init() {
			this.region = getDocumentRegion();
			this.contentType = getContentType();
			this.hyperlinkRegion = getHyperlinkRegion();
		}
		
		private ITypedRegion getDocumentRegion() {
			ITypedRegion region = null;
			try {
				region = (document instanceof IDocumentExtension3 ? 
						((IDocumentExtension3)document).getDocumentPartitioner("org.eclipse.wst.sse.core.default_structured_text_partitioning").getPartition(offset) : 
						document.getDocumentPartitioner().getPartition(offset)); 
			} catch (Exception x) {
				x.printStackTrace();
			}
			
			return region;
		}
		
		public IHyperlinkRegion getHyperlinkRegion() {
			if (hyperlinkRegion != null)
				return hyperlinkRegion;
			
			return new IHyperlinkRegion() {
		        public String getAxis() {
                    return AxisUtil.getAxis(document, region.getOffset());
                }
                public String getContentType() {
                    return contentType;
                }
                public String getType() {
                    return region.getType();
                }
                public int getLength() {
                    return region.getLength();
                }
                public int getOffset() {
                    return region.getOffset();
                }
                public String toString() {
                	try {
						return document.get(getOffset(), getLength()) + " [" + getOffset() + "-" + (getOffset() + getLength() - 1) + ":" + getType() + ":" + getContentType() +  "]";
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return "";
                }
            };
		}
		
		
		/**
		 * Returns the content type of document
		 * 
		 * @param document -
		 *            assumes document is not null
		 * @return String content type of given document
		 */
		private String getContentType() {
			String type = null;
	
			IModelManager mgr = StructuredModelManager.getModelManager();
			IStructuredModel model = null;
			try {
				model = mgr.getExistingModelForRead(document);
				if (model != null) {
					type = model.getContentTypeIdentifier();
				}
			} finally {
				if (model != null) {
					model.releaseFromRead();
				}
			}
			return type;
		}
	}

}

class TestHyperlinkDetector extends HyperlinkDetector {

	/**
	 * Returns the partition types located at offset in the document
	 * 
	 * @param document -
	 *            assumes document is not null
	 * @param offset
	 * @return String partition types
	 */
	public String[] getPartitionTypes(IDocument document, int offset) {
		return super.getPartitionTypes(document, offset);
	}
};

