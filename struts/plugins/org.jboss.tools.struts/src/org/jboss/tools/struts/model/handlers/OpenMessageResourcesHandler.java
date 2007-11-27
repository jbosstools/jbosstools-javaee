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
package org.jboss.tools.struts.model.handlers;

import java.util.*;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.meta.action.impl.handlers.HUtil;

public class OpenMessageResourcesHandler extends AbstractHandler {

    public OpenMessageResourcesHandler() {}

    public boolean isEnabled(XModelObject object) {
        return (object != null);
    }

    public void executeHandler(XModelObject object, Properties p) throws Exception {
        String parameter = object.getAttributeValue("parameter");
        if(parameter == null || parameter.length() == 0) return;
        String path = "/" + parameter.replace('.', '/') + ".properties";
        XModelObject[] os = getResourceObject(object);
        if(os == null || os.length == 0) {
            ServiceDialog d = object.getModel().getService();
            d.showDialog("Warning", "Resource " + path + " is not found.", new String[]{"Ok"}, null, ServiceDialog.WARNING);
        } else {
        	XModelObject selected = (os.length == 1) ? os[0] : select(os);
            if(selected != null) XActionInvoker.invoke("Open", selected, null);
        }
    }
    
    private XModelObject select(XModelObject[] os) {
    	Map<String,XModelObject> map = new TreeMap<String,XModelObject>();
    	for (int i = 0; i < os.length; i++) {
    		String n = FileAnyImpl.toFileName((FileAnyImpl)os[i]);
    		map.put(n, os[i]);
    	}
    	String[] ns = map.keySet().toArray(new String[0]);
    	XEntityData data = XEntityDataImpl.create(new String[][]{
    		{"StrutsSelectResourceWizard"}, {"resource", "yes"}
    	});
    	data.setValue("resource", ns[0]);
    	HUtil.hackAttributeConstraintList(new XEntityData[]{data}, 0, "resource", ns);
    	ServiceDialog d = os[0].getModel().getService();
    	int q = d.showDialog("Open Resource", "Please select resource.", new String[]{"OK", "Cancel"}, data, ServiceDialog.QUESTION);
    	return (q != 0) ? null : map.get(data.getValue("resource"));
    }
    
    public static boolean isReferencingResourceObject(XModelObject strutsResource) {
		String parameter = strutsResource.getAttributeValue("parameter");
		return (parameter != null && parameter.length() > 0);
    }
    
    public static XModelObject[] getResourceObject(XModelObject strutsResource) {
    	if(!isReferencingResourceObject(strutsResource)) return null;
		String parameter = strutsResource.getAttributeValue("parameter");
		String path = "/" + parameter.replace('.', '/') + ".properties";
		XModelObject[] os = getResourceObjectFromModel(strutsResource.getModel(), path);
		if(os != null) return os;
		return getResourceObjectFromClasspath(strutsResource, path);		
    }

    static XModelObject[] getResourceObjectFromClasspath(XModelObject strutsResource, String path) {
		IResource[] ps = EclipseResourceUtil.getClasspathResources(EclipseResourceUtil.getProject(strutsResource));
		for (int i = 0; i < ps.length; i++) {
			XModelObject[] os = getResourceObjectFromClasspathResource(ps[i], path);
			if(os != null && os.length != 0) return os;
		}
		return null;
    }

    static XModelObject[] getResourceObjectFromClasspathResource(IResource resource, String path) {
		if(resource instanceof IProject) {
			IProject p = (IProject)resource;
			IModelNature n = EclipseResourceUtil.getModelNature(p);
			XModel model = (n != null) ? n.getModel() : null;
			if(model == null) {
				XModelObject o = EclipseResourceUtil.createObjectForResource(p);
				if(o != null) model = o.getModel();
			}
			return (model == null) ? null : getResourceObjectFromModel(model, path);
		} else {
			XModelObject o = null;
			XModelObject ro = EclipseResourceUtil.getObjectByResource(resource);
			if(ro == null && resource instanceof IContainer) {
				ro = EclipseResourceUtil.createObjectForResource(resource);
			} else if(ro == null && resource instanceof IFile && resource.getName().endsWith(".jar")) {
				ro = EclipseResourceUtil.createObjectForResource(resource);
				if(ro == null) EclipseResourceUtil.createObjectForResource(resource.getProject());
				if(ro != null) {
					return getResourceObjectFromModel(ro.getModel(), path);
				}
			}
			if(ro == null) return null;
	    	o = ro.getChildByPath(path.substring(1));
	    	if(o == null && ro.getFileType() == XModelObject.FILE) {
	    		o = ro.getModel().getByPath(path);
	    	}
			return o == null ? null : new XModelObject[]{o};
		}
    }

    static XModelObject[] getResourceObjectFromModel(XModel model, String path) {
    	XModelObject o = model.getByPath(path);
		ArrayList<XModelObject> l = new ArrayList<XModelObject>();
		if(o != null) {
			String n = o.getAttributeValue("name");
			int s = n.indexOf("_");
			if(s >= 0) n = n.substring(0, s);
			findInFolder(o.getParent(), n, l);
		} else {
			int s = path.lastIndexOf("/");
			String fileName = path.substring(s + 1);
			String n = fileName.substring(0, fileName.length() - ".properties".length());
			int s_ = n.indexOf("_");
			if(s_ >= 0) n = n.substring(0, s_);
			if(s > 0) {
				XModelObject p = model.getByPath(path.substring(0, s));
				findInFolder(p, n, l);
			} else {
				XModelObject fs = model.getByPath("FileSystems");
				if(fs == null) return null;
				XModelObject[] os = fs.getChildren("FileSystemFolder");
				for (int i = 0; i < os.length; i++) findInFolder(os[i], n, l);
			}
		}
		return l.size() == 0 ? null : l.toArray(new XModelObject[l.size()]);
    }
    
    static void findInFolder(XModelObject parent, String n, ArrayList<XModelObject> list) {
    	if(parent == null) return;
		XModelObject[] cs = parent.getChildren("FilePROPERTIES");
		for (int i = 0; i < cs.length; i++) {
			String in = cs[i].getAttributeValue("name");
			if(in.equals(n) || in.startsWith(n + "_")) list.add(cs[i]);
		}
    }
    
    

}
