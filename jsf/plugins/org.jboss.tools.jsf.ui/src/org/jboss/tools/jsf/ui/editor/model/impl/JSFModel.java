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
package org.jboss.tools.jsf.ui.editor.model.impl;

import java.util.*;

import java.beans.*;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Control;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.event.*;
import org.jboss.tools.jsf.JSFPreference;
import org.jboss.tools.jsf.model.helpers.JSFProcessStructureHelper;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jsf.ui.editor.model.IJSFElement;
import org.jboss.tools.jsf.ui.editor.model.IJSFElementList;
import org.jboss.tools.jsf.ui.editor.model.IJSFModel;
import org.jboss.tools.jsf.ui.editor.model.IJSFModelListener;
import org.jboss.tools.jsf.ui.editor.model.IJSFOptions;
import org.jboss.tools.jsf.ui.editor.model.ILink;
import org.jboss.tools.jsf.ui.editor.model.IGroup;
import org.jboss.tools.jsf.ui.editor.model.IPage;
import org.jboss.tools.jsf.ui.preferences.JSFFlowTabbedPreferencesPage;

import org.jboss.tools.common.model.ui.action.*;
import org.jboss.tools.common.model.ui.util.ModelUtilities;
import org.jboss.tools.common.model.util.XModelTreeListenerSWTSync;

public class JSFModel extends JSFElement implements IJSFModel, PropertyChangeListener, XModelTreeListener {
	protected List<IJSFModelListener> strutsModelListeners = new Vector<IJSFModelListener>();
	protected JSFHashtable map = new JSFHashtable();
	protected JSFElementList processItemList = new ProcessItemList();
	protected JSFProcessStructureHelper helper = JSFProcessStructureHelper.instance;
	protected JSFOptions options;
	protected boolean modified = false;

	public JSFModel() {
		try {
			setName("Struts Model");
		} catch (PropertyVetoException ex) {
			JsfUiPlugin.getPluginLog().logError(ex);
		}
	}

	public void dispose() {
		this.disconnectFromModel();
		if (map != null)
			map.dispose();
		map = null;
		if (strutsModelListeners != null)
			strutsModelListeners.clear();
		strutsModelListeners = null;
		if (processItemList != null)
			processItemList.dispose();
		processItemList = null;
		if (options != null)
			options.dispose();
		options = null;
	}

	public boolean isBorderPaint() {
		return false;
	}

	public IJSFOptions getOptions() {
		return options;
	}

	public JSFModel(Object data) {
		this();
		setData(((XModelObject) data).getChildByPath("process")); //$NON-NLS-1$
		map.setData((XModelObject) data);
	}

	public void updateLinks() {
		IGroup group;
		IPage page;

		for (int i = 0; i < getGroupList().size(); i++) {
			group = (IGroup) getGroupList().get(i);
			for (int j = 0; j < group.getPageList().size(); j++) {
				page = (IPage) group.getPageList().get(j);
				for (int k = 0; k < page.getLinkList().size(); k++) {
					((ILink) page.getLinkList().get(k)).setTarget();
				}
			}
		}
	}

	public Object get(String name) {
		return null;
	}

	public void put(String name, Object value) {

	}

	public JSFProcessStructureHelper getHelper() {
		return helper;
	}

	public int getProcessItemCounter() {
		return processItemList.size();
	}

	public IGroup getGroup(int index) {
		return (IGroup) processItemList.get(index);
	}

	public IGroup getGroup(String groupName) {
		return (IGroup) processItemList.get(groupName);
	}

	public IGroup getGroup(Object source) {
		IGroup[] is = (IGroup[]) processItemList.elements
				.toArray(new IGroup[0]);
		for (int i = 0; i < is.length; i++)
			if (is[i].getSource() == source)
				return is[i];
		return null;
	}

	// Module removers

	public void removeGroup(String moduleName) {
	}

	public void removeGroup(IGroup removeProcessItem) {
	}

	public void propertyChange(PropertyChangeEvent pce) {
	}

	IGroup selectedGroup = null;

	public void setSelectedProcessItem(IGroup group) {
		IGroup oldValue = selectedGroup;
		selectedGroup = group;
		propertyChangeSupport.firePropertyChange("selectedProcessItem", //$NON-NLS-1$
				oldValue, group);
	}

	public IGroup getSelectedProcessItem() {
		return selectedGroup;
	}

	public String getText() {
		return ""; //$NON-NLS-1$
	}

	XModelTreeListenerSWTSync listener = null;

	public void setData(Object data) {
		source = (XModelObject) data;
		if (source == null) {
			return;
		}
		helper.autolayout(source);
		map.put(source.getPath(), this);
		processItemList = new ProcessItemList(source);
		listener = new XModelTreeListenerSWTSync(this);
		source.getModel().addModelTreeListener(listener);
		options = new JSFOptions();
	}

	public void disconnectFromModel() {
		if (listener != null)
			source.getModel().removeModelTreeListener(listener);
		options.disconnectFromModel();
		map.disconnectFromModel();
	}

	public boolean isEditable() {
		return source != null
				&& source.getModelEntity().isEditable(source, "body"); //$NON-NLS-1$
	}

	public boolean areCommentsVisible() {
		return false;
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean set) {
		boolean oldValue = modified;
		modified = set;
		propertyChangeSupport
				.firePropertyChange("modified", oldValue, modified); //$NON-NLS-1$
	}

	// -----------------------------------------------------------------------
	// fire events
	// -----------------------------------------------------------------------

	public void fireProcessChanged(boolean flag) {
		if (strutsModelListeners == null)
			return;
		List<IJSFModelListener> targets = new ArrayList<IJSFModelListener>();
		targets.addAll(strutsModelListeners);
		for (int i = 0; i < targets.size(); i++) {
			IJSFModelListener listener = (IJSFModelListener) targets.get(i);
			if (listener != null) {
				listener.processChanged(flag);
			}
		}
		setModified(true);
	}

	public void fireProcessItemAdd(IGroup newProcessItem) {
		List<IJSFModelListener> targets = new ArrayList<IJSFModelListener>();
		targets.addAll(strutsModelListeners);
		for (int i = 0; i < targets.size(); i++) {
			IJSFModelListener listener = (IJSFModelListener) targets.get(i);
			if (listener != null) {
				listener.groupAdd(newProcessItem);
			}
		}
		setModified(true);
	}

	public void fireProcessItemRemove(IGroup newProcessItem, int index) {
		List<IJSFModelListener> targets = new ArrayList<IJSFModelListener>();
		targets.addAll(strutsModelListeners);
		for (int i = 0; i < targets.size(); i++) {
			IJSFModelListener listener = (IJSFModelListener) targets.get(i);
			if (listener != null) {
				listener.groupRemove(newProcessItem);
			}
		}
		setModified(true);
	}

	public void fireLinkAdd(ILink newLink) {
		List<IJSFModelListener> targets = new ArrayList<IJSFModelListener>();
		targets.addAll(strutsModelListeners);
		for (int i = 0; i < targets.size(); i++) {
			IJSFModelListener listener = (IJSFModelListener) targets.get(i);
			if (listener != null) {
				listener.linkAdd(newLink);
			}
		}
		setModified(true);
	}

	public void fireLinkRemove(ILink newLink) {
		List<IJSFModelListener> targets = new ArrayList<IJSFModelListener>();
		targets.addAll(strutsModelListeners);
		for (int i = 0; i < targets.size(); i++) {
			IJSFModelListener listener = (IJSFModelListener) targets.get(i);
			if (listener != null) {
				listener.linkRemove(newLink);
			}
		}
		setModified(true);
	}

	public void fireElementRemoved(IJSFElement element, int index) {
	}

	public void fireElementInserted(IJSFElement element) {
	}

	public void addJSFModelListener(IJSFModelListener listener) {
		strutsModelListeners.add(listener);
	}

	public void removeJSFModelListener(IJSFModelListener listener) {
		if(strutsModelListeners != null) strutsModelListeners.remove(listener);
	}

	public void remove() {
	}

	public String getIconPath() {
		return null;
	}

	public IGroup addGroup(IGroup group) {
		return null;
	}

	public IGroup addGroup(String group) {
		return null;
	}

	public IGroup addGroup(Object source, int x, int y) {
		return null;
	}

	public IGroup addGroup(IJSFElement element) {
		return null;
	}

	public IJSFElementList getGroupList() {
		return processItemList;
	}

	public void nodeChanged(XModelTreeEvent event) {
		fireProcessChanged(false);
		if (map == null || event == null)
			return;
		IJSFElement element = (JSFElement) map.get(event.getInfo());
		if (element != null
				&& !event.getModelObject().getPath()
						.equals(event.getInfo())) {
			updateCash((String) event.getInfo());
		}
		String path = event.getModelObject().getPath();
		element = (path == null) ? null : (IJSFElement) map.get(path);
		if (element == null) {
			return;
		}
		element.nodeChanged(event);
	}

	public void structureChanged(XModelTreeEvent event) {
		JSFElement element;
		Object obj = event.getModelObject().getPath();
		if (obj == null)
			return;
		if (map == null)
			return;
		element = (JSFElement) map.get(obj);
		if (element == null) {
			return;
		}
		if (event.kind() == XModelTreeEvent.STRUCTURE_CHANGED) {
			element.structureChanged(event);
		} else if (event.kind() == XModelTreeEvent.CHILD_ADDED) {
			element.nodeAdded(event);
		} else if (event.kind() == XModelTreeEvent.CHILD_REMOVED) {
			element.nodeRemoved(event);
		}
	}

	public void putToMap(Object key, IJSFElement value) {
		getMap().put(key, value);
	}

	public void removeFromMap(Object key) {
		getMap().remove(key);
	}

	public IJSFElement getFromMap(Object key) {
		return getMap().get(key);
	}

	public class ProcessItemPropertyChangeListener implements
			PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			IGroup processItem = (IGroup) event.getSource();
			if (event.getPropertyName().equals("selected")) { //$NON-NLS-1$
				if (((Boolean) event.getNewValue()).booleanValue())
					setSelectedProcessItem(processItem);
			}
		}
	}

	public class ProcessItemList extends JSFElementList {
		protected ProcessItemList() {
		}

		public ProcessItemList(XModelObject processItemSource) {
			super(JSFModel.this, JSFModel.this.source);
			if (((XModelObject) JSFModel.this.getSource()).getPath() == null)
				return;

			XModelObject[] processItemNodeList = getHelper().getGroups(
					JSFModel.this.source);

			for (int i = 0; i < processItemNodeList.length; i++) {
				IGroup newProcessItem = new Group(JSFModel.this,
						processItemNodeList[i]);
				newProcessItem.addPropertyChangeListener("selected", //$NON-NLS-1$
						new ProcessItemPropertyChangeListener());
				add(newProcessItem);
			}
		}

		public void structureChanged(Object eventData) {
		}

		public void nodeAdded(Object eventData) {
			XModelTreeEvent event = (XModelTreeEvent) eventData;
			IGroup newProcessItem = new Group(JSFModel.this,
					((XModelObject) event.getInfo()));
			this.add(newProcessItem);
			fireProcessItemAdd(newProcessItem);
		}

		public void nodeRemoved(Object eventData) {
			XModelTreeEvent event = (XModelTreeEvent) eventData;
			IJSFElement removedProcessItem = this.getFromMap(event.getInfo());
			int index = this.indexOf(removedProcessItem);
			removedProcessItem.remove();
			this.remove(removedProcessItem);
			this.removeFromMap(((XModelTreeEvent) eventData).getInfo());
			fireProcessItemRemove((Group) removedProcessItem, index);
			clearCash((String) event.getInfo());
		}
	}

	public JSFHashtable getMap() {
		return map;
	}

	public IJSFElement findElement(String key) {
		return map.get(key);
	}

	public class JSFHashtable implements XModelTreeListener {
		private Hashtable<Object, IJSFElement> map = new Hashtable<Object, IJSFElement>();

		XModelObject source;

		String name;

		public void dispose() {
			disconnectFromModel();
			if (map != null)
				map.clear();
			map = null;
		}

		public void put(Object key, IJSFElement value) {
			map.put(key, value);
		}

		public void setData(XModelObject data) {
			source = data;
			source.getModel().addModelTreeListener(JSFHashtable.this);
			name = source.getAttributeValue("name"); //$NON-NLS-1$
		}

		public void disconnectFromModel() {
			source.getModel().removeModelTreeListener(JSFHashtable.this);
		}

		public IJSFElement get(Object key) {
			return map.get(key);
		}

		public void remove(Object key) {
			map.remove(key);
		}

		public void nodeChanged(XModelTreeEvent event) {
			String path;
			JSFElement element;

			if (!source.getAttributeValue("name").equals(name)) { //$NON-NLS-1$
				name = source.getAttributeValue("name"); //$NON-NLS-1$
				Enumeration<Object> keys = map.keys();
				while (keys.hasMoreElements()) {
					Object key = keys.nextElement();
					if(!(key instanceof String)) continue;
					path = (String) key;
					element = (JSFElement) map.get(path);
					if (element != null) {
						if (element.getSource() != null) {
							map.remove(path);
							map.put(((XModelObject) element.getSource())
									.getPath(), element);
						}
					}
				}
			}
		}

		public void structureChanged(XModelTreeEvent event) {
		}

	}

	protected void clearCash(String path) {
		updateCash(path, true);
	}

	protected void updateCash(String path) {
		updateCash(path, false);
	}

	protected void updateCash(String path, boolean clear) {
		String rpath = path + "/"; //$NON-NLS-1$
		Object[] ks = map.map.keySet().toArray();
		for (int i = 0; i < ks.length; i++) {
			if (!ks[i].equals(path) && !ks[i].toString().startsWith(rpath))
				continue;
			IJSFElement n = (IJSFElement) map.map.get(ks[i]);
			map.map.remove(ks[i]);
			if (clear)
				continue;
			XModelObject o = (XModelObject) n.getSource();
			if (!o.isActive())
				continue;
			map.map.put(o.getPath(), n);
		}
	}

	public Menu getPopupMenu(Control control, Object environment) {
		if (source == null)
			return null;
		if (source.getModelEntity().getActionList().getActionItems().length != 0) {
			XModelObjectActionList l = new XModelObjectActionList(source
					.getModelEntity().getActionList(), source, null,
					environment);

			Menu menu = l.createMenu(control);
			return menu;
		}
		return null;
	}

	public Menu getPopupMenu(Control control) {
		return getPopupMenu(control, null);
	}

	public boolean isConfirmed() {
		return true;
	}

	class JSFOptions implements XModelTreeListener, IJSFOptions {

		XModelObject optionsObject = ModelUtilities.getPreferenceModel()
				.getByPath(JSFFlowTabbedPreferencesPage.JSF_EDITOR_PATH);

		XModelTreeListenerSWTSync optionsListener = new XModelTreeListenerSWTSync(
				this);

		Font viewPathFont = null, linkPathFont = null;

		public JSFOptions() {
			optionsObject.getModel().addModelTreeListener(optionsListener);
		}

		public void dispose() {
			disconnectFromModel();
			if (linkPathFont != null && linkPathFont.isDisposed())
				linkPathFont.dispose();
			linkPathFont = null;
			if (viewPathFont != null && viewPathFont.isDisposed())
				viewPathFont.dispose();
			viewPathFont = null;
		}

		public boolean isGridVisible() {
			String str = optionsObject.getAttributeValue("Show Grid"); //$NON-NLS-1$
			if (str.equals("yes")) //$NON-NLS-1$
				return true;
			else
				return false;
		}

		public int getGridStep() {
			return 8;
		}

		public int getVisualGridStep() {
			String str = optionsObject.getAttributeValue("Grid Step"); //$NON-NLS-1$
			return Integer.parseInt(str);
		}

		public Font getLinkPathFont() {
			String name;
			int size = 8, style = 1;
			int pos, pos2, pos3;
			String str = optionsObject.getAttributeValue("Link Path Font"); //$NON-NLS-1$
			pos = str.indexOf(","); //$NON-NLS-1$
			if (pos < 0)
				name = str;
			else {
				name = str.substring(0, pos);
				pos2 = str.indexOf("size="); //$NON-NLS-1$
				if (pos2 >= 0) {
					pos3 = str.indexOf(",", pos2); //$NON-NLS-1$
					if (pos3 < 0)
						size = Integer.parseInt(str
								.substring(pos2 + 5, str.length()));
					else
						size = Integer.parseInt(str.substring(pos2 + 5, pos3));
				}
				pos2 = str.indexOf("style="); //$NON-NLS-1$
				if (pos2 >= 0) {
					pos3 = str.indexOf(",", pos2); //$NON-NLS-1$
					if (pos3 < 0)
						style = Integer.parseInt(str.substring(pos2 + 6, str
								.length()));
					else
						style = Integer.parseInt(str.substring(pos2 + 6, pos3));
				}

			}

			if (linkPathFont == null) {
				linkPathFont = new Font(null, name, size, style);
			} else {
				if (!linkPathFont.getFontData()[0].getName().equals(name)
						|| linkPathFont.getFontData()[0].getHeight() != size
						|| linkPathFont.getFontData()[0].getStyle() != style) {
					linkPathFont = new Font(null, name, size, style);
				}
			}
			return linkPathFont;
		}

		public Font getViewPathFont() {
			String name;
			int size = 8, style = 1;
			int pos, pos2, pos3;
			String str = optionsObject.getAttributeValue("View Path Font"); //$NON-NLS-1$
			pos = str.indexOf(","); //$NON-NLS-1$
			if (pos < 0)
				name = str;
			else {
				name = str.substring(0, pos);
				pos2 = str.indexOf("size="); //$NON-NLS-1$
				if (pos2 >= 0) {
					pos3 = str.indexOf(",", pos2); //$NON-NLS-1$
					if (pos3 < 0)
						size = Integer.parseInt(str
								.substring(pos2 + 5, str.length()));
					else
						size = Integer.parseInt(str.substring(pos2 + 5, pos3));
				}
				pos2 = str.indexOf("style="); //$NON-NLS-1$
				if (pos2 >= 0) {
					pos3 = str.indexOf(",", pos2); //$NON-NLS-1$
					if (pos3 < 0)
						style = Integer.parseInt(str.substring(pos2 + 6, str
								.length()));
					else
						style = Integer.parseInt(str.substring(pos2 + 6, pos3));
				}

			}

			if (viewPathFont == null) {
				viewPathFont = new Font(null, name, size, style);
			} else {
				if (!viewPathFont.getFontData()[0].getName().equals(name)
						|| viewPathFont.getFontData()[0].getHeight() != size
						|| viewPathFont.getFontData()[0].getStyle() != style) {
					viewPathFont = new Font(null, name, size, style);
				}
			}
			return viewPathFont;
		}

		public void disconnectFromModel() {
			optionsObject.getModel().removeModelTreeListener(optionsListener);
			if (optionsListener != null)
				optionsListener.dispose();
			optionsListener = null;
		}

		public void nodeChanged(XModelTreeEvent event) {
			fireProcessChanged(false);
			Group group;
			for (int i = 0; i < getGroupList().size(); i++) {
				group = (Group) getGroupList().get(i);
				group.fireGroupChange();
				for (int j = 0; j < group.getListOutputLinks().size(); j++) {
					((Link) group.getListOutputLinks().get(j)).fireLinkChange();
				}
			}

		}

		public void structureChanged(XModelTreeEvent event) {
			//fireProcessChanged(false);
		}

		public boolean switchToSelectionTool() {
			String str = JSFPreference.ENABLE_CONTROL_MODE_ON_TRANSITION_COMPLETED
					.getValue();
			if (str != null && str.equals("yes")) //$NON-NLS-1$
				return true;
			else
				return false;
		}

		public boolean showShortcutIcon() {
			String str = JSFPreference.SHOW_SHORTCUT_ICON.getValue();
			if (str != null && str.equals("yes")) //$NON-NLS-1$
				return true;
			else
				return false;
		}

		public boolean showShortcutPath() {
			String str = JSFPreference.SHOW_SHORTCUT_PATH.getValue();
			if (str != null && str.equals("yes")) //$NON-NLS-1$
				return true;
			else
				return false;
		}

	}

}

