package org.jboss.tools.cdi.seam.config.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class CDISeamPreferencesMessages extends NLS {
	private static final String BUNDLE_NAME = CDISeamPreferencesMessages.class.getName();

	// Seam Validator Preference page
	public static String CDISeamValidatorConfigurationBlock_common_description;

	//Section Config
	public static String CDIValidatorConfigurationBlock_section_config;
	public static String CDIValidatorConfigurationBlock_pb_unresolvedType_label;
	public static String CDIValidatorConfigurationBlock_pb_unresolvedMember_label;
	public static String CDIValidatorConfigurationBlock_pb_unresolvedMethod_label;
	public static String CDIValidatorConfigurationBlock_pb_unresolvedConstructor_label;
	public static String CDIValidatorConfigurationBlock_pb_annotationExpected_label;

	//Section Solder
	public static String CDIValidatorConfigurationBlock_section_solder;
	public static String CDIValidatorConfigurationBlock_pb_ambiguousGenericConfigurationPoint_label;
	public static String CDIValidatorConfigurationBlock_pb_wrongTypeOfGenericConfigurationPoint_label;
	

	public static String CDI_SEAM_VALIDATOR_PREFERENCE_PAGE_TITLE;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, CDISeamPreferencesMessages.class);
	}
}
