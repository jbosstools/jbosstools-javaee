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


import org.w3c.dom.*;

import org.jboss.tools.struts.ui.StrutsUIPlugin;
import org.jboss.tools.struts.ui.editor.model.*;

//import javax.swing.tree.*;
//import java.util.*;

public class Segment extends StrutsElement implements ISegment {

   int length;

   public Segment(ILink parent, Element segmentElement, ISegment prevSegment) {
		super(parent);

		try {
			setName("SEGMENT");
			length = Integer.parseInt(segmentElement.getAttribute("length"));
		} catch (Exception e) {
			StrutsUIPlugin.getPluginLog().logError(e);
			length = 0;
		}
		
		this.prevSegment = prevSegment;
		
		if (prevSegment != null)
			prevSegment.setNext(this);
	}

   public Segment(ILink parent, int length, ISegment prevSegment) {
		super(parent);
		
		try {
			setName("SEGMENT");
			this.length = length;
		} catch (Exception e) {
			StrutsUIPlugin.getPluginLog().logError(e);
			length = 0;
		}

		this.length = length;
		this.prevSegment = (Segment) prevSegment;

		if (prevSegment != null)
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
      propertyChangeSupport.firePropertyChange("prev",oldPrev,segment);
   }

   public void setNext(ISegment segment) {
      ISegment oldNext = nextSegment;
      nextSegment = (Segment)segment;
      propertyChangeSupport.firePropertyChange("next",oldNext,segment);
   }

   public int getLength() {
      return length;
   }

   public void setLength(int length) {
      int oldLength = this.length;
      this.length = length;
      propertyChangeSupport.firePropertyChange("length",oldLength,length);
   }

   public String getName() {
      return "" + getLength();
   }

   // tree node implementation

   /*public TreeNode getParent() {
      return null;
   }

   public TreeNode getChildAt(int childIndex) {
      return null;
   }

   public int getChildCount() {
      // flow model has only two children Units list and Modules list
      return 0;
   }

   public int getIndex(TreeNode node) {
      return 0;
   }

   public boolean getAllowsChildren() {
      return false;
   }

   public boolean isLeaf() {
      return true;
   }

   public Enumeration children() {
      return null;
   }*/

}