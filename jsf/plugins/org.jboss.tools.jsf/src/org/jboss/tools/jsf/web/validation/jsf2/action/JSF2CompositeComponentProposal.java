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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.web.validation.jsf2.JSF2XMLValidator;
import org.jboss.tools.jsf.web.validation.jsf2.components.IJSF2ValidationComponent;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ResourceUtil;

/**
 * 
 * @author yzhishko
 * 
 */

public class JSF2CompositeComponentProposal extends JSF2AbstractProposal {

	private String componentPath = null;
	private String[] attrs = null;

	public JSF2CompositeComponentProposal() {
		super();
	}

	public JSF2CompositeComponentProposal(IResource validateResource,
			String compPath, String[] attrs) {
		super(validateResource);
		this.componentPath = compPath;
		this.attrs = attrs;
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

	public String getDisplayString() {
		return JSFUIMessages.Create_JSF_2_Composite_Component;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void runWithMarker(IMarker marker) throws CoreException {
		if (marker != null) {
			validateResource = marker.getResource();
			Map attrsMap = marker.getAttributes();
			Object object = attrsMap
					.get(JSF2ResourceUtil.COMPONENT_RESOURCE_PATH_KEY);
			componentPath = (String) object;
			attrs = getAttributes(marker);
		}
		final IFile createdFile = JSF2ResourceUtil
				.createCompositeComponentFile(validateResource.getProject(),
						new Path(componentPath), attrs);
		validateResource.getProject().deleteMarkers(
				JSF2XMLValidator.JSF2_PROBLEM_ID, false, 1);
		if (createdFile != null) {
			IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage(), createdFile);
		}

	}

}
