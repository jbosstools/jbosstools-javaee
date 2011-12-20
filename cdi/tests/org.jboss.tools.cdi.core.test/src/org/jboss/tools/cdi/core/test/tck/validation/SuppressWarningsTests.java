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
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.preferences.CDIPreferences;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.jst.web.kb.internal.validation.ELValidationMessages;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 * See https://issues.jboss.org/browse/JBIDE-10187
 */
public class SuppressWarningsTests extends ValidationTest {

	private void modifyPreferences(String preference) throws Exception{
		IPreferenceStore store = CDICorePlugin.getDefault().getPreferenceStore();
		store.putValue(preference, CDIPreferences.WARNING);
		((IPersistentPreferenceStore)store).save();
	}

	private void restorePreferences(String preference) throws Exception{
		IPreferenceStore store = CDICorePlugin.getDefault().getPreferenceStore();
		store.putValue(preference, CDIPreferences.ERROR);
		((IPersistentPreferenceStore)store).save();
	}

	private void modifyPreferences() throws Exception{
		modifyPreferences(CDIPreferences.PRODUCER_ANNOTATED_INJECT);
	}

	private void restorePreferences() throws Exception{
		restorePreferences(CDIPreferences.PRODUCER_ANNOTATED_INJECT);
	}

	public void testClass() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME, ".*"), 8);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherFish.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME, ".*"), 7, 12, 38);
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
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 20, 24, 28, 31, 39);
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

	public void testNameAll() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 35);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherFish.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 20, 24, 28, 31, 39);
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testEL() throws Exception {
		String message = NLS.bind(ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, "abc");
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, message, 39);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherFish.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, message, 34);
	}

	public void testWarningsOnClassNameRegion() throws Exception {
		String message = NLS.bind(CDIValidationMessages.NOT_PASSIVATION_CAPABLE_BEAN, "Rabbit", "SessionScoped");
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Rabbit.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, message, 9);

		message = NLS.bind(CDIValidationMessages.NOT_PASSIVATION_CAPABLE_BEAN, "AnotherRabbit", "SessionScoped");
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherRabbit.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, message, 8);
	}

	/**
	 * In creating some markers, validator uses ITypeDeclaration as text source reference.
	 * It should convert it to IJavaSourceReference when it is relevant.
	 * 
	 * @throws Exception
	 */
	public void testWarningsOnTyped() throws Exception {
		try {
			modifyPreferences(CDIPreferences.ILLEGAL_TYPE_IN_TYPED_DECLARATION);
			boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
			IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Rabbit.java");
			TestUtil.validate(file);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.ILLEGAL_TYPE_IN_TYPED_DECLARATION, 13);
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		} finally {
			restorePreferences(CDIPreferences.ILLEGAL_TYPE_IN_TYPED_DECLARATION);
		}

		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherRabbit.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_TYPE_IN_TYPED_DECLARATION, 11);
	}

	public void testMultipleSuppressFromElementAndItsParent() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 45);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME, ".*"), 44);
	}
}