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
package org.jboss.tools.jsf.ui.editor.model;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Control;

import java.beans.*;

public interface IJSFElement{
   public static String NAME_PROPERTY = "title"; //$NON-NLS-1$
   public static String BOUNDS_PROPERTY = "shape"; //$NON-NLS-1$
   public static String PATH_PROPERTY = "path"; //$NON-NLS-1$
   public static String TARGET_PROPERTY = "target"; //$NON-NLS-1$
   public static String TYPE_PROPERTY = "type"; //$NON-NLS-1$
   public static String SUBTYPE_PROPERTY = "subtype"; //$NON-NLS-1$

   public static Point DEFAULT_POINT = new Point(50,50);

   public IJSFElement getRoot();
   public IJSFModel getJSFModel();

   public Object getSource();
   public void setSource(Object object);

   public IJSFElement getParentJSFElement();
   public void setParentJSFElement(IJSFElement parent);

   public String getName();
   public String getType();
   public void setName(String name) throws PropertyVetoException ;

   public Dimension getSize();
   public void setSize(Dimension size);

   public Point getPosition();
   public void setPosition(Point point);

   public Rectangle getBounds();
   public void setBounds(Rectangle rec);
   public void setBounds(int x, int y, int w, int h);

   public void remove();
   public String getJSFElementPath();

   public String getIconPath();
   public void setIconPath(String path);

   public void setSourceProperty(String name, Object value);

   public Object getSourceProperty(String name);
   public Object getSourceProperty(int index);
   public int getSourcePropertyCounter();
   public String[] getSourcePropertyNames();
   public String[] getSourcePropertyDisplayNames();
   public Menu getPopupMenu(Control control, Object environment);
   public Menu getPopupMenu(Control control);

   public void addPropertyChangeListener(PropertyChangeListener l);
   public void removePropertyChangeListener(PropertyChangeListener l);
   public void addPropertyChangeListener(String propertyName, PropertyChangeListener l);
   public void removePropertyChangeListener(String propertyName, PropertyChangeListener l);

   public String getText();
   public Object clone();

   public void structureChanged(Object eventData);
   public void nodeChanged(Object eventData);
   public void nodeAdded(Object eventData);
   public void nodeRemoved(Object eventData);

   public boolean isConfirmed();
}
