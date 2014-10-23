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
package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedTypeFeature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.UniquePaths;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class DefinitionContext implements IRootDefinitionContext {
	protected CDICoreNature project;
	protected IJavaProject javaProject;

	private Set<String> types = new HashSet<String>();
	private Map<IPath, Set<IPath>> childPaths = new HashMap<IPath, Set<IPath>>();
	private Map<IPath, Set<String>> resources = new HashMap<IPath, Set<String>>();
	private Map<String, TypeDefinition> typeDefinitions = new TreeMap<String, TypeDefinition>();
	private Map<String, AnnotationDefinition> annotations = new HashMap<String, AnnotationDefinition>();
	private Map<String, AnnotationDefinition> usedAnnotations = new HashMap<String, AnnotationDefinition>();

	private Set<String> vetoedTypes = new HashSet<String>();

	private Set<String> packages = new HashSet<String>();
	private Map<String, PackageDefinition> packageDefinitions = new HashMap<String, PackageDefinition>();

	private Map<IPath, BeansXMLDefinition> beanXMLs = new HashMap<IPath, BeansXMLDefinition>();

	Set<IDefinitionContextExtension> extensions = new HashSet<IDefinitionContextExtension>();

	private Dependencies dependencies = new Dependencies();

	private DefinitionContext workingCopy;
	private DefinitionContext original;

	public DefinitionContext() {}

	public void setExtensions(Set<IDefinitionContextExtension> extensions) {
		this.extensions.clear();
		this.extensions.addAll(extensions);
		for (IDefinitionContextExtension e: extensions) e.setRootContext(this);
	}

	public Set<IDefinitionContextExtension> getExtensions() {
		return extensions;
	}

	public DefinitionContext getCleanCopy() {
		return copy(true);
	}

	private DefinitionContext copy(boolean clean) {
		DefinitionContext copy = new DefinitionContext();
		copy.project = project;
		copy.javaProject = javaProject;
		copy.extensions = new HashSet<IDefinitionContextExtension>();
		for (IDefinitionContextExtension e: extensions) {
			e.newWorkingCopy(clean);
			IDefinitionContextExtension ecopy = e.getWorkingCopy();
			ecopy.setRootContext(copy);
			copy.extensions.add(ecopy);
		}
		if(!clean)  {
			synchronized(this) {
				copy.types.addAll(types);
				for (String qn: typeDefinitions.keySet()) {
					TypeDefinition d = typeDefinitions.get(qn);
					if(d.exists()) {
						copy.typeDefinitions.put(qn, d);
					} else {
						copy.types.remove(qn);
					}
				}
				for (String qn: annotations.keySet()) {
					AnnotationDefinition d = annotations.get(qn);
					if(d.exists()) {
						copy.annotations.put(qn, d);
					}
				}
				copy.vetoedTypes.addAll(vetoedTypes);

				copy.packages.addAll(packages);
				for (String qn: packageDefinitions.keySet()) {
					PackageDefinition d = packageDefinitions.get(qn);
					if(d.exists()) {
						copy.packageDefinitions.put(qn, d);
					} else {
						packages.remove(qn);
					}
				}

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
				copy.beanXMLs.putAll(beanXMLs);
				copy.dependencies = dependencies;
			}
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

	public synchronized void addType(IPath file, String typeName, AbstractTypeDefinition def) {
		addType(file, typeName);
		if(def != null) {
			if(def instanceof AnnotationDefinition) {
				AnnotationDefinition newD = (AnnotationDefinition)def;
				AnnotationDefinition oldD = annotations.get(def.getQualifiedName());
				annotations.put(def.getQualifiedName(), newD);
				if(oldD != null && oldD.getKind() != newD.getKind()) {
					annotationKindChanged(typeName);
				}
			} else {
				typeDefinitions.put(def.getQualifiedName(), (TypeDefinition)def);
			}
		}
	}

	public void addPackage(IPath file, String packageName, PackageDefinition def) {
		if(file != null) {
			file = UniquePaths.getInstance().intern(file);
			Set<String> ts = resources.get(file);
			if(ts == null) {
				ts = new HashSet<String>();
				resources.put(file, ts);
			}
			packageName = packageName.intern();
			ts.add(packageName);
			packages.add(packageName);
			addToParents(file);
		}
		if(def != null) {
			synchronized (this) {
				packageDefinitions.put(def.getQualifiedName(), def);
			}
		}
	}

	public void addBeanXML(IPath path, BeansXMLDefinition def) {
		synchronized (this) {
			beanXMLs.put(path, def);
		}
		addToParents(path);
	}

	public void addType(IPath file, String typeName) {
		if(file != null) {
			file = UniquePaths.getInstance().intern(file);
			Set<String> ts = resources.get(file);
			if(ts == null) {
				ts = new HashSet<String>();
				resources.put(file, ts);
			}
			typeName = typeName.intern();
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
				childPaths.put(UniquePaths.getInstance().intern(q), cs = new HashSet<IPath>());
			}
			cs.add(file);
		}
	}

	public synchronized void clean() {
		childPaths.clear();
		resources.clear();
		types.clear();
		packages.clear();
		typeDefinitions.clear();
		vetoedTypes.clear();
		annotations.clear();
		packageDefinitions.clear();
		beanXMLs.clear();

		clean((IProject)null);

		for (IDefinitionContextExtension e: extensions) e.clean();
		dependencies.clean();
	}

	public synchronized void clean(IProject project) {
		Iterator<String> it = usedAnnotations.keySet().iterator();
		while(it.hasNext()) {
			AnnotationDefinition d = usedAnnotations.get(it.next());
			IType t = d.getType();
			if(t == null || !t.exists() 
					|| t.getJavaProject().getProject() == project || !t.getJavaProject().exists()) {
				it.remove();
			}
		}
	}

	public void clean(IPath path) {
		Set<String> ts = resources.remove(path);
		if(ts != null) for (String t: ts) {
			clean(t);
		}
		synchronized (this) {
			beanXMLs.remove(path);
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
	
		for (IDefinitionContextExtension e: extensions) e.clean(path);
		dependencies.clean(path);
	}

	public synchronized void clean(String typeName) {
		types.remove(typeName);
		typeDefinitions.remove(typeName);
		vetoedTypes.remove(typeName);
		annotations.remove(typeName);
		packages.remove(typeName);
		packageDefinitions.remove(typeName);
		for (IDefinitionContextExtension e: extensions) e.clean(typeName);
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

	private Set<String> underConstruction = new HashSet<String>();

	public int getAnnotationKind(IType annotationType) {
		if(annotationType == null) return -1;
		if(!annotationType.exists()) return -1;
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
		d.setType(annotationType, this, 0);
		int kind = d.getKind();
		if(kind <= AnnotationDefinition.CDI) {
//			d = null; //We need it to compare kind if extensions change it.
		}
		addType(annotationType.getPath(), name, d);
		underConstruction.remove(name);
		return kind;
	}

	public void newWorkingCopy(boolean forFullBuild) {
		if(original != null || workingCopy != null) return;
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
		vetoedTypes = workingCopy.vetoedTypes;
		annotations = workingCopy.annotations;
		packages = workingCopy.packages;
		packageDefinitions = workingCopy.packageDefinitions;
		beanXMLs = workingCopy.beanXMLs;

		Set<IProcessAnnotatedTypeFeature> fs = project.getExtensionManager().getProcessAnnotatedTypeFeatures();
		if(fs != null && !fs.isEmpty()) {
			for (TypeDefinition nd: newTypeDefinitions) {
				for (IProcessAnnotatedTypeFeature f: fs) {
					f.processAnnotatedType(nd, workingCopy);
				}
			}
		}
	
		for (IDefinitionContextExtension e: extensions) {
			e.applyWorkingCopy();
		}

		//extensions may add to dependencies while they change
		dependencies = workingCopy.dependencies;

		project.getDelegate().update(true);

		workingCopy = null;
		
		if(!project.getProject().isAccessible()) {
			clean();
		}
	}

	public void dropWorkingCopy() {
		if(original != null) {
			original.dropWorkingCopy();
		} else {
			workingCopy = null;
		}
	}

	public AnnotationDefinition getAnnotation(IType type) {
		return getAnnotation(type.getFullyQualifiedName());
	}

	/**
	 * Looks up for annotation definition loaded by this project or by projects used by it.
	 */
	public AnnotationDefinition getAnnotation(String fullyQualifiedName) {
		//1. Look in annotations loaded by this project
		AnnotationDefinition result = annotations.get(fullyQualifiedName);
		//2. Validate result.
		if(result != null && (!result.getType().exists())) {
			synchronized (this) {
				annotations.remove(fullyQualifiedName);
			}
			result = null;
		}
		if(result == null || usedAnnotations.containsKey(fullyQualifiedName) 
				|| (result.getType().getJavaProject() != null && result.getType().getJavaProject().getProject() != project.getProject())
				) {
			//3. Look in annotations loaded by used projects
			Set<CDICoreNature> ns2 = project.getCDIProjects(true);
			for (CDICoreNature n: toListOrderedByDependencies(ns2)) {
				DefinitionContext d = n.getDefinitions();
				AnnotationDefinition r = d.annotations.get(fullyQualifiedName);
				if(r != null) {
					result = r;
					//4. Store result for the case if used project is cleaned.
					synchronized (this) {
						usedAnnotations.put(fullyQualifiedName, result);
					}
					break;
				}
			}
		}
		if(result == null && usedAnnotations.containsKey(fullyQualifiedName)) {
			//4. Finally, try in annotations obtained earlier from used projects - they may be cleaned now.
			// The result may be out-of-date until used project is rebuilt.
			result = usedAnnotations.get(fullyQualifiedName);
			if(!result.getType().exists()) {
				synchronized (this) {
					usedAnnotations.remove(fullyQualifiedName);
				}
				result = null;
			}
		}
		return result;
	}

	/**
	 * Returns both annotations loaded by this project, and stored annotations 
	 * loaded by used projects. This method can be only used in combination with 
	 * getting up-to-date annotations from used projects. Stored annotations 
	 * can only be used if used project is cleaned.
	 * 
	 * @return
	 */
	public synchronized List<AnnotationDefinition> getAllAnnotations() {
		List<AnnotationDefinition> result = new ArrayList<AnnotationDefinition>();
		//1. Add annotations loaded by this project.
		result.addAll(annotations.values());
		//2. Add stored annotations loaded by used projects. They may be out-of-date.
		result.addAll(usedAnnotations.values());
		return result;
	}

	public List<AnnotationDefinition> getAllAnnotationsWithDependencies() {
		Set<CDICoreNature> ps = project.getCDIProjects(true);
		if(ps.isEmpty() || ps.contains(project)) {
			return getAllAnnotations();
		}
		List<AnnotationDefinition> result = new ArrayList<AnnotationDefinition>();
		Set<IType> types = new HashSet<IType>();
		for (CDICoreNature p: toListOrderedByDependencies(ps)) {
			List<AnnotationDefinition> ds2 = p.getDefinitions().getAllAnnotations();
			for (AnnotationDefinition d: ds2) {
				IType t = d.getType();
				if(t != null && !types.contains(t)) {
					types.add(t);
					result.add(d);
				}
			}
		}

		List<AnnotationDefinition> ds = getAllAnnotations();
		for (AnnotationDefinition d: ds) {
			IType t = d.getType();
			if(t != null && !types.contains(t)) {
				types.add(t);
				result.add(d);
			}
		}

		return result;
	}

	public List<TypeDefinition> getTypeDefinitions() {
		List<TypeDefinition> result = new ArrayList<TypeDefinition>();
		synchronized (this) {
			result.addAll(typeDefinitions.values());
		}
		for (IDefinitionContextExtension e: extensions) {
			List<TypeDefinition> ds = e.getTypeDefinitions();
			if(ds != null && !ds.isEmpty()) result.addAll(ds);
		}
		return result;
	}

	public Set<BeansXMLDefinition> getBeansXMLDefinitions() {
		Set<BeansXMLDefinition> result = new HashSet<BeansXMLDefinition>();
		synchronized (this) {
			result.addAll(beanXMLs.values());
		}
		return result;
	}

	public PackageDefinition getPackageDefinition(String packageName) {
		return packageDefinitions.get(packageName);
	}

	public TypeDefinition getTypeDefinition(String fullyQualifiedName) {
		return typeDefinitions.get(fullyQualifiedName);
	}

	private void annotationKindChanged(String typeName) {
		List<TypeDefinition> ds = getTypeDefinitions();
		for (TypeDefinition d: ds) {
			d.annotationKindChanged(typeName, this);
		}
	}

	public void veto(IType type) {
		TypeDefinition d = typeDefinitions.get(type.getFullyQualifiedName());
		if(d != null) {
			d.veto();
		} else {
			vetoedTypes.add(type.getFullyQualifiedName());
		}		
	}
	
	public void unveto(IType type) {
		TypeDefinition d = typeDefinitions.get(type.getFullyQualifiedName());
		if(d != null) {
			d.unveto();
		} else {
			vetoedTypes.remove(type.getFullyQualifiedName());
		}
	}

	public Set<String> getVetoedTypes() {
		return vetoedTypes;
	}

	/**
	 * Returns true only if type was requested by this project to be vetoed, but its definition belongs to
	 * another project, where it ma bey not vetoed.
	 * @param type
	 * @return
	 */
	public boolean isVetoedTypeFromUsedProject(IType type) {
		TypeDefinition d = typeDefinitions.get(type.getFullyQualifiedName());
		return d == null && vetoedTypes.contains(type.getFullyQualifiedName());
	}

	public void addDependency(IPath source, IPath target) {
		dependencies.addDependency(source, target);
	}

	public Dependencies getDependencies() {
		return dependencies;
	}

	public Dependencies getAllDependencies() {
		Set<CDICoreNature> ns = project.getCDIProjects(true);
		if(!ns.isEmpty()) {
			Dependencies d = new Dependencies();
			dependencies.copyTo(d);
			for (CDICoreNature n: ns) {
				n.getDefinitions().getDependencies().copyTo(d);
			}
			return d;
		}
		return dependencies;
	}
	
	/**
	 * Returns list with projects ordered by dependencies. 
	 * First go projects that do not depend on others, 
	 * then projects that depend only on these projects,
	 * and so on until the entire set is exhausted.
	 * In the case of circular dependencies, the order is not determined,
	 * the method will return some order that ignores (randomly chosen)
	 * wrong dependencies.
	 * 
	 * @param set
	 * @return
	 */
	public static List<CDICoreNature> toListOrderedByDependencies(Set<CDICoreNature> set) {
		List<CDICoreNature> result = new ArrayList<CDICoreNature>();
		if(set.size() < 2) {
			result.addAll(set);
			return result;
		}

		Map<CDICoreNature, Integer> map = new HashMap<CDICoreNature, Integer>();
		LinkedList<CDICoreNature> leaves = new LinkedList<CDICoreNature>();
		for (CDICoreNature n: set) {
			int k = n.countDirectDependencies(set);
			if(k == 0) {
				leaves.addLast(n);
			} else {
				map.put(n, k);
			}
		}
		while(!map.isEmpty() || !leaves.isEmpty()) {
			while(!leaves.isEmpty()) {
				CDICoreNature n = leaves.removeFirst();
				result.add(n);
				synchronized(n) {
					for (CDICoreNature c: n.getDependentProjects()) {
						Integer i = map.get(c);
						if(i != null) {
							if(i > 1) {
								map.put(c, i - 1);
							} else {
								map.remove(c);
								leaves.addLast(c);
							}
						}
					}					
				}
			}
			if(!map.isEmpty()) {
				//This code will only work when dependencies contain loops.
				//The order is not very important because user has to fix dependencies anyway.
				//But let us try and find some nice order without wasting much time.
				int m = map.size() + 1;
				CDICoreNature n = null;
				for (Map.Entry<CDICoreNature,Integer> e: map.entrySet()) {
					if(e.getValue() < m) {
						m = e.getValue();
						n = e.getKey();
					}
				}
				if(n == null) {
					n = map.keySet().iterator().next();
				}
				map.remove(n);
				leaves.addLast(n);
			}
		}
		return result;
	}

}
