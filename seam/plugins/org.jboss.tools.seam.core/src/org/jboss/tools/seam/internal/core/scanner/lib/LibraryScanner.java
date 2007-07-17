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

import java.io.ByteArrayInputStream;
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
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileSystemsImpl;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XModelObjectUtil;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.InnerModelHelper;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;
import org.jboss.tools.seam.internal.core.scanner.xml.PropertiesScanner;
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
		XModel model = InnerModelHelper.createXModel(f.getProject());
		if(model == null) return false;
		XModelObject o = EclipseResourceUtil.getObjectByResource(model, f);
		if(o == null) return false;
		if(!o.getModelEntity().getName().equals("FileSystemJar")) {
			((FileSystemsImpl)o.getModel().getByPath("FileSystems")).updateOverlapped();
			o = EclipseResourceUtil.getObjectByResource(f);
			if(o == null || !o.getModelEntity().getName().equals("FileSystemJar")) return false;
		}
		return isLikelyComponentSource(o);
	}

	public LoadedDeclarations parse(IFile f) throws Exception {
		XModel model = InnerModelHelper.createXModel(f.getProject());
		if(model == null) return null;
		XModelObject o = EclipseResourceUtil.getObjectByResource(model, f);
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
		if(o.getChildByPath("seam.properties") != null) return true;
		if(o.getChildByPath("META-INF/seam.properties") != null) return true;
		if(o.getChildByPath("META-INF/components.xml") != null) return true;
		return false;
	}

	public LoadedDeclarations parse(XModelObject o, IPath path) throws Exception {
		if(o == null) return null;
		sourcePath = path;
		XModelObject seamProperties = o.getChildByPath("META-INF/seam.properties");
		if(seamProperties == null) seamProperties = o.getChildByPath("seam.properties");
		XModelObject componentsXML = o.getChildByPath("META-INF/components.xml");
		if(componentsXML == null && seamProperties == null) return null;
		
		LoadedDeclarations ds = new LoadedDeclarations();

		processJavaClasses(o, ds);
		
		if(componentsXML != null) {
			LoadedDeclarations ds1 = new XMLScanner().parse(componentsXML, path);
			if(ds1 != null) ds.add(ds1);
		}
		if(seamProperties != null) {
			PropertiesScanner scanner = new PropertiesScanner();
			LoadedDeclarations ds1 = scanner.parse(seamProperties, path);
			if(ds1 != null) ds.add(ds1);
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
				processWithClassReader(typeRoot, ds);		
//				processWithClassLoader(typeRoot, ds);
			}
		}
	}
	
	void processWithClassReader(IClassFile typeRoot, LoadedDeclarations ds) {
		IType type = typeRoot.getType();
		
		String className = type.getFullyQualifiedName();
		
		byte[] bs = null;
		
		try {
			bs = typeRoot.getBytes();
		} catch (JavaModelException e) {
			return;
		}
		
		ClassFileReader reader = null;

		try {
			reader = ClassFileReader.read(new ByteArrayInputStream(bs), className, false);
		} catch (Throwable t) {
			//ignore
		}
		
		if(reader == null) return;
		LoadedDeclarations ds1 = null;

		TypeScanner scanner = new TypeScanner();
		try {
			if(!scanner.isLikelyComponentSource(reader)) return;
			ds1 = scanner.parse(type, reader, sourcePath);
		} catch (Throwable t) {
			System.out.println("failed " + className);
//			SeamCorePlugin.getPluginLog().logError(t);
		}

		if(ds1 != null) {
//			System.out.println("declarations found in " + className);
			ds.add(ds1);
		}
	}
	
	void processWithClassLoader(IClassFile typeRoot, LoadedDeclarations ds) {
		IType type = typeRoot.getType();
		String className = type.getFullyQualifiedName();
		
		Class<?> cls = null;
		try {
			cls = classPath.getClassLoader().loadClass(className);
		} catch (NoClassDefFoundError e) {
			//ignore
		} catch (ClassNotFoundException e) {
			//ignore
		}
		if(cls == null) return;
		if(!CLASS_SCANNER.isLikelyComponentSource(cls)) return;
		LoadedDeclarations ds1 = null;
		try {
			ds1 = CLASS_SCANNER.parse(type, cls, sourcePath);
		} catch (Exception e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		if(ds1 != null) {
			ds.add(ds1);
		}
//		System.out.println(className);
	}
	
	

}
