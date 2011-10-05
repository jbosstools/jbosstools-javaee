package org.jboss.tools.cdi.seam.config.core;

import java.util.HashSet;
import java.util.Set;

import org.jboss.tools.common.preferences.SeverityPreferences;

public class CDISeamConfigPreferences extends SeverityPreferences {
	public static final Set<String> SEVERITY_OPTION_NAMES = new HashSet<String>();

	private static CDISeamConfigPreferences INSTANCE = new CDISeamConfigPreferences();

	public static final String UNRESOLVED_TYPE = INSTANCE.createSeverityOption("unresolvedType");
	public static final String UNRESOLVED_MEMBER = INSTANCE.createSeverityOption("unresolvedMember");
	public static final String UNRESOLVED_METHOD = INSTANCE.createSeverityOption("unresolvedMethod");
	public static final String UNRESOLVED_CONSTRUCTOR = INSTANCE.createSeverityOption("unresolvedConstructor");
	public static final String ANNOTATION_EXPECTED = INSTANCE.createSeverityOption("annotationExpected");

	public static final String INLINE_BEAN_TYPE_MISMATCH = INSTANCE.createSeverityOption("inlineBeanTypeMismatch");
	public static final String ABSTRACT_TYPE_IS_CONFIGURED_AS_BEAN = INSTANCE.createSeverityOption("abstractTypeIsConfiguredAsBean");
	public static final String BEAN_CONSTRUCTOR_IS_MISSING = INSTANCE.createSeverityOption("beanConstructorIsMissing");

	public static CDISeamConfigPreferences getInstance() {
		return INSTANCE;
	}

	private CDISeamConfigPreferences() {}

	@Override
	protected Set<String> getSeverityOptionNames() {
		return SEVERITY_OPTION_NAMES;
	}

	@Override
	protected String createSeverityOption(String shortName) {
		String name = getPluginId() + ".validator.problem." + shortName; //$NON-NLS-1$
		SEVERITY_OPTION_NAMES.add(name);
		return name;
	}

	@Override
	protected String getPluginId() {
		return CDISeamConfigCorePlugin.PLUGIN_ID;
	}

}
