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
package org.jboss.tools.seam.ui.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.Viewer;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.Seam2FacetInstallDelegate;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetAbstractInstallDelegate;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetInstallDelegate;
import org.jboss.tools.seam.internal.core.project.facet.AntCopyUtils.FileSet;
import org.jboss.tools.seam.internal.core.project.facet.AntCopyUtils.FileSetFileFilter;

/**
 * 
 * @author snjeza
 */
public class WarFileSetProvider extends AbstractFileSetContentProvider {

	private boolean isWarConfiguration;
	private SeamVersion version;

	public WarFileSetProvider(boolean isWarConfiguration, SeamVersion version) {
		super();
		this.isWarConfiguration = isWarConfiguration;
		this.version = version;
	}

	private FileSet getFileSet() {
		if (isWarConfiguration) {
			if (SeamVersion.SEAM_1_2.equals(version)) {
				return SeamFacetInstallDelegate.JBOSS_WAR_LIB_FILESET_WAR_CONFIG;
			} else {
				return Seam2FacetInstallDelegate.SEAM2_JBOSS_WAR_LIB_FILESET_WAR_CONFIG;
			}
		} else {
			if (SeamVersion.SEAM_1_2.equals(version)) {
				return SeamFacetInstallDelegate.JBOSS_WAR_LIB_FILESET_EAR_CONFIG;
			} else {
				return Seam2FacetInstallDelegate.SEAM2_JBOSS_WAR_LIB_FILESET_EAR_CONFIG;
			}
		}
	}
	
	private String getDroolsLibPath() {
		if (SeamVersion.SEAM_1_2.equals(version)) {
			return SeamFacetInstallDelegate.DROOLS_LIB_SEAM_RELATED_PATH;
		} else {
			return Seam2FacetInstallDelegate.DROOLS_LIB_SEAM_RELATED_PATH;
		}
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof File) {
			Set<File> set = new TreeSet<File>();
			File seamHomePath = (File) newInput;
			set.addAll(getFiles(seamHomePath));
			File seamLibFolder = new File(seamHomePath, SeamFacetAbstractInstallDelegate.SEAM_LIB_RELATED_PATH);
			set.addAll(getFiles(seamLibFolder));
			File droolsLibFolder = new File(seamHomePath, getDroolsLibPath());
			set.addAll(getFiles(droolsLibFolder));
			elements = set.toArray(new File[0]);
		} else {
			elements = ZERO_ARRAY;
		}
		sort();
	}

	private List<File> getFiles(File dir) {
		List<File> list = new ArrayList<File>();
		if (dir == null || !dir.isDirectory()) {
			return list;
		}
		FileSet set = getFileSet().dir(dir);
		FileSetFileFilter filter = new FileSetFileFilter(set);
		File[] files = dir.listFiles(filter);
		for (int i = 0; i < files.length; i++) {
			list.add(files[i]);
		}
		return list;
	}
}
