/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.ui.test;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.internal.ui.palette.editparts.ToolEntryEditPart;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.views.palette.PalettePage;
import org.eclipse.gef.ui.views.palette.PaletteView;
import org.eclipse.jst.j2ee.internal.common.classpath.J2EEComponentClasspathUpdater;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.IPage;
import org.jboss.tools.common.model.ui.views.palette.IPaletteAdapter;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.jst.jsp.jspeditor.PalettePageImpl;
import org.jboss.tools.jst.web.ui.WebDevelopmentPerspectiveFactory;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.vpe.ui.palette.CustomDrawerEditPart;
import org.jboss.tools.vpe.ui.palette.PaletteAdapter;
import org.jboss.tools.vpe.ui.palette.PaletteViewer;
import org.jboss.tools.vpe.ui.palette.model.PaletteModel;
import org.jboss.tools.vpe.ui.palette.model.PaletteRoot;

import junit.framework.TestCase;

public class TestPalette  extends TestCase {
	static String jsfProjectName = "testJSFProject";
	static IProject jsfProject;

	protected void setUp() throws Exception {
		loadProjects();
		List<IProject> projectList = new ArrayList<IProject>();
		projectList.add(jsfProject);
		J2EEComponentClasspathUpdater.getInstance().forceUpdate(projectList);
		loadProjects();
	}

	private void loadProjects() throws Exception {
		jsfProject = ProjectImportTestSetup.loadProject(jsfProjectName);
		jsfProject.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}

	public void testPalette() throws Exception {
		IWorkbench w = JsfUiPlugin.getDefault().getWorkbench();
		IWorkbenchWindow window = w.getActiveWorkbenchWindow();
		
		//open Web Development perspective
		w.showPerspective(WebDevelopmentPerspectiveFactory.PERSPECTIVE_ID, window);
		
		//open file
		IFile f = jsfProject.getFile(new Path("WebContent/pages/hello.jsp"));
		assertNotNull(f);
		IEditorPart editor = IDE.openEditor(window.getActivePage(), f);
		assertNotNull(editor);
	
		//check palette model
		PaletteModel paletteModel = PaletteModel.getInstance(null);
		assertNotNull(paletteModel);
		PaletteRoot root = paletteModel.getPaletteRoot();
		List<?> tabs = root.getChildren();
		assertTrue(!tabs.isEmpty());
		for (Object tab: tabs) {
			assertTrue(tab instanceof PaletteContainer);
			PaletteContainer c = (PaletteContainer)tab;
			List<?> macros = c.getChildren();
			assertTrue(!macros.isEmpty());
			for (Object m: macros) {
				PaletteEntry e = (PaletteEntry)m;
				//
			}
		}

		//get palette view
		IViewPart part = window.getActivePage().findView("org.eclipse.gef.ui.palette_view");
		assertNotNull(part);
		assertTrue(part instanceof PaletteView);
		PaletteView view = (PaletteView)part;
		IPage page1 = view.getCurrentPage();

		assertTrue(editor instanceof JSPMultiPageEditor);
		PalettePage page = (PalettePage)editor.getAdapter(PalettePage.class);

		//compare page obtained from view and editor, must be the same
		assertTrue(page1 == page);
		assertTrue(page instanceof PalettePageImpl);

		PalettePageImpl palettePage = (PalettePageImpl)page;
		IPaletteAdapter adapter = palettePage.getAdapter();
		assertTrue(adapter instanceof PaletteAdapter);

		//check edit parts
		PaletteViewer viewer = ((PaletteAdapter)adapter).getViewer();
		List<?> parts = viewer.getRootEditPart().getChildren();
		for (Object p: parts) {
			List<?> parts1 = ((EditPart)p).getChildren();
			for (Object p1: parts1) {
				List<?> parts2 = ((EditPart)p1).getChildren();
				for (Object p2: parts2) {
					assertTrue(p2 instanceof ToolEntryEditPart);
				}
			}
		}
		
	}

}
