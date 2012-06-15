/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.test.validation;

import java.util.Iterator;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.jst.web.kb.WebKbPlugin;
import org.jboss.tools.jst.web.kb.preferences.ELSeverityPreferences;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * 
 * @author Victor V. Rubezhny
 *
 */
public class JSFAsYouTypeValidationTest extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "JSF2KickStartWithoutLibs";
	private static final String PAGE_NAME = "WebContent/pages/inputname.xhtml";

	private static final String [][] EL2VALIDATE = 
		{ 
			{"#{user.names}", "\"names\" cannot be resolved"}, 
			{"#{suser.name}", "\"suser\" cannot be resolved"},
			{"#{['}", "EL syntax error: Expecting expression."}
		};

	private static final int MAX_SECONDS_TO_WAIT = 10;
	public static final String MARKER_TYPE = "org.jboss.tools.common.validation.asyoutype"; //$NON-NLS-1$
	public static final String EL2FIND_START = "#{";
	public static final String EL2FIND_END = "}";
	
	private static boolean isSuspendedValidationDefaultValue;

	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		isSuspendedValidationDefaultValue = ValidationFramework.getDefault().isSuspended();
		ValidationFramework.getDefault().suspendAllValidation(false);
	}
	
	public void tearDown() throws Exception {
		ValidationFramework.getDefault().suspendAllValidation(isSuspendedValidationDefaultValue);
	}

	public void testAsYouTypeInJavaValidation() throws JavaModelException, BadLocationException {
		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);
		openEditor(PAGE_NAME);
		IPreferenceStore store = WebKbPlugin.getDefault().getPreferenceStore();
		String defaultValidateUnresolvedEL = SeverityPreferences.ENABLE;
		String defaultUnknownELVariableName = SeverityPreferences.IGNORE;
		try {
			defaultValidateUnresolvedEL = store.getString(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL);
			defaultUnknownELVariableName = store.getString(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME);
			store.setValue(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, SeverityPreferences.ENABLE);
			store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, SeverityPreferences.ERROR);
			for (int i = 0; i < EL2VALIDATE.length; i++) {
				doAsYouTipeInJavaValidationTest(EL2VALIDATE[i][0], EL2VALIDATE[i][1]);
			}
		} finally {
			store.setValue(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, defaultValidateUnresolvedEL);
			store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, defaultUnknownELVariableName);
			closeEditor();	
		}
	}
	
	public void doAsYouTipeInJavaValidationTest(String elToValidate,
			String errorMessage) throws JavaModelException, BadLocationException {
		String documentContent = document.get();
		int start = (documentContent == null ? -1 : documentContent
				.indexOf(EL2FIND_START));
		assertFalse("No EL found in Java Strings: Starting '" + EL2FIND_START
				+ "' characters are not found in document", (start == -1));
		int end = (documentContent == null ? -1 : documentContent.indexOf(
				EL2FIND_END, start));
		assertFalse("EL is not closed in Java Strings: Ending '"
				+ EL2FIND_START + "' characters are not found in document",
				(end == -1));

		int offset = start;
		int length = end - start + EL2FIND_END.length();

		IProgressMonitor monitor = new NullProgressMonitor();
		document.replace(start, length, elToValidate);

		end = start + elToValidate.length();

		TemporaryAnnotation problemAnnotation = waitForProblemAnnotationAppearance(
				start, end, MARKER_TYPE, MAX_SECONDS_TO_WAIT);
		assertNotNull("No ProblemAnnotation found for Marker Type: "
				+ MARKER_TYPE, problemAnnotation);

		String message = problemAnnotation.getText();
		assertEquals(
				"Not expected error message found in ProblemAnnotation. Expected: ["
						+ errorMessage + "], Found: [" + message + "]",
				errorMessage, message);
	}
	
	private TemporaryAnnotation waitForProblemAnnotationAppearance(
			final int start, final int end, final String markerType,
			final int seconds) {
		final TemporaryAnnotation[] result = new TemporaryAnnotation[] { null };

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				int secondsLeft = seconds;
				while (secondsLeft-- > 0) {
					JobUtils.delay(1000);

					// clean deffered events
					while (Display.getCurrent().readAndDispatch())
						;

					IAnnotationModel annotationModel = getAnnotationModel(textEditor);
					boolean found = false;
					Iterator it = annotationModel.getAnnotationIterator();
					while (!found && it.hasNext()) {
						Object o = it.next();

						if (!(o instanceof TemporaryAnnotation))
							continue;

						TemporaryAnnotation temporaryAnnotation = (TemporaryAnnotation) o;
						Position position = annotationModel
								.getPosition(temporaryAnnotation);

						if (position.getOffset() < start
								|| position.getOffset() >= end)
							continue;

						if (position.getOffset() + position.getLength() >= end)
							continue;

						if (temporaryAnnotation.getAttributes() == null && temporaryAnnotation.getAttributes().isEmpty())
							continue;
						
						Object value = temporaryAnnotation.getAttributes().get(MARKER_TYPE);
						
						if (Boolean.TRUE != value)
							continue;

						result[0] = temporaryAnnotation;
						return;
					}
				}
			}
		});

		return result[0];
	}

	protected IAnnotationModel getAnnotationModel(ITextEditor editor) {
		final IDocumentProvider documentProvider = editor.getDocumentProvider();
		if (documentProvider == null) {
			return null;
		}
		return documentProvider.getAnnotationModel(editor.getEditorInput());
	}

}
