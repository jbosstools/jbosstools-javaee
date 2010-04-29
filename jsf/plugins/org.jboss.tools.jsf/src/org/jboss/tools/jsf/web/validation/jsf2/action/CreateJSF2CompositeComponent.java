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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.web.validation.jsf2.JSF2XMLValidator;
import org.jboss.tools.jsf.web.validation.jsf2.components.IJSF2ValidationComponent;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ResourceUtil;

/**
 * 
 * @author yzhishko
 * 
 */

public class CreateJSF2CompositeComponent implements IMarkerResolution {

	public String getLabel() {
		return JSFUIMessages.Create_JSF_2_Composite_Component;
	}

	@SuppressWarnings("unchecked")
	public void run(final IMarker marker) {
		try {
			Map attrsMap = marker.getAttributes();
			Object object = attrsMap
					.get(JSF2ResourceUtil.COMPONENT_RESOURCE_PATH_KEY);
			final IFile createdFile = JSF2ResourceUtil
					.createCompositeComponentFile(marker.getResource()
							.getProject(), new Path((String) object),
							getAttributes(marker));
			marker.getResource().deleteMarkers(
					JSF2XMLValidator.JSF2_PROBLEM_ID, false, 1);
			if (createdFile != null) {
				IDE.openEditor(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage(),
						createdFile);
			}
			Job validateJob = new Job("JSF 2 Components Validator") { //$NON-NLS-1$

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						ValidationFramework.getDefault().validate(
								new IProject[] { marker.getResource()
										.getProject() }, false, false,
								new NullProgressMonitor());
					} catch (CoreException e) {
						return new Status(IStatus.CANCEL,
								JSFModelPlugin.PLUGIN_ID, "Cancel"); //$NON-NLS-1$
					}
					return IValidatorJob.OK_STATUS;
				}
			};
			validateJob.schedule(500);
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
	}

	@SuppressWarnings("unchecked")
	private String[] getAttributes(IMarker marker) throws CoreException {
		Map attrsMap = marker.getAttributes();
		if (attrsMap != null) {
			Set<String> set = new HashSet<String>(0);
			Set<Entry> entries = attrsMap.entrySet();
			for (Entry entry : entries) {
				String key = (String) entry.getKey();
				if (key.startsWith(IJSF2ValidationComponent.JSF2_ATTR_NAME_KEY)) {
					set.add((String) entry.getValue());
				}
			}
			return set.toArray(new String[0]);
		}
		return null;
	}

}
