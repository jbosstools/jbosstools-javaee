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
package org.jboss.tools.struts.ui.wizard.selectpath;

import java.util.*;
import org.jboss.tools.common.meta.*;
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.XFileObject;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.struts.model.helpers.path.*;
import org.jboss.tools.common.model.ui.actions.IActionProvider;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;

import org.jboss.tools.common.model.ui.*;
import org.jboss.tools.common.model.ui.attribute.adapter.*;
import org.eclipse.swt.widgets.Control;

public class PathEditorAdapter extends DefaultValueAdapter implements IActionHelper {
	public static int ACTIONS = 1, PAGES = 2, TILES = 4;
	private XModelObject object;
	private XModelObject contextProcess = null;
	protected String attrname = "";
	Set<String> tiles = new TreeSet<String>();
	ActionsTree actions = null;

	public void dispose() {
		super.dispose();
		if (tiles!=null) tiles.clear();
		tiles = null;
		if (actions!=null) actions.dispose();
		actions = null;
	}
	
	public void load() {
		setConstraints(attribute, modelObject);
		super.load();	
	}

	public String getCommand() {
		return "..."; //$NON-NLS-1$
	}

	public Object getAdapter(Class adapter) {
		if (adapter == IActionHelper.class) return this;
		return super.getAdapter(adapter);
	}
	
	public void setConstraints(XAttribute a, XModelObject o) {
		XAttribute attr = a;
		attrname = attr.getName();
		object = o;
		if(object != null && isTileDefinition(object)) {
			contextProcess = object;
		} else {
			updateContextProcess();
		}
		getActionProvider();
	}

	private boolean isTileDefinition(XModelObject o) {
		String entity = o.getModelEntity().getName();
		return (entity.equals("TilesDefinition") || entity.equals("FileTiles"));
	}

	private boolean isCreatingLink(XModelObject o) {
		String entity = o.getModelEntity().getName();
		return "StrutsProcessItem".equals(entity) &&
			   "page".equals(o.getAttributeValue("type"));
	}

	private void updateContextProcess() {
		XModelObject o = object;
		while(o != null && o.getFileType() == XFileObject.NONE) o = o.getParent();
		contextProcess = (o == null) ? null : o.getChildByPath("process");
	}

	public String invoke(Control control) {
		return invoke0(control);			
	}
	
	public String invoke0(Control control) {
		if(contextProcess == null) return null;
		Properties runningProperties = new Properties();
		loadContext(runningProperties);
		String v = getStringValue(false);
		if(v != null && v.length() > 0) {
			if((getTools() & TILES) == 0 || !tiles.contains(v)) {
				if(!v.startsWith("/")) v = "/" + v;
				XModelObject config = StrutsProcessStructureHelper.instance.getParentFile(contextProcess);
				WebModulesHelper wmh = WebModulesHelper.getInstance(config.getModel()); 
				String module = wmh.getModuleForConfig(config);
				UrlPattern urlPattern = wmh.getUrlPattern(module);
				urlPattern.getActionPath(v);
				if(urlPattern.isActionUrl(v)) v = urlPattern.getActionUrl(v);
			}
		}
		runningProperties.setProperty("selectedPath", "" + v);
		runningProperties.put("contextProcess", contextProcess);
		runningProperties.put("title", "Edit " + attrname);
		if(control != null) runningProperties.put("shell", control.getShell());
		if(object.getModelEntity().getName().startsWith("StrutsException"))
		runningProperties.setProperty("isException", "yes");  
		XActionInvoker.invoke("StrutsCreateActionForwardStep", "EditPath", object, runningProperties);
		return runningProperties.getProperty("selectedPath");
	}

	protected int getTools() {
		String entity = object.getModelEntity().getName();
		if(isTileDefinition(object)) {
			if("path".equals(attrname)) return 2;
			if("controllerUrl".equals(attrname)) return 1;
		}
		if(entity.startsWith("StrutsException")) return 3;
		entity = object.getParent().getModelEntity().getName();
		if(entity.startsWith("StrutsGlobalForwards")) return 7;
		if(isCreatingLink(object)) return 3;
		return 6;
	}

	private void loadContext(Properties p) {
		int tools = getTools();
		if((tools & ACTIONS) != 0) {
			actions = new ActionsTree();
			actions.setModel(object.getModel());
			actions.setConstraint(object);
			p.put("actionsTree", actions);
		} else {
			actions = null;
			p.remove("actionsTree");
		}
		p.setProperty("tools", "" + tools);
		tiles.clear();
		if((tools & TILES) != 0)
		  tiles.addAll(TilesHelper.getTiles(object).keySet());
		p.put("tiles", tiles);
	}
	
	//link action support
	
}
