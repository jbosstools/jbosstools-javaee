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
import org.jboss.tools.common.model.XJob;
import org.jboss.tools.common.test.util.TestProjectProvider;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkDetector;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.AxisUtil;
import org.jboss.tools.seam.text.ext.hyperlink.SeamViewHyperlinkPartitioner;

public class SeamViewHyperlinkPartitionerTest  extends TestCase {
	TestProjectProvider provider = null;
	IProject project = null;
	boolean makeCopy = false;
	private static final String PROJECT_NAME = "TestSeamELContentAssist";
	private static final String PAGE_NAME = "/WebContent/login.xhtml";

	public static Test suite() {
		return new TestSuite(SeamViewHyperlinkPartitionerTest.class);
	}

	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.seam.ui.test", null, PROJECT_NAME, makeCopy); 
		project = provider.getProject();
		Throwable exception = null;
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (Exception x) {
			exception = x;
			x.printStackTrace();
		}
		assertNull("An exception caught: " + (exception != null? exception.getMessage() : ""), exception);
	}

	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}

	public void testSeamViewPartitioner() {
		try {
			XJob.waitForJob();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		assertTrue("Test project \"" + PROJECT_NAME + "\" is not loaded", (project != null));

		IFile jspFile = project.getFile(PAGE_NAME);

		assertTrue("The file \"" + PAGE_NAME + "\" is not found", (jspFile != null));
		assertTrue("The file \"" + PAGE_NAME + "\" is not found", (jspFile.exists()));

		FileEditorInput editorInput = new FileEditorInput(jspFile);
		
		IDocumentProvider documentProvider = null;
		Throwable exception = null;
		try {
			documentProvider = DocumentProviderRegistry.getDefault().getDocumentProvider(editorInput);
		} catch (Exception x) {
			exception = x;
			x.printStackTrace();
			
		}
		assertNull("An exception caught: " + (exception != null? exception.getMessage() : ""), exception);

		assertTrue("The document provider for the file \"" + PAGE_NAME + "\" is not loaded", (documentProvider != null));

		try {
			documentProvider.connect(editorInput);
		} catch (Exception x) {
			exception = x;
			x.printStackTrace();
			assertTrue("The document provider is not able to be initialized with the editor input", false);
		}
		assertNull("An exception caught: " + (exception != null? exception.getMessage() : ""), exception);
		
		IDocument document = documentProvider.getDocument(editorInput);

		assertTrue("The document for the file \"" + PAGE_NAME + "\" is not loaded", (document != null));
		
		IStructuredModel model = null;
		if (document instanceof IStructuredDocument) {
			// corresponding releaseFromEdit occurs in
			// dispose()
			model = StructuredModelManager.getModelManager().getModelForEdit((IStructuredDocument) document);
			EditorModelUtil.addFactoriesTo(model);
		}

		assertTrue("The document model for the file \"" + PAGE_NAME + "\" is not loaded", (model != null));

		SeamViewHyperlinkPartitioner seamViewPartitioner = new SeamViewHyperlinkPartitioner();

		TestHyperlinkDetector detector = new TestHyperlinkDetector();
		HashMap<Object, ArrayList> recognitionTest = new HashMap<Object, ArrayList>();
		
		ArrayList<Region> regionList = new ArrayList<Region>();
		regionList.add(new Region(1754, 12));
		regionList.add(new Region(1809, 12));
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
					}
				}
				assertTrue("Wrong recognition for the region: " + testData.getHyperlinkRegion().toString() 
						+ " matches the wrong region [" + r.getOffset() + "-" + (r.getOffset() + r.getLength()) + "]" , (testResult == false));
			}
		}
		
		assertTrue("Wrong recognized region count: " + counter  
				+ " (must be 24)" , (counter == 24));

		model.releaseFromEdit();

		documentProvider.disconnect(editorInput);
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
			} catch (Exception x) {}
			
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
                	return "[" + getOffset() + "-" + (getOffset() + getLength() - 1) + ":" + getType() + ":" + getContentType() +  "]";
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

