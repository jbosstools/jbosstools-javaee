/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.tck.validation;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.internal.core.validation.CDICoreValidator;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.jst.web.kb.internal.validation.ValidationContext;
import org.jboss.tools.jst.web.kb.validation.IValidator;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class ValidationTest extends TCKTest {

	/**
	 * https://jira.jboss.org/browse/JBIDE-6507
	 *  
	 * @throws Exception
	 */
	public void testDisabledValidator() throws Exception {
		IPreferenceStore preferenceStore = CDICorePlugin.getDefault().getPreferenceStore();
		preferenceStore.setValue(SeverityPreferences.ENABLE_BLOCK_PREFERENCE_NAME, false);
		((IPersistentPreferenceStore)preferenceStore).save();

		assertNull("CDICoreValidator is not be disabled.", getCDIValidator());

		preferenceStore.setValue(SeverityPreferences.ENABLE_BLOCK_PREFERENCE_NAME, true);
		((IPersistentPreferenceStore)preferenceStore).save();

		assertNotNull("CDICoreValidator is disabled.", getCDIValidator());
	}

	private CDICoreValidator getCDIValidator() {
		ValidationContext context = new ValidationContext(tckProject);
		List<IValidator> validators = context.getValidators();
		for (IValidator validator : validators) {
			if(validator instanceof CDICoreValidator) {
				return (CDICoreValidator)validator;
			}
		}
		return null;
	}

	public static int getMarkersNumber(IResource resource) {
		return AbstractResourceMarkerTest.getMarkersNumberByGroupName(resource, null);
	}

	protected static void assertMarkerIsCreated(IResource resource, String pattern, int... expectedLines) throws CoreException {
		assertMarkerIsCreated(resource, pattern, true, expectedLines);
	}

	protected static void assertMarkerIsCreated(IResource resource, String message, boolean pattern, int... expectedLines) throws CoreException {
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, AbstractResourceMarkerTest.MARKER_TYPE, pattern?convertMessageToPatern(message):message, pattern, expectedLines);
	}

	protected static void assertMarkerIsNotCreated(IResource resource, String message) throws CoreException {
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, AbstractResourceMarkerTest.MARKER_TYPE, convertMessageToPatern(message));
	}

	protected static void assertMarkerIsNotCreated(IResource resource, String message, int expectedLine) throws CoreException {
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, AbstractResourceMarkerTest.MARKER_TYPE, convertMessageToPatern(message), expectedLine);
	}

	protected static void assertMarkerIsCreatedForGivenPosition(IResource resource, String message, int lineNumber, int startPosition, int endPosition) throws CoreException {
		AbstractResourceMarkerTest.assertMarkerIsCreatedForGivenPosition(resource, AbstractResourceMarkerTest.MARKER_TYPE, convertMessageToPatern(message), lineNumber, startPosition, endPosition);
	}

	protected static String convertMessageToPatern(String message) {
		return message.replace("[", "\\[").replace("]", "\\]").replace("<", "\\<").replace(">", "\\>").replace("(", "\\(").replace(")", "\\)")
				.replace("{", "\\{").replace("}", "\\}").replace("'", "\\'");
	}
}