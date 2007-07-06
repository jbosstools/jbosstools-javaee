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
import org.jboss.tools.seam.core.internal.project.facet.SeamFacetInstallDelegete.FileSetFileFilter;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * 
 * @author eskimo
 *
 */
public class AntCopyUtils {
	
	public static void copyFilesAndFolders(File sourceFolder, File destinationFolder, FilterSetCollection set, boolean override) {
		copyFilesAndFolders(sourceFolder, destinationFolder, null, set, override);
	}
	
	public static void copyFilesAndFolders(File sourceFolder, File destinationFolder,
			FileSetFileFilter fileSetFilter,
			FilterSetCollection filterSetCollection, boolean override) {
		if(!destinationFolder.exists()) 
			destinationFolder.mkdirs();
		File[] files = fileSetFilter==null?sourceFolder.listFiles():sourceFolder.listFiles(fileSetFilter);
		for (File file : files) {
			if(file.isDirectory()) {
				copyFilesAndFolders(file,new File(destinationFolder,file.getName()),fileSetFilter,filterSetCollection,override);
			} else {
				try {
					FileUtils.getFileUtils().copyFile(file, new File(destinationFolder,file.getName()),filterSetCollection,override);
				} catch (IOException e) {
					e.printStackTrace();
					SeamCorePlugin.getPluginLog().logError(e);
				}
			}
		}
	}
	
	public static void copyFileToFolder(File source, File dest, boolean override) {
			copyFileToFolder(source, dest,new FilterSetCollection(),override);
	}
	
	public static void copyFileToFolder(File source, File dest, FilterSetCollection filterSetCollection, boolean override ) {
		try {
			FileUtils.getFileUtils().copyFile(source, new File(dest,source.getName()),filterSetCollection,override);
		} catch (IOException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}		
	}
	public static void copyFileToFile(File source, File dest, FilterSetCollection filterSetCollection, boolean override ) {
		try {
			FileUtils.getFileUtils().copyFile(source, dest,filterSetCollection,override);
		} catch (IOException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}		
	}
}
