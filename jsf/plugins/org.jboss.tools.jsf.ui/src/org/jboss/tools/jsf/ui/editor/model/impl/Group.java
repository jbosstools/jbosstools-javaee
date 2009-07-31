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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import java.beans.PropertyChangeEvent;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.event.*;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.ui.editor.model.IPage;
import org.jboss.tools.jsf.ui.editor.model.IJSFElement;
import org.jboss.tools.jsf.ui.editor.model.IJSFElementList;
import org.jboss.tools.jsf.ui.editor.model.IJSFElementListListener;
import org.jboss.tools.jsf.ui.editor.model.IJSFModel;
import org.jboss.tools.jsf.ui.editor.model.ILink;
import org.jboss.tools.jsf.ui.editor.model.IGroup;
import org.jboss.tools.jsf.ui.editor.model.IGroupListener;

public class Group extends JSFElement implements IGroup, IJSFElementListListener {
	public static final Color DEFAULT_FOREGROUND_COLOR = new Color(null, 0x00,
			0x00, 0x00);
	public static final Color DEFAULT_BACKGROUND_COLOR = new Color(null, 0xE4,
			0xE4, 0xE4);
	public static String TYPE_PROPERTY = "type"; //$NON-NLS-1$
	public static String SUBTYPE_PROPERTY = "subtype"; //$NON-NLS-1$
	public static String COMMENT_PROPERTY = "comment"; //$NON-NLS-1$

	protected Color headerForegroundColor = DEFAULT_FOREGROUND_COLOR;
	protected Color headerBackgroundColor = DEFAULT_BACKGROUND_COLOR;
	String type;
	protected Image icon = null;
	public PageList pageList;

	List<IGroupListener> groupListeners = new Vector<IGroupListener>();
	List<ILink> inputLinks = new Vector<ILink>();
	List<ILink> outputLinks = new Vector<ILink>();

	public void dispose() {
		super.dispose();
		if (groupListeners != null)
			groupListeners.clear();
		groupListeners = null;
		if (inputLinks != null)
			inputLinks.clear();
		inputLinks = null;
		if (outputLinks != null)
			outputLinks.clear();
		outputLinks = null;
	}

	public void addInputLink(ILink link) {
		if (!inputLinks.contains(link))
			inputLinks.add(link);
	}

	public void removeInputLink(ILink link) {
		inputLinks.remove(link);
	}

	public void addOutputLink(ILink link, int index) {
		if (!outputLinks.contains(link))
			outputLinks.add(index, link);
	}

	public void addOutputLink(ILink link) {
		outputLinks.add(link);
	}

	public void removeOutputLink(ILink link) {
		outputLinks.remove(link);
	}

	public Group(IJSFModel model, XModelObject groupNode) {
		super(model, groupNode);

		this.type = groupNode.getAttributeValue(TYPE_PROPERTY);
		global = false;
		icon = EclipseResourceUtil.getImage(groupNode);

		if (type != null) {
			pageList = new PageList(groupNode);
			pageList.addJSFElementListListener(this);
		}
	}

	public Image getImage() {
		return EclipseResourceUtil.getImage((XModelObject) source);
	}

	public Group(IJSFModel model, IJSFElement element) {
		super(model);
	}

	public String getViewClassName() {
		return ""; //$NON-NLS-1$
	}

	public String getVisiblePath() {
		return this.getJSFModel().getHelper().getPageTitle(source);
	}

	boolean selected = false;

	public boolean isSelected() {
		return selected;
	}

	public boolean isPage() {
		return true;
	}

	public boolean isComment() {
		return false;
	}

	public void setSelected(boolean set) {
		boolean oldValue = selected;
		selected = set;
		this.propertyChangeSupport
				.firePropertyChange("selected", oldValue, set); //$NON-NLS-1$
		if (set)
			this.getJSFModel().setSelectedProcessItem(this);
	}

	public void clearSelection() {
		IJSFElementList list = getJSFModel().getGroupList();
		for (int i = 0; i < list.size(); i++) {
			IGroup activity = (IGroup) list.get(i);
			activity.setSelected(false);
		}
	}

	public boolean hasPageHiddenLinks() {
		if (getSource() != null) {
			return getJSFModel().getHelper().hasPageHiddenLinks(
					(XModelObject) getSource());
		} else
			return false;
	}

	public IGroup getCommentTarget() {
		return null;
	}

	public boolean isConfirmed() {
		if (getSource() != null) {
			return !getJSFModel().getHelper().isUnconfirmedPage(
					(XModelObject) getSource());
		} else
			return false;
	}

	public boolean isPattern() {
		if (getSource() != null) {
			return getJSFModel().getHelper().isGroupPattern(
					(XModelObject) getSource());
		} else
			return false;
	}

	public boolean hasErrors() {
		return getJSFModel().getHelper().hasErrors((XModelObject) getSource());
	}

	// getters
	public void setHeaderForegroundColor(Color color) {
		headerForegroundColor = color;
	}

	public Color getHeaderForegroundColor() {
		return headerForegroundColor;
	}

	public void setHeaderBackgroundColor(Color color) {
		headerBackgroundColor = color;
	}

	public Color getHeaderBackgroundColor() {
		return headerBackgroundColor;
	}

	public ILink[] getLinks() {
		ILink link;
		IJSFElementList list;
		IJSFElementList pages = this.getPageList();
		List<ILink> links = new ArrayList<ILink>();
		links.addAll(inputLinks);
		for (int i = 0; i < pages.size(); i++) {
			list = ((IPage) pages.get(i)).getLinkList();
			for (int j = 0; j < list.size(); j++) {
				link = (ILink) list.get(j);
				if (link == null)
					continue;
				links.add(link);
			}
		}
		ILink[] trans = new ILink[links.size()];
		links.toArray(trans);
		return trans;
	}

	public ILink[] getInputLinks() {
		return (ILink[]) inputLinks.toArray();
	}

	public List getListInputLinks() {
		return inputLinks;
	}

	public ILink[] getOutputLinks() {
		return getLinks();
	}

	public List getListOutputLinks() {
		return outputLinks;
	}

	public IPage getPage(String pageName) {
		return (Page) pageList.get(pageName);
	}

	public IJSFElementList getPageList() {
		return pageList;
	}

	// Unit messages
	public void fireGroupChange() {
		if (groupListeners.size() == 0)
			return;
		List<IGroupListener> targets = new ArrayList<IGroupListener>();
		targets.addAll(groupListeners);
		for (int i = 0; i < targets.size(); i++) {
			IGroupListener listener = (IGroupListener) targets.get(i);
			if (listener != null) {
				listener.groupChange();
			}
		}
	}

	public void firePageAdd(IPage page) {
		List<IGroupListener> listeners = new ArrayList<IGroupListener>();
		listeners.addAll(groupListeners);
		for (int i = 0; i < listeners.size(); i++) {
			IGroupListener listener = (IGroupListener) listeners.get(i);
			if (listener != null && listener.isGroupListenerEnable())
				((IGroupListener) listeners.get(i)).pageAdd(this, page);
		}
	}

	public void firePageRemove(IPage page, int index) {
		List<IGroupListener> listeners = new ArrayList<IGroupListener>();
		listeners.addAll(groupListeners);
		for (int i = 0; i < listeners.size(); i++) {
			IGroupListener listener = (IGroupListener) listeners.get(i);
			if (listener != null && listener.isGroupListenerEnable())
				((IGroupListener) listeners.get(i)).pageRemove(this, page);
		}
	}

	public void firePageChange(IPage message, PropertyChangeEvent evt) {
	}

	public void fireLinkAdd(IPage page, ILink link) {
		List<IGroupListener> listeners = new ArrayList<IGroupListener>();
		listeners.addAll(groupListeners);
		for (int i = 0; i < listeners.size(); i++) {
			IGroupListener listener = (IGroupListener) listeners.get(i);
			if (listener != null && listener.isGroupListenerEnable())
				((IGroupListener) listeners.get(i)).linkAdd(page, link);
		}
	}

	public void fireLinkRemove(IPage page, ILink link, int index) {
		List<IGroupListener> listeners = new ArrayList<IGroupListener>();
		listeners.addAll(groupListeners);
		for (int i = 0; i < listeners.size(); i++) {
			IGroupListener listener = (IGroupListener) listeners.get(i);
			if (listener != null && listener.isGroupListenerEnable())
				((IGroupListener) listeners.get(i)).linkRemove(page, link);
		}
	}

	// remove state from model
	public void remove() {
		for (int i = 0; i < getPageList().size(); i++) {
			((Page) getPageList().get(i)).remove();
		}
	}

	public void addGroupListener(IGroupListener listener) {
		groupListeners.add(listener);
	}

	public void removeGroupListener(IGroupListener listener) {

	}

	public void removeFromJSFModel() {
	}

	public void nodeChanged(Object eventData) {
		fireGroupChange();
		this.propertyChangeSupport.firePropertyChange("name", "", this //$NON-NLS-1$ //$NON-NLS-2$
				.getSourceProperty("name")); //$NON-NLS-1$
		this.propertyChangeSupport.firePropertyChange("shape", "", this //$NON-NLS-1$ //$NON-NLS-2$
				.getSourceProperty("shape")); //$NON-NLS-1$
		this.propertyChangeSupport.firePropertyChange("path", "", this //$NON-NLS-1$ //$NON-NLS-2$
				.getSourceProperty("path")); //$NON-NLS-1$
	}

	public class PageList extends JSFElementList {
		public PageList(XModelObject listSource) {
			super(Group.this);
			XModelObject[] pages = listSource.getChildren();
			for (int i = 0; i < pages.length; i++) {
				IPage newPage = new Page(Group.this, pages[i]);
				add(newPage);
			}
		}

		public void structureChanged(Object eventData) {
			XModelObject listSource = (XModelObject) Group.this.getSource();
			XModelObject[] pages = listSource.getChildren();
			if (pages.length != size()) {
				// not implemented
				return;
			}
			for (int i = 0; i < size(); i++) {
				IJSFElement pe = (IJSFElement) get(i);
				XModelObject o = (XModelObject) pe.getSource();
				if (listSource.getChildByPath(o.getPathPart()) == null) {
					// not implemented
					return;
				}
			}
			// implemented only change of order of elements in list
			for (int i = 0; i < size(); i++) {
				IJSFElement pe = (IJSFElement) get(i);
				XModelObject o = (XModelObject) pe.getSource();
				if (!pages[i].getPathPart().equals(o.getPathPart())) {
					moveTo(pe, size() - 1);
					--i;
				}
			}

		}

		public void nodeAdded(Object eventData) {
			XModelTreeEvent event = (XModelTreeEvent) eventData;
			IPage newPage = new Page(Group.this, ((XModelObject) event
					.getInfo()));
			addPage(newPage);
		}

		public void nodeRemoved(Object eventData) {
			XModelTreeEvent event = (XModelTreeEvent) eventData;
			IPage removedPage = (IPage) getFromMap(event.getInfo());
			removedPage.remove();
			removePage(removedPage);
		}

		public void addPage(IPage page) {
			((JSFModel) jsfModel).putToMap(page.getSource(), page);
			this.add(page);
			firePageAdd(page);
		}

		public void removePage(IPage page) {
			removeFromMap(page);
			int index = this.indexOf(page);
			this.remove(page);
			firePageRemove(page, index);
		}
	}

	public void structureChanged(Object eventData) {
		pageList.structureChanged(eventData);
	}

	public void nodeAdded(Object eventData) {
		pageList.nodeAdded(eventData);
	}

	public void nodeRemoved(Object eventData) {
		pageList.nodeRemoved(eventData);
	}

	public boolean isElementListListenerEnable() {
		return true;
	}

	public void setElementListListenerEnable(boolean set) {

	}

	public void listElementMove(IJSFElementList list, IJSFElement element,
			int newIndex, int oldIndex) {

	}

	public void listElementAdd(IJSFElementList list, IJSFElement element,
			int index) {
		this.firePageAdd((IPage) element);
	}

	public void listElementRemove(IJSFElementList list, IJSFElement element,
			int index) {
		this.firePageRemove((IPage) element, index);
	}

	public void listElementChange(IJSFElementList list, IJSFElement element,
			int index, PropertyChangeEvent event) {

	}

}