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
package org.jboss.tools.struts.model.icons;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.icons.impl.*;
import org.jboss.tools.struts.StrutsModelPlugin;

public class StrutsCustomizedIcon implements ImageComponent {

    public StrutsCustomizedIcon() {}

    public int getHash(XModelObject obj) {
        String p = obj.getAttributeValue("small-icon");
        return (p == null || p.length() == 0)
               ? getDefaultIconName(obj).hashCode() : p.hashCode();
    }

    public Image getImage(XModelObject obj) {
        try {
        	if(true) throw new Exception("Needs to be reimplemented.");
            String p = obj.getAttributeValue("small-icon");
            if(p == null) throw new Exception("");
            java.net.URL url = null;
            try {
//                url = obj.getModel().getModelClassLoader().getResource(p.substring(1));
            } catch (Exception e) {
                StrutsModelPlugin.getPluginLog().logError(e);
            }
            return ImageDescriptor.createFromURL(url).createImage();
        } catch (Exception e) {
          try {
            String s = getDefaultIconName(obj);
            return obj.getModelEntity().getMetaModel().getIconList().getImage(s, "default.unknown");
          } catch (Exception e2) {
              StrutsModelPlugin.getPluginLog().logError(e2);
        	  return null;
          }
        }
    }

    private String getDefaultIconName(XModelObject obj) {
        try {
            return obj.getModelEntity().getRenderer().getIconInfo("strutsCustomized");
        } catch (Exception e) {
            StrutsModelPlugin.getPluginLog().logError(e);
            return "main.closedbox";
        }
    }

}
