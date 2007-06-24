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
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jst.web.project.WebProject;
import org.jboss.tools.jst.web.project.helpers.*;

public class LibrariesPerformer extends PerformerItem {
	XModel model;
	XModelObject lib;
	IContainer libResource;
	
	JarPerformer[] jarPerformers;
	
	public LibrariesPerformer() {}
	
	public String getDisplayName() {
		return "Libraries";
	}

	public IPerformerItem[] getChildren() {
		return jarPerformers;
	}

	public void init(XModel model, XModelObject[] libraryReferences) {
		this.model = model;
		lib = model.getByPath("FileSystems/lib");
		if(lib == null) {
			XModelObject webinf = FileSystemsHelper.getWebInf(model);
			if(webinf != null) lib = webinf.getChildByPath("lib");
		}
		if(lib != null) libResource = (IContainer)EclipseResourceUtil.getResource(lib);
		ArrayList<LibrarySet> l = new ArrayList<LibrarySet>();
		for (int i = 0; i < libraryReferences.length; i++) {
			String n = libraryReferences[i].getAttributeValue("name");
			LibrarySet set = LibrarySets.getInstance().getLibrarySet(n);
			if(set != null) l.add(set);
		}
		LibrarySet[] sets = l.toArray(new LibrarySet[0]);
		ArrayList<JarPerformer> l2 = new ArrayList<JarPerformer>();
		for (int i = 0; i < sets.length; i++) {
			File libraryTemplateRoot = new File(sets[i].getPath());
			if(!libraryTemplateRoot.isDirectory()) continue;
			String[] ss = sets[i].getJarList();
			for (int j = 0; j < ss.length; j++) {
				JarPerformer p = new JarPerformer();
				p.setParent(this);
				p.init(model, sets[i], ss[j]);
				l2.add(p);
			}			
			File[] zs = getZipList(sets[i]);
			for (int j = 0; j < zs.length; j++) {
				JarPerformer p = new JarPerformer();
				p.setParent(this);
				p.init(model, sets[i], zs[j].getName());
				l2.add(p);
			}			
		}
		jarPerformers = l2.toArray(new JarPerformer[0]);
	}
	
	public boolean check() {
		if(!isSelected()) return true;
		if(libResource == null) return false;
		String[] existing = getExistingJars();
		if(existing.length == 0) return true;
		ServiceDialog d =  model.getService();
		String message;
		if(existing.length > 1) 
			message = NLS.bind(JSFUIMessages.PROJECT_ALREADY_HAS_SOME_OF_LIBRARIES_INCLUDED_2, existing[0], "" + (existing.length-1) );
		else
			message = NLS.bind(JSFUIMessages.PROJECT_ALREADY_HAS_SOME_OF_LIBRARIES_INCLUDED, existing[0]);
		
		int q = d.showDialog(JSFUIMessages.WARNING, message, new String[]{JSFUIMessages.OVERWRITE, JSFUIMessages.CANCEL}, null, ServiceDialog.WARNING);
		return q == 0;
	}
	
	private String[] getExistingJars() {
		ArrayList<String> l = new ArrayList<String>();
		for (int i = 0; i < jarPerformers.length; i++) {
			if(!jarPerformers[i].isSelected()) continue;
			IResource r = libResource.findMember(jarPerformers[i].jar);
			if(r != null && r.exists()) l.add(jarPerformers[i].jar);
		}
		return l.toArray(new String[0]);
	}
	
	public boolean execute(PerformerContext context) throws Exception {
		if(!isSelected()) return true;
		boolean changed = false;
		boolean zip = false;
		XModelObject fss = FileSystemsHelper.getFileSystems(model);
		File location = libResource.getLocation().toFile();
		String libName = null;
		XModelObject webinf = FileSystemsHelper.getWebInf(model);
		File webInfDir = ((IResource)webinf.getAdapter(IResource.class)).getLocation().toFile();
        libName = (location.getParentFile().equals(webInfDir))
        ? "%redhat.workspace%/lib/" : location.getAbsolutePath().replace('\\', '/')+"/";
        
        CapabilityPerformer pp = (CapabilityPerformer)getParent();
		String capability = pp.capability.getAttributeValue("name");
		for (int i = 0; i < jarPerformers.length; i++) {
			if(!jarPerformers[i].isSelected()) continue;
			context.monitor.worked(1);
			LibrarySet set = jarPerformers[i].set;
			File libraryTemplateRoot = new File(set.getPath());
			if(!libraryTemplateRoot.isDirectory()) continue;
			String ss = jarPerformers[i].jar;
			File source = new File(libraryTemplateRoot, ss);
			if(!source.isFile()) continue;
			String n = source.getName();
			if(n.endsWith(".jar")) {
				File target = new File(location, ss);
				String task = capability + ": " + "Added ";
				String postfix = "to";
				if(target.isFile()) {
					task = capability + ": " + "Replaced ";
					postfix = "in";
					if(!target.delete()) continue;
				}
				FileUtil.copyFile(source, target, true);
				changed = true;
				String fsName = "lib-" + n;
				if(fss.getChildByPath(fsName) == null) {
	                Properties fsProp = new Properties();
	                fsProp.setProperty("name", fsName);
	                fsProp.setProperty("location", libName + n);
	                fsProp.setProperty("info", "hidden=yes");
	                XModelObject fsJar = XModelObjectLoaderUtil.createValidObject(model, "FileSystemJar", fsProp);
	                if(fss.getChildByPath(fsJar.getPathPart()) == null) {
	                	DefaultCreateHandler.addCreatedObject(fss, fsJar, false, -1);
	                }
				}
				context.changeList.add(task + n + " " + postfix + " WEB-INF/lib");
			} else if(n.endsWith(".zip")) {
				if(!source.isFile()) continue;
				String webRoot = WebProject.getInstance(model).getWebRootLocation();
				try {
					FileUtil.unzip(new File(webRoot), source.getAbsolutePath());
				} catch (Exception e) {
					//ignore
				}
				zip = true;
				context.changeList.add(capability + ": " + "Unpacked " + n + " to " + new File(webRoot).getName());
			}
		}
		if(changed) {
			libResource.refreshLocal(IResource.DEPTH_INFINITE, null);
			model.save();
		}
		if(zip) {
			IProject p = EclipseResourceUtil.getProject(model.getRoot());
			if(p != null) try {
				p.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (Exception e) {
				//ignore
			}
		}
		context.monitor.worked(1);
		return true;
	}
	
	private File[] getZipList(LibrarySet set) {
		ArrayList<File> list = new ArrayList<File>();
		String path = set.getPath();
		File f = new File(path);
		File[] fs = (f.isDirectory()) ? f.listFiles() : null;
		if(fs != null) for (int i = 0; i < fs.length; i++) {
			if(fs[i].getName().endsWith(".zip")) list.add(fs[i]);
		}
		return list.toArray(new File[0]);
	}

}
