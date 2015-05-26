/*************************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.batch.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.batch.internal.core.impl.BatchProject;
import org.jboss.tools.batch.internal.core.impl.BatchProjectFactory;
import org.jboss.tools.batch.internal.core.scanner.BatchArchiveDetector;
import org.jboss.tools.foundation.core.plugin.BaseCorePlugin;
import org.jboss.tools.foundation.core.plugin.log.IPluginLog;
import org.osgi.framework.BundleContext;

public class BatchCorePlugin extends BaseCorePlugin {
	public static String PLUGIN_ID = "org.jboss.tools.batch.core"; //$NON-NLS-1$
	static BatchCorePlugin plugin = null;

	public BatchCorePlugin() {
		plugin = this;
	}
	
	public static BatchCorePlugin getDefault() {
		return plugin;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		ResourcesPlugin.getWorkspace().addSaveParticipant(PLUGIN_ID, new ISaveParticipant() {			
			@Override
			public void saving(ISaveContext context) throws CoreException {
				BatchArchiveDetector.getInstance().save();
			}			
			@Override
			public void rollback(ISaveContext context) {}			
			@Override
			public void prepareToSave(ISaveContext context) throws CoreException {}			
			@Override
			public void doneSaving(ISaveContext context) {}
		});
	}

	/**
	 * Get the IPluginLog for this plugin. This method 
	 * helps to make logging easier, for example:
	 * 
	 *     FoundationCorePlugin.pluginLog().logError(etc)
	 *  
	 * @return IPluginLog object
	 */
	public static IPluginLog pluginLog() {
		return getDefault().pluginLogInternal();
	}

	public static IBatchProject getBatchProject(IProject project, boolean resolve) {
		BatchProject result = BatchProjectFactory.getBatchProject(project, resolve);
		return (result != null && result.exists()) ? result : null;
	}


}
