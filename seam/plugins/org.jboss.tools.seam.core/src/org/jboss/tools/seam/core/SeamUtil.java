/******************************************************************************* 
 * Copyright (c) 2008 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.core;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;
import org.eclipse.jst.j2ee.project.EarUtilities;
import org.eclipse.jst.j2ee.project.WebUtilities;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.jboss.tools.common.model.project.ext.ITextSourceReference;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.internal.core.AbstractContextVariable;
import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;

/**
 * @author Alexey Kazakov
 */
public class SeamUtil {

	/**
	 * Returns Seam version from <Seam Runtime>/lib/jboss-seam.jar/META-INF/MANIFEST.MF
	 * from Seam Runtime which is set for the project.
	 * @param project
	 * @return
	 */
	public static String getSeamVersionFromManifest(IProject project) {
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, false);
		if(seamProject == null) {
			SeamCorePlugin.getPluginLog().logWarning("Can't find Seam Project for " + project.getName());
			return null;
		}
		return getSeamVersionFromManifest(seamProject);
	}

	/**
	 * Returns Seam version from <Seam Runtime>/lib/jboss-seam.jar/META-INF/MANIFEST.MF
	 * from Seam Runtime which is set for the project.
	 * @param project
	 * @return
	 */
	public static String getSeamVersionFromManifest(ISeamProject project) {
		SeamRuntime runtime = project.getRuntime();
		if(runtime==null) {
			SeamCorePlugin.getPluginLog().logWarning("Seam Runtime for " + project.getProject().getName() + " is null.");
			return null;
		}
		return getSeamVersionFromManifest(runtime);
	}

	private final static String seamJarName = "jboss-seam.jar";
	private final static String seamVersionAttributeName = "Seam-Version";

	/**
	 * Returns Seam version from <Seam Runtime>/lib/jboss-seam.jar/META-INF/MANIFEST.MF
	 * from Seam Runtime.
	 * @param runtime
	 * @return
	 */
	public static String getSeamVersionFromManifest(SeamRuntime runtime) {
		return getSeamVersionFromManifest(runtime.getHomeDir());
	}

	/**
	 * Returns trimmed Seam version from <SeamHome>/lib/jboss-seam.jar/META-INF/MANIFEST.MF
	 * @param depth - number of segments of Seam version string. 
	 * @param seamHome
	 * @return
	 */
	public static String getSeamVersionFromManifest(String seamHome, int depth) {
		return trimSeamVersion(getSeamVersionFromManifest(seamHome), depth);
	}

	/**
	 * Returns true if two versions are matched.
	 * For example "2.1.1.GA" matches "2.1"
	 * @param version1
	 * @param version2
	 * @return
	 */
	public static boolean areSeamVersionsMatched(String version1, String version2) {
		String longerVersion = version1.length()>version2.length()?version1:version2;
		String shorterVersion = longerVersion==version1?version2:version1;
		StringTokenizer sSt = new StringTokenizer(shorterVersion, ".", false);
		StringTokenizer lSt = new StringTokenizer(longerVersion, ".", false);
		while (sSt.hasMoreElements() && lSt.hasMoreElements()) {
			if(!sSt.nextToken().equals(lSt.nextToken())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns Seam version from <SeamHome>/lib/jboss-seam.jar/META-INF/MANIFEST.MF
	 * @param seamHome
	 * @return
	 */
	public static String getSeamVersionFromManifest(String seamHome) {
		File jarFile = new File(seamHome, "lib/" + seamJarName);
		if(!jarFile.isFile()) {
			jarFile = new File(seamHome, seamJarName);
			if(!jarFile.isFile()) {
				SeamCorePlugin.getPluginLog().logWarning(jarFile.getAbsolutePath() + " as well as " + new File(seamHome, "lib/" + seamJarName).getAbsolutePath() + " don't exist.");
				return null;
			}
		}
		try {
			JarFile jar = new JarFile(jarFile);
			Attributes attributes = jar.getManifest().getMainAttributes();
			String version = attributes.getValue(seamVersionAttributeName);
			if(version==null) {
				SeamCorePlugin.getPluginLog().logWarning("Can't get Seam-Version from " + jar.getName() + " MANIFEST.");
			}
			return version;
		} catch (IOException e) {
			SeamCorePlugin.getPluginLog().logError(e);
			return null;
		}
	}

	/**
	 * Trims Seam version string.
	 * For example "2.1.0.SP1" will be trimmed to 2.1 (depth=2); "2.1.1.GA" -> "2.1.1" (depth=3); "2.0.0.SP1" -> "2.0.0.SP1" (depth<1)
	 * @param fullSeamVersion
	 * @param depth - number of segments of Seam version string.
	 * @return
	 */
	public static String trimSeamVersion(String fullSeamVersion, int depth) {
		if(fullSeamVersion==null || depth<1) {
			return fullSeamVersion;
		}
		StringBuffer version = new StringBuffer();
		StringTokenizer st = new StringTokenizer(fullSeamVersion, ".", false);
		while (st.hasMoreElements() && depth>0) {
			version.append(st.nextToken());
			depth--;
			if(st.hasMoreElements() && depth>0) {
				version.append('.');
			}
		}
		return version.toString();
	}

	/**
	 * Converts seam project name to string which suitable for package names
	 * @param projectNamePackage
	 * @return
	 */
	public static String getSeamPackageName(String projectName){
		if(projectName == null)
			return null;
		
		String packageName = projectName.toLowerCase();
		
		if(packageName.indexOf(" ") >= 0)
			packageName = packageName.replaceAll(" ", "");
		
		if(packageName.indexOf("-") >= 0)
			packageName = packageName.replaceAll("-", "");
			
		if(packageName.indexOf("+") >= 0)
			packageName = packageName.replaceAll("+", "");
			
		if(packageName.indexOf("_") >= 0)
			packageName = packageName.replaceAll("_", "");
			
		while(packageName.indexOf("..") >= 0){
			packageName = packageName.replace("..", ".");
		}
		return packageName;
	}

	/**
	 * Finds referencing Seam war project
	 * @param project
	 * @return
	 */
	public static ISeamProject findReferencingSeamWarProjectForProject(IProject project) {
		return findReferencingSeamWarProjectForProject(project, true);
	}

	/**
	 * Finds referencing Seam war project
	 * @param project
	 * @param searchInEARs if "true" then try to search web projects in parent EAR project.
	 * @return
	 */
	public static ISeamProject findReferencingSeamWarProjectForProject(IProject project, boolean searchInEARs) {
		IProject[] referencing = J2EEProjectUtilities.getReferencingWebProjects(project);
		for (int i = 0; i < referencing.length; i++) {
			ISeamProject seamProject = SeamCorePlugin.getSeamProject(referencing[i], false);
			if(seamProject!=null) {
				return seamProject;
			}
		}
		if(searchInEARs) {
			referencing = EarUtilities.getReferencingEARProjects(project);
			for (int i = 0; i < referencing.length; i++) {
				ISeamProject seamProject = findReferencingSeamWarProjectForProject(referencing[i], false);
				if(seamProject!=null) {
					return seamProject;
				}
			}
			IVirtualComponent comp = ComponentCore.createComponent(project);
			IVirtualReference[] refComponents = null;
			if(comp!=null) {
				refComponents = comp.getReferences();
				for (IVirtualReference virtualReference : refComponents) {
					IVirtualComponent component = virtualReference.getReferencedComponent();
					if(component!=null && !component.isBinary() && WebUtilities.isDynamicWebComponent(component)) {
						ISeamProject seamProject = SeamCorePlugin.getSeamProject(component.getProject(), false);
						if(seamProject!=null) {
							return seamProject;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * @param element
	 * @return Resource of seam model element
	 */
	public static IResource getComponentResourceWithName(ISeamElement element) {
		if(element instanceof ISeamComponent) {
			Set<ISeamComponentDeclaration> declarations = ((ISeamComponent)element).getAllDeclarations();
			for (Object o : declarations) {
				SeamComponentDeclaration d = (SeamComponentDeclaration)o;
				ITextSourceReference location = d.getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
				if(!isEmptyLocation(location)) {
					return d.getResource();
				}
			}
		}
		return element.getResource();
	}

	/**
	 * @param seam model element
	 * @return location of name attribute
	 */
	public static ITextSourceReference getLocationOfName(ISeamElement element) {
		return getLocationOfAttribute(element, SeamComponentDeclaration.PATH_OF_NAME);
	}

	/**
	 * @param seam model element
	 * @return location of attribute
	 */
	public static ITextSourceReference getLocationOfAttribute(ISeamElement element, String attributeName) {
		ITextSourceReference location = null;
		if(element instanceof AbstractContextVariable) {
			location = ((AbstractContextVariable)element).getLocationFor(attributeName);
		} else if(element instanceof ISeamComponent) {
			Set<ISeamComponentDeclaration> declarations = ((ISeamComponent)element).getAllDeclarations();
			for (ISeamComponentDeclaration d : declarations) {
				location = ((SeamComponentDeclaration)d).getLocationFor(attributeName);
				if(!isEmptyLocation(location)) {
					break;
				}
			}
		} else if(element instanceof SeamComponentDeclaration) {
			location = ((SeamComponentDeclaration)element).getLocationFor(attributeName);
		}
		if(isEmptyLocation(location) && element instanceof ITextSourceReference) {
			location = (ITextSourceReference)element;
		}
		return location;
	}

	public static boolean isEmptyLocation(ITextSourceReference location) {
		return (location == null
			//is dead location, we cannot now change provider to return null
			//because it may give rise to other errors. 
			//In the future, null should be returned instead of 'dead' location
			//and correctly processed
			|| location.getStartPosition() == 0 && location.getLength() == 0);		
	}

	/**
	 * @param resource
	 * @return true if resource is Jar file
	 */
	public static boolean isJar(IPath path) {
		if(path == null) {
			throw new IllegalArgumentException(SeamCoreMessages.SEAM_VALIDATION_HELPER_RESOURCE_MUST_NOT_BE_NULL);
		}
		String ext = path.getFileExtension();
		return ext != null && ext.equalsIgnoreCase("jar"); //$NON-NLS-1$
	}

	/**
	 * @param element
	 * @return true if seam element packed in Jar file
	 */
	public static boolean isJar(ISeamElement element) {
		return isJar(element.getSourcePath());
	}

	/**
	 * @param componentXmlFile
	 * @return IType of component for <ComponentName>.component.xml
	 */
	public static IType getClassTypeForComponentXml(IFile componentXmlFile, IProject rootProject) {
		String className = getClassNameForComponentXml(componentXmlFile, rootProject);
		if(className==null) {
			return null;
		}
		return findType(className, rootProject);
	}

	/**
	 * @param type name
	 * @return IType
	 */
	public static IType findType(String fullyQualifiedName, IProject rootProject) {
		try {
			IJavaProject jp = EclipseResourceUtil.getJavaProject(rootProject);
			return jp.findType(fullyQualifiedName);
		} catch (JavaModelException e) {
			SeamCorePlugin.getDefault().logError(e);
			return null;
		}
	}

	/**
	 * @param componentXmlFile
	 * @return name of component class for <ComponentName>.component.xml
	 */
	public static String getClassNameForComponentXml(IFile componentXmlFile, IProject rootProject) {
		String fileName  = componentXmlFile.getName();
		int firstDot = fileName.indexOf('.');
		if(firstDot==-1) {
			return null;
		}
		String className = fileName.substring(0, firstDot);		
		try {
			IJavaProject jp = EclipseResourceUtil.getJavaProject(rootProject);
			IPackageFragment packageFragment = jp.findPackageFragment(componentXmlFile.getFullPath().removeLastSegments(1));
			if(packageFragment==null) {
				return null;
			}
			return packageFragment.getElementName() + "." + className; //$NON-NLS-1$
		} catch (JavaModelException e) {
			SeamCorePlugin.getDefault().logError(e);
			return null;
		}
	}

	/**
	 * Find a setter or a field for a property.
	 * @param type
	 * @param propertyName
	 * @return IMethod (setter) or IFiled (field)
	 */
	public static IMember findProperty(IType type, String propertyName) {
		if(propertyName == null || propertyName.length()==0) {
			return null;
		}
		try {
			return findPropertyInHierarchy(type, propertyName);
		} catch (JavaModelException e) {
			SeamCorePlugin.getDefault().logError(e);
		}
		return null;
	}

	public static IMember findPropertyInHierarchy(IType type, String propertyName) throws JavaModelException {
		String firstLetter = propertyName.substring(0, 1).toUpperCase();
		String nameWithoutFirstLetter = propertyName.substring(1);
		String setterName = "set" + firstLetter + nameWithoutFirstLetter; //$NON-NLS-1$

		IMethod[] methods = type.getMethods();
		for (int i = 0; i < methods.length; i++) {
			if(methods[i].getElementName().equals(setterName) && methods[i].getParameterNames().length==1) {
				return methods[i];
			}
		}
		IField[] fields = type.getFields();
		for (int i = 0; i < fields.length; i++) {
			if(fields[i].getElementName().equals(propertyName)) {
				return fields[i];
			}
		}

		String superclassName = type.getSuperclassName();
		if(superclassName!=null) {
			String[][] packages = type.resolveType(superclassName);
			if(packages!=null) {
				for (int i = 0; i < packages.length; i++) {
					String packageName = packages[i][0];
					if(packageName!=null && packageName.length()>0) {
						packageName = packageName + ".";  //$NON-NLS-1$
					} else {
						packageName = ""; //$NON-NLS-1$
					}
					String qName = packageName + packages[i][1];
					IType superclass = type.getJavaProject().findType(qName);
					if(superclass!=null) {
						IMember property = findPropertyInHierarchy(superclass, propertyName);
						if(property!=null) {
							return property;
						}
					}
				}
			}
		}
		return null;
	}
}