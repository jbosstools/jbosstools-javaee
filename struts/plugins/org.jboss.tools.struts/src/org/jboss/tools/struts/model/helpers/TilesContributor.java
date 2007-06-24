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
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.jst.web.tiles.model.TilesConstants;
import org.jboss.tools.jst.web.tiles.model.helpers.*;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public class TilesContributor implements ITilesDefinitionSetContributor {

	public Set<XModelObject> getTileFiles(XModel model) {
		XModelObject[] cs = WebModulesHelper.getInstance(model).getAllConfigs();
		Set<XModelObject> set = new HashSet<XModelObject>();
		for (int i = 0; i < cs.length; i++) {
			collectTileFiles(set, cs[i]);
		}
		return set;
	}
	
	private void collectTileFiles(Set<XModelObject> set, XModelObject strutsconfig) {
		XModelObject plugin = TilesHelper.getTilesPlugin(strutsconfig);
		XModelObject dc = TilesHelper.getDefinitionsConfig(plugin);
		collectTileFilesFromTilesConfig(set, dc);
	}

    private static void collectTileFilesFromTilesConfig(Set<XModelObject> set, XModelObject cg) {
        String[] fs = TilesHelper.getTileFileNames(cg);
        if(cg != null) for (int i = 0; i < fs.length; i++) {
            XModelObject tilesdef = TilesHelper.findTilesFile(cg.getModel(), fs[i]);
            if(tilesdef == null) continue;
            set.add(tilesdef);
        }
    }

	public boolean isRelevant(XModelTreeEvent event) {
		XModelObject source = event.getModelObject();
		String sourceEntity = source.getModelEntity().getName();
		if(sourceEntity.startsWith("WebApp")) return true;
		if(event.kind() == XModelTreeEvent.CHILD_ADDED) {
			XModelObject a = (XModelObject)event.getInfo();
			String addedEntity = a.getModelEntity().getName();
			if(addedEntity.equals(TilesConstants.ENT_DEFINITION)) {
				return true;
			} else if(addedEntity.startsWith(StrutsConstants.ENT_STRUTSCONFIG)) {
				return true;
			} else if(addedEntity.equals(TilesConstants.ENT_FILE)) {
				return true;
			}
		} else if(event.kind() == XModelTreeEvent.CHILD_REMOVED) {
			if(sourceEntity.equals(TilesConstants.ENT_FILE)) return true;
		} else if(event.kind() == XModelTreeEvent.NODE_CHANGED) {
			if(sourceEntity.startsWith("StrutsPlugin")) return true;
			if(sourceEntity.equals(TilesConstants.ENT_DEFINITION)) return true;
		}
		return false;
	}

}
