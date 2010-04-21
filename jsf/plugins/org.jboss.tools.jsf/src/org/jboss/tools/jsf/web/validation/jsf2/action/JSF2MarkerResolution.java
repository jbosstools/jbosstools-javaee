 /*******************************************************************************
  * Copyright (c) 2007-2010 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/

package org.jboss.tools.jsf.web.validation.jsf2.action;

import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.web.validation.jsf2.JSF2Validator;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ResourceUtil;

/**
 *  
 * @author yzhishko
 *
 */

public class JSF2MarkerResolution implements IMarkerResolution {

	public String getLabel() {
		return JSFUIMessages.Create_JSF_2_Composition_Component;
	}

	@SuppressWarnings("unchecked")
	public void run(final IMarker marker) {
		try {
			Map attrsMap = marker.getAttributes();
			Object object = attrsMap
					.get(JSF2ResourceUtil.COMPONENT_RESOURCE_PATH_KEY);
			final IFile createdFile = JSF2ResourceUtil
					.createJSF2CompositeComponent(marker.getResource()
							.getProject(), new Path((String) object));
			marker.getResource().deleteMarkers(JSF2Validator.JSF2_PROBLEM_ID,
					false, 1);
			if (createdFile != null) {
				IDE.openEditor(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage(),
						createdFile);
			}
			new Thread("JSF2 Validator") { //$NON-NLS-1$
				public void run() {
					SafeRunner.run(new SafeRunnable() {

						public void run() throws Exception {
							try {
								ValidationFramework.getDefault().validate(
										new IProject[] { marker.getResource()
												.getProject() }, false, false,
										new NullProgressMonitor());
							} catch (CoreException e) {
								JSFModelPlugin.getPluginLog().logError(e);
							}
						}
					});
				};
			}.start();
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
	}

}
