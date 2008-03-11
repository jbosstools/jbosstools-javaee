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
//import javax.swing.tree.*;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.ui.editor.model.*;

public class Forward extends StrutsElement implements IForward{

   public Vector listeners = new Vector();
   Link link;
   IStrutsElementList partList = new StrutsElementList();
   String name;
   String target;
   //boolean hidden;
   boolean relink = false;

   public Forward(IStrutsElement parent, XModelObject source) {
      super(parent, source);
      target = source.getAttributeValue(TARGET_PROPERTY);
      initLink();
      name = getName();
   }

   public Forward(IStrutsElement parent) {
      super(parent);
      target = ((XModelObject)parent.getSource()).getAttributeValue(TARGET_PROPERTY);
      initLink();
      name = getName();
   }

   public XModelObject getTargetModel(){
      XModelObject target;
      if(source == null){
         source = (XModelObject)parent.getSource();
         target = strutsModel.getHelper().getItemTarget(source);
      }else{
         target = strutsModel.getHelper().getItemOutputTarget(source);
      }
      return target;
   }

   public boolean isHidden(){
      String hidden;
      if(source == null) source = (XModelObject)parent.getSource();
      hidden = source.getAttributeValue(Link.HIDDEN_PROPERTY);
      if("yes".equals(hidden)) return true;
      else return false;
   }

   public String getTargetString(){
      return target;
   }

   public void initLink() {
      XModelObject target = getTargetModel();
      if(getProcessItem().isPage() && isHidden()){
         link = null;
         return;
      }
      if(target != null){
         link = new Link(this, target);
      }else{
         link = null;
      }
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
      return "\t\t\t\t<MSG MESSAGE=\"" + getName() + "\"/>";
   }

   // ------------------------------------------------------------------------
   // Event support
   // ------------------------------------------------------------------------

   public void addForwardListener(IForwardListener listener) {
      listeners.add(listener);
   }

   public void removeForwardListener(IForwardListener listener) {
      listeners.remove(listener);
   }

   public void fireForwardRemoved() {
      Vector listeners = (Vector)this.listeners.clone();
      for(int i=0;i<listeners.size();i++) {
         IForwardListener listener = (IForwardListener)listeners.elementAt(i);
         if(listener!=null)
            listener.forwardRemoved(this);
      }
   }

   public void fireForwardChanged() {
	  Vector listeners = (Vector)this.listeners.clone();
	  for(int i=0;i<listeners.size();i++) {
		 IForwardListener listener = (IForwardListener)listeners.elementAt(i);
		 if(listener!=null)
			listener.forwardChanged(this);
	  }
   }

   public void removeLink(IForward message) {
   }

   public ILink addLink(IProcessItem toProcessItem) {
      return null;
   }

   public ILink getLink() {
      return link;
   }

   public void rename(String newName) {

   }

   public IProcessItem getProcessItem() {
      return (IProcessItem) getParentStrutsElement();
   }

   public IStrutsElementList getPartList() {
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

   // TreeNode implementation

   /*public TreeNode getChildAt(int childIndex) {
      if(link == null)
         return (TreeNode)partList.get(childIndex);
      else
         return (TreeNode)(childIndex == 0 ? link : partList.get(childIndex-1));
   }

   public int getChildCount() {
      if (link == null)
         return partList.size();
      else
         return partList.size()+1;
   }

   public TreeNode getParent() {
      return (TreeNode)getParentStrutsElement();
   }

   public int getIndex(TreeNode node) {
      if(link == null)
         return partList.indexOf(node);
      else {
         int index = partList.indexOf(node);
         return index==-1?0:index;
      }
   }*/

   public void remove() {
      if(link != null)
         link.remove();
   }

   public boolean getAllowsChildren() {
      return true;
   }

   public void nodeChanged(Object eventData) {
      if(!name.equals(getName())){
        propertyChangeSupport.firePropertyChange("name", name, getName());
        name = getName();
      }
      String targ = source.getAttributeValue(TARGET_PROPERTY);
      ProcessItem item = (ProcessItem)getProcessItem();
      if(link == null){
        if(!"".equals(targ)){
          if(item.isPage() && isHidden())return; //hide
          initLink();
          if (link != null){
            if(item.isPage()){
              item.forwardList.fireElementAdded(this, item.forwardList.indexOf(this));
              item.forwardList.add(this);
              item.fireForwardAdd(this);
            }else{
              item.fireLinkAdd(link);
              fireForwardChanged();
            }
          }
        }
      }else{
         if(item.isPage() && isHidden()){
           link.remove();
           item.fireLinkRemove(link);
           link = null;
         }else if("".equals(targ)){
            link.remove();
            item.fireLinkRemove(link);
            link = null;
         }else{
            if(!targ.equals(target)){
                relink = true;
                if(item.isPage() && isHidden()) relink = false;
                int index = item.getForwardList().indexOf(this);
                link.remove();
                item.getForwardList().remove(this);
                item.fireLinkRemove(link);
                link = null;
                if(index < 0) {
                	///What else can be done?
                	return;
                }

                item.getForwardList().add(index, this);
                initLink();
                if(link != null){
                  item.fireLinkAdd(link);
                }
                relink = false;
            }else link.nodeChanged(eventData);
         }
		//link.nodeChanged(eventData);
      }
      target = targ;
   }

   public boolean isException(){
      if("exception".equals(getType())) return true;
      else return false;
   }

   public boolean isRelink(){
      return relink;
   }
}

