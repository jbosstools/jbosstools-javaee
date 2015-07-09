/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.internal.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.jboss.tools.batch.core.IRootDefinitionContext;
import org.jboss.tools.batch.internal.core.impl.definition.BatchJobDefinition;
import org.jboss.tools.batch.internal.core.impl.definition.BatchXMLDefinition;
import org.jboss.tools.batch.internal.core.impl.definition.Dependencies;
import org.jboss.tools.batch.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.common.EclipseUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class DefinitionContext implements IRootDefinitionContext {
	protected BatchProject project;
	protected IJavaProject javaProject;

	private Set<String> types = new HashSet<String>();
	private Map<IPath, Set<IPath>> childPaths = new HashMap<IPath, Set<IPath>>();
	private Map<IPath, Set<String>> resources = new HashMap<IPath, Set<String>>();
	private Map<String, TypeDefinition> typeDefinitions = new HashMap<String, TypeDefinition>();

	private Map<IPath, BatchJobDefinition> batchJobs = new HashMap<IPath, BatchJobDefinition>();
	private Map<IPath, BatchXMLDefinition> batchXMLs = new HashMap<IPath, BatchXMLDefinition>();

	private Dependencies dependencies = new Dependencies();

	private DefinitionContext workingCopy;
	private DefinitionContext original;

	public DefinitionContext() {}

	private synchronized DefinitionContext copy(boolean clean) {
		DefinitionContext copy = new DefinitionContext();
		copy.project = project;
		copy.javaProject = javaProject;
		if(!clean) {
			copy.types.addAll(types);
			copy.typeDefinitions.putAll(typeDefinitions);
			copy.batchJobs.putAll(batchJobs);
			copy.batchXMLs.putAll(batchXMLs);

			for (IPath p: resources.keySet()) {
				Set<String> set = resources.get(p);
				if(set != null) {
					Set<String> s1 = new HashSet<String>();
					s1.addAll(set);
					copy.resources.put(p, s1);
				}
			}
			for (IPath p: childPaths.keySet()) {
				Set<IPath> set = childPaths.get(p);
				if(set != null) {
					Set<IPath> s1 = new HashSet<IPath>();
					s1.addAll(set);
					copy.childPaths.put(p, s1);
				}
			}
		}
		
		return copy;
	}

	public void setProject(BatchProject project) {
		this.project = project;
		javaProject = EclipseUtil.getJavaProject(project.getProject());
	}

	public BatchProject getProject() {
		return project;
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	void addType(IPath file, String typeName, TypeDefinition def) {
		addType(file, typeName);
		if(def != null) {
			synchronized (this) {
				typeDefinitions.put(def.getQualifiedName(), (TypeDefinition)def);
			}
		}
	}

	public void addBatchConfig(BatchJobDefinition def) {
		synchronized(this) {
			batchJobs.put(def.getPath(), def);
		}
		addToParents(def.getPath());
	}

	public void addBatchXML(BatchXMLDefinition def) {
		synchronized(this) {
			batchXMLs.put(def.getPath(), def);
		}
		addToParents(def.getPath());
	}

	public synchronized void addType(IPath file, String typeName) {
		if(file != null) {
			Set<String> ts = resources.get(file);
			if(ts == null) {
				ts = new HashSet<String>();
				resources.put(file, ts);
			}
			ts.add(typeName);
			types.add(typeName);
			addToParents(file);
		}
	}

	public void addToParents(IPath file) {
		if(file == null) return;
		if(file.segmentCount() < 2) return;
		IPath q = file;
		while(q.segmentCount() >= 2) {
			q = q.removeLastSegments(1);
			synchronized(this) {
				Set<IPath> cs = childPaths.get(q);
				if(cs == null) {
					childPaths.put(q, cs = new HashSet<IPath>());
				}
				cs.add(file);
			}
		}
	}

	public synchronized void clean() {
		childPaths.clear();
		resources.clear();
		types.clear();
		batchJobs.clear();
		batchXMLs.clear();
		typeDefinitions.clear();
	}

	public synchronized void clean(IPath path) {
		Set<String> ts = resources.remove(path);
		if(ts != null) for (String t: ts) {
			clean(t);
		}
		batchJobs.remove(path);
		batchXMLs.remove(path);

		Set<IPath> cs = childPaths.get(path);
		if(cs != null) {
			IPath[] ps = cs.toArray(new IPath[0]);
			for (IPath p: ps) {
				clean(p);
			}
		} else {
			removeFromParents(path);
		}
	
	}

	public synchronized void clean(String typeName) {
		types.remove(typeName);
		typeDefinitions.remove(typeName);
	}

	void removeFromParents(IPath file) {
		if(file == null) return;
		IPath q = file;
		while(q.segmentCount() >= 2) {
			q = q.removeLastSegments(1);
			synchronized (this) {
				Set<IPath> cs = childPaths.get(q);
				if(cs != null) {
					cs.remove(file);
					if(cs.isEmpty()) {
						childPaths.remove(q);
					}
				}
			}
		}
	}

	public void newWorkingCopy(boolean forFullBuild) {
		if(original != null) return;
		workingCopy = copy(forFullBuild);
		workingCopy.original = this;
	}

	public DefinitionContext getWorkingCopy() {
		if(original != null) {
			return this;
		}
		if(workingCopy != null) {
			return workingCopy;
		}
		workingCopy = copy(false);
		workingCopy.original = this;
		return workingCopy;
	}

	public void applyWorkingCopy() {
		if(original != null) {
			original.applyWorkingCopy();
			return;
		}
		if(workingCopy == null) {
			return;
		}
		
		Set<TypeDefinition> newTypeDefinitions = new HashSet<TypeDefinition>();
		for (String typeName: workingCopy.typeDefinitions.keySet()) {
			TypeDefinition nd = workingCopy.typeDefinitions.get(typeName);
			TypeDefinition od = typeDefinitions.get(typeName);
			if(od != nd) {
				newTypeDefinitions.add(nd);
			}
		}
		
		types = workingCopy.types;
		resources = workingCopy.resources;
		childPaths = workingCopy.childPaths;
		typeDefinitions = workingCopy.typeDefinitions;
		batchJobs = workingCopy.batchJobs;
		batchXMLs = workingCopy.batchXMLs;

		project.update(true);

		workingCopy = null;
	}

	public void dropWorkingCopy() {
		if(original != null) {
			original.dropWorkingCopy();
		} else {
			workingCopy = null;
		}
	}

	public List<TypeDefinition> getTypeDefinitions() {
		List<TypeDefinition> result = new ArrayList<TypeDefinition>();
		synchronized (this) {
			result.addAll(typeDefinitions.values());
		}
		return result;
	}

	public Set<BatchJobDefinition> getBatchJobDefinitions() {
		Set<BatchJobDefinition> result = new HashSet<BatchJobDefinition>();
		synchronized (this) {
			result.addAll(batchJobs.values());
		}
		return result;
	}

	public Set<BatchXMLDefinition> getBatchXMLDefinitions() {
		Set<BatchXMLDefinition> result = new HashSet<BatchXMLDefinition>();
		synchronized (this) {
			result.addAll(batchXMLs.values());
		}
		return result;
	}

	public synchronized TypeDefinition getTypeDefinition(String fullyQualifiedName) {
		return typeDefinitions.get(fullyQualifiedName);
	}

	@Override
	public void addDependency(IPath source, IPath target) {
		dependencies.addDependency(source, target);
	}

	public Dependencies getDependencies() {
		return dependencies;
	}

}

