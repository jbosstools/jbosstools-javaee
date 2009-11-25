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
package org.jboss.tools.seam.internal.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jst.jsf.designtime.DesignTimeApplicationManager;
import org.jboss.tools.common.model.project.ext.event.Change;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamNamespace;
import org.jboss.tools.seam.core.ISeamPackage;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamScope;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamCoreBuilder;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.event.ISeamProjectChangeListener;
import org.jboss.tools.seam.core.event.SeamProjectChangeEvent;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.internal.core.el.VariableResolver;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;
import org.jboss.tools.seam.internal.core.scanner.lib.ClassPath;
import org.jboss.tools.jst.web.kb.internal.validation.ProjectValidationContext;
import org.osgi.service.prefs.BackingStoreException;
import org.w3c.dom.Element;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamProject extends SeamObject implements ISeamProject, IProjectNature {
	
	IProject project;
	
	ClassPath classPath = new ClassPath(this);
	
//	boolean useDefaultRuntime = false;
	
	Set<IPath> sourcePaths = new HashSet<IPath>();
	
	Map<IPath, LoadedDeclarations> sourcePaths2 = new HashMap<IPath, LoadedDeclarations>();
	
	private boolean isStorageResolved = false;
	
	SeamScope[] scopes = new SeamScope[ScopeType.values().length];
	
	Map<ScopeType, SeamScope> scopesMap = new HashMap<ScopeType, SeamScope>();
	
	Set<SeamProject> dependsOn = new HashSet<SeamProject>();
	
	Set<SeamProject> usedBy = new HashSet<SeamProject>();
	
	Map<String,List<String>> imports = new HashMap<String, List<String>>();
	
	{
		createScopes();
	}

	NamespaceStorage namespaces = new NamespaceStorage();
	ComponentStorage components = new ComponentStorage();
	FactoryStorage factories = new FactoryStorage();
	VariablesStorage variables = new VariablesStorage();

	Map<String, ISeamPackage> packages = new HashMap<String, ISeamPackage>();

	List<ISeamProjectChangeListener> listeners = new ArrayList<ISeamProjectChangeListener>();

	ProjectValidationContext validationContext;

	/**
	 * 
	 */
	public SeamProject() {}

	/**
	 * 
	 */
	public void configure() throws CoreException {
		addToBuildSpec(SeamCoreBuilder.BUILDER_ID);
		DesignTimeApplicationManager dtAppManager = DesignTimeApplicationManager.getInstance(project);
		if(dtAppManager!=null)
			dtAppManager.setVariableResolverProvider(VariableResolver.ID);
	}

	/**
	 * 
	 */
	public void deconfigure() throws CoreException {
		removeFromBuildSpec(SeamCoreBuilder.BUILDER_ID);
		DesignTimeApplicationManager dtAppManager = DesignTimeApplicationManager.getInstance(project);
		if(dtAppManager!=null) {
			dtAppManager.setVariableResolverProvider(null);
		}
	}

	/**
	 * 
	 */
	public IProject getProject() {
		return project;
	}

	/**
	 * 
	 */
	public String getRuntimeName() {
		IEclipsePreferences p = getSeamPreferences();
		return p.get(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, null);
	}

	/**
	 * 
	 */
	public SeamRuntime getRuntime() {
		String parent = getParentProjectName();
		if(parent != null) {
			IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(parent);
			if(p == null || !p.isAccessible()) return null;
			ISeamProject sp = SeamCorePlugin.getSeamProject(p, false);
			return sp == null ? null : sp.getRuntime();
		}
		String runtimeName = getRuntimeName();
		return runtimeName == null ? null : SeamRuntimeManager.getInstance().findRuntimeByName(runtimeName);
	}

	public String getParentProjectName() {
		IEclipsePreferences p = getSeamPreferences();
		return p == null ? null : p.get(ISeamFacetDataModelProperties.SEAM_PARENT_PROJECT, null);
	}
	
	public ISeamProject getParentProject() {
		String n = getParentProjectName();
		if(n == null || n.length() == 0) return null;
		IProject parent = ResourcesPlugin.getWorkspace().getRoot().getProject(n);
		return n == null ? null : SeamCorePlugin.getSeamProject(parent, true);
	}

	public void setRuntimeName(String runtimeName) {
		IEclipsePreferences prefs = getSeamPreferences();
		String storedRuntimeName = getRuntimeName();
		boolean changed = (storedRuntimeName == null) ? runtimeName != null : !storedRuntimeName.equals(runtimeName);
		if(!changed) return;

		if(runtimeName == null) {
			prefs.remove(RUNTIME_NAME);
		} else {
			prefs.put(RUNTIME_NAME, runtimeName);
		}		
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
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
	public SeamScope getScope(ScopeType scopeType) {
		return scopesMap.get(scopeType);
	}
	
	/**
	 * 
	 */
	public Collection<ISeamPackage> getPackages() {
		synchronized(packages) {
			return packages.values();
		}
	}
	
	public void removePackage(ISeamPackage p) {
		synchronized(packages) {
			packages.remove(p.getName());
		}
	}
	
	/**
	 * 
	 */
	public Collection<ISeamPackage> getAllPackages() {
		List<ISeamPackage> list = new ArrayList<ISeamPackage>();
		SeamPackageUtil.collectAllPackages(packages, list);
		return list;
	}
	
	/**
	 * 
	 */
	public ISeamPackage getPackage(ISeamComponent c) {
		String pkg = SeamPackageUtil.getPackageName(c);
		return SeamPackageUtil.findPackage(this, packages, pkg);
	}

	/**
	 * 
	 */
	public ISeamProject getSeamProject() {
		return this;
	}

	/**
	 * 
	 */
	public void setProject(IProject project) {
		this.project = project;
		setSourcePath(project.getFullPath());
		resource = project;
		classPath.init();
//		load();
	}

	IEclipsePreferences preferences = null;

	/**
	 * 
	 * @return
	 */
	public IEclipsePreferences getSeamPreferences() {
		if(preferences == null) {
			IScopeContext projectScope = new ProjectScope(project);
			preferences = projectScope.getNode(SeamCorePlugin.PLUGIN_ID);
		}
		return preferences;
	}

	/**
	 * 
	 * @param p
	 */
	public void addSeamProject(SeamProject p) {
		if(dependsOn.contains(p)) return;
		dependsOn.add(p);
		p.addDependentSeamProject(this);
		if(!p.isStorageResolved) {
			p.resolve();
		} else {
			Map<IPath,LoadedDeclarations> map = null;
			try {
				map = p.getAllDeclarations();
			} catch (CloneNotSupportedException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
			for (IPath source : map.keySet()) {
				LoadedDeclarations ds = map.get(source);
				registerComponents(ds, source);
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<SeamProject> getSeamProjects() {
		return dependsOn;
	}
	
	/**
	 * 
	 * @param p
	 */
	public void addDependentSeamProject(SeamProject p) {
		usedBy.add(p);
	}

	public Map<String, Set<ISeamNamespace>> getNamespaces() {
		return namespaces.namespacesByURI;
	}

	/**
	 * 
	 * @param p
	 */
	public void removeSeamProject(SeamProject p) {
		if(!dependsOn.contains(p)) return;
		p.usedBy.remove(this);
		dependsOn.remove(p);
		IPath[] ps = sourcePaths2.keySet().toArray(new IPath[0]);
		for (int i = 0; i < ps.length; i++) {
			IPath pth = ps[i];
			if(p.getSourcePath().isPrefixOf(pth) || (p.isPathLoaded(pth) && !EclipseResourceUtil.isJar(pth.toString()))) {
				pathRemoved(pth);
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public ClassPath getClassPath() {
		return classPath;
	}
	
	/**
	 * 
	 * @param load
	 */
	public void resolveStorage(boolean load) {
		if(isStorageResolved) return;
		if(load) {
			load();
		} else {
			isStorageResolved = true;
		}
	}

	/**
	 * 
	 */
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
		
		postponeFiring();
		
		try {
		
			boolean b = getClassPath().update();
			if(b) {
				getClassPath().validateProjectDependencies();
			}
			File file = getStorageFile();
			Element root = null;
			if(file != null && file.isFile()) {
				root = XMLUtilities.getElement(file, null);
				if(root != null) {
					loadProjectDependencies(root);
					loadNamespaces(root);
					if(XMLUtilities.getUniqueChild(root, "paths") != null) {
						loadSourcePaths2(root);
					} else {
						//old code
						loadSourcePaths(root);
					}
				}
			}

			if(b) {
				getClassPath().process();
			}

			if(root != null) {
				getValidationContext().load(root);
			}
		
		} finally {
			fireChanges();
		}

	}

	public void clean() {
		File file = getStorageFile();
		if(file != null && file.isFile()) {
			file.delete();
		}
		classPath.clean();
		postponeFiring();
		IPath[] ps = sourcePaths2.keySet().toArray(new IPath[0]);
		for (int i = 0; i < ps.length; i++) {
			pathRemoved(ps[i]);
		}
		packages.clear();
		createScopes();
		fireChanges();
	}
	
	public long fullBuildTime;
	public List<Long> statistics;

	/**
	 * Method testing how long it takes to load Seam model
	 * serialized previously.
	 * This approach makes sure, that all other services 
	 * (JDT, XModel, etc) are already loaded at first start of 
	 * Seam model, so that now it is more or less pure time 
	 * to be computed.
	 * 
	 * @return
	 */
	public long reload() {
		statistics = new ArrayList<Long>();
		classPath = new ClassPath(this);
		sourcePaths.clear();
		sourcePaths2.clear();
		isStorageResolved = false;
		dependsOn.clear();
		usedBy.clear();
		namespaces.clear();
		components.clear();
		factories.clear();
		variables.clear();
		imports.clear();
		packages.clear();
		createScopes();
		
		long begin = System.currentTimeMillis();

		classPath.init();
		resolve();

		long end = System.currentTimeMillis();
		return end - begin;
	}

	private void createScopes() {
		ScopeType[] types = ScopeType.values();
		for (int i = 0; i < scopes.length; i++) {
			scopes[i] = new SeamScope(this, types[i]);
			scopesMap.put(types[i], scopes[i]);
		}
	}
	/**
	 * Stores results of last build, so that on exit/enter Eclipse
	 * load them without rebuilding project
	 * @throws IOException 
	 */
	public void store() throws IOException {
		File file = getStorageFile();
		file.getParentFile().mkdirs();
		
		Element root = XMLUtilities.createDocumentElement("seam-project"); //$NON-NLS-1$
		storeProjectDependencies(root);

		storeNamespaces(root);
//		storeSourcePaths(root);
		storeSourcePaths2(root);
		
		if(validationContext != null) validationContext.store(root);
		
		XMLUtilities.serialize(root, file.getAbsolutePath());
	}

	/*
	 * 
	 */
//	private void storeSourcePaths(Element root) {
//		Element sourcePathsElement = XMLUtilities.createElement(root, "source-paths"); //$NON-NLS-1$
//		for (IPath path : sourcePaths) {
//			Element pathElement = XMLUtilities.createElement(sourcePathsElement, "path"); //$NON-NLS-1$
//			pathElement.setAttribute("value", path.toString()); //$NON-NLS-1$
//		}
//	}

	private void storeNamespaces(Element root) {
		Element namespacesElement = XMLUtilities.createElement(root, "namespaces"); //$NON-NLS-1$
		for (String uri : namespaces.namespacesByURI.keySet()) {
			Set<ISeamNamespace> s = namespaces.namespacesByURI.get(uri);
			for (ISeamNamespace n: s) {
				((SeamNamespace)n).toXML(namespacesElement);
			}
		}
	}

	private void storeSourcePaths2(Element root) {
		Properties context = new Properties();
		Element sourcePathsElement = XMLUtilities.createElement(root, "paths"); //$NON-NLS-1$
		for (IPath path : sourcePaths2.keySet()) {
			IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			if(f != null && f.exists() && f.getProject() != project) {
				continue;
			}
			context.put(SeamXMLConstants.ATTR_PATH, path);
			LoadedDeclarations ds = sourcePaths2.get(path);
			Element pathElement = XMLUtilities.createElement(sourcePathsElement, "path"); //$NON-NLS-1$
			pathElement.setAttribute("value", path.toString()); //$NON-NLS-1$
			List<ISeamComponentDeclaration> cs = ds.getComponents();
			if(cs != null && !cs.isEmpty()) {
				Element cse = XMLUtilities.createElement(pathElement, "components"); //$NON-NLS-1$
				for (ISeamComponentDeclaration d: cs) {
					SeamObject o = (SeamObject)d;
					o.toXML(cse, context);
				}
			}
			List<ISeamFactory> fs = ds.getFactories();
			if(fs != null && !fs.isEmpty()) {
				Element cse = XMLUtilities.createElement(pathElement, "factories"); //$NON-NLS-1$
				for (ISeamFactory d: fs) {
					SeamObject o = (SeamObject)d;
					o.toXML(cse, context);
				}
			}
			List<String> imports = ds.getImports();
			if(imports != null && !imports.isEmpty()) {
				Element cse = XMLUtilities.createElement(pathElement, "imports"); //$NON-NLS-1$
				for (String d: imports) {
					Element e = XMLUtilities.createElement(cse, SeamXMLConstants.TAG_IMPORT); //$NON-NLS-1$
					e.setAttribute(SeamXMLConstants.ATTR_VALUE, d);
				}
			}
			
		}
	}
	
	/*
	 * 
	 */
	private void storeProjectDependencies(Element root) {
		Element dependsOnElement = XMLUtilities.createElement(root, "depends-on-projects"); //$NON-NLS-1$
		for (ISeamProject p : dependsOn) {
			if(!p.getProject().isAccessible()) continue;
			Element pathElement = XMLUtilities.createElement(dependsOnElement, "project"); //$NON-NLS-1$
			pathElement.setAttribute("name", p.getProject().getName()); //$NON-NLS-1$
		}
		Element usedElement = XMLUtilities.createElement(root, "used-by-projects"); //$NON-NLS-1$
		for (ISeamProject p : usedBy) {
			if(!p.getProject().isAccessible()) continue;
			Element pathElement = XMLUtilities.createElement(usedElement, "project"); //$NON-NLS-1$
			pathElement.setAttribute("name", p.getProject().getName()); //$NON-NLS-1$
		}
	}
	
	/*
	 * obsolete, will work only for old projects
	 */
	private void loadSourcePaths(Element root) {
		Element sourcePathsElement = XMLUtilities.getUniqueChild(root, "source-paths"); //$NON-NLS-1$
		if(sourcePathsElement == null) return;
		Element[] paths = XMLUtilities.getChildren(sourcePathsElement, "path"); //$NON-NLS-1$
		if(paths != null) for (int i = 0; i < paths.length; i++) {
			String p = paths[i].getAttribute("value"); //$NON-NLS-1$
			if(p == null || p.trim().length() == 0) continue;
			IPath path = new Path(p.trim());
			if(sourcePaths.contains(path)) continue;
			IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			if(f == null || !f.exists() || !f.isSynchronized(IResource.DEPTH_ZERO)) continue;
			SeamResourceVisitor b = new SeamResourceVisitor(this);
			b.visit(f);
		}
	}

	private void loadNamespaces(Element root) {
		Element namespacesElement = XMLUtilities.getUniqueChild(root, "namespaces"); //$NON-NLS-1$
		if(namespacesElement == null) return;
		Element[] ns = XMLUtilities.getChildren(namespacesElement, "namespace"); //$NON-NLS-1$
		for (int i = 0; i < ns.length; i++) {
			SeamNamespace sn = new SeamNamespace();
			sn.loadXML(ns[i]);
			namespaces.addNamespace(sn);
		}
	}

	private void loadSourcePaths2(Element root) {
		Properties context = new Properties();
		context.put("seamProject", this);
		Element sourcePathsElement = XMLUtilities.getUniqueChild(root, "paths"); //$NON-NLS-1$
		if(sourcePathsElement == null) return;
		Element[] paths = XMLUtilities.getChildren(sourcePathsElement, "path"); //$NON-NLS-1$
		if(paths != null) for (int i = 0; i < paths.length; i++) {
			String p = paths[i].getAttribute("value"); //$NON-NLS-1$
			if(p == null || p.trim().length() == 0) continue;
			IPath path = new Path(p.trim());
			if(sourcePaths2.containsKey(path)) continue;

			if(!getClassPath().hasPath(path)) {
				IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				if(f == null || !f.exists() || !f.isSynchronized(IResource.DEPTH_ZERO)) continue;
			}

			context.put(SeamXMLConstants.ATTR_PATH, path);
			
			long t1 = System.currentTimeMillis();
			LoadedDeclarations ds = new LoadedDeclarations();
			Element components = XMLUtilities.getUniqueChild(paths[i], "components");
			if(components != null) {
				Element[] cs = XMLUtilities.getChildren(components, SeamXMLConstants.TAG_COMPONENT);
				for (int j = 0; j < cs.length; j++) {
					String cls = cs[j].getAttribute(SeamXMLConstants.ATTR_CLASS);
					SeamComponentDeclaration d = null;
					if(SeamXMLConstants.CLS_JAVA.equals(cls)) {
						d = new SeamJavaComponentDeclaration();
					} else if(SeamXMLConstants.CLS_XML.equals(cls)) {
						d = new SeamXmlComponentDeclaration();
					} else if(SeamXMLConstants.CLS_PROPERTIES.equals(cls)) {
						d = new SeamPropertiesDeclaration();
					}
					if(d == null) continue;
					d.loadXML(cs[j], context);
					ds.getComponents().add(d);
				}
			}
			Element factories = XMLUtilities.getUniqueChild(paths[i], "factories");
			if(factories != null) {
				Element[] cs = XMLUtilities.getChildren(factories, SeamXMLConstants.TAG_FACTORY);
				for (int j = 0; j < cs.length; j++) {
					String cls = cs[j].getAttribute(SeamXMLConstants.ATTR_CLASS);
					AbstractContextVariable d = null;
					if(SeamXMLConstants.CLS_XML.equals(cls)) {
						d = new SeamXmlFactory();
					} else if(SeamXMLConstants.CLS_JAVA.equals(cls)) {
						d = new SeamAnnotatedFactory();
					} else if(SeamXMLConstants.CLS_MESSAGES.equals(cls)) {
						d = new SeamMessages();
					}
					if(d == null) continue;
					d.loadXML(cs[j], context);
					ds.getFactories().add((ISeamFactory)d);
				}
			}
			Element imports = XMLUtilities.getUniqueChild(paths[i], "imports");
			if(imports != null) {
				Element[] cs = XMLUtilities.getChildren(imports, SeamXMLConstants.TAG_IMPORT);
				for (int j = 0; j < cs.length; j++) {
					String v = cs[j].getAttribute(SeamXMLConstants.ATTR_VALUE);
					if(v != null && v.length() > 0) {
						ds.getImports().add(v);
					}
				}
				
			}
			getClassPath().pathLoaded(path);

			Set<ISeamNamespace> ns = namespaces.getNamespacesBySource(path);
			if(ns != null) {
				//we need that, or registering will remove them
				ds.getNamespaces().addAll(ns);
			}

			registerComponents(ds, path);
			long t2 = System.currentTimeMillis();
			if(statistics != null) {
				statistics.add(new Long(t2 - t1));
				if(t2 - t1 > 30) {
					System.out.println("--->" + statistics.size() + " " + (t2 - t1));
					System.out.println("stop");
				}
			}
		}
		postBuild();
	}
	
	/*
	 * 
	 */
	private void loadProjectDependencies(Element root) {
		Element dependsOnElement = XMLUtilities.getUniqueChild(root, "depends-on-projects"); //$NON-NLS-1$
		if(dependsOnElement != null) {
			Element[] paths = XMLUtilities.getChildren(dependsOnElement, "project"); //$NON-NLS-1$
			for (int i = 0; i < paths.length; i++) {
				String p = paths[i].getAttribute("name"); //$NON-NLS-1$
				if(p == null || p.trim().length() == 0) continue;
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(p);
				if(project == null || !project.isAccessible()) continue;
				SeamProject sp = (SeamProject)SeamCorePlugin.getSeamProject(project, false);
				if(sp != null) {
					dependsOn.add(sp);
					sp.addDependentSeamProject(this);
				}
			}
		}

		Element usedElement = XMLUtilities.getUniqueChild(root, "used-by-projects"); //$NON-NLS-1$
		if(usedElement != null) {
			Element[] paths = XMLUtilities.getChildren(usedElement, "project"); //$NON-NLS-1$
			for (int i = 0; i < paths.length; i++) {
				String p = paths[i].getAttribute("name"); //$NON-NLS-1$
				if(p == null || p.trim().length() == 0) continue;
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(p);
				if(project == null || !project.isAccessible()) continue;
				SeamProject sp = (SeamProject)SeamCorePlugin.getSeamProject(project, false);
				if(sp != null) usedBy.add(sp);
			}
		}
	
	}
	
	/*
	 * 
	 */
	private File getStorageFile() {
		IPath path = SeamCorePlugin.getDefault().getStateLocation();
		File file = new File(path.toFile(), "projects/" + project.getName()); //$NON-NLS-1$
		return file;
	}
	
	public void clearStorage() {
		File f = getStorageFile();
		if(f != null && f.isFile()) f.delete();
	}

	public boolean hasNoStorage() {
		File f = getStorageFile();
		return f == null || !f.isFile();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamProject#getValidationContext()
	 */
	public ProjectValidationContext getValidationContext() {
		if(validationContext==null) {
			validationContext = new ProjectValidationContext();
		}
		return validationContext;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public ISeamComponent getComponentByName(String name) {
		return components.getByName(name);
	}

	/**
	 * 
	 */
	public ISeamComponent[] getComponents() {
		return components.getAllComponentsArray();
	}

	/**
	 * Package local method called by builder.
	 * @param seamComponent
	 * @param source
	 */	
	public void registerComponents(LoadedDeclarations ds, IPath source) {

		ISeamNamespace[] ns = ds.getNamespaces().toArray(new ISeamNamespace[0]);
		ISeamComponentDeclaration[] components = ds.getComponents().toArray(new ISeamComponentDeclaration[0]);
		ISeamFactory[] factories = ds.getFactories().toArray(new ISeamFactory[0]);
		
		if(ns.length == 0 && components.length == 0 && factories.length == 0 && ds.getImports().isEmpty()) {
			pathRemoved(source);
			if(EclipseResourceUtil.isJar(source.toString())) {
				if(!sourcePaths.contains(source)) sourcePaths.add(source);
				sourcePaths2.put(source, ds);
			}
			return;
		}
		if(!sourcePaths.contains(source)) sourcePaths.add(source);
		sourcePaths2.put(source, ds);

		if(ns.length > 0) {
			namespaces.addPath(source, ns);
		}
		
		if(!ds.getImports().isEmpty()) {
			setImports(source.toString(), ds.getImports());
		} else {
			removeImports(source.toString());
		}

		Map<Object,ISeamComponentDeclaration> currentComponents = findComponentDeclarations(source);

		Set<ISeamComponent> affectedComponents = new HashSet<ISeamComponent>();
		List<Change> addedComponents = null;
		for (int i = 0; i < components.length; i++) {
			SeamComponentDeclaration loaded = (SeamComponentDeclaration)components[i];
			adopt(loaded);
			SeamComponentDeclaration current = (SeamComponentDeclaration)currentComponents.remove(loaded.getId());

			loaded.setSourcePath(source);

			String name = getComponentName(loaded);
			String oldName = getComponentName(current);

			boolean nameChanged = current != null && !stringsEqual(name, oldName);
			
			SeamComponent c = getComponent(name);
			
			if(c != null) {
				affectedComponents.add(c);
			}

			String oldClassName = c == null ? null : c.getClassName();
			String loadedClassName = getClassName(loaded);

			Set<ISeamXmlComponentDeclaration> nameless = new HashSet<ISeamXmlComponentDeclaration>();

			if(current != null) {
				String currentClassName = getClassName(current);
				List<Change> changes = current.merge(loaded);
				if(isClassNameChanged(currentClassName, loadedClassName)) {
					this.components.onClassNameChanged(currentClassName, loadedClassName, current);
				}
				if(changes != null && !changes.isEmpty()) {
					Change cc = new Change(c, null, null, null);
					cc.addChildren(changes);
					List<Change> cchanges = Change.addChange(null, cc);
					fireChanges(cchanges);
					//TODO if java, fire to others
				}
				if(nameChanged) {
					Map<Object,ISeamComponentDeclaration> old = new HashMap<Object, ISeamComponentDeclaration>();
					old.put(current.getId(), current);
					SeamComponent oc = getComponent(oldName);
					if(oc != null) {
						ISeamXmlComponentDeclaration[] xds = oc.getXmlDeclarations().toArray(new ISeamXmlComponentDeclaration[0]);
						for (ISeamXmlComponentDeclaration x: xds) {
							String n = x.getName();
							if(loadedClassName.equals(x.getClassName()) && (n == null || n.length() == 0)) {
								old.put(((SeamXmlComponentDeclaration)x).getId(), x);
								nameless.add(x);
							}
						}
					}
					componentDeclarationsRemoved(old);
					loaded = current;
					current = null;
					c = getComponent(name);
					if(c != null) {
						affectedComponents.add(c);
					}
				} else {
					if(loaded instanceof ISeamXmlComponentDeclaration) {
						ISeamXmlComponentDeclaration xml = (ISeamXmlComponentDeclaration)loaded;
						onXMLLoadedDeclaration(c, oldClassName, xml);
					}
					continue;
				}
			}
			
			this.components.addDeclaration(loaded);

			if(c == null && name != null) {
				ScopeType scopeType = loaded.getScope();
				c = newComponent(name, scopeType);
				affectedComponents.add(c);
				this.components.addComponent(c);
				addVariable(c);
				c.addDeclaration(loaded);
				if(nameless != null && nameless.size() > 0) {
					for (ISeamComponentDeclaration d: nameless) c.addDeclaration(d);
				}
				addedComponents = Change.addChange(addedComponents, new Change(this, null, null, c));
			} else if(c != null) {
				c.addDeclaration(loaded);
				List<Change> changes = Change.addChange(null, new Change(c, null, null, loaded));
				fireChanges(changes);
			}
			
			if(loaded instanceof ISeamJavaComponentDeclaration) {
				SeamJavaComponentDeclaration jd = (SeamJavaComponentDeclaration)loaded;
				Set<ISeamComponent> cs = getComponentsByClass(jd.getClassName());
				for (ISeamComponent ci: cs) {
					if(ci == c) continue;
					SeamComponent cii = (SeamComponent)ci;
					cii.addDeclaration(loaded);
					List<Change> changes = Change.addChange(null, new Change(ci, null, null, loaded));
					fireChanges(changes);
					affectedComponents.add(cii);
				}
				SeamComponent empty = this.components.getByName("");
				if(empty != null && name != null && name.length() > 0) {
					ISeamXmlComponentDeclaration[] xds = empty.getXmlDeclarations().toArray(new ISeamXmlComponentDeclaration[0]);
					for (ISeamXmlComponentDeclaration x: xds) {
						if(jd.getClassName().equals(x.getClassName())) {
							empty.removeDeclaration(x);
							List<Change> changes = Change.addChange(null, new Change(empty, null, x, null));
							c.addDeclaration(x);
							changes = Change.addChange(changes, new Change(empty, null, null, x));
							fireChanges(changes);
						}
					}
				}
				if(oldClassName != null && isClassNameChanged(oldClassName, loadedClassName)) {
					nameless.clear();
					Map<Object,ISeamComponentDeclaration> old = new HashMap<Object, ISeamComponentDeclaration>();
					ISeamXmlComponentDeclaration[] xds = c.getXmlDeclarations().toArray(new ISeamXmlComponentDeclaration[0]);
					for (ISeamXmlComponentDeclaration x: xds) {
						String n = x.getName();
						if(oldClassName.equals(x.getClassName()) && (n == null || n.length() == 0)) {
							old.put(((SeamXmlComponentDeclaration)x).getId(), x);
							nameless.add(x);
						}
					}
					componentDeclarationsRemoved(old);
					if(nameless.size() > 0) {
						if(empty == null) {
							empty = newComponent("", nameless.iterator().next().getScope());
							affectedComponents.add(empty);
							this.components.addComponent(empty);
						}
						List<Change> changes = null;
						for (ISeamXmlComponentDeclaration d: nameless) {
							empty.addDeclaration(d);
							changes = Change.addChange(changes, new Change(empty, null, null, d));
						}
						fireChanges(changes);
					}
				}
			} else if(loaded instanceof ISeamXmlComponentDeclaration) {
				ISeamXmlComponentDeclaration xml = (ISeamXmlComponentDeclaration)loaded;
				onXMLLoadedDeclaration(c, oldClassName, xml);
			}
		}

		for (ISeamComponent c: affectedComponents) {
			SeamComponent sc = (SeamComponent)c;
			addedComponents = sc.revalidate(addedComponents);
		}
		
		fireChanges(addedComponents);
		
		componentDeclarationsRemoved(currentComponents);

//		revalidate();

		Map<Object, ISeamFactory> currentFactories = findFactoryDeclarations(source);
		List<Change> addedFactories = null;
		for (int i = 0; i < factories.length; i++) {
			AbstractContextVariable loaded = (AbstractContextVariable)factories[i];
			AbstractContextVariable current = (AbstractContextVariable)currentFactories.remove(loaded.getId());
			if(current != null && current.getClass() != loaded.getClass()) {
				this.factories.removeFactory((ISeamFactory)current);
				current = null;
			}
			if(current != null) {
				List<Change> changes = current.merge(loaded);
				fireChanges(changes);
				continue;
			}
			if(factories[i].getParent() == null) {
				adopt(factories[i]);
			}
			this.factories.addFactory(factories[i]);
			addedFactories = Change.addChange(addedFactories, new Change(this, null, null, loaded));
		}
		fireChanges(addedFactories); 
		
		factoryDeclarationsRemoved(currentFactories);
		
		variables.revalidate(source);
		
		try {
			registerComponentsInDependentProjects(ds, source);
		} catch (CloneNotSupportedException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
	}

	private String getComponentName(SeamComponentDeclaration d) {
		if(d == null) return null;
		String name = d.getName();

		if((name == null || name.length() == 0) && d instanceof SeamXmlComponentDeclaration) {
			String className = getClassName(d);
			if(className != null && className.length() > 0) {
				SeamJavaComponentDeclaration jd = components.getJavaDeclaration(className);
				if(jd != null) {
					name = jd.getName();
				}
			}
		}
		
		return name;
		
	}

	private static String getClassName(ISeamComponentDeclaration d) {
		if(d instanceof ISeamJavaComponentDeclaration) {
			return ((ISeamJavaComponentDeclaration)d).getClassName();
		} else if(d instanceof ISeamXmlComponentDeclaration) {
			return ((ISeamXmlComponentDeclaration)d).getClassName();
		}
		return null;
	}
	
	/**
	 * 
	 * @param ds
	 * @param source
	 * @throws CloneNotSupportedException
	 */
	public void registerComponentsInDependentProjects(LoadedDeclarations ds, IPath source) throws CloneNotSupportedException {
		if(usedBy.isEmpty()) return;
		if(EclipseResourceUtil.isJar(source.toString())) return;
		
		for (SeamProject p : usedBy) {
			p.resolve();
			LoadedDeclarations ds1 = new LoadedDeclarations();
			for (ISeamNamespace n: ds.getNamespaces()) {
				ds1.getNamespaces().add(n); //no need to clone, it is read-only.
			}
			for (ISeamComponentDeclaration d:  ds.getComponents()) {
				ds1.getComponents().add(d.clone());
			}
			for (ISeamFactory f : ds.getFactories()) {
				ds1.getFactories().add(f.clone());
			}
			ds1.getImports().addAll(ds.getImports());
			p.registerComponents(ds1, source);
		}
	}
	
	/**
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	boolean stringsEqual(String s1, String s2) {
		return s1 == null ? s2 == null : s1.equals(s2);
	}
	
	/*
	 * 
	 * @param oldClassName
	 * @param newClassName
	 * @return
	 */
	private boolean isClassNameChanged(String oldClassName, String newClassName) {
		if(oldClassName == null || oldClassName.length() == 0) return false;
		if(newClassName == null || newClassName.length() == 0) return false;
		return !oldClassName.equals(newClassName);
	}

	/*
	 * 
	 */
	private void onXMLLoadedDeclaration(SeamComponent c, String oldClassName, ISeamXmlComponentDeclaration xml) {
		String className = xml.getClassName();
		List<Change> changes = null;
		if(isClassNameChanged(oldClassName, className)) {
			ISeamComponentDeclaration[] ds1 = c.getAllDeclarations().toArray(new ISeamComponentDeclaration[0]);
			for (int i1 = 0; i1 < ds1.length; i1++) {
				if(!(ds1[i1] instanceof ISeamJavaComponentDeclaration)) continue;
				ISeamJavaComponentDeclaration jcd = (ISeamJavaComponentDeclaration)ds1[i1];
				if(jcd.getClassName().equals(className)) continue;
				// We cannot remove declaration if it declares the same component. 
				// This is an error to be reported by Seam Validator.
				if(c.getName().equals(jcd.getName())) continue;
				c.removeDeclaration(jcd);
				changes = Change.addChange(changes, new Change(c, null, jcd, null));
			}
		}
		SeamJavaComponentDeclaration j = components.getJavaDeclaration(className);
		if(j != null && !c.getAllDeclarations().contains(j)) {
			c.addDeclaration(j);
			changes = Change.addChange(changes, new Change(c, null, null, j));
		}
		if(changes != null) c.revalidate(changes); 
		fireChanges(changes);
	}

	public boolean isPathLoaded(IPath source) {
		return sourcePaths2.containsKey(source);
	}

	/**
	 * Package local method called by builder.
	 * @param source
	 */
	public void pathRemoved(IPath source) {
		if(!sourcePaths.contains(source) && !sourcePaths2.containsKey(source)) return;
		sourcePaths.remove(source);
		sourcePaths2.remove(source);
		
		namespaces.removePath(source);
		removeImports(source.toString());

		List<Change> changes = null;

		Set<SeamComponentDeclaration> ds = components.getDeclarationsBySource(source);
		if(ds != null) for (SeamComponentDeclaration d: ds) {
			Set<SeamComponent> cs = new HashSet<SeamComponent>();
			cs.addAll(d.getComponents());
			for (SeamComponent c: cs) {
				c.removeDeclaration(d);
				changes = Change.addChange(changes, new Change(c, null, d, null));
				if(isComponentEmpty(c)) {
					changes = removeEmptyComponent(c);
				}
			}
		}
		
		components.removePath(source);

		fireChanges(changes);

//		revalidate();

		changes = null;
		Set<ISeamFactory> fs = this.factories.removePath(source);
		if(fs != null) for (ISeamFactory f: fs) {
			changes = Change.addChange(changes, new Change(this, null, f, null));
		}
		fireChanges(changes);
		
		firePathRemovedToDependentProjects(source);
	}

	public void firePathRemovedToDependentProjects(IPath source) {
		if(usedBy.isEmpty()) return;
		if(EclipseResourceUtil.isJar(source.toString())) return;
		
		for (SeamProject p : usedBy) {
			p.resolve();
			p.pathRemoved(source);
		}
	}

	/**
	 * 
	 * @param source
	 * @return
	 */
	public Map<Object,ISeamComponentDeclaration> findComponentDeclarations(IPath source) {
		Map<Object,ISeamComponentDeclaration> map = new HashMap<Object, ISeamComponentDeclaration>();
		Set<SeamComponentDeclaration> ds = components.getDeclarationsBySource(source);
		if(ds != null) for (ISeamComponentDeclaration d: ds) {
			SeamComponentDeclaration di = (SeamComponentDeclaration)d;
			map.put(di.getId(), di);
		}
		return map;
	}
	
	/**
	 * 
	 * @param removed
	 */
	void componentDeclarationsRemoved(Map<Object,ISeamComponentDeclaration> removed) {
		if(removed == null || removed.isEmpty()) return;
		List<Change> changes = null;
		for (ISeamComponentDeclaration declaration: removed.values()) {
			SeamComponentDeclaration d = (SeamComponentDeclaration)declaration;
			components.removeDeclaration(d);
			Set<SeamComponent> sc = new HashSet<SeamComponent>();
			sc.addAll(d.getComponents());
			for (SeamComponent c: sc) {
				c.removeDeclaration(d);
				changes = Change.addChange(changes, new Change(c, null, d, null));
				Set<ISeamComponentDeclaration> ds = c.getAllDeclarations();
				if(ds.size() == 1) {
					ISeamComponentDeclaration d1 = ds.iterator().next();
					if(d1 instanceof ISeamJavaComponentDeclaration 
						&& !c.getName().equals(d1.getName())) {
						c.removeDeclaration(d1);
						changes = Change.addChange(changes, new Change(c, null, d1, null));
					}
				}
				if(isComponentEmpty(c)) {
					changes = removeEmptyComponent(c);
				}
				
			}
		}
		fireChanges(changes);
	}
	
	/*
	 * 
	 */
	private List<Change> removeEmptyComponent(SeamComponent c) {
		components.removeComponent(c);
		List<Change> changes = c.removeFromModel(null);
		removeVariable(c);
		changes = Change.addChange(changes, new Change(this, null, c, null));
		return changes;
	}
	
	/*
	 * 
	 */
	private boolean isComponentEmpty(SeamComponent c) {
		if(c.getAllDeclarations().isEmpty()) return true;
		for (ISeamComponentDeclaration d: c.getAllDeclarations()) {
			if(c.getName().equals(d.getName())) return false;
		}
		return true;
	}

	/**
	 * 
	 * @param source
	 * @return
	 */
	public Map<Object,ISeamFactory> findFactoryDeclarations(IPath source) {
		Map<Object,ISeamFactory> map = new HashMap<Object, ISeamFactory>();
		Set<ISeamFactory> fs = factories.getFactoriesBySource(source);
		if(fs != null) for (ISeamFactory c: fs) {
			AbstractContextVariable ci = (AbstractContextVariable)c;
			map.put(ci.getId(), c);
		}		
		return map;
	}
	
	/**
	 * 
	 * @param removed
	 */
	void factoryDeclarationsRemoved(Map<Object,ISeamFactory> removed) {
		if(removed == null || removed.isEmpty()) return;
		Iterator<ISeamFactory> iterator = removed.values().iterator();
		List<Change> changes = null;
		while(iterator.hasNext()) {
			ISeamFactory c = iterator.next();
			factories.removeFactory(c);
			removeVariable(c);
			changes = Change.addChange(changes, new Change(this, null, c, null));
		}
		fireChanges(changes);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getComponentsByClass(java.lang.String)
	 */
	public Set<ISeamComponent> getComponentsByClass(String className) {
		Set<ISeamComponent> result = new HashSet<ISeamComponent>();
		Set<SeamComponentDeclaration> ds = components.getDeclarationsByClasName(className);
		if(ds == null) return result;
		for (SeamComponentDeclaration d: ds) {
			for (ISeamComponent c: d.getComponents()) {
				if(result.contains(c)) continue;
				if(c.getSeamProject() != this) continue;
				if(className.equals(c.getClassName())) result.add(c);
			}
		}
		return result;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getComponentsByScope(org.jboss.tools.seam.core.ScopeType)
	 */
	public Set<ISeamComponent> getComponentsByScope(ScopeType type) {
		return getComponentsByScope(type, false);
	}

	/**
	 * 
	 */
	public Set<ISeamComponent> getComponentsByScope(ScopeType type, boolean addVisibleScopes) {
		Set<ISeamComponent> result = new HashSet<ISeamComponent>();
		//TODO map needed
		for(SeamComponent component: components.allComponents.values()) {
			if(isVisibleInScope(component, type, addVisibleScopes)) {
				result.add(component);
			}
		}		
		return result;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#addComponent(org.jboss.tools.seam.core.ISeamComponent)
	 */
	public void addComponent(ISeamComponent component) {
		components.addComponent(component);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#removeComponent(org.jboss.tools.seam.core.ISeamComponent)
	 */
	public void removeComponent(ISeamComponent component) {
		components.removeComponent(component);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getVariables()
	 */
	public Set<ISeamContextVariable> getVariables() {
		return variables.getVariablesCopy();
	}
	
	public void addVariable(ISeamContextVariable v) {
		variables.add(v);
	}

	public void removeVariable(ISeamContextVariable v) {
		variables.remove(v);
	}
	
	public void setImports(String source, List<String> paths) {
		if(equalLists(imports.get(source), paths)) return;
		synchronized(variables) {
			variables.allVariablesPlusShort = null;
			variables.byName = null;
		}
		imports.put(source, paths);
	}
	
	private boolean equalLists(List<String> s1, List<String> s2) {
		if(s1 == null || s2 == null) return s1 == s2;
		if(s1.size() != s2.size()) return false;
		for (int i = 0; i < s1.size(); i++) {
			if(!s1.get(i).equals(s2.get(i))) return false;
		}
		return true;
	}

	public void removeImports(String source) {
		if(!imports.containsKey(source)) return;
		synchronized(variables) {
			variables.allVariablesPlusShort = null;
		}
		imports.remove(source);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getVariables()
	 */
	public Set<ISeamContextVariable> getVariables(boolean includeShortNames) {
		if(!includeShortNames) {
			return variables.getVariablesCopy();
		} else {
			return variables.getVariablesPlusShort();
		}
	}
	
	public boolean isImportedPackage(String packageName) {
		for (String s: imports.keySet()) {
			List<String> list = imports.get(s);
			if(list.contains(packageName)) return true;
		}
		return false;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getVariablesByName(java.lang.String)
	 */
	public Set<ISeamContextVariable> getVariablesByName(String name) {
		return variables.getByName(name);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getVariablesByScope(org.jboss.tools.seam.core.ScopeType)
	 */
	public Set<ISeamContextVariable> getVariablesByScope(ScopeType scope) {
		return getVariablesByScope(scope, false);
	}

	/**
	 * 
	 */
	public Set<ISeamContextVariable> getVariablesByScope(ScopeType scope, boolean addVisibleScopes) {
		Set<ISeamContextVariable> result = new HashSet<ISeamContextVariable>();
		for (ISeamContextVariable v: getVariables(true)) {
			if(isVisibleInScope(v, scope, addVisibleScopes)) {
				result.add(v);
			}
		}
		return result;
	}

	public Set<IBijectedAttribute> getBijectedAttributes() {
		Set<IBijectedAttribute> result = new HashSet<IBijectedAttribute>();
		ISeamJavaComponentDeclaration[] ds = components.getJavaDeclarationsArray();
		for (ISeamJavaComponentDeclaration d: ds) {
			Set<IBijectedAttribute> as = d.getBijectedAttributes();
			if(as != null) result.addAll(as);
		}
		return result;
	}

	public Set<IBijectedAttribute> getBijectedAttributesByType(BijectedAttributeType type) {
		Set<IBijectedAttribute> result = new HashSet<IBijectedAttribute>();
		ISeamJavaComponentDeclaration[] ds = components.getJavaDeclarationsArray();
		for (ISeamJavaComponentDeclaration d: ds) {
			Set<IBijectedAttribute> as = d.getBijectedAttributesByType(type);
			if(as != null) result.addAll(as);
		}
		return result;
	}

	public Set<IBijectedAttribute> getBijectedAttributesByName(String name, BijectedAttributeType type) {
		Set<IBijectedAttribute> result = new HashSet<IBijectedAttribute>();
		ISeamJavaComponentDeclaration[] ds = components.getJavaDeclarationsArray();
		for (ISeamJavaComponentDeclaration d: ds) {
			Set<IBijectedAttribute> as = d.getBijectedAttributes();
			for (IBijectedAttribute a: as) {
				if(name != null && !name.equals(a.getName())) continue;
				if(type != null && !a.isOfType(type)) continue;
				result.add(a);
			}
		}
		return result;
	}

	/**
	 * 
	 * @param v
	 * @param scope
	 * @param addVisibleScopes
	 * @return
	 */
	private boolean isVisibleInScope(ISeamContextVariable v, ScopeType scope, boolean addVisibleScopes) {
		if(scope == v.getScope()) {
			return true;
		} else if(addVisibleScopes && scope != null && v.getScope() != null) {
			if(v.getScope().getPriority() >= scope.getPriority()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 */
	public void addFactory(ISeamFactory factory) {
		factories.addFactory(factory);
	}

	/**
	 * 
	 */
	public ISeamFactory[] getFactories() {
		return factories.getAllFactoriesArray();
	}

	/**
	 * 
	 */
	public Set<ISeamFactory> getFactories(String name, ScopeType scope) {
		Set<ISeamFactory> result = new HashSet<ISeamFactory>();
		for (ISeamFactory f: getFactories()) {
			if(name.equals(f.getName()) && scope.equals(f.getScope())) result.add(f);
		}
		return result;
	}

	/**
	 * 
	 */
	public Set<ISeamFactory> getFactoriesByName(String name) {
		Set<ISeamFactory> result = new HashSet<ISeamFactory>();
		for (ISeamFactory f: getFactories()) {
			if(name.equals(f.getName())) result.add(f);
		}
		return result;
	}

	/**
	 * 
	 */
	public Set<ISeamFactory> getFactoriesByScope(ScopeType scope) {
		return getFactoriesByScope(scope, false);
	}

	/**
	 * 
	 */
	public Set<ISeamFactory> getFactoriesByScope(ScopeType scope, boolean addVisibleScopes) {
		Set<ISeamFactory> result = new HashSet<ISeamFactory>();
		for (ISeamFactory f: getFactories()) {
			if(isVisibleInScope(f, scope, addVisibleScopes)) {
				result.add(f);
			}
		}
		return result;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getFactoriesByPath(org.eclipse.core.runtime.IPath)
	 */
	public Set<ISeamFactory> getFactoriesByPath(IPath path) {
		Set<ISeamFactory> result = new HashSet<ISeamFactory>();
		Set<ISeamFactory> fs = factories.getFactoriesBySource(path);
		if(fs != null) result.addAll(fs);
		return result;
	}

	/**
	 * 
	 */
	public void removeFactory(ISeamFactory factory) {
		factories.removeFactory(factory);
		removeVariable(factory);
	}
	
	/**
	 * Returns map of all java classes that contain seam annotations.
	 * They may be seam components, as well as cases of incomplete annotating
	 * that does not create a seam component.
	 * @return
	 */
	public ISeamJavaComponentDeclaration[] getAllJavaComponentDeclarations() {
		return components.getJavaDeclarationsArray();
	}

	public ISeamJavaComponentDeclaration getJavaComponentDeclaration(String className) {
		return components.getJavaDeclaration(className);
	}

	/**
	 * Returns set of java classes that contain seam annotations with specified path.
	 * They may be seam components, as well as cases of incomplete annotating
	 * that does not create a seam component.
	 * @return
	 */
	public Set<ISeamJavaComponentDeclaration> findJavaDeclarations(IPath source) {
		//TODO map needed
		Set<ISeamJavaComponentDeclaration> set = new HashSet<ISeamJavaComponentDeclaration>();
		for (ISeamJavaComponentDeclaration d: components.getJavaDeclarationsArray()) {
			if(source.equals(d.getSourcePath())) set.add(d);
		}		
		return set;
	}

	/**
	 * 
	 */
	public SeamComponent getComponent(String name) {
		return components.getByName(name);
	}
	
	public static String MESSAGES_COMPONENT_NAME = "org.jboss.seam.core.messages";
	/**
	 * 
	 * @param name
	 * @param scopeType
	 * @return
	 */
	SeamComponent newComponent(String name, ScopeType scopeType) {
		SeamComponent c = 
			MESSAGES_COMPONENT_NAME.equals(name) ? new SeamMessagesComponent()
			: new SeamComponent();
		c.setName(name);
		c.setId(name);
		c.setParent(getScope(scopeType));
		((SeamScope)getScope(scopeType)).addComponent(c);
		return c;
	}
	
	List<Change> postponedChanges = null;
	
	public void postponeFiring() {
		if(postponedChanges == null) {
			postponedChanges = new ArrayList<Change>();
		}
	}
	
	public void fireChanges() {
		if(postponedChanges == null) return;
		List<Change> changes = postponedChanges;
		postponedChanges = null;
		fireChanges(changes);
	}
	
	/**
	 * 
	 * @param changes
	 */
	void fireChanges(List<Change> changes) {
		if(changes == null || changes.isEmpty()) return;
		if(postponedChanges != null) {
			postponedChanges.addAll(changes);
			return;
		}
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
		SeamCorePlugin.fire(event);
	}

	/**
	 * 
	 */
	public synchronized void addSeamProjectListener(ISeamProjectChangeListener listener) {
		if(listeners.contains(listener)) return;
		listeners.add(listener);
	}

	/**
	 * 
	 */
	public synchronized void removeSeamProjectListener(ISeamProjectChangeListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getComponentsByResource(org.eclipse.core.resources.IResource)
	 */
	public Set<ISeamComponent> getComponentsByPath(IPath path) {
		Set<ISeamComponent> result = new HashSet<ISeamComponent>();
		Set<SeamComponentDeclaration> ds = components.getDeclarationsBySource(path);
		if(ds != null) for (SeamComponentDeclaration d: ds) {
			Set<SeamComponent> cs = d.getComponents();
			for (SeamComponent c: cs) {
				if(c.getSeamProject() == this) result.add(c);
			}
		}
		return result;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamProject#getVariablesByPath(org.eclipse.core.runtime.IPath)
	 */
	public Set<ISeamContextVariable> getVariablesByPath(IPath path) {
		return variables.getByPath(path);
	}

	/**
	 * 
	 */
	void revalidate() {
//		if(revalidateLock > 0) return;
//		revalidateScopes();
//		revalidatePackages();
	}
	
	/**
	 * 
	 */
//	void revalidateScopes() {
//		List<Change> changes = null;
//		for(SeamComponent c : allComponents.values()) {
//			SeamScope pc = (SeamScope)c.getParent();
//			SeamScope pn = getScope(c.getScope());
//			if(pc == pn) continue;
//			c.setParent(pn);
//			if(pc != null) {
//				pc.removeComponent(c);
//				changes = Change.addChange(changes, new Change(pc, null, c, null));
//			}
//			pn.addComponent(c);
//			changes = Change.addChange(changes, new Change(pn, null, null, c));
//		}
//		for (int i = 0; i < scopes.length; i++) {
//			scopes[i].revalidatePackages();
//		}
//		fireChanges(changes);
//	}
	
	/**
	 * 
	 */
//	void revalidatePackages() {
//		List<Change> changes = SeamPackageUtil.revalidatePackages(this, allComponents, getComponents(), packages);
//		fireChanges(changes);
//	}
	
	/**
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	Map<IPath, LoadedDeclarations> getAllDeclarations() throws CloneNotSupportedException {
		Map<IPath, LoadedDeclarations> map = new HashMap<IPath, LoadedDeclarations>();
		for (ISeamComponent c : getComponents()) {
			for (ISeamComponentDeclaration d : c.getAllDeclarations()) {
				IPath p = d.getSourcePath();
				if(p == null || EclipseResourceUtil.isJar(p.toString())) continue;
				LoadedDeclarations ds = map.get(p);
				if(ds == null) {
					ds = new LoadedDeclarations();
					map.put(p, ds);
				}
				ds.getComponents().add(d.clone());
			}
		}
		for (String uri : namespaces.namespacesByURI.keySet()) {
			Set<ISeamNamespace> s = namespaces.namespacesByURI.get(uri);
			for (ISeamNamespace n : s) {
				IPath p = n.getSourcePath();
				if(p == null || EclipseResourceUtil.isJar(p.toString())) continue;
				LoadedDeclarations ds = map.get(p);
				if(ds == null) {
					ds = new LoadedDeclarations();
					map.put(p, ds);
				}
				ds.getNamespaces().add(n);
			}
		}
		for (ISeamFactory f : getFactories()) {
			IPath p = f.getSourcePath();
			if(p == null || EclipseResourceUtil.isJar(p.toString())) continue;
			LoadedDeclarations ds = map.get(p);
			if(ds == null) {
				ds = new LoadedDeclarations();
				map.put(p, ds);
			}
			ds.getFactories().add(f.clone());
		}
		for (String s: imports.keySet()) {
			IPath p = new Path(s);
			if(p == null || EclipseResourceUtil.isJar(p.toString())) continue;
			LoadedDeclarations ds = map.get(p);
			if(ds == null) {
				ds = new LoadedDeclarations();
				map.put(p, ds);
			}
			ds.getImports().addAll(imports.get(s));
		}
		return map;
	}
	
	public void postBuild() {
		if(factories.messages != null) {
			factories.messages.revalidate();
		}
		ISeamComponent m = getComponent(MESSAGES_COMPONENT_NAME);
		if(m instanceof SeamMessagesComponent) {
			((SeamMessagesComponent)m).revalidate();
		}
	}
	
	/**
	 * 
	 * @param builderID
	 * @throws CoreException
	 */
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

	/**
	 * 
	 */
	static String EXTERNAL_TOOL_BUILDER = "org.eclipse.ui.externaltools.ExternalToolBuilder"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	static final String LAUNCH_CONFIG_HANDLE = "LaunchConfigHandle"; //$NON-NLS-1$

	/**
	 * 
	 * @param builderID
	 * @throws CoreException
	 */
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

	public void validatePackage(SeamComponent c) {
		SeamPackage p = (SeamPackage)SeamPackageUtil.findOrCreatePackage(this, packages, SeamPackageUtil.getPackageName(c));
		c.setProjectPackage(p);
		p.getComponents().add(c);
	}

	class NamespaceStorage {
		Map<IPath, Set<ISeamNamespace>> namespacesBySource = new HashMap<IPath, Set<ISeamNamespace>>();
		Map<String, Set<ISeamNamespace>> namespacesByURI = new HashMap<String, Set<ISeamNamespace>>();
		
		public void clear() {
			namespacesBySource.clear();
			namespacesByURI.clear();
		}

		public Set<ISeamNamespace> getNamespacesBySource(IPath path) {
			return namespacesBySource.get(path);
		}

		public void addPath(IPath source, ISeamNamespace[] ns) {
			Set<ISeamNamespace> sd = namespacesBySource.get(source);
			if(sd == null && ns.length == 0) return;
			if(ns.length == 0) {
				removePath(source);
			} else {
				//TODO replace this with merge!
				removePath(source);
				for (int i = 0; i < ns.length; i++) {
					addNamespace(ns[i]);
				}
			}
		}

		public void addNamespace(ISeamNamespace n) {
			String uri = n.getURI();
			if(uri != null) {
				Set<ISeamNamespace> s = namespacesByURI.get(uri);
				if(s == null) {
					s = new HashSet<ISeamNamespace>();
					namespacesByURI.put(n.getURI(), s);
				}
				s.add(n);
			}
			IPath path = n.getSourcePath();
			if(path != null) {
				Set<ISeamNamespace> fs = namespacesBySource.get(path);
				if(fs == null) {
					fs = new HashSet<ISeamNamespace>();
					namespacesBySource.put(path, fs);
				}
				fs.add(n);
			}
		}

		public void removePath(IPath path) {
			Set<ISeamNamespace> sd = namespacesBySource.get(path);
			if(sd == null) return;
			for (ISeamNamespace d: sd) {
				if(d.getURI() != null)
					namespacesByURI.remove(d.getURI());
			}
			namespacesBySource.remove(path);
		}

		public void removeNamespace(ISeamNamespace n) {
			namespacesByURI.remove(n.getURI());
			IPath path = n.getSourcePath();
			if(path != null) {
				Set<ISeamNamespace> fs = namespacesBySource.get(path);
				if(fs != null) {
					fs.remove(n);
				}
				if(fs.isEmpty()) {
					namespacesBySource.remove(fs);
				}
			}
		}

	}

	class ComponentStorage {
		private Set<ISeamComponent> allComponentsSet = new HashSet<ISeamComponent>();
		Map<String, SeamComponent> allComponents = new HashMap<String, SeamComponent>();
		Map<String, SeamJavaComponentDeclaration> javaDeclarations = new HashMap<String, SeamJavaComponentDeclaration>();
		ISeamJavaComponentDeclaration[] javaDeclarationsArray = null;
		
		
		Map<IPath, Set<SeamComponentDeclaration>> declarationsBySource = new HashMap<IPath, Set<SeamComponentDeclaration>>();
		Map<String, Set<SeamComponentDeclaration>> declarationsByClassName = new HashMap<String, Set<SeamComponentDeclaration>>();
		
		public void clear() {
			synchronized(allComponentsSet) {
				allComponentsSet.clear();
			}
			allComponents.clear();
			synchronized(javaDeclarations) {
				javaDeclarations.clear();
				javaDeclarationsArray = null;
			}
			declarationsBySource.clear();
			declarationsByClassName.clear();
		}

		public ISeamComponent[] getAllComponentsArray() {
			synchronized(allComponentsSet) {
				return components.allComponentsSet.toArray(new ISeamComponent[0]);
			}
		}

		public ISeamJavaComponentDeclaration[] getJavaDeclarationsArray() {
			ISeamJavaComponentDeclaration[] result = javaDeclarationsArray;
			if(result == null) {
				synchronized(javaDeclarations) {
					javaDeclarationsArray = new ISeamJavaComponentDeclaration[javaDeclarations.values().size()];
					result = javaDeclarationsArray;
					int i = 0;
					for (ISeamJavaComponentDeclaration d: javaDeclarations.values()) {
						javaDeclarationsArray[i++] = d;
					}
				}
			}
			return result;
		}
		public SeamComponent getByName(String name) {
			if(name == null) return null;
			synchronized(allComponentsSet) {
				return allComponents.get(name);
			}
		}
		
		public void removeComponent(ISeamComponent c) {
			synchronized(allComponentsSet) {
				allComponentsSet.remove(c);
			}
			allComponents.remove(c.getName());
		}

		public void addComponent(ISeamComponent c) {
			synchronized(allComponentsSet) {
				allComponentsSet.add(c);
			}
			allComponents.put(c.getName(), (SeamComponent)c);
		}
		
		public void addDeclaration(SeamComponentDeclaration d) {
			IPath path = d.getSourcePath();
			Set<SeamComponentDeclaration> sc = declarationsBySource.get(path);
			if(sc == null) {
				sc = new HashSet<SeamComponentDeclaration>();
				declarationsBySource.put(path, sc);
			}
			sc.add(d);
			if(d instanceof ISeamJavaComponentDeclaration) {
				SeamJavaComponentDeclaration jd = (SeamJavaComponentDeclaration)d;
				for (ISeamContextVariable v: jd.getDeclaredVariables()) addVariable(v);
				synchronized(javaDeclarations) {
					javaDeclarations.put(jd.getClassName(), jd);
					javaDeclarationsArray = null;
				}
				addDeclaration(jd.getClassName(), jd);
			} else if(d instanceof ISeamXmlComponentDeclaration) {
				ISeamXmlComponentDeclaration xd = (ISeamXmlComponentDeclaration)d;
				String className = xd.getClassName();
				if(className != null && className.length() > 0) {
					addDeclaration(className, d);
				}
			}
		}
		
		public void removeDeclaration(SeamComponentDeclaration d) {
			IPath path = d.getSourcePath();
			Set<SeamComponentDeclaration> sc = declarationsBySource.get(path);
			if(sc != null) {
				sc.remove(d);
				if(sc.isEmpty()) declarationsBySource.remove(path);
			}
			removeDeclarationWithClass(d);
		}
		
		public Set<SeamComponentDeclaration> getDeclarationsBySource(IPath path) {
			return declarationsBySource.get(path);
		}

		public SeamJavaComponentDeclaration getJavaDeclaration(String className) {
			synchronized(javaDeclarations) {
				return javaDeclarations.get(className);
			}
		}
		
		public Set<SeamComponentDeclaration> getDeclarationsByClasName(String className) {
			return declarationsByClassName.get(className);
		}
		
		public void removePath(IPath path) {
			Set<SeamComponentDeclaration> sd = declarationsBySource.get(path);
			if(sd == null) return;
			for (SeamComponentDeclaration d: sd) {
				removeDeclarationWithClass(d);
			}
			declarationsBySource.remove(path);
		}
		
		private void removeDeclarationWithClass(SeamComponentDeclaration d) {
			if(d instanceof ISeamJavaComponentDeclaration) {
				SeamJavaComponentDeclaration jd = (SeamJavaComponentDeclaration)d;
				synchronized(javaDeclarations) {
					javaDeclarations.remove(jd.getClassName());
					javaDeclarationsArray = null;
				}
				for (ISeamContextVariable v: jd.getDeclaredVariables()) removeVariable(v);
				removeDeclaration(jd.getClassName(), d);
			} else if(d instanceof ISeamXmlComponentDeclaration) {
				ISeamXmlComponentDeclaration xd = (ISeamXmlComponentDeclaration)d;
				String className = xd.getClassName();
				if(className != null && className.length() > 0) {
					removeDeclaration(className, d);
				}					
			}
		}
		
		public void onClassNameChanged(String oldClassName, String newClassName, SeamComponentDeclaration d) {
			if(oldClassName != null) {
				removeDeclaration(oldClassName, d);
			}
			if(newClassName != null) {
				addDeclaration(newClassName, d);
			}
		}

		private void addDeclaration(String className, SeamComponentDeclaration d) {
			Set<SeamComponentDeclaration> sc = declarationsByClassName.get(className);
			if(sc == null) {
				sc = new HashSet<SeamComponentDeclaration>();
				declarationsByClassName.put(className, sc);
			}
			sc.add(d);
		}

		private void removeDeclaration(String className, SeamComponentDeclaration d) {
			Set<SeamComponentDeclaration> sc = declarationsByClassName.get(className);
			if(sc != null) {
				sc.remove(d);
				if(sc.isEmpty()) {
					declarationsByClassName.remove(className);
				}
			}
		}

	}
	
	class FactoryStorage {
		private Set<ISeamFactory> allFactories = new HashSet<ISeamFactory>();
		private ISeamFactory[] allFactoriesArray = null;
		Map<IPath, Set<ISeamFactory>> factoriesBySource = new HashMap<IPath, Set<ISeamFactory>>();
		SeamMessages messages = null;

		public void clear() {
			synchronized(allFactories) {
				allFactories.clear();
				allFactoriesArray = null;
			}
			factoriesBySource.clear();
			messages = null;
		}

		public ISeamFactory[] getAllFactoriesArray() {
			ISeamFactory[] result = allFactoriesArray;
			if(result == null) {
				synchronized(allFactories) {
					allFactoriesArray = allFactories.toArray(new ISeamFactory[0]);
					result = allFactoriesArray;
				}
			}
			return result;
		}

		public Set<ISeamFactory> getFactoriesBySource(IPath path) {
			return factoriesBySource.get(path);
		}
		
		public void addFactory(ISeamFactory f) {
			synchronized(allFactories) {
				allFactories.add(f);
				allFactoriesArray = null;
			}
			IPath path = f.getSourcePath();
			if(path != null) {
				Set<ISeamFactory> fs = factoriesBySource.get(path);
				if(fs == null) {
					fs = new HashSet<ISeamFactory>();
					factoriesBySource.put(path, fs);
				}
				fs.add(f);
			}
			addVariable(f);
			if(f instanceof SeamMessages) {
				messages = (SeamMessages)f;
			}
		}
		
		public void removeFactory(ISeamFactory f) {
			synchronized(allFactories) {
				allFactories.remove(f);
				allFactoriesArray = null;
			}
			IPath path = f.getSourcePath();
			if(path != null) {
				Set<ISeamFactory> fs = factoriesBySource.get(path);
				if(fs != null) {
					fs.remove(f);
				}
				if(fs.isEmpty()) {
					factoriesBySource.remove(fs);
				}
			}
			removeVariable(f);
			if(f == messages) {
				messages = null;
			}
		}

		public Set<ISeamFactory> removePath(IPath path) {
			Set<ISeamFactory> fs = factoriesBySource.get(path);
			if(fs == null) return null;
			for (ISeamFactory f: fs) {
				synchronized(allFactories) {
					allFactories.remove(f);
					allFactoriesArray = null;
				}
				removeVariable(f);
				if(f == messages) messages = null;
			}
			factoriesBySource.remove(path);
			return fs;
		}
		
	}
	
	class VariablesStorage {
		Set<ISeamContextVariable> allVariables = new HashSet<ISeamContextVariable>();
		Set<ISeamContextVariable> allVariablesCopy = null;
		Set<ISeamContextVariable> allVariablesPlusShort = null;
		Map<IPath, Set<ISeamContextVariable>> byPath = null;
		Map<String, Set<ISeamContextVariable>> byName = null;

		public void clear() {
			allVariables.clear();
			clearCopies();
		}
		
		synchronized void clearCopies() {
			allVariablesCopy = null;
			allVariablesPlusShort = null;
			byName = null;
			byPath = null;
		}
		
		public Set<ISeamContextVariable> getVariablesCopy() {
			Set<ISeamContextVariable> vs = allVariablesCopy;
			if(vs == null) {
				synchronized(this) {
					allVariablesCopy = new HashSet<ISeamContextVariable>();
					allVariablesCopy.addAll(allVariables);
					vs = allVariablesCopy;
				}
			}
			return vs;
		}
		
		public Set<ISeamContextVariable> getVariablesPlusShort() {
			Set<ISeamContextVariable> vs = getVariablesCopy();
			Set<ISeamContextVariable> result = allVariablesPlusShort;
			if(result != null) return result;
			synchronized (this) {
				result = new HashSet<ISeamContextVariable>();
				result.addAll(vs);
				for (ISeamContextVariable v: vs) {
					String n = v.getName();
					int i = n.lastIndexOf('.');
					if(i < 0) continue;
					String packageName = n.substring(0, i);
					if(isImportedPackage(packageName)) {
						result.add(new SeamContextShortVariable(v));
					}
				}
				allVariablesPlusShort = result;
			}
			return result;
		}

		public synchronized void add(ISeamContextVariable v) {
			if(allVariables.contains(v)) return;
			clearCopies();
			allVariables.add(v);
		}

		public synchronized void remove(ISeamContextVariable v) {
			if(!allVariables.contains(v)) return;
			clearCopies();
			allVariables.remove(v);
		}
		
		public synchronized void revalidate(IPath path) {
			byPath = null;
			byName = null;
		}
		
		private void create() {
			byName = new HashMap<String, Set<ISeamContextVariable>>();
			byPath = new HashMap<IPath, Set<ISeamContextVariable>>();
			Set<ISeamContextVariable> q = getVariablesPlusShort();
			for (ISeamContextVariable v : q) {
				if(v instanceof ISeamComponent) {
					ISeamComponent c = (ISeamComponent)v;
					for (ISeamComponentDeclaration d: c.getAllDeclarations()) {
						SeamComponentDeclaration di = (SeamComponentDeclaration)d;
						addForPath(di.getSourcePath(), v);
					}
				} else {
					IResource variableResource = v.getResource();
					if(variableResource != null) {
						addForPath(variableResource.getFullPath(), v);
					}
				}
				
				String n = "" + v.getName();
				Set<ISeamContextVariable> s = byName.get(n);
				if(s == null) {
					s = new HashSet<ISeamContextVariable>();
					byName.put(n, s);
				}
				s.add(v);
			}
		}
		
		private void addForPath(IPath p, ISeamContextVariable v) {
			if(p == null) return;
			Set<ISeamContextVariable> s = byPath.get(p);
			if(s == null) {
				s = new HashSet<ISeamContextVariable>();
				byPath.put(p, s);
			}
			s.add(v);
		}
		
		public synchronized Set<ISeamContextVariable> getByName(String n) {
			if(byName == null) {
				create();
			}
			return byName.get(n);
		}

		public synchronized Set<ISeamContextVariable> getByPath(IPath p) {
			if(byPath == null) {
				create();
			}
			return byPath.get(p);
		}
	}

}
