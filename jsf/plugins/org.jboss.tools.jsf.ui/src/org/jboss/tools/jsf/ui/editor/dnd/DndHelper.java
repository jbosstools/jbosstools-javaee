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
package org.jboss.tools.jsf.ui.editor.dnd;

import java.util.Properties;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.XModelTransferBuffer;

import org.eclipse.draw2d.geometry.Point;
import org.jboss.tools.common.model.ui.dnd.DnDUtil;
import org.jboss.tools.common.meta.action.XAction;


public class DndHelper{

	public DndHelper() {
	}

	public static boolean drag(Object source) {
		if (source == null)
			return false;
		XAction copy = (XAction) DnDUtil.getEnabledAction(
				(XModelObject) source, null, "CopyActions.Copy");
		if (copy == null)
			return false;
		XModelTransferBuffer.getInstance().enable();
		Properties properties = new Properties();
		properties.setProperty("isDrop", "true");
		properties.setProperty("isDrag", "true");
		properties.setProperty("actionSourceGUIComponentID", "editor");
		try {
			copy.executeHandler((XModelObject) source, properties);
		} catch (Exception e) {
			XModelTransferBuffer.getInstance().disable();
			return false;
		}
		return true;
	}
	
	public static void dragEnd() {
		XModelTransferBuffer.getInstance().disable();
	}

	public static void drop(Object target, Point point) {
		if (target == null)
			return;
		Properties properties = new Properties();
		properties.setProperty("isDrop", "true");
		properties.setProperty("actionSourceGUIComponentID", "editor");
		if (point != null) {
			properties.put("process.mouse.x", "" + point.x);
			properties.put("process.mouse.y", "" + point.y);
		}
		try {
			DnDUtil.paste((XModelObject) target, properties);
		} catch (Exception ex) {
			//ignore
		}
	}

	public static void drop(Object target) {
		drop(target, null);
	}

	public static boolean isDropEnabled(Object target) {
		return DnDUtil.isPasteEnabled((XModelObject) target);
	}

}