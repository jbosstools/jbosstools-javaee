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

	private void restorePreferences(String preference, IFile file) throws Exception{
		IPreferenceStore store = CDICorePlugin.getDefault().getPreferenceStore();
		store.putValue(preference, CDIPreferences.ERROR);
		((IPersistentPreferenceStore)store).save();
		TestUtil.validate(file);
	}

	private void modifyPreferences() throws Exception{
		modifyPreferences(CDIPreferences.PRODUCER_ANNOTATED_INJECT);
	}

	private void restorePreferences(IFile file) throws Exception{
		restorePreferences(CDIPreferences.PRODUCER_ANNOTATED_INJECT, file);
	}

	public void testWOSuppress() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherFish.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 20, 24, 28, 31, 39, 42, 45);
	}

	public void testClass() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME[getVersionIndex()], ".*"), 8);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherFish.java");
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME[getVersionIndex()], ".*"), 7, 12, 38);
	}

	public void testFieldWithSuppressInParentElement() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME[getVersionIndex()], ".*"), 13);
	}

	public void testField() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		IFile file2 = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherFish.java");
		try {
			modifyPreferences();
			boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
			TestUtil.validate(file);
			getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.PRODUCER_ANNOTATED_INJECT[getVersionIndex()], 17);
			getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.PRODUCER_ANNOTATED_INJECT[getVersionIndex()], 19);
	
			getAnnotationTest().assertAnnotationIsCreated(file2, CDIValidationMessages.PRODUCER_ANNOTATED_INJECT[getVersionIndex()], 15, 17, 23);
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		} finally {
			restorePreferences(file);
			restorePreferences(file2);
		}
	}

	public void testParam() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 22);
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 31);
	}

	public void testMultipleSuppress() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		try {
			modifyPreferences();
			boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
			TestUtil.validate(file);
			getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 27);
			getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.PRODUCER_ANNOTATED_INJECT[getVersionIndex()], 26);
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		} finally {
			restorePreferences(file);
		}
	}

	public void testErrorSuppress() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		TestUtil.validate(file);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 27);
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.PRODUCER_ANNOTATED_INJECT[getVersionIndex()], 17, 19, 26);
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

	public void testNameAll() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 35);
	}

	public void testGroupName() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 49);
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testEL() throws Exception {
		String message = NLS.bind(ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, "abc");
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, message, 39);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherFish.java");
		getAnnotationTest().assertAnnotationIsCreated(file, message, 34);
	}

	public void testWarningsOnClassNameRegion() throws Exception {
		String message = NLS.bind(CDIValidationMessages.NOT_PASSIVATION_CAPABLE_BEAN[getVersionIndex()], "Rabbit", "SessionScoped");
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Rabbit.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, message, 9);

		message = NLS.bind(CDIValidationMessages.NOT_PASSIVATION_CAPABLE_BEAN[getVersionIndex()], "AnotherRabbit", "SessionScoped");
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherRabbit.java");
		getAnnotationTest().assertAnnotationIsCreated(file, message, 8);
	}

	/**
	 * In creating some markers, validator uses ITypeDeclaration as text source reference.
	 * It should convert it to IJavaSourceReference when it is relevant.
	 * 
	 * @throws Exception
	 */
	public void testWarningsOnTyped() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Rabbit.java");
		try {
			modifyPreferences(CDIPreferences.ILLEGAL_TYPE_IN_TYPED_DECLARATION);
			boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
			TestUtil.validate(file);
			getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.ILLEGAL_TYPE_IN_TYPED_DECLARATION[getVersionIndex()], 13);
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		} finally {
			restorePreferences(CDIPreferences.ILLEGAL_TYPE_IN_TYPED_DECLARATION, file);
		}

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherRabbit.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.ILLEGAL_TYPE_IN_TYPED_DECLARATION[getVersionIndex()], 11);
	}

	public void testMultipleSuppressFromElementAndItsParent() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 45);
		getAnnotationTest().assertAnnotationIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME[getVersionIndex()], ".*"), 44);
	}

	public void testWarningsOnAnnotatedParam() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		try {
			modifyPreferences(CDIPreferences.OBSERVER_ANNOTATED_INJECT);
			boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
			TestUtil.validate(file);
			getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.OBSERVER_ANNOTATED_INJECT[getVersionIndex()], 51);
			getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.OBSERVER_ANNOTATED_INJECT[getVersionIndex()], 52);
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		} finally {
			restorePreferences(CDIPreferences.OBSERVER_ANNOTATED_INJECT, file);
		}

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherFish.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.OBSERVER_ANNOTATED_INJECT[getVersionIndex()], 44, 45);
	}
}