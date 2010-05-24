package org.jboss.tools.cdi.text.ext.test;

import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.DocumentProviderRegistry;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.text.ext.hyperlink.InjectedPointHyperlinkDetector;

public class InjectedPointInProducerMethodHyperlinkDetectorTest extends HyperlinkDetectorTest{
	private static final String PROJECT_NAME = "/tests/decorators/invocation/producer/method";
	private static final String FILE_NAME = "JavaSource/org/jboss/jsr299/tck/tests/decorators/invocation/producer/method/CustomProducerImpl.java";

	public static Test suite() {
		return new TestSuite(InjectedPointInProducerMethodHyperlinkDetectorTest.class);
	}

	public void testProducerDisposerHyperlinkDetector()  throws Exception {
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
		regionList.add(new Region(1374, 5)); // order
		regionList.add(new Region(1462, 3)); 
		regionList.add(new Region(1498, 3)); 

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
						fail("Wrong detection for region - "+region.getOffset()+" : "+region.getLength()+" region - "+i);
				}
			}
		}

		assertEquals("Wrong recognized region count: ", 14,  counter);

		documentProvider.disconnect(editorInput);
	}

}
