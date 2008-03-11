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
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.files.handlers.CreateFileSupport;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.undo.*;
import org.jboss.tools.jsf.model.*;
import org.jboss.tools.jsf.web.JSFWebHelper;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;

public class CreateFacesConfigSupport extends CreateFileSupport implements JSFConstants {
	static String REGISTER = "register in web.xml";
	
	public void reset() {
		super.reset();
		initDefaultName();
		initRegister();
	}
	
	void initDefaultName() {
		XModelObject[] cs = getFacesConfigs(getTarget());
		Set<String> names = new HashSet<String>();
		for (int i = 0; i < cs.length; i++) {
			names.add(cs[i].getAttributeValue("name"));
		}
		if(cs.length == 0) {
			setAttributeValue(0, "name", "faces-config");
			return;
		}
		String name = "faces-config", namef = name;
		int i = 0;
		while(names.contains(namef)) namef = name + "-" + (++i);
		setAttributeValue(0, "name", namef);
	}
	
	void initRegister() {
		if(checkRegister(getTarget(), getAttributeValue(0, REGISTER)) != null) {
			setAttributeValue(0, REGISTER, "no");
		}
	}

	protected void execute() throws Exception {
		Properties p0 = extractStepData(0);
		XUndoManager undo = getTarget().getModel().getUndoManager();
		XTransactionUndo u = new XTransactionUndo("Create faces config in " + getTarget().getAttributeValue("element type")+" "+getTarget().getPresentationString(), XTransactionUndo.ADD);
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

		FacesProcessImpl process = (FacesProcessImpl)file.getChildByPath("process");
		process.firePrepared();

		register(file.getParent(), file, p0);

		open(file);	
	}

	private void register(XModelObject object, XModelObject created, Properties prop) throws Exception {
		boolean register = "yes".equals(getAttributeValue(0, "register in web.xml"));
		if(!register) return;
		String uri = getURI(created);
		JSFWebHelper.registerFacesConfig(created.getModel(), uri);
	}
	
	private String getURI(XModelObject file) {
		String result = "/" + FileAnyImpl.toFileName(file);
		XModelObject o = file.getParent();
		while(o != null && o.getFileType() != XModelObject.SYSTEM) {
			result = "/" + o.getAttributeValue("name") + result;
			o = o.getParent();
		}
		if(o == null || !"WEB-ROOT".equals(o.getAttributeValue("name"))) {
			result = "/WEB-INF" + result;
		}
		return result;
	}

	protected DefaultWizardDataValidator createValidator() {
		return new CreateFacesConfigValidator(); 
	}
	
	class CreateFacesConfigValidator extends CreateFileSupport.Validator {
		public void validate(Properties data) {
			super.validate(data);
			if(message != null) return;
			message = checkRegister(getTarget(), data.getProperty("register in web.xml"));
		}
	}

	private String checkRegister(XModelObject object, String register) {
		if(!"yes".equals(register)) return null;
		XModelObject webxml = WebAppHelper.getWebApp(object.getModel());
		if(webxml == null) return "Faces config cannot be registered because web.xml is not found.";
		if("yes".equals(webxml.get("isIncorrect"))) return "Faces config file cannot be registered because web.xml is incorrect.";
		if(!webxml.isObjectEditable()) return "Faces config file cannot be registered because web.xml is read only.";
		return null;
	}

	public static XModelObject[] getFacesConfigs(XModelObject folder) {
		XModelObject[] cs = folder.getChildren();
		List<XModelObject> list = new ArrayList<XModelObject>();
		for (int i = 0; i < cs.length; i++) {
			if(cs[i].getModelEntity().getName().startsWith(ENT_FACESCONFIG)) list.add(cs[i]);
		}
		return list.toArray(new XModelObject[0]);
	}

}
