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

import java.beans.PropertyVetoException;

import org.w3c.dom.*;

import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jsf.ui.editor.model.ILink;
import org.jboss.tools.jsf.ui.editor.model.ISegment;

public class Segment extends JSFElement implements ISegment {

   int length;

   public Segment(ILink parent, Element segmentElement, ISegment prevSegment) {
      super(parent);
      try {
         setName("SEGMENT");
         String s = segmentElement.getAttribute("length"); //$NON-NLS-1$
         if(s == null || s.length() == 0) {
        	 length = 0;
         } else {
        	 length = Integer.parseInt(s);
         }
      } catch(PropertyVetoException exception) {
          length = 0;
      } catch(NumberFormatException exception) {
          length = 0;
          JsfUiPlugin.getPluginLog().logError(exception);
      }
      this.prevSegment = prevSegment;
      if(prevSegment!=null)
         prevSegment.setNext(this);
   }

   public Segment(ILink parent, int length, ISegment prevSegment) {
      super(parent);
      try {
         setName("SEGMENT");
         this.length = length;
      } catch(PropertyVetoException exception) {
         length = 0;
      }

      this.length = length;
      this.prevSegment = (Segment)prevSegment;
      if(prevSegment!=null)
         prevSegment.setNext(this);
   }

   ISegment prevSegment;
   ISegment nextSegment;

   public ISegment getNext() {
      return nextSegment;
   }

   public ISegment getPrev() {
      return prevSegment;
   }

   public void setPrev(ISegment segment) {
      ISegment oldPrev = prevSegment;
      prevSegment = (Segment)segment;
      propertyChangeSupport.firePropertyChange("prev",oldPrev,segment); //$NON-NLS-1$
   }

   public void setNext(ISegment segment) {
      ISegment oldNext = nextSegment;
      nextSegment = (Segment)segment;
      propertyChangeSupport.firePropertyChange("next",oldNext,segment); //$NON-NLS-1$
   }

   public int getLength() {
      return length;
   }

   public void setLength(int length) {
      int oldLength = this.length;
      this.length = length;
      propertyChangeSupport.firePropertyChange("length",oldLength,length); //$NON-NLS-1$
   }

   public String getName() {
      return "" + getLength(); //$NON-NLS-1$
   }

}