/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.batch.core.itest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
//import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitDocumentProvider.ProblemAnnotation;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.StructuredMarkerAnnotation;
import org.jboss.tools.batch.ui.editor.internal.model.JobXMLEditor;
import org.jboss.tools.common.base.test.validation.AbstractAsYouTypeValidationTest;
import org.jboss.tools.common.validation.CommonValidationPlugin;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * 
 * @author Victor V. Rubezhny and Viacheslav Kabanovich
 *
 */
@SuppressWarnings("restriction")
public class BaseAsYouTypeInJobXMLValidationTest extends AbstractAsYouTypeValidationTest {
	public static final String MARKER_TYPE = "org.jboss.tools.common.validation.temp"; //$NON-NLS-1$
	public static final String RESOURCE_MARKER_TYPE = "org.jboss.tools.batch.core.batchproblem"; //$NON-NLS-1$
	private String fResourceMarkerType = null;

	public BaseAsYouTypeInJobXMLValidationTest(IProject project, String resourceMarkerType) {
		this.project = project;
		this.fResourceMarkerType = resourceMarkerType;
		CommonValidationPlugin.getDefault().earlyStartup(); // JBIDE-14515 - We need it here because of 
															// no early startup code is called under Tycho
	}
	public BaseAsYouTypeInJobXMLValidationTest() {
		this.fResourceMarkerType = RESOURCE_MARKER_TYPE;
		CommonValidationPlugin.getDefault().earlyStartup(); // JBIDE-14515 - We need it here because of 
															// no early startup code is called under Tycho
	}
	
	@Override
	protected void obtainEditor(IEditorPart editorPart) {
		if (!(editorPart instanceof JobXMLEditor))
			return;

		textEditor = ((JobXMLEditor) editorPart).getSourceEditor();

		assertNotNull(
				"Cannot get the Job XML Editor instance for file \"" //$NON-NLS-1$
						+ fileName + "\"", textEditor);

		file = ((IFileEditorInput) textEditor.getEditorInput()).getFile();
		assertNotNull("Cannot find file in editor input", file);
	}

	protected ISourceViewer getTextViewer() {
		return textEditor instanceof StructuredTextEditor ? ((StructuredTextEditor)textEditor).getTextViewer() : null;
	}

	@Override
	protected boolean isAnnotationAcceptable(Annotation annotation) {
		if (!(annotation instanceof StructuredMarkerAnnotation))
			return false;

		StructuredMarkerAnnotation problemAnnotation = (StructuredMarkerAnnotation) annotation;

		String markerType = problemAnnotation.getAnnotationType();
		if (!/*MARKER_TYPE*/"org.eclipse.wst.sse.ui.temp.warning".equalsIgnoreCase(markerType))
			return false;

		return true;
	}
	@Override
	protected boolean isMarkerAnnotationAcceptable(Annotation annotation) {
		if (!(annotation instanceof MarkerAnnotation))
			return false;

		MarkerAnnotation markerAnnotation = (MarkerAnnotation) annotation;

		IMarker marker = markerAnnotation.getMarker();
		String type;
		try {
			type = marker.getType();
			return fResourceMarkerType.equals(type);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}
	@Override
	public void assertResourceMarkerIsCreated(IFile file,
			String errorMessage, int line) throws CoreException {
		IMarker[] markers = AbstractResourceMarkerTest.findMarkers(
				file, fResourceMarkerType, errorMessage, true);

		assertNotNull("Resource Marker not found for type: " + fResourceMarkerType + ", message: [" + errorMessage + "] at line: " + line, markers);
		assertFalse("Resource Marker not found for type: " + fResourceMarkerType + ", message: [" + errorMessage + "] at line: " + line, markers.length == 0);

		for (IMarker m : markers) {
			Integer l = m.getAttribute(IMarker.LINE_NUMBER, -1);
			if (l != null && line == l.intValue()) {
				return;
			}
		}
	
		fail("Resource Marker not found for type: " + fResourceMarkerType + ", message: [" + errorMessage + "] at line: " + line);
	}

	public void assertNoResourceMarkerIsCreated(IFile file,
			String errorMessage, int line) throws CoreException {
		IMarker[] markers = AbstractResourceMarkerTest.findMarkers(
				file, fResourceMarkerType, errorMessage, true);

		assertTrue("Resource Marker is found for type: " + fResourceMarkerType + ", message: [" + errorMessage + "] at line: " + line, (markers == null || markers.length == 0));

		if (markers != null) {
			for (IMarker m : markers) {
				Integer l = m.getAttribute(IMarker.LINE_NUMBER, -1);
				if (l != null && line == l.intValue()) {
					fail("Resource Marker is found for type: " + fResourceMarkerType + ", message: [" + errorMessage + "] at line: " + line);
				}
			}
		}
	}
}
