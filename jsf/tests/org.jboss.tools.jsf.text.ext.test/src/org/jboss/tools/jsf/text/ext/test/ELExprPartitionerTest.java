/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.text.ext.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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
import org.jboss.tools.common.test.util.TestProjectProvider;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.AxisUtil;
import org.jboss.tools.jsf.text.ext.hyperlink.JSPExprHyperlinkPartitioner;
import org.jboss.tools.test.util.JobUtils;

public class ELExprPartitionerTest extends TestCase {
	TestProjectProvider provider = null;
	IProject project = null;
	boolean makeCopy = false;
	private static final String PROJECT_NAME = "numberguess";
	private static final String PAGE_NAME = "/web/giveup.jspx";

	public static Test suite() {
		return new TestSuite(ELExprPartitionerTest.class);
	}

	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.jsf.text.ext.test", null, PROJECT_NAME, makeCopy); 
		project = provider.getProject();
		Throwable exception = null;
		
		assertNull("An exception caught: " + (exception != null? exception.getMessage() : ""), exception);
	}

	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}

	public void testELExprPartitioner() {
		try { 
			JobUtils.waitForIdle(); 
		} catch (Exception e) { 
			assertNull("An exception caught: " + e.getMessage(), e);
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

		assertNotNull("The document provider for the file \"" + PAGE_NAME + "\" is not loaded", documentProvider);

		try {
			documentProvider.connect(editorInput);
		} catch (Exception x) {
			exception = x;
			x.printStackTrace();
			assertTrue("The document provider is not able to be initialized with the editor input", false);
		}
		assertNull("An exception caught: " + (exception != null? exception.getMessage() : ""), exception);
		
		IDocument document = documentProvider.getDocument(editorInput);

		assertNotNull("The document for the file \"" + PAGE_NAME + "\" is not loaded", document);
		
		IStructuredModel model = null;
		if (document instanceof IStructuredDocument) {
			// corresponding releaseFromEdit occurs in
			// dispose()
			model = StructuredModelManager.getModelManager().getModelForEdit((IStructuredDocument) document);
			EditorModelUtil.addFactoriesTo(model);
		}

		assertNotNull("The document model for the file \"" + PAGE_NAME + "\" is not loaded", model);

		JSPExprHyperlinkPartitioner elPartitioner = new JSPExprHyperlinkPartitioner();

		HashMap<Object, ArrayList<Region>> recognitionTest = new HashMap<Object, ArrayList<Region>>();
		
		ArrayList<Region> regionList = new ArrayList<Region>();
		regionList.add(new Region(623, 16));
		regionList.add(new Region(706, 16));
		regionList.add(new Region(813, 18));
		regionList.add(new Region(914, 19));
		regionList.add(new Region(972, 18));
		regionList.add(new Region(1041, 17));
		recognitionTest.put("org.jboss.tools.common.text.ext.jsp.JSP_BUNDLE", regionList);
		
		regionList = new ArrayList<Region>();
		regionList.add(new Region(859, 11));
		regionList.add(new Region(871, 16));
		recognitionTest.put("org.jboss.tools.common.text.ext.jsp.JSP_BEAN", regionList);
		
		regionList = new ArrayList<Region>();
		regionList.add(new Region(859, 11));
		regionList.add(new Region(871, 16));
		recognitionTest.put("org.jboss.tools.seam.text.ext.SEAM_BEAN", regionList);
		
		regionList = new ArrayList<Region>();
		regionList.add(new Region(859, 11));
		regionList.add(new Region(870, 16));
		regionList.add(new Region(886, 1));
		
		recognitionTest.put("org.jboss.tools.common.text.ext.jsp.JSP_EXPRESSION", regionList);
		
//		regionList = new ArrayList<Region>();
//		regionList.add(new Region(870, 16));
//		recognitionTest.put("org.eclipse.jst.jsp.SCRIPT.JSP_EL2", regionList);
		
		regionList = new ArrayList<Region>();
		regionList.add(new Region(859, 11));
		regionList.add(new Region(871, 16));
		recognitionTest.put("org.jboss.tools.common.text.ext.jsp.EXPRESSION", regionList);
		
		int counter = 0;
		for (int i = 0; i < document.getLength(); i++) {
			TestData testData = new TestData(document, i);
			boolean recognized = elPartitioner.recognize(testData.document, testData.getHyperlinkRegion());
			if (recognized) {
				String childPartitionType = elPartitioner.getChildPartitionType(testData.document, testData.getHyperlinkRegion());
				if (childPartitionType != null) {
					ArrayList<Region> test = (ArrayList<Region>)recognitionTest.get(childPartitionType);
					boolean testResult = false;
					Iterator<Region> regions = test.iterator();
					Region r = null;
					while (!testResult && regions.hasNext()) {
						r = regions.next();
						if (r.getOffset() <= testData.offset && testData.offset < (r.getOffset() + r.getLength()))
							testResult = true;
					}
					StringBuffer assertMessage = new StringBuffer();
					assertMessage.append("Wrong recognition for the region #")
						.append(i)
						.append(": ")
						.append(testData.getHyperlinkRegion().toString())
						.append(" doesn't matches the regions for PARTITION_TYPE '")
						.append(childPartitionType)
						.append("' {");
					boolean first = true;
					for (Region reg : test) {
						if (!first) {
							assertMessage.append(", ");
						} else {
							first = false;
						}
						assertMessage.append("[")
							.append(reg.getOffset())
							.append("-")
							.append(reg.getOffset() + reg.getLength())
							.append("]");
					}
					assertMessage.append("}");

					assertTrue(assertMessage.toString() , testResult);
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
				assertFalse("Wrong recognition for the region: " + testData.getHyperlinkRegion().toString() 
						+ " matches the wrong region [" + r.getOffset() + "-" + (r.getOffset() + r.getLength()) + "]" , testResult);
			}
		}

		assertEquals("Wrong recognized region count: ", 132,  counter);
		
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
