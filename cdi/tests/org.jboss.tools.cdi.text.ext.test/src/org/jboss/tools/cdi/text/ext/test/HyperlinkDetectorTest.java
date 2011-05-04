package org.jboss.tools.cdi.text.ext.test;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.text.JavaWordFinder;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
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
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.common.editor.ObjectMultiPageEditor;
import org.jboss.tools.common.model.ui.editor.EditorPartWrapper;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkDetector;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.AxisUtil;

public class HyperlinkDetectorTest  extends TCKTest {
	protected void checkRegions(String fileName, List<Region> regionList, AbstractHyperlinkDetector elPartitioner) throws Exception {
		IFile file = tckProject.getFile(fileName);

		assertNotNull("The file \"" + fileName + "\" is not found", file);
		assertTrue("The file \"" + fileName + "\" is not found", file.isAccessible());

		FileEditorInput editorInput = new FileEditorInput(file);

		IDocumentProvider documentProvider = null;
		try {
			documentProvider = DocumentProviderRegistry.getDefault().getDocumentProvider(editorInput);
		} catch (Exception x) {
			x.printStackTrace();
			fail("An exception caught: " + x.getMessage());
		}

		assertNotNull("The document provider for the file \"" + fileName + "\" is not loaded", documentProvider);

		try {
			documentProvider.connect(editorInput);
		} catch (Exception x) {
			x.printStackTrace();
			fail("The document provider is not able to be initialized with the editor input\nAn exception caught: "+x.getMessage());
		}

		IDocument document = documentProvider.getDocument(editorInput);
		
		assertNotNull("The document for the file \"" + fileName + "\" is not loaded", document);

		int expected = 0;
		for(Region region : regionList)
			expected += region.getLength()+1;
		
		IEditorPart part = openFileInEditor(file);
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
					if(i >= region.getOffset() && i <= region.getOffset()+region.getLength()) {
						int line = document.getLineOfOffset(region.getOffset());
						fail("Wrong detection for region - "+region.getOffset()+" : "+region.getLength()+" region - "+i);
					}
				}
			}
		}

		assertEquals("Wrong recognized region count: ", expected,  counter);

		documentProvider.disconnect(editorInput);
	}

	protected void checkHyperLinkInXml(String fileName, int offset, String hyperlinkClassName) throws Exception {
		checkHyperLinkInXml(fileName, tckProject, offset, hyperlinkClassName);
	}

	public static IHyperlink checkHyperLinkInXml(String fileName, IProject project, int offset, String hyperlinkClassName) throws Exception {
		Region region = new Region(offset, 0);
		IFile file = project.getFile(fileName);

		assertNotNull("The file \"" + fileName + "\" is not found", file);
		assertTrue("The file \"" + fileName + "\" is not found", file.isAccessible());

		FileEditorInput editorInput = new FileEditorInput(file);

		IEditorPart part = openFileInEditor(file);
		if(part instanceof EditorPartWrapper) part = ((EditorPartWrapper)part).getEditor();
		ISourceViewer viewer = null;
		if (part instanceof XMLMultiPageEditorPart) {
			IEditorPart[] parts = ((XMLMultiPageEditorPart)part).findEditors(editorInput);
			if(parts.length>0) {
				viewer = ((StructuredTextEditor)parts[0]).getTextViewer();
			}
		} else if(part instanceof ObjectMultiPageEditor) {
			viewer = ((ObjectMultiPageEditor)part).getSourceEditor().getTextViewer();
		} else if(part instanceof StructuredTextEditor) {
			viewer = ((StructuredTextEditor)part).getTextViewer();
		}

		IHyperlink[] links = HyperlinkDetector.getInstance().detectHyperlinks(viewer, region, true);
		if(links!=null) {
			for (IHyperlink hyperlink : links) {
				if(hyperlink.getClass().getName().equals(hyperlinkClassName)) {
					return hyperlink;
				}
			}
		}
		fail("Can't find HyperLink");
		return null;
	}

	protected boolean findOffsetInRegions(int offset, List<Region> regionList){
		for(Region region : regionList){
			if(offset >= region.getOffset() && offset <= region.getOffset()+region.getLength())
				return true;
		}
		return false;
	}

	public static IEditorPart openFileInEditor(IFile input) {
		return openFileInEditor(input, null);
	}

	public static IEditorPart openFileInEditor(IFile input, String id) {
		if (input != null && input.exists()) {
			try {
				if(id==null) {
					IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
					return IDE.openEditor(page, input, true);
				} else {
					IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
					return IDE.openEditor(page, input, id, true);
				}
			} catch (PartInitException pie) {
				pie.printStackTrace();
				fail(pie.getMessage());
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
			} catch (Exception x) {
				x.printStackTrace();
				fail(x.getMessage());
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