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
package org.jboss.tools.jsf.project.capabilities;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.loaders.impl.*;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.common.projecttemplates.ProjectTemplatesPlugin;

public class JSFCapabilitiesLoader extends FileRootLoader {
    protected static XModelObjectLoaderUtil util = new XModelObjectLoaderUtil();
    
    static {
    	util.setup(null, false);
    }
    
    public JSFCapabilitiesLoader() {}

    protected XModelObjectLoaderUtil util() {
        return util;
    }
	public File file(XModelObject object) {
		Bundle b = Platform.getBundle("org.jboss.tools.jsf");
		String stateLocation = Platform.getStateLocation(b).toString().replace('\\', '/');
		String fileLocation = stateLocation + "/templates/JSFCapabilities.xml";
		File f = new File(fileLocation);
		if(!f.exists()) {
			IPath install = ProjectTemplatesPlugin.getTemplateStatePath();
			File source = new File(install.toFile(), "templates/JSFCapabilities.xml");
			FileUtil.copyFile(source, f, true);
		}
		return f;
	}

}
