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
package org.jboss.tools.cdi.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.cdi.internal.core.impl.definition.DefinitionContext;
import org.jboss.tools.cdi.internal.core.scanner.lib.ClassPathMonitor;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jst.web.kb.internal.validation.ProjectValidationContext;

public class CDICoreNature implements IProjectNature {
	public static String NATURE_ID = "org.jboss.tools.cdi.core.cdinature";

	IProject project = null;
	ICDIProject cdiProjectDelegate;

	ClassPathMonitor classPath = new ClassPathMonitor(this);
	DefinitionContext definitions = new DefinitionContext();

	ProjectValidationContext validationContext = null;

	boolean isBuilt = false;

//	Map<IPath, Object> sourcePaths2 = new HashMap<IPath, Object>(); //TODO

	private boolean isStorageResolved = false;

	public CDICoreNature() {
		definitions.setProject(this);
	}

	public void configure() throws CoreException {
		addToBuildSpec(CDICoreBuilder.BUILDER_ID);
	}

	public void deconfigure() throws CoreException {
		removeFromBuildSpec(CDICoreBuilder.BUILDER_ID);
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
		classPath.init();
	}

	public void setCDIProject(ICDIProject cdiProject) {
		this.cdiProjectDelegate = cdiProject;
		cdiProject.setNature(this);
	}

	public DefinitionContext getDefinitions() {
		return definitions;
	}

	public ICDIProject getDelegate() {
		return cdiProjectDelegate;
	}

	public ClassPathMonitor getClassPath() {
		return classPath;
	}
	/**
	 * 
	 * @param load
	 */
	public void resolveStorage(boolean load) {
		if(isStorageResolved) return;
		if(load) {
			load();
		} else {
			isStorageResolved = true;
		}
	}

	/**
	 * 
	 */
	public void resolve() {
		resolveStorage(true);
	}

	/**
	 * Loads results of last build, which are considered 
	 * actual until next build.
	 */	
	public void load() {
		if(isStorageResolved) return;
		isStorageResolved = true;
		try {
			getProject().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		
//		postponeFiring();
//		
//		try {		
//			boolean b = getClassPath().update();
//			if(b) {
//				getClassPath().validateProjectDependencies();
//			}
//			File file = getStorageFile();
//
//			//TODO
//
//			if(b) {
//				getClassPath().process();
//			}
//
//		} finally {
//			fireChanges();
//		}
	}

	public void clean() {
		File file = getStorageFile();
		if(file != null && file.isFile()) {
			file.delete();
		}
		isBuilt = false;
		classPath.clean();
		postponeFiring();

		definitions.clean();
		if(cdiProjectDelegate != null) {
			cdiProjectDelegate.update();
		}
//		IPath[] ps = sourcePaths2.keySet().toArray(new IPath[0]);
//		for (int i = 0; i < ps.length; i++) {
//			pathRemoved(ps[i]);
//		}
		fireChanges();
	}
	
	/**
	 * Stores results of last build, so that on exit/enter Eclipse
	 * load them without rebuilding project
	 * @throws IOException 
	 */
	public void store() throws IOException {
		isBuilt = true;
		File file = getStorageFile();
//TODO
//		file.getParentFile().mkdirs();
	}
	/**
	 * 
	 * @param builderID
	 * @throws CoreException
	 */
	protected void addToBuildSpec(String builderID) throws CoreException {
		IProjectDescription description = getProject().getDescription();
		ICommand command = null;
		ICommand commands[] = description.getBuildSpec();
		for (int i = 0; i < commands.length && command == null; ++i) {
			if (commands[i].getBuilderName().equals(builderID)) 
				command = commands[i];
		}
		if (command == null) {
			command = description.newCommand();
			command.setBuilderName(builderID);
			ICommand[] oldCommands = description.getBuildSpec();
			ICommand[] newCommands = new ICommand[oldCommands.length + 1];
			System.arraycopy(oldCommands, 0, newCommands, 0, oldCommands.length);
			newCommands[oldCommands.length] = command;
			description.setBuildSpec(newCommands);
			getProject().setDescription(description, null);
		}
	}

	/**
	 * 
	 */
	static String EXTERNAL_TOOL_BUILDER = "org.eclipse.ui.externaltools.ExternalToolBuilder"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	static final String LAUNCH_CONFIG_HANDLE = "LaunchConfigHandle"; //$NON-NLS-1$

	/**
	 * 
	 * @param builderID
	 * @throws CoreException
	 */
	protected void removeFromBuildSpec(String builderID) throws CoreException {
		IProjectDescription description = getProject().getDescription();
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			String builderName = commands[i].getBuilderName();
			if (!builderName.equals(builderID)) {
				if(!builderName.equals(EXTERNAL_TOOL_BUILDER)) continue;
				Object handle = commands[i].getArguments().get(LAUNCH_CONFIG_HANDLE);
				if(handle == null || handle.toString().indexOf(builderID) < 0) continue;
			}
			ICommand[] newCommands = new ICommand[commands.length - 1];
			System.arraycopy(commands, 0, newCommands, 0, i);
			System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
			description.setBuildSpec(newCommands);
			getProject().setDescription(description, null);
			return;
		}
	}

	/*
	 * 
	 */
	private File getStorageFile() {
		IPath path = CDICorePlugin.getDefault().getStateLocation();
		File file = new File(path.toFile(), "projects/" + project.getName()); //$NON-NLS-1$
		return file;
	}
	
	public void clearStorage() {
		File f = getStorageFile();
		if(f == null || !f.exists()) return;
		FileUtil.clear(f);
		f.delete();
	}

	public boolean hasNoStorage() {
			if(isBuilt) return false;
		File f = getStorageFile();
		return f == null || !f.exists();
	}

	public void postponeFiring() {
		//TODO
	}

	public void fireChanges() {
		//TODO
	}

	public long fullBuildTime;
	public List<Long> statistics;

	public void pathRemoved(IPath source) {
//		sourcePaths2.remove(source);
		definitions.clean(source);
		//TODO
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamProject#getValidationContext()
	 */
	public ProjectValidationContext getValidationContext() {
		if(validationContext==null) {
			validationContext = new ProjectValidationContext();
		}
		return validationContext;
	}

}
