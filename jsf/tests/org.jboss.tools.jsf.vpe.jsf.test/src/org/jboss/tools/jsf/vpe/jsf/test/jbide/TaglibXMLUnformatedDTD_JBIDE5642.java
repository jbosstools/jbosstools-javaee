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

package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import java.util.StringTokenizer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.jst.web.ui.navigator.WebProjectsNavigator;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * 
 * @author yzhishko
 *
 */

public class TaglibXMLUnformatedDTD_JBIDE5642 extends VpeTest {

	private static final String WEB_PROJECTS_VIEW_ID = "org.jboss.tools.jst.web.ui.navigator.WebProjectsView"; //$NON-NLS-1$
	private static final String PACKAGE_EXPLORER_VIEW_ID = "org.eclipse.jdt.ui.PackageExplorer"; //$NON-NLS-1$
	private static final String LIB_PATH = "jsfTest/Tag Libraries/"; //$NON-NLS-1$
	private static final String LIB_NAME_I = "primefaces-i.taglib.xml - primefaces-2.0.0.RC.jar"; //$NON-NLS-1$
	private static final String LIB_NAME_P = "primefaces-p.taglib.xml - primefaces-2.0.0.RC.jar"; //$NON-NLS-1$

	private IViewPart webProjectsView;

	public TaglibXMLUnformatedDTD_JBIDE5642(String name) {
		super(name);
	}

	public void testTaglibXMLUnformatedDTD() throws Throwable {
		webProjectsView = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().showView(WEB_PROJECTS_VIEW_ID);
		assertNotNull("Web Projects view is not available", webProjectsView); //$NON-NLS-1$
		TestUtil.delay(2000);
		TestUtil.waitForJobs();

		WebProjectsNavigator projectsNavigator = (WebProjectsNavigator) webProjectsView;
		TreeViewer treeViewer = projectsNavigator.getViewer();
		assertNotNull(treeViewer);

		Object testLibI = findElementByPath(LIB_PATH + LIB_NAME_I, treeViewer);
		assertNotNull("The tag library " + LIB_NAME_I + " was not found", //$NON-NLS-1$ //$NON-NLS-2$
				testLibI);

		Object testLibP = findElementByPath(LIB_PATH + LIB_NAME_P, treeViewer);
		assertNotNull("The tag library " + LIB_NAME_P + " was not found", //$NON-NLS-1$ //$NON-NLS-2$
				testLibP);
	}

	private Object findElementByPath(String path, final TreeViewer searchTreeViwer) {
		Tree searchTree = searchTreeViwer.getTree();
		if (searchTree == null || path == null) {
			return null;
		}
		StringTokenizer tokenizer = new StringTokenizer(path, "/", false); //$NON-NLS-1$
		TreeItem childItem = getItemByName(tokenizer.nextToken(), searchTree);
		if (childItem != null) {
			searchTreeViwer.expandToLevel(childItem.getData(), 1);
		} else {
			return null;
		}
		while (tokenizer.hasMoreTokens()) {
			childItem = getItemByName(tokenizer.nextToken(), childItem);
			if (childItem != null) {
				searchTreeViwer.expandToLevel(childItem.getData(), 1);
			} else {
				return null;
			}
		}
		return childItem.getData();
	}

	private TreeItem getItemByName(String elementName, Widget rootItem) {
		if (rootItem == null || elementName == null) {
			return null;
		}
		TreeItem[] children = null;
		if (rootItem instanceof Tree) {
			children = ((Tree) rootItem).getItems();
		} else if (rootItem instanceof TreeItem) {
			children = ((TreeItem) rootItem).getItems();
		}
		if (children == null) {
			return null;
		}
		for (int i = 0; i < children.length; i++) {
			if (elementName.equals(children[i].getText())) {
				return children[i];
			}
		}
		return null;
	}

	@Override
	protected void tearDown() throws Exception {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.hideView(webProjectsView);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.showView(PACKAGE_EXPLORER_VIEW_ID);
		super.tearDown();
	}

}
