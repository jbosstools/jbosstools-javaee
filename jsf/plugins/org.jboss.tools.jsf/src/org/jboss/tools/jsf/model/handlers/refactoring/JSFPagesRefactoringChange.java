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
package org.jboss.tools.jsf.model.handlers.refactoring;

import java.util.*;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.refactoring.RefactoringHelper;
import org.jboss.tools.jsf.JSFModelPlugin;

public class JSFPagesRefactoringChange extends CompositeChange {
	protected String newName;
	Properties replacements;
	protected XModel model;
	
	public JSFPagesRefactoringChange(XModel model, String newName, Properties replacements) {
		super("JSP refactoring");
		this.model = model;
		this.newName = newName;
		this.replacements = replacements;
		try {
			addChanges();
		} catch (Exception e) {
			JSFModelPlugin.log(e);
		}
	}
	
	public XModel getModel() {
		return model;
	}
	
	private void addChanges() throws Exception {
		if(model == null) return;
		XModelObject webRoot = FileSystemsHelper.getWebRoot(model);
		if(webRoot == null) return;
		addChanges(webRoot.getChildren());
	}
	
	private void addChanges(XModelObject[] objects) {
		if(replacements.size() == 0) return;
		for (int i = 0; i < objects.length; i++) {
			if(objects[i].getFileType() == XModelObject.FOLDER) {
				addChanges(objects[i].getChildren());
			} else {
				String entity = objects[i].getModelEntity().getName();
				if(!"FileJSP".equals(entity)) continue;
				RefactoringHelper.addChanges(objects[i], replacements, this);
			}
		}
	}

}
