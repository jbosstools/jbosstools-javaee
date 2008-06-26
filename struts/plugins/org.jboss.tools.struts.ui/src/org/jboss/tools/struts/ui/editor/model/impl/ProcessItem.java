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

import org.eclipse.swt.graphics.*;

import java.beans.PropertyChangeEvent;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.event.*;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.model.helpers.StrutsBreakpointManager;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;
import org.jboss.tools.struts.ui.editor.model.*;

public class ProcessItem extends StrutsElement implements IProcessItem, IStrutsElementListListener {

   public static Color DEFAULT_FOREGROUND_COLOR = new Color(null, 0x00, 0x00, 0x00);
   public static Color DEFAULT_BACKGROUND_COLOR = new Color(null, 0xE4, 0xE4, 0xE4);

   //public static String HEADER_BACKGROUND_COLOR_PROPERTY = "header background color";
   //public static String HEADER_FOREGROUND_COLOR_PROPERTY = "header foreground color";

   //public static String STARTABLE_PROPERTY = "startable";
   //public static String STOPPABLE_PROPERTY = "stoppable";
   //public static String UNIT_NAME_PROPERTY = "unit name";
   //public static String MODULE_NAME_PROPERTY = "module name";
   public static String TYPE_PROPERTY = "type";
   public static String SUBTYPE_PROPERTY = "subtype";
   public static String COMMENT_PROPERTY = "comment";
   protected Image icon=null;

   //protected int splitPosition=0;
   //protected String templatePath="";

   protected Color headerForegroundColor = DEFAULT_FOREGROUND_COLOR;
   protected Color headerBackgroundColor = DEFAULT_BACKGROUND_COLOR;

   String type;

   public ForwardList forwardList;

   Vector processItemListeners = new Vector();

   public ProcessItem(IStrutsModel model, XModelObject processItemNode){
      super(model, processItemNode);
      bp = new BreakPoint();

      this.type = processItemNode.getAttributeValue(TYPE_PROPERTY);
      icon = EclipseResourceUtil.getImage(processItemNode);

      if(type.equals(StrutsConstants.TYPE_ACTION) || type.equals(StrutsConstants.TYPE_PAGE)){
         global = false;
      }else if(type.equals(StrutsConstants.TYPE_FORWARD) || type.equals(StrutsConstants.TYPE_EXCEPTION)){
         global = true;
      }

      if(type != null){
         forwardList = new ForwardList(processItemNode);
         forwardList.addStrutsElementListListener(this);
      }
      name = (String)getSourceProperty("name");
      path = (String)getSourceProperty("path");
   }

   public Image getImage(){
   	return icon;
   }

   public ProcessItem(IStrutsModel model,IStrutsElement element) {
      super(model);
   }

   /*public ProcessItem(IStrutsElement parent, XModelObject processItemNode) {
      super(parent, processItemNode);
   }*/

   public String getViewClassName() {
      return strutsModel.getHelper().getItemGUIClass(source);
   }

   public String getVisiblePath(){
     if(type.equals(StrutsConstants.TYPE_PAGE)) return StrutsProcessStructureHelper.instance.getPageTitle(source);
     else return getPath();
   }

   boolean selected = false;

   public boolean isSelected() {
      return selected;
   }

   public boolean isPage(){
     if(type.equals(StrutsConstants.TYPE_PAGE)) return true;
     else return false;
   }

   public boolean isComment(){
     if(type.equals(StrutsConstants.TYPE_COMMENT)) return true;
     else return false;
   }
   
   public boolean hasErrors() {
	  return getStrutsModel().getHelper().hasErrors((XModelObject)getSource());
   }

   public boolean isAction(){
     if(type.equals(StrutsConstants.TYPE_ACTION)) return true;
     else return false;
   }

   public boolean isSwitchAction(){
     if(isAction()){
       String subtype = source.getAttributeValue(SUBTYPE_PROPERTY);
       if (subtype.equals(StrutsConstants.SUBTYPE_SWITCH))
         return true;
       else
         return false;
     }else return false;
   }

   public boolean isAnotherModule(){
     return strutsModel.getHelper().isItemFromOtherModule((XModelObject)getSource());
   }

   public boolean isGlobal(){
     if(type.equals(StrutsConstants.TYPE_FORWARD) || type.equals(StrutsConstants.TYPE_EXCEPTION)) return true;
     else return false;
   }

   public void setSelected(boolean set) {
      boolean oldValue = selected;
      selected = set;
      this.propertyChangeSupport.firePropertyChange("selected",oldValue,set);
      if(set)
         this.getStrutsModel().setSelectedProcessItem(this);
   }

   public void clearSelection() {
      IStrutsElementList list = getStrutsModel().getProcessItemList();
      for(int i=0;i<list.size();i++) {
         IProcessItem activity = (IProcessItem)list.get(i);
         activity.setSelected(false);
      }
   }

   public boolean hasPageHiddenLinks(){
     if(getSource() != null && isPage()){
        return strutsModel.getHelper().hasPageHiddenLinks((XModelObject)getSource());
     }else return false;
   }

   public IProcessItem getCommentTarget(){
      IProcessItem pItem = null;
      XModelObject target=null;
      if(getSource() != null){
         target = strutsModel.getHelper().getItemTarget((XModelObject)getSource());
         if(target == null) return null;
         pItem = (IProcessItem)strutsModel.findElement(target.getPath());
      }
      return pItem;
   }

   public boolean isConfirmed(){
      if(type.equals(StrutsConstants.TYPE_PAGE)){
         return strutsModel.getHelper().isPageConfirmed(source);
      }
      return !(StrutsConstants.SUBTYPE_UNKNOWN.equals(source.getAttributeValue(SUBTYPE_PROPERTY)));
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

   Vector<ILink> inputLinks = new Vector<ILink>();

   public ILink[] getLinks() {
      IStrutsElementList outputForwards = this.getForwardList();
      Vector<ILink> links  = (Vector<ILink>)inputLinks.clone();
      for(int i=0;i<outputForwards.size();i++) {
         ILink link = ((IForward)outputForwards.get(i)).getLink();
         if(link==null) continue;
         links.add(link);
      }
      ILink[] trans = new ILink[links.size()];
      links.toArray(trans);
      return trans;
   }

   public Vector getListInputLinks() {
		return inputLinks;
	}
   
	public Vector getListOutputLinks() {
	     IStrutsElementList outputForwards = this.getForwardList();
	     Vector links  = new Vector();
	     for(int i=0;i<outputForwards.size();i++) {
	        ILink link = ((IForward)outputForwards.get(i)).getLink();
	        if(link==null) continue;
	        links.add(link);
	     }
	     return links;
	}

   public ILink[] getInputLinks() {
   	  ILink[] links = new ILink[inputLinks.size()];
      return (ILink[])inputLinks.toArray(links);
   }

   public void addInputLink(ILink link){
   	if(!inputLinks.contains(link))
      inputLinks.add(link);
   }

   public void removeInputLink(ILink link){
      inputLinks.remove(link);
   }

   public ILink[] getOutputLinks() {
     IStrutsElementList outputForwards = this.getForwardList();
     Vector links  = new Vector();
     for(int i=0;i<outputForwards.size();i++) {
        ILink link = ((IForward)outputForwards.get(i)).getLink();
        if(link==null) continue;
        links.add(link);
     }
     ILink[] trans = new ILink[links.size()];
     links.toArray(trans);
     return trans;
   }

   public IForward getForward(String forwardName) {
      return (Forward)forwardList.get(forwardName);
   }

   public IStrutsElementList getForwardList(){
      return forwardList;
   }

   // Unit messages
   public void fireProcessItemChange() {
      if(processItemListeners.size()==0) return;
      Vector targets = (Vector)processItemListeners.clone();
      for(int i=0;i<targets.size();i++) {
         IProcessItemListener listener = (IProcessItemListener)targets.get(i);
         if(listener!=null) {
            listener.processItemChange();
         }
      }
   }


   public void fireForwardAdd(IForward message) {
      Vector listeners = (Vector)this.processItemListeners.clone();
      for(int i=0;i<listeners.size();i++) {
         IProcessItemListener listener = (IProcessItemListener)listeners.get(i);
         if(listener!=null && listener.isProcessItemListenerEnable())
            ((IProcessItemListener)listeners.get(i)).processItemForwardAdd(this,message);
      }
   }
   
   public void fireLinkAdd(ILink link) {
   	Vector listeners = (Vector)this.processItemListeners.clone();
    for(int i=0;i<listeners.size();i++) {
       IProcessItemListener listener = (IProcessItemListener)listeners.get(i);
       if(listener!=null && listener.isProcessItemListenerEnable())
          ((IProcessItemListener)listeners.get(i)).linkAdd(link);
    }
 }
   public void fireLinkRemove(ILink link) {
   	Vector listeners = (Vector)this.processItemListeners.clone();
    for(int i=0;i<listeners.size();i++) {
       IProcessItemListener listener = (IProcessItemListener)listeners.get(i);
       if(listener!=null && listener.isProcessItemListenerEnable())
          ((IProcessItemListener)listeners.get(i)).linkRemove(link);
    }
 }

   public void fireForwardRemove(IForward message,int index) {
      Vector listeners = (Vector)this.processItemListeners.clone();
      for(int i=0;i<listeners.size();i++) {
         IProcessItemListener listener = (IProcessItemListener)listeners.get(i);
         if(listener!=null && listener.isProcessItemListenerEnable())
            ((IProcessItemListener)listeners.get(i)).processItemForwardRemove(this,message);
      }
   }

   public void fireForwardChange(IForward message,PropertyChangeEvent evt) {
   }

   // remove state from model
   public void remove() {
      ILink[] links = getLinks();
      for(int i=0;i<links.length;i++) {
         links[i].remove();
      }
   }

  // Tree node realization
   /*public TreeNode getChildAt(int childIndex) {
      return null;
   }

   public int getChildCount() {
      return 2;
   }

   public TreeNode getParent() {
      return (TreeNode)(getParentStrutsElement());
   }

   public int getIndex(TreeNode node) {
      if(node.toString().equals("Output")) {
         return 1;
      } else {
         return 0;
      }
   }

   public boolean getAllowsChildren() {
      return true;
   }

   public boolean isLeaf() {
      return false;
   }

   public Enumeration children() {
      return null;
   }*/

   public void addProcessItemListener(IProcessItemListener listener) {
      processItemListeners.add(listener);
   }

   public void removeProcessItemListener(IProcessItemListener listener) {

   }

   public void removeFromStrutsModel() {
   }
   
   String name,path;

   public void nodeChanged(Object eventData) {
      fireProcessItemChange();
      this.propertyChangeSupport.firePropertyChange("name",name,this.getSourceProperty("name"));
      name = (String)getSourceProperty("name");
      bp.fireBreakPointChange();
      for(int i=0;i<inputLinks.size();i++){
      	((Link)inputLinks.get(i)).headBP.fireBreakPointChange();
      }
      //this.propertyChangeSupport.firePropertyChange("shape","",this.getSourceProperty("shape"));
      
      if(global){

         if(forwardList.size() == 0){
            if(!"".equals(getPath())){
               IForward newForward = new Forward(ProcessItem.this);
               forwardList.add(newForward);
               fireForwardAdd(newForward);
               ILink newLink = newForward.getLink();
               if(newLink != null){
                  fireLinkAdd(newLink);
               }
            }
         }else{
            if("".equals(getTarget())){
               IForward forward = (IForward)forwardList.get(0);
               forwardList.remove(forward);
               forward.remove();
               fireForwardRemove((Forward)forward,0);
            }else{
               Forward forward = (Forward)forwardList.get(0);
               if(!forward.getTargetString().equals(getTarget())){
                  forwardList.remove(forward);
                  forward.remove();
                  fireForwardRemove((Forward)forward,0);

                  IForward newForward = new Forward(ProcessItem.this);
                  forwardList.add(newForward);
                  fireForwardAdd(newForward);
                  ILink newLink = newForward.getLink();
                  if(newLink != null){
                     fireLinkAdd(newLink);
                  }
               }forward.nodeChanged(eventData);
            }
         }
      }else{
         this.propertyChangeSupport.firePropertyChange("path",path,this.getSourceProperty("path"));
		path = (String)getSourceProperty("name");
      }
      //this.propertyChangeSupport.firePropertyChange("show header","",this.getSourceProperty("show header"));
   }

   /*protected Link getGlobalLink(){
      if(global){
         return (Link)((IForward)forwardList.get(0)).getLink();
      }
      return null;
   }*/


   public class ForwardList extends StrutsElementList {
      public ForwardList(XModelObject listSource) {
         super(ProcessItem.this);
         if(ProcessItem.this.global){
            if(strutsModel.getHelper().getItemTarget(ProcessItem.this.source) != null){
               IForward newForward = new Forward(ProcessItem.this);
               add(newForward);
            }
         }else{
            XModelObject[] forwards = listSource.getChildren();
            for(int i=0;i<forwards.length;i++) {
               IForward newForward = new Forward(ProcessItem.this, forwards[i]);
               if(!newForward.isHidden())add(newForward);
            }
         }
      }

      public void structureChanged(Object eventData) {
         XModelObject listSource = (XModelObject)ProcessItem.this.getSource();
         XModelObject[] forwards = listSource.getChildren();
         if(forwards.length != size()) {
             // not implemented
             return;
         }
         for (int i = 0; i < size(); i++) {
             IStrutsElement pe = (IStrutsElement)get(i);
             XModelObject o = (XModelObject)pe.getSource();
             if(listSource.getChildByPath(o.getPathPart()) == null) {
                 // not implemented
                 return;
             }
         }
         // implemented only change of order of elements in list
         for (int i = 0; i < size(); i++) {
             IStrutsElement pe = (IStrutsElement)get(i);
             XModelObject o = (XModelObject)pe.getSource();
             if(!forwards[i].getPathPart().equals(o.getPathPart())) {
                 moveTo(pe, size() - 1);
                 --i;
             }
         }

      }

      public void nodeAdded(Object eventData) {
         XModelTreeEvent event = (XModelTreeEvent)eventData;
         IForward newForward = new Forward(ProcessItem.this,((XModelObject)event.getInfo()));
         if(type.equals("page") && newForward.getLink() == null) return;
         this.add(newForward);
         fireForwardAdd(newForward);
         //if(type.equals("page")) this.fireElementAdded(newForward, this.indexOf(newForward));
         ILink newLink = newForward.getLink();
         if(newLink != null){
            fireLinkAdd(newLink);
         }
      }

      public void nodeRemoved(Object eventData) {
         XModelTreeEvent event = (XModelTreeEvent)eventData;
         IStrutsElement removedForward = getFromMap(event.getInfo());
         removeFromMap(((XModelTreeEvent)eventData).getInfo());
         int index = this.indexOf(removedForward);
         //ILink link = ProcessItem.this.getLink((IForward)removedForward);
         //link.remove();
         this.remove(removedForward);
         removedForward.remove();
         fireForwardRemove((Forward)removedForward,index);
         //this.fireElementRemoved(removedForward, index);
      }

      public void addForward(IForward forward){
         ((StrutsModel)strutsModel).putToMap(forward.getSource(), forward);
         this.add(forward);
         fireForwardAdd(forward);
         //this.fireElementRemoved(forward, index);
      }

      public void removeForward(IForward forward){
         removeFromMap(forward);
         int index = this.indexOf(forward);
         //ILink link = ProcessItem.this.getLink((IForward)removedForward);
         //link.remove();
         this.remove(forward);
         fireForwardRemove(forward,index);
         //this.fireElementRemoved(forward, index);
      }
   }
   public void structureChanged(Object eventData) {
      forwardList.structureChanged(eventData);
   }

   public void nodeAdded(Object eventData) {
      forwardList.nodeAdded(eventData);
   }

   public void nodeRemoved(Object eventData) {
      forwardList.nodeRemoved(eventData);
   }

   public boolean isElementListListenerEnable() {
      return true;
   }

   public void setElementListListenerEnable(boolean set) {

   }

   public void listElementMove(IStrutsElementList list, IStrutsElement element, int newIndex, int oldIndex) {

   }

   public void listElementAdd(IStrutsElementList list, IStrutsElement element, int index) {
      this.fireForwardAdd((IForward)element);
   }

   public void listElementRemove(IStrutsElementList list, IStrutsElement element, int index) {
      this.fireForwardRemove((IForward)element,index);
   }

   public void listElementChange(IStrutsElementList list, IStrutsElement element, int index, PropertyChangeEvent event) {

   }
   private BreakPoint bp;
   public IBreakPoint getBreakPoint(){
   	return bp;
   }
   
	class BreakPoint implements IBreakPoint{
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
		public boolean isProcessItem() {
			return true;
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
		   
		   public boolean isDebugMode(){
			 return getStrutsModel().getHelper().getBreakpointManager((XModelObject)getStrutsModel().getSource()).isDebugMode();
		   }
		   
			public int getActiveStatus() {
				return getStrutsModel().getHelper().getBreakpointManager((XModelObject)getStrutsModel().getSource()).getActiveBreakpointNumber((XModelObject)getSource());
			}
		
	}
}