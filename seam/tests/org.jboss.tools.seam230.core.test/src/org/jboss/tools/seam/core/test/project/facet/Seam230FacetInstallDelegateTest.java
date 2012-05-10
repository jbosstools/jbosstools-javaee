/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.core.test.project.facet;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.internal.EventManager;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.AntCopyUtils;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.project.facet.Seam23FacetInstallDelegate;

/**
 * @author Alexey Kazakov
 */
public class Seam230FacetInstallDelegateTest extends AbstractSeam2FacetInstallDelegateTest {

	protected static final String SEAM_2_2_3 = "Seam 2.2.3";

	public Seam230FacetInstallDelegateTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		suspendAllValidation = ValidationFramework.getDefault().isSuspended();
		ValidationFramework.getDefault().suspendAllValidation(true);

		ResourcesPlugin.getWorkspace().removeResourceChangeListener(EventManager.getManager());
		// commented to run tests on wtp 3.0.4 build
		// ws.removeResourceChangeListener( ValManager.getDefault() );
		// EventManager.getManager().shutdown();

		assertSeamHomeAvailable();

		seam2Facet = ProjectFacetsManager.getProjectFacet("jst.seam");
		seam2FacetVersion = seam2Facet.getVersion("2.3");

		javaVersion = JavaFacet.VERSION_1_6;
		dynamicWebVersion = ProjectFacetsManager.getProjectFacet("jst.web").getVersion("3.0");
		javaFacesVersion = ProjectFacetsManager.getProjectFacet("jst.jsf").getVersion("2.0");

		File folder = getSeamHomeFolder();

		SeamRuntimeManager.getInstance().addRuntime(SEAM_2_2_3,	folder.getAbsolutePath(), SeamVersion.SEAM_2_3, true);
		seamRuntime = SeamRuntimeManager.getInstance().findRuntimeByName(SEAM_2_2_3);
		IProject war = (IProject) ResourcesPlugin.getWorkspace().getRoot().findMember("warprj");
		warProject = (war != null ? ProjectFacetsManager.create(war, false, null) : createSeamWarProject("warprj"));
		IProject ear = (IProject) ResourcesPlugin.getWorkspace().getRoot().findMember("earprj");
		earProject = (ear != null ? ProjectFacetsManager.create(ear, false, null) : createSeamEarProject("earprj"));
	}

	@Override
	protected IDataModel createSeamDataModel(String deployType) {
		IDataModel dataModel = super.createSeamDataModel(deployType);
		dataModel.setStringProperty(
				ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, SEAM_2_2_3);
		dataModel.setBooleanProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_LIBRARIES_COPYING, true);

		return dataModel;
	}

	private static Set<String> convertToStrings(AntCopyUtils.FileSet fileSet) {
		Set<String> seamgenlibs = new HashSet<String>();
		List<Pattern> list = fileSet.getIncluded();
		for (Pattern pattern : list) {
			seamgenlibs.add(pattern.pattern());
		}
		return seamgenlibs;
	}

	@Override
	protected Set<String> getWarLibs() {
		AntCopyUtils.FileSet fileSet = Seam23FacetInstallDelegate.getWarLibFileSet(seamRuntime);
		return convertToStrings(fileSet);
	}

	@Override
	protected Set<String> getEarLibs() {
		AntCopyUtils.FileSet fileSet = Seam23FacetInstallDelegate.getEarLibFileSet(seamRuntime);
		return convertToStrings(fileSet);
	}

	@Override
	protected Set<String> getEarWarLibs() {
		AntCopyUtils.FileSet fileSet = Seam23FacetInstallDelegate.getWarLibFileSetForEar(seamRuntime);
		return convertToStrings(fileSet);
	}

	@Override
	protected boolean shouldCheckJBossAppXML() {
		return false;
	}
}