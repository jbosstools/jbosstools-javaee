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
package org.jboss.tools.cdi.deltaspike.ui.preferences;

import org.jboss.tools.cdi.deltaspike.core.DeltaspikeCorePlugin;
import org.jboss.tools.cdi.deltaspike.core.DeltaspikeSeverityPreferences;
import org.jboss.tools.cdi.ui.preferences.IConfigurationBlockDescriptionProvider;
import org.jboss.tools.common.ui.preferences.SeverityConfigurationBlock.SectionDescription;

/**
 * @author Viacheslav Kabanovich
 */
public class DeltaspikeConfigBlockDescriptionProvider implements IConfigurationBlockDescriptionProvider {

	private static SectionDescription SECTION_DELTASPIKE = new SectionDescription(
		DeltaspikePreferencesMessages.CDIValidatorConfigurationBlock_section_deltaspike,
		new String[][]{
			{DeltaspikeSeverityPreferences.AMBIGUOUS_AUTHORIZER, DeltaspikePreferencesMessages.CDIValidatorConfigurationBlock_pb_ambiguousAuthorizer_label},
			{DeltaspikeSeverityPreferences.UNRESOLVED_AUTHORIZER, DeltaspikePreferencesMessages.CDIValidatorConfigurationBlock_pb_unresolvedAuthorizer_label},
			{DeltaspikeSeverityPreferences.INVALID_AUTHORIZER, DeltaspikePreferencesMessages.CDIValidatorConfigurationBlock_pb_invalidAuthorizer_label},
		},
		DeltaspikeCorePlugin.PLUGIN_ID
	);

	private static SectionDescription[] ALL_SECTIONS = {
		SECTION_DELTASPIKE
	};

	@Override
	public SectionDescription[] getSections() {
		return ALL_SECTIONS;
	}

}
