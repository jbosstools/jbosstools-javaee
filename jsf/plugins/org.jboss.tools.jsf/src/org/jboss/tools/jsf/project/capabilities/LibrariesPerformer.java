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
import java.io.IOException;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.filesystems.impl.Libs;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jst.web.project.WebProject;
import org.jboss.tools.jst.web.project.helpers.*;

public class LibrariesPerformer extends PerformerItem {
	XModel model;
	XModelObject lib;
	IContainer libResource;
	
	JarPerformer[] jarPerformers;
	IFile[] conflictingFiles;
	
	public LibrariesPerformer() {}
	
	public String getDisplayName() {
		return "Libraries";
	}

	public IPerformerItem[] getChildren() {
		return jarPerformers;
	}

	public void init(XModel model, XModelObject[] libraryReferences, XModelObject[] conflictingLibraryReferences) {
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
		
		conflictingFiles = null;
		IContainer r = lib == null ? null : (IContainer)lib.getAdapter(IResource.class);
		if(conflictingLibraryReferences != null && lib != null && r != null) {
			ArrayList<IFile> cfjs = new ArrayList<IFile>();
			for (int i = 0; i < conflictingLibraryReferences.length; i++) {
				String name = conflictingLibraryReferences[i].getAttributeValue("name");
				collectFiles(r, name, cfjs);
			}
			conflictingFiles = cfjs.toArray(new IFile[0]);
		}
	}

	private void collectFiles(IContainer r, String name, ArrayList<IFile> cfjs) {
		int wildcard = name.indexOf('*');
		if(wildcard < 0) {
			IFile f = r.getFile(new Path(name));
			if(f.exists()) cfjs.add(f);
		} else {
			IResource[] rs = null;
			try {
				rs = r.members();
			} catch (CoreException e) {
				ModelPlugin.getPluginLog().logError(e);
			}
			if(rs != null) {
				String prefix = name.substring(0, wildcard);
				String suffix = name.substring(wildcard);
				for (int i = 0; i < rs.length; i++) {
					if(!(rs[i] instanceof IFile)) continue;
					String n = rs[i].getName();
					if(prefix.length() > 0 && !n.startsWith(prefix)) continue;
					if(suffix.length() > 0 && !n.endsWith(suffix)) continue;
					cfjs.add((IFile)rs[i]);
				}
			}
		}
	}
	
	public boolean check(PerformerContext context) {
		if(!isSelected()) return true;
		if(libResource == null) return false;
		ServiceDialog d =  model.getService();

		if(conflictingFiles != null && conflictingFiles.length > 0) {
			String message = NLS.bind(JSFUIMessages.PROJECT_HAS_COFLICTING_LIBRARIES, conflictingFiles[0].getName() );
			int q = d.showDialog(JSFUIMessages.WARNING, message, new String[]{JSFUIMessages.YES, JSFUIMessages.NO, JSFUIMessages.CANCEL}, null, ServiceDialog.WARNING);
			if(q == 2) {
				return false;
			}
			if(q == 1) {
				conflictingFiles = null;
				if(jarPerformers != null) for (int i = 0; i < jarPerformers.length; i++) jarPerformers[i].setSelected(false);
				return true;
			}
		}
		
		String[] existing = getExistingJars();
		if(existing.length == 0) return true;
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
	
	public boolean execute(PerformerContext context) throws XModelException {
		if(!isSelected()) return true;
		boolean changed = false;
		boolean zip = false;
		XModelObject fss = FileSystemsHelper.getFileSystems(model);
		File location = libResource.getLocation().toFile();
		String libName = null;
		XModelObject webinf = FileSystemsHelper.getWebInf(model);
		File webInfDir = ((IResource)webinf.getAdapter(IResource.class)).getLocation().toFile();
        libName = (location.getParentFile().equals(webInfDir))
        ? XModelConstants.WORKSPACE_REF + "/lib/" : location.getAbsolutePath().replace('\\', '/')+"/";
        
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
				String fsName = Libs.LIB_PREFIX + n;
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
				} catch (IOException e) {
					//ignore
				}
				zip = true;
				context.changeList.add(capability + ": " + "Unpacked " + n + " to " + new File(webRoot).getName());
			}
		}
		if(changed) {
			try {
				libResource.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				throw new XModelException(e);
			}
			model.save();
		}
		if(zip) {
			IProject p = EclipseResourceUtil.getProject(model.getRoot());
			if(p != null) try {
				p.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				//ignore
			}
		}
		
		if(conflictingFiles != null) {
			for (int i = 0; i < conflictingFiles.length; i++) {
				try {
					conflictingFiles[i].delete(true, context.monitor);
					context.changeList.add(capability + ": Removed " + conflictingFiles[i].getName() + " from WEB-INF/lib");
				} catch (CoreException ce) {
					JSFModelPlugin.getPluginLog().logError(ce);
				}
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
