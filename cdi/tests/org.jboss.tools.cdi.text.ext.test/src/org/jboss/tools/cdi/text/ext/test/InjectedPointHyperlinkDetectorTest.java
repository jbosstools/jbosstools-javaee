package org.jboss.tools.cdi.text.ext.test;

import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.text.JavaWordFinder;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.DocumentProviderRegistry;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.text.ext.hyperlink.InjectedPointHyperlinkDetector;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.AxisUtil;

public class InjectedPointHyperlinkDetectorTest extends TestCase {
	private static final String PROJECT_NAME = "/tests/lookup/injectionpoint";
	private static final String FILE_NAME = "JavaSource/org/jboss/jsr299/tck/tests/lookup/injectionpoint/LoggerConsumer.java";

	public static Test suite() {
		return new TestSuite(InjectedPointHyperlinkDetectorTest.class);
	}

	public void testInjectedPointHyperlinkDetector()  throws Exception {
		IProject project = TCKTest.importPreparedProject(PROJECT_NAME);
		doTest(project);
		TCKTest.cleanProject(PROJECT_NAME);
	}

	private void doTest(IProject project) throws Exception {
		IFile javaFile = project.getFile(FILE_NAME);

		TCKTest.assertTrue("The file \"" + FILE_NAME + "\" is not found", (javaFile != null));
		TCKTest.assertTrue("The file \"" + FILE_NAME + "\" is not found", (javaFile.exists()));

		FileEditorInput editorInput = new FileEditorInput(javaFile);

		IDocumentProvider documentProvider = null;
		Throwable exception = null;
		try {
			documentProvider = DocumentProviderRegistry.getDefault().getDocumentProvider(editorInput);
		} catch (Exception x) {
			exception = x;
			x.printStackTrace();
		}
		assertNull("An exception caught: " + (exception != null? exception.getMessage() : ""), exception);

		assertNotNull("The document provider for the file \"" + FILE_NAME + "\" is not loaded", documentProvider);

		try {
			documentProvider.connect(editorInput);
		} catch (Exception x) {
			exception = x;
			x.printStackTrace();
			assertTrue("The document provider is not able to be initialized with the editor input", false);
		}
		assertNull("An exception caught: " + (exception != null? exception.getMessage() : ""), exception);

		IDocument document = documentProvider.getDocument(editorInput);

		assertNotNull("The document for the file \"" + FILE_NAME + "\" is not loaded", document);

		InjectedPointHyperlinkDetector elPartitioner = new InjectedPointHyperlinkDetector();

		ArrayList<Region> regionList = new ArrayList<Region>();
		regionList.add(new Region(115, 6)); // Inject
		regionList.add(new Region(140, 6)); // logger
		regionList.add(new Region(196, 6)); // logger
		regionList.add(new Region(250, 6)); // logger

		IEditorPart part = openFileInEditor(javaFile);
		ISourceViewer viewer = null;
		if(part instanceof JavaEditor){
			viewer = ((JavaEditor)part).getViewer();
		}

		elPartitioner.setContext(new TestContext((ITextEditor)part));

		int counter = 0;
		for (int i = 0; i < document.getLength(); i++) {
			TestData testData = new TestData(document, i);
			IHyperlink[] links = elPartitioner.detectHyperlinks(viewer, testData.getHyperlinkRegion(), true);

			boolean recognized = links != null;

			if (recognized) {
				counter++;
				if(!findOffsetInRegions(i, regionList)){
					fail("Wrong detection for offset - "+i);
				}
			} else {
				for(Region region : regionList){
					if(i >= region.getOffset() && i <= region.getOffset()+region.getLength())
						fail("Wrong detection for region - "+region.getOffset()+" : "+region.getLength()+region.getLength()+" region - "+i);
				}
			}
		}

		assertEquals("Wrong recognized region count: ", 28,  counter);

		documentProvider.disconnect(editorInput);
	}

	private boolean findOffsetInRegions(int offset, ArrayList<Region> regionList){
		for(Region region : regionList){
			if(offset >= region.getOffset() && offset <= region.getOffset()+region.getLength())
				return true;
		}
		return false;
	}

	private IEditorPart openFileInEditor(IFile input) {
		if (input != null && input.exists()) {
			try {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				return IDE.openEditor(page, input, true);
			} catch (PartInitException pie) {
				
			}
		}
		return null;
	}

	class TestData {
		IDocument document;
		int offset;
		IRegion region;
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

		private IRegion getDocumentRegion() {
			IRegion region = null;
			try {
				region = JavaWordFinder.findWord(document, offset);
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
                    return region.toString();
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

	class TestContext implements IAdaptable{
		ITextEditor editor;

		public TestContext(ITextEditor editor){
			this.editor = editor;
		}

		public Object getAdapter(Class adapter) {
			if(adapter.equals(ITextEditor.class))
				return editor;
			return null;
		}
	}
}