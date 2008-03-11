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
package org.jboss.tools.struts.model.helpers;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public class TilesHelper {
    static String PLUGIN = "org.apache.struts.tiles.TilesPlugin";
    static String CONFIG = "definitions-config";

    public static Map<String,XModelObject> getTiles(XModelObject object) {
        XModelObject o = StrutsProcessStructureHelper.instance.getParentFile(object);
        if(o == null || o.getModelEntity().getName().endsWith("10") ||
           !o.getModelEntity().getName().startsWith("StrutsConfig")) return new TreeMap<String,XModelObject>();
		WebModulesHelper h = WebModulesHelper.getInstance(object.getModel());
		String module = h.getModuleForConfig(o);
		XModelObject[] cs = h.getConfigsForModule(object.getModel(), module);
		if(cs.length < 2) return getDefinitions(getDefinitionsConfig(getTilesPlugin(o)));
		Map<String,XModelObject> map = getDefinitions(getDefinitionsConfig(getTilesPlugin(cs[0])));
		for (int i = 1; i < cs.length; i++) {
			map.putAll(getDefinitions(getDefinitionsConfig(getTilesPlugin(cs[i]))));
		}
		return map;
    }

    public static XModelObject findTile(XModelObject object, String name) {
        XModelObject o = StrutsProcessStructureHelper.instance.getParentFile(object);
        if(o == null || o.getModelEntity().getName().endsWith("10")) return null;
        XModelObject dc = getDefinitionsConfig(getTilesPlugin(o));
        XModelObject t = findTileByDefinitionConfig(dc, name);
        if(t != null) return t;
		WebModulesHelper h = WebModulesHelper.getInstance(object.getModel());
		String module = h.getModuleForConfig(o);
		XModelObject[] cs = h.getConfigsForModule(object.getModel(), module);
		if(cs.length < 2) return null;
		for (int i = 0; i < cs.length; i++) {
			if(cs[i] == o) continue;
			dc = getDefinitionsConfig(getTilesPlugin(cs[i]));
			t = findTileByDefinitionConfig(dc, name);
			if(t != null) return t;
		}
        return null;
    }

    static XModelObject getTilesPlugin(XModelObject strutsconfig) {
    	XModelObject f = strutsconfig.getChildByPath("plug-ins");
    	if(f == null) return null;
        XModelObject[] ps = f.getChildren();
        for (int i = 0; i < ps.length; i++)
          if(PLUGIN.equals(ps[i].getAttributeValue("className"))) return ps[i];
        return null;
    }

    static XModelObject getDefinitionsConfig(XModelObject plugin) {
        if(plugin == null) return null;
        XModelObject[] ps = plugin.getChildren();
        for (int i = 0; i < ps.length; i++)
          if(CONFIG.equals(ps[i].getAttributeValue("property"))) return ps[i];
        return null;
    }

    private static Map<String,XModelObject> getDefinitions(XModelObject cg) {
        String[] fs = getTileFileNames(cg);
        Map<String,XModelObject> set = new TreeMap<String,XModelObject>();
        if(cg != null) for (int i = 0; i < fs.length; i++) {
            XModelObject tilesdef = findTilesFile(cg.getModel(), fs[i]);
            if(tilesdef == null) continue;
            XModelObject[] ds = tilesdef.getChildren();
            for (int j = 0; j < ds.length; j++) set.put(ds[j].getPathPart(), ds[j]);
        }
        return set;
    }

    private static XModelObject findTileByDefinitionConfig(XModelObject cg, String name) {
        String[] fs = getTileFileNames(cg);
        if(cg != null) for (int i = 0; i < fs.length; i++) {
            XModelObject tilesdef = findTilesFile(cg.getModel(), fs[i]);
            if(tilesdef == null) continue;
            XModelObject c = tilesdef.getChildByPath(name);
            if(c != null) return c;
        }
        return null;
    }

    static String[] getTileFileNames(XModelObject cg) {
        if(cg == null) return new String[0];
        String value = cg.getAttributeValue("value");
        if(value == null || value.length() == 0) return new String[0];
        StringTokenizer st = new StringTokenizer(value, ",");
        String[] ns = new String[st.countTokens()];
        for (int i = 0; i < ns.length; i++) ns[i] = st.nextToken().trim();
        return ns;
    }

    static XModelObject findTilesFile(XModel model, String path) {
        if(path.length() == 0) return null;
        XModelObject o = model.getByPath("FileSystems" + path);
        if(o != null) return o;
        return XModelImpl.getByRelativePath(model, path);
    }

}

