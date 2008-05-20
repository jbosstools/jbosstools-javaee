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

import java.util.*;

import java.beans.*;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.jboss.tools.common.model.ui.action.XModelObjectActionList;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Control;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.meta.*;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;
import org.jboss.tools.struts.ui.StrutsUIPlugin;
import org.jboss.tools.struts.ui.editor.model.*;
//import org.jboss.tools.common.meta.action.*;
//import org.jboss.tools.common.model.util.*;



public class StrutsElement implements IStrutsElement{

   protected boolean jump=true;

   public boolean isJump() {
		return jump;
	}
	public void setJump(boolean flag) {
		jump = flag;
	}
	
   protected String name = "";
   protected boolean visible = false;
   protected boolean hidden = false;
   protected boolean deleted = false;
   protected boolean global = false;
   protected Dimension size;
   protected Point position;
   protected IStrutsElement parent;
   protected IStrutsModel strutsModel;

   protected String iconPath;

   protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
   protected VetoableChangeSupport vetoableChangeSupport = new VetoableChangeSupport(this);

   protected XModelObject source;

   protected Hashtable map = new Hashtable();
   
   private Rectangle forwardBounds = new Rectangle(0,0,0,0);

   public StrutsElement() {
   }

   public StrutsElement(IStrutsElement parent) {
      this.parent = parent;
      strutsModel = getStrutsModel();
   }

   public StrutsElement(IStrutsElement parent, XModelObject source) {
      this.parent = parent;
      this.source = source;
      strutsModel = getStrutsModel();
      ((StrutsModel)strutsModel).putToMap(source.getPath(),this);
   }

   public Object getSource() {
      return source;
   }

   public void setSource(Object obj) {
      source = (XModelObject)obj;
   }



   public String getText() {
      return "";
   }

   public IStrutsElement getRoot() {
      IStrutsElement current = this;
      while(current.getParentStrutsElement()!=null) {
         current = current.getParentStrutsElement();
      }
      return current;
   }

   public IStrutsModel getStrutsModel() {
      IStrutsElement model = getRoot();
      if(model instanceof IStrutsElement) {
         return (IStrutsModel)model;
      }
      return null;
   }

   public String getStrutsElementPath() {
      IStrutsElement current = this;
      String path = current.getName();
      while(current.getParentStrutsElement()!=null) {
         current = current.getParentStrutsElement();
         path = current.getName() + "/" + path;
      }
      return path;
   }

   public void updateModelModifiedProperty(Object oldValue, Object newValue) {
		if (getStrutsModel() != null) {
			try {
				if (oldValue == null || !oldValue.equals(newValue))
					getStrutsModel().setModified(true);
			} catch (Exception e) {
				StrutsUIPlugin.getPluginLog().logError(e);
				if (newValue != null)
					getStrutsModel().setModified(true);
			}
		}
	}

   public void updateModelModifiedProperty(int oldValue,int newValue) {
      if(getStrutsModel()!=null) {
         if(newValue!=oldValue);
            getStrutsModel().setModified(true);
      }
   }

   public void updateModelModifiedProperty(boolean oldValue,boolean newValue) {
      if(getStrutsModel()!=null) {
         if(newValue!=oldValue);
            getStrutsModel().setModified(true);
      }
   }

   public void fireModelElementChanged() {
//      if(getProcessModel()!=null)
//         ((ProcessModel)getProcessModel()).fireElementChanged(this);
   }

   public String getIconPath() {
      return iconPath;
   }

   public void setIconPath(String path) {
      String oldValue = iconPath;
      iconPath = path;
      propertyChangeSupport.firePropertyChange("iconPath",oldValue,iconPath);
      updateModelModifiedProperty(oldValue,path);
   }

   public IStrutsElement getParentStrutsElement() {
      return parent;
   }

   public void setParentStrutsElement(IStrutsElement element) {
      IStrutsElement oldValue = parent;
      parent = element;
      strutsModel = getStrutsModel();
      propertyChangeSupport.firePropertyChange("parent",oldValue,element);
      updateModelModifiedProperty(oldValue,element);
   }

   public String getName() {
      return source.getAttributeValue(NAME_PROPERTY);
   }

   public String getPath() {
      return StrutsProcessStructureHelper.instance.getModuleRelativePath(source); ///source.getAttributeValue(this.PATH_PROPERTY);
   }

   public String getType() {
      return source.getAttributeValue(TYPE_PROPERTY);
   }

   public String getTarget() {
      return source.getAttributeValue(TARGET_PROPERTY);
   }

   public void setName(String name) throws PropertyVetoException {
      String oldValue = this.name;
      vetoableChangeSupport.fireVetoableChange("name",oldValue,name);
      this.name = name;
      //updateModelModifiedProperty(oldValue,name);
      //fireModelElementChanged();
   }

   public Dimension getSize() {
      int[] bounds = strutsModel.getHelper().asIntArray(source,BOUNDS_PROPERTY);
      if(bounds.length<4) {
         return new Dimension(0,0);
      } else {
         return new Dimension(bounds[2],bounds[3]);
      }
   }

   public void setSize(Dimension size)  {
      Dimension oldValue = this.size;
      this.size = size;
      updateSourceProperty("shape",getPosition(),size);
      propertyChangeSupport.firePropertyChange("shape",oldValue,size);
      updateModelModifiedProperty(oldValue,size);
   }

   public Point getPosition() {
      int[] bounds = strutsModel.getHelper().asIntArray(source,BOUNDS_PROPERTY);
      if (bounds.length<2) {
         return DEFAULT_POINT;
      } else {
         if(bounds[0] == 0 && bounds[1] == 0) return DEFAULT_POINT;
         else return new Point(bounds[0],bounds[1]);
      }
   }

   public void setPosition(Point point) {
      Point oldValue = position!=null?new Point(position.x, position.y):null;
      position = point;
      updateSourceProperty("shape",point,getSize());
      propertyChangeSupport.firePropertyChange("shape",oldValue,point);
      updateModelModifiedProperty(oldValue,point);
   }


   public Rectangle getBounds() {
   		if(this instanceof Forward){
   			return forwardBounds;
   		}else{
   			int[] bounds = strutsModel.getHelper().asIntArray(source,BOUNDS_PROPERTY);
   			if(bounds.length<4) {
   				return new Rectangle(0,0,0,0);
   			} else {
   				return new Rectangle(bounds[0],bounds[1],bounds[2],bounds[3]);
   			}
   		}
   }

   public void setBounds(Rectangle rec) {
   	  if(this instanceof Forward){
   	  	forwardBounds = rec;
   	  }else{
   	  	Rectangle oldBounds = getBounds();
   	  	updateSourceProperty(
   	  			"shape",
				new Point(rec.x,rec.y),
				new Dimension(rec.width,rec.height)
   	  	);
   	  	propertyChangeSupport.firePropertyChange(
   	  			"shape",
				oldBounds,
				new Rectangle(rec.x,rec.y,
						rec.width,rec.height)
   	  	);
   	  }
      //updateModelModifiedProperty(oldBounds,rec);
   }

   public void setBounds(int x,int y,int w,int h) {
      setBounds(new Rectangle(x,y,w,h));
   }


   public void setSourceProperty(String name,Object value) {
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
      XModelObject mobject = (XModelObject)source;
      XAttribute[] attributes = mobject.getModelEntity().getAttributes();
      String[] attributeNames = new String[attributes.length];
      for(int i=0;i<attributeNames.length;i++) {
         attributeNames[i] = attributes[i].getName();
      }
      return attributeNames;
   }

   public String[] getSourcePropertyDisplayNames() {
      XModelObject mobject = (XModelObject)source;
      XAttribute[] attributes = mobject.getModelEntity().getAttributes();
      String[] attributeNames = new String[attributes.length];
      for(int i=0;i<attributeNames.length;i++) {
         attributeNames[i] = attributes[i].getName();
      }
      return attributeNames;
   }

   public void remove(){
   }

   // Support for vetoable change

   public void addVetoableChangeListener(VetoableChangeListener l) {
      vetoableChangeSupport.addVetoableChangeListener(l);
   }

   public void removeVetoableChangeListener(VetoableChangeListener l) {
      vetoableChangeSupport.removeVetoableChangeListener(l);
   }

   public void addVetoableChangeListener(String propertyName,VetoableChangeListener l) {
      vetoableChangeSupport.addVetoableChangeListener(propertyName,l);
   }

   public void removeVetoableChangeListener(String propertyName,VetoableChangeListener l) {
      vetoableChangeSupport.removeVetoableChangeListener(propertyName,l);
   }

   //Support for unvetoable change

   public void addPropertyChangeListener(PropertyChangeListener l) {
      propertyChangeSupport.addPropertyChangeListener(l);
   }

   public void removePropertyChangeListener(PropertyChangeListener l) {
      propertyChangeSupport.removePropertyChangeListener(l);
   }

   public void addPropertyChangeListener(String propertyName,PropertyChangeListener l) {
      propertyChangeSupport.addPropertyChangeListener(propertyName,l);
   }

   public void removePropertyChangeListener(String propertyName,PropertyChangeListener l) {
      propertyChangeSupport.removePropertyChangeListener(propertyName,l);
   }

   public Object clone() {
      StrutsElement newElement = new StrutsElement();
      newElement.source = source.copy();
      return newElement;
   }

   public Enumeration children() {
      return null;
   }

   public boolean isLeaf() {
      return true;
   }


   /*public TreeNode getChildAt(int childIndex) {
      return null;
   }

   public int getChildCount() {
      return -1;
   }

   public TreeNode getParent() {
      return (TreeNode)getParentStrutsElement();
   }

   public int getIndex(TreeNode node) {
      return -1;
   }

   public boolean getAllowsChildren() {
      return true;
   }*/

   public void removeAllListeners() {
      this.propertyChangeSupport = new PropertyChangeSupport(this);
   }

   /*protected Link getGlobalLink(){
      return null;
   }*/

   void updateSourceProperty(String name, Point position, Dimension size){
      /*String value = new String(""+position.x + "," + position.y + "," + size.width + "," + size.height);
      if(global && getGlobalLink() != null){
         Link link = getGlobalLink();
         if(link.getHeadSegment() != null) value = value+","+link.getPathText();
      }
      if(source!=null)
        strutsModel.getHelper().setAttributeValue(source,name, value);*/
      if(source!=null)
        strutsModel.getHelper().setAttributeValue(source,name, ""+position.x + "," + position.y + "," + size.width + "," + size.height);
   }

   public void structureChanged(Object eventData) {

   }

   public void nodeChanged(Object eventData) {

   }

   public void nodeAdded(Object eventData) {

   }

   public void nodeRemoved(Object eventData) {

   }

   public StrutsModel.StrutsHashtable getMap() {
      return ((StrutsModel)getStrutsModel()).getMap();
   }

   public void removeFromMap(Object key) {
      ((StrutsModel)getStrutsModel()).removeFromMap(key);
   }

   public IStrutsElement getFromMap(Object key) {
      return ((StrutsModel)getStrutsModel()).getFromMap(key);
   }
   
   public Menu getPopupMenu(Control control, Object environment) {
	if(getSource() == null) return null;
	if(((XModelObject)getSource()).getModelEntity().getActionList().getActionItems().length!=0){
	  XModelObjectActionList l = new XModelObjectActionList(((XModelObject)getSource()).getModelEntity().getActionList(), ((XModelObject)getSource()), null, environment);
				
	  Menu menu = l.createMenu(control);
	  return menu;
	}
	return null;
   }

   public Menu getPopupMenu(Control control) {
	  return getPopupMenu(control, null);
   }

   /*public XModelObjectActionList getSourceActionList(Object environment) {
      if(source.getModelEntity().getActionList().getActionItems().length!=0)
	    return new XModelObjectActionList(((XModelObject)parent.getSource()).getModelEntity().getActionList(), (XModelObject)parent.getSource(), null, environment);
	    
      return null;
   }

   public XModelObjectActionList getSourceActionList() {
      return getSourceActionList(null);
   }*/

   /*public AbstractAction getAction(String name,Object environment) {
      XAction action = ModelUtilities.getXAction(source,name);
      if(action!=null)
         return new XModelObjectAction(action,source,environment);
      return null;
   }

   public AbstractAction getAction(String name) {
      XAction action = ModelUtilities.getXAction(source,name);
      if(action!=null)
         return new XModelObjectAction(action,source,null);
      return null;
   }*/

   public boolean isConfirmed(){
      return false;
   }
}
