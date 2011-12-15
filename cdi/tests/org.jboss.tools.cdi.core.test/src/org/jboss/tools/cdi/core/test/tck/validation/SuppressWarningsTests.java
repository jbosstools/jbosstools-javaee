/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.tck.validation;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.preferences.CDIPreferences;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 * See https://issues.jboss.org/browse/JBIDE-10187
 */
public class SuppressWarningsTests extends ValidationTest {

	private void modifyPreferences() throws Exception{
		IPreferenceStore store = CDICorePlugin.getDefault().getPreferenceStore();
		store.putValue(CDIPreferences.PRODUCER_ANNOTATED_INJECT, CDIPreferences.WARNING);
		((IPersistentPreferenceStore)store).save();
	}

	private void restorePreferences() throws Exception{
		IPreferenceStore store = CDICorePlugin.getDefault().getPreferenceStore();
		store.putValue(CDIPreferences.PRODUCER_ANNOTATED_INJECT, CDIPreferences.ERROR);
		((IPersistentPreferenceStore)store).save();
	}

	public void testClass() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME, ".*"), 8);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherFish.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME, ".*"), 7, 12);
	}

	public void testFieldWithSuppressInParentElement() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME, ".*"), 13);
	}

	public void testField() throws Exception {
		try {
			modifyPreferences();
			boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
			IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
			TestUtil.validate(file);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.PRODUCER_ANNOTATED_INJECT, 17);
			AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_ANNOTATED_INJECT, 19);
	
			file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherFish.java");
			AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_ANNOTATED_INJECT, 15, 17, 23);
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		} finally {
			restorePreferences();
		}
	}

	public void testParam() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 22);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 31);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherFish.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 20, 24, 28, 31);
	}

	public void testMultipleSuppress() throws Exception {
		try {
			modifyPreferences();
			boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
			IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
			TestUtil.validate(file);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 27);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.PRODUCER_ANNOTATED_INJECT, 26);
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		} finally {
			restorePreferences();
		}
	}

	public void testErrorSuppress() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		TestUtil.validate(file);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 27);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_ANNOTATED_INJECT, 17, 19, 26);
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

	public void testShortName() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 35);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherFish.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 31);
	}
}