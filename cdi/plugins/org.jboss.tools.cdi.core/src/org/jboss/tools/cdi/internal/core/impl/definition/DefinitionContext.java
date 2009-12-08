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

public class DefinitionContext {
	protected CDICoreNature project;
	protected IJavaProject javaProject;

	Set<String> types = new HashSet<String>();
	Map<IPath, Set<String>> resources = new HashMap<IPath, Set<String>>();
	Map<String, AbstractTypeDefinition> typeDefinitions = new HashMap<String, AbstractTypeDefinition>();
	Map<String, AnnotationDefinition> annotations = new HashMap<String, AnnotationDefinition>();

	DefinitionContext workingCopy;
	DefinitionContext original;

	public DefinitionContext() {}

	private DefinitionContext copy() {
		DefinitionContext copy = new DefinitionContext();
		copy.project = project;
		copy.javaProject = javaProject;
		copy.types.addAll(types);
		copy.typeDefinitions.putAll(typeDefinitions);
		
		return copy;
	}

	public void setProject(CDICoreNature project) {
		this.project = project;
		javaProject = EclipseResourceUtil.getJavaProject(project.getProject());
	}

	public CDICoreNature getProject() {
		return project;
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
					typeDefinitions.put(def.getQualifiedName(), def);
				}
			}
		}
	}

	public void clean(IPath path) {
		Set<String> ts = resources.remove(path);
		if(ts == null) return;
		for (String t: ts) {
			types.remove(t);
			synchronized (typeDefinitions) {
				typeDefinitions.remove(t);
			}
			synchronized (annotations) {
				annotations.remove(t);
			}
		}
	}

	public int getAnnotationKind(IType annotationType) {
		if(annotationType == null) return -1;
		AnnotationDefinition d = annotations.get(annotationType);
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
		if(AnnotationHelper.BASIC_ANNOTATION_TYPES.contains(name)) {
			return AnnotationDefinition.BASIC;
		}
		if(AnnotationHelper.CDI_ANNOTATION_TYPES.contains(name)) {
			return AnnotationDefinition.CDI;
		}

		return createAnnotation(annotationType, name);
	}

	private int createAnnotation(IType annotationType, String name) {
		AnnotationDefinition d = new AnnotationDefinition();
		d.setType(annotationType, this);
		int kind = d.getKind();
		if(kind <= AnnotationDefinition.CDI) {
			d = null;
		}
		addType(annotationType.getPath(), name, d);
		return kind;
	}

	public DefinitionContext getWorkingCopy() {
		if(original != null) {
			return this;
		}
		if(workingCopy != null) {
			return workingCopy;
		}
		workingCopy = copy();
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
		//TODO
		
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

	
}

