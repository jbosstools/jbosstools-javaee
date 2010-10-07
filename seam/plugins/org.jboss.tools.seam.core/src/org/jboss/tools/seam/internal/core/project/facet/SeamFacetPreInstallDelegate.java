/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.seam.internal.core.project.facet;

import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.DriverManager;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;


/**
 * TBD
 * 
 * @author eskimo
 */
public class SeamFacetPreInstallDelegate implements IDelegate {

	/**
	 * Indentation for hibernate property declaration
	 */
	public static final String PROP_INDENT = "\n         ";

	/**
	 * Property declaration in persistence.xml
	 */
	public static final String PROP_DECL = "<property name=\"{0}\" value=\"{1}\"/>";

	private static final String EMPTY_STRING = "";

	/**
	 * Execute pre-install operations for installed facet
	 * 
	 * @param project
	 *            created after finish pressed in wizard
	 * @param fv
	 *            -IProjectFacetversion described facet that will be installed
	 * @param config
	 *            - IDataModel that provide data collected from user
	 * @param monitor
	 *            - IProgressMonitor
	 * @throws CoreException
	 *             when meet the problems
	 */
	public void execute(IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		IDataModel model = (IDataModel) config;

		if (model
				.getProperty(ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE) != null) {
			IConnectionProfile connProfile = ProfileManager
					.getInstance()
					.getProfileByName(
							model.getProperty(
									ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE)
									.toString());
			if (connProfile != null) {
				Properties props = connProfile.getBaseProperties(); // Properties("org.eclipse.datatools.connectivity.db.generic.connectionProfile");

				// Collect properties name from DTP Connection Profile
				model.setProperty(
						ISeamFacetDataModelProperties.DB_USER_NAME,
						props.get("org.eclipse.datatools.connectivity.db.username") == null //$NON-NLS-1$
						? ""	: props.get("org.eclipse.datatools.connectivity.db.username").toString()); //$NON-NLS-1$ //$NON-NLS-2$

				model.setProperty(
						ISeamFacetDataModelProperties.JDBC_DRIVER_CLASS_NAME,
						props.get("org.eclipse.datatools.connectivity.db.driverClass") == null //$NON-NLS-1$
						? ""	: props.get("org.eclipse.datatools.connectivity.db.driverClass").toString()); //$NON-NLS-1$ //$NON-NLS-2$

				model.setProperty(
						ISeamFacetDataModelProperties.DB_USER_PASSWORD,
						props.get("org.eclipse.datatools.connectivity.db.password") == null //$NON-NLS-1$
						? ""	: props.get("org.eclipse.datatools.connectivity.db.password").toString()); //$NON-NLS-1$ //$NON-NLS-2$

				model.setProperty(
						ISeamFacetDataModelProperties.JDBC_URL_FOR_DB,
						props.get("org.eclipse.datatools.connectivity.db.URL") == null //$NON-NLS-1$
						? ""	: props.get("org.eclipse.datatools.connectivity.db.URL").toString()); //$NON-NLS-1$ //$NON-NLS-2$

				if(props.get("org.eclipse.datatools.connectivity.driverDefinitionID")!=null) {
					model.setProperty(
							ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH,
							DriverManager
									.getInstance()
									.getDriverInstanceByID(
											props.get(
													"org.eclipse.datatools.connectivity.driverDefinitionID").toString()).getJarListAsArray()); //$NON-NLS-1$
				}
			}
		}

		String defaultSchema = (String) model
				.getProperty(ISeamFacetDataModelProperties.DB_DEFAULT_SCHEMA_NAME);

		if (!EMPTY_STRING.equals(defaultSchema)) {
			model.setStringProperty(
					ISeamFacetDataModelProperties.DB_SCHEMA_NAME,
					PROP_INDENT
							+ NLS.bind(
									PROP_DECL,
									new String[] {
											ISeamFacetDataModelProperties.DB_DEFAULT_SCHEMA_NAME,
											defaultSchema }));
		}

		String defaultCatalog = (String) model
				.getProperty(ISeamFacetDataModelProperties.DB_DEFAULT_CATALOG_NAME);

		if (!EMPTY_STRING.equals(defaultCatalog)) {
			model.setStringProperty(
					ISeamFacetDataModelProperties.DB_CATALOG_NAME,
					PROP_INDENT
							+ NLS.bind(
									PROP_DECL,
									new String[] {
											ISeamFacetDataModelProperties.DB_DEFAULT_CATALOG_NAME,
											defaultCatalog }));
		}
	}
}