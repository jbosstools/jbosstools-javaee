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
package org.jboss.tools.seam.core.internal.project.facet;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.j2ee.project.facet.IJ2EEModuleFacetInstallDataModelProperties;
import org.eclipse.jst.j2ee.web.project.facet.IWebFacetInstallDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

public class SeamFacetInstallDelegete extends Object implements IDelegate {

	public void execute(IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
		IDataModel model = (IDataModel)config;
		System.out.println("InstallDelegate invoked");

		for (Object property : model.getNestedModel("").getAllProperties()) {
			System.out.println(property + " + " + model.getProperty(property.toString()));
		}
		Properties propertiew = new Properties();
		
		
		// Collect data typed by user
		Collection properties = model.getAllProperties();
		// get WebContents folder path from model 
		String webContentFolder = model.getProperty(IJ2EEModuleFacetInstallDataModelProperties.CONFIG_FOLDER).toString();
		
		File destination = new File(project.getLocation().toFile(),webContentFolder);
		String seamHomeFolder = model.getProperty(ISeamFacetDataModelProperties.JBOSS_SEAM_HOME).toString();
		File source = new File(seamHomeFolder,"seam-gen/view");
		// TODO - copy veiw folder from seam-gen installation to
		copyViewFolder(source, destination, new HashMap<String, String>());
		// project location with filled out FIlterSet
		
		// TODO copy manifest and configuration resources the same way as view
		
		// TODO copy libraries/link libraries
		
		// TODO generate db support as seam-gen does
		
		// TODO may be generate RHDS studio feature to show it on projects view
		
		// TODO say JBoss AS adapter what kind of deployment to use
		
		// TODO ggenerate build.xml
	}

	protected void copyViewFolder(File viewSource, File viewDestination, HashMap<String, String> properties) {
		
		FilterSet filterSet = new FilterSet();
		for (Object	propertyName : properties.keySet()) {
			filterSet.addFilter(propertyName.toString(), properties.get(propertyName));
		}
		
		FilterSetCollection filters = new FilterSetCollection();
		filters.addFilterSet(filterSet);
		AntCopyUtils.copyFilesAndFolders(viewSource, viewDestination, filters, true);
		
	}
}
