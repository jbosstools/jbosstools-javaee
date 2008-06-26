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
package org.jboss.tools.struts.validators.model.helpers;

import java.util.Properties;
import org.jboss.tools.common.meta.action.SpecialWizard;
import org.jboss.tools.common.meta.action.SpecialWizardFactory;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.struts.StrutsProject;
import org.jboss.tools.jst.web.project.WebProject;

public class ValidatorRegistrationHelper {
	static ValidatorRegistrationHelper instance;
	
	public static ValidatorRegistrationHelper getInstance() {
		if(instance == null) instance = new ValidatorRegistrationHelper();
		return instance;
	}
	
	protected Registrator[] registrators;
		
	private ValidatorRegistrationHelper() {
		registrators = new Registrator[]{
			new Registrator(StrutsProject.NATURE_ID, "org.jboss.tools.struts.plugins.model.handlers.ValidationFileRegistration")
		};
	}
	public boolean isEnabled(XModel model) {
		for (int i = 0; i < registrators.length; i++) {
			if(registrators[i].canRegister(model)) return true;
		}
		return false;
	}
	
	public String getRegistratorNature(XModel model) {
		for (int i = 0; i < registrators.length; i++) {
			if(registrators[i].canRegister(model)) return registrators[i].nature;
		}
		return null;
	}
	
	public void register(XModel model, XModelObject file) {
    	for (int i = 0; i < registrators.length; i++) {
    		if(registrators[i].canRegister(model)) {
    			registrators[i].register(model, file, null, false);
    		}
    	}
	}

	public boolean isRegistered(XModel model, XModelObject file) {
    	for (int i = 0; i < registrators.length; i++) {
    		if(registrators[i].canRegister(model)) {
    			if(registrators[i].register(model, file, null, true)) return true;
    		}
    	}
    	return false;
	}
	

	public void update(XModel model, XModelObject file, String oldPath) {
    	for (int i = 0; i < registrators.length; i++) {
    		if(registrators[i].canRegister(model)) {
    			registrators[i].register(model, file, oldPath, false);
    		}
    	}
	}

	public void unregister(XModel model, String oldPath) {
    	for (int i = 0; i < registrators.length; i++) {
    		if(registrators[i].canRegister(model)) {
    			registrators[i].register(model, null, oldPath, false);
    		}
    	}
	}

}

class Registrator {
	String nature;
	String className;
	SpecialWizard wizard;
	
	public Registrator(String nature, String className) {
		this.nature = nature;
		this.className = className;
	}
	
	public boolean canRegister(XModel model) {
		if(!EclipseResourceUtil.hasNature(model, nature)) return false;
		if(wizard == null) {
			if(className != null) {
				wizard = SpecialWizardFactory.createSpecialWizard(className);
				className = null;
			}
		}
		return wizard != null;
	}
	
	public boolean register(XModel model, XModelObject file, String oldPath, boolean test) {
    	Properties p = new Properties();
    	p.put("model", model);
    	String webRoot = WebProject.getInstance(model).getWebRootLocation().replace('\\', '/');
    	if(!webRoot.endsWith("/")) webRoot += "/";
    	if(file != null && file.isActive()) {
        	String path = ((FileAnyImpl)file).getAbsolutePath();
        	if(!path.toLowerCase().startsWith(webRoot.toLowerCase())) return false;
        	path = path.substring(webRoot.length() - 1);
        	p.setProperty("path", path);
    	}
    	if(oldPath != null) {
    		oldPath = oldPath.substring(webRoot.length() - 1);
    		p.setProperty("oldPath", oldPath);    		
    	}
    	if(test) p.setProperty("test", "true");
    	wizard.setObject(p);
    	return wizard.execute() == 0;
	}

}
