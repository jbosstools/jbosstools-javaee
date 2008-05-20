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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import java.util.*;

import org.eclipse.swt.widgets.*;

import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.model.helpers.StrutsBreakpointManager;
import org.jboss.tools.struts.ui.editor.model.*;
import org.jboss.tools.common.model.ui.action.*;

public class Link extends StrutsElement implements ILink {

   public static final String PATH_PROPERTY = "link shape";
   public static final String SHAPE_PROPERTY = "shape";
   public static final String HIDDEN_PROPERTY = "hidden";

   private boolean global = false;
   private String pathString;
   private String linkStatus;
   
   private boolean preferredMode=false;
   private int preferredLength = 0;

   Vector linkListeners = new Vector();

   XModelObject target;
   //XModelObject parentCopy;
   IProcessItem toProcessItem;

   ISegment headSegment;
   ISegment tailSegment;
   
   public boolean isPreferredMode(){
   	 return preferredMode;
   }
   
   public int getPreferredLength(){
   	 if(preferredMode) return preferredLength;
   	 else return 0;
   }

   public void clearPreferred(){
	 preferredMode = false;
   }
   

   public void update(){
      pathString = getPathText();
      if(global)strutsModel.getHelper().setAttributeValue(source, Link.PATH_PROPERTY, getPathText());
      else strutsModel.getHelper().setAttributeValue(source, Link.SHAPE_PROPERTY, getPathText());
   }

   public String getPathFromModel(){
   	  if(preferredMode) return "";
   	  else{
        if(global) return source.getAttributeValue(Link.PATH_PROPERTY);
        else return source.getAttributeValue(Link.SHAPE_PROPERTY);
   	  }
   }

   public String getPathText() {
      String text = "";
      if(headSegment!=null) {
         ISegment currentSegment = headSegment;
         text+=currentSegment.getLength();
         while(currentSegment.getNext()!=null) {
            text += "," + currentSegment.getNext().getLength();
            currentSegment = (ISegment)currentSegment.getNext();
         }
      }
      return text;
   }
   public HeadBreakPoint headBP;
   public TailBreakPoint tailBP;
   
   public IBreakPoint getHeadBreakPoint(){
   	return headBP;
   }
   
   public IBreakPoint getTailBreakPoint(){
   	return tailBP;
   }

   public Link(IStrutsElement parent, XModelObject target) {
      super(parent);
      this.target = target;
      headBP = new HeadBreakPoint();
      tailBP = new TailBreakPoint();

      int[] path = null;

      source = (XModelObject)parent.getSource();

      if(source.getParent() == strutsModel.getSource()) global = true;
      else global = false;

      if(global){
        pathString = source.getAttributeValue(PATH_PROPERTY);
        path = strutsModel.getHelper().asIntArray(source, PATH_PROPERTY);
      }else{
        pathString = source.getAttributeValue(SHAPE_PROPERTY);
        path = strutsModel.getHelper().asIntArray(source, SHAPE_PROPERTY);
      }
      linkStatus = getLinkStatus();
      
      if(path.length > 1 && path[0] == -1){
		preferredMode = true;
		preferredLength = path[1];
		path = new int[]{};
      }else preferredMode = false;

      Segment prevSegment = null;
      for(int i=0;i<path.length;i++) {
         Segment newSegment = new Segment(this,path[i],prevSegment);
         if(i==0)
            headSegment = newSegment;

         if(i==path.length-1)
            tailSegment = newSegment;
         prevSegment = newSegment;
      }

   }

   public void setTarget(){
      if (target.getPath() == null) return;
      toProcessItem = (IProcessItem)strutsModel.findElement(target.getPath());
      ((ProcessItem)toProcessItem).addInputLink(this);
   }

   public XModelObject getTargetModel(){
      return target;
   }

   public Menu getPopupMenu(Control control, Object environment) {
	if(getSource() == null) return null;
	if(strutsModel.getHelper().getLinkActionList((XModelObject)parent.getSource()).getActionItems().length!=0){
	  XModelObjectActionList l = new XModelObjectActionList(strutsModel.getHelper().getLinkActionList((XModelObject)parent.getSource()), ((XModelObject)parent.getSource()), null, environment);
				
	  Menu menu = l.createMenu(control);
	  return menu;
	}
	return null;
   }

   public String getToProcessItemName() {
      return toProcessItem.getPath();
   }

   public IProcessItem getToProcessItem() {
      //toProcessItem = getStrutsModel().getProcessItem((String)getSourceProperty("toProcessItem"));
      return toProcessItem;
   }

   public IForward getFromForward() {
      return (IForward)getParentStrutsElement();
   }

   public IProcessItem getFromProcessItem() {
      return getFromForward().getProcessItem();
   }

   public boolean isShortcut() {
     //if(((ProcessItem)getFromProcessItem()).type.equals(StrutsConstants.TYPE_PAGE)){
        boolean flag = strutsModel.getHelper().isShortcut(source);
        return flag;
     //}
     //return false;
   }

   public boolean isConfirmed(){
      if(((ProcessItem)getFromProcessItem()).type.equals(StrutsConstants.TYPE_PAGE)){
         String subtype = source.getAttributeValue(Link.SUBTYPE_PROPERTY);
         if("confirmed".equals(subtype)) return true;
         else return false;
      }
      return true;
   }

   public boolean isHidden(){
      String hidden = source.getAttributeValue(Link.HIDDEN_PROPERTY);
      if("yes".equals(hidden)) return true;
      else return false;
   }

   public void remove() {
      if(toProcessItem != null)((ProcessItem)toProcessItem).removeInputLink(this);
      if(((ProcessItem)getFromProcessItem()).type.equals(StrutsConstants.TYPE_PAGE)){
         if(!((Forward)parent).isRelink())((ProcessItem)getFromProcessItem()).forwardList.removeForward(getFromForward());
      }else ((ProcessItem)getFromProcessItem()).forwardList.removeForward(getFromForward());
      fireLinkRemove();
      ((ProcessItem)getFromProcessItem()).fireLinkRemove(this);
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
      headSegment = (ISegment)segment;
      //Debug.println("Head segment setted = " + segment);
      propertyChangeSupport.firePropertyChange("headSegment",oldHeadSegment,segment);
   }

   public void setTailSegment(ISegment segment) {
      ISegment oldTailSegment = tailSegment;
      tailSegment = (ISegment)segment;
      //Debug.println("Tail segment setted = " + segment + " = " + getText());
      propertyChangeSupport.firePropertyChange("tailSegment", oldTailSegment,segment);
   }

   public ISegment createSegment(int length, ISegment prevSegment) {
      ISegment newSegment = new Segment(this, length, prevSegment);
      return newSegment;
   }


   public void fireLinkChange() {
      Vector targets = (Vector)this.linkListeners.clone();
      for(int i=0;i<targets.size();i++) {
         ILinkListener listener = (ILinkListener)targets.get(i);
         if(listener!=null)
            listener.linkChange(this);
      }
   }

   public void fireLinkRemove() {
      Vector targets = (Vector)this.linkListeners.clone();
      for(int i=0; i<targets.size(); i++) {
         ILinkListener listener = (ILinkListener)targets.get(i);
         if(listener!=null)
            listener.linkRemove(this);
      }
      ((ProcessItem)getFromProcessItem()).fireLinkRemove(this);
   }

   public void fireLinkRelink() {
      Vector targets = (Vector)this.linkListeners.clone();
      for(int i=0;i<targets.size();i++) {
         ILinkListener listener = (ILinkListener)targets.get(i);
         if(listener!=null)
            listener.linkRelink(this);
      }
   }

   private String getLinkStatus() {
       return source.getAttributeValue(HIDDEN_PROPERTY) + ":" + source.getAttributeValue("shortcut");
   }

   public void nodeChanged(Object eventData) {
     int[] path = null;

     String temp, ls;
     ls = getLinkStatus();
     if(global){
       temp = source.getAttributeValue(PATH_PROPERTY);
       path = strutsModel.getHelper().asIntArray(source, PATH_PROPERTY);
     }else{
       temp = source.getAttributeValue(SHAPE_PROPERTY);
       path = strutsModel.getHelper().asIntArray(source, SHAPE_PROPERTY);
     }

	 if(path.length > 1 && path[0] == -1){
	   preferredMode = true;
	   preferredLength = path[1];
	   path = new int[]{};
	 }else preferredMode = false;
	 
	 if(pathString.equals(temp) && (linkStatus == null || linkStatus.equals(ls))){
	 	fireLinkChange(); 
	 	tailBP.fireBreakPointChange();
	 	return; 
	 }

     Segment prevSegment = null;
     for(int i=0;i<path.length;i++) {
        Segment newSegment = new Segment(this,path[i],prevSegment);
        if(i==0)
           headSegment = newSegment;

        if(i==path.length-1)
           tailSegment = newSegment;
        prevSegment = newSegment;
     }
     pathString = temp;
     linkStatus = ls;
     fireLinkChange();
     tailBP.fireBreakPointChange();
   }
   

   public boolean isHeadBreakpointActive(){
	 if((getStrutsModel().getHelper().getBreakpointManager((XModelObject)getStrutsModel().getSource()).getBreakpointStatus((XModelObject)getToProcessItem().getSource()) & StrutsBreakpointManager.STATUS_BREAKPOINT_ACTIVE) > 0)
		return true;
	 else return false;
   }

   public int getTailBreakpointStatus(){
	 int value = getStrutsModel().getHelper().getBreakpointManager((XModelObject)getStrutsModel().getSource()).getBreakpointStatus((XModelObject)getSource());
	 int status=0;
   	 
	 if((value & StrutsBreakpointManager.STATUS_NO_BREAKPOINT) > 0) status = StrutsBreakpointManager.STATUS_NO_BREAKPOINT;
	 else if((value & StrutsBreakpointManager.STATUS_BREAKPOINT_ENABLED) > 0) status = StrutsBreakpointManager.STATUS_BREAKPOINT_ENABLED;
	 else if((value & StrutsBreakpointManager.STATUS_BREAKPOINT_DISABLED) > 0) status = StrutsBreakpointManager.STATUS_BREAKPOINT_DISABLED;
	 
	 return status;
   }

   public boolean isTailBreakpointActive(){
	 if((getStrutsModel().getHelper().getBreakpointManager((XModelObject)getStrutsModel().getSource()).getBreakpointStatus((XModelObject)getSource()) & StrutsBreakpointManager.STATUS_BREAKPOINT_ACTIVE) > 0)
		return true;
	 else return false;
   }
   
   public boolean isDebugMode(){
	 return getStrutsModel().getHelper().getBreakpointManager((XModelObject)getStrutsModel().getSource()).isDebugMode();
   }

	
	public PointList getPointList(){
		int[] path = null;
		if(global){
			pathString = source.getAttributeValue(PATH_PROPERTY);
			path = strutsModel.getHelper().asIntArray(source, PATH_PROPERTY);
		}else{
			pathString = source.getAttributeValue(SHAPE_PROPERTY);
			path = strutsModel.getHelper().asIntArray(source, SHAPE_PROPERTY);
		}
		
		PointList list = new PointList();
		
		if(path.length < 4) return list;
		if(path.length % 2 == 1) {
			//old format. Maybe some convertation should be done
			return list;
		}
		
		for(int i=0;i<path.length;i+=2){
			list.addPoint(path[i], path[i+1]);
		}
		
		return list;   	
	   }
	   
	   public void savePointList(PointList list){
	   	 String value = new String("");
	   	 for(int i=0;i<list.size();i++){
	   	 	if(i != 0) value += ",";
	   	 	Point p = list.getPoint(i);
	   	 	value += p.x +","+p.y;
	   	 }
	   	
	   	 if(global)strutsModel.getHelper().setAttributeValue(source, PATH_PROPERTY, value);
	   	 else strutsModel.getHelper().setAttributeValue(source, SHAPE_PROPERTY, value); 
	   }
	   
	   public void clearPointList(){
		 source.setAttributeValue(SHAPE_PROPERTY, "");
	   }
	   
	   class HeadBreakPoint implements IBreakPoint{
		Vector listeners = new Vector();

		public void addBreakPointListener(IBreakPointListener listener) {
			listeners.add(listener);
		}

		public void removeBreakPointListener(IBreakPointListener listener) {
			listeners.remove(listener);
		}
		
		public void fireBreakPointChange(){
			for(int i=0;i<listeners.size();i++){
				((IBreakPointListener)listeners.get(i)).breakPointChange();
			}
		}

		public boolean isDebugMode(){
	   	 return getStrutsModel().getHelper().getBreakpointManager((XModelObject)getStrutsModel().getSource()).isDebugMode();
	      }
	   	
	    public int getStatus(){
	      	 int value = getStrutsModel().getHelper().getBreakpointManager((XModelObject)getStrutsModel().getSource()).getBreakpointStatus((XModelObject)getToProcessItem().getSource());
	      	 int status=0;
	      	 
	      	 if((value & StrutsBreakpointManager.STATUS_NO_BREAKPOINT) > 0) status = StrutsBreakpointManager.STATUS_NO_BREAKPOINT;
	      	 else if((value & StrutsBreakpointManager.STATUS_BREAKPOINT_ENABLED) > 0) status = StrutsBreakpointManager.STATUS_BREAKPOINT_ENABLED;
	   	 else if((value & StrutsBreakpointManager.STATUS_BREAKPOINT_DISABLED) > 0) status = StrutsBreakpointManager.STATUS_BREAKPOINT_DISABLED;
	   	 
	      	 return status;
	      }
	    
		public int getActiveStatus() {
			return getStrutsModel().getHelper().getBreakpointManager((XModelObject)getStrutsModel().getSource()).getActiveBreakpointNumber((XModelObject)getToProcessItem().getSource());
		}
		   public boolean isActive(){
			 if((getStrutsModel().getHelper().getBreakpointManager((XModelObject)getStrutsModel().getSource()).getBreakpointStatus((XModelObject)getToProcessItem().getSource()) & StrutsBreakpointManager.STATUS_BREAKPOINT_ACTIVE) > 0)
				return true;
			 else return false;
		   }
		   public boolean isProcessItem(){
		   	return true;
		   }

	   	
	   }

	   class TailBreakPoint implements IBreakPoint{
		Vector listeners = new Vector();

		public void addBreakPointListener(IBreakPointListener listener) {
			listeners.add(listener);
		}

		public void removeBreakPointListener(IBreakPointListener listener) {
			listeners.remove(listener);
		}
		
		public void fireBreakPointChange(){
			for(int i=0;i<listeners.size();i++){
				((IBreakPointListener)listeners.get(i)).breakPointChange();
			}
		}
	   	
	    public boolean isDebugMode(){
	   	 return getStrutsModel().getHelper().getBreakpointManager((XModelObject)getStrutsModel().getSource()).isDebugMode();
	      }
	   	
		public int getActiveStatus() {
			return getStrutsModel().getHelper().getBreakpointManager((XModelObject)getStrutsModel().getSource()).getActiveBreakpointNumber((XModelObject)getSource());
		}

		   public int getStatus(){
			 int value = getStrutsModel().getHelper().getBreakpointManager((XModelObject)getStrutsModel().getSource()).getBreakpointStatus((XModelObject)getSource());
			 int status=0;
		   	 
			 if((value & StrutsBreakpointManager.STATUS_NO_BREAKPOINT) > 0) status = StrutsBreakpointManager.STATUS_NO_BREAKPOINT;
			 else if((value & StrutsBreakpointManager.STATUS_BREAKPOINT_ENABLED) > 0) status = StrutsBreakpointManager.STATUS_BREAKPOINT_ENABLED;
			 else if((value & StrutsBreakpointManager.STATUS_BREAKPOINT_DISABLED) > 0) status = StrutsBreakpointManager.STATUS_BREAKPOINT_DISABLED;
			 
			 return status;
		   }

		   public boolean isActive(){
			 if((getStrutsModel().getHelper().getBreakpointManager((XModelObject)getStrutsModel().getSource()).getBreakpointStatus((XModelObject)getSource()) & StrutsBreakpointManager.STATUS_BREAKPOINT_ACTIVE) > 0)
				return true;
			 else return false;
		   }
		   public boolean isProcessItem(){
		   	return false;
		   }
	   }

}

