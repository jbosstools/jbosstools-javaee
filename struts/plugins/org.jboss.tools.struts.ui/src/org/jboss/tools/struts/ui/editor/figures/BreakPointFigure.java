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
package org.jboss.tools.struts.ui.editor.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.graphics.Color;

import org.jboss.tools.common.gef.GEFGraphicalViewer;
import org.jboss.tools.struts.model.helpers.StrutsBreakpointManager;
import org.jboss.tools.struts.ui.editor.StrutsEditor;
import org.jboss.tools.struts.ui.editor.model.IBreakPoint;
import org.jboss.tools.struts.ui.editor.model.IBreakPointListener;


public class BreakPointFigure extends Figure implements MouseListener, IBreakPointListener{

	public static boolean itemFlag = false;
	public static boolean outputFlag = false;
	
	public static final Color greenColor = new Color(null, 0x1c, 0xce, 0x00);
	public static final Color redColor = new Color(null, 0xff, 0x00, 0x00);
	
	boolean sheduled = false;
	IBreakPoint bp;
	EditPart part;
	boolean flag = false;
	
	public BreakPointFigure(EditPart editPart, IBreakPoint bp){
		super();
		setSize(9,9);
		this.bp = bp;
		part = editPart;
		addMouseListener(this);
		update();
	}
	
	public BreakPointFigure(EditPart editPart, IBreakPoint bp, boolean flag){
		this(editPart,bp);
		this.flag = flag;
	}
	public void addNotify() {
		super.addNotify();
		bp.addBreakPointListener(this);
	}
	public void removeNotify() {
		super.removeNotify();
		bp.removeBreakPointListener(this);
	}
	
	public void breakPointChange() {
		update();
		repaint();
	}
	
	public void update(){
		if(bp.isDebugMode() || bp.getStatus() != StrutsBreakpointManager.STATUS_NO_BREAKPOINT){
			setVisible(true);
		}else setVisible(false);
	}
	
	public void paintFigure(Graphics g){
		update();
		Rectangle bounds = getBounds();
		g.translate(bounds.x, bounds.y);
		
		  g.setForegroundColor(NodeFigure.blackColor);
		  g.drawRectangle(0,0,8,8);

		if(bp.isActive() && !sheduled){
		  StrutsEditor.blinker.addRedrawListener(this);
		  sheduled = true;
		}
		if(!bp.isActive() && sheduled){
			StrutsEditor.blinker.removeRedrawListener(this);
		  sheduled = false;
		}
		  
		  
		  if(StrutsEditor.blinker.isBlink() && bp.isActive()){
			g.setBackgroundColor(greenColor);
		  }else{
			  if(bp.getStatus() == StrutsBreakpointManager.STATUS_NO_BREAKPOINT)
				  g.setBackgroundColor(NodeFigure.whiteColor);
			  else if(bp.getStatus() == StrutsBreakpointManager.STATUS_BREAKPOINT_DISABLED)
				  g.setBackgroundColor(NodeFigure.mediumGrayColor);
			  else if(bp.getStatus() == StrutsBreakpointManager.STATUS_BREAKPOINT_ENABLED)
				  g.setBackgroundColor(redColor);
		  }

  	
		  g.fillRectangle(1,1,7,7);
		  
		  if(bp.getActiveStatus() == 1){
			  // 1
			  g.setForegroundColor(NodeFigure.whiteColor);
			  g.drawLine(4,2,4,6);
			  g.drawLine(3,3,4,3);
		  }else if(bp.getActiveStatus() == 2){
			  // 2
			  g.setForegroundColor(NodeFigure.whiteColor);
			  g.drawLine(2,2,5,2);
			  g.drawLine(6,3,6,4);
			  g.drawLine(4,4,6,4);
			  g.drawLine(2,5,3,5);
			  g.drawLine(2,6,6,6);
		  }else if(bp.getActiveStatus() == 3){
			  // 3
			  g.setForegroundColor(NodeFigure.whiteColor);
			  g.drawLine(2,2,6,2);
			  g.drawLine(6,2,6,3);
			  g.drawLine(4,4,5,4);
			  g.drawLine(6,5,6,6);
			  g.drawLine(2,6,6,6);
		  }
	}
	public void mouseDoubleClicked(MouseEvent me) {
	}

	public void mousePressed(MouseEvent me) {
		if(me.button == 3){
			if(flag){
				((GEFGraphicalViewer)part.getViewer()).setNoDeselect();
				part.getViewer().select(part);
			}
			if(bp.isProcessItem()) itemFlag = true;
			else outputFlag = true;
		}
	}

	public void mouseReleased(MouseEvent me) {
	}

}
