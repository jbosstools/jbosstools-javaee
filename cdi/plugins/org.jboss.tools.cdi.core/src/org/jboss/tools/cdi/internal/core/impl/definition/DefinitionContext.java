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
package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class DefinitionContext {
	protected CDICoreNature project;
	protected IJavaProject javaProject;

	private Set<String> types = new HashSet<String>();
	private Map<IPath, Set<String>> resources = new HashMap<IPath, Set<String>>();
	private Map<String, TypeDefinition> typeDefinitions = new HashMap<String, TypeDefinition>();
	private Map<String, AnnotationDefinition> annotations = new HashMap<String, AnnotationDefinition>();

	private Map<IPath, BeansXMLDefinition> beanXMLs = new HashMap<IPath, BeansXMLDefinition>();

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
			copy.annotations.putAll(annotations);
			for (IPath p: resources.keySet()) {
				Set<String> set = resources.get(p);
				if(set != null) {
					Set<String> s1 = new HashSet<String>();
					s1.addAll(set);
					copy.resources.put(p, s1);
				}
			}
			copy.beanXMLs.putAll(beanXMLs);
		}
		
		return copy;
	}

	public void setProject(CDICoreNature project) {
		this.project = project;
		javaProject = EclipseResourceUtil.getJavaProject(project.getProject());
	}

	public CDICoreNature getProject() {
		return project;
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public void addType(IPath file, String typeName, AbstractTypeDefinition def) {
		if(file != null) {
			Set<String> ts = resources.get(file);
			if(ts == null) {
				ts = new HashSet<String>();
				resources.put(file, ts);
			}
			ts.add(typeName);
			types.add(typeName);
		}
		if(def != null) {
			if(def instanceof AnnotationDefinition) {
				synchronized (annotations) {
					annotations.put(def.getQualifiedName(), (AnnotationDefinition)def);
				}
			} else {
				synchronized (typeDefinitions) {
					typeDefinitions.put(def.getQualifiedName(), (TypeDefinition)def);
				}
			}
		}
	}

	public void addBeanXML(IPath path, BeansXMLDefinition def) {
		synchronized (beanXMLs) {
			beanXMLs.put(path, def);
		}
	}

	public void clean() {
		resources.clear();
		types.clear();
		synchronized (typeDefinitions) {
			typeDefinitions.clear();
		}
		synchronized (annotations) {
			annotations.clear();
		}
		synchronized (beanXMLs) {
			beanXMLs.clear();
		}
	}

	public void clean(IPath path) {
		Set<String> ts = resources.remove(path);
		if(ts != null) for (String t: ts) {
			types.remove(t);
			synchronized (typeDefinitions) {
				typeDefinitions.remove(t);
			}
			synchronized (annotations) {
				annotations.remove(t);
			}
		}
		synchronized (beanXMLs) {
			beanXMLs.remove(path);
		}
	}

	private Set<String> underConstruction = new HashSet<String>();

	public int getAnnotationKind(IType annotationType) {
		if(annotationType == null) return -1;
		AnnotationDefinition d = getAnnotation(annotationType);
		if(d != null) {
			return d.getKind();
		}
		String name = annotationType.getFullyQualifiedName();
		//? use cache for basic?
		if(types.contains(name)) {
			return AnnotationDefinition.NON_RELEVANT;
		}
		if(AnnotationHelper.SCOPE_ANNOTATION_TYPES.contains(name)) {
			createAnnotation(annotationType, name);
			return AnnotationDefinition.SCOPE;
		}
		if(AnnotationHelper.STEREOTYPE_ANNOTATION_TYPES.contains(name)) {
			createAnnotation(annotationType, name);
			return AnnotationDefinition.STEREOTYPE;
		}
		if(AnnotationHelper.QUALIFIER_ANNOTATION_TYPES.contains(name)) {
			createAnnotation(annotationType, name);
			return AnnotationDefinition.QUALIFIER;
		}
		if(AnnotationHelper.BASIC_ANNOTATION_TYPES.contains(name)) {
			return AnnotationDefinition.BASIC;
		}
		if(AnnotationHelper.CDI_ANNOTATION_TYPES.contains(name)) {
			return AnnotationDefinition.CDI;
		}
		if(underConstruction.contains(name)) {
			return AnnotationDefinition.BASIC;
		}
		return createAnnotation(annotationType, name);
	}

	private int createAnnotation(IType annotationType, String name) {
		underConstruction.add(name);
		AnnotationDefinition d = new AnnotationDefinition();
		d.setType(annotationType, this);
		int kind = d.getKind();
		if(kind <= AnnotationDefinition.CDI) {
			d = null;
		}
		addType(annotationType.getPath(), name, d);
		underConstruction.remove(name);
		return kind;
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

		types = workingCopy.types;
		resources = workingCopy.resources;
		typeDefinitions = workingCopy.typeDefinitions;
		annotations = workingCopy.annotations;
	
		project.getDelegate().update();

		workingCopy = null;
	}

	public AnnotationDefinition getAnnotation(IType type) {
		String name = type.getFullyQualifiedName();
		return annotations.get(name);
	}

	public List<AnnotationDefinition> getAllAnnotations() {
		List<AnnotationDefinition> result = new ArrayList<AnnotationDefinition>();
		synchronized (annotations) {
			result.addAll(annotations.values());
		}
		return result;
	}

	public List<TypeDefinition> getTypeDefinitions() {
		List<TypeDefinition> result = new ArrayList<TypeDefinition>();
		synchronized (typeDefinitions) {
			result.addAll(typeDefinitions.values());
		}
		return result;
	}

	public Set<BeansXMLDefinition> getBeansXMLDefinitions() {
		Set<BeansXMLDefinition> result = new HashSet<BeansXMLDefinition>();
		synchronized (beanXMLs) {
			result.addAll(beanXMLs.values());
		}
		return result;
	}
	
}

