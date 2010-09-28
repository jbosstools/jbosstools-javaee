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
package org.jboss.tools.struts;

import java.io.File;
import java.util.*;
import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.jst.web.project.helpers.*;
import org.jboss.tools.jst.web.project.version.*;

public class StrutsUtils extends AbstractWebProjectTemplate {
	
	static StrutsUtils instance = null;
	
	public static StrutsUtils getInstance() {
		if(instance == null) {
			instance = new StrutsUtils();
		}
		return instance;
	}

	public String getNatureDir() {
		return "struts";
	}
	
	public ProjectVersions getProjectVersions() {
		return StrutsVersions.getInstance(getProjectTemplatesLocation());
	}

	public String getStrutsSupportTemplatesLocation() {
		return getTemplatesBase() + "/strutssupport/";
	}
	
	public String getStrutsSupportTemplatesLocation(String version) {
		return getTemplatesBase() + "/strutssupport/" + getStrutsVersion(version);
	}
	
	public String[] getTldTemplates(String version) {
		File templateDir = new File(getStrutsSupportTemplatesLocation(version) + "/tld");
		ArrayList<String> l = new ArrayList<String>();
		File[] fs = templateDir.listFiles();
		if(fs != null) for (int i = 0; i < fs.length; i++) {
			if(fs[i].isFile() && fs[i].getName().endsWith(".tld")) l.add(fs[i].getName());
		}
		return l.toArray(new String[0]);
	}
	
	private String getStrutsVersion(String version) {
		if(version == null) return "1.1";
		return (version.indexOf("1.0") >= 0) ? "1.0" : 
			(version.indexOf("1.2") >= 0) ? "1.2" : "1.1";
	}
	
	public Properties getTldTemplateDefaultProperties(String version) {
		version = getStrutsVersion(version);
		String path = StrutsPreference.OPTIONS_STRUTS_SUPPORT_BASE_PATH + version;
		XModelObject o = PreferenceModelUtilities.getPreferenceModel().getByPath(path);
		XAttribute[] as = o.getModelEntity().getAttributes();
		Properties p = new Properties();
		for (int i = 0; i < as.length; i++) {
			String xml = as[i].getXMLName();
			if(xml != null && xml.length() > 0)
			  p.setProperty(xml, o.getAttributeValue(as[i].getName()));
		}
		return p;
	}
	
	public String getDefaultVersion() {
		String preference = StrutsPreference.DEFAULT_STRUTS_VERSION.getValue();
		return getDefaultVersion(preference);
	}

	public String getDefaultTemplate(String version) {
		String preference = StrutsPreference.DEFAULT_PROJECT_TEMPLATE.getValue();
		return getDefaultTemplate(version, preference);	
	}

	public void setDefaultTemplate(String template) {
		try {
			StrutsPreference.DEFAULT_PROJECT_TEMPLATE.setValue(template);
		} catch (XModelException e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected String getWizardEntitySuffix() {
		return "Struts";
	}
	
	protected boolean isSetDefaultAllowed() {
		return true;
	}
}
