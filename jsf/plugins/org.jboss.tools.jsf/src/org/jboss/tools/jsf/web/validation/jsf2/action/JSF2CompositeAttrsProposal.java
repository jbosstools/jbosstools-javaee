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

import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.web.validation.jsf2.JSF2XMLValidator;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ValidatorConstants;

/**
 * 
 * @author yzhishko
 * 
 */

public class JSF2CompositeAttrsProposal implements IMarkerResolution2 {
	private IResource resource;
	private String componentPath = null;
	private String[] attrs = null;
	private String elementName = null;
	private String attrName="";


	public JSF2CompositeAttrsProposal(IResource resource, String compPath, String elementName, String[] attrs, String attrName) {
		this.resource = resource;
		this.componentPath = compPath;
		this.attrs = attrs;
		this.attrName = attrName;
		this.elementName=elementName;
	}

	@Override
	public String getLabel() {
		return MessageFormat.format(JSFUIMessages.Create_JSF_2_Interface_Attr, attrName, elementName, componentPath);
	}

	@Override
	public void run(IMarker marker) {
		try{
			final IFile createdFile = JSF2ResourceUtil
					.createCompositeComponentFile(resource.getProject(),
							new Path(componentPath), attrs);
			if (createdFile != null) {
				IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage(), createdFile);
			}
		}catch(CoreException ex){
			JSFModelPlugin.getPluginLog().logError(ex);
		}
	}

	@Override
	public String getDescription() {
		return getLabel();
	}

	@Override
	public Image getImage() {
		return null;
	}

}
