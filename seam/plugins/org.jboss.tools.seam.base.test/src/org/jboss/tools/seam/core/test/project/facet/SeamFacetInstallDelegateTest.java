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
package org.jboss.tools.seam.core.test.project.facet;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetPreInstallDelegate;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

public class SeamFacetInstallDelegateTest extends AbstractSeamFacetTest {

	public SeamFacetInstallDelegateTest(String name) {
		super(name);
	}

	public void testCreateWar() throws CoreException, IOException {
		
		final IFacetedProject fproj = createSeamWarProject("seamwar");
		
	}


	public void testCreateEar() throws CoreException, IOException {
		
		final IFacetedProject fproj = createSeamEarProject("seamear");
	}
	
	public void testCreateCustomProject() throws CoreException, IOException {

		IDataModel createSeamDataModel = createSeamDataModel("war");
		createSeamDataModel.setProperty(ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME, "x.y.z");
		
		final IFacetedProject fproj = createSeamProject("customProject",createSeamDataModel);
		JobUtils.waitForIdle(500);
		assertTrue(fproj.getProject().findMember("src/hot/x/y/z").exists());
	}
	
	public void testJiraJbide1544() throws CoreException, IOException {

		final String catalogName = "catalog1";
		final String schemaName = "schema1";
		
		IDataModel createSeamDataModel = createSeamDataModel("war");
		createSeamDataModel.setProperty(ISeamFacetDataModelProperties.DB_DEFAULT_CATALOG_NAME, catalogName);
		createSeamDataModel.setProperty(ISeamFacetDataModelProperties.DB_DEFAULT_SCHEMA_NAME, schemaName);		
		IFacetedProject fproj = createSeamProject("customSchemaAndCatalog",createSeamDataModel);
		IFile  persistence = (IFile)fproj.getProject().findMember("src/main/META-INF/persistence.xml");
		assertTrue(persistence.exists());
		boolean schemaExists = ResourcesUtils.findLineInFile(persistence, ".*" +
					NLS.bind(
						SeamFacetPreInstallDelegate.PROP_DECL, 
						new String[]{
							ISeamFacetDataModelProperties.DB_DEFAULT_SCHEMA_NAME.replace(".","\\."),
							schemaName}));
		boolean catalogExists = ResourcesUtils.findLineInFile(persistence, ".*" +
						NLS.bind(
							SeamFacetPreInstallDelegate.PROP_DECL, 
							new String[]{
								ISeamFacetDataModelProperties.DB_DEFAULT_CATALOG_NAME.replace(".","\\."),
								catalogName}));		
		assertTrue(
			NLS.bind(
				"Cannot find ''{0}'' property in persistence.xml file",
					new String[]{
						ISeamFacetDataModelProperties.DB_DEFAULT_SCHEMA_NAME})
			, schemaExists);
		
		assertTrue(
			NLS.bind(
				"Cannot find ''{0}'' property in persistence.xml file",
					new String[]{
					ISeamFacetDataModelProperties.DB_DEFAULT_CATALOG_NAME})
			, catalogExists);
		
		createSeamDataModel = createSeamDataModel("war");
		fproj = createSeamProject("noSchemaAndCatalog",createSeamDataModel);
		persistence = (IFile)fproj.getProject().findMember("src/main/META-INF/persistence.xml");
		assertTrue(persistence.exists());
		schemaExists = ResourcesUtils.findLineInFile(persistence, ".*" +
					NLS.bind(
						SeamFacetPreInstallDelegate.PROP_DECL, 
						new String[]{
							ISeamFacetDataModelProperties.DB_DEFAULT_SCHEMA_NAME.replace(".","\\."),
							".*"}));
		catalogExists = ResourcesUtils.findLineInFile(persistence, ".*" +
						NLS.bind(
							SeamFacetPreInstallDelegate.PROP_DECL, 
							new String[]{
								ISeamFacetDataModelProperties.DB_DEFAULT_CATALOG_NAME.replace(".","\\."),
								".*"}));		
		assertTrue(
				NLS.bind(
					"''{0}'' property mustn't be in persistence.xml file",
						new String[]{
							ISeamFacetDataModelProperties.DB_DEFAULT_SCHEMA_NAME})
				, !schemaExists);
			
			assertTrue(
				NLS.bind(
					"''{0}'' property mustn't be in persistence.xml file",
						new String[]{
						ISeamFacetDataModelProperties.DB_DEFAULT_CATALOG_NAME})
				, !catalogExists);

	}

}
