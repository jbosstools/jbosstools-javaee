/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.event.Change;
import org.jboss.tools.seam.core.event.ISeamProjectChangeListener;
import org.jboss.tools.seam.core.event.SeamProjectChangeEvent;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;
import org.jboss.tools.seam.internal.core.scanner.lib.ClassPath;
import org.jboss.tools.seam.internal.core.validation.SeamValidationContext;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamProject implements ISeamProject {
	IProject project;
	ClassPath classPath = new ClassPath(this);
	Map<String, SeamComponent> allComponents = new HashMap<String, SeamComponent>();
	protected Set<ISeamFactory> allFactories = new HashSet<ISeamFactory>();
	Set<ISeamContextVariable> allVariables = new HashSet<ISeamContextVariable>();
	Map<String, SeamJavaComponentDeclaration> javaDeclarations = new HashMap<String, SeamJavaComponentDeclaration>();
	
	List<ISeamProjectChangeListener> listeners = new ArrayList<ISeamProjectChangeListener>();

	SeamValidationContext validationContext;

	public SeamProject() {}

	public void configure() throws CoreException {
	}

	public void deconfigure() throws CoreException {
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
		classPath.init();
		load();
	}
	
	public ClassPath getClassPath() {
		return classPath;
	}

	/**
	 * Loads results of last build, which are considered 
	 * actual until next build.
	 */	
	protected void load() {
	}

	/**
	 * Stores results of last build, so that on exit/enter Eclipse
	 * load them without rebuilding project
	 */
	protected void store() {
	}

	public SeamValidationContext getValidationContext() {
		if(validationContext==null) {
			validationContext = new SeamValidationContext();
		}
		return validationContext;
	}

	public ISeamComponent getComponentByName(String name) {
		return allComponents.get(name);
	}

	public Set<ISeamComponent> getComponents() {
		Set<ISeamComponent> result = new HashSet<ISeamComponent>();
		result.addAll(allComponents.values());
		return result;
	}

	/**
	 * Package local method called by builder.
	 * @param component
	 * @param source
	 */	
	public void registerComponents(LoadedDeclarations ds, IPath source) {
		
		SeamComponentDeclaration[] components = ds.getComponents().toArray(new SeamComponentDeclaration[0]);
		ISeamFactory[] factories = ds.getFactories().toArray(new ISeamFactory[0]);
		
		if(components.length == 0 && factories.length == 0) {
			pathRemoved(source);
			return;
		}
		
		Map<Object,ISeamComponentDeclaration> currentComponents = findComponentDeclarations(source);

		List<Change> addedComponents = null;
		for (int i = 0; i < components.length; i++) {
			SeamComponentDeclaration loaded = (SeamComponentDeclaration)components[i];
			SeamComponentDeclaration current = (SeamComponentDeclaration)currentComponents.remove(loaded.getId());

			loaded.setSourcePath(source);
			
			String name = loaded.getName();

			boolean nameChanged = current != null && !stringsEqual(name, current.getName());
			
			SeamComponent c = getComponent(name);

			if(current != null) {
				List<Change> changes = current.merge(loaded);
				if(changes != null && changes.size() > 0) {
					Change cc = new Change(c, null, null, null);
					cc.addChildren(changes);
					List<Change> cchanges = Change.addChange(null, cc);
					fireChanges(cchanges);
					//TODO if java, fire to others
				}
				if(nameChanged) {
					Map<Object,ISeamComponentDeclaration> old = new HashMap<Object, ISeamComponentDeclaration>();
					old.put(current.getId(), current);
					componentDeclarationsRemoved(old);
					loaded = current;
					current = null;
					c = getComponent(name);
				} else {
					continue;
				}
			}
			
			if(c == null && name != null) {
				c = newComponent(name);
				allComponents.put(name, c);
				allVariables.add(c);
				c.addDeclaration(loaded);
				addedComponents = Change.addChange(addedComponents, new Change(this, null, null, c));
			} else if(c != null) {
				c.addDeclaration(loaded);
				List<Change> changes = Change.addChange(null, new Change(c, null, null, loaded));
				fireChanges(changes);
			}

			if(loaded instanceof ISeamJavaComponentDeclaration) {
				SeamJavaComponentDeclaration jd = (SeamJavaComponentDeclaration)loaded;
				javaDeclarations.put(jd.getClassName(), jd);
				Set<ISeamComponent> cs = getComponentsByClass(jd.getClassName());
				for (ISeamComponent ci: cs) {
					if(ci == c) continue;
					SeamComponent cii = (SeamComponent)ci;
					cii.addDeclaration(loaded);
					List<Change> changes = Change.addChange(null, new Change(ci, null, null, loaded));
					fireChanges(changes);
				}
			} else if(loaded instanceof ISeamXmlComponentDeclaration) {
				ISeamXmlComponentDeclaration xml = (ISeamXmlComponentDeclaration)loaded;
				String className = xml.getClassName();
				SeamJavaComponentDeclaration j = javaDeclarations.get(className);
				if(j != null) {
					c.addDeclaration(j);
					List<Change> changes = Change.addChange(null, new Change(c, null, null, j));
					fireChanges(changes);
				}
			}			
		}
		fireChanges(addedComponents);
		
		componentDeclarationsRemoved(currentComponents);
		
		Map<Object, ISeamFactory> currentFactories = findFactoryDeclarations(source);
		List<Change> addedFactories = null;
		for (int i = 0; i < factories.length; i++) {
			AbstractContextVariable loaded = (AbstractContextVariable)factories[i];
			AbstractContextVariable current = (AbstractContextVariable)currentFactories.remove(loaded.getId());
			if(current != null) {
				List<Change> changes = current.merge(loaded);
				fireChanges(changes);
				continue;
			}
			allFactories.add(factories[i]);
			allVariables.add(factories[i]);
			addedFactories = Change.addChange(addedFactories, new Change(this, null, null, loaded));
		}
		fireChanges(addedFactories); 
		
		factoryDeclarationsRemoved(currentFactories);
	}

	boolean stringsEqual(String s1, String s2) {
		return s1 == null ? s2 == null : s1.equals(s2);
	}

	/**
	 * Package local method called by builder.
	 * @param source
	 */
	public void pathRemoved(IPath source) {
		Iterator<SeamComponent> iterator = allComponents.values().iterator();
		while(iterator.hasNext()) {
			List<Change> changes = null;
			SeamComponent c = iterator.next();
			SeamComponentDeclaration[] ds = c.getAllDeclarations().toArray(new SeamComponentDeclaration[0]);
			for (int i = 0; i < ds.length; i++) {
				if(ds[i].source.equals(source)) {
					c.removeDeclaration(ds[i]);
					if(ds[i] instanceof ISeamJavaComponentDeclaration) {
						String className = ((ISeamJavaComponentDeclaration)ds[i]).getClassName();
						javaDeclarations.remove(className);
					}
					changes = Change.addChange(changes, new Change(c, null, ds[i], null));
				}
			}
			if(c.getAllDeclarations().size() == 0) {
				iterator.remove();
				allVariables.remove(c);
				changes = null;
				changes = Change.addChange(changes, new Change(this, null, c, null));
				
			}
			fireChanges(changes);
		}
		Iterator<ISeamFactory> factories = allFactories.iterator();
		while(factories.hasNext()) {
			AbstractContextVariable f = (AbstractContextVariable)factories.next();
			if(source.equals(f.getSourcePath())) {
				List<Change> changes = Change.addChange(null, new Change(this, null, f, null));
				factories.remove();
				allVariables.remove(f);
				fireChanges(changes);
			}
		}
	}
	
	public Map<Object,ISeamComponentDeclaration> findComponentDeclarations(IPath source) {
		Map<Object,ISeamComponentDeclaration> map = new HashMap<Object, ISeamComponentDeclaration>();
		for (SeamComponent c: allComponents.values()) {
			for (ISeamComponentDeclaration d: c.getAllDeclarations()) {
				SeamComponentDeclaration di = (SeamComponentDeclaration)d;
				if(source.equals(di.getSourcePath())) map.put(di.getId(), di);
			}
		}		
		return map;
	}
	
	void componentDeclarationsRemoved(Map<Object,ISeamComponentDeclaration> removed) {
		Iterator<SeamComponent> iterator = allComponents.values().iterator();
		while(iterator.hasNext()) {
			List<Change> changes = null;
			SeamComponent c = iterator.next();
			SeamComponentDeclaration[] ds = c.getAllDeclarations().toArray(new SeamComponentDeclaration[0]);
			for (int i = 0; i < ds.length; i++) {
				if(removed.containsKey(ds[i].getId())) {
					if(ds[i] instanceof ISeamJavaComponentDeclaration) {
						String className = ((ISeamJavaComponentDeclaration)ds[i]).getClassName();
						if(javaDeclarations.get(className) == ds[i]) {
							javaDeclarations.remove(className);
						}
					}
					c.removeDeclaration(ds[i]);
					changes = Change.addChange(changes, new Change(c, null, ds[i], null));
				}
			}
			if(c.getAllDeclarations().size() == 0) {
				iterator.remove();
				allVariables.remove(c);
				changes = Change.addChange(null, new Change(this, null, c, null));
			}
			fireChanges(changes);
		}		
	}

	public Map<Object,ISeamFactory> findFactoryDeclarations(IPath source) {
		Map<Object,ISeamFactory> map = new HashMap<Object, ISeamFactory>();
		for (ISeamFactory c: allFactories) {
			AbstractContextVariable ci = (AbstractContextVariable)c;
			if(source.equals(ci.getSourcePath())) map.put(ci.getId(), c);
		}		
		return map;
	}
	
	void factoryDeclarationsRemoved(Map<Object,ISeamFactory> removed) {
		Iterator<ISeamFactory> iterator = allFactories.iterator();
		List<Change> changes = null;
		while(iterator.hasNext()) {
			AbstractContextVariable c = (AbstractContextVariable)iterator.next();
			if(removed.containsKey(c.getId())) {
				iterator.remove();
				allVariables.remove(c);
				changes = Change.addChange(changes, new Change(this, null, c, null));
			}
		}
		fireChanges(changes);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getComponentsByClass(java.lang.String)
	 */
	public Set<ISeamComponent> getComponentsByClass(String className) {
		Set<ISeamComponent> result = new HashSet<ISeamComponent>();
		for(SeamComponent component: allComponents.values()) {
			if(className.equals(component.getClassName())) {
				result.add(component);
			}
		}		
		return result;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getComponentsByScope(org.jboss.tools.seam.core.ScopeType)
	 */
	public Set<ISeamComponent> getComponentsByScope(ScopeType type) {
		Set<ISeamComponent> result = new HashSet<ISeamComponent>();
		for(SeamComponent component: allComponents.values()) {
			if(type.equals(component.getScope())) {
				result.add(component);
			}
		}		
		return result;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#addComponent(org.jboss.tools.seam.core.ISeamComponent)
	 */
	public void addComponent(ISeamComponent component) {
		allComponents.put(component.getName(), (SeamComponent)component);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#removeComponent(org.jboss.tools.seam.core.ISeamComponent)
	 */
	public void removeComponent(ISeamComponent component) {
		allComponents.remove(component);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getVariables()
	 */
	public Set<ISeamContextVariable> getVariables() {
		return allVariables;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getVariablesByName(java.lang.String)
	 */
	public Set<ISeamContextVariable> getVariablesByName(String name) {
		Set<ISeamContextVariable> result = new HashSet<ISeamContextVariable>();
		for (ISeamContextVariable v: allVariables) {
			if(name.equals(v.getName())) {
				result.add(v);
			}
		}
		return result;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getVariablesByScope(org.jboss.tools.seam.core.ScopeType)
	 */
	public Set<ISeamContextVariable> getVariablesByScope(ScopeType scope) {
		Set<ISeamContextVariable> result = new HashSet<ISeamContextVariable>();
		for (ISeamContextVariable v: allVariables) {
			if(scope.equals(v.getScope())) {
				result.add(v);
			}
		}
		return result;
	}

	public void addFactory(ISeamFactory factory) {
		allFactories.add(factory);		
	}

	public Set<ISeamFactory> getFactories() {
		return allFactories;
	}

	public Set<ISeamFactory> getFactories(String name, ScopeType scope) {
		Set<ISeamFactory> result = new HashSet<ISeamFactory>();
		for (ISeamFactory f: allFactories) {
			if(name.equals(f.getName()) && scope.equals(f.getScope())) result.add(f);
		}
		return result;
	}

	public Set<ISeamFactory> getFactoriesByName(String name) {
		Set<ISeamFactory> result = new HashSet<ISeamFactory>();
		for (ISeamFactory f: allFactories) {
			if(name.equals(f.getName())) result.add(f);
		}
		return result;
	}

	public Set<ISeamFactory> getFactoriesByScope(ScopeType scope) {
		Set<ISeamFactory> result = new HashSet<ISeamFactory>();
		for (ISeamFactory f: allFactories) {
			if(scope.equals(f.getScope())) result.add(f);
		}
		return result;
	}

	public void removeFactory(ISeamFactory factory) {
		allFactories.remove(factory);
		allVariables.remove(factory);
	}
	
	public SeamComponent getComponent(String name) {
		return name == null ? null : allComponents.get(name);
	}
	
	SeamComponent newComponent(String name) {
		SeamComponent c = new SeamComponent();
		c.setName(name);
		return c;
	}
	
	void fireChanges(List<Change> changes) {
		if(changes == null || changes.size() == 0) return;
		SeamProjectChangeEvent event = new SeamProjectChangeEvent(this, changes);
		ISeamProjectChangeListener[] ls = null;
		synchronized(this) {
			ls = listeners.toArray(new ISeamProjectChangeListener[0]);
		}
		if(ls != null) {
			for (int i = 0; i < ls.length; i++) {
				ls[i].projectChanged(event);
			}
		}
	}

	public synchronized void addSeamProjectListener(ISeamProjectChangeListener listener) {
		if(listeners.contains(listener)) return;
		listeners.add(listener);
	}

	public synchronized void removeSeamProjectListener(ISeamProjectChangeListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getComponentsByResource(org.eclipse.core.resources.IResource)
	 */
	public Set<ISeamComponent> getComponentsByPath(IPath path) {
		Set<ISeamComponent> result = new HashSet<ISeamComponent>();
		for (SeamComponent c: allComponents.values()) {
			for (ISeamComponentDeclaration d: c.getAllDeclarations()) {
				SeamComponentDeclaration di = (SeamComponentDeclaration)d;
				if(path.equals(di.getSourcePath())) {
					result.add(c);
					break;
				}
			}
		}
		return result;
	}
}