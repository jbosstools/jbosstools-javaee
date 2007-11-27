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
package org.jboss.tools.struts.plugins.model.handlers;

import java.io.*;
import java.util.*;

import org.eclipse.osgi.util.NLS;
import org.w3c.dom.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.xml.XMLEntityResolver;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.struts.StrutsModelPlugin;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.xml.sax.*;

public class AddSpecialPluginHandler extends AbstractHandler {

    public AddSpecialPluginHandler() {}

    public boolean isEnabled(XModelObject object) {
    	boolean b = validateActionName();
        if(object == null || !object.isObjectEditable()) return false;
        return b;
    }

	boolean validateActionName() {
        XModelObject plugin = getPlugin(PreferenceModelUtilities.getPreferenceModel());
        String dn = null;
        if(plugin != null) {
        	dn = plugin.getAttributeValue("title"); //$NON-NLS-1$
        	if(dn == null) dn = plugin.getAttributeValue("name"); //$NON-NLS-1$
        } else {
        	dn = StrutsUIMessages.ADD_PLUGIN_BY_TEMPLATE;
        }
    	((XActionImpl)action).setDisplayName(dn);
        return plugin != null;
	}


    public void executeHandler(XModelObject object, Properties p) throws Exception {
        XModelObject plugin = getPlugin(PreferenceModelUtilities.getPreferenceModel());
        if(plugin == null) return;
        String text = plugin.getAttributeValue("text"); //$NON-NLS-1$
        ///text = XModelObjectLoaderUtil.loadFromXMLAttribute(text);
        Element e = getElement(plugin.getAttributeValue("text")); //$NON-NLS-1$
        if(e == null) {
            fireErrors(object.getModel(), text, plugin.getAttributeValue("title")); //$NON-NLS-1$
            return;
        }
        plugin = loadPlugin(object.getModel(), e);
        XModelObject old = findPlugin(object, plugin);
        if(old != null) {
            ServiceDialog d = object.getModel().getService();
            String mes = NLS.bind(StrutsUIMessages.PLUGIN_FOR_CLASS_EXISTS, plugin.getAttributeValue("className")); //$NON-NLS-2$ //$NON-NLS-3$
            int q = d.showDialog(StrutsUIMessages.ADD_PLUGIN, mes, new String[]{StrutsUIMessages.OK, StrutsUIMessages.CANCEL}, null, ServiceDialog.QUESTION);
            if(q != 0) return;
            DefaultRemoveHandler.removeFromParent(old);
        }
        DefaultCreateHandler.addCreatedObject(object, plugin, p);
    }

    private XModelObject getPlugin(XModel model) {
        int i = -1;
        try { 
        	i = Integer.valueOf(action.getProperty("index")).intValue();  //$NON-NLS-1$
        } catch (Exception e) {
        	//ignore
        }
        if(i < 0) return null;
        XModelObject[] ps = model.getByPath("%Options%/Struts Studio/Automation/Plug-ins Insets").getChildren(); //$NON-NLS-1$
        return (i < ps.length) ? ps[i] : null;
    }

    private Element getElement(String text) throws Exception {
        StringReader sr = new StringReader(text);
        return XMLUtil.getElement(sr);
    }

    private void fireErrors(XModel model, String text, String title) {
        String[] s = getXMLErrors(new StringReader(text));
        if(s.length == 0) return;
        ServiceDialog d = model.getService();
        d.showDialog("", NLS.bind(StrutsUIMessages.WRONG_TEXT_IN_PLUGIN_OPTION, title, s[0]), new String[]{StrutsUIMessages.OK}, null, ServiceDialog.ERROR);    }

    private XModelObject loadPlugin(XModel model, Element e) {
        XModelObject plugin = model.createModelObject("StrutsPlugin11", null); //$NON-NLS-1$
        XModelObjectLoaderUtil util = new XModelObjectLoaderUtil();
        util.loadAttributes(e, plugin);
        util.loadChildren(e, plugin);
        return plugin;
    }

    private XModelObject findPlugin(XModelObject parent, XModelObject plugin) {
        String cn = plugin.getAttributeValue("className"); //$NON-NLS-1$
        XModelObject[] ps = parent.getChildren();
        for (int i = 0; i < ps.length; i++)
          if(cn.equals(ps[i].getAttributeValue("className"))) return ps[i]; //$NON-NLS-1$
        return null;
    }

    private static String[] getXMLErrors(Reader reader) {
        try {
            org.xml.sax.InputSource inSource = new org.xml.sax.InputSource(reader);
            return getXMLErrors(inSource);
        } catch (Exception e) {
            StrutsModelPlugin.getPluginLog().logError(e);
            return new String[]{e.getMessage()};
        }
    }

    private static String[] getXMLErrors(InputSource is) {
    	return XMLUtilities.getXMLErrors(is, false, XMLEntityResolver.getInstance());    	
    }

}

class ErrorHandlerImpl implements ErrorHandler {
  List<String> errors = new ArrayList<String>();

  public void error(SAXParseException e) throws SAXException {
      add(e);
      throw e;
  }

  public void fatalError(SAXParseException e) throws SAXException {
      add(e);
      throw e;
  }

  public void warning(SAXParseException e) throws SAXException {
      add(e);
  }

  private void add(SAXParseException e) {
      errors.add("" + e.getMessage() + ":" + e.getLineNumber() + ":" + e.getColumnNumber()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

}

