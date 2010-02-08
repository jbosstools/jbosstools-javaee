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
package org.jboss.tools.jsf.vpe.jsf.i18n;

import java.util.Locale;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.model.helpers.converter.OpenKeyHelper;
import org.jboss.tools.vpe.editor.i18n.ILocaleProvider;

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
	public Locale getLocale(StructuredTextEditor editor) {
		IEditorInput editorInput = editor.getEditorInput();
		if (editorInput instanceof IFileEditorInput) {
			IProject project = ((IFileEditorInput)editorInput)
					.getFile().getProject();
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

	public String getLocaleString() {
		return localeString;
	}
	
}
