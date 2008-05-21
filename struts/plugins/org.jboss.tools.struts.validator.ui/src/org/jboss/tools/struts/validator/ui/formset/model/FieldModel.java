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
package org.jboss.tools.struts.validator.ui.formset.model;

import java.util.*;

public class FieldModel extends FModel {
    public static int DEFINED = 0;
    public static int OVERWRITTEN = 1;
    public static int INHERITED = 2;
    protected FModel defaultDependency = FModel.createInstance(DependencyModel.class, this, "");
    protected String page = "";
    protected String index = "";
    protected int pageStatus = DEFINED;
    protected int indexStatus = DEFINED;

    public FieldModel() {}

    public FModel getDefaultDependency() {
        return defaultDependency;        
    }

    public void reload() {
        objects = FSUtil.getChildren(parent.getModelObjects(), name);

        Map<String,FModel> c = new HashMap<String,FModel>();
        for (int i = 0; i < children.length; i++)
          c.put(children[i].getName(), children[i]);
        Set<String> q = new HashSet<String>();
        ArrayList<FModel> l2 = new ArrayList<FModel>();
        boolean noinh = false;
        for (int i = 0; i < objects.length; i++) {
            boolean inhi = isInherited(objects[i]);
            if(inhi) { if(noinh) continue; } else noinh = true;
            String depends = objects[i].getAttributeValue("depends");
            StringTokenizer st = new StringTokenizer(depends, ",;");
            while(st.hasMoreTokens()) {
                String n = st.nextToken().trim();
                if(n.length() == 0 || q.contains(n)) continue;
                q.add(n);
                FModel f = (FModel)c.remove(n);
                if(f == null) f = FModel.createInstance(DependencyModel.class, this, n);
                l2.add(f);
                f.reload();
            }
        }
        boolean differ = false;
        if(FSUtil.differ(children, l2)) {
            children = l2.toArray(new FModel[0]);
            differ = true;
        }
        differ |= reloadPage();
        differ |= reloadIndex();
        if(differ) fire(this);
        defaultDependency.reload();
        isInherited = objects.length == 0 || isInherited(objects[0]);
        isInheriting = false;
        for (int i = 0; i < objects.length && !isInheriting; i++) isInheriting = isInherited(objects[i]);
    }

    private boolean reloadPage() {
        String p = "";
        int status = -1;
        for (int i = 0; i < objects.length; i++) {
            String pi = objects[i].getAttributeValue("page");
            if(pi == null) pi = "";
            boolean inh = isInherited(objects[i]);
            if(pi.length() > 0) {
                if(p.length() == 0) {
                    p = pi;
                    status = (!inh) ? DEFINED : INHERITED;
                } else {
                    if(!inh && (status == INHERITED)) status = OVERWRITTEN;
                    else if(inh && (status == DEFINED)) status = OVERWRITTEN;
                    if(status == OVERWRITTEN && pi.equals(p)) status = INHERITED;
                }
            }
        }
        if(status < 0) {
            boolean inh = objects.length == 0 || isInherited(objects[0]);
            status = (!inh) ? DEFINED : INHERITED;
        }
        boolean differ = !p.equals(page) || (pageStatus != status);
        page = p;
        pageStatus = status;
        return differ;
    }

    private boolean reloadIndex() {
        String p = "";
        int status = -1;
        for (int i = 0; i < objects.length; i++) {
            String pi = objects[i].getAttributeValue("indexedListProperty");
            if(pi == null) pi = "";
            boolean inh = isInherited(objects[i]);
            if(pi.length() > 0) {
                if(p.length() == 0) {
                    p = pi;
                    status = (!inh) ? DEFINED : INHERITED;
                } else {
                    if(!inh && (status == INHERITED)) status = OVERWRITTEN;
                    else if(inh && (status == DEFINED)) status = OVERWRITTEN;
                }
            }
        }
        if(status < 0) {
            boolean inh = objects.length == 0 || isInherited(objects[0]);
            status = (!inh) ? DEFINED : INHERITED;
        }
        boolean differ = !p.equals(index) || (indexStatus != status);
        index = p;
        indexStatus = status;
        return differ;
    }

    public String getPage() {
        return page;
    }

    public int getPageStatus() {
        return pageStatus;
    }

    public String getIndex() {
        return index;
    }

    public int getIndexStatus() {
        return indexStatus;
    }

    public String getKey() {
        return "Validation_Editor_Field";
    }

}

