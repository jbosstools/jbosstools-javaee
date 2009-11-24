package org.jboss.tools.seam.ui;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.navigator.resources.ProjectExplorer;

public class SeamPerspectiveFactory implements IPerspectiveFactory {

	private static final String SEAM_COMPONENTS_NAVIGATOR = "org.jboss.tools.seam.ui.views.SeamComponentsNavigator"; //$NON-NLS-1$
	private static String JBOSS_SERVERS_VIEW = "org.jboss.ide.eclipse.as.ui.views.JBossServerView";  //$NON-NLS-1$
	private static String WTP_SERVERS_VIEW = "org.eclipse.wst.server.ui.ServersView"; //$NON-NLS-1$
	
	public static final String PERSPECTIVE_ID = "org.jboss.tools.seam.ui.SeamPerspective"; //$NON-NLS-1$
	
	public void createInitialLayout(IPageLayout layout) {
 		String editorArea = layout.getEditorArea();

		IFolderLayout leftTop = layout.createFolder("leftTop", IPageLayout.LEFT, (float)0.2, editorArea); //$NON-NLS-1$
		leftTop.addView(JavaUI.ID_PACKAGES);
		leftTop.addView(ProjectExplorer.VIEW_ID);
		leftTop.addPlaceholder(IPageLayout.ID_RES_NAV);
		

		IFolderLayout leftBottom = layout.createFolder("leftBottom", IPageLayout.BOTTOM, 0.64f, "leftTop"); //$NON-NLS-1$ //$NON-NLS-2$
		//leftBottom.addView(IPageLayout.ID_OUTLINE);
		leftBottom.addView(IPageLayout.ID_PROP_SHEET);			
		

		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, (float)0.78, editorArea); //$NON-NLS-1$
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView(IPageLayout.ID_TASK_LIST);
		bottom.addView(SEAM_COMPONENTS_NAVIGATOR);
		bottom.addView(WTP_SERVERS_VIEW);
		
		IFolderLayout rightTop = layout.createFolder("right", IPageLayout.RIGHT, (float)0.8, editorArea); //$NON-NLS-1$
//		rightTop.addView("org.jboss.tools.common.model.ui.views.palette.PaletteView"); //$NON-NLS-1$
		rightTop.addView("org.eclipse.gef.ui.palette_view"); //$NON-NLS-1$
		IFolderLayout rightBottom = layout.createFolder("rightBottom", IPageLayout.BOTTOM, (float)0.64, "right"); //$NON-NLS-1$ //$NON-NLS-2$
		rightBottom.addView(IPageLayout.ID_OUTLINE);

		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		layout.addActionSet(JavaUI.ID_ACTION_SET);
		layout.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);
		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);

		// views - seam
		layout.addShowViewShortcut(SEAM_COMPONENTS_NAVIGATOR);
		
		// views - java
		layout.addShowViewShortcut(JavaUI.ID_PACKAGES);
		layout.addShowViewShortcut(JavaUI.ID_TYPE_HIERARCHY);
		layout.addShowViewShortcut(JavaUI.ID_SOURCE_VIEW);

		// views - standard workbench
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);

		// new actions - Java project creation wizard
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewPackageCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewClassCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewInterfaceCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSourceFolderCreationWizard");	 //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSnippetFileCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");//$NON-NLS-1$
	
	}

}
