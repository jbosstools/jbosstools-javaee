/*************************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.batch.internal.core.scanner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.internal.core.IBatchProject;
import org.jboss.tools.batch.internal.core.impl.BatchBuilder;
import org.jboss.tools.batch.internal.core.impl.BatchProject;
import org.jboss.tools.batch.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.util.FileUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BatchArchiveDetector {
	public static final int UNRESOLVED = -1;
	public static final int NOT_ARCHIVE = 0;
	public static final int ARCHIVE = 1;

	static BatchArchiveDetector instance = new BatchArchiveDetector();

	public static BatchArchiveDetector getInstance() {
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

	private BatchArchiveDetector() {}

	/**
	 * Returns NOT_ARCHIVE if path is tested not to be a bean archive,
	 *         UNRESOLVED if path is not tested yet,
	 *         NONE, ANNOTATED or ALL if path is tested to be a bean archive 
	 * @param path
	 * @return
	 */
	public synchronized int getBatchArchive(String path) {
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

	public synchronized void setBatchArchive(String path, int archive) {
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
							BatchCorePlugin.pluginLog().logError(e);
						}						
					} else if(c == 2 && t.startsWith("archive=")) {
						try {
							archive = Integer.parseInt(t.substring(8));
							if(getSize(path) == size) {
								paths.put(path, new Result(size, archive));
							}
							c = 0;
						} catch (NumberFormatException e) {
							BatchCorePlugin.pluginLog().logError(e);
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
		BatchCorePlugin plugin = BatchCorePlugin.getDefault();
		if( plugin != null) {
			//The plug-in instance can be null at shutdown, when the plug-in is stopped. 
			IPath path = plugin.getStateLocation();
			File file = new File(path.toFile(), "bean-archives.txt"); //$NON-NLS-1$
			return file;
		} else {
			return null;
		}
	}

	public int resolve(String jar, IBatchProject project) throws JavaModelException {
		IPackageFragmentRoot root = findPackageFragmentRoot(jar, project);
		if (root != null && root.exists()) {
			if(hasBatchArtifcts(root, project)) {
				setBatchArchive(jar, ARCHIVE);
			} else {
				setBatchArchive(jar, NOT_ARCHIVE);
			}
		}
		return UNRESOLVED;
	}

	boolean hasBatchArtifcts(IPackageFragmentRoot root, IBatchProject project) throws JavaModelException {
		IJavaElement[] es = root.getChildren();
		for (IJavaElement e : es) {
			if (e instanceof IPackageFragment) {
				IPackageFragment pf = (IPackageFragment) e;
				IClassFile[] cs = pf.getClassFiles();
				for (IClassFile c : cs) {
					if(isBatchArtifact(c.getType(), project)) {
						return true;
					}
				}
			}
		}
		//TODO check also jobs
		return false;
	}

	public static boolean isBatchArtifact(IType type, IBatchProject project) throws JavaModelException {
		TypeDefinition def = new TypeDefinition();
		def.setType(type, ((BatchProject)project).getDefinitions(), TypeDefinition.FLAG_NO_ANNOTATIONS);
		if(def.getArtifactType() != null) {
			return true;
		}
		return false;
	}

	public static IPackageFragmentRoot findPackageFragmentRoot(String jar, IBatchProject project) {
		IJavaProject jp = EclipseUtil.getJavaProject(project.getProject());
		return (jp == null) ? null : findPackageFragmentRoot(jar, jp);
	}

	public static IPackageFragmentRoot findPackageFragmentRoot(String jar, IProject project) {
		IJavaProject jp = EclipseUtil.getJavaProject(project);
		return (jp == null) ? null : findPackageFragmentRoot(jar, jp);
	}

	public static IPackageFragmentRoot findPackageFragmentRoot(String jar, IJavaProject jp) {
		IPackageFragmentRoot root = jp.getPackageFragmentRoot(jar);
		if(root != null && !root.exists()) {
			IFile f = BatchBuilder.getFile(jar);
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
