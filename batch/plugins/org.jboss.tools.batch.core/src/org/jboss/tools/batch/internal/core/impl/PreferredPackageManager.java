/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.internal.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.core.IBatchArtifact;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.common.EclipseUtil;

public class PreferredPackageManager {
	private static final String QUALIFIED_NAME_PREFIX = "create.artifact.package.";
	
	/**
	 * returns suggested package name for the given batch artifact type previously saved with method savePreferredPackage
	 * 
	 * @param batchProject
	 * @param type
	 * @return
	 */
	public static IPackageFragment getPackageSuggestion(IBatchProject batchProject, BatchArtifactType type) {
		ArrayList<BatchArtifactType> list = new ArrayList<BatchArtifactType>();
		list.add(type);
		return getPackageSuggestion(batchProject, list);
	}
	
	/**
	 * returns suggested package name for the given list of batch artifact types previously saved with method savePreferredPackage
	 * 
	 * @param batchProject
	 * @param types
	 * @return
	 */
	public static IPackageFragment getPackageSuggestion(IBatchProject batchProject, List<BatchArtifactType> types) {
		IProject project = batchProject.getProject();
		for(BatchArtifactType type : types){
			QualifiedName qualifiedName = new QualifiedName("", QUALIFIED_NAME_PREFIX + type.toString());
			try {
				String packPath = project.getPersistentProperty(qualifiedName);
				if(packPath != null && packPath.length() > 0) {
					IJavaProject javaProject = EclipseUtil.getJavaProject(project);
					if(javaProject != null) {
						IPackageFragment result = javaProject.findPackageFragment(new Path(packPath));
						if(result != null && result.exists() && !result.isReadOnly()) {
							return result;
						}
					}
				}
			} catch (CoreException e) {
				BatchCorePlugin.pluginLog().logError(e);
			}
		}
		
		return findPackage(batchProject, types);
	}

	private static IPackageFragment findPackage(IBatchProject batchProject, List<BatchArtifactType> types) {
		for (BatchArtifactType type: types) {
			for (IBatchArtifact artifact: batchProject.getArtifacts(type)) {
				if(!artifact.getType().isBinary()){
					return artifact.getType().getPackageFragment();
				}
			}
		}
		return null;
	}
	
	/**
	 * saves package name for the given batch artifact type as project persistent property 
	 * 
	 * @param batchProject
	 * @param type
	 * @param packageName
	 */
	public static void savePreferredPackage(IBatchProject batchProject, BatchArtifactType type, String packageName){
		QualifiedName qualifiedName = new QualifiedName("", QUALIFIED_NAME_PREFIX + type.toString());
		try {
			batchProject.getProject().setPersistentProperty(qualifiedName, packageName);
		} catch (CoreException e) {
			BatchCorePlugin.pluginLog().logError(e);
		}
	}

}
