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
package org.jboss.tools.struts.ui.navigator;

import org.jboss.tools.common.model.ui.navigator.LabelDecoratorImpl;
import org.jboss.tools.common.model.ui.navigator.TreeViewerMenuInvoker;
import org.jboss.tools.common.model.ui.navigator.TreeViewerModelListenerImpl;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import org.jboss.tools.common.meta.action.XActionItem;
import org.jboss.tools.common.meta.action.XActionList;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.model.ui.views.navigator.NavigatorMenuInvoker;
import org.jboss.tools.common.model.ui.views.navigator.NavigatorViewPart;

public class StrutsProjectsNavigator extends NavigatorViewPart {	
	public static String VIEW_ID = "org.jboss.tools.struts.ui.navigator.StrutsProjectsView";
	
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
	}

	protected void initLabelProvider(TreeViewer viewer)	{
		viewer.setLabelProvider(LabelDecoratorImpl.decorateLabelProvider(new StrutsProjectsLabelProvider()));			
	}
	
	protected void initContentProvider(TreeViewer viewer) {
		if(true) {
			StrutsProjectsContentProvider c = new StrutsProjectsContentProvider();
			TreeViewerModelListenerImpl listener = new StrutsProjectsTreeListener();
			listener.setViewer(viewer);
			c.setListener(listener);
			contentProvider = c;
			viewer.setContentProvider(contentProvider);
		} else {
			viewer.setContentProvider(
				new ITreeContentProvider() {
					public Object[] getChildren(Object parentElement) {
						return new Object[]{};
					}
					public Object getParent(Object element) {
						return null;	
					}
					public boolean hasChildren(Object element) {
						return false;				
					}
					public Object[] getElements(Object o) {
						return new Object[]{"no license"};
					}
					public void inputChanged(Viewer v, Object o1,Object o2) {
					
					}
					public void dispose() {
					}
				}
			);
		}
	}

	protected String[] getActionClasses() {
		String[] actions = new String[]{
			"org.jboss.tools.struts.ui.internal.action.CreateProjectAction",
			"org.jboss.tools.struts.ui.internal.action.ImportProjectAction"        			
		};
		return actions;
	}
	
	protected TreeViewerMenuInvoker createMenuInvoker() {
		return new StrutsNavigatorMenuInvoker();
	}

	public static void main(String[] args) {
	}

}

class StrutsNavigatorMenuInvoker extends NavigatorMenuInvoker {
	private static XModelObject strutsWorkspace = PreferenceModelUtilities.getPreferenceModel().createModelObject("StrutsWorkspace", null);
	
	protected XModelObject getWorkspaceObject() {
		return strutsWorkspace;
	}

	protected XActionList getActionList(XModelObject o) {
		XActionList l = o.getModelEntity().getActionList();
		if(o.getModelEntity().getName().equals("FileSystemFolder")) {
			l = getWebContextActionList(l);
		} else {
			l = (XActionList)l.copy(acceptor);
		}
		return l;
	}
	
	static XActionList webContextActionList = null;
	
	private  XActionList getWebContextActionList(XActionList l) {
		if(webContextActionList == null) {
			webContextActionList = (XActionList)l.copy(new FileSystemFolder());
		}
		return webContextActionList;
	}
	
	class FileSystemFolder implements XActionItem.Acceptor {
		public boolean accepts(XActionItem item) {
			if("Help".equals(item.getName())) return false;
			String path = item.getPath();
			if(path == null) return true;
			int q = path.indexOf('/');
			if(q > 0) return true;
			String s = "." + path + ".";
			return ".CreateActions.CopyActions.Properties.".indexOf(s) >= 0;
		}
	}
	
	AcceptorImpl acceptor = new AcceptorImpl();
	static String HIDDEN_ACTIONS = ".Help.Mount.Unmount.";

	class AcceptorImpl implements XActionItem.Acceptor {
		public boolean accepts(XActionItem item) {
			if(HIDDEN_ACTIONS.indexOf("." + item.getName() + ".") >= 0) return false;
			return true;
		}
	}

}
