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

import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.swt.graphics.Color;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.ui.editor.edit.CommentEditPart;
import org.jboss.tools.struts.ui.editor.edit.ProcessItemEditPart;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;

public class CommentFigure extends ProcessItemFigure implements FigureListener{
	
	public static final Color BACKGROUND_COLOR = new Color(null, 0xff, 0xff, 0xcd);
	
	protected PointList fillPointlist;
	protected ProcessItemFigure commented=null;
	protected CommentsCorner corner;
	protected Point deltaPoint=null;
	private TextFlow textFlow;
	
	public CommentFigure(IProcessItem processItem, CommentEditPart part) {
		super(processItem, part);
		setBorder(new CommentBorder());
		if(processItem != null){
			IProcessItem pItem = processItem.getCommentTarget();
			if(pItem != null){
			  	commented = (ProcessItemFigure)((ProcessItemEditPart)editPart.getViewer().getEditPartRegistry().get(pItem)).getFigure();
			  	if(commented != null){
			  		corner = new CommentsCorner(this,commented);
			  	}
			}
			FlowPage flowPage = new FlowPage();

			textFlow = new TextFlow();

			textFlow.setLayoutManager(new ParagraphTextLayout(textFlow,
							ParagraphTextLayout.WORD_WRAP_SOFT));
			textFlow.setFont(processItem.getStrutsModel().getOptions().getCommentFont());
			textFlow.setText(((XModelObject)processItem.getSource()).getAttributeValue("comment"));

			flowPage.add(textFlow);

			setLayoutManager(new StackLayout());
			add(flowPage);
			
			
		}
	}
	
	public String getText() {
		return textFlow.getText();
	}

	public void setText(String newText) {
		textFlow.setFont(processItem.getStrutsModel().getOptions().getCommentFont());
		textFlow.setText(newText);
	}

	protected void calculateDeltaPoint(){
	      if(commented != null){
	        deltaPoint = new Point(getLocation().x-commented.getLocation().x, getLocation().y-commented.getLocation().y);
	        if(deltaPoint.x == 0 && deltaPoint.y == 0)deltaPoint=null;
	      }
    }

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if(commented != null){
	  		corner.setVisible(visible);
	  	}
	}
	
	/*public void setBounds(Rectangle rect) {
		if(rect.width < 100) rect.width = 100;
		super.setBounds(rect);
		resizeFigure();
	}*/
	
	public void figureMoved(IFigure source) {
		if(this instanceof CommentFeedbackFigure) return;
		 super.figureMoved(source);
		 
		 if(commented != null && source == this){
			 if(processItem.isJump()){
			 	calculateDeltaPoint();
			 	return;
			 }else processItem.setJump(true); 
			 
		 	calculateDeltaPoint();
	        return;
		 }
		if(commented == null || commented != source){
			return;
		}
	    if(commented.processItem.isJump()){
	        calculateDeltaPoint();
	        return;
	    }else commented.processItem.setJump(true);
	    
	    if(deltaPoint != null){
	      	Rectangle rect = getBounds();
	      	rect.x = commented.getLocation().x + deltaPoint.x;
	      	rect.y = commented.getLocation().y + deltaPoint.y;
	      	if(rect.x < 0) rect.x = 0;
	      	if(rect.y < 0) rect.y = 0;
	      	
	        //setBounds(rect);
	      	//jump=true;
	        processItem.setBounds(rect);
	        
	        corner.figureMoved(this);
	        getParent().revalidate();
	        getParent().repaint();
	    }//else calculateDeltaPoint();
	}
	
	public void addNotify(){
		if(commented != null){
			getParent().add(corner);
			commented.addFigureListener(this);
			calculateDeltaPoint();
		}
	}
	
	public void removeNotify(){
		if(commented != null){
			getParent().remove(corner);
			commented.removeFigureListener(this);
		}
	}
	
	protected void resizeFigure(){
		if(width == getSize().width && height == getSize().height) return;
		
		//int start=0;
	 	/*if ( getSize().width < 100) {
	 		setBounds(new Rectangle(getBounds().x < 0 ? 0 : getBounds().x, 
	 						getBounds().y < 0 ? 0 : getBounds().y,  
	 						100, height + 1 ));
	 		return;
	 	}*/
	 	width = getSize().width-1;
	 	height = getSize().height-1;
	 	fillPointlist = new PointList();
		
	 	fillPointlist.addPoint(1, 8);
	 	fillPointlist.addPoint(2, 8);
	 	fillPointlist.addPoint(2, 6); 
	 	fillPointlist.addPoint(3, 6); 
	 	fillPointlist.addPoint(3, 5);
	 	fillPointlist.addPoint(4, 5); 
	 	fillPointlist.addPoint(4, 4);
	 	fillPointlist.addPoint(5, 4);
	 	fillPointlist.addPoint(5, 3);
	 	fillPointlist.addPoint(7, 3);
	 	fillPointlist.addPoint(7, 2);
	 	fillPointlist.addPoint(10, 2);
	 	fillPointlist.addPoint(10, 1);
	 	fillPointlist.addPoint(width-10, 1);
	 	fillPointlist.addPoint(width-10, 2);
	    fillPointlist.addPoint(width-7, 2);
	    fillPointlist.addPoint(width-7, 3);
	    fillPointlist.addPoint(width-5, 3); 
	    fillPointlist.addPoint(width-5, 4);
	    fillPointlist.addPoint(width-4, 4);
	    fillPointlist.addPoint(width-4, 5);
	    fillPointlist.addPoint(width-3, 5);
	    fillPointlist.addPoint(width-3, 6);
	    fillPointlist.addPoint(width-2, 6);
	    fillPointlist.addPoint(width-2, 8);
	    fillPointlist.addPoint(width-1, 8);
	    fillPointlist.addPoint(width-1, height-8);
	    fillPointlist.addPoint(width-2, height-8);
	    fillPointlist.addPoint(width-2, height-6);
	    fillPointlist.addPoint(width-3, height-6);
	    fillPointlist.addPoint(width-3, height-5);
	    fillPointlist.addPoint(width-4, height-5);
	    fillPointlist.addPoint(width-4, height-4);
	    fillPointlist.addPoint(width-5, height-4);
	    fillPointlist.addPoint(width-5, height-3);
	    fillPointlist.addPoint(width-7, height-3);
	    fillPointlist.addPoint(width-7, height-2);
	    fillPointlist.addPoint(width-10, height-2);
	    fillPointlist.addPoint(width-10, height-1);
	    fillPointlist.addPoint(10, height-1);
	    fillPointlist.addPoint(10, height-2);
	    fillPointlist.addPoint(7, height-2);
	    fillPointlist.addPoint(7, height-3);
	    fillPointlist.addPoint(5, height-3);
	    fillPointlist.addPoint(5, height-4);
	    fillPointlist.addPoint(4, height-4);
	    fillPointlist.addPoint(4, height-5);
	    fillPointlist.addPoint(3, height-5);
	    fillPointlist.addPoint(3, height-6);
	    fillPointlist.addPoint(2, height-6);
	    fillPointlist.addPoint(2, height-8);
	    fillPointlist.addPoint(1, height-8);
	 	
	}
	
	protected void paintFigure(Graphics g) {
		Rectangle r = getBounds().getCopy();
		g.translate(r.getLocation());
//		int width = r.width-1;
//		int height = r.height-1;
		if(processItem == null) g.setBackgroundColor(lightGrayColor);
		else g.setBackgroundColor(BACKGROUND_COLOR);
		
		
	    g.fillPolygon(fillPointlist);
		
		
	}	
	
	class CommentBorder extends MarginBorder{
		public CommentBorder() {
			super(6);
		}
		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			Rectangle r = getPaintRectangle(figure, insets).getCopy();
			graphics.translate(r.getLocation());
			int width = r.width-1;
			int height = r.height-1;
			if(processItem != null && processItem.isConfirmed())
				graphics.setForegroundColor(blackColor);
			else 
				graphics.setForegroundColor(darkGrayColor);
				
		      
			graphics.drawLine(1, 7, 1, 6);
			graphics.drawLine(2, 5, 4, 3);
			graphics.drawLine(5, 2, 6, 2);
			graphics.drawLine(7, 1, 9, 1);
			graphics.drawLine(10, 0, width-11, 0);
			graphics.drawLine(width-10, 1, width-8, 1);
			graphics.drawLine(width-7, 2, width-6, 2);
			graphics.drawLine(width-5, 3, width-3, 5);
			graphics.drawLine(width-2, 6, width-2, 7);
			graphics.drawLine(width-1, 8, width-1, height-9);
			graphics.drawLine(width-2, height-8, width-2, height-7);
			graphics.drawLine(width-3, height-6, width-5, height-4);
			graphics.drawLine(width-6, height-3, width-7, height-3);
			graphics.drawLine(width-8, height-2, width-10, height-2);
			graphics.drawLine(width-11, height-1, 10, height-1);
			graphics.drawLine(9, height-2, 7, height-2);
			graphics.drawLine(6, height-3, 5, height-3);
			graphics.drawLine(4, height-4, 2, height-6);
			graphics.drawLine(1, height-7, 1, height-8);
			graphics.drawLine(0, height-9, 0, 8);
			
			graphics.setBackgroundColor(BACKGROUND_COLOR);
			graphics.setForegroundColor(BACKGROUND_COLOR);
		      if(corner != null){
		         if(corner.cornerType == CommentsCorner.NORTH_CORNER || corner.cornerType == CommentsCorner.NE_CORNER || corner.cornerType == CommentsCorner.NW_CORNER) graphics.drawLine(31, height-1, 59, height-1);
		         else if(corner.cornerType == CommentsCorner.SOUTH_CORNER || corner.cornerType == CommentsCorner.SE_CORNER || corner.cornerType == CommentsCorner.SW_CORNER) graphics.drawLine(31, 0, 59, 0);
		         else if(corner.cornerType == CommentsCorner.WEST_CORNER){
		            if (height <= 46) graphics.drawLine(width-1, 9, width-1, height-10);
		            else graphics.drawLine(width-1, 9, width-1, 37);
		         }else if(corner.cornerType == CommentsCorner.EAST_CORNER){
		               if (height <= 46) graphics.drawLine(0, 9, 0, height-10);
		               else graphics.drawLine(0, 9, 0, 37);
		         }
		      }
			
		}
	}
	
}
