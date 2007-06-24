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

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;


//import javax.swing.*;
//import javax.swing.tree.*;

public interface IStrutsElement{
	public void setJump(boolean flag);
	public boolean isJump();
	
   public static String NAME_PROPERTY = "title";
   public static String BOUNDS_PROPERTY = "shape";
   public static String PATH_PROPERTY = "path";
   public static String TARGET_PROPERTY = "target";
   public static String TYPE_PROPERTY = "type";
   public static String SUBTYPE_PROPERTY = "subtype";

   public static Point DEFAULT_POINT = new Point(50,50);

   public IStrutsElement getRoot();
   public IStrutsModel getStrutsModel();

   public Object getSource();
   public void setSource(Object object);

   public IStrutsElement getParentStrutsElement();
   public void setParentStrutsElement(IStrutsElement parent);

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
   public String getStrutsElementPath();

   public String getIconPath();
   public void setIconPath(String path);

   public void setSourceProperty(String name, Object value);

   public Object getSourceProperty(String name);
   public Object getSourceProperty(int index);
   public int getSourcePropertyCounter();
   public String[] getSourcePropertyNames();
   public String[] getSourcePropertyDisplayNames();

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
