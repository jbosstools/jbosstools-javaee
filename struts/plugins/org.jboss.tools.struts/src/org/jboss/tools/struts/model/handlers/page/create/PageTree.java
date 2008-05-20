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
package org.jboss.tools.struts.model.handlers.page.create;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.meta.*;
import org.jboss.tools.common.model.filesystems.XFileObject;
import org.jboss.tools.common.model.impl.trees.FileSystemResourceTree;

public class PageTree extends FileSystemResourceTree {
    protected String rootFileSystem = null;

    public PageTree() {}

    public void setConstraint(Object object) {
        super.setConstraint(object);
        Object[] os = (Object[])object;
        XAttribute a = (XAttribute)os[0];
        CreatePageContext c = (CreatePageContext)a.getEditor().getContext();
        rootFileSystem = c.getSelectedRoot();
    }

    public XModelObject getRoot() {
        XModelObject o = model.getByPath("FileSystems/" + rootFileSystem);
        return (o != null) ? o : model.getByPath("FileSystems");
    }

    public XModelObject getParent(XModelObject object) {
        return (rootFileSystem == null) ? super.getParent(object) : object.getParent();
    }

    public XModelObject[] getChildren(XModelObject object) {
        if(rootFileSystem == null) return super.getChildren(object);
        int t = object.getFileType();
        if(t == XFileObject.FILE) return new XModelObject[0];
        return getChildrenInFileSystem(object);
    }

    protected boolean accepts0(XModelObject o) {
        int type = o.getFileType();
        if(type == XModelObject.FOLDER) return true;
        if(type != XModelObject.FILE) return false;
        String pathpart = o.getPathPart();
        String pp = pathpart.substring(pathpart.lastIndexOf('.') + 1);
        String ent = o.getModelEntity().getName();
        return (extensions == null || extensions.contains(pp)) &&
               (entities == null || entities.contains(ent));
    }

    private XModelObject[] getChildrenInFileSystem(XModelObject object) {
        SortedMap<String,XModelObject> t = new TreeMap<String,XModelObject>();
        XModelObject[] cs = object.getChildren();
        for (int j = 0; j < cs.length; j++) {
            String p = cs[j].getPathPart();
            if(accepts0(cs[j])) t.put(p, cs[j]);
        }
        Object[] keys = t.keySet().toArray();
        XModelObject[] vs = new XModelObject[keys.length];
        for (int i = 0; i < vs.length; i++)
          vs[i] = (XModelObject)t.get(keys[i]);
        return vs;
    }

}
