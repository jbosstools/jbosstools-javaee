/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.cdi.core.test.project;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.internal.core.impl.CDIProjectAsYouType;
import org.jboss.tools.common.util.IEditorWrapper;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDIProjectAsYouTypeTest extends TCKTest {

	public void testModel() throws Exception {
		IFile f = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/lookup/duplicateName/TwoNamedProducers.java");
		assertTrue(f.exists());
		
		IEditorPart editorPart = WorkbenchUtils.openEditor(f.getFullPath());
		assertNotNull(editorPart);

		try {
			CDIProjectAsYouType ayt = new CDIProjectAsYouType(cdiProject, f);
			Collection<IBean> bs = getInjectedBeans(ayt);
			assertEquals(2, bs.size());

			ISourceViewer s = getTextViewer(editorPart);
		
			modifyDocument(s.getDocument(), "create()", "_create()");

			ayt = new CDIProjectAsYouType(cdiProject, f);
			bs = getInjectedBeans(ayt);
			assertEquals(1, bs.size());
			
			modifyDocument(s.getDocument(), "_create()", "create()");

			ayt = new CDIProjectAsYouType(cdiProject, f);
			bs = getInjectedBeans(ayt);
			assertEquals(2, bs.size());
		} finally {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(editorPart, false);
		}
	}

	private Collection<IBean> getInjectedBeans(CDIProjectAsYouType ayt) {
		Collection<IBean> bs = ayt.getBeans(true, "org.jboss.jsr299.tck.tests.jbt.lookup.duplicateName.TwoNamedProducers");
		IBean b = bs.iterator().next();
		Collection<IInjectionPoint> ps =  b.getInjectionPoints();
		IInjectionPoint p = ps.iterator().next();
		return ayt.getBeans(true, p);
	}

	private ISourceViewer getTextViewer(IEditorPart editorPart) {
		ISourceViewer viewer = null;
		ITextEditor textEditor = null;
		if (editorPart instanceof IEditorWrapper) {
			editorPart = ((IEditorWrapper) editorPart).getEditor();
		}
		if (editorPart instanceof ITextEditor) {
			textEditor = (ITextEditor) editorPart;
		} else {
			textEditor = editorPart == null ? null : (ITextEditor)editorPart.getAdapter(ITextEditor.class);
		}
		if(textEditor instanceof JavaEditor) {
			viewer = ((JavaEditor)textEditor).getViewer();
		} else if(textEditor instanceof StructuredTextEditor) {
			viewer = ((StructuredTextEditor)textEditor).getTextViewer();
		}
		return viewer;
	}

	private void modifyDocument(IDocument document, String oldText, String newText) throws BadLocationException {
		int q = document.get().indexOf(oldText);
		document.replace(q, oldText.length(), newText);
	}

}
