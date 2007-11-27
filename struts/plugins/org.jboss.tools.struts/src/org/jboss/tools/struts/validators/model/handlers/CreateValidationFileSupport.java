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
package org.jboss.tools.struts.validators.model.handlers;

import java.util.Properties;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.files.handlers.CreateFileSupport;
import org.jboss.tools.common.model.undo.XTransactionUndo;
import org.jboss.tools.common.model.undo.XUndoManager;
import org.jboss.tools.struts.validators.model.ValidatorConstants;
import org.jboss.tools.struts.validators.model.XModelEntityResolver;
import org.jboss.tools.struts.validators.model.helpers.ValidatorRegistrationHelper;

public class CreateValidationFileSupport extends CreateFileSupport {
	static String REGISTER = "register";

	public void reset() {
		super.reset();
//		setAttributeValue(0, "register", (canRegisterInternal()) ? "yes" : "no");
	}
	
	protected void execute() throws Exception {
		Properties p0 = extractStepData(0);
		XUndoManager undo = getTarget().getModel().getUndoManager();
		XTransactionUndo u = new XTransactionUndo("Create tiles " + getTarget().getAttributeValue("element type")+" "+getTarget().getPresentationString(), XTransactionUndo.ADD);
		undo.addUndoable(u);
		try {
			doExecute(p0);
		} catch (RuntimeException e) {
			undo.rollbackTransactionInProgress();
			throw e;
		} finally {
			u.commit();
		}
	}
	
	private void doExecute(Properties p0) throws Exception {
		Properties p = extractStepData(0);
		String path = p.getProperty("name");
		path = revalidatePath(path);
		XModelObject file = createFile(path);
		if(file == null) return;
		
		if(canRegisterInternal() 
//				&& "yes".equals(getAttributeValue(0, REGISTER))
				) {
			registerInternal(file);
		}

		final XModelObject q = file;
		open(q);	
	}
	
    protected XModelObject modifyCreatedObject(XModelObject o) {
    	String entity = XModelEntityResolver.resolveEntity(o, ValidatorConstants.ENT_FORMSET);
        XModelObject formset = o.getModel().createModelObject(entity, null);
        o.addChild(formset);
        return o;
    }

	private boolean canRegisterInternal() {
		return ValidatorRegistrationHelper.getInstance().isEnabled(getTarget().getModel());
	}

    public boolean isFieldEditorEnabled(int stepId, String name, Properties values) {
    	if(REGISTER.equals(name)) return canRegisterInternal();
    	return true;
    }
    
    void registerInternal(XModelObject file) {
    	ValidatorRegistrationHelper.getInstance().register(getTarget().getModel(), file);
    }
    
}

