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
package org.jboss.tools.struts.webprj.model.helpers.sync;

import java.io.File;

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.jst.web.project.WebModuleConstants;

public class ModulesDataValidator implements WebModuleConstants {
	String project; 
	
	public void setProject(String project) {
		this.project = project;
	}
	
	private void setMessage(String[] errors, int i, String message) {
		if(errors[i] == null) errors[i] = message;
	}
	
	void updateErrors(XModelObject[] modules) {
		String[] errors = new String[modules.length];
		for (int i = 0; i < modules.length; i++) {
			if("deleted".equals(modules[i].get("state"))) continue; //$NON-NLS-1$ //$NON-NLS-2$
			String n = modules[i].getAttributeValue(ATTR_NAME);
			String dn = getDisplayName(n);
			String uri = modules[i].getAttributeValue(ATTR_URI);
			if(uri.length() == 0) setMessage(errors, i, getRequiredMessage("URI", dn)); //$NON-NLS-1$
			String path = modules[i].getAttributeValue(ATTR_DISK_PATH);
			if(path.length() == 0) setMessage(errors, i, getRequiredMessage("Path on Disk", dn)); //$NON-NLS-1$
			File f = new File(path);
			if(!f.isFile()) setMessage(errors, i, NLS.bind(StrutsUIMessages.PATH_IN_MODULE_MUST_REFERENCE_CONFIGFILE, dn));  //$NON-NLS-2$
			if(!modules[i].getModelEntity().getName().equals(WebModuleConstants.ENTITY_WEB_CONFIG)) {
				String mod = modules[i].getAttributeValue(ATTR_ROOT);
				String adn = (n.length() == 0) ? "Web Root" : "Module Root"; //$NON-NLS-1$ //$NON-NLS-2$
				if(mod.length() == 0) setMessage(errors, i, getRequiredMessage(adn, dn));
				if(project != null && project.startsWith(mod.toLowerCase() + "/")) //$NON-NLS-1$
					setMessage(errors, i, getContainsProjectMessage(adn, dn));
			}
			for (int j = i + 1; j < modules.length; j++) {
				if("deleted".equals(modules[j].get("state"))) continue; //$NON-NLS-1$ //$NON-NLS-2$
//				String n_i = modules[j].getAttributeValue(ATTR_NAME);
//				String dn_i = getDisplayName(n_i);
				String uri_i = modules[j].getAttributeValue(ATTR_URI);
				if(uri.equals(uri_i)) {
					String ms = NLS.bind(StrutsUIMessages.URI_ISNOT_UNIQUE, uri);
					setMessage(errors, i, ms);
					setMessage(errors, j, ms);
				} 
				String path_i = modules[j].getAttributeValue(ATTR_DISK_PATH);
				if(path_i.equals(path)) {
					String ms = StrutsUIMessages.EACH_URI_MUST_REFERENCE_UNIQUE_PATH;
					setMessage(errors, i, ms);
					setMessage(errors, j, ms);
				} 				  
			}
		}
		for (int i = 0; i < modules.length; i++) {
			modules[i].setObject("error", errors[i]); //$NON-NLS-1$
		}
	}
	
	public String getErrorMessage(XModelObject[] modules, XModelObject selected) {
		if(project != null) {
			project = project.toLowerCase().replace('\\', '/');
		}
		updateErrors(modules);
		if(selected != null && selected.getObject("error") != null) { //$NON-NLS-1$
			return selected.getObject("error").toString(); //$NON-NLS-1$
		}
		for (int i = 0; i < modules.length; i++) {
			if(modules[i].getObject("error") != null) return modules[i].getObject("error").toString(); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}
	private String getRequiredMessage(String attr, String module) {
		return NLS.bind(StrutsUIMessages.ATTRIBUTE_FOR_MODULE_MUST_BE_SET, attr, module);
	}
	private String getContainsProjectMessage(String attr, String module) {
		return NLS.bind(StrutsUIMessages.ATTRIBUTE_OF_MODULE_CANNOT_REFERENCE_FOLDER, attr, module);
	}
	
	private String getDisplayName(String name) {
		return (name.length() == 0) ? "<default>" : name; //$NON-NLS-1$
	}

}
