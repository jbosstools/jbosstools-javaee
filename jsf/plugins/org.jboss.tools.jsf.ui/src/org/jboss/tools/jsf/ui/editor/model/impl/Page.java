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

import java.beans.PropertyChangeEvent;
import java.util.*;

import org.eclipse.draw2d.geometry.Rectangle;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.jsf.ui.editor.model.IJSFElementListListener;
import org.jboss.tools.jsf.ui.editor.model.IPage;
import org.jboss.tools.jsf.ui.editor.model.IPageListener;
import org.jboss.tools.jsf.ui.editor.model.IJSFElement;
import org.jboss.tools.jsf.ui.editor.model.IJSFElementList;
import org.jboss.tools.jsf.ui.editor.model.ILink;
import org.jboss.tools.jsf.ui.editor.model.IGroup;

public class Page extends JSFElement implements IPage, IJSFElementListListener{
	public LinkList linkList;
	Rectangle rect = new Rectangle(0, 0, 10, 10);

	public IJSFElementList getLinkList() {
		return linkList;
	}

	public void setBounds(int x, int y, int width, int height) {
		rect = new Rectangle(x, y, width, height);
	}

	public Rectangle getBounds() {
		return rect;
	}

	public class LinkList extends JSFElementList {
		public LinkList(XModelObject listSource) {
			super(Page.this);
			XModelObject[] links = listSource.getChildren();
			for (int i = 0; i < links.length; i++) {
				ILink newLink = new Link(Page.this, links[i]);
				add(newLink);
				((Group) Page.this.getParentJSFElement())
						.addOutputLink(newLink);
			}
		}

		public void structureChanged(Object eventData) {
			XModelObject listSource = (XModelObject) Page.this.getSource();
			XModelObject[] links = listSource.getChildren();
			if (links.length != size()) {
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
				if (!links[i].getPathPart().equals(o.getPathPart())) {
					moveTo(pe, size() - 1);
					--i;
				}
			}

		}

		public void nodeAdded(Object eventData) {
			XModelTreeEvent event = (XModelTreeEvent) eventData;
			ILink newLink = new Link(Page.this, (XModelObject) event.getInfo());
			addLink(newLink);
		}

		public void nodeRemoved(Object eventData) {
			XModelTreeEvent event = (XModelTreeEvent) eventData;
			ILink removedLink = (ILink) getFromMap(event.getInfo());
			removeLink(removedLink);
		}

		public void addLink(ILink link) {
			((JSFModel) jsfModel).putToMap(link.getSource(), link);
			this.add(link);
			IGroup group = (IGroup) Page.this.getParentJSFElement();
			int index = 0;

			for (int i = 0; i < group.getPageList().size(); i++) {
				index += ((IPage) group.getPageList().get(i)).getLinkList()
						.size();
				if (i == group.getPageList().indexOf(Page.this))
					break;
			}
			group.addOutputLink(link, index - 1);
			link.setTarget();
		}

		public void removeLink(ILink link) {
			removeFromMap(link);
			int index = this.indexOf(link);
			link.getToGroup().removeInputLink(link);
			this.remove(link);
			((Group) Page.this.getParentJSFElement()).removeOutputLink(link);

			fireLinkRemove(link, index);
			link.remove();
		}
	}

	public List<IPageListener> pageListeners = new Vector<IPageListener>();
	IJSFElementList partList = new JSFElementList();
	String name;
	String target;
	boolean relink = false;

	public Page(IJSFElement parent, XModelObject source) {
		super(parent, source);
		linkList = new LinkList(source);
		linkList.addJSFElementListListener(this);

		target = source.getAttributeValue(TARGET_PROPERTY);
		initLink();
		name = getName();
	}

	public void dispose() {
		super.dispose();
		if (pageListeners != null)
			pageListeners.clear();
		pageListeners = null;
	}

	public XModelObject getTargetModel() {
		XModelObject target = jsfModel.getHelper().getItemOutputTarget(source);
		return target;
	}

	public boolean isHidden() {
		String hidden;
		if (source == null)
			source = (XModelObject) parent.getSource();
		hidden = source.getAttributeValue(Link.HIDDEN_PROPERTY);
		if ("yes".equals(hidden)) //$NON-NLS-1$
			return true;
		else
			return false;
	}

	public String getTargetString() {
		return target;
	}

	public void initLink() {
	}

	// ------------------------------------------------------------------------
	// IMessage implementation
	// ------------------------------------------------------------------------

	public boolean canRename() {
		return true;
	}

	public boolean canDelete() {
		return true;
	}

	public String getText() {
		return "\t\t\t\t<MSG MESSAGE=\"" + getName() + "\"/>"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	// ------------------------------------------------------------------------
	// Event support
	// ------------------------------------------------------------------------

	public void addPageListener(IPageListener listener) {
		pageListeners.add(listener);
	}

	public void removePageListener(IPageListener listener) {
		pageListeners.remove(listener);
	}

	void firePageRemoved() {
		List<IPageListener> listeners = new ArrayList<IPageListener>();
		listeners.addAll(pageListeners);
		for (int i = 0; i < listeners.size(); i++) {
			IPageListener listener = listeners.get(i);
			if (listener != null)
				listener.pageRemoved(this);
		}
	}

	public void removeLink(IPage message) {
	}

	public void rename(String newName) {
	}

	public IGroup getGroup() {
		return (IGroup) getParentJSFElement();
	}

	public IJSFElementList getPartList() {
		return partList;
	}

	public boolean isLinkAllowed() {
		return true;
	}

	public Enumeration children() {
		return null;
	}

	public boolean isLeaf() {
		return false;
	}

	public void remove() {
		for (int i = getLinkList().size() - 1; i >= 0; i--) {
			ILink link = (ILink) getLinkList().get(i);
			linkList.removeLink(link);
		}
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public boolean hasErrors() {
		return getJSFModel().getHelper().hasErrors((XModelObject) getSource());
	}

	public void nodeChanged(Object eventData) {
		if (!name.equals(getName())) {
			propertyChangeSupport.firePropertyChange("name", name, getName()); //$NON-NLS-1$
			name = getName();
		}
	}

	public void fireLinkAdd(ILink link) {
		List<IPageListener> listeners = new ArrayList<IPageListener>();
		listeners.addAll(pageListeners);
		for (int i = 0; i < listeners.size(); i++) {
			IPageListener listener = (IPageListener) listeners.get(i);
			if (listener != null && listener.isPageListenerEnable())
				((IPageListener) listeners.get(i)).linkAdd(this, link);
		}
		((Group) getParentJSFElement()).fireLinkAdd(this, link);
	}

	public void fireLinkRemove(ILink link, int index) {
		List<IPageListener> listeners = new ArrayList<IPageListener>();
		listeners.addAll(pageListeners);
		for (int i = 0; i < listeners.size(); i++) {
			IPageListener listener = (IPageListener) listeners.get(i);
			if (listener != null && listener.isPageListenerEnable())
				((IPageListener) listeners.get(i)).linkRemove(this, link);
		}
		((Group) getParentJSFElement()).fireLinkRemove(this, link, index);
	}

	public void firePageChange(IPage message, PropertyChangeEvent evt) {
	}

	public boolean isRelink() {
		return relink;
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
	}

	public void listElementRemove(IJSFElementList list, IJSFElement element,
			int index) {
	}

	public void listElementChange(IJSFElementList list, IJSFElement element,
			int index, PropertyChangeEvent event) {

	}

	public void nodeAdded(Object eventData) {
		linkList.nodeAdded(eventData);
	}

	public void nodeRemoved(Object eventData) {
		linkList.nodeRemoved(eventData);
	}

}

