/*******************************************************************************
 * Copyright (c) 2007-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.handlers;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.IInputValidator;
import org.jboss.tools.jsf.jsf2.model.CompositeComponentConstants;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;

public class NameInputValidator implements IInputValidator {
	
	private static final Pattern NAME_PATTERN = Pattern.compile("([a-zA-Z]+\\d*)+"); //$NON-NLS-1$
	private IProject project;
	
	public NameInputValidator(IProject project) {
		super();
		this.project = project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}
	
	@Override
	public String isValid(String newText) {
		String trim = newText.trim();
		String result = null;
		String[] split = trim.split(":", 2); //$NON-NLS-1$
		/*
		 * Check the correct format.
		 * Matcher will accept only word characters with optional numbers.
		 */
		if ((split.length != 2) || trim.startsWith(":") || trim.endsWith(":") //$NON-NLS-1$ //$NON-NLS-2$
				|| (split[0].length() == 0) || (split[1].length() == 0)) {
			result = "Component's name should fit in the pattern \"namespace:name\""; //$NON-NLS-1$
		} else if(!NAME_PATTERN.matcher(split[0]).matches()) {
			result = "Namespace '"+split[0]+"' has wrong spelling, please correct"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if(!NAME_PATTERN.matcher(split[1]).matches()) {
			result = "Name '"+split[1]+"' has wrong spelling, please correct"; //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			String nameSpaceURI = CompositeComponentConstants.COMPOSITE_XMLNS + "/" + split[0];  //$NON-NLS-1$
			Object fld = JSF2ResourceUtil.findResourcesFolderContainerByNameSpace(project, nameSpaceURI);
			if (fld instanceof IFolder) {
				IResource res = ((IFolder) fld).findMember(split[1]+ ".xhtml"); //$NON-NLS-1$
				if ((res instanceof IFile) && ((IFile)res).exists() ) {
					result = "Component with the same name already exists"; //$NON-NLS-1$
				}
			}
		}
		return result;
	}
}
