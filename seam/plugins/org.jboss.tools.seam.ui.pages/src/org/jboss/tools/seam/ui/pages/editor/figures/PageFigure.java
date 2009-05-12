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
package org.jboss.tools.seam.ui.pages.editor.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.seam.pages.xml.model.SeamPagesPreference;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;
import org.jboss.tools.seam.ui.pages.editor.PagesEditor;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;
import org.jboss.tools.seam.ui.pages.editor.edit.PageEditPart;
import org.jboss.tools.seam.ui.pages.editor.figures.xpl.FixedConnectionAnchor;

public class PageFigure extends NodeFigure implements HandleBounds{
	private static final Dimension SIZE = new Dimension(56, 100);
	
	private static final Color backgroundColor = new Color(null, 0xff, 0xf7, 0xcb);
	
	private static final Color foregroundColor = new Color(null, 0x9d, 0x96, 0x24);
	
	private static final Color greyBackground = new Color(null, 0xf1, 0xf1, 0xf1);
	
	private static final Color greyForeground = new Color(null, 0x99, 0x95, 0x99);
	
	private static final Color borderColor = new Color(null, 0xad, 0xa9, 0xad);
	
	private static final Image pageImage = ImageDescriptor.createFromFile(
			PagesEditor.class, "icons/ico_page.png").createImage();
	
	private static final Image plusImage = ImageDescriptor.createFromFile(
			PagesEditor.class, "icons/ico_plus.png").createImage();

	private static final Image minusImage = ImageDescriptor.createFromFile(
			PagesEditor.class, "icons/ico_minus.png").createImage();
	
	private static final Image crossImage = ImageDescriptor.createFromFile(
			PagesEditor.class, "icons/ico_cross.gif").createImage();

	public Page page;

	String path;

	PageEditPart editPart;

	public void setPageEditPart(PageEditPart part) {
		editPart = part;
	}

	public void setConstraint(IFigure child, Object constraint) {
		super.setConstraint(child, constraint);
	}

	public void setIcon(Image i) {
		//icon = PrintIconHelper.getPrintImage(i);
	}

		
	public PageFigure(Page page) {
		this.page = page;

		if (page != null && page.getData() != null) {
			setIcon(page.getImage());
			initConnectionAnchors(page.getOutputLinks().size());
		}

		setOpaque(false);
		setLayoutManager(new XYLayout());

		setBorder(new PageBorder(ColorConstants.black));

		if (page != null) {
			FixedConnectionAnchor c;
			c = new FixedConnectionAnchor(this);
			c.offsetV = 10;
			c.offsetH = -8;
			connectionAnchors.put("1_IN", c);
			inputConnectionAnchors.addElement(c);

		}
	}
	public ConnectionAnchor getConnectionAnchor(String terminal) {
		ConnectionAnchor anchor = (ConnectionAnchor)connectionAnchors.get(terminal);
		while(anchor == null){
			addConnectionAnchor(outputConnectionAnchors.size());
			anchor = (ConnectionAnchor)connectionAnchors.get(terminal);
		}
		return anchor;
	}
	/**
	 * @see org.eclipse.gef.handles.HandleBounds#getHandleBounds()
	 */
	public Rectangle getHandleBounds() {
		return getBounds().getCropped(new Insets(0, 0, 0, 0));
	}

	/**
	 * @see org.eclipse.draw2d.Figure#getPreferredSize(int, int)
	 */
	public Dimension getPreferredSize(int wHint, int hHint) {
		return SIZE;
	}

	//int width, height;


	/**
	 * @see org.eclipse.draw2d.Figure#paintFigure(Graphics)
	 */
	protected void paintFigure(Graphics g) {
		Rectangle r = getBounds().getCopy();
		g.translate(r.getLocation());

		int height = r.height - 1;
		
		if (page != null) {
			if(page.isConfirmed()){
				g.setBackgroundColor(backgroundColor);
				g.setForegroundColor(foregroundColor);
			}else{
				g.setBackgroundColor(greyBackground);
				g.setForegroundColor(greyForeground);
			}
		} else{
			g.setBackgroundColor(greyBackground);
			g.setForegroundColor(greyForeground);
		}
		
		Rectangle boundingRect = new Rectangle(1, 1, r.width, r.height);
		
		g.fillRectangle(boundingRect);

		// drawIcon
		g.drawImage(pageImage, 1, 1);
		
		if(page != null && page.getData() != null && SeamPagesDiagramStructureHelper.instance.isUnconfirmedPage((XModelObject)page.getData())){
			g.drawImage(crossImage, getInsetX()-1, getInsetY()+4);
		}
		
		if(page != null && page.getName() != null){
			g.setFont(nodeLabelFont);
			g.drawString(page.getName(), 27, 3);			
		}
		
		if(!page.getChildren().isEmpty()){
			if(page.isParamsVisible()){
				g.drawImage(minusImage, 4, height-12);
			}else{
				g.drawImage(plusImage, 4, height-12);
			}
		}
	}

	/** distance from e.g. icon to border Y-axis*/
	private int getInsetY() {
		return 2;
	}

	/** distance from e.g. icon to border X-axis*/
	private int getInsetX() {
		return 4;
	}

	/** the one drawing the "bend corner rectangle" **/
	class PageBorder extends LineBorder {
		public PageBorder(Color color) {
			super(color);
		}

		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			Rectangle r = getPaintRectangle(figure, insets).getCopy();
			graphics.translate(r.getLocation());
			int width = r.width - 1;
			int height = r.height - 1;
			
			if (page != null)
				graphics.setForegroundColor(foregroundColor);
			else
				graphics.setForegroundColor(greyForeground);
			
			// if page has not page element
			if(page != null && !page.isConfirmed()){
				graphics.setLineDash(new int[]{3,3});
				graphics.setLineStyle(SWT.LINE_CUSTOM);
				graphics.setForegroundColor(borderColor);
			}

			graphics.drawLine(1, 0, width-1, 0);
			graphics.drawLine(0, 1, 0, height - 1);
			graphics.drawLine(1, height, width-1, height);
			graphics.drawLine(width, 1, width, height - 1);
		}
	}
}