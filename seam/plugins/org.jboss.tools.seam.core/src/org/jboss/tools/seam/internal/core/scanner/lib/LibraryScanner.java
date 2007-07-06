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
package org.jboss.tools.seam.internal.core.scanner.lib;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileSystemsImpl;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XModelObjectUtil;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.SeamPropertiesDeclaration;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;
import org.jboss.tools.seam.internal.core.scanner.xml.XMLScanner;

/**
 * @author Viacheslav Kabanovich
 */
public class LibraryScanner implements IFileScanner {
	static ClassScanner CLASS_SCANNER = new ClassScanner();
	ClassPath classPath = null;
	
	//Now it is absolute file on disk
	IPath sourcePath = null;
	
	public LibraryScanner() {}
	
	public void setClassPath(ClassPath classPath) {
		this.classPath = classPath;
	}

	public boolean isRelevant(IFile f) {
		if(f.getName().endsWith(".jar")) return true;
		return false;
	}

	public boolean isLikelyComponentSource(IFile f) {
		XModelObject o = EclipseResourceUtil.getObjectByResource(f);
		if(o == null) return false;
		if(!o.getModelEntity().getName().equals("FileSystemJar")) {
			((FileSystemsImpl)o.getModel().getByPath("FileSystems")).updateOverlapped();
			o = EclipseResourceUtil.getObjectByResource(f);
			if(o == null || !o.getModelEntity().getName().equals("FileSystemJar")) return false;
		}
		return isLikelyComponentSource(o);
	}

	public LoadedDeclarations parse(IFile f) throws Exception {
		XModelObject o = EclipseResourceUtil.getObjectByResource(f);
		if(o == null) return null;
		if(!o.getModelEntity().getName().equals("FileSystemJar")) {
			((FileSystemsImpl)o.getModel().getByPath("FileSystems")).updateOverlapped();
			o = EclipseResourceUtil.getObjectByResource(f);
			if(o == null || !o.getModelEntity().getName().equals("FileSystemJar")) return null;
		}
		return parse(o, f.getFullPath());
	}

	public boolean isLikelyComponentSource(XModelObject o) {
		if(o == null) return false;
		if(o.getChildByPath("META-INF/seam.properties") != null) return true;
		if(o.getChildByPath("META-INF/components.xml") != null) return true;
		return false;
	}

	public LoadedDeclarations parse(XModelObject o, IPath path) throws Exception {
		if(o == null) return null;
		sourcePath = path;
		XModelObject seamProperties = o.getChildByPath("META-INF/seam.properties");
		XModelObject componentsXML = o.getChildByPath("META-INF/components.xml");
		if(componentsXML == null && seamProperties == null) return null;
		
		LoadedDeclarations ds = new LoadedDeclarations();

		processJavaClasses(o, ds);
		
		if(componentsXML != null) {
			LoadedDeclarations ds1 = new XMLScanner().parse(componentsXML, path);
			if(ds1 != null) ds.add(ds1);
		}
		if(seamProperties != null) {
			XModelObject[] properties = seamProperties.getChildren();
			Map<String, SeamPropertiesDeclaration> ds1 = new HashMap<String, SeamPropertiesDeclaration>();
			for (int i = 0; i < properties.length; i++) {
				String name = properties[i].getAttributeValue("name");
				String value = properties[i].getAttributeValue("value");
				int q = name.lastIndexOf('.');
				if(q < 0) continue;
				String componentName = name.substring(0, q);
				String propertyName = name.substring(q + 1);
				SeamPropertiesDeclaration d = ds1.get(componentName);
				if(d == null) {
					d = new SeamPropertiesDeclaration();
					d.setId(properties[i]);
					d.setSourcePath(path);
					d.setName(componentName);
					ds1.put(componentName, d);
				}
				d.addStringProperty(propertyName, value);
			}
			ds.getComponents().addAll(ds1.values());
		}		
		
		return ds;
	}
	
	protected void processJavaClasses(XModelObject o, LoadedDeclarations ds) throws JavaModelException {
		IJavaProject javaProject = JavaCore.create(classPath.getProject().getProject());
		String location = o.getAttributeValue("location");
		location = XModelObjectUtil.expand(location, o.getModel(), null);
		
		IFile[] fs = ModelPlugin.getWorkspace().getRoot().findFilesForLocation(new Path(location));
		IPackageFragmentRoot root = null;
		if(fs != null) for (int i = 0; i < fs.length && root == null; i++) {
			root = javaProject.findPackageFragmentRoot(fs[i].getFullPath());
		}
		if(root == null) {
			root = javaProject.findPackageFragmentRoot(new Path(location));
		}
		if(root == null) return;
		process(root, ds);
		
	}
	
	protected void process(IParent element, LoadedDeclarations ds) throws JavaModelException {
		if(element == null) return;
		IJavaElement[] es = element.getChildren();
		for (int i = 0; i < es.length; i++) {
			if(es[i] instanceof IPackageFragment) {
				process((IPackageFragment)es[i], ds);
			} else if(es[i] instanceof IClassFile) {
				IClassFile typeRoot = (IClassFile)es[i];
				IType type = typeRoot.getType();
				String className = type.getFullyQualifiedName();
				if(className.equals("org.jboss.seam.core.TransactionListener")) {
//					System.out.println("!!");
				}
				
				Class<?> cls = null;
				try {
					cls = classPath.getClassLoader().loadClass(className);
				} catch (NoClassDefFoundError e) {
					//ignore
				} catch (ClassNotFoundException e) {
					//ignore
				}
				if(cls == null) continue;
				if(!CLASS_SCANNER.isLikelyComponentSource(cls)) continue;
				LoadedDeclarations ds1 = null;
				try {
					ds1 = CLASS_SCANNER.parse(type, cls, sourcePath);
				} catch (Exception e) {
					SeamCorePlugin.getPluginLog().logError(e);
				}
				if(ds1 != null) {
					ds.add(ds1);
				}
//				System.out.println(className);
			}
		}
	}
	
	

}
