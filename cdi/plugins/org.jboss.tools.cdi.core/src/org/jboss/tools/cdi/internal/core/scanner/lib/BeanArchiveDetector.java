/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.internal.core.scanner.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIVersion;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.FileUtil;

/**
 * Keeps list of all CDI 1.1 implicit bean archives that were checked if they have managed beans.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BeanArchiveDetector {
	public static final int UNRESOLVED = -1;
	public static final int NOT_ARCHIVE = 0;
	public static final int NONE = 1;
	public static final int ANNOTATED = 2;
	public static final int ALL = 3;
	
	public static BeanArchiveDetector instance = new BeanArchiveDetector();

	public static BeanArchiveDetector getInstance() {
		return instance;
	}

	static class Result {
		int size;
		int archive = UNRESOLVED;

		Result(int size, int archive) {
			this.size = size;
			this.archive = archive;
		}
	}

	boolean isLoaded = false;
	boolean isDirty = false;
	Map<String, Result> paths = new HashMap<String, Result>();

	private BeanArchiveDetector() {
	}

	/**
	 * Returns NOT_ARCHIVE if path is tested not to be a bean archive,
	 *         UNRESOLVED if path is not tested yet,
	 *         NONE, ANNOTATED or ALL if path is tested to be a bean archive 
	 * @param path
	 * @return
	 */
	public synchronized int getBeanArchive(String path) {
		load();
		if(!paths.containsKey(path)) {
			return UNRESOLVED;
		}
		int size = getSize(path);
		if(size != paths.get(path).size) {
			paths.remove(path);
			isDirty = true;
			return UNRESOLVED;
		}
		return paths.get(path).archive;
	}

	public synchronized void setBeanArchive(String path, int archive) {
		load();
		int size = getSize(path);
		if(size > 0) {
			paths.put(path, new Result(size, archive));
			isDirty = true;
		}
	}

	private int getSize(String path) {
		return getSize(new File(path));
	}

	private int getSize(File f) {
		if(f.isFile()) {
			return (int)f.length();
		} else if(f.isDirectory()) {
			int result = 0;
			File[] fs = f.listFiles();
			if(fs != null) {
				for (File c: fs) {
					result += getSize(c);
				}
			}
			return result;
		}
		return 0;
	}

	private synchronized void load() {
		if(isLoaded) return;
		try {
			File f = getStorageFile();
			if(f.isFile()) {
				String content = FileUtil.readFile(f);
				StringTokenizer st = new StringTokenizer(content, "\n");
				String path = null;
				int size = 0;
				int archive = UNRESOLVED;
				int c = 0;
				while(st.hasMoreTokens()) {
					String t = st.nextToken();
					if(c == 0 && t.startsWith("path=")) {
						path = t.substring(5);
						c++;
					} else if(c == 1 && t.startsWith("size=")) {
						try {
							size = Integer.parseInt(t.substring(5));
							c++;
						} catch (NumberFormatException e) {
							CDICorePlugin.getDefault().logError(e);
						}						
					} else if(c == 2 && t.startsWith("archive=")) {
						try {
							archive = Integer.parseInt(t.substring(8));
							if(getSize(path) == size) {
								paths.put(path, new Result(size, archive));
							}
							c = 0;
						} catch (NumberFormatException e) {
							CDICorePlugin.getDefault().logError(e);
						}						
					}
				}
			}
		} finally {
			isLoaded = true;
		}
	}

	public synchronized void save() {
		if(isLoaded && isDirty) {
			isDirty = false;
			File f = getStorageFile();
			StringBuilder sb = new StringBuilder();
			for (String path: paths.keySet()) {
				Result r = paths.get(path);
				sb.append("path=").append(path).append("\n")
				  .append("size=").append(r.size).append("\n")
				  .append("archive=").append(r.archive).append("\n");
			}
			FileUtil.writeFile(f, sb.toString());
		}
	}

	private File getStorageFile() {
		CDICorePlugin plugin = CDICorePlugin.getDefault();
		if( plugin != null) {
			//The plug-in instance can be null at shutdown, when the plug-in is stopped. 
			IPath path = plugin.getStateLocation();
			File file = new File(path.toFile(), "bean-archives.txt"); //$NON-NLS-1$
			return file;
		} else {
			return null;
		}
	}

	public int resolve(String jar, CDICoreNature project) throws JavaModelException {
		IPackageFragmentRoot root = findPackageFragmentRoot(jar, project);
		if (root != null && root.exists()) {
			if(hasAnnotatedBeans(root, project)) {
				setBeanArchive(jar, ANNOTATED);
			} else {
				setBeanArchive(jar, NOT_ARCHIVE);
			}
		}
		return UNRESOLVED;
	}

	/**
	 * Returns true if at least one type in the archive is an annotated CDI 1.1 bean.
	 * 
	 * @param root
	 * @param project
	 * @return
	 * @throws JavaModelException
	 */
	public static boolean hasAnnotatedBeans(IPackageFragmentRoot root, CDICoreNature project) throws JavaModelException {
		IJavaElement[] es = root.getChildren();
		for (IJavaElement e : es) {
			if (e instanceof IPackageFragment) {
				IPackageFragment pf = (IPackageFragment) e;
				IClassFile[] cs = pf.getClassFiles();
				for (IClassFile c : cs) {
					if(isAnnotatedBean(c.getType(), project)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns true if type has a scope annotation and therefore is a CDI 1.1 annotated bean.
	 * 
	 * @param type
	 * @param project
	 * @return
	 * @throws JavaModelException
	 */
	public static boolean isAnnotatedBean(IType type, CDICoreNature project) throws JavaModelException {
		IAnnotation[] as = type.getAnnotations();
		for (IAnnotation a: as) {
			String typeName = EclipseJavaUtil.resolveType(type, a.getElementName());
			if(CDIConstants.STATELESS_ANNOTATION_TYPE_NAME.equals(typeName)
					|| CDIConstants.SINGLETON_ANNOTATION_TYPE_NAME.equals(typeName)) {
				//session bean annotation
				return true;
			}
			if(CDIVersion.CDI_1_2.equals(project.getVersion())) {
				if(CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME.equals(typeName)
						|| CDIConstants.INTERCEPTOR_ANNOTATION_TYPE_NAME.equals(typeName)) {
					return true;
				}
			}
			IType at = project.getType(typeName);
			if(at != null) {
				int k = project.getDefinitions().getAnnotationKind(at);
				if(k == AnnotationDefinition.SCOPE) {
					//scope annotation
					if(CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME.equals(typeName)) {
						return true;
					}
					AnnotationDefinition sa = project.getDefinitions().getAnnotation(at);
					return sa != null && sa.getAnnotation(CDIConstants.NORMAL_SCOPE_ANNOTATION_TYPE_NAME) != null;
				}
				if(CDIVersion.CDI_1_2.equals(project.getVersion()) && k == AnnotationDefinition.STEREOTYPE) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isQualifier(IType type) throws JavaModelException {
		if(!type.isAnnotation()) {
			return false;
		}
		IAnnotation[] as = type.getAnnotations();
		for (IAnnotation a: as) {
			String typeName = EclipseJavaUtil.resolveType(type, a.getElementName());
			if(CDIConstants.QUALIFIER_ANNOTATION_TYPE_NAME.equals(typeName)) {
				return true;
			}
		}
		return false;
	}

	public static IType findPackageInfo(IClassFile[] cs) {
		for (IClassFile cf: cs) {
			IType c = cf.getType();
			if("package-info".equals(c.getElementName())) {
				return c;
			}
		}
		return null;
	}

	public static boolean isVetoed(IType type) throws JavaModelException {
		IAnnotation[] as = type.getAnnotations();
		for (IAnnotation a: as) {
			String typeName = EclipseJavaUtil.resolveType(type, a.getElementName());
			if(CDIConstants.VETOED_ANNOTATION_TYPE_NAME.equals(typeName)) {
				return true;
			}
		}
		return false;
	}

	public static IType[] getAnnotatedTypes(IType[] ts,CDICoreNature project) throws CoreException {
		if(ts.length == 1 && (BeanArchiveDetector.isAnnotatedBean(ts[0], project)
				|| BeanArchiveDetector.isQualifier(ts[0]))) {
			return ts;
		}
		List<IType> result = new ArrayList<IType>();
		for (IType t: ts) {
			if(BeanArchiveDetector.isAnnotatedBean(t, project)) {
				result.add(t);
			}
		}
		return result.toArray(new IType[0]);
	}
	
	public static IPackageFragmentRoot findPackageFragmentRoot(String jar, CDICoreNature project) {
		IJavaProject jp = EclipseResourceUtil.getJavaProject(project.getProject());
		return (jp == null) ? null : findPackageFragmentRoot(jar, jp);
	}

	public static IPackageFragmentRoot findPackageFragmentRoot(String jar, IJavaProject jp) {
		IPackageFragmentRoot root = jp.getPackageFragmentRoot(jar);
		if(root != null && !root.exists()) {
			IFile f = EclipseResourceUtil.getFile(jar);
			if(f != null && f.exists()) {
				root = jp.getPackageFragmentRoot(f);
			} else {
				IContainer c = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(new Path(jar + "/META-INF").makeAbsolute());
				if(c != null && c.exists()) {
					root = jp.getPackageFragmentRoot(c.getParent());
				}
			}
		}
		return root;
	}

}
