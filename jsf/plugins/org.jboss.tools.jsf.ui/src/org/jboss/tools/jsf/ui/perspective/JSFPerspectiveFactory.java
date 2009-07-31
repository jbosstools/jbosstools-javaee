/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.ui.perspective;
import org.eclipse.jdt.ui.JavaUI;import org.eclipse.ui.IFolderLayout;import org.eclipse.ui.IPageLayout;import org.eclipse.ui.IPerspectiveFactory;import org.jboss.tools.jsf.ui.navigator.JsfProjectsNavigator;import org.jboss.tools.common.model.ui.views.palette.PaletteViewPart;
public class JSFPerspectiveFactory implements IPerspectiveFactory {	public static final String PERSPECTIVE_ID = "org.jboss.tools.jsf.ui.JSFPerspective"; //$NON-NLS-1$
	public JSFPerspectiveFactory() {}
	public void createInitialLayout(IPageLayout layout) {			String editorArea = layout.getEditorArea();			IFolderLayout leftTop = layout.createFolder("leftTop", IPageLayout.LEFT, 0.24f, editorArea); //$NON-NLS-1$			leftTop.addView(JavaUI.ID_PACKAGES);			leftTop.addView(JsfProjectsNavigator.VIEW_ID);							IFolderLayout leftBottom = layout.createFolder("leftBottom", IPageLayout.BOTTOM, 0.7f, "leftTop"); //$NON-NLS-1$ //$NON-NLS-2$			leftBottom.addView(IPageLayout.ID_PROP_SHEET);						leftBottom.addView(IPageLayout.ID_OUTLINE);			IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.8f, editorArea); //$NON-NLS-1$			bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
			bottom.addView(IPageLayout.ID_TASK_LIST);			IFolderLayout rightTop = layout.createFolder("rightTop", IPageLayout.RIGHT, 0.84f, editorArea); //$NON-NLS-1$			rightTop.addView(PaletteViewPart.VIEW_ID);			}}