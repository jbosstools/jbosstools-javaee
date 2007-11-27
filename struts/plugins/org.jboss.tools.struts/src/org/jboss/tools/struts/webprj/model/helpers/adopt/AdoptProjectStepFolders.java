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
package org.jboss.tools.struts.webprj.model.helpers.adopt;

import java.io.*;

public class AdoptProjectStepFolders extends AWStep {

    public String getTitle() {
        return "Folders";
    }

    public String getAttributeMessage(String name) {
        return ("lib".equals(name)) ? "Lib Folder" :
               ("classes".equals(name)) ? "Classes Folder" :
               "Ant Build File";
    }

    protected void validate() throws Exception {
        String bf = support.getAttributeValue(3, "build");
        File f = new File(bf);
        if(f.isFile()) f = f.getParentFile();
        support.setAttributeValue(0, "build", f.getAbsolutePath());
    }

}

