package org.jboss.tools.seam.ui.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.widget.editor.INamedElement;

/**
 * 
 * TODO move operations to core plugin
 */
public class SeamActionCreateOperation extends SeamBaseOperation{
	
	/**
	 * @param label
	 */
	public SeamActionCreateOperation() {
		super((SeamUIMessages.SEAM_ACTION_WIZARD_ACTION_CREATING_OPERATION));
	}

	@Override
	public List<String[]> getFileMappings(Map<String, Object> vars) {
		if("war".equals(vars.get(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS))) //$NON-NLS-1$
			return ACTION_WAR_MAPPING;
		else
			return ACTION_EAR_MAPPING;
	}

	public static final List<String[]> ACTION_WAR_MAPPING = new ArrayList<String[]>();

	public static final List<String[]> ACTION_EAR_MAPPING = new ArrayList<String[]>();

	static {
		// initialize war files mapping
		ACTION_WAR_MAPPING.add(new String[]{
				"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/ActionJavaBean.java", //$NON-NLS-1$ //$NON-NLS-2$
				"${" + IParameter.SEAM_PROJECT_SRC_ACTION + "}/${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_LOCAL_INTERFACE_NAME +"}.java"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ACTION_WAR_MAPPING.add(new String[]{
				"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/test/ActionTest.java", //$NON-NLS-1$ //$NON-NLS-2$
				"${" + IParameter.SEAM_TEST_PROJECT_LOCATION_PATH + "}/test-src/${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.java"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ACTION_WAR_MAPPING.add(new String[]{
				"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/test/testng.xml", //$NON-NLS-1$ //$NON-NLS-2$
				"${" + IParameter.SEAM_TEST_PROJECT_LOCATION_PATH + "}/test-src/${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.xml"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ACTION_WAR_MAPPING.add(new String[]{
				"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/view/action.xhtml", //$NON-NLS-1$ //$NON-NLS-2$
				"${" + IParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/${" + IParameter.SEAM_PAGE_NAME +"}.xhtml"});	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// initialize ear files mapping
		ACTION_EAR_MAPPING.add(new String[]{
				"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/ActionBean.java", //$NON-NLS-1$ //$NON-NLS-2$
				"${" + IParameter.SEAM_PROJECT_SRC_ACTION + "}/${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_BEAN_NAME +"}.java"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ACTION_EAR_MAPPING.add(new String[]{
				"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/Action.java", //$NON-NLS-1$ //$NON-NLS-2$
				"${" + IParameter.SEAM_PROJECT_SRC_ACTION + "}/${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_LOCAL_INTERFACE_NAME +"}.java"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ACTION_EAR_MAPPING.add(new String[]{
				"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/test/ActionTest.java", //$NON-NLS-1$ //$NON-NLS-2$
				"${" + IParameter.SEAM_TEST_PROJECT_LOCATION_PATH + "}/test-src/${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.java"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ACTION_EAR_MAPPING.add(new String[]{
				"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/test/testng.xml", //$NON-NLS-1$ //$NON-NLS-2$
				"${" + IParameter.SEAM_TEST_PROJECT_LOCATION_PATH + "}/test-src/${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.xml"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ACTION_EAR_MAPPING.add(ACTION_WAR_MAPPING.get(3));
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.wizard.SeamBaseOperation#getSessionBeanPackageName(org.eclipse.core.runtime.preferences.IEclipsePreferences, java.util.Map)
	 */
	@Override
	protected String getSessionBeanPackageName(IEclipsePreferences seamFacetPrefs, Map<String, INamedElement> wizardParams) {
		return wizardParams.get(IParameter.SEAM_PACKAGE_NAME).getValue().toString();
	}
}