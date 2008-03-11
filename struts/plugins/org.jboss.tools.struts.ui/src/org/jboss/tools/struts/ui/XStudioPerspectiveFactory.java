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
package org.jboss.tools.struts.ui;

///import org.jboss.tools.struts.ui.navigation.NavigationPanel;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import org.jboss.tools.common.model.ui.views.palette.PaletteViewPart;
import org.jboss.tools.struts.ui.navigator.StrutsProjectsNavigator;

public class XStudioPerspectiveFactory implements IPerspectiveFactory {
	public static final String PERSPECTIVE_ID = "org.jboss.tools.common.model.ui.XStudioPerspective";
	 
	public XStudioPerspectiveFactory() {
	}

	public void createInitialLayout(IPageLayout layout) {
		try {
			String editorArea = layout.getEditorArea();
			IFolderLayout leftTop = layout.createFolder("leftTop", IPageLayout.LEFT, 0.3f, editorArea);
			leftTop.addView(JavaUI.ID_PACKAGES);
			leftTop.addView(StrutsProjectsNavigator.VIEW_ID);	
			IFolderLayout leftBottom = layout.createFolder("leftBottom", IPageLayout.BOTTOM, 0.7f, "leftTop");
			leftBottom.addView(IPageLayout.ID_PROP_SHEET);			
			leftBottom.addView(IPageLayout.ID_OUTLINE);

			IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.8f, editorArea);
			bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
			bottom.addView(IPageLayout.ID_TASK_LIST);

			IFolderLayout rightTop = layout.createFolder("rightTop", IPageLayout.RIGHT, 0.78f, editorArea);
			rightTop.addView(PaletteViewPart.VIEW_ID);
		} catch (Exception ex) {
			StrutsUIPlugin.getPluginLog().logError(ex);
		}
		
	}

}
