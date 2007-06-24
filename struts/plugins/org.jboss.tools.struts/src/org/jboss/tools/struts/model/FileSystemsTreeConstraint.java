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
package org.jboss.tools.struts.model;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.XFileObject;

public class FileSystemsTreeConstraint implements XFilteredTreeConstraint {
	private static final long serialVersionUID = 321271480151368609L;
	private boolean showProcess = false;
    public FileSystemsTreeConstraint() {}

    public void update(XModel model) {}

    public boolean isHidingAllChildren(XModelObject object) {
        return false;
    }

    public boolean isHidingSomeChildren(XModelObject object) {
        return !showProcess && object.getFileType() == XFileObject.FILE &&
               object.getModelEntity().getChild("StrutsProcess") != null;
    }

    public boolean accepts(XModelObject object) {
        return !"StrutsProcess".equals(object.getModelEntity().getName());
    }
}
