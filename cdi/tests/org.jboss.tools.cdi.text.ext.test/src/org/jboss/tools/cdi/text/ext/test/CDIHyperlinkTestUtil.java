package org.jboss.tools.cdi.text.ext.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

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
import org.jboss.tools.cdi.core.ICDIElement;
import org.jboss.tools.cdi.text.ext.hyperlink.ITestableCDIHyperlink;
import org.jboss.tools.common.editor.ObjectMultiPageEditor;
import org.jboss.tools.common.model.ui.editor.EditorPartWrapper;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkDetector;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.AxisUtil;

public class CDIHyperlinkTestUtil extends TestCase{
	public static void checkRegions(IProject project, String fileName, List<TestRegion> regionList, AbstractHyperlinkDetector elPartitioner) throws Exception {
		IFile file = project.getFile(fileName);

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
		for(TestRegion testRegion : regionList)
			expected += testRegion.region.getLength()+1;
		
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
				TestRegion testRegion = findOffsetInRegions(i, regionList); 
				if(testRegion == null){
					fail("Wrong detection for offset - "+i);
				}else{
					checkTestRegion(links, testRegion);
				}
			} 
			else {
				for(TestRegion testRegion : regionList){
					if(i >= testRegion.region.getOffset() && i <= testRegion.region.getOffset()+testRegion.region.getLength()) {
						int line = document.getLineOfOffset(testRegion.region.getOffset());
						fail("Wrong detection for region - "+testRegion.region.getOffset()+" : "+testRegion.region.getLength()+" region - "+i);
					}
				}
			}
		}

		assertEquals("Wrong recognized region count: ", expected,  counter);

		documentProvider.disconnect(editorInput);
	}
	
	private static void checkTestRegion(IHyperlink[] links, TestRegion testRegion){
		for(IHyperlink link : links){
			TestHyperlink testLink = findTestHyperlink(testRegion.hyperlinks, link);
			assertNotNull("Unexpected hyperlink - "+link.getHyperlinkText(), testLink);
			assertEquals("Unexpected hyperlink type", testLink.hyperlink, link.getClass());
			assertTrue("Validation fails for hyperlink - "+link.getHyperlinkText(), testLink.validateHyperlink(link));
		}
		
		for(TestHyperlink testLink : testRegion.hyperlinks){
			IHyperlink link = findHyperlink(links, testLink);
			assertNotNull("Hyperlink - "+testLink.name+" not found", link);
		}
	}
	
	private static TestHyperlink findTestHyperlink(List<TestHyperlink> testHyperlinks, IHyperlink link){
		for(TestHyperlink testLink : testHyperlinks){
			if(testLink.name.equals(link.getHyperlinkText()))
				return testLink;
		}
		return null;
	}

	private static IHyperlink findHyperlink(IHyperlink[] links, TestHyperlink testLink){
		for(IHyperlink link : links){
			if(testLink.name.equals(link.getHyperlinkText()))
				return link;
		}
		return null;
	}

	public static void checkHyperLinkInXml(IProject project, String fileName, int offset, String hyperlinkClassName) throws Exception {
		checkHyperLinkInXml(fileName, project, offset, hyperlinkClassName);
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

	private static TestRegion findOffsetInRegions(int offset, List<TestRegion> regionList){
		for(TestRegion testRegion : regionList){
			if(offset >= testRegion.region.getOffset() && offset <= testRegion.region.getOffset()+testRegion.region.getLength())
				return testRegion;
		}
		return null;
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

	static class TestData {
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

	static class TestContext implements IAdaptable{
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
	
	public static class TestRegion{
		Region region;
		ArrayList<TestHyperlink> hyperlinks = new ArrayList<TestHyperlink>();
		
		public TestRegion(int offset, int length, TestHyperlink[] testHyperlinks){
			region = new Region(offset, length);
			for(TestHyperlink testHyperlink : testHyperlinks){
				hyperlinks.add(testHyperlink);
			}
		}
	}
	
	public static class TestHyperlink{
		Class<? extends IHyperlink> hyperlink;
		ICDIElement element = null;
		String[] elementPaths = null;
		String name;
		
		public TestHyperlink(Class<? extends IHyperlink> hyperlink, String name){
			this.hyperlink = hyperlink;
			this.name = name;
		}
		
		public TestHyperlink(Class<? extends IHyperlink> hyperlink, String name, ICDIElement element){
			this(hyperlink, name);
			this.element = element;
		}

		public TestHyperlink(Class<? extends IHyperlink> hyperlink, String name, String[] elementPaths){
			this(hyperlink, name);
			this.elementPaths = elementPaths;
		}
		
		public boolean validateHyperlink(IHyperlink hyperlink){
			if(hyperlink instanceof ITestableCDIHyperlink && ((ITestableCDIHyperlink)hyperlink).getCDIElement() != null && element != null){
				assertEquals(element, ((ITestableCDIHyperlink)hyperlink).getCDIElement());
			}else if(hyperlink instanceof ITestableCDIHyperlink && ((ITestableCDIHyperlink)hyperlink).getCDIElements() != null && elementPaths != null){
				for(ICDIElement element : ((ITestableCDIHyperlink)hyperlink).getCDIElements()){
					String elementPath = findElementPath(elementPaths, element);
					assertNotNull("Unexpected CDI element - "+element.getSourcePath().toString(), elementPath);
				}
				
				for(String elementPath : elementPaths){
					ICDIElement element = findCDIElement(((ITestableCDIHyperlink)hyperlink).getCDIElements(), elementPath);
					assertNotNull("CDI element - "+elementPath+" not found", element);
				}
			}
			return true;
		}
		
		protected String findElementPath(String[] elementPaths, ICDIElement element){
			for(String elementPath : elementPaths){
				if(elementPath.equals(element.getSourcePath().toString()))
					return elementPath;
			}
			return null;
		}

		protected ICDIElement findCDIElement(Set<? extends ICDIElement> elements, String elementPath){
			for(ICDIElement element : elements){
				if(elementPath.equals(element.getSourcePath().toString()))
					return element;
			}
			return null;
		}

	}
}