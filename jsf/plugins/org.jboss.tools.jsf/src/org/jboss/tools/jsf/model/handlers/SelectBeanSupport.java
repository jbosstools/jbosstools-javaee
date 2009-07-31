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
package org.jboss.tools.jsf.model.handlers;

import java.util.Properties;

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.meta.action.XEntityData;
import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.meta.action.impl.XEntityDataImpl;
import org.jboss.tools.common.model.ServiceDialog;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.pv.JSFProjectBean;

public class SelectBeanSupport extends SpecialWizardSupport {
	static String ATTR_SELECTED = "managed-bean-name"; //$NON-NLS-1$
	
	public static String run(JSFProjectBean bean) {
		ServiceDialog d = bean.getModel().getService();
		SelectBeanSupport support = new SelectBeanSupport();
		XEntityData data = XEntityDataImpl.create(new String[][]{
			{"JSFSelectBeanWizard"}, {ATTR_SELECTED, "yes"} //$NON-NLS-1$ //$NON-NLS-2$
		});
		Properties p = new Properties();
		support.setActionData(null, new XEntityData[]{data}, bean, p);
		support.reset();
		d.showDialog(support);
		return support.getProperties().getProperty(ATTR_SELECTED);		
	}
	
	JSFProjectBean bean;
	
	public void reset() {
		bean = (JSFProjectBean)getTarget();
		XModelObject[] bs = bean.getBeanList();
		String[] s = new String[bs.length];
		for (int i = 0; i < s.length; i++) {
			s[i] = bs[i].getAttributeValue("managed-bean-name"); //$NON-NLS-1$
		}
		setValueList(0, ATTR_SELECTED, s);
		if(s.length > 0) setAttributeValue(0, ATTR_SELECTED, s[0]);
	}

	public String getTitle() {
		return JSFUIMessages.PASTE;
	}

	public String getSubtitle() {
		return JSFUIMessages.SELECT_BEAN;
	}

    public String getMessage(int stepId) {
        return NLS.bind(JSFUIMessages.CLASS_IS_REFERENCED_BY_SEVERAL_BEANS, bean.getAttributeValue("class name")); //$NON-NLS-1$
    }

	public String[] getActionNames(int stepId) {
		return new String[]{FINISH, CANCEL, HELP};
	}

	public void action(String name) throws XModelException {
		if(FINISH.equals(name)) {
			Properties p = extractStepData(0);
			getProperties().setProperty(ATTR_SELECTED, p.getProperty(ATTR_SELECTED));
			setFinished(true);
		} else if(CANCEL.equals(name)) {
			getProperties().remove(ATTR_SELECTED);
			setFinished(true);
		}
	}

}
