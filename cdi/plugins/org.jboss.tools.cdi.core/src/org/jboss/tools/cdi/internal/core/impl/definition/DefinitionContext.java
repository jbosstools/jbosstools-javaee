package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.HashMap;
import java.util.HashSet;
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

	Set<IType> types = new HashSet<IType>();
	Map<IPath, Set<IType>> resources = new HashMap<IPath, Set<IType>>();
	Map<IType, AbstractTypeDefinition> typeDefinitions = new HashMap<IType, AbstractTypeDefinition>();
	Map<IType, AnnotationDefinition> annotations = new HashMap<IType, AnnotationDefinition>();

	public DefinitionContext() {}

	public void setProject(CDICoreNature project) {
		this.project = project;
		javaProject = EclipseResourceUtil.getJavaProject(project.getProject());
	}

	public CDICoreNature getProject() {
		return project;
	}

	public void addType(IPath file, IType type, AbstractTypeDefinition def) {
		if(file != null) {
			Set<IType> ts = resources.get(file);
			if(ts == null) {
				ts = new HashSet<IType>();
				resources.put(file, ts);
			}
			ts.add(type);
			types.add(type);
		}
		if(def != null) {
			typeDefinitions.put(type, def);
			if(def instanceof AnnotationDefinition) {
				annotations.put(type, (AnnotationDefinition)def);
			}
		}
	}

	public void clean(IPath path) {
		Set<IType> ts = resources.remove(path);
		if(ts == null) return;
		for (IType t: ts) {
			types.remove(t);
			typeDefinitions.remove(t);
			annotations.remove(t);
		}
	}

	public int getAnnotationKind(IType annotationType) {
		if(annotationType == null) return -1;
		AnnotationDefinition d = annotations.get(annotationType);
		if(d != null) {
			return d.getKind();
		}
		//? use cache for basic?
		if(types.contains(annotationType)) {
			return AnnotationDefinition.NON_RELEVANT;
		}
		String name = annotationType.getFullyQualifiedName();
		if(AnnotationHelper.BASIC_ANNOTATION_TYPES.contains(name)) {
			return AnnotationDefinition.BASIC;
		}
		if(AnnotationHelper.CDI_ANNOTATION_TYPES.contains(name)) {
			return AnnotationDefinition.CDI;
		}

		d = new AnnotationDefinition();
		d.setType(annotationType, this);
		int kind = d.getKind();
		if(kind <= AnnotationDefinition.CDI) {
			d = null;
		}
		addType(annotationType.getPath(), annotationType, d);
		return kind;
	}
}

