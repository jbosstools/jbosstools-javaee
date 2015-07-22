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
package org.jboss.tools.batch.internal.core.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.core.BatchConstants;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.core.BatchProjectChangeEvent;
import org.jboss.tools.batch.core.IBatchArtifact;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.batch.core.IBatchProjectChangeListener;
import org.jboss.tools.batch.internal.core.impl.definition.BatchJobDefinition;
import org.jboss.tools.batch.internal.core.impl.definition.BatchXMLDefinition;
import org.jboss.tools.batch.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.batch.internal.core.scanner.lib.ClassPathMonitor;
import org.jboss.tools.common.java.ParametedTypeFactory;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.jst.web.kb.internal.AbstractKbProjectExtension;
import org.jboss.tools.jst.web.kb.internal.IKbProjectExtension;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BatchProject extends AbstractKbProjectExtension implements IBatchProject {
	ClassPathMonitor classPath = new ClassPathMonitor(this);
	DefinitionContext definitions = new DefinitionContext();

	ParametedTypeFactory typeFactory = new ParametedTypeFactory();

	private Set<IBatchArtifact> allArtifacts = new HashSet<IBatchArtifact>();
	private Map<IPath, Set<IBatchArtifact>> artifactsByPath = new HashMap<IPath, Set<IBatchArtifact>>();
	private Map<String, Set<IBatchArtifact>> artifactsByName = new HashMap<String, Set<IBatchArtifact>>();
	private Map<BatchArtifactType, Set<IBatchArtifact>> artifactsByType = new HashMap<BatchArtifactType, Set<IBatchArtifact>>();
	private Map<String, IBatchArtifact> artifactsByJavaType = new HashMap<String, IBatchArtifact>();

	private List<IBatchProjectChangeListener> listeners = new ArrayList<IBatchProjectChangeListener>();

	public BatchProject() {
		definitions.setProject(this);
	}

	@Override
	protected void resolveUsedProjectInJob(IKbProjectExtension project) {
		JOB.add(project);
	}

	private static BatchBuildJob JOB = new BatchBuildJob();
	private static boolean suspended = false;

	static class BatchBuildJob extends WorkspaceJob {
		List<IKbProjectExtension> list = new ArrayList<IKbProjectExtension>();
		boolean running = false;

		public BatchBuildJob() {
			super("Build Batch");
		}
		void add(IKbProjectExtension project) {
			if (!isSuspended()) {
				if(list.contains(project)) {
					return;
				}
				list.add(project);
				if (getState() == Job.NONE || !isRunning()) {
					schedule(0);
				}
			}		
		}
		@Override
		public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
			synchronized(this) {
				running = true;
			}
			while(true) {
				IKbProjectExtension r = null;
				synchronized (this) {
					if(list.size() == 0) {
						running = false;
						break;
					}
					r = list.remove(0);
				}
				if(monitor.isCanceled()) {
					break;
				}
				try {
					r.resolve();
					r.update(true);
				} catch (Exception e) {
					if(e instanceof RuntimeException) {
						throw (RuntimeException)e;
					}
					BatchCorePlugin.pluginLog().logError("Error in job ", e);
				}
				
			}
			
			return Status.OK_STATUS;
		}

		public static void shutdown() {
			setSuspended(true);
			synchronized (JOB) {
				JOB.list.clear();
			}
			if(JOB.isRunning()) {
				JOB.cancel();
			}
		}

		private boolean isRunning() {
			synchronized(this) {
				return running;
			}
		}

		public static boolean isSuspended() {
			return suspended;
		}

		public static void setSuspended(boolean suspended) {
			BatchProject.suspended = suspended;
		}
	}

	public boolean exists() {
		return getType(BatchConstants.ABSTRACT_BATCHLET_TYPE) != null;
	}

	public List<TypeDefinition> getAllTypeDefinitions() {
		Set<IKbProjectExtension> ps = getUsedProjects(true);
		if(ps == null || ps.isEmpty()) {
			return getDefinitions().getTypeDefinitions();
		}
		List<TypeDefinition> ds = getDefinitions().getTypeDefinitions();
		List<TypeDefinition> result = new ArrayList<TypeDefinition>();
		result.addAll(ds);
		Set<IType> types = new HashSet<IType>();
		for (TypeDefinition d: ds) {
			IType t = d.getType();
			if(t != null) types.add(t);
		}
		for (IKbProjectExtension p: ps) {
			List<TypeDefinition> ds2 = ((BatchProject)p).getDefinitions().getTypeDefinitions();
			for (TypeDefinition d: ds2) {
				IType t = d.getType();
				if(t != null && !types.contains(t)) {
					types.add(t);
					result.add(d);
				}
			}
		}
		return result;
	}

	public Set<BatchJobDefinition> getDeclaredBatchJobDefinitions() {
		return getDefinitions().getBatchJobDefinitions();
	}

	public Set<BatchJobDefinition> getAllBatchJobDefinitions() {
		if(!dependsOnOtherProjects()) {
			return getDeclaredBatchJobDefinitions();
		}
		Set<BatchJobDefinition> ds = getDefinitions().getBatchJobDefinitions();
		Set<BatchJobDefinition> result = new HashSet<BatchJobDefinition>();
		result.addAll(ds);
		Set<IPath> paths = new HashSet<IPath>();
		for (BatchJobDefinition d: ds) {
			IPath t = d.getPath();
			if(t != null) paths.add(t);
		}
		for (BatchProject p: getBatchProjects(true)) {
			Set<BatchJobDefinition> ds2 = p.getDeclaredBatchJobDefinitions();
			for (BatchJobDefinition d: ds2) {
				IPath t = d.getPath();
				if(t != null && !paths.contains(t)) {
					paths.add(t);
					result.add(d);
				}
			}
		}
		return result;
	}

	public Set<BatchXMLDefinition> getDeclaredBatchXMLDefinitions() {
		return getDefinitions().getBatchXMLDefinitions();
	}

	/**
	 * Returns all the project that are included into classpath of this project.
	 * Modification to the returned set does not affect stored references.
	 * 
	 * @param hierarchy If false then return the projects explicitly included into the project classpath.
	 * If true then all the project from the entire hierarchy will be returned.
	 * @return
	 */
	public Set<BatchProject> getBatchProjects(boolean hierarchy) {
		if(hierarchy && dependsOnOtherProjects()) {
			Set<BatchProject> result = new HashSet<BatchProject>();
			getAllBatchProjects(result);
			return result;
		} else {
			return getBatchProjects();
		}
	}

	synchronized void getAllBatchProjects(Set<BatchProject> result) {
		for (IKbProjectExtension n: dependsOn) {
			if(result.contains(n)) continue;
			result.add((BatchProject)n);
			((BatchProject)n).getAllBatchProjects(result);
		}
	}

	public Set<BatchProject> getBatchProjects() {
		Set<BatchProject> result = new HashSet<BatchProject>();
		synchronized(this) {
			for (IKbProjectExtension n: dependsOn) {
				result.add((BatchProject)n);
			}
		}
		return result;
	}

	public ParametedTypeFactory getTypeFactory() {
		return typeFactory;
	}

	public ClassPathMonitor getClassPath() {
		return classPath;
	}

	public void cleanTypeFactory() {
		typeFactory.clean();
		BatchProject[] ps = getAllDependentProjects();
		for (BatchProject n: ps) {
			n.typeFactory.clean();
		}
	}
	
	@Override
	public Collection<IBatchArtifact> getAllArtifacts() {
		Set<IBatchArtifact> result = new HashSet<IBatchArtifact>();
		synchronized(this) {
			result.addAll(allArtifacts);
		}
		return result;
	}

	@Override
	public synchronized Collection<IBatchArtifact> getArtifacts(IResource resource) {
		Set<IBatchArtifact> result = artifactsByPath.get(resource.getFullPath());
		return result != null ? new HashSet<IBatchArtifact>(result) : EMPTY;
	}

	@Override
	public synchronized Set<IFile> getDeclaredBatchJobs() {
		Set<IFile> result = new HashSet<IFile>();
		for (BatchJobDefinition def: getDefinitions().getBatchJobDefinitions()) {
			result.add(def.getFile());
		}
		return result;
	}

	@Override
	public synchronized Collection<IBatchArtifact> getArtifacts(String name) {
		Set<IBatchArtifact> result = artifactsByName.get(name);
		return result != null ? new HashSet<IBatchArtifact>(result) : EMPTY;
	}

	@SuppressWarnings("unchecked")
	static Collection<IBatchArtifact> EMPTY = Collections.EMPTY_SET;

	@Override
	public synchronized Collection<IBatchArtifact> getArtifacts(BatchArtifactType type) {
		Collection<IBatchArtifact> result = artifactsByType.get(type);
		return result != null ? new HashSet<IBatchArtifact>(result) : EMPTY;
	}

	@Override
	public IBatchArtifact getArtifact(IType type) {
		return artifactsByJavaType.get(type.getFullyQualifiedName());
	}

	@Override
	public Collection<ITextSourceReference> getReferences(IType type) {
		Collection<ITextSourceReference> result = new HashSet<ITextSourceReference>();
		for (IFile file: getDeclaredBatchJobs()) {
			result.addAll(BatchUtil.getAttributeReferences(file, BatchConstants.ATTR_CLASS, type.getFullyQualifiedName()));
		}
		return result;
	}

	public void store() throws IOException {
		isBuilt = true;
//		File file = getStorageFile();
	}

	public boolean hasNoStorage() {
		if(isBuilt) return false;
		File f = null;// getStorageFile();
		return f == null || !f.exists();
	}

	@Override
	protected void build() {
		try {
			new BatchBuilder(this);
		} catch (CoreException e) {
			BatchCorePlugin.pluginLog().logError(e);
		}
	}

	public void clean() {
//		File file = getStorageFile();
//		if(file != null && file.isFile()) {
//			file.delete();
//		}
		isBuilt = false;
		classPath.clean();
		definitions.clean();

		synchronized (this) {
			artifactsByPath.clear();
			artifactsByName.clear();
			artifactsByType.clear();
			artifactsByJavaType.clear();
			allArtifacts.clear();
		}

		postponeFiring();

		fireChanges();
	}

	@Override
	public void fireChanges() {
		IBatchProjectChangeListener[] ls = null;
		synchronized(this) {
			ls = listeners.toArray(new IBatchProjectChangeListener[0]);
		}
		if(ls != null) {
			BatchProjectChangeEvent event = new BatchProjectChangeEvent();
			for (int i = 0; i < ls.length; i++) {
				ls[i].projectChanged(event);
			}
		}
	}

	/**
	 * Updates model by loaded definitions.
	 */
	@Override
	public void update(boolean updateDependent) {
		//Set<BatchJobDefinition> batchJobs = getAllBatchJobDefinitions();
		//
		List<TypeDefinition> typeDefinitions = getAllTypeDefinitions();
		List<IBatchArtifact> artifacts = new ArrayList<IBatchArtifact>();
		for (TypeDefinition typeDefinition: typeDefinitions) {
			BatchArtifact a = new BatchArtifact();
			a.setProject(this);
			a.setDefinition(typeDefinition);
			artifacts.add(a);
		}
		synchronized (this) {
			artifactsByPath.clear();
			artifactsByName.clear();
			artifactsByType.clear();
			artifactsByJavaType.clear();
			allArtifacts.clear();
		}
		for (IBatchArtifact a: artifacts) {
			addArtifact(a);
		}

		if(updateDependent) {
			Collection<IKbProjectExtension> dependent = new ArrayList<IKbProjectExtension>(usedBy);
			for (IKbProjectExtension p: dependent) {
				p.update(false);
			}
		}
		
		fireChanges();
	}

	public void addArtifact(IBatchArtifact artifact) {
		addToMap(artifactsByName, artifact.getName(), artifact);
		addToMap(artifactsByType, artifact.getArtifactType(), artifact);
		addToMap(artifactsByPath, artifact.getSourcePath(), artifact);
		artifactsByJavaType.put(artifact.getType().getFullyQualifiedName(), artifact);
		synchronized (this) {
			allArtifacts.add(artifact);
		}
	}

	private synchronized <P> void addToMap(Map<P, Set<IBatchArtifact>> map, P key, IBatchArtifact artifact) {
		if(key == null) {
			return;
		}
		Set<IBatchArtifact> bs = map.get(key);
		if(bs == null) {
			bs = new HashSet<IBatchArtifact>();
			map.put(key, bs);
		}
		bs.add(artifact);
	}

	public BatchProject[] getAllDependentProjects(boolean resolve) {
		Map<BatchProject, Integer> set = new HashMap<BatchProject, Integer>();
		getAllDependentProjects(set, 0);
		if(resolve) {
			for (BatchProject n: set.keySet()) {
				n.resolve();
			}
		}
		BatchProject[] result = set.keySet().toArray(new BatchProject[set.size()]);
		Arrays.sort(result, new D(set));
		return result;
	}

	public BatchProject[] getAllDependentProjects() {
		return getAllDependentProjects(false);
	}

	private void getAllDependentProjects(Map<BatchProject, Integer> result, int level) {
		if(level > 10) return;
		BatchProject[] array = null;
		synchronized(this) {
			array = usedBy.toArray(new BatchProject[0]);
		}
		for (BatchProject n: array) {
			if(!result.containsKey(n) || result.get(n).intValue() < level) {
				result.put(n, level);
				n.getAllDependentProjects(result, level + 1);
			}
		}
	}
	private static class D implements Comparator<BatchProject> {
		Map<BatchProject, Integer> set;
		D(Map<BatchProject, Integer> set) {
			this.set = set;
		}
		@Override
		public int compare(BatchProject o1, BatchProject o2) {
			return set.get(o1).intValue() - set.get(o2).intValue();
		}
		
	}

	public DefinitionContext getDefinitions() {
		return definitions;
	}

	public void pathRemoved(IPath path) {
		definitions.getWorkingCopy().clean(path);
	}

	@Override
	protected IKbProjectExtension loadWithFactory(IProject project, boolean resolve) {
		return BatchProjectFactory.getBatchProject(project, resolve);
	}

	public synchronized void addBatchProjectListener(IBatchProjectChangeListener listener) {
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public synchronized void removeBatchProjectListener(IBatchProjectChangeListener listener) {
		listeners.remove(listener);
	}
}
