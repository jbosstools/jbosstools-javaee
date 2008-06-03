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
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.validators.model.ValidatorConstants;
import org.jboss.tools.struts.validators.model.XModelEntityResolver;

public class FormsetsModel extends FModel {
    protected String language = "";
    protected String country = "";
    protected XModelObject root = null;
    protected FModelListener listener = null;
    protected FModel constants = FModel.createInstance(FConstantsModel.class, this, "Constants");
    protected FModel empty = FModel.createInstance(FModel.class, this, "No one frameset is created");
    protected XModelObject[] distinctFormsets = new XModelObject[0];

    public FormsetsModel() {}

	public void dispose() {
		super.dispose();
		listener = null;
		if (constants!=null) constants.dispose();
		constants = null;
		if (empty!=null) empty.dispose();
		empty = null;
	}

	public XModel getModel() {
        return (root == null) ? null : root.getModel();
    }

    public void addListener(FModelListener listener) {
        this.listener = listener;
    }

    public int getChildCount() {
        return (root == null || objects.length == 0) ? 0 : children.length + 1;
    }

    public FModel getChildAt(int i) {
        return (i > 0) ? super.getChildAt(i - 1) : constants;
    }

    public XModelObject[] getDistinctFormsets() {
        return distinctFormsets;
    }

    public FModel getConstantsModel() {
        return constants;
    }

    public String[] getLanguages() {
        if(root == null) return new String[0];
        XModelObject[] os = XModelEntityResolver.getResolvedChildren(root, ValidatorConstants.ENT_FORMSET);
        TreeSet<String> s = new TreeSet<String>();
        for (int i = 0; i < os.length; i++) s.add(os[i].getAttributeValue("language"));
        return s.toArray(new String[0]);
    }

    public String[] getCountries(String language) {
        if(root == null) return new String[0];
        XModelObject[] os = XModelEntityResolver.getResolvedChildren(root, ValidatorConstants.ENT_FORMSET);
        TreeSet<String> s = new TreeSet<String>();
        for (int i = 0; i < os.length; i++) {
            if(os[i].getAttributeValue("language").equals(language))
              s.add(os[i].getAttributeValue("country"));
        }
        return s.toArray(new String[0]);
    }

    public XModelObject[] getCurrentFormsets() {
        ArrayList<XModelObject> list = new ArrayList<XModelObject>();
        for (int i = 0; i < objects.length; i++) {
            String l = objects[i].getAttributeValue("language");
            String c = objects[i].getAttributeValue("country");
            if(l.equals(language) && c.equals(country)) list.add(objects[i]);
        }
        return list.toArray(new XModelObject[0]);
    }

    public void setLanguage(String language, String country) {
        if(this.language.equals(language) &&
           this.country.equals(country)) return;
        this.language = language;
        this.country = country;
        reload();
    }

    public void setObject(XModelObject root) {
        this.root = root;
        reload();
    }

    public boolean isEditable() {
        return root != null && root.isObjectEditable();
    }

    public XModelObject getModelObject() {
        return root;
    }

    public void clear() {
        objects = new XModelObject[0];
        children = new FModel[0];
        fire(this);
    }

    public void reload() {
        if(root == null) {
            clear();
        } else {
            boolean differ = reloadDistinctFormsets();
            XModelObject[] fs = XModelEntityResolver.getResolvedChildren(root, ValidatorConstants.ENT_FORMSET);
            ArrayList<XModelObject> list = new ArrayList<XModelObject>();
            for (int i = 0; i < fs.length; i++) {
                String l = fs[i].getAttributeValue("language");
                String c = fs[i].getAttributeValue("country");
                if(l.length() > 0 && !l.equals(language)) continue;
                if(c.length() > 0 && !c.equals(country)) continue;
                list.add(fs[i]);
            }
            XModelObject[] os = (XModelObject[])list.toArray(new XModelObject[0]);
			Arrays.sort(os, new FSComparator());
            differ |= FSUtil.differ(objects, os);
			objects = os;
            constants.reload();
            differ |= reloadForms();
            if(differ) fire(this);
        }
    }

    private boolean reloadDistinctFormsets() {
        Map<String,XModelObject> s = new TreeMap<String,XModelObject>();
        XModelObject[] fs = XModelEntityResolver.getResolvedChildren(root, ValidatorConstants.ENT_FORMSET);
        for (int i = 0; i < fs.length; i++) {
            String lc = fs[i].getAttributeValue("language") + "_" + fs[i].getAttributeValue("country");
            if(!s.containsKey(lc)) s.put(lc, fs[i]);
        }
        XModelObject[] df = s.values().toArray(new XModelObject[0]);
        boolean differ = FSUtil.differ(distinctFormsets, df);
        if(differ) distinctFormsets = df;
        return differ;
    }

    private boolean reloadForms() {
        Map<String,FModel> c = new HashMap<String,FModel>();
        for (int i = 0; i < children.length; i++)
          c.put(children[i].getName(), children[i]);
        Set<String> q = new HashSet<String>();
        ArrayList<FModel> l2 = new ArrayList<FModel>();
        for (int i = 0; i < objects.length; i++) {
            XModelObject[] fs = XModelEntityResolver.getResolvedChildren(objects[i], ValidatorConstants.ENT_FORM);
            for (int j = 0; j < fs.length; j++) {
                String n = fs[j].getAttributeValue("name");
                if(q.contains(n)) continue;
                q.add(n);
                FModel f = (FModel)c.remove(n);
                if(f == null) f = FModel.createInstance(FormModel.class, this, n);
                f.reload();
                l2.add(f);
            }
        }
        boolean differ = FSUtil.differ(children, l2);
        if(differ) children = (FModel[])l2.toArray(new FModel[0]);
        return differ;
    }

    public boolean isInherited(XModelObject formset) {
        return !language.equals(formset.getAttributeValue("language")) ||
               !country.equals(formset.getAttributeValue("country"));
    }

    class FSComparator implements Comparator<XModelObject> {
        public int compare(XModelObject m1, XModelObject m2) {
            int l1 = m1.getAttributeValue("country").length();
            int l2 = m2.getAttributeValue("country").length();
            if(l1 != l2) return (l1 < l2) ? 1 : -1;
            l1 = m1.getAttributeValue("language").length();
            l2 = m2.getAttributeValue("language").length();
            return (l1 < l2) ? 1 : (l1 > l2) ? -1 : 0;
        }
        public boolean equals(Object obj) {
            return true;
        }
    }

    public void fire(FModel source) {
        if(listener != null) listener.modelChanged(source);
    }

    public String toString() {
        return (getModelObjects().length == 0) ? "No one frameset is created" : getModelObjects()[0].getPresentationString();
    }

    public String getKey() {
        return "Validation_Editor_Formset";
    }

}

