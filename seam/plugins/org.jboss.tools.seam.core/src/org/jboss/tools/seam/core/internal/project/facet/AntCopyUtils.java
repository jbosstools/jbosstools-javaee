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
import java.io.IOException;
import java.util.Properties;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.ResourceUtils;
import org.jboss.tools.seam.core.SeamCorePlugin;


public class AntCopyUtils {
	public static void main(String[] args) {
		Project prj = new Project();
		prj.setBaseDir(new File("C:\\java\\jboss-seam-1.2.1.GA\\seam-gen\\view"));
		Resource folder = new FileResource(new File("C:\\java\\jboss-seam-1.2.1.GA\\seam-gen\\view\\action.xhtml"));
		Resource dest = new FileResource();
		FilterSet set = new FilterSet();
		//set.readFiltersFromFile(filtersFile);
		set.addFilter("methodName", "testValue");

			copyFilesAndFolders(new File("C:\\java\\jboss-seam-1.2.1.GA\\seam-gen\\view"), new File("C:\\temp\\WebContent"), new FilterSetCollection(set),true);

	
	}
	
	public static interface AntCopyConfiguration {
		public Properties getProperties();
		public File getBaseDir();
	}

	public static void copyFilesAndFolders(File sourceFolder, File destinationFolder, FilterSetCollection set, boolean override) {
		if(!destinationFolder.exists()) destinationFolder.mkdirs();
		File[] files = sourceFolder.listFiles();
		for (File file : files) {
			if(file.isDirectory()) {
				copyFilesAndFolders(file,new File(destinationFolder,file.getName()),set,override);
			} else {
				try {
					FileUtils.getFileUtils().copyFile(file, new File(destinationFolder,file.getName()),set,override);
				} catch (IOException e) {
					SeamCorePlugin.getPluginLog().logError(e);
				}
			}
		}
	}
	
}
