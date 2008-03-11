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

import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.ui.editor.model.*;

public class StrutsElementList extends StrutsElement implements IStrutsElementList, VetoableChangeListener{

   Vector listeners = new Vector();
   Vector<IStrutsElement> elements = new Vector<IStrutsElement>();
   boolean elementListListenerEnable = true;
   boolean allowDuplicate = false;

   public StrutsElementList() {

   }
   
   public Vector getElements(){
   	return elements;
   }

   public StrutsElementList(IStrutsElement parent) {
      super(parent);
   }

   public StrutsElementList(IStrutsElement parent,XModelObject source) {
      super(parent,source);
   }

   public StrutsElementList(Vector vector) {
      elements = vector;
   }

   public void setAllowDuplicate(boolean set) {
      allowDuplicate = set;
   }

   public boolean isAllowDuplicate() {
      return allowDuplicate;
   }

   public void moveTo(IStrutsElement object, int index){
         int currentIndex = indexOf(object);
         if(index<0 || index>=size())
            return;
         if(currentIndex>index) { // move down
            for(int i=currentIndex-1;i>=index;i--) {
            	IStrutsElement elementAt = get(i);
               set(i+1,elementAt);
            }
            set(index,object);
            this.fireElementMoved((IStrutsElement)object,index,currentIndex);
         } else if(currentIndex<index) { // move up
            for(int i=currentIndex+1;i<=index;i++) {
            	IStrutsElement elementAt = get(i);
               set(i-1,elementAt);
            }
            set(index,object);
            this.fireElementMoved((IStrutsElement)object,index,currentIndex);
         }
   }

   public void moveUp(IStrutsElement object){
         int currentIndex = indexOf(object);
         if(currentIndex==0) return;
         set(currentIndex,get(currentIndex-1));
         set(currentIndex-1,object);
         this.fireElementMoved((IStrutsElement)object,currentIndex-1,currentIndex);
   }

   public void moveDown(IStrutsElement object) {
         int currentIndex = indexOf(object);
         if(currentIndex==size()) return;
         set(currentIndex,get(currentIndex+1));
         set(currentIndex+1,object);
         this.fireElementMoved((IStrutsElement)object,currentIndex+1,currentIndex);
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

   public boolean add(IStrutsElement o){
//      IStrutsElement element = (IStrutsElement)o;
      boolean result = elements.add(o);
      if(result){
        int index = elements.size() - 1;
        this.fireElementAdded( (IStrutsElement) o, index);
      }
      return result;
   }

   public void add(IStrutsElementList list){
      for(int i=0;i<list.size();i++) {
//         StrutsElement element = (StrutsElement)list.get(i);
         add(list.get(i));
         this.fireElementAdded((IStrutsElement)list.get(i),elements.size()-1);
      }

   }

   public boolean remove(IStrutsElement o){
      int index = indexOf(o);
      boolean result = elements.remove(o);
//      ((IStrutsElement)o).removeVetoableChangeListener(this);
      fireElementRemoved((IStrutsElement)o,index);
      return result;
   }

   public void remove(Comparator comp){
      for(int i=size()-1;i>=0;i--) {
         if(comp.equals(get(i))) {
            remove(get(i));
         };
      }
   }

   public void removeAll() {
      for(int i=size()-1;i>=0;i--) {
            remove(get(i));
      }
   }

   public IStrutsElement get(int index) {
      return elements.get(index);
   }

   public Object get(String name) {
      if(name == null) return null;
      for(int i=0;i<elements.size();i++) {
         StrutsElement element = (StrutsElement)elements.get(i);
         if(name.equals(element.getPath()))
            return element;
      }
      return null;
   }

   public IStrutsElement set(int index, IStrutsElement element) {
//      int oldIndex = elements.indexOf(element);
	   IStrutsElement newElement = elements.set(index,element);
      return newElement;
   }

   public void add(int index, IStrutsElement element){
      elements.add(index, element);
      this.fireElementAdded((IStrutsElement)element,index);
   }

/*   public Object remove(int index)  throws VetoException{
      Object obj = elements.get(index);
      elements.remove(obj);
      return obj;
   }
*/
   public int indexOf(Object o) {
      return elements.indexOf(o);
   }

   public IStrutsElement findElement(Comparator comparator) {
/*      for(int i=0;i<size();i++) {
         IStrutsElement element = (IStrutsElement)get(i);
         if(comparator.equals(element)) return element;
      }
      return null;

*/    return null;
   }



   public IStrutsElementList findElements(Comparator comparator) {
/*      ProcessElementList subList = new ProcessElementList();
      subList.setAllowDuplicate(isAllowDuplicate());
      for(int i=0;i<size();i++) {
         IStrutsElement element = (IStrutsElement)get(i);
         if(comparator.equals(element))
            try {
               subList.add(element);
            } catch(Exception exception) {
               return subList;
            }
      }
      return subList;
*/
      return null;
   }

   public String getText() {
/*      Iterator elements = this.iterator();
      String currentText = "";
      while(elements.hasNext()) {
         currentText = currentText + (currentText.equals("")?"":",") + ((ProcessElement)elements.next()).getName();
      }
      return currentText;
*/    return toString();
   }

   public Object clone() {
      Vector newVector = (Vector)elements.clone();
      StrutsElementList clone = new StrutsElementList(newVector);
      return clone;
   }

   public StrutsElementList getClone() {
      StrutsElementList list = (StrutsElementList)clone();
      return list;
   }

   public void vetoableChange(PropertyChangeEvent evt)
      throws PropertyVetoException {
    }

   public void addStrutsElementListListener(IStrutsElementListListener l) {
      listeners.add(l);
   }

   public void removeStrutsElementListListener(IStrutsElementListListener l) {
      listeners.remove(l);
   }

   protected void fireElementMoved(IStrutsElement element,int newIndex,int oldIndex){
      for(int i=0;i<listeners.size();i++) {
         IStrutsElementListListener listener = (IStrutsElementListListener)listeners.get(i);
         if(listener!=null && listener.isElementListListenerEnable())
            listener.listElementMove(this,element,newIndex,oldIndex);
      }
      ((StrutsModel)getStrutsModel()).fireElementRemoved(element,oldIndex);
      ((StrutsModel)getStrutsModel()).fireElementInserted(element);
   }

   protected void fireElementAdded(IStrutsElement element,int index){
      for(int i=0;i<listeners.size();i++) {
         IStrutsElementListListener listener = (IStrutsElementListListener)listeners.get(i);
         if(listener!=null && listener.isElementListListenerEnable())
            listener.listElementAdd(this,element,index);
      }
   }

   protected void fireElementRemoved(IStrutsElement element,int index) {
      for(int i=0;i<listeners.size();i++) {
         IStrutsElementListListener listener = (IStrutsElementListListener)listeners.get(i);
         if(listener!=null && listener.isElementListListenerEnable())
            listener.listElementRemove(this,element,index);
      }
   }

   protected void fireElementChanged(IStrutsElement element,int index,PropertyChangeEvent event) {
      for(int i=0;i<listeners.size();i++) {
         IStrutsElementListListener listener = (IStrutsElementListListener)listeners.get(i);
         if(listener!=null && listener.isElementListListenerEnable())
            listener.listElementChange(this,element,index,event);
      }
   }


   // Tree node realization

   /*public TreeNode getChildAt(int childIndex) {
      return (TreeNode)elements.elementAt(childIndex);
   }

   public int getChildCount() {
      // flow model has only two children Units list and Modules list
      Debug.println("Child count for '" + getName() + "' = " + size());
      return size();
   }

   public TreeNode getParent() {
      return (TreeNode)getParentStrutsElement();
   }

   public int getIndex(TreeNode node) {
      return this.indexOf(node);
   }

   public boolean getAllowsChildren() {
      return true;
   }

   public boolean isLeaf() {
      return false;
   }

   public Enumeration children() {
      return elements.elements();
   }*/

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

   public int compare(Object o1,Object o2) {
      return 0;
   }

   public boolean equals(Object obj) {
      if(obj instanceof IStrutsElement) {
         IStrutsElement element = (IStrutsElement)obj;
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

   public int compare(Object obj1,Object obj2) {
      return 0;
   }

   public boolean equals(Object obj) {
      if(obj instanceof IForward) {
         IForward message = (IForward)obj;
         return message.getName().equals(this.message);
      }
      return false;
   }
}

class ForwardComparator implements Comparator {
   Forward message;
   public ForwardComparator(Forward message) {
      this.message = message;
   }

   public int compare(Object obj1,Object obj2) {
      return 0;
   }

   public boolean equals(Object obj) {
      if(obj instanceof IForward) {
         IForward message = (IForward)obj;
         return message.getName().equals(this.message.getName()) &&
            message.getParentStrutsElement().getName().equals(this.message.getParentStrutsElement().getName());
      }
      return false;
   }
}


class TransitionComparator implements Comparator {
   IForward messageFrom;
   IForward messageTo;

   public TransitionComparator(IForward messageFrom,IForward messageTo) {
      this.messageFrom = messageFrom;
      this.messageTo = messageTo;
   }

   public boolean equals(Object transition) {
/*      ITransition trans = (ITransition)transition;
      return trans.getForwardFrom().getActivity().getName().equals(messageFrom.getActivity().getName()) &&
         trans.getForwardTo().getActivity().getName().equals(messageTo.getActivity().getName()) &&
         trans.getForwardFrom().getName().equals(messageFrom.getName()) &&
         trans.getForwardTo().getName().equals(messageTo.getName());
*/    return false;
   }

   public int compare(Object obj1,Object obj2) {
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
      if(object instanceof StrutsElement) {
         StrutsElement unit = (StrutsElement) object;
         return unit.getName().equals(elementName);
      }
      return false;
   }

   public int compare(Object obj1,Object obj2) {
      return 0;
   }
}

class TransitionForwardComparator implements Comparator {
  IForward message;
   public TransitionForwardComparator(IForward message) {
      this.message = message;
   }

   public int compare(Object obj1,Object obj2) {
      return 0;
   }

   public boolean equals(Object obj) {
/*      if(obj instanceof ITransition) {
         ITransition trans = (ITransition)obj;
          IForward transForward = null;
         if(message.getType()==IForward.OUTPUT_TYPE) {
            transForward = trans.getForwardFrom();
         } else if(message.getType()==IForward.INPUT_TYPE) {
            transForward = trans.getForwardTo();
         }
         return transForward.getName().equals(message.getName()) &&
            transForward.getActivity().getName().equals(message.getActivity().getName());
      }
*/
      return false;
   }
}




