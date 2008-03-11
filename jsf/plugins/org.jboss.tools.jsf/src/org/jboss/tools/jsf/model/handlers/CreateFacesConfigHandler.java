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
import org.jboss.tools.common.meta.action.XEntityData;
import org.jboss.tools.common.meta.action.impl.handlers.HUtil;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.impl.CreateFileHandler;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.undo.*;
import org.jboss.tools.jsf.model.FacesProcessImpl;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jsf.web.JSFWebHelper;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;

public class CreateFacesConfigHandler extends CreateFileHandler implements JSFConstants {
	private XModelObject created = null;

	public XEntityData[] getEntityData(XModelObject object) {
		super.getEntityData(object);
		XModelObject[] cs = CreateFacesConfigSupport.getFacesConfigs(object);
		Set<String> names = new HashSet<String>();
		for (int i = 0; i < cs.length; i++) {
			names.add(cs[i].getAttributeValue("name"));
		}
		if(cs.length == 0) {
			HUtil.find(data, 0, "name").setValue("faces-config");
			return data;
		}
		String name = "faces-config", namef = name;
		int i = 0;
		while(names.contains(namef)) namef = name + "-" + (++i);
		HUtil.find(data, 0, "name").setValue(namef);
		return data;
	}

	public void executeHandler(XModelObject object, Properties prop) throws Exception {
		Properties p = extractProperties(data[0]);
		checkRegister(object, p);
		/*TRIAL_JSF*/
		XUndoManager undo = object.getModel().getUndoManager();
		XTransactionUndo u = new XTransactionUndo("Create faces config in " + object.getAttributeValue("element type")+" "+object.getPresentationString(), XTransactionUndo.ADD);
		undo.addUndoable(u);
		try {
			super.executeHandler(object, prop);
			if(created != null) {
				FacesProcessImpl process = (FacesProcessImpl)created.getChildByPath("process");
				process.firePrepared();
			}            
			register(object, prop);
		} catch (RuntimeException e) {
			undo.rollbackTransactionInProgress();
			throw e;
		} finally {
			u.commit();
			created = null;
		}
	}
    
	private void checkRegister(XModelObject object, Properties p) throws Exception {
		boolean register = "yes".equals(extractProperties(data[0]).getProperty("register in web.xml"));
		if(!register) return;
		XModelObject webxml = WebAppHelper.getWebApp(object.getModel());
		if(webxml == null) throw new Exception ("Faces config cannot be registered because web.xml is not found.");
		if("yes".equals(webxml.get("isIncorrect"))) throw new Exception ("Faces config file cannot be registered because web.xml is incorrect.");
		if(!webxml.isObjectEditable()) throw new Exception ("Faces config file cannot be registered because web.xml is read only.");
	}

	private void register(XModelObject object, Properties prop) throws Exception {
		boolean register = "yes".equals(extractProperties(data[0]).getProperty("register in web.xml"));
		if(!register) return;
		String uri = "/WEB-INF/" + FileAnyImpl.toFileName(created);
		JSFWebHelper.registerFacesConfig(created.getModel(), uri);
	}

	protected XModelObject modifyCreatedObject(XModelObject o) {
		return created = o;
	}

}
