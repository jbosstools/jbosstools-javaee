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

import java.util.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultEditHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;

public class AddEntrySupport extends SpecialWizardSupport {
	boolean isEditing;
	boolean isProperty;
	
	public boolean isEnabled(XModelObject target) {
		if(!super.isEnabled(target)) return false;
		isProperty = "JSFManagedProperty".equals(target.getModelEntity().getName()); //$NON-NLS-1$
		if(isProperty) {
			String toKind = action.getProperty("value-kind"); //$NON-NLS-1$
			return ChangeValueKindHandler.isNewValueKind(target, toKind);
		}
		return true;
	}

	public void reset() {
		isEditing = getTarget().getModelEntity() == getEntityData()[0].getModelEntity();
		isProperty = "JSFManagedProperty".equals(getTarget().getModelEntity().getName()); //$NON-NLS-1$
		if(isEditing) {
			setAttributeDataByObject(0, getTarget());
		} else if(isProperty) {
			if(!ChangeValueKindHandler.checkChangeSignificance(getTarget())) {
				setFinished(true);
			}
		}
	}
	
	public void action(String name) throws XModelException {
		if(FINISH.equals(name)) {
			execute();
			setFinished(true);
		} else if(CANCEL.equals(name)) {
			setFinished(true);
		} else if(HELP.equals(name)) {
			help();
		}
	}

	public String[] getActionNames(int stepId) {
		return new String[]{FINISH, CANCEL, HELP};
	}
	
	void execute() throws XModelException {
		Properties p = extractStepData(0);
		if(isProperty) {
			String kind = action.getProperty("value-kind"); //$NON-NLS-1$
			getTarget().getModel().changeObjectAttribute(getTarget(), "value-kind", kind); //$NON-NLS-1$
			String entity = getEntityData()[0].getModelEntity().getName();
			XModelObject o = getTarget().getModel().createModelObject(entity, p);
			DefaultCreateHandler.addCreatedObject(getTarget().getChildByPath("Entries"), o, getProperties()); //$NON-NLS-1$
		} else if(!isEditing) {
			String entity = getEntityData()[0].getModelEntity().getName();
			XModelObject o = getTarget().getModel().createModelObject(entity, p);
			DefaultCreateHandler.addCreatedObject(getTarget(), o, getProperties());
		} else {
			DefaultEditHandler.edit(getTarget(), p);
		}
		/*TRIAL_JSF*/
	}

	public boolean isFieldEditorEnabled(int stepId, String name, Properties values) {
		if(isEditing && !getTarget().isObjectEditable()) return false;
		boolean isNullValue = "true".equals(values.getProperty("null-value")); //$NON-NLS-1$ //$NON-NLS-2$
		if(name.equals("value")) return !isNullValue; //$NON-NLS-1$
		return true;
	}
	/*TRIAL_JSF_CLASS*/
}
