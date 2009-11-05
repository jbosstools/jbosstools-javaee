/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.ui;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.navigator.CommonNavigator;

/**
 * @author Daniel Azarov
 */

public class SeamUIUtil {
	public static void refreshSeamComponentView(){
		CommonNavigator navigator = (CommonNavigator)Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().findView("org.jboss.tools.seam.ui.views.SeamComponentsNavigator");
		if(navigator != null)
			navigator.getCommonViewer().refresh();
	}
	
	public static boolean saveAll(){
		return SeamGuiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors(true); 
	}
	
	public static void waiteForBuild(){
		try {
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		} catch (InterruptedException e) {
			// do nothing
		}
	}
}
