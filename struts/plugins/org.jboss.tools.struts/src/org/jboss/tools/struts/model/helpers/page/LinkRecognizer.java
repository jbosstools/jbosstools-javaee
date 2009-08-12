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
package org.jboss.tools.struts.model.helpers.page;

import java.io.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.engines.impl.EnginesLoader;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.common.model.options.*;
import org.jboss.tools.common.model.options.impl.SharableElementImpl;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.struts.StrutsModelPlugin;
import org.jboss.tools.struts.model.helpers.page.link.Links;

public class LinkRecognizer {
	
	//private static Object lock = new Object();
	
	public static LinkRecognizer getInstance() {
		return LinkRecognizerHolder.INSTANCE;
	}
	
	public static class LinkRecognizerHolder {
		public static LinkRecognizer INSTANCE = new LinkRecognizer();
		static{
			INSTANCE.init();
		}
	}
	
	long timeStamp = 0;
	XModel model;
	XModelObject option;
	JSPLinkRecognizerObjectImpl object;
	Links links;
	
	private LinkRecognizer() {
		links = new Links();
	}
	
	void init() {
		model = PreferenceModelUtilities.getPreferenceModel();
		option = model.getByPath("%Options%/Struts Studio/Link Recognizer");
		String value = option.getAttributeValue("value");
		if(value.length() == 0) value = getDefaultValue();
		object = load(value);
		object.setActive(true);
		((XModelImpl)model).setExtraRoot(object);
		timeStamp = object.getTimeStamp();
		links.update(object);		
	}
	
	private JSPLinkRecognizerObjectImpl load(String value) {
		JSPLinkRecognizerObjectImpl object = (JSPLinkRecognizerObjectImpl)model.createModelObject("JSPLinkRecognizer", null);
		if(value.length() > 0) {
			try {
				StringReader r = new StringReader(value); 
				new XModelObjectLoaderUtil().loadChildren(XMLUtil.getElement(r), object);
			} catch (Exception e) {
				StrutsModelPlugin.getPluginLog().logError(e);
			}
		}
		return object;
	}
	
	public XModelObject getModelObject() {
		return object;
	}
	
	public void save() {
		if(timeStamp == object.getTimeStamp()) return;
		timeStamp = object.getTimeStamp();
		XModelObjectLoaderUtil loader = new XModelObjectLoaderUtil();
		try {
			StringWriter w = new StringWriter();		
			loader.serialize(object, w);
			model.changeObjectAttribute(option, "value", w.toString());
			model.saveOptions();
		} catch (Exception e) {
			StrutsModelPlugin.getPluginLog().logError(e);
		}
		links.update(object);
	}
	
	public void restoreDefaults(XModelObject object) throws XModelException {
		EnginesLoader.merge(object, load(getDefaultValue()));
	}
	
	private String getDefaultValue() {
		SharableElementImpl s = (SharableElementImpl)option;
		return s.getAttributeValue("value", SharableElementImpl.GENERAL);
	}
	
	public Links getLinks() {
		return links;
	}

}
