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
package org.jboss.tools.struts.validator.ui;

import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.ui.action.*;

public abstract class ValidatorAttributeEditor implements CommandBarListener {
	public static int LABEL_WIDTH = 110;
	public static int BUTTON_WIDTH = 70;
	protected XModelObject object;
	protected Composite control;
	protected String name;
	protected String[] displayNames;
	protected String [] commands;
	protected CommandBar bar = new CommandBar();		
		
	/**
	 * 
	 * @param name (non-translatable)
	 * @param displayName (translatable)
	 * @param command (non-translatable)
	 */
	public ValidatorAttributeEditor(String name, String displayName, String command) {
		this(name, new String[]{displayName}, new String[]{command});
	}

	/**
	 * 
	 * @param name (non-translatable)
	 * @param displayNames (translatable)
	 * @param commands (non-translatable)
	 */
	public ValidatorAttributeEditor(String name, String[] displayNames, String[] commands) {
		this.name = name;
		this.displayNames = displayNames;
		this.commands = commands;
		bar.getLayout().buttonWidth = getButtonWidth();
		bar.getLayout().setMargins(0, 5, 0, 5);
		bar.setCommands(displayNames);
		bar.addCommandBarListener(this);
	}

	public void dispose() {
		if (bar!=null) bar.dispose();
		bar = null;
	}
	
	protected int getButtonWidth() {
		return BUTTON_WIDTH;
	}
	
	public void setObject(XModelObject object) {
		if(this.object == object) return;
		this.object = object;
	}
	
	public String getAttributeName() {
		return name; 
	}
	
	public XModelObject getModelObject() {
		return object;
	}
	    
	public abstract Control createControl(Composite parent);
		
	public Control getControl() {
		return control;
	}
		
	public void load() {}
		
	public void setEnabled(boolean enabled) {}
		
	public void action(String command) {
		invoke(commands[0], object);
	}
	
	protected void invoke(String action, XModelObject o) {
		if(o != null) {
			XActionInvoker.invoke(action, o, null);
		}
	}

}
