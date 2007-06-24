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
package org.jboss.tools.struts.ui.editor.model.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.event.XModelTreeListener;
import org.jboss.tools.common.model.ui.action.XModelObjectActionList;
import org.jboss.tools.common.model.ui.util.ModelUtilities;
import org.jboss.tools.common.model.util.XModelTreeListenerSWTSync;
import org.jboss.tools.struts.StrutsPreference;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;
import org.jboss.tools.struts.ui.StrutsUIPlugin;
import org.jboss.tools.struts.ui.editor.model.IForward;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;
import org.jboss.tools.struts.ui.editor.model.IStrutsElement;
import org.jboss.tools.struts.ui.editor.model.IStrutsElementList;
import org.jboss.tools.struts.ui.editor.model.IStrutsModel;
import org.jboss.tools.struts.ui.editor.model.IStrutsModelListener;
import org.jboss.tools.struts.ui.editor.model.IStrutsOptions;
import org.jboss.tools.struts.ui.preferences.WebFlowTabbedPreferencesPage;
import org.xml.sax.SAXException;

public class StrutsModel extends StrutsElement implements IStrutsModel, PropertyChangeListener, XModelTreeListener {

	protected Vector strutsModelListeners = new Vector();
	protected Vector errors = new Vector();
	protected StrutsHashtable map = new StrutsHashtable();
	protected StrutsElementList processItemList = new ProcessItemList();
	protected StrutsProcessStructureHelper helper = new StrutsProcessStructureHelper();
	protected StrutsOptions options;
	protected int splitPosition = 0;
	protected boolean modified = false;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public StrutsModel() {
		try {
			setName("Struts Model");
		} catch (Exception ex) {
			StrutsUIPlugin.getPluginLog().logError(ex);
		}
	}

	public boolean isBorderPaint() {
		return false;
	}

	public IStrutsOptions getOptions() {
		return options;
	}

	public StrutsModel(Object data) throws SAXException, Exception {
		this();
		setData(((XModelObject) data).getChildByPath("process"));
		map.setData((XModelObject) data);
	}

	public void updateLinks() {
		IProcessItem processItem;
		IForward forward;

		for (int i = 0; i < getProcessItemList().size(); i++) {
			processItem = (IProcessItem) getProcessItemList().get(i);
			for (int j = 0; j < processItem.getForwardList().size(); j++) {
				forward = (IForward) processItem.getForwardList().get(j);
				if (forward.getLink() != null)
					forward.getLink().setTarget();
			}
		}
	}

	public Object get(String name) {
		return null;
	}

	public void put(String name, Object value) {

	}

	public StrutsProcessStructureHelper getHelper() {
		return helper;
	}

	public int getProcessItemCounter() {
		return processItemList.size();
	}

	public IProcessItem getProcessItem(int index) {
		return (IProcessItem) processItemList.get(index);
	}

	public IProcessItem getProcessItem(String processItemName) {
		return (IProcessItem) processItemList.get(processItemName);
	}

	public IProcessItem getProcessItem(Object source) {
		IProcessItem[] is = (IProcessItem[]) processItemList.elements
				.toArray(new IProcessItem[0]);
		for (int i = 0; i < is.length; i++)
			if (is[i].getSource() == source)
				return is[i];
		return null;
	}

	// Module removers

	public void removeProcessItem(String moduleName) {
	}

	public void removeProcessItem(IProcessItem removeProcessItem) {
	}

	public void propertyChange(PropertyChangeEvent pce) {
	}

	IProcessItem selectedProcessItem = null;

	public void setSelectedProcessItem(IProcessItem processItem) {
		IProcessItem oldValue = selectedProcessItem;
		selectedProcessItem = processItem;
		propertyChangeSupport.firePropertyChange("selectedProcessItem",
				oldValue, processItem);
	}

	public IProcessItem getSelectedProcessItem() {
		return selectedProcessItem;
	}

	public String getText() {
		return "";
	}

	XModelTreeListenerSWTSync listener = null;

	public void setData(Object data) throws Exception {
		source = (XModelObject) data;
		if (source == null) {
			return;
		}
		helper.autolayout(source);
		map.put(source.getPath(), this);
		processItemList = new ProcessItemList(source);
		listener = new XModelTreeListenerSWTSync(this);
		source.getModel().addModelTreeListener(listener);
		options = new StrutsOptions();
	}

	public void disconnectFromModel() {
		if (listener != null)
			source.getModel().removeModelTreeListener(listener);
		options.disconnectFromModel();
		map.disconnectFromModel();
	}

	public boolean isEditable() {
		return source != null
				&& source.getModelEntity().isEditable(source, "body");
	}

	public boolean areCommentsVisible() {
		if (source == null)
			return false;
		return !helper.areProcessCommentsHidden(source);
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean set) {
		boolean oldValue = modified;
		modified = set;
		propertyChangeSupport
				.firePropertyChange("modified", oldValue, modified);
	}

	// -----------------------------------------------------------------------
	// fire events
	// -----------------------------------------------------------------------

	public void fireProcessChanged(boolean flag) {
		Vector targets = (Vector) strutsModelListeners.clone();
		for (int i = 0; i < targets.size(); i++) {
			IStrutsModelListener listener = (IStrutsModelListener) targets
					.get(i);
			if (listener != null) {
				listener.processChanged(flag);
			}
		}
		setModified(true);
	}

	public void fireProcessItemAdd(IProcessItem newProcessItem) {
		Vector targets = (Vector) strutsModelListeners.clone();
		for (int i = 0; i < targets.size(); i++) {
			IStrutsModelListener listener = (IStrutsModelListener) targets
					.get(i);
			if (listener != null) {
				listener.processItemAdd(newProcessItem);
			}
		}
		setModified(true);
	}

	public void fireProcessItemRemove(IProcessItem newProcessItem, int index) {
		Vector targets = (Vector) strutsModelListeners.clone();
		for (int i = 0; i < targets.size(); i++) {
			IStrutsModelListener listener = (IStrutsModelListener) targets
					.get(i);
			if (listener != null) {
				listener.processItemRemove(newProcessItem);
			}
		}
		setModified(true);
	}

	public void fireElementRemoved(IStrutsElement element, int index) {
	}

	public void fireElementInserted(IStrutsElement element) {
	}

	public void addStrutsModelListener(IStrutsModelListener listener) {
		strutsModelListeners.add(listener);
	}

	public void removeStrutsModelListener(IStrutsModelListener listener) {
		strutsModelListeners.remove(listener);
	}

	public void remove() {
	}

	public String getIconPath() {
		return null;
	}

	public void setStartProcessItemName(String module) {
	}

	public IProcessItem addProcessItem(IProcessItem processItem) {
		return null;
	}

	public IProcessItem addProcessItem(String processItemName) {
		return null;
	}

	public IProcessItem addProcessItem(Object source, int x, int y) {
		return null;
	}

	public IProcessItem addProcessItem(IStrutsElement element) {
		return null;
	}

	public IStrutsElementList getProcessItemList() {
		return processItemList;
	}

	public void nodeChanged(XModelTreeEvent event) {
		if (source != null && helper.isNodeChangeListenerLocked(source))
			return;
		try {
			fireProcessChanged(false);
			IStrutsElement element = (StrutsElement) map.get(event.getInfo());
			if (element != null
					&& !event.getModelObject().getPath()
							.equals(event.getInfo())) {
				updateCash((String) event.getInfo());
			}
			String path = event.getModelObject().getPath();
			element = (path == null) ? null : (IStrutsElement) map.get(path);
			if (element == null) {
				return;
			}
			element.nodeChanged(event);
		} catch (Exception e) {
			StrutsUIPlugin.getPluginLog().logError("Error while processing model event", e);
		}
	}

	public void structureChanged(XModelTreeEvent event) {
		StrutsElement element;
		try {
			Object obj = event.getModelObject().getPath();
			if (obj == null)
				return;

			element = (StrutsElement) map.get(obj);
			if (element == null) {
				return;
			}
			if (event.kind() == XModelTreeEvent.STRUCTURE_CHANGED) {
				if (obj.equals(source.getPath())) {
					for (int i = 0; i < map.map.size(); i++) {
						element = (StrutsElement) map.map.values().toArray()[i];
						element.nodeChanged(event);
					}
					fireProcessChanged(true);
				}

				element.structureChanged(event);
			} else if (event.kind() == XModelTreeEvent.CHILD_ADDED) {
				element.nodeAdded(event);
			} else if (event.kind() == XModelTreeEvent.CHILD_REMOVED) {
				element.nodeRemoved(event);
			}
		} catch (Exception exc) {
			StrutsUIPlugin.getPluginLog().logError("Error while processing model event", exc);
		}
	}

	public void putToMap(Object key, Object value) {
		getMap().put(key, value);
	}

	public void removeFromMap(Object key) {
		getMap().remove(key);
	}

	public IStrutsElement getFromMap(Object key) {
		return getMap().get(key);
	}

	public class ProcessItemPropertyChangeListener implements
			PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			IProcessItem processItem = (IProcessItem) event.getSource();
			if (event.getPropertyName().equals("selected")) {
				if (((Boolean) event.getNewValue()).booleanValue())
					setSelectedProcessItem(processItem);
			}
		}
	}

	public class ProcessItemList extends StrutsElementList {
		protected ProcessItemList() {
		}

		public ProcessItemList(XModelObject processItemSource) {
			super(StrutsModel.this, StrutsModel.this.source);
			if (((XModelObject) StrutsModel.this.getSource()).getPath() == null)
				return;

			XModelObject[] processItemNodeList = helper
					.getProcessItems(StrutsModel.this.source);

			for (int i = 0; i < processItemNodeList.length; i++) {
				IProcessItem newProcessItem = new ProcessItem(StrutsModel.this,
						processItemNodeList[i]);
				newProcessItem.addPropertyChangeListener("selected",
						new ProcessItemPropertyChangeListener());
				add(newProcessItem);
			}
		}

		public void structureChanged(Object eventData) {
		}

		public void nodeAdded(Object eventData) {
			XModelTreeEvent event = (XModelTreeEvent) eventData;
			IProcessItem newProcessItem = new ProcessItem(StrutsModel.this,
					((XModelObject) event.getInfo()));
			this.add(newProcessItem);
			fireProcessItemAdd(newProcessItem);
		}

		public void nodeRemoved(Object eventData) {
			XModelTreeEvent event = (XModelTreeEvent) eventData;
			IStrutsElement removedProcessItem = this
					.getFromMap(event.getInfo());
			int index = this.indexOf(removedProcessItem);
			removedProcessItem.remove();
			this.remove(removedProcessItem);
			this.removeFromMap(((XModelTreeEvent) eventData).getInfo());
			fireProcessItemRemove((ProcessItem) removedProcessItem, index);
			clearCash((String) event.getInfo());
		}
	}

	public StrutsHashtable getMap() {
		return map;
	}

	public IStrutsElement findElement(String key) {
		return map.get(key);
	}

	public class StrutsHashtable implements XModelTreeListener {
		Hashtable map = new Hashtable();

		XModelObject source;

		String name;

		public void setData(XModelObject data) {
			source = data;
			source.getModel().addModelTreeListener(StrutsHashtable.this);
			name = source.getAttributeValue("name");
		}

		public void disconnectFromModel() {
			source.getModel().removeModelTreeListener(StrutsHashtable.this);
		}

		public void put(Object key, Object value) {
			map.put(key, value);
		}

		public IStrutsElement get(Object key) {
			return (IStrutsElement) map.get(key);
		}

		public void remove(Object key) {
			map.remove(key);
		}

		public void nodeChanged(XModelTreeEvent event) {
			String path;
			StrutsElement element;

			if (!source.getAttributeValue("name").equals(name)) {
				name = source.getAttributeValue("name");
				Enumeration keys = map.keys();
				while (keys.hasMoreElements()) {
					path = (String) keys.nextElement();
					element = (StrutsElement) map.get(path);
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
		String rpath = path + "/";
		Object[] ks = map.map.keySet().toArray();
		for (int i = 0; i < ks.length; i++) {
			if (!ks[i].equals(path) && !ks[i].toString().startsWith(rpath))
				continue;
			IStrutsElement n = (IStrutsElement) map.map.get(ks[i]);
			map.map.remove(ks[i]);
			if (clear)
				continue;
			XModelObject o = (XModelObject) n.getSource();
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

	class StrutsOptions implements XModelTreeListener, IStrutsOptions {
		XModelObject optionsObject = ModelUtilities.getPreferenceModel()
				.getByPath(WebFlowTabbedPreferencesPage.STRUTS_EDITOR_PATH);

		XModelTreeListenerSWTSync optionsListener = new XModelTreeListenerSWTSync(
				this);

		Font actionFont = null, forwardFont = null, pathFont = null,
				commentFont = null;

		public StrutsOptions() {
			optionsObject.getModel().addModelTreeListener(optionsListener);
		}

		public boolean isGridVisible() {
			String str = optionsObject.getAttributeValue("Show Grid");
			if (str.equals("yes"))
				return true;
			else
				return false;
		}

		public int getGridStep() {
			String str = optionsObject.getAttributeValue("Grid Step");
			return new Integer(str).intValue();
		}

		public Font getActionFont() {
			String name;
			int size = 8, style = 1;
			int pos, pos2, pos3;
			String str = optionsObject.getAttributeValue("Action Font");
			pos = str.indexOf(",");
			if (pos < 0)
				name = str;
			else {
				name = str.substring(0, pos);
				pos2 = str.indexOf("size=");
				if (pos2 >= 0) {
					pos3 = str.indexOf(",", pos2);
					if (pos3 < 0)
						size = new Integer(str
								.substring(pos2 + 5, str.length())).intValue();
					else
						size = new Integer(str.substring(pos2 + 5, pos3))
								.intValue();
				}
				pos2 = str.indexOf("style=");
				if (pos2 >= 0) {
					pos3 = str.indexOf(",", pos2);
					if (pos3 < 0)
						style = new Integer(str.substring(pos2 + 6, str
								.length())).intValue();
					else
						style = new Integer(str.substring(pos2 + 6, pos3))
								.intValue();
				}

			}

			if (actionFont == null) {
				actionFont = new Font(null, name, size, style);
			} else {
				if (!actionFont.getFontData()[0].getName().equals(name)
						|| actionFont.getFontData()[0].getHeight() != size
						|| actionFont.getFontData()[0].getStyle() != style) {
					actionFont = new Font(null, name, size, style);
				}
			}
			return actionFont;
		}

		public Font getForwardFont() {
			String name;
			int size = 8, style = 1;
			int pos, pos2, pos3;
			String str = optionsObject.getAttributeValue("Forward Font");
			pos = str.indexOf(",");
			if (pos < 0)
				name = str;
			else {
				name = str.substring(0, pos);
				pos2 = str.indexOf("size=");
				if (pos2 >= 0) {
					pos3 = str.indexOf(",", pos2);
					if (pos3 < 0)
						size = new Integer(str
								.substring(pos2 + 5, str.length())).intValue();
					else
						size = new Integer(str.substring(pos2 + 5, pos3))
								.intValue();
				}
				pos2 = str.indexOf("style=");
				if (pos2 >= 0) {
					pos3 = str.indexOf(",", pos2);
					if (pos3 < 0)
						style = new Integer(str.substring(pos2 + 6, str
								.length())).intValue();
					else
						style = new Integer(str.substring(pos2 + 6, pos3))
								.intValue();
				}

			}

			if (forwardFont == null) {
				forwardFont = new Font(null, name, size, style);
			} else {
				if (!forwardFont.getFontData()[0].getName().equals(name)
						|| forwardFont.getFontData()[0].getHeight() != size
						|| forwardFont.getFontData()[0].getStyle() != style) {
					forwardFont = new Font(null, name, size, style);
				}
			}
			return forwardFont;
		}

		public Font getPathFont() {
			String name;
			int size = 8, style = 1;
			int pos, pos2, pos3;
			String str = optionsObject.getAttributeValue("Path Font");
			pos = str.indexOf(",");
			if (pos < 0)
				name = str;
			else {
				name = str.substring(0, pos);
				pos2 = str.indexOf("size=");
				if (pos2 >= 0) {
					pos3 = str.indexOf(",", pos2);
					if (pos3 < 0)
						size = new Integer(str
								.substring(pos2 + 5, str.length())).intValue();
					else
						size = new Integer(str.substring(pos2 + 5, pos3))
								.intValue();
				}
				pos2 = str.indexOf("style=");
				if (pos2 >= 0) {
					pos3 = str.indexOf(",", pos2);
					if (pos3 < 0)
						style = new Integer(str.substring(pos2 + 6, str
								.length())).intValue();
					else
						style = new Integer(str.substring(pos2 + 6, pos3))
								.intValue();
				}

			}

			if (pathFont == null) {
				pathFont = new Font(null, name, size, style);
			} else {
				if (!pathFont.getFontData()[0].getName().equals(name)
						|| pathFont.getFontData()[0].getHeight() != size
						|| pathFont.getFontData()[0].getStyle() != style) {
					pathFont = new Font(null, name, size, style);
				}
			}
			return pathFont;
		}

		public Font getCommentFont() {
			String name;
			int size = 8, style = 1;
			int pos, pos2, pos3;
			String str = optionsObject.getAttributeValue("Comment Font");
			pos = str.indexOf(",");
			if (pos < 0)
				name = str;
			else {
				name = str.substring(0, pos);
				pos2 = str.indexOf("size=");
				if (pos2 >= 0) {
					pos3 = str.indexOf(",", pos2);
					if (pos3 < 0)
						size = new Integer(str
								.substring(pos2 + 5, str.length())).intValue();
					else
						size = new Integer(str.substring(pos2 + 5, pos3))
								.intValue();
				}
				pos2 = str.indexOf("style=");
				if (pos2 >= 0) {
					pos3 = str.indexOf(",", pos2);
					if (pos3 < 0)
						style = new Integer(str.substring(pos2 + 6, str
								.length())).intValue();
					else
						style = new Integer(str.substring(pos2 + 6, pos3))
								.intValue();
				}

			}

			if (commentFont == null) {
				commentFont = new Font(null, name, size, style);
			} else {
				if (!commentFont.getFontData()[0].getName().equals(name)
						|| commentFont.getFontData()[0].getHeight() != size
						|| commentFont.getFontData()[0].getStyle() != style) {
					commentFont = new Font(null, name, size, style);
				}
			}
			return commentFont;
		}

		public boolean switchToSelectionTool() {
			String str = StrutsPreference.ENABLE_CONTROL_MODE_ON_TRANSITION_COMPLETED
					.getValue();
			if (str != null && str.equals("yes"))
				return true;
			else
				return false;
		}

		public boolean showShortcutIcon() {
			String str = StrutsPreference.SHOW_SHORTCUT_ICON.getValue();
			if (str != null && str.equals("yes"))
				return true;
			else
				return false;
		}

		public boolean showShortcutPath() {
			String str = StrutsPreference.SHOW_SHORTCUT_PATH.getValue();
			if (str != null && str.equals("yes"))
				return true;
			else
				return false;
		}

		public void disconnectFromModel() {
			optionsObject.getModel().removeModelTreeListener(optionsListener);
		}

		public void nodeChanged(XModelTreeEvent event) {
			fireProcessChanged(false);
			ProcessItem item;
			Forward forward;
			for (int i = 0; i < getProcessItemList().size(); i++) {
				item = (ProcessItem) getProcessItemList().get(i);
				item.fireProcessItemChange();
				for (int j = 0; j < item.getForwardList().size(); j++) {
					forward = (Forward) item.getForwardList().get(j);
					forward.fireForwardChanged();
					if (forward.getLink() != null)
						((Link) forward.getLink()).fireLinkChange();
				}

			}

		}

		public void structureChanged(XModelTreeEvent event) {
		}

	}

}
