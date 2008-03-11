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
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

public class CommentsCorner extends Figure implements FigureListener{
   public static final int INCORRECT_CORNER = 0;
   public static final int NORTH_CORNER     = 1;
   public static final int WEST_CORNER      = 2;
   public static final int EAST_CORNER      = 3;
   public static final int SOUTH_CORNER     = 4;
   public static final int NE_CORNER        = 5;
   public static final int NW_CORNER        = 6;
   public static final int SE_CORNER        = 7;
   public static final int SW_CORNER        = 8;
   
   public static final Color BACKGROUND_COLOR = new Color(null, 0xff, 0xff, 0xcd);
   public static final Color FOREGROUND_COLOR = NodeFigure.blackColor;
	  
   public int cornerType = INCORRECT_CORNER;

   private ProcessItemFigure commentView, processItemView;
   private Rectangle commentBounds, processItemBounds;
   private Point rightPoint, leftPoint, basePoint;

   public CommentsCorner(ProcessItemFigure commentView, ProcessItemFigure processItemView){
   	  setBorder(new CommentCornerBorder(FOREGROUND_COLOR));
      this.commentView = commentView;
      this.processItemView = processItemView;
      commentBounds = commentView.getBounds();
      processItemBounds = processItemView.getBounds();
      commentView.addFigureListener(this);
      processItemView.addFigureListener(this);
   }
   
	public void addNotify(){
	      calculate();
		  panelRedraw();
	}

   public void close(){
     commentView.removeFigureListener(this);
     processItemView.removeFigureListener(this);
   }

   public void paintFigure(Graphics g){
//	Rectangle r = getBounds().getCopy();
//	int width = r.width-1;
//	int height = r.height-1;
   	
     if(cornerType == INCORRECT_CORNER) return;
     
     g.setBackgroundColor(BACKGROUND_COLOR);

     PointList list = new PointList();
     list.addPoint(basePoint);
     list.addPoint(rightPoint);
     list.addPoint(leftPoint);
     
     g.fillPolygon(list);

     //g.setForegroundColor(FOREGROUND_COLOR);
	     
	 //g.drawLine(basePoint, rightPoint);
	 //g.drawLine(basePoint, leftPoint);
     
   }

   private void calculate(){
      cornerType = INCORRECT_CORNER;
      if((commentBounds.y+commentBounds.height) < processItemBounds.y){
         if((commentBounds.x+commentBounds.width) < processItemBounds.x){
            cornerType = NW_CORNER;
         }else if((processItemBounds.x+processItemBounds.width) < commentBounds.x){
            cornerType = NE_CORNER;
         }else cornerType = NORTH_CORNER;
      }else if((processItemBounds.y+processItemBounds.height) < commentBounds.y){
        if((commentBounds.x+commentBounds.width) < processItemBounds.x){
           cornerType = SW_CORNER;
        }else if((processItemBounds.x+processItemBounds.width) < commentBounds.x){
           cornerType = SE_CORNER;
        }else cornerType = SOUTH_CORNER;
      }else if((commentBounds.x+commentBounds.width) < processItemBounds.x){
         cornerType = WEST_CORNER;
      }else if((processItemBounds.x+processItemBounds.width) < commentBounds.x){
         cornerType = EAST_CORNER;
      }
      switch(cornerType){
         case NORTH_CORNER:
            basePoint = new Point(processItemBounds.x+processItemBounds.width/2, processItemBounds.y);
            leftPoint = new Point(commentBounds.x+30, commentBounds.y+commentBounds.height-1);
            rightPoint = new Point(commentBounds.x+60, commentBounds.y+commentBounds.height-1);
         break;

         case SOUTH_CORNER:
            basePoint = new Point(processItemBounds.x+processItemBounds.width/2, processItemBounds.y+processItemBounds.height);
            leftPoint = new Point(commentBounds.x+30, commentBounds.y);
            rightPoint = new Point(commentBounds.x+60, commentBounds.y);
         break;

         case WEST_CORNER:
            basePoint = new Point(processItemBounds.x, processItemBounds.y+processItemBounds.height/2);
            if(commentBounds.height <= 46){
               leftPoint = new Point(commentBounds.x+commentBounds.width-1, commentBounds.y+8);
               rightPoint = new Point(commentBounds.x+commentBounds.width-1, commentBounds.y+commentBounds.height-9);
            }else{
               leftPoint = new Point(commentBounds.x+commentBounds.width-1, commentBounds.y+8);
               rightPoint = new Point(commentBounds.x+commentBounds.width-1, commentBounds.y+38);
            }
         break;

         case EAST_CORNER:
            basePoint = new Point(processItemBounds.x + processItemBounds.width, processItemBounds.y + processItemBounds.height / 2);
            if (commentBounds.height <= 46) {
               leftPoint = new Point(commentBounds.x+1, commentBounds.y + 8);
               rightPoint = new Point(commentBounds.x+1, commentBounds.y + commentBounds.height - 9);
            } else {
               leftPoint = new Point(commentBounds.x+1, commentBounds.y + 8);
               rightPoint = new Point(commentBounds.x+1, commentBounds.y + 38);
            }
         break;

         case NE_CORNER:
            basePoint = new Point(processItemBounds.x + processItemBounds.width, processItemBounds.y + processItemBounds.height / 2);
            leftPoint = new Point(commentBounds.x+30, commentBounds.y+commentBounds.height-1);
            rightPoint = new Point(commentBounds.x+60, commentBounds.y+commentBounds.height-1);
         break;

         case NW_CORNER:
            basePoint = new Point(processItemBounds.x, processItemBounds.y+processItemBounds.height/2);
            leftPoint = new Point(commentBounds.x+30, commentBounds.y+commentBounds.height-1);
            rightPoint = new Point(commentBounds.x+60, commentBounds.y+commentBounds.height-1);
         break;

         case SE_CORNER:
            basePoint = new Point(processItemBounds.x + processItemBounds.width, processItemBounds.y + processItemBounds.height / 2);
            leftPoint = new Point(commentBounds.x+30, commentBounds.y);
            rightPoint = new Point(commentBounds.x+60, commentBounds.y);
         break;

         case SW_CORNER:
            basePoint = new Point(processItemBounds.x, processItemBounds.y+processItemBounds.height/2);
            leftPoint = new Point(commentBounds.x+30, commentBounds.y);
            rightPoint = new Point(commentBounds.x+60, commentBounds.y);
         break;
      }
   }
   
   public void panelRedraw(){
   	if(cornerType == INCORRECT_CORNER) return;
   	 int minX, minY, maxX, maxY,width,height;

	minX = basePoint.x-1;
	minY = basePoint.y-1;
	maxX = basePoint.x+1;
	maxY = basePoint.y+1;
	
	if(leftPoint.x-1 < minX)
	   minX = leftPoint.x-1;
	if(leftPoint.y-1 < minY)
	   minY = leftPoint.y-1;
	if(leftPoint.x+1 > maxX)
	   maxX = leftPoint.x+1;
	if(leftPoint.y+1 > maxY)
	   maxY = leftPoint.y+1;
	   
	if(rightPoint.x-1 < minX)
	   minX = rightPoint.x-1;
	if(rightPoint.y-1 < minY)
	   minY = rightPoint.y-1;
	if(rightPoint.x+1 > maxX)
	   maxX = rightPoint.x+1;
	if(rightPoint.y+1 > maxY)
	   maxY = rightPoint.y+1;
	
   	 width = maxX-minX;
   	 height = maxY-minY;
   	 setBounds(new Rectangle(minX,minY,width,height));
   	 repaint();
   }

   public void figureMoved(IFigure source) {
        commentBounds = commentView.getBounds();
        processItemBounds = processItemView.getBounds();
        
        calculate();
	    panelRedraw();
	    commentView.repaint();
   }
   
	class CommentCornerBorder extends LineBorder{
		public CommentCornerBorder(Color color) {
			super(color);
		}
		
		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			if(cornerType == INCORRECT_CORNER) return;
			
			graphics.setForegroundColor(FOREGROUND_COLOR);
		     
			graphics.drawLine(basePoint, rightPoint);
			graphics.drawLine(basePoint, leftPoint);
		}
	}
}