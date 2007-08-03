/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 

package org.jboss.tools.seam.internal.core.project.facet;

import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.DriverManager;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

public class SeamFacetPreInstallDelegate implements IDelegate {

	public void execute(IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		IDataModel model = (IDataModel)config;
		if(model.getProperty(ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE)==null) return;
		IConnectionProfile connProfile = ProfileManager.getInstance().getProfileByName(model.getProperty(ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE).toString());
		Properties props = connProfile.getProperties("org.eclipse.datatools.connectivity.db.generic.connectionProfile");

		// Collect properties name from DTP Connection Profile
		model.setProperty(ISeamFacetDataModelProperties.DB_USER_NAME,props.get("org.eclipse.datatools.connectivity.db.username").toString());
		model.setProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_CLASS_NAME,props.get("org.eclipse.datatools.connectivity.db.driverClass").toString());
		model.setProperty(ISeamFacetDataModelProperties.DB_USERP_PASSWORD,props.get("org.eclipse.datatools.connectivity.db.password").toString());
		model.setProperty(ISeamFacetDataModelProperties.JDBC_URL_FOR_DB,props.get("org.eclipse.datatools.connectivity.db.URL").toString());
		model.setProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH,DriverManager.getInstance().getDriverInstanceByID(
		props.get("org.eclipse.datatools.connectivity.driverDefinitionID").toString()).getJarListAsArray());
		
	}

}
