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
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.jboss.tools.common.model.ui.action.*;

import java.beans.*;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Control;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.meta.*;
import org.jboss.tools.jsf.ui.editor.model.IJSFElement;
import org.jboss.tools.jsf.ui.editor.model.IJSFModel;

public class JSFElement implements IJSFElement{

	protected String name = ""; //$NON-NLS-1$
	protected boolean visible = false;
	protected boolean hidden = false;
	protected boolean deleted = false;
	protected boolean global = false;
	protected Dimension size;
	protected Point position;
	protected IJSFElement parent;
	protected IJSFModel jsfModel;
	protected String iconPath;

	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	protected VetoableChangeSupport vetoableChangeSupport = new VetoableChangeSupport(
			this);

	protected XModelObject source;

	protected Hashtable map = new Hashtable();

	public JSFElement() {
	}

	public JSFElement(IJSFElement parent) {
		this.parent = parent;
		jsfModel = getJSFModel();
	}

	public JSFElement(IJSFElement parent, XModelObject source) {
		this.parent = parent;
		this.source = source;
		jsfModel = getJSFModel();
		((JSFModel) jsfModel).putToMap(source.getPath(), this);
	}

	public void dispose() {
		vetoableChangeSupport = null;
		propertyChangeSupport = null;
		if (map != null)
			map.clear();
		map = null;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object obj) {
		source = (XModelObject) obj;
	}

	public String getText() {
		return ""; //$NON-NLS-1$
	}

	public IJSFElement getRoot() {
		IJSFElement current = this;
		while (current.getParentJSFElement() != null) {
			current = current.getParentJSFElement();
		}
		return current;
	}

	public IJSFModel getJSFModel() {
		IJSFElement model = getRoot();
		if (model instanceof IJSFElement) {
			return (IJSFModel) model;
		}
		return null;
	}

	public String getJSFElementPath() {
		IJSFElement current = this;
		String path = current.getName();
		while (current.getParentJSFElement() != null) {
			current = current.getParentJSFElement();
			path = current.getName() + "/" + path; //$NON-NLS-1$
		}
		return path;
	}

	public void updateModelModifiedProperty(Object oldValue, Object newValue) {
		if (getJSFModel() != null) {
			if (oldValue == null || !oldValue.equals(newValue))
				getJSFModel().setModified(true);
		}
	}

	public void updateModelModifiedProperty(int oldValue, int newValue) {
		if (getJSFModel() != null) {
			if (newValue != oldValue)
				;
			getJSFModel().setModified(true);
		}
	}

	public void updateModelModifiedProperty(boolean oldValue, boolean newValue) {
		if (getJSFModel() != null) {
			if (newValue != oldValue)
				;
			getJSFModel().setModified(true);
		}
	}

	public void fireModelElementChanged() {
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String path) {
		String oldValue = iconPath;
		iconPath = path;
		propertyChangeSupport
				.firePropertyChange("iconPath", oldValue, iconPath); //$NON-NLS-1$
		updateModelModifiedProperty(oldValue, path);
	}

	public IJSFElement getParentJSFElement() {
		return parent;
	}

	public void setParentJSFElement(IJSFElement element) {
		IJSFElement oldValue = parent;
		parent = element;
		jsfModel = getJSFModel();
		propertyChangeSupport.firePropertyChange("parent", oldValue, element); //$NON-NLS-1$
		updateModelModifiedProperty(oldValue, element);
	}

	public String getName() {
		return source.getAttributeValue(NAME_PROPERTY);
	}

	public String getPath() {
		return this.getJSFModel().getHelper().getPath(source);
	}

	public String getType() {
		return source.getAttributeValue(TYPE_PROPERTY);
	}

	public String getTarget() {
		return source.getAttributeValue(TARGET_PROPERTY);
	}

	public void setName(String name) throws PropertyVetoException {
		String oldValue = this.name;
		vetoableChangeSupport.fireVetoableChange("name", oldValue, name); //$NON-NLS-1$
		this.name = name;
	}

	public Dimension getSize() {
		int[] bounds = jsfModel.getHelper().asIntArray(source, BOUNDS_PROPERTY);
		if (bounds.length < 4) {
			return new Dimension(0, 0);
		} else {
			return new Dimension(bounds[2], bounds[3]);
		}
	}

	public void setSize(Dimension size) {
		Dimension oldValue = this.size;
		this.size = size;
		updateSourceProperty("shape", getPosition(), size); //$NON-NLS-1$
		propertyChangeSupport.firePropertyChange("shape", oldValue, size); //$NON-NLS-1$
		updateModelModifiedProperty(oldValue, size);
	}

	public Point getPosition() {
		int[] bounds = jsfModel.getHelper().asIntArray(source, BOUNDS_PROPERTY);
		if (bounds.length < 2) {
			return DEFAULT_POINT;
		} else {
			if (bounds[0] == 0 && bounds[1] == 0)
				return DEFAULT_POINT;
			else
				return new Point(bounds[0], bounds[1]);
		}
	}

	public void setPosition(Point point) {
		Point oldValue = position != null ? new Point(position.x, position.y)
				: null;
		position = point;
		updateSourceProperty("shape", point, getSize()); //$NON-NLS-1$
		propertyChangeSupport.firePropertyChange("shape", oldValue, point); //$NON-NLS-1$
		updateModelModifiedProperty(oldValue, point);
	}

	public Rectangle getBounds() {
		int[] bounds = jsfModel.getHelper().asIntArray(source, BOUNDS_PROPERTY);
		if (bounds.length < 4) {
			return new Rectangle(0, 0, 0, 0);
		} else {
			return new Rectangle(bounds[0], bounds[1], bounds[2], bounds[3]);
		}
	}

	public void setBounds(Rectangle rec) {
		Rectangle oldBounds = getBounds();
		updateSourceProperty("shape", new Point(rec.x, rec.y), new Dimension( //$NON-NLS-1$
				rec.width, rec.height));
		propertyChangeSupport.firePropertyChange("shape", oldBounds, //$NON-NLS-1$
				new Rectangle(rec.x, rec.y, rec.width, rec.height));
	}

	public void setBounds(int x, int y, int w, int h) {
		setBounds(new Rectangle(x, y, w, h));
	}

	public void setSourceProperty(String name, Object value) {
	}

	public Object getSourceProperty(String name) {
		return source.getAttributeValue(name);
	}

	public Object getSourceProperty(int index) {
		return null;
	}

	public int getSourcePropertyCounter() {
		return 0;
	}

	public String[] getSourcePropertyNames() {
		XModelObject mobject = (XModelObject) source;
		XAttribute[] attributes = mobject.getModelEntity().getAttributes();
		String[] attributeNames = new String[attributes.length];
		for (int i = 0; i < attributeNames.length; i++) {
			attributeNames[i] = attributes[i].getName();
		}
		return attributeNames;
	}

	public String[] getSourcePropertyDisplayNames() {
		XModelObject mobject = (XModelObject) source;
		XAttribute[] attributes = mobject.getModelEntity().getAttributes();
		String[] attributeNames = new String[attributes.length];
		for (int i = 0; i < attributeNames.length; i++) {
			attributeNames[i] = attributes[i].getName();
		}
		return attributeNames;
	}

	public void remove() {
	}

	// Support for vetoable change

	public void addVetoableChangeListener(VetoableChangeListener l) {
		vetoableChangeSupport.addVetoableChangeListener(l);
	}

	public void removeVetoableChangeListener(VetoableChangeListener l) {
		vetoableChangeSupport.removeVetoableChangeListener(l);
	}

	public void addVetoableChangeListener(String propertyName,
			VetoableChangeListener l) {
		vetoableChangeSupport.addVetoableChangeListener(propertyName, l);
	}

	public void removeVetoableChangeListener(String propertyName,
			VetoableChangeListener l) {
		vetoableChangeSupport.removeVetoableChangeListener(propertyName, l);
	}

	//Support for unvetoable change

	public void addPropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.removePropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener l) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, l);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener l) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, l);
	}

	public Object clone() {
		JSFElement newElement = new JSFElement();
		newElement.source = source.copy();
		return newElement;
	}

	public Enumeration children() {
		return null;
	}

	public boolean isLeaf() {
		return true;
	}

	public void removeAllListeners() {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}

	void updateSourceProperty(String name, Point position, Dimension size) {
		if (source != null)
			jsfModel.getHelper().setAttributeValue(
					source,
					name,
					"" + position.x + "," + position.y + "," + size.width + "," //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							+ size.height);
	}

	public void structureChanged(Object eventData) {
	}

	public void nodeChanged(Object eventData) {
	}

	public void nodeAdded(Object eventData) {
	}

	public void nodeRemoved(Object eventData) {
	}

	public JSFModel.JSFHashtable getMap() {
		return ((JSFModel) getJSFModel()).getMap();
	}

	public void removeFromMap(Object key) {
		((JSFModel) getJSFModel()).removeFromMap(key);
	}

	public IJSFElement getFromMap(Object key) {
		return ((JSFModel) getJSFModel()).getFromMap(key);
	}

	public Menu getPopupMenu(Control control, Object environment) {
		if (getSource() == null)
			return null;
		if (((XModelObject) getSource()).getModelEntity().getActionList()
				.getActionItems().length != 0) {
			XModelObjectActionList l = new XModelObjectActionList(
					((XModelObject) getSource()).getModelEntity()
							.getActionList(), ((XModelObject) getSource()),
					null, environment);

			Menu menu = l.createMenu(control);
			return menu;
		}
		return null;
	}

	public Menu getPopupMenu(Control control) {
		return getPopupMenu(control, null);
	}

	public boolean isConfirmed() {
		return false;
	}

}
