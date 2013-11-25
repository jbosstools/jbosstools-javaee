/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf;

import java.util.Locale;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.model.helpers.converter.OpenKeyHelper;
import org.jboss.tools.jst.web.ui.internal.editor.i18n.ILocaleProvider;

/**
 * Implementation of {@link ILocaleProvider} for JSF projects.
 *
 * @author yradtsevich
 */
public class JsfLocaleProvider implements ILocaleProvider {
	
	/*
	 * Stores the string representation of the current locale.
	 */
	private String localeString = ""; //$NON-NLS-1$
	
	/**
	 * Returns the locale for given {@code editor}, or {@code null} if it
	 * can not determine it.
	 */
	public Locale getLocale(ITextEditor editor) {
		IEditorInput editorInput = editor.getEditorInput();
		IProject fileProject = null;
		if (editorInput instanceof IFileEditorInput) {
			fileProject=((IFileEditorInput)editorInput).getFile().getProject();
		}
		return getLocale(fileProject);
	}

	public String getLocaleString() {
		return localeString;
	}

	public Locale getLocale(IProject project) {
		if (project !=null) {

			IModelNature modelNature = EclipseResourceUtil.getModelNature(project);
			if (modelNature == null) {
				return null;
			}
			XModel model = modelNature.getModel();
			localeString = OpenKeyHelper.getDeafultLocaleFromFacesConfig(model);
			return new Locale(localeString);
		} else {
			return null;
		}
	}
	
}
