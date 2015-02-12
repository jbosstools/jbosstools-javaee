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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.internal.KbProject;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BatchProjectFactory {
	private static final String MODEL_ID = "Batch";

	/**
	 * This internal method returns non-null handle object even when project
	 * has no batch capabilities. Result may be checked for exists() which 
	 * returns true for a Batch project. 
	 * 
	 * @param project
	 * @param resolve
	 * @return
	 */
	public static BatchProject getBatchProject(IProject project, boolean resolve) {
		BatchProject result = null;
		KbProject kb = (KbProject)KbProjectFactory.getKbProject(project, resolve);
		
		if(kb != null) {
			synchronized (kb) {
				result = (BatchProject)kb.getExtensionModel(MODEL_ID);
				if(result == null) {
					result = new BatchProject();
					result.setProject(project);
					kb.setExtensionModel(MODEL_ID, result);
				}
			}
		}
		if(result != null && resolve) {
			result.resolve();
		}
		return result;
	}

	public static IBatchProject getBatchProjectWithProgress(final IProject project){
		final IBatchProject batch = getBatchProject(project, false);
		if(batch != null && !batch.isStorageResolved()){
			if (Display.getCurrent() != null) {
				try{
					PlatformUI.getWorkbench().getProgressService().busyCursorWhile(new IRunnableWithProgress(){
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException, InterruptedException {
							monitor.beginTask("Building batch project", 10);
							monitor.worked(3);
							getBatchProject(project, true);
							monitor.worked(7);
						}
					});
				}catch(InterruptedException ie){
					BatchCorePlugin.pluginLog().logError(ie);
				}catch(InvocationTargetException ite){
					BatchCorePlugin.pluginLog().logError(ite);
				}
			} else {
				getBatchProject(project, true);
			}
		}

		return batch;
	}

}
