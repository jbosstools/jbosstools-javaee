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
package org.jboss.tools.struts.webprj.model.helpers.context;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import org.eclipse.core.resources.IProject;
import org.jboss.tools.common.model.ServiceDialog;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.common.model.util.XModelObjectUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.StrutsPreference;
import org.jboss.tools.struts.StrutsProjectUtil;
import org.jboss.tools.struts.StrutsUtils;
import org.jboss.tools.jst.web.context.AdoptWebProjectContext;
import org.jboss.tools.jst.web.context.ImportWebDirProjectContext;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;
import org.jboss.tools.jst.web.webapp.model.FileWebAppLoader;
import org.jboss.tools.struts.webprj.model.helpers.adopt.AdoptProjectContext;
import org.jboss.tools.struts.webprj.model.helpers.sync.ModulesDataValidator;

public class ImportProjectWizardContext extends ImportWebDirProjectContext {
	Delta delta = new Delta();
	
	public ImportProjectWizardContext(XModelObject target) {
		super(target);
	}
	
	protected void initRegistry() {
		getRegisterTomcatContext().setNatureIndex(StrutsProjectUtil.NATURE_NICK);
		getRegisterTomcatContext().setPreferences(StrutsPreference.REGISTER_IMPORTED_PROJECT_IN_TOMCAT);
		getRegisterTomcatContext().init();
	}
	
	public void setPexFileName(String value) {
	}

	ModulesDataValidator modulesValidator = new ModulesDataValidator();
	 
	public String getModulesErrorMessage(XModelObject[] modules, XModelObject selected) {
		modulesValidator.setProject(getProjectLocation());
		return modulesValidator.getErrorMessage(modules, selected);
	}
	
	/*
	 *  deprecated: cannot resolve between 11 and 12 versions.
	 */
	public boolean createConfigFile(String path) {
		File f = new File(path);
		if(f.exists()) {
			ServiceDialog d = target.getModel().getService();
			d.showDialog("Error", "File " + path + " exists.", new String[]{"OK"}, null, ServiceDialog.ERROR);			
			return false;
		}
		createConfigFile(f, "StrutsConfig11");
		return true;		
	}
	
	public void addSupportDelta(Properties p) {
    	XModelObject folder = webxml.getChildByPath(WebAppHelper.SERVLET_FOLDER);
    	if(folder == null) folder = webxml;
		File f = new File(webXmlLocation);
		String version = p.getProperty("version");
		String servletclass = p.getProperty("servlet class");
		String urlpattern = p.getProperty("url pattern");
		String tldfiles = p.getProperty("tld files");
		delta.webxmlCopy = webxml.copy();
		XModel model = webxml.getModel();
		XModelObject servlet = webxml.getChildByPath("action"); 
		if(servlet == null) {
			servlet = XModelObjectLoaderUtil.createValidObject(model, WebAppHelper.SERVLET_ENTITY); 
			servlet.setAttributeValue("servlet-name", "action");
			servlet.setAttributeValue("load-on-startup", "1");
		}
		servlet.setAttributeValue("servlet-class", servletclass);
		XModelObject config = XModelObjectLoaderUtil.createValidObject(model, "WebAppInitParam");
		config.setAttributeValue("param-name", "config");
		config.setAttributeValue("param-value", "/WEB-INF/struts-config.xml");
		servlet.addChild(config);
		XModelObject mapping = XModelObjectLoaderUtil.createValidObject(model, WebAppHelper.SERVLET_MAPPING_ENTITY);
		mapping.setAttributeValue("servlet-name", "action");
		mapping.setAttributeValue("url-pattern", urlpattern);
//		XModelObject o = webxml.getChildByPath(servlet.getPathPart());
		if(servlet.getParent() == null) folder.addChild(servlet);
		folder.addChild(mapping);
		String[] ts = XModelObjectUtil.asStringArray(tldfiles);
		for (int i = 0; i < ts.length; i++) {
			XModelObject taglib = XModelObjectLoaderUtil.createValidObject(model, "WebAppTaglib");
			String path = "/WEB-INF/" + ts[i], uri = path;
			if(uri.endsWith(".tld")) uri = uri.substring(0, uri.length() - 4);
			taglib.setAttributeValue("taglib-uri", uri);
			taglib.setAttributeValue("taglib-location", path);
			WebAppHelper.getJSPConfig(webxml).addChild(taglib);
			File sf = new File(new StrutsUtils().getStrutsSupportTemplatesLocation(version) + "/tld/" + ts[i]);
			File tf = new File(f.getParent() + "/" + ts[i]);
			delta.addTLDDelta(sf, tf);
		}
		File sf = new File(f.getParent() + "/" + "struts-config.xml");
		String suffix = ("1.0".equals(version)) ? StrutsConstants.VER_SUFFIX_10 : 
		                ("1.2".equals(version)) ? StrutsConstants.VER_SUFFIX_12 :
		                StrutsConstants.VER_SUFFIX_11;
		String entity = StrutsConstants.ENT_STRUTSCONFIG + suffix; 
		if(!sf.exists()) {
			createConfigFile(sf, entity);
			delta.struts.f = sf;
		}
		delta.lib.version = version; 
		modules = createAdoptContext().createModulesInfo(webxml, f.getParentFile());
		createAllModules();
		setProjectJavaSrc();
	}
	
	public String getNatureID() {
		return StrutsProjectUtil.STRUTS_NATURE_ID;
	}

	AdoptWebProjectContext adoptWebProjectContext = null;
	
	protected AdoptWebProjectContext createAdoptContext() {
		if(adoptWebProjectContext == null) {
			adoptWebProjectContext = new AdoptProjectContext();
		}
		return adoptWebProjectContext; 
	}

	public void rollbackSupportDelta() {
		if(delta.webxmlCopy == null) return;
		webxml = delta.webxmlCopy;
		delta.webxmlCopy = null;
		modules = adoptWebProjectContext.createModulesInfo(webxml, new File(webXmlLocation).getParentFile());
		createAllModules();
		delta.tlds.clear();
		delta.struts.rollback();	
	}
	
	public void commitSupportDelta() {
		if(delta.webxmlCopy == null) return; 
		delta.webxmlCopy = null;
		delta.commitTLD();
		String body =((FileWebAppLoader)XModelObjectLoaderUtil.getObjectLoader(webxml)).serializeObject(webxml);
		FileUtil.writeFile(new File(webXmlLocation), body);
		delta.lib.commit();
		try {
			getProjectHandle().refreshLocal(IProject.DEPTH_INFINITE, null);
		} catch (Exception e) {
			//ignore
		}		
	}
	
	class Delta {
		XModelObject webxmlCopy = null;
		StrutsDelta struts = new StrutsDelta();
		ArrayList<TLDDelta> tlds = new ArrayList<TLDDelta>();
		LibDelta lib = new LibDelta();
		
		public void addTLDDelta(File sf, File tf) {
			TLDDelta d = new TLDDelta();
			d.sf = sf;
			d.tf = tf;
			tlds.add(d);
		}		
		
		public void commitTLD() {
			TLDDelta[] ds = (TLDDelta[])tlds.toArray(new TLDDelta[0]);
			for (int i = 0; i < ds.length; i++) ds[i].commit();
		}
	
	}
	
	class TLDDelta {
		File sf;
		File tf;		
		public void commit() {
			if(sf.isFile() && !tf.exists()) FileUtil.copyFile(sf, tf, true);
		}
	}
	
	class StrutsDelta {
		File f = null;
		
		public void rollback() {
			if(f != null) f.delete();
			f = null;
		}
	}
	
	class LibDelta {
		String version;
		public void commit() {
			String strutsJars[] = new StrutsUtils().getLibraries(version);
			String libDir = getWebInfLocation() + "/lib";
			for (int i = 0; i < strutsJars.length; i++) {
				File source = new File(strutsJars[i]);
				File target = new File(libDir, source.getName());
				if(target.isFile()) continue;
				FileUtil.copyFile(source, target, true);
			}
		}
	}

}
