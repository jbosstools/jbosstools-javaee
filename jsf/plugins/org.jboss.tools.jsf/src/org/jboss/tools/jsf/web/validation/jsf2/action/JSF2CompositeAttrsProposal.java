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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.web.validation.jsf2.JSF2XMLValidator;
import org.jboss.tools.jsf.web.validation.jsf2.components.IJSF2ValidationComponent;

/**
 * 
 * @author yzhishko
 * 
 */

public class JSF2CompositeAttrsProposal extends JSF2AbstractProposal {

	private String componentPath = null;
	private String[] attrs = null;

	public JSF2CompositeAttrsProposal() {
		super();
	}

	public JSF2CompositeAttrsProposal(IResource validateResource,
			String compPath, String[] attrs) {
		super(validateResource);
		this.componentPath = compPath;
		this.attrs = attrs;
	}

	public String getDisplayString() {
		return JSFUIMessages.Create_JSF_2_Interface_Attr;
	}

	@Override
	protected void runWithMarker(IMarker marker) throws CoreException {
		if (marker != null) {
			validateResource = marker.getResource();
			Map<?, ?> attrsMap = marker.getAttributes();
			Object object = attrsMap
					.get(JSF2ResourceUtil.COMPONENT_RESOURCE_PATH_KEY);
			componentPath = (String) object;
			attrs = new String[] { (String) marker
					.getAttribute(IJSF2ValidationComponent.JSF2_ATTR_NAME_KEY) };
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
