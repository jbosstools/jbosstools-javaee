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
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.widgets.*;

import org.jboss.tools.common.model.*;
import org.jboss.tools.jsf.ui.editor.model.IPage;
import org.jboss.tools.jsf.ui.editor.model.IJSFElement;
import org.jboss.tools.jsf.ui.editor.model.ILink;
import org.jboss.tools.jsf.ui.editor.model.ILinkListener;
import org.jboss.tools.jsf.ui.editor.model.IGroup;
import org.jboss.tools.jsf.ui.editor.model.ISegment;

public class Link extends JSFElement implements ILink {
	public static final String PATH_PROPERTY = "link shape"; //$NON-NLS-1$
	public static final String SHAPE_PROPERTY = "shape"; //$NON-NLS-1$
	public static final String HIDDEN_PROPERTY = "hidden"; //$NON-NLS-1$

	private String pathString;
	private String linkStatus;
	private boolean preferredMode = false;
	private int preferredLength = 0;
	List<ILinkListener> linkListeners = new Vector<ILinkListener>();
	XModelObject target = null;
	IGroup toGroup;
	ISegment headSegment;
	ISegment tailSegment;

	public void dispose() {
		super.dispose();
		if (linkListeners != null)
			linkListeners.clear();
		linkListeners = null;
	}

	public boolean isPreferredMode() {
		return preferredMode;
	}

	public int getPreferredLength() {
		if (preferredMode)
			return preferredLength;
		else
			return 0;
	}

	public String getPathFromModel() {
		if (preferredMode)
			return ""; //$NON-NLS-1$
		else {
			return source.getAttributeValue(Link.SHAPE_PROPERTY);
		}
	}

	public PointList getPointList() {
		int[] path = null;

		pathString = source.getAttributeValue(SHAPE_PROPERTY);
		path = jsfModel.getHelper().asIntArray(source, SHAPE_PROPERTY);

		PointList list = new PointList();

		if (path.length < 4)
			return list;

		for (int i = 0; i < path.length; i += 2) {
			list.addPoint(path[i], path[i + 1]);
		}

		return list;
	}

	public void savePointList(PointList list) {
		String value = ""; //$NON-NLS-1$
		for (int i = 0; i < list.size(); i++) {
			if (i != 0)
				value += ","; //$NON-NLS-1$
			Point p = list.getPoint(i);
			value += p.x + "," + p.y; //$NON-NLS-1$
		}
		jsfModel.getHelper().setAttributeValue(source, SHAPE_PROPERTY, value);
	}

	public void clearPointList() {
		source.setAttributeValue(SHAPE_PROPERTY, ""); //$NON-NLS-1$
	}

	public Link(IJSFElement parent, XModelObject source) {
		super(parent, source);

		target = getJSFModel().getHelper().getItemOutputTarget(source);

		int[] path = null;

		pathString = source.getAttributeValue(SHAPE_PROPERTY);
		path = jsfModel.getHelper().asIntArray(source, SHAPE_PROPERTY);
		linkStatus = getLinkStatus();

		if (path.length > 1 && path[0] == -1) {
			preferredMode = true;
			preferredLength = path[1];
			path = new int[] {};
		} else
			preferredMode = false;

		Segment prevSegment = null;
		for (int i = 0; i < path.length; i++) {
			Segment newSegment = new Segment(this, path[i], prevSegment);
			if (i == 0)
				headSegment = newSegment;

			if (i == path.length - 1)
				tailSegment = newSegment;
			prevSegment = newSegment;
		}
	}

	public void setTarget() {
		if (target == null) {
			return;
		}
		if (target.getPath() == null) {
			return;
		}
		toGroup = (IGroup) jsfModel.findElement(target.getPath());
		if (toGroup == null) {
			return;
		}
		if (toGroup != null) {
			((Group) toGroup).addInputLink(this);
			((Page) getParentJSFElement()).fireLinkAdd(this);
		}

	}

	public XModelObject getTargetModel() {
		return target;
	}

	public Menu getPopupMenu(Control control, Object environment) {
		if (getSource() == null)
			return null;
		return null;
	}

	public String getLinkName() {
		return getJSFModel().getHelper().getItemOutputPresentation(source);
	}

	public IGroup getToGroup() {
		return toGroup;
	}

	public IPage getFromPage() {
		return (IPage) getParentJSFElement();
	}

	public IGroup getFromGroup() {
		return getFromPage().getGroup();
	}

	public boolean isShortcut() {
		return jsfModel.getHelper().isShortcut(source);
	}

	public boolean isConfirmed() {
		if (((Group) getFromGroup()).type.equals("page")) { //$NON-NLS-1$
			String subtype = source.getAttributeValue(Link.SUBTYPE_PROPERTY);
			if ("confirmed".equals(subtype)) //$NON-NLS-1$
				return true;
			else
				return false;
		}
		return true;
	}

	public boolean isHidden() {
		String hidden = source.getAttributeValue(Link.HIDDEN_PROPERTY);
		if ("yes".equals(hidden)) //$NON-NLS-1$
			return true;
		else
			return false;
	}

	public void remove() {
	}

	public void addLinkListener(ILinkListener l) {
		linkListeners.add(l);
	}

	public void removeLinkListener(ILinkListener l) {
		linkListeners.remove(l);
	}

	public ISegment getHeadSegment() {
		return headSegment;
	}

	public ISegment getTailSegment() {
		return tailSegment;
	}

	public void setHeadSegment(ISegment segment) {
		ISegment oldHeadSegment = headSegment;
		headSegment = (ISegment) segment;
		propertyChangeSupport.firePropertyChange("headSegment", oldHeadSegment, //$NON-NLS-1$
				segment);
	}

	public void setTailSegment(ISegment segment) {
		ISegment oldTailSegment = tailSegment;
		tailSegment = (ISegment) segment;
		propertyChangeSupport.firePropertyChange("tailSegment", oldTailSegment, //$NON-NLS-1$
				segment);
	}

	public ISegment createSegment(int length, ISegment prevSegment) {
		ISegment newSegment = new Segment(this, length, prevSegment);
		return newSegment;
	}

	public void fireLinkChange() {
		List<ILinkListener> targets = new ArrayList<ILinkListener>();
		targets.addAll(linkListeners);
		for (int i = 0; i < targets.size(); i++) {
			ILinkListener listener = (ILinkListener) targets.get(i);
			if (listener != null)
				listener.linkChange(this);
		}
	}

	public void fireLinkRemove() {
		List<ILinkListener> targets = new ArrayList<ILinkListener>();
		targets.addAll(linkListeners);
		for (int i = 0; i < targets.size(); i++) {
			ILinkListener listener = (ILinkListener) targets.get(i);
			if (listener != null)
				listener.linkRemove(this);
		}
		((JSFModel) getJSFModel()).fireLinkRemove(this);
	}

	private String getLinkStatus() {
		return source.getAttributeValue(HIDDEN_PROPERTY) + ":" //$NON-NLS-1$
				+ source.getAttributeValue("shortcut"); //$NON-NLS-1$
	}

	public void nodeChanged(Object eventData) {
		int[] path = null;
		if (target == null) {
			target = getJSFModel().getHelper().getItemOutputTarget(source);
			if (target != null) {
				setTarget();
			}
		} else {
			if (!target.equals(getJSFModel().getHelper().getItemOutputTarget(
					source))) {
				((Group) toGroup).removeInputLink(this);
				target = getJSFModel().getHelper().getItemOutputTarget(source);
				setTarget();
				fireLinkChange();
				return;
			}
		}

		String temp, ls;
		ls = getLinkStatus();
		temp = source.getAttributeValue(SHAPE_PROPERTY);
		path = jsfModel.getHelper().asIntArray(source, SHAPE_PROPERTY);

		if (pathString.equals(temp)
				&& (linkStatus == null || linkStatus.equals(ls))) {
			fireLinkChange();
			return;
		}

		if (path.length > 1 && path[0] == -1) {
			preferredMode = true;
			preferredLength = path[1];
			path = new int[] {};
		} else
			preferredMode = false;

		Segment prevSegment = null;
		for (int i = 0; i < path.length; i++) {
			Segment newSegment = new Segment(this, path[i], prevSegment);
			if (i == 0)
				headSegment = newSegment;

			if (i == path.length - 1)
				tailSegment = newSegment;
			prevSegment = newSegment;
		}
		pathString = temp;
		linkStatus = ls;
		fireLinkChange();
	}

}

