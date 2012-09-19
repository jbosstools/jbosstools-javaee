/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.jsf2.bean.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.jsf2.bean.model.JSF2Constants;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class DefinitionContext {
	protected JSF2Project project;
	protected IJavaProject javaProject;

	private Set<String> types = new HashSet<String>();
	private Map<IPath, Set<IPath>> childPaths = new HashMap<IPath, Set<IPath>>();
	private Map<IPath, Set<String>> resources = new HashMap<IPath, Set<String>>();
	private Map<String, TypeDefinition> typeDefinitions = new HashMap<String, TypeDefinition>();

	FacesConfigDefinition facesConfig = null;

	private DefinitionContext workingCopy;
	private DefinitionContext original;

	public DefinitionContext() {}

	private DefinitionContext copy(boolean clean) {
		DefinitionContext copy = new DefinitionContext();
		copy.project = project;
		copy.javaProject = javaProject;
		if(!clean) {
			copy.types.addAll(types);
			copy.typeDefinitions.putAll(typeDefinitions);
			copy.facesConfig = facesConfig;

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

	public void setProject(JSF2Project project) {
		this.project = project;
		javaProject = EclipseResourceUtil.getJavaProject(project.getProject());
	}

	public JSF2Project getProject() {
		return project;
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public void addType(IPath file, String typeName, TypeDefinition def) {
		addType(file, typeName);
		if(def != null) {
			synchronized (typeDefinitions) {
				typeDefinitions.put(def.getQualifiedName(), (TypeDefinition)def);
			}
		}
	}

	public void setFacesConfig(FacesConfigDefinition def) {
		facesConfig = def;
		if(def != null) {
			addToParents(def.getPath());
		}
	}

	public void addType(IPath file, String typeName) {
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
			Set<IPath> cs = childPaths.get(q);
			if(cs == null) {
				childPaths.put(q, cs = new HashSet<IPath>());
			}
			cs.add(file);
		}
	}

	public void clean() {
		childPaths.clear();
		resources.clear();
		types.clear();
		facesConfig = null;
		synchronized (typeDefinitions) {
			typeDefinitions.clear();
		}

	}

	public void clean(IPath path) {
		Set<String> ts = resources.remove(path);
		if(ts != null) for (String t: ts) {
			clean(t);
		}
		if(facesConfig != null && path.equals(facesConfig.getPath())) {
			facesConfig = null;
		}

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

	public void clean(String typeName) {
		types.remove(typeName);
		synchronized (typeDefinitions) {
			typeDefinitions.remove(typeName);
		}
	}

	void removeFromParents(IPath file) {
		if(file == null) return;
		IPath q = file;
		while(q.segmentCount() >= 2) {
			q = q.removeLastSegments(1);
			Set<IPath> cs = childPaths.get(q);
			if(cs != null) {
				cs.remove(file);
				if(cs.isEmpty()) {
					childPaths.remove(q);
				}
			}
		}
	}

	public int getAnnotationKind(IType annotationType) {
		if(annotationType == null) return -1;
		if(!annotationType.exists()) return -1;
		String name = annotationType.getFullyQualifiedName();
		//? use cache for basic?
		if(types.contains(name)) {
			return 0;
		}
//		if(AnnotationHelper.SCOPE_ANNOTATION_TYPES.contains(name)) {
//			return AnnotationDefinition.SCOPE;
//		}
		if(JSF2Constants.MANAGED_BEAN_ANNOTATION_TYPE_NAME.equals(name)) {
			return 1;
		}
		return 0;
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
		facesConfig = workingCopy.facesConfig;

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
		synchronized (typeDefinitions) {
			result.addAll(typeDefinitions.values());
		}
		return result;
	}

	public TypeDefinition getTypeDefinition(String fullyQualifiedName) {
		return typeDefinitions.get(fullyQualifiedName);
	}

	public FacesConfigDefinition getFacesConfig() {
		return facesConfig;
	}
}

