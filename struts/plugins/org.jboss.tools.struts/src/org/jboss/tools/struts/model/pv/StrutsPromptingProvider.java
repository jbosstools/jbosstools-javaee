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
package org.jboss.tools.struts.model.pv;

import java.io.File;
import java.util.*;
import org.eclipse.core.resources.IFile;
import org.jboss.tools.common.model.java.handlers.OpenJavaSourceHandler;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.struts.model.helpers.open.*;
import org.jboss.tools.jst.web.project.list.IWebPromptingProvider;

public class StrutsPromptingProvider implements IWebPromptingProvider {

	public boolean isSupporting(String id) {
		return id != null && id.startsWith("struts");
	}

	public List<Object> getList(XModel model, String id, String prefix, Properties properties) {
		try {
			return getListInternal(model, id, prefix, properties);
		} catch (Exception e) {
			if(properties != null) {
				String message = e.getMessage();
				if(message==null) {
					message = e.getClass().getName();
				}
				properties.setProperty(ERROR, e.getMessage());
			}
			return EMPTY_LIST;
		}
	}
	
	private List<Object> getListInternal(XModel model, String id, String prefix, Properties properties) throws Exception {
		String error = null;
		if(STRUTS_OPEN_BUNDLE.equals(id)) {
			String bundle = properties.getProperty(BUNDLE);
			if(bundle == null) bundle = prefix;
			OpenBundleHelper h = new OpenBundleHelper();
			error = h.run(model, bundle);
		} else if(STRUTS_OPEN_PARAMETER.equals(id)) {
			String name = properties.getProperty(NAME);
			OpenResourceParameterHelper h = new OpenResourceParameterHelper();
			error = h.run(model, name);
		} else if(STRUTS_OPEN_KEY.equals(id)) {
			String bundle = properties.getProperty(BUNDLE);
			String key = properties.getProperty(KEY);
			String locale = properties.getProperty(LOCALE);
			OpenKeyHelper h = new OpenKeyHelper();
			error = h.run(model, bundle, key, locale);
		} else if(STRUTS_OPEN_LINK_FORWARD.equals(id)) {
			String forward = prefix;
			OpenLinkForwardHelper h = new OpenLinkForwardHelper();
			error = h.run(model, forward);
		} else if(STRUTS_OPEN_LINK_PAGE.equals(id)) {
			String page = prefix;
			try {
				if(new File(page).isFile()) {
					List<Object> l = new ArrayList<Object>();
					l.add(page);
					return l;
				}
			} catch (Exception e) {}
			IFile f = (IFile)properties.get(FILE);
			OpenLinkPageHelper h = new OpenLinkPageHelper();
			error = h.run(f, model, page);
		} else if(STRUTS_OPEN_LINK_ACTION.equals(id)) {
			String action = prefix;
			String module = properties.getProperty(MODULE);
			OpenLinkActionHelper h = new OpenLinkActionHelper();
			error = h.run(model, action, module);
		} else if(STRUTS_OPEN_PROPERTY.equals(id)) {
			String action = properties.getProperty(ACTION);
			String type = properties.getProperty(TYPE);
			String property = prefix;
			if(property == null) property = properties.getProperty(PROPERTY);
			OpenProperty h = new OpenProperty();
			error = h.run(model, type, action, property);
		} else if(STRUTS_OPEN_ACTION_MAPPING.equals(id)) {
			String action = prefix;
			if(action == null) action = properties.getProperty(ACTION);
			OpenLinkActionHelper h = new OpenLinkActionHelper();
			error = h.run(model, action, null);
		} else if(STRUTS_OPEN_FORM_BEAN.equals(id)) {
			String name = prefix;
			String formProperty = properties.getProperty(PROPERTY);
			OpenFormBean h = new OpenFormBean();
			error = h.run(model, name, formProperty);
		} else if(STRUTS_OPEN_FORWARD_PATH.equals(id)) {
			String objectPath = properties.getProperty(MODEL_OBJECT_PATH);
			String pathValue = prefix;
			IFile f = (IFile)properties.get(FILE);
			OpenForwardTargetHelper h = new OpenForwardTargetHelper();
			error = h.run(f, objectPath, pathValue);
		} else if(STRUTS_OPEN_OBJECT_BY_PATH.equals(id)) {
			IFile f = (IFile)properties.get(FILE);
			String objectPath = properties.getProperty(MODEL_OBJECT_PATH);
			OpenObjectByPath h = new OpenObjectByPath();
			error = h.run(f, objectPath);
		} else if(STRUTS_OPEN_FILE_IN_WEB_ROOT.equals(id)) {
			String path = prefix;
			OpenFileInWebRoot h = new OpenFileInWebRoot();
			error = h.run(model, path);
		} else if(STRUTS_OPEN_VALIDATOR.equals(id)) {
			String name = prefix;
			OpenValidator h = new OpenValidator();
			error = h.run(model, name);
		} else if(STRUTS_OPEN_TAG_LIBRARY.equals(id)) {
			String uri = prefix;
			String tagName = properties.getProperty(NAME);
			String attributeName = properties.getProperty(ATTRIBUTE);
			OpenTagLib h = new OpenTagLib();
			error = h.run(model, uri, tagName, attributeName);
		} else if(STRUTS_OPEN_METHOD.equals(id)) {
			String type = properties.getProperty(TYPE);
			String method = properties.getProperty(NAME);
			Properties pp = new Properties();
			pp.setProperty("method", method);
			pp.setProperty("ignoreWarning", "true");
			OpenJavaSourceHandler.open(model, type, pp);
			error = pp.getProperty("error");
		}
		if(error != null) throw new Exception(error);
		return EMPTY_LIST;
	}

}
