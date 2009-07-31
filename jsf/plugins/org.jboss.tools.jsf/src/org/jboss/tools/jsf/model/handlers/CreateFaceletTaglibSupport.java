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

import java.text.MessageFormat;
import java.util.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.files.handlers.CreateFileSupport;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.undo.*;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.*;
import org.jboss.tools.jsf.web.JSFWebHelper;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;

public class CreateFaceletTaglibSupport extends CreateFileSupport implements JSFConstants {
	static String REGISTER = "register in web.xml"; //$NON-NLS-1$
	
	public void reset() {
		super.reset();
		initDefaultName();
		initRegister();
	}
	
	void initDefaultName() {
		XModelObject[] cs = getFaceletsTaglibs(getTarget());
		Set<String> names = new HashSet<String>();
		for (int i = 0; i < cs.length; i++) {
			names.add(cs[i].getAttributeValue("name")); //$NON-NLS-1$
		}
		if(cs.length == 0) {
			setAttributeValue(0, "name", "facelets-taglib"); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		String name = "facelets-taglib", namef = name; //$NON-NLS-1$
		int i = 0;
		while(names.contains(namef)) namef = name + "-" + (++i); //$NON-NLS-1$
		setAttributeValue(0, "name", namef); //$NON-NLS-1$
	}
	
	void initRegister() {
		if(checkRegister(getTarget(), getAttributeValue(0, REGISTER)) != null) {
			setAttributeValue(0, REGISTER, "no"); //$NON-NLS-1$
		}
	}

	protected void execute() throws XModelException {
		Properties p0 = extractStepData(0);
		XUndoManager undo = getTarget().getModel().getUndoManager();
		XTransactionUndo u = new XTransactionUndo(MessageFormat.format(JSFUIMessages.CreateFaceletTaglibSupport_CreateFaceletsTaglib, getTarget().getAttributeValue("element type"), //$NON-NLS-1$
				getTarget().getPresentationString()), XTransactionUndo.ADD);
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
	
	private void doExecute(Properties p0) throws XModelException {
		Properties p = extractStepData(0);
		String path = p.getProperty("name"); //$NON-NLS-1$
		path = revalidatePath(path);
		XModelObject file = createFile(path);
		if(file == null) return;		

		register(file.getParent(), file, p0);

		open(file);	
	}

	private void register(XModelObject object, XModelObject created, Properties prop) throws XModelException {
		boolean register = "yes".equals(getAttributeValue(0, "register in web.xml")); //$NON-NLS-1$ //$NON-NLS-2$
		if(!register) return;
		String uri = getURI(created);
		JSFWebHelper.registerFaceletsTaglib(created.getModel(), uri);
	}
	
	private String getURI(XModelObject file) {
		String result = "/" + FileAnyImpl.toFileName(file); //$NON-NLS-1$
		XModelObject o = file.getParent();
		while(o != null && o.getFileType() != XModelObject.SYSTEM) {
			result = "/" + o.getAttributeValue("name") + result; //$NON-NLS-1$ //$NON-NLS-2$
			o = o.getParent();
		}
		if(o == null || !"WEB-ROOT".equals(o.getAttributeValue("name"))) { //$NON-NLS-1$ //$NON-NLS-2$
			//TODO resolve to webRoot
//			result = "/WEB-INF" + result;
		}
		return result;
	}

	protected DefaultWizardDataValidator createValidator() {
		return new CreateFaceletsTaglibValidator(); 
	}
	
	class CreateFaceletsTaglibValidator extends CreateFileSupport.Validator {
		public void validate(Properties data) {
			super.validate(data);
			if(message != null) return;
			message = checkRegister(getTarget(), data.getProperty("register in web.xml")); //$NON-NLS-1$
		}
	}

	private String checkRegister(XModelObject object, String register) {
		if(!"yes".equals(register)) return null; //$NON-NLS-1$
		XModelObject webxml = WebAppHelper.getWebApp(object.getModel());
		if(webxml == null) return JSFUIMessages.CreateFaceletTaglibSupport_WebXMLNotFound;
		if("yes".equals(webxml.get("isIncorrect"))) return JSFUIMessages.CreateFaceletTaglibSupport_WebXMLIncorrect; //$NON-NLS-1$ //$NON-NLS-2$
		if(!webxml.isObjectEditable()) return JSFUIMessages.CreateFaceletTaglibSupport_WebXMLReadOnly;
		return null;
	}

	public static XModelObject[] getFaceletsTaglibs(XModelObject folder) {
		XModelObject[] cs = folder.getChildren();
		List<XModelObject> list = new ArrayList<XModelObject>();
		for (int i = 0; i < cs.length; i++) {
			if(cs[i].getModelEntity().getName().startsWith("FileFaceletTaglib")) list.add(cs[i]); //$NON-NLS-1$
		}
		return list.toArray(new XModelObject[0]);
	}

}
