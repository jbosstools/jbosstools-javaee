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

import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.common.preferences.SeverityPreferences;

/**
 * @author Alexey Kazakov
 */
public class CoreValidationTest extends ValidationTest {

	/**
	 * https://jira.jboss.org/browse/JBIDE-6507
	 *  
	 * @throws Exception
	 */
	public void testDisabledValidator() throws Exception {
		IPreferenceStore preferenceStore = CDICorePlugin.getDefault().getPreferenceStore();
		preferenceStore.setValue(SeverityPreferences.ENABLE_BLOCK_PREFERENCE_NAME, false);
		((IPersistentPreferenceStore)preferenceStore).save();

		assertNull("CDICoreValidator is still enabled.", getCDIValidator());

		preferenceStore.setValue(SeverityPreferences.ENABLE_BLOCK_PREFERENCE_NAME, true);
		((IPersistentPreferenceStore)preferenceStore).save();

		assertNotNull("CDICoreValidator is disabled.", getCDIValidator());
	}
}