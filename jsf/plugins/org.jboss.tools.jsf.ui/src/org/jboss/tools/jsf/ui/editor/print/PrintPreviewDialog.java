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
package org.jboss.tools.jsf.ui.editor.print;


import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.ui.editor.JSFEditor;

public class PrintPreviewDialog extends Dialog{
			String message = ""; //$NON-NLS-1$
			String result = null;
			Shell dialog;
			Text text;
			Display display;
			GraphicalViewer viewer;
			JSFEditor editor;
			Rectangle dialogSize = new Rectangle(0,0,640,480);
			PagesView imageView;
			Pages pages;
			
			public class SliderPanel extends Composite{
				Scale sc;
				Label percent;
				public SliderPanel(Composite parent, int style) {
					super (parent, style);
					GridLayout gl = new GridLayout();
					gl.numColumns = 1;
					this.setLayout(gl);
					percent = new Label(this,SWT.CENTER);
					percent.setText(String.valueOf((int)(PageFormat.printScale*100))+ JSFUIMessages.OF_NORMAL_SIZE);
					GridData gd = new GridData();
					gd.horizontalAlignment = GridData.CENTER;
					percent.setLayoutData(gd);
					gd = new GridData();
					gd.horizontalAlignment = GridData.CENTER;
					sc = new Scale(this,SWT.HORIZONTAL);
					sc.setMinimum(30);
					sc.setMaximum(300);
					sc.setIncrement(10);
					sc.setSelection((int)(pages.getScale()*100));
					sc.setLayoutData(gd);
					sc.addSelectionListener(new SelectionListener(){
						public void widgetSelected(SelectionEvent e){
							percent.setText(String.valueOf(sc.getSelection())+JSFUIMessages.OF_NORMAL_SIZE);
							percent.redraw();
							pages.setScale((double)sc.getSelection()/100);
							PageFormat.printScale = (double)sc.getSelection()/100;
						}
						public void widgetDefaultSelected(SelectionEvent e){
						}
					});
				}
			}
			
			public class ControlPanel extends Composite implements PaintListener{
				Button all;
				SliderPanel sp;
				Button selectPage;
				Button selectAll;
				Button unselectAll;
				SelectionListener allListener;
				SelectionListener selectListener;
				SelectionListener selectAllListener;
				SelectionListener unSelectAllListener;
				  
				public ControlPanel(Composite parent, int style) {
					super (parent, style);
					GridLayout gl = new GridLayout();
					gl.numColumns = 2;
					gl.verticalSpacing = -12;
					this.setLayout(gl);
					GridData data = new GridData();
					data.grabExcessHorizontalSpace=true;
					data.horizontalAlignment = GridData.BEGINNING;
					data.horizontalIndent = 8;
					Label print = new Label(this,SWT.NONE);
					print.setText(JSFUIMessages.PRINT);
					print.setLayoutData(data);
					data = new GridData();
					data.horizontalAlignment = GridData.BEGINNING;
					data.horizontalIndent = 8;
					Label zoom = new Label(this,SWT.NONE);
					zoom.setText(JSFUIMessages.ZOOM);
					zoom.setLayoutData(data);
					Group gr1 = new Group(this,SWT.SHADOW_ETCHED_IN);
					gl = new GridLayout();
					gl.numColumns = 2;
					gr1.setLayout(gl);
					data = new GridData();
					data.grabExcessHorizontalSpace=true;
					data.horizontalAlignment = GridData.FILL;
					gr1.setLayoutData(data);
					data = new GridData();
					data.horizontalSpan = 2;
					data.horizontalAlignment = GridData.FILL;
					all = new Button(gr1,SWT.RADIO);
					all.setText(JSFUIMessages.ZOOM);
					all.setSelection(true);
					all.setLayoutData(data);
					selectPage = new Button(gr1,SWT.RADIO);
					selectPage.setText(JSFUIMessages.SELECTED_PAGES);
					data = new GridData();
					data.horizontalSpan = 2;
					data.horizontalAlignment = GridData.FILL;
					selectPage.setLayoutData(data);
					selectAll = new Button(gr1,SWT.PUSH);
					selectAll.setText(JSFUIMessages.SELECT_ALL);
					selectAll.setEnabled(false);
					unselectAll = new Button(gr1,SWT.PUSH);
					unselectAll.setText(JSFUIMessages.UNSELECT_ALL);
					unselectAll.setEnabled(false);
					Group gr2 = new Group(this,SWT.SHADOW_ETCHED_IN);
					gl = new GridLayout();
					gl.numColumns = 1;
					gl.marginHeight = 3;
					gl.verticalSpacing = 2;
					gr2.setLayout(gl);
					data = new GridData();
					data.horizontalAlignment = GridData.END;
					gr2.setLayoutData(data);
					sp = new SliderPanel(gr2,SWT.NONE);
					data = new GridData();
					data.horizontalAlignment = GridData.END;
					sp.setLayoutData(data);
					allListener = new SelectionListener(){
						public void widgetSelected(SelectionEvent e){
							pages.selectAll();
							selectAll.setEnabled(false);
							unselectAll.setEnabled(false);
							imageView.setSelectionEnabled(false);
						}
						public void widgetDefaultSelected(SelectionEvent e){}
					};
					all.addSelectionListener(allListener);
					selectListener =  new SelectionListener(){
						public void widgetSelected(SelectionEvent e){
							imageView.setSelectionEnabled(true);
							pages.unSelectAll();
							imageView.redraw();
						}
						public void widgetDefaultSelected(SelectionEvent e){}
						};
					selectPage.addSelectionListener(selectListener);
					PropertyChangeListener pcl = new PropertyChangeListener(){
						public void propertyChange(PropertyChangeEvent e){
						   if(e.getPropertyName().equals("selectAll")){ //$NON-NLS-1$
							  if(!all.getSelection()){
								 selectAll.setEnabled(false);
								 unselectAll.setEnabled(true);
								 imageView.redraw();
							  }
						   }
						   if(e.getPropertyName().equals("unSelectAll")){ //$NON-NLS-1$
							  if(!all.getSelection()){
								 unselectAll.setEnabled(false);
								 selectAll.setEnabled(true);
								 imageView.redraw();
							 }
						   }
						   if(e.getPropertyName().equals("PageSelection")){ //$NON-NLS-1$
							  if(!all.getSelection()){
								 unselectAll.setEnabled(true);
								selectAll.setEnabled(true);
							  }
						   }
						}
					};
					pages.addPropertyChangeListener(pcl);
					selectAllListener =  new SelectionListener(){
						public void widgetSelected(SelectionEvent e){
					   		pages.selectAll();
					    }
				   		public void widgetDefaultSelected(SelectionEvent e){}
					};
					selectAll.addSelectionListener(selectAllListener);
					
					unSelectAllListener =  new SelectionListener(){
						public void widgetSelected(SelectionEvent e){
							pages.unSelectAll();
						}
						public void widgetDefaultSelected(SelectionEvent e){}
					};
					unselectAll.addSelectionListener(unSelectAllListener);
							
				}
				
				
				public void paintControl(PaintEvent pevent){
					GC gc = pevent.gc;
					gc.setForeground(new Color(ControlPanel.this.getDisplay(),0x00,0x00,0x00));
					gc.drawLine(pevent.x,pevent.y,pevent.width,pevent.x);
					gc.drawLine(pevent.x,pevent.y,pevent.x,pevent.height);
					gc.drawLine(pevent.x,pevent.y+pevent.height-2,pevent.x+pevent.width,pevent.y+pevent.height-2);
					gc.drawLine(pevent.x+pevent.width-2,pevent.y+pevent.height,pevent.x+pevent.width-2,pevent.y);
					gc.setForeground(new Color(ControlPanel.this.getDisplay(),0xff,0xff,0xff));
					gc.drawLine(pevent.x+1,pevent.y+1,pevent.width-1,pevent.x+1);
					gc.drawLine(pevent.x+1,pevent.y+1,pevent.x+1,pevent.height-1);
					gc.dispose();
				}
				
			}
			
			public PrintPreviewDialog (Shell parent, int style) {
				super (parent, style);
				display = parent.getDisplay();
				
			}
			public PrintPreviewDialog (Shell parent) {
				this (parent, SWT.APPLICATION_MODAL);
			}
			public String getMessage () {
				return message;
			}
			public void setMessage (String string) {
				message = string;
			}
			public String open () {
				dialog = new Shell(getParent(), getStyle());
				dialog.setText(JSFUIMessages.PRINT_PREVIEW);
				GridLayout rl = new GridLayout();
				dialog.setLayout(rl);
				ControlPanel controlPanel = new ControlPanel(dialog,SWT.RESIZE);
				GridData rd = new GridData();
				rd.widthHint = 550;
				rd.heightHint = 105;
				controlPanel.setLayoutData(rd);
				imageView = new PagesView(pages,new Dimension(550,370),dialog,SWT.RESIZE);
				imageView.setBackground(new Color(dialog.getDisplay(),0xff,0xff,0xff));
				GridData rd1 = new GridData();
				rd1.widthHint = 550;
				rd1.heightHint = 400;
				imageView.setLayoutData(rd1);
				Composite buttons = new Composite(dialog, SWT.END|SWT.RESIZE);
				GridLayout grid = new GridLayout();
				grid.numColumns = 2;
				buttons.setLayout(grid);
				GridData data2 = new GridData(GridData.HORIZONTAL_ALIGN_END);
				buttons.setLayoutData(data2);
				GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END);
				Button ok = new Button(buttons, SWT.PUSH&SWT.MULTI);
				ok.setText(JSFUIMessages.PRINT);
				data = new GridData();
				data.widthHint = 75;
				ok.setLayoutData(data);
				ok.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						result = "ok";
						if(editor.isBordersPaint()){
						  PrintPreviewDialog.this.pages.unSelectAll();
						  PrintPreviewDialog.this.pages.getSourcePage(0).setSelected(true);
						}
						dialog.dispose();
					}
				});
				Button cancel = new Button(buttons, SWT.PUSH);
				cancel.setText(JSFUIMessages.CLOSE);
				data = new GridData(GridData.HORIZONTAL_ALIGN_END);
				data.widthHint = 75;
				cancel.setLayoutData(data);
				cancel.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						result="cancel";
						dialog.dispose();
					}
				});
				dialog.setDefaultButton(ok);
				dialog.pack();
				dialog.open();
				while (!dialog.isDisposed()) {
					if (!display.readAndDispatch()) display.sleep();
				}
				return result;
			}
			
			public void setPrintViewer(GraphicalViewer viewer){
				this.viewer = viewer;
			}

			public void setEditor(JSFEditor editor){
				this.editor = editor;
			}
			
			public GraphicalViewer getPrintViewer(){
				return this.viewer;
			}
			
			public void setPages(Pages pages){
					this.pages = pages;
			}
			
			public Pages getPages(){
					return this.pages;
			}
		
}
