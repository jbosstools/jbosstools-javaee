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
package org.jboss.tools.struts.ui.adapter;

import org.jboss.tools.common.model.ui.attribute.IListContentProvider;
import org.jboss.tools.common.model.ui.attribute.adapter.DefaultComboBoxValueAdapter;
import org.eclipse.jface.viewers.Viewer;
import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;

public class ActionNameListAdapter extends DefaultComboBoxValueAdapter implements IListContentProvider {
	
	protected IListContentProvider createListContentProvider(XAttribute attribute) {
		return this;	
	}

	public Object[] getElements(Object inputElement) {
		XModelObject o = (modelObject == null) ? null : StrutsProcessStructureHelper.instance.getParentFile(modelObject);
		XModelObject[] os = (o == null) ? new XModelObject[0] : o.getChildByPath("form-beans").getChildren();
		String[] vs = new String[os.length];
		for (int i =0; i < os.length; i++) vs[i] = os[i].getAttributeValue("name");		
		return vs;
	}

	public void dispose() {}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}	

}
