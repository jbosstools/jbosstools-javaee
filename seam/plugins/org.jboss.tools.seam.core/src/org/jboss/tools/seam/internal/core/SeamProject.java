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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamScope;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamCoreBuilder;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.event.Change;
import org.jboss.tools.seam.core.event.ISeamProjectChangeListener;
import org.jboss.tools.seam.core.event.SeamProjectChangeEvent;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;
import org.jboss.tools.seam.internal.core.scanner.lib.ClassPath;
import org.jboss.tools.seam.internal.core.validation.SeamValidationContext;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamProject extends SeamObject implements ISeamProject {
	IProject project;
	ClassPath classPath = new ClassPath(this);
	
	Set<IPath> sourcePaths = new HashSet<IPath>();
	private boolean isStorageResolved = false;
	
	SeamScope[] scopes = new SeamScope[ScopeType.values().length];
	Map<ScopeType, SeamScope> scopesMap = new HashMap<ScopeType, SeamScope>();
	
	{
		ScopeType[] types = ScopeType.values();
		for (int i = 0; i < scopes.length; i++) {
			scopes[i] = new SeamScope(this, types[i]);
			scopesMap.put(types[i], scopes[i]);
		}
	}
	
	Map<String, SeamComponent> allComponents = new HashMap<String, SeamComponent>();
	protected Set<ISeamFactory> allFactories = new HashSet<ISeamFactory>();
	Set<ISeamContextVariable> allVariables = new HashSet<ISeamContextVariable>();
	Map<String, SeamJavaComponentDeclaration> javaDeclarations = new HashMap<String, SeamJavaComponentDeclaration>();
	
	List<ISeamProjectChangeListener> listeners = new ArrayList<ISeamProjectChangeListener>();

	SeamValidationContext validationContext;

	public SeamProject() {}

	public void configure() throws CoreException {
		addToBuildSpec(SeamCoreBuilder.BUILDER_ID);
	}

	public void deconfigure() throws CoreException {
		removeFromBuildSpec(SeamCoreBuilder.BUILDER_ID);
	}

	public IProject getProject() {
		return project;
	}
	
	/**
	 * Returns list of scope objects for all scope types.
	 * @return
	 */
	public ISeamScope[] getScopes() {
		return scopes;
	}
	
	/**
	 * Returns scope object for specified scope type.
	 * @param scopeType
	 * @return
	 */
	public ISeamScope getScope(ScopeType scopeType) {
		return scopesMap.get(scopeType);
	}

	public ISeamProject getSeamProject() {
		return this;
	}

	public void setProject(IProject project) {
		this.project = project;
		setSourcePath(project.getFullPath());
		resource = project;
		classPath.init();
//		load();
	}
	
	public ClassPath getClassPath() {
		return classPath;
	}
	
	public void resolveStorage(boolean load) {
		if(isStorageResolved) return;
		if(load) {
			load(); 
		} else {
			isStorageResolved = true;
		}
	}

	public void resolve() {
		resolveStorage(true);
	}

	/**
	 * Loads results of last build, which are considered 
	 * actual until next build.
	 */	
	public void load() {
		if(isStorageResolved) return;
		isStorageResolved = true;
		
		long begin = System.currentTimeMillis();
		
		if(getClassPath().update()) {
			getClassPath().process();
		}
		File file = getStorageFile();
		if(file == null || !file.isFile()) return;
		String s = FileUtil.readFile(file);
		String[] ps = s.split("\n");
		for (int i = 0; i < ps.length; i++) {
			IPath path = new Path(ps[i].trim());
			if(sourcePaths.contains(path)) continue;
			IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			if(f == null || !f.exists() || !f.isSynchronized(IResource.DEPTH_ZERO)) continue;
			SeamResourceVisitor b = new SeamResourceVisitor(this);
			b.visit(f);
		}
		
		long e = System.currentTimeMillis();
		
		System.out.println("loaded in " + (e - begin));
	}

	/**
	 * Stores results of last build, so that on exit/enter Eclipse
	 * load them without rebuilding project
	 */
	public void store() {
		File file = getStorageFile();
		file.getParentFile().mkdirs();
		StringBuffer sb = new StringBuffer();
		for (IPath path : sourcePaths) {
			sb.append(path.toString()).append('\n');
		}		
		FileUtil.writeFile(file, sb.toString());
	}
	
	private File getStorageFile() {
		IPath path = SeamCorePlugin.getDefault().getStateLocation();
		File file = new File(path.toFile(), "projects/" + project.getName());
		return file;
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
		if(!sourcePaths.contains(source)) sourcePaths.add(source);
		
		revalidateScopesLock++;

		Map<Object,ISeamComponentDeclaration> currentComponents = findComponentDeclarations(source);

		List<Change> addedComponents = null;
		for (int i = 0; i < components.length; i++) {
			SeamComponentDeclaration loaded = (SeamComponentDeclaration)components[i];
			loaded.setParent(this);
			SeamComponentDeclaration current = (SeamComponentDeclaration)currentComponents.remove(loaded.getId());

			loaded.setSourcePath(source);
			
			String name = loaded.getName();

			boolean nameChanged = current != null && !stringsEqual(name, current.getName());
			
			SeamComponent c = getComponent(name);
			
			String oldClassName = c == null ? null : c.getClassName();

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
					if(loaded instanceof ISeamXmlComponentDeclaration) {
						ISeamXmlComponentDeclaration xml = (ISeamXmlComponentDeclaration)loaded;
						onXMLLoadedDeclaration(c, oldClassName, xml);
					}
					continue;
				}
			}
			
			if(c == null && name != null) {
				ScopeType scopeType = loaded.getScope();
				c = newComponent(name, scopeType);
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
				Set<IBijectedAttribute> as = jd.getBijectedAttributes();
				for (IBijectedAttribute a : as) {
					if(a.isContextVariable()) allVariables.add(a);
				}
				allVariables.addAll(jd.getRoles());
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
				onXMLLoadedDeclaration(c, oldClassName, xml);
			}			
		}
		fireChanges(addedComponents);
		
		componentDeclarationsRemoved(currentComponents);

		revalidateScopesLock--;
		revalidateScopes();

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
			if(factories[i].getParent() == null) {
				adopt(factories[i]);
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
	
	private boolean isClassNameChanged(String oldClassName, String newClassName) {
		if(oldClassName == null || oldClassName.length() == 0) return false;
		if(newClassName == null || newClassName.length() == 0) return false;
		return !oldClassName.equals(newClassName);
	}
	
	private void onXMLLoadedDeclaration(SeamComponent c, String oldClassName, ISeamXmlComponentDeclaration xml) {
		String className = xml.getClassName();
		List<Change> changes = null;
		if(isClassNameChanged(oldClassName, className)) {
			SeamComponentDeclaration[] ds1 = c.getAllDeclarations().toArray(new SeamComponentDeclaration[0]);
			for (int i1 = 0; i1 < ds1.length; i1++) {
				if(!(ds1[i1] instanceof ISeamJavaComponentDeclaration)) continue;
				ISeamJavaComponentDeclaration jcd = (ISeamJavaComponentDeclaration)ds1[i1];
				if(jcd.getClassName().equals(className)) continue;
				c.removeDeclaration(jcd);
				changes = Change.addChange(changes, new Change(c, null, jcd, null));
			}
		}
		SeamJavaComponentDeclaration j = javaDeclarations.get(className);
		if(j != null && !c.getAllDeclarations().contains(j)) {
			c.addDeclaration(j);
			changes = Change.addChange(changes, new Change(c, null, null, j));
		}
		fireChanges(changes);
	}

	/**
	 * Package local method called by builder.
	 * @param source
	 */
	public void pathRemoved(IPath source) {
		if(!sourcePaths.contains(source)) return;
		sourcePaths.remove(source);
		revalidateScopesLock++;
		Iterator<SeamComponent> iterator = allComponents.values().iterator();
		while(iterator.hasNext()) {
			List<Change> changes = null;
			SeamComponent c = iterator.next();
			SeamComponentDeclaration[] ds = c.getAllDeclarations().toArray(new SeamComponentDeclaration[0]);
			for (int i = 0; i < ds.length; i++) {
				if(ds[i].source.equals(source)) {
					c.removeDeclaration(ds[i]);
					if(ds[i] instanceof ISeamJavaComponentDeclaration) {
						ISeamJavaComponentDeclaration jd = (ISeamJavaComponentDeclaration)ds[i];
						String className = jd.getClassName();
						javaDeclarations.remove(className);
						allVariables.removeAll(jd.getBijectedAttributes());
						allVariables.removeAll(jd.getRoles());
					}
					changes = Change.addChange(changes, new Change(c, null, ds[i], null));
				}
			}
			if(isComponentEmpty(c)) {
				iterator.remove();
				changes = removeEmptyComponent(c);
			}
			fireChanges(changes);
		}
		revalidateScopesLock--;
		revalidateScopes();

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
		Collection<ISeamComponentDeclaration> declarations = removed.values();
		for (ISeamComponentDeclaration declaration: declarations) {
			if(declaration instanceof ISeamJavaComponentDeclaration) {
				ISeamJavaComponentDeclaration jd = (ISeamJavaComponentDeclaration)declaration;
				String className = jd.getClassName();
				if(javaDeclarations.get(className) == jd) {
					javaDeclarations.remove(className);
				}
				allVariables.removeAll(jd.getRoles());
				allVariables.removeAll(jd.getBijectedAttributes());
			}
		}
		
		Iterator<SeamComponent> iterator = allComponents.values().iterator();
		while(iterator.hasNext()) {
			List<Change> changes = null;
			SeamComponent c = iterator.next();
			SeamComponentDeclaration[] ds = c.getAllDeclarations().toArray(new SeamComponentDeclaration[0]);
			for (int i = 0; i < ds.length; i++) {
				if(removed.containsKey(ds[i].getId())) {
					c.removeDeclaration(ds[i]);
					changes = Change.addChange(changes, new Change(c, null, ds[i], null));
				}
			}
			if(isComponentEmpty(c)) {
				iterator.remove();
				changes = removeEmptyComponent(c);
			}
			fireChanges(changes);
		}		
	}
	
	private List<Change> removeEmptyComponent(SeamComponent c) {
		List<Change> changes = null;
		ISeamElement p = c.getParent();
		if(p instanceof SeamScope) {
			((SeamScope)p).removeComponent(c);
			changes = Change.addChange(null, new Change(p, null, c, null));
		}
		allVariables.remove(c);
		changes = Change.addChange(changes, new Change(this, null, c, null));
		return changes;
	}
	
	private boolean isComponentEmpty(SeamComponent c) {
		if(c.getAllDeclarations().size() == 0) return true;
		for (ISeamComponentDeclaration d: c.getAllDeclarations()) {
			if(c.getName().equals(d.getName())) return false;
		}
		return true;
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
	
	SeamComponent newComponent(String name, ScopeType scopeType) {
		SeamComponent c = new SeamComponent();
		c.setName(name);
		c.setId(name);
		c.setParent(getScope(scopeType));
		((SeamScope)getScope(scopeType)).addComponent(c);
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

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getVariablesByPath(org.eclipse.core.runtime.IPath)
	 */
	public Set<ISeamContextVariable> getVariablesByPath(IPath path) {
		Set<ISeamContextVariable> result = new HashSet<ISeamContextVariable>();
		for (ISeamContextVariable variable : allVariables) {
			if(variable instanceof ISeamComponent) {
				ISeamComponent c = (ISeamComponent)variable;
				for (ISeamComponentDeclaration d: c.getAllDeclarations()) {
					SeamComponentDeclaration di = (SeamComponentDeclaration)d;
					if(path.equals(di.getSourcePath())) {
						result.add(variable);
						break;
					}
				}
			} else {
				IResource variableResource = variable.getResource();
				if(path.equals(variableResource.getFullPath())) {
					result.add(variable);
				}
			}
		}
		return result;
	}

	int revalidateScopesLock = 0;
	void revalidateScopes() {
		List<Change> changes = null;
		if(revalidateScopesLock > 0) return;
		for(SeamComponent c : allComponents.values()) {
			SeamScope pc = (SeamScope)c.getParent();
			SeamScope pn = (SeamScope)getScope(c.getScope());
			if(pc == pn) continue;
			c.setParent(pn);
			if(pc != null) {
				pc.removeComponent(c);
				changes = Change.addChange(changes, new Change(pc, null, c, null));
			}
			pn.addComponent(c);
			changes = Change.addChange(changes, new Change(pn, null, null, c));
		}
		fireChanges(changes);
	}
	

	protected void addToBuildSpec(String builderID) throws CoreException {
		IProjectDescription description = getProject().getDescription();
		ICommand command = null;
		ICommand commands[] = description.getBuildSpec();
		for (int i = 0; i < commands.length && command == null; ++i) {
			if (commands[i].getBuilderName().equals(builderID)) 
				command = commands[i];
		}
		if (command == null) {
			command = description.newCommand();
			command.setBuilderName(builderID);
			ICommand[] oldCommands = description.getBuildSpec();
			ICommand[] newCommands = new ICommand[oldCommands.length + 1];
			System.arraycopy(oldCommands, 0, newCommands, 0, oldCommands.length);
			newCommands[oldCommands.length] = command;
			description.setBuildSpec(newCommands);
			getProject().setDescription(description, null);
		}
	}

	static String EXTERNAL_TOOL_BUILDER = "org.eclipse.ui.externaltools.ExternalToolBuilder";
	static final String LAUNCH_CONFIG_HANDLE = "LaunchConfigHandle";

	protected void removeFromBuildSpec(String builderID) throws CoreException {
		IProjectDescription description = getProject().getDescription();
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			String builderName = commands[i].getBuilderName();
			if (!builderName.equals(builderID)) {
				if(!builderName.equals(EXTERNAL_TOOL_BUILDER)) continue;
				Object handle = commands[i].getArguments().get(LAUNCH_CONFIG_HANDLE);
				if(handle == null || handle.toString().indexOf(builderID) < 0) continue;
			}
			ICommand[] newCommands = new ICommand[commands.length - 1];
			System.arraycopy(commands, 0, newCommands, 0, i);
			System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
			description.setBuildSpec(newCommands);
			getProject().setDescription(description, null);
			return;
		}
	}
	
}
