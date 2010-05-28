/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core.scanner.lib;

import java.io.ByteArrayInputStream;
import java.io.IOException;

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
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileSystemsImpl;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XModelObjectUtil;
import org.jboss.tools.jst.web.model.helpers.InnerModelHelper;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.internal.core.SeamNamespace;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;
import org.jboss.tools.seam.internal.core.scanner.ScannerException;
import org.jboss.tools.seam.internal.core.scanner.java.SeamAnnotations;
import org.jboss.tools.seam.internal.core.scanner.xml.PropertiesScanner;
import org.jboss.tools.seam.internal.core.scanner.xml.XMLScanner;

/**
 * @author Viacheslav Kabanovich
 */
public class LibraryScanner implements IFileScanner {
	ClassPath classPath = null;
	
	//Now it is absolute file on disk
	IPath sourcePath = null;
	
	public LibraryScanner() {}
	
	public void setClassPath(ClassPath classPath) {
		this.classPath = classPath;
	}

	public boolean isRelevant(IFile f) {
		if(EclipseResourceUtil.isJar(f.getName())) return true;
		return false;
	}

	XModelObject o = null;
	XModel model = null;
	
	public boolean isLikelyComponentSource(IFile f) {
		cleanState();
		boolean isComponent = false;
		try {
			model = InnerModelHelper.createXModel(f.getProject());
			if(model != null) {
				o = EclipseResourceUtil.getObjectByResource(model, f);
				if(o != null && o.getModelEntity().getName().equals("FileSystemJar")) {
					((FileSystemsImpl)o.getModel().getByPath("FileSystems")).updateOverlapped(); //$NON-NLS-1$
					o = EclipseResourceUtil.getObjectByResource(f);
					if(o != null && o.getModelEntity().getName().equals("FileSystemJar")) {
						isComponent =  isLikelyComponentSource(o);
					}
				}
			}
		} finally {	
			if(!isComponent) {
				cleanState();
			}
		}
		return isComponent;
	}

	private void cleanState() {
		model = null;
		o = null;
	}

	public LoadedDeclarations parse(IFile f, ISeamProject sp) throws ScannerException {
		LoadedDeclarations decls = null;
		if(o!=null) {
			decls = parse(o, f.getFullPath(), sp);
		}
		return decls;
	}

	public static final boolean isLikelyComponentSource(XModelObject o) {
		return o.getChildByPath("seam.properties") != null 
			|| o.getChildByPath("META-INF/seam.properties") != null
			|| o.getChildByPath("META-INF/components.xml") != null;
	}

	public LoadedDeclarations parse(XModelObject o, IPath path, ISeamProject sp) throws ScannerException {
		sourcePath = path;
		XModelObject seamProperties = o.getChildByPath("META-INF/seam.properties"); //$NON-NLS-1$
		if(seamProperties == null) seamProperties = o.getChildByPath("seam.properties"); //$NON-NLS-1$
		XModelObject componentsXML = o.getChildByPath("META-INF/components.xml"); //$NON-NLS-1$
		if(componentsXML == null && seamProperties == null) return null;
		
		LoadedDeclarations ds = new LoadedDeclarations();

		try {
			processJavaClasses(o, ds);
		} catch (JavaModelException e) {
			throw new ScannerException(SeamCoreMessages.LIBRARY_SCANNER_CANNOT_PROCESS_JAVA_CLASSES, e);
		} catch (ClassFormatException e) {
			throw new ScannerException(SeamCoreMessages.LIBRARY_SCANNER_CANNOT_PROCESS_JAVA_CLASSES, e);
		} catch (IOException e) {
			throw new ScannerException(SeamCoreMessages.LIBRARY_SCANNER_CANNOT_PROCESS_JAVA_CLASSES, e);
		}
		
		if(componentsXML != null) {
			LoadedDeclarations ds1 = new XMLScanner().parse(componentsXML, path, sp);
			if(ds1 != null) ds.add(ds1);
		}
		if(seamProperties != null) {
			PropertiesScanner scanner = new PropertiesScanner();
			LoadedDeclarations ds1 = scanner.parse(seamProperties, path);
			if(ds1 != null) ds.add(ds1);
		}		
		
		return ds;
	}
	
	protected void processJavaClasses(XModelObject o, LoadedDeclarations ds) throws JavaModelException, ClassFormatException, IOException {
		IJavaProject javaProject = JavaCore.create(classPath.getProject().getProject());
		String location = o.getAttributeValue("location"); //$NON-NLS-1$
		location = XModelObjectUtil.expand(location, o.getModel(), null);
		
		IFile[] fs = ModelPlugin.getWorkspace().getRoot().findFilesForLocation(new Path(location));
		IPackageFragmentRoot root = null;
		if(fs != null) { 
			for (int i = 0; i < fs.length && root == null; i++) {
				root = javaProject.findPackageFragmentRoot(fs[i].getFullPath());
			}
			if(root == null) {
				root = javaProject.findPackageFragmentRoot(new Path(location));
				if(root != null) {
					process(root, ds);
				}				
			}
		} 
	}
	
	protected void process(IParent element, LoadedDeclarations ds) throws JavaModelException, ClassFormatException, IOException {
		if(element == null) return;
		IJavaElement[] es = element.getChildren();
		String prefix = null;
		for (int i = 0; i < es.length; i++) {
			if(es[i] instanceof IClassFile) {
				IClassFile typeRoot = (IClassFile)es[i];
				if(es[i].getElementName().equals("package-info.class")) {
					prefix = processPackageInfo(typeRoot, ds);
					break;
				}
			}
		}
		for (int i = 0; i < es.length; i++) {
			if(es[i] instanceof IPackageFragment) {
				process((IPackageFragment)es[i], ds);
			} else if(es[i] instanceof IClassFile) {
				IClassFile typeRoot = (IClassFile)es[i];
				if(es[i].getElementName().equals("package-info.class")) {
					continue;
				} else {
					processWithClassReader(typeRoot, ds, prefix);
				}
			}
		}
	}
	
	void processWithClassReader(IClassFile typeRoot, LoadedDeclarations ds, String prefix) throws JavaModelException, ClassFormatException, IOException {
		IType type = typeRoot.getType();
		
		ClassFileReader reader = getReader(type, typeRoot);

		if(reader == null) return;
		LoadedDeclarations ds1 = null;

		//TODO use prefix
		TypeScanner scanner = new TypeScanner();
		if(!scanner.isLikelyComponentSource(reader)) return;
		ds1 = scanner.parse(type, reader, sourcePath);

		if(ds1 != null) {
			ds.add(ds1);
		}
	}

	String processPackageInfo(IClassFile typeRoot, LoadedDeclarations ds) throws JavaModelException, ClassFormatException, IOException {
		IType type = typeRoot.getType();
		
		ClassFileReader reader = getReader(type, typeRoot);
		
		if(reader == null) return null;
		IBinaryAnnotation[] as = reader.getAnnotations();
		IBinaryAnnotation namespaceAnnotation = getNamespaceAnnotation(as);
		if(namespaceAnnotation == null) return null;
		String uri = TypeScanner.getValue(namespaceAnnotation, "value");
		String prefix = TypeScanner.getValue(namespaceAnnotation, "prefix");
		if(uri == null) return null;
		
		String className = type.getFullyQualifiedName();
		int i = className.indexOf(".package-info");
		if(i < 0) return null;
		String packageName = className.substring(0, i);

		SeamNamespace n = new SeamNamespace();
		n.setSourcePath(sourcePath);
		n.setURI(uri);
		n.setPackage(packageName);

		ds.getNamespaces().add(n);

		return prefix;
	}

	static IBinaryAnnotation getNamespaceAnnotation(IBinaryAnnotation[] as) {
		if(as != null) {
			for (int i = 0; i < as.length; i++) {
				String type = TypeScanner.getTypeName(as[i]);
				if(type != null && type.equals(SeamAnnotations.NAMESPACE_ANNOTATION_TYPE)) {
					return as[i];
				}
			}
		}
		return null;
	}

	private ClassFileReader getReader(IType type, IClassFile typeRoot) throws JavaModelException, ClassFormatException, IOException {
		String className = type.getFullyQualifiedName();
		ClassFileReader newReader = null;
		byte[] bs = null;
		
			bs = typeRoot.getBytes();
			return ClassFileReader.read(new ByteArrayInputStream(bs), className, false);
	}

}
