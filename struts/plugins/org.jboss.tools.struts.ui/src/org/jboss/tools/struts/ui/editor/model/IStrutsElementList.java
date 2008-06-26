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
package org.jboss.tools.struts.ui.editor.model;

//import java.util.List;
import java.util.Comparator;
import java.util.*;

public interface IStrutsElementList extends IStrutsElement {
	public Vector getElements();
	
   public void moveTo(IStrutsElement object, int index);
   public void moveUp(IStrutsElement object);
   public void moveDown(IStrutsElement object);

   public IStrutsElement findElement(Comparator comparator);
   public IStrutsElementList findElements(Comparator comparator);

   public int size();
   public boolean isEmpty();
   public boolean contains(Object o);
   public Iterator iterator();
   public Object[] toArray();
   public Object[] toArray(Object a[]);
   public boolean add(IStrutsElement o);
   public void add(IStrutsElementList list);
   public void add(int index, IStrutsElement element);
   public boolean remove(IStrutsElement o);
   public void remove(Comparator comparator);
//   public Object remove(int index) throws VetoException;
   public void removeAll();
   public IStrutsElement get(int index);
   public int indexOf(Object o);
   public void addStrutsElementListListener(IStrutsElementListListener l);
   public void removeStrutsElementListListener(IStrutsElementListListener l);
}

