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
package org.jboss.tools.jsf.project.capabilities;

import java.util.*;
import org.eclipse.core.resources.*;
import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jst.web.project.WebProject;

public class FileAdditionsPerformer extends PerformerItem {
	XModel model;
	XModelObject capability;
	
	Map<String,XModelObject> configFiles;
	FileAdditionPerformer[] performers;

	public String getDisplayName() {
		return "File Additions";
	}

	public IPerformerItem[] getChildren() {
		return performers;
	}

	public void init(XModel model, XModelObject capability) {
		this.capability = capability;
		configFiles = new HashMap<String,XModelObject>();
		this.model = model;
		initPerformers();
	}
	
	public boolean check() {
		if(!checkPerformers()) return false;
		return true;
	}
	
	private void initPerformers() {
		XModelObject[] fileAdditions = capability.getChildren("JSFFileAddition");
		performers = new FileAdditionPerformer[fileAdditions.length];
		for (int i = 0; i < fileAdditions.length; i++) {
			performers[i] = new FileAdditionPerformer();
			performers[i].setParent(this);
			performers[i].init(null, fileAdditions[i]);
		}		
	}
	
	private boolean checkPerformers() {
		if(!isSelected()) return true;
		XModelObject[] fileAdditions = capability.getChildren("JSFFileAddition");
		for (int i = 0; i < fileAdditions.length; i++) {
			if(!performers[i].isSelected()) continue;
			String filePath = fileAdditions[i].getAttributeValue("file name");
			XModelObject configFile = getConfigFile(filePath);
			if(configFile == null) return false;
			performers[i].setConfigFile(configFile);
			if(!performers[i].check()) return false;			
		}		
		return true;
	}
	
	private XModelObject getConfigFile(String filePath) {
		XModelObject configFile = configFiles.get(filePath);
		if(configFile == null) {
			configFile = XModelImpl.getByRelativePath(model, filePath);
			if(configFile == null) {
				configFile = selectConfigFile(filePath);
				if(configFile == null) return null;
			}
			configFiles.put(filePath, configFile);
		}
		return configFile;
	}
	
	private XModelObject selectConfigFile(String filePath) {
		SpecialWizard wizard = SpecialWizardFactory.createSpecialWizard("org.jboss.tools.common.model.ui.dialog.SelectEclipseFileWizard");
		IProject project = EclipseResourceUtil.getProject(model.getRoot());

		Properties p = new Properties();
		String message = NLS.bind(JSFUIMessages.CONFIGURATION_FILE_ISNOT_FOUND_IN_PROJECT, filePath, project.getName());
		p.setProperty("message", message);
		p.setProperty("extension", "*");
		p.put("root", project);
		String webRoot = WebProject.getInstance(model).getWebRootLocation();
		String location = webRoot + filePath;
		p.setProperty("selection", location);
		wizard.setObject(p);

		int q = wizard.execute();
		Object result = (q != 0) ? null : p.get("result");
		IFile f = (result instanceof IFile) ? (IFile)result : null;
		return (f == null) ? null : EclipseResourceUtil.getObjectByResource(f);
	}
	
	public boolean execute(PerformerContext context) throws Exception {
		if(!isSelected()) return true;
		for (int i = 0; i < performers.length; i++) {
			if(performers[i].isSelected()) performers[i].execute(context);		
		}
		saveModifications();
		context.monitor.worked(1);
		return true;
	}
	
	private void saveModifications() {
		XModelObject[] fs = configFiles.values().toArray(new XModelObject[0]);
		for (int i = 0; i < fs.length; i++) {
			if(fs[i].isModified()) {
				XActionInvoker.invoke("SaveActions.Save", fs[i], new Properties());
			}
		}
	}

}
