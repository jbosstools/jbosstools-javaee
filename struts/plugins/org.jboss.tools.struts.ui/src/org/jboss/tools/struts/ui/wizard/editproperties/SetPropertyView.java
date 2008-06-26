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
package org.jboss.tools.struts.ui.wizard.editproperties;

import org.jboss.tools.common.model.ui.objecteditor.XChildrenEditor;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.struts.model.helpers.StrutsEditPropertiesContext;

public class SetPropertyView extends XChildrenEditor {
	protected StrutsEditPropertiesContext context;
	
	public void dispose() {
		super.dispose();
		if (context!=null) context.dispose();
		context = null;
	}

	public SetPropertyView() {
		setMnemonicEnabled(true);
	}
	
	protected AbstractTableHelper createHelper() {
		return new SetPropertyTableHelper();
	}

	public boolean isEnabled() {
		XModelObject o = helper.getModelObject();
		return (o != null && o.getModelEntity().getActionList().getAction("CreateActions.CreateSetProperty") != null);
	}

	public void setContext(StrutsEditPropertiesContext context) {
		this.context = context;
		helper.setModelObject(context.getObject());
//		tablemodel.setEditable((context.getObject() != null && context.getObject().isObjectEditable()));
//		tablemodel.fireTableDataChanged();
	}

	protected String getAddActionPath() {
		return "CreateActions.CreateSetProperty";
	}

}

class SetPropertyTableHelper extends AbstractTableHelper {
	public String[] header = new String[]{"property", "value"};

	public String[] getHeader() {
		return header;
	}

	XModelObject o;
	String entity = null;

	public int size() {
		updateEntity();
		return (object == null) ? 0 : object.getChildren(entity).length;
	}

	public XModelObject getModelObject(int r) {
		if(object == null) return null;
		updateEntity();
		XModelObject[] cs = object.getChildren(entity);
		return (r < 0 || r >= cs.length) ? null : cs[r];
	}

	private void updateEntity() {
		if(object == o || object == null) return;
		entity = object.getModelEntity().getChildren()[0].getName();
		o = object;
	}

}
