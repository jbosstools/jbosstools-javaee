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
package org.jboss.tools.seam.ui.pages.editor.edit;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SelectionUtil {

	/**
	 * Returns array of XModelObject objects associated with selection 
	 * containing PagesEditPart objects.
	 * @param ss
	 * @return
	 */
	public static XModelObject[] getTargets(IStructuredSelection ss) {
		if(ss.size() < 2) return null;
		Iterator it = ss.iterator();
		ArrayList<XModelObject> l = new ArrayList<XModelObject>();
		while(it.hasNext()) {
			XModelObject o = getTarget(it.next());
			if(o != null) l.add(o);		
		}
		return l.toArray(new XModelObject[0]);
	}
	
	/**
	 * Returns XModelObject associated with PagesEditPart
	 * @param selected
	 * @return
	 */
	public static XModelObject getTarget(Object selected) {
		if(selected instanceof ParamEditPart) {
			ParamEditPart part = (ParamEditPart)selected;
			Object partModel = part.getParamModel().getParent();
			if(partModel instanceof PagesElement) {
				XModelObject o = (XModelObject)((PagesElement)partModel).getData();
				if(o instanceof ReferenceObject) {
					XModelObject p = ((ReferenceObject)o).getReference();
					if(p != null) {
						String name = part.getParamModel().getName();
						XModelObject[] cs = p.getChildren();
						for (int i = 0; i < cs.length; i++) {
							if(name.equals(cs[i].getAttributeValue(SeamPagesConstants.ATTR_NAME))) {
								return cs[i];
							}
						}
						
					}
				}
				return o;
			}
		}
		if(selected instanceof PagesEditPart) {
			PagesEditPart part = (PagesEditPart)selected;
			Object partModel = part.getModel();
			if(partModel instanceof PagesElement) {
				return (XModelObject)((PagesElement)partModel).getData();
			}
		}
		if(selected instanceof LinkEditPart) {
			LinkEditPart part = (LinkEditPart)selected;
			Object partModel = part.getModel();
			if(partModel instanceof Link) {
				return (XModelObject)((Link)partModel).getData();
			}
		}

		return null;
	}

}
