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
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jsf.ui.editor.model.IPage;
import org.jboss.tools.jsf.ui.editor.model.IJSFElement;
import org.jboss.tools.jsf.ui.editor.model.IJSFElementList;
import org.jboss.tools.jsf.ui.editor.model.IJSFElementListListener;

public class JSFElementList extends JSFElement implements IJSFElementList, VetoableChangeListener{
	List<IJSFElementListListener> listeners = new Vector<IJSFElementListListener>();
	List<Object> elements = new Vector<Object>();
	boolean elementListListenerEnable = true;
	boolean allowDuplicate = false;

	public JSFElementList() {
	}

	public JSFElementList(IJSFElement parent) {
		super(parent);
	}

	public JSFElementList(IJSFElement parent, XModelObject source) {
		super(parent, source);
	}

	public List getElements() {
		return elements;
	}

	public JSFElementList(List<Object> vector) {
		elements = vector;
	}

	public void dispose() {
		super.dispose();
		if (listeners != null)
			listeners.clear();
		listeners = null;
		if (elements != null)
			elements.clear();
		elements = null;
	}

	public void setAllowDuplicate(boolean set) {
		allowDuplicate = set;
	}

	public boolean isAllowDuplicate() {
		return allowDuplicate;
	}

	public void moveTo(Object object, int index) {
		int currentIndex = indexOf(object);
		if (index < 0 || index >= size())
			return;
		if (currentIndex > index) { // move down
			for (int i = currentIndex - 1; i >= index; i--) {
				Object elementAt = get(i);
				set(i + 1, elementAt);
			}
			set(index, object);
			this.fireElementMoved((IJSFElement) object, index, currentIndex);
		} else if (currentIndex < index) { // move up
			for (int i = currentIndex + 1; i <= index; i++) {
				Object elementAt = get(i);
				set(i - 1, elementAt);
			}
			set(index, object);
			this.fireElementMoved((IJSFElement) object, index, currentIndex);
		}
	}

	public void moveUp(Object object) {
		int currentIndex = indexOf(object);
		if (currentIndex == 0)
			return;
		set(currentIndex, get(currentIndex - 1));
		set(currentIndex - 1, object);
		this.fireElementMoved((IJSFElement) object, currentIndex - 1,
				currentIndex);
	}

	public void moveDown(Object object) {
		int currentIndex = indexOf(object);
		if (currentIndex == size())
			return;
		set(currentIndex, get(currentIndex + 1));
		set(currentIndex + 1, object);
		this.fireElementMoved((IJSFElement) object, currentIndex + 1,
				currentIndex);
	}

	public int size() {
		return elements.size();
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}

	public boolean contains(Object o) {
		return elements.contains(o);
	}

	public Iterator iterator() {
		return elements.iterator();
	}

	public Object[] toArray() {
		return elements.toArray();
	}

	public Object[] toArray(Object a[]) {
		return elements.toArray(a);
	}

	public boolean add(Object o) {
		boolean result = (o instanceof IJSFElement) && elements.add(o);
		return result;
	}

	public void add(IJSFElementList list) {
		for (int i = 0; i < list.size(); i++) {
			Object o = list.get(i);
			if (o instanceof JSFElement) {
				add(o);
			}
		}

	}

	public boolean remove(Object o) {
		boolean result = elements.remove(o);
		return result;
	}

	public void remove(Comparator comp) {
		for (int i = size() - 1; i >= 0; i--) {
			if (comp.equals(get(i))) {
				remove(get(i));
			}
			;
		}
	}

	public void removeAll() {
		for (int i = size() - 1; i >= 0; i--) {
			remove(get(i));
		}
	}

	public Object get(int index) {
		return elements.get(index);
	}

	public Object get(String name) {
		if (name == null)
			return null;
		for (int i = 0; i < elements.size(); i++) {
			JSFElement element = (JSFElement) elements.get(i);
			if (name.equals(element.getPath()))
				return element;
		}
		return null;
	}

	public Object set(int index, Object element) {
		Object newElement = elements.set(index, element);
		return newElement;
	}

	public void add(int index, Object element) {
		elements.add(index, element);
	}

	public int indexOf(Object o) {
		return elements.indexOf(o);
	}

	public IJSFElement findElement(Comparator comparator) {
		return null;
	}

	public IJSFElementList findElements(Comparator comparator) {
		return null;
	}

	public String getText() {
		return toString();
	}

	public Object clone() {
		List<Object> copy = new Vector<Object>();
		copy.addAll(elements);
		JSFElementList clone = new JSFElementList(copy);
		return clone;
	}

	public JSFElementList getClone() {
		JSFElementList list = (JSFElementList) clone();
		return list;
	}

	public void vetoableChange(PropertyChangeEvent evt)
			throws PropertyVetoException {
	}

	public void addJSFElementListListener(IJSFElementListListener l) {
		listeners.add(l);
	}

	public void removeJSFElementListListener(IJSFElementListListener l) {
		listeners.remove(l);
	}

	protected void fireElementMoved(IJSFElement element, int newIndex,
			int oldIndex) {
		for (int i = 0; i < listeners.size(); i++) {
			IJSFElementListListener listener = (IJSFElementListListener) listeners
					.get(i);
			if (listener != null && listener.isElementListListenerEnable())
				listener.listElementMove(this, element, newIndex, oldIndex);
		}
		((JSFModel) getJSFModel()).fireElementRemoved(element, oldIndex);
		((JSFModel) getJSFModel()).fireElementInserted(element);
	}

	protected void fireElementAdded(IJSFElement element, int index) {
		for (int i = 0; i < listeners.size(); i++) {
			IJSFElementListListener listener = (IJSFElementListListener) listeners
					.get(i);
			if (listener != null && listener.isElementListListenerEnable())
				listener.listElementAdd(this, element, index);
		}
	}

	protected void fireElementRemoved(IJSFElement element, int index) {
		for (int i = 0; i < listeners.size(); i++) {
			IJSFElementListListener listener = (IJSFElementListListener) listeners
					.get(i);
			if (listener != null && listener.isElementListListenerEnable())
				listener.listElementRemove(this, element, index);
		}
	}

	protected void fireElementChanged(IJSFElement element, int index,
			PropertyChangeEvent event) {
		for (int i = 0; i < listeners.size(); i++) {
			IJSFElementListListener listener = (IJSFElementListListener) listeners
					.get(i);
			if (listener != null && listener.isElementListListenerEnable())
				listener.listElementChange(this, element, index, event);
		}
	}

	public void remove(int index) {
		Object obj = elements.get(index);
		elements.remove(obj);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}

class Comp implements Comparator {
	String name;

	public Comp(String name) {
		this.name = name;
	}

	public int compare(Object o1, Object o2) {
		return 0;
	}

	public boolean equals(Object obj) {
		if (obj instanceof IJSFElement) {
			IJSFElement element = (IJSFElement) obj;
			return element.getName().equals(name);
		}
		return false;
	}

}

class MessageNameComparator implements Comparator {
	String message;

	public MessageNameComparator(String message) {
		this.message = message;
	}

	public int compare(Object obj1, Object obj2) {
		return 0;
	}

	public boolean equals(Object obj) {
		if (obj instanceof IPage) {
			IPage message = (IPage) obj;
			return message.getName().equals(this.message);
		}
		return false;
	}

}

class ForwardComparator implements Comparator {
	Page message;

	public ForwardComparator(Page message) {
		this.message = message;
	}

	public int compare(Object obj1, Object obj2) {
		return 0;
	}

	public boolean equals(Object obj) {
		if (obj instanceof IPage) {
			IPage message = (IPage) obj;
			return message.getName().equals(this.message.getName())
					&& message.getParentJSFElement().getName().equals(
							this.message.getParentJSFElement().getName());
		}
		return false;
	}

}

class TransitionComparator implements Comparator {
	IPage messageFrom;

	IPage messageTo;

	public TransitionComparator(IPage messageFrom, IPage messageTo) {
		this.messageFrom = messageFrom;
		this.messageTo = messageTo;
	}

	public boolean equals(Object transition) {
		return false;
	}

	public int compare(Object obj1, Object obj2) {
		return 0;
	}

}

class ElementNameComparator implements Comparator {
	String elementName;

	public ElementNameComparator(String elementName) {
		this.elementName = elementName;
	}

	public void setName(String name) {
		elementName = name;
	}

	public boolean equals(Object object) {
		if (object instanceof JSFElement) {
			JSFElement unit = (JSFElement) object;
			return unit.getName().equals(elementName);
		}
		return false;
	}

	public int compare(Object obj1, Object obj2) {
		return 0;
	}

}

class TransitionForwardComparator implements Comparator {
	IPage message;

	public TransitionForwardComparator(IPage message) {
		this.message = message;
	}

	public int compare(Object obj1, Object obj2) {
		return 0;
	}

	public boolean equals(Object obj) {
		return false;
	}

}
