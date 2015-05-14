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
package org.jboss.tools.cdi.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipant2Feature;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationHelper;
import org.jboss.tools.cdi.internal.core.impl.definition.DefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.definition.Dependencies;
import org.jboss.tools.cdi.internal.core.impl.definition.PackageDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.scanner.CDIBuilderDelegate;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.cdi.internal.core.scanner.lib.BeanArchiveDetector;
import org.jboss.tools.cdi.internal.core.scanner.lib.JarSet;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.UniquePaths;
import org.jboss.tools.common.web.WebUtils;

public class CDICoreBuilder extends IncrementalProjectBuilder {
	public static String BUILDER_ID = "org.jboss.tools.cdi.core.cdibuilder";
	
	public static final String PACKAGE_INFO = "package-info.java";

	static Set<ICDIBuilderDelegate> delegates = null;

	static Set<ICDIBuilderDelegate> getDelegates() {
		if(delegates == null) {
			delegates = new HashSet<ICDIBuilderDelegate>();
			//TODO populate; extension point will be used
			delegates.add(new CDIBuilderDelegate()); //default
		}
		return delegates;
	}

	ICDIBuilderDelegate builderDelegate;

	CDIResourceVisitor resourceVisitor = null;

	Set<IBuildParticipantFeature> buildParticipants = null;
	Set<IBuildParticipant2Feature> buildParticipants2 = null;

	/**
	 * Set only for instance created to initially load cdi model.
	 */
	CDICoreNature cdi;

	public CDICoreBuilder() {}

	public CDICoreBuilder(CDICoreNature cdi) throws CoreException {
		this.cdi = cdi;
		build(IncrementalProjectBuilder.FULL_BUILD, null, new NullProgressMonitor());
	}

	CDICoreNature getCDICoreNature() {
		if(cdi != null) {
			return cdi;
		}
		IProject p = getProject();
		if(p == null) return null;
		return CDICorePlugin.getCDI(p, false);
	}

	IProject getCurrentProject() {
		return cdi != null ? cdi.getProject() : getProject();
	}

	CDIResourceVisitor getResourceVisitor() {
		if(resourceVisitor == null) {
			resourceVisitor = new CDIResourceVisitor();
		}
		return resourceVisitor;
	}

	private void findDelegate() {
		Set<ICDIBuilderDelegate> ds = getDelegates();
		int relevance = 0;
		for (ICDIBuilderDelegate d: ds) {
			int r = d.computeRelevance(getCurrentProject());
			if(r > relevance) {
				builderDelegate = d;
				relevance = r;
			}
		}
	}

	public ICDIBuilderDelegate getDelegate() {
		return builderDelegate;
	}

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		
		resourceVisitor = null;
		findDelegate();
		if(getDelegate() == null) {
			return null;
		}
		CDICoreNature n = getCDICoreNature();
		if(n == null) {
			return null;
		}

		if(n.updateVersion() || n.hasNoStorage()) {
			kind = FULL_BUILD;
		}
		
		n.postponeFiring();

		long begin = System.currentTimeMillis();

		try {
			n.resolveStorage(kind != FULL_BUILD);

			if(!n.requestForBuild()) {
				return null;
			}
try {
			if(n.getDelegate() == null || n.getDelegate().getClass() != getDelegate().getProjectImplementationClass()) {
				if(n.getDelegate() != null) {
					n.clean();
					n.postponeFiring();
				}
				kind = FULL_BUILD;
				try {
					ICDIProject delegate = (ICDIProject)getDelegate().getProjectImplementationClass().newInstance();
					n.setCDIProject(delegate);
				} catch (IllegalAccessException e1) {
					CDICorePlugin.getDefault().logError(e1);
				} catch (InstantiationException e2) {
					CDICorePlugin.getDefault().logError(e2);
				}
			}

			IncrementCheck increment = null;
			IResourceDelta delta = null;
			if(kind != FULL_BUILD) {
				delta = getDelta(getCurrentProject());
				if(delta != null) {
					increment = new IncrementCheck(delta, n);
					if(!increment.isIncremental || increment.file == null) {
						increment = null;
					}
				}
			}

			if(increment == null) {
				n.cleanTypeFactory();
			}

			if(kind == FULL_BUILD) n.getClassPath().reset();
		
			//1. Check class path.
			boolean isClassPathUpdated = n.getClassPath().update();
	
			JarSet newJars = new JarSet();
			if(isClassPathUpdated || kind == FULL_BUILD) {
				//2. Update class path. Removed paths will be cached to be applied to working copy of context. 
				n.getClassPath().setSrcs(getResourceVisitor().srcs);
				newJars = n.getClassPath().process();
			}
			if(isClassPathUpdated || buildParticipants == null) {
				//3. Install extensions. That should be done before constructing working copy of context.
				buildParticipants = n.getExtensionManager().getBuildParticipantFeatures();
				buildParticipants2 = new HashSet<IBuildParticipant2Feature>();
				Set<IDefinitionContextExtension> es = new HashSet<IDefinitionContextExtension>();
				for (IBuildParticipantFeature p: buildParticipants) {
					IDefinitionContextExtension e = p.getContext();
					if(e != null) es.add(e);
					if(p instanceof IBuildParticipant2Feature) {
						buildParticipants2.add((IBuildParticipant2Feature)p);
					}
				}
				n.getDefinitions().setExtensions(es);
			}

			//4. Create working copy of context.
			n.getDefinitions().newWorkingCopy(kind == FULL_BUILD);

			//5. Modify working copy of context.
			//5.1 Apply Removed paths.
			if(isClassPathUpdated) {
				n.getClassPath().applyRemovedPaths();
			}
		
			for (IBuildParticipantFeature p: buildParticipants) p.beginVisiting();

			//5.2 Discover sources and build definitions.
			if(isClassPathUpdated || kind == FULL_BUILD) {
				buildJars(newJars);
				
				n.getClassPath().validateProjectDependencies();
				
				kind = FULL_BUILD;
			} else if(n.getClassPath().hasToUpdateProjectDependencies()) {
				n.getClassPath().validateProjectDependencies();
			}

			//5.2.a Update bean discovery mode.
			if(updateBeanDiscoveryMode()) {
				kind = FULL_BUILD;
				if(this.cdi == null) { //excluding initial model loading
					getCDICoreNature().getValidationContext().setFullValidationRequired(true);
				}
			}

			if (kind == FULL_BUILD || delta == null) {
				increment = null;
				fullBuild(monitor);
			} else if(increment != null) {
				incrementalBuild(increment, monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
			for (IBuildParticipantFeature p: buildParticipants) p.buildDefinitions();

			// 6. Save created definitions to project context and build beans.
			if(increment != null) {
				getCDICoreNature().getDefinitions().applyIncrementalWorkingCopy();
			} else {
				getCDICoreNature().getDefinitions().applyWorkingCopy();
			}
			
			long end = System.currentTimeMillis();
			n.fullBuildTime += end - begin;
			try {
				n.store();
			} catch (IOException e) {
				CDICorePlugin.getDefault().logError(e); //$NON-NLS-1$
			}

//			n.postBuild();
} finally {
	n.releaseBuild();
}
		} finally {
			n.fireChanges();
		}

		resourceVisitor = null;
		
		return null;
	}

	private boolean updateBeanDiscoveryMode() {
		int oldValue = getCDICoreNature().getBeanDiscoveryMode();
		int newValue = BeanArchiveDetector.ALL;
		if(getCDICoreNature().getVersion() != CDIVersion.CDI_1_0) {
			newValue = getBeanDiscoveryMode(getPrimaryBeanXML());
		}
		getCDICoreNature().setBeanDiscoveryMode(newValue);
		return oldValue != newValue;
	}

	private int getBeanDiscoveryMode(XModelObject beansXML) {
		if(getCDICoreNature().getVersion() == CDIVersion.CDI_1_0) {
			return BeanArchiveDetector.ALL;
		} else if(beansXML == null) {
			return BeanArchiveDetector.ANNOTATED;
		}
		String bdm = beansXML.getAttributeValue("bean-discovery-mode");
		if("annotated".equals(bdm)) {
			return BeanArchiveDetector.ANNOTATED;
		} else if("none".equals(bdm)) {
			return BeanArchiveDetector.NONE;
		}
		return BeanArchiveDetector.ALL;
	}

	private XModelObject getPrimaryBeanXML() {
		IWorkspaceRoot root = getCurrentProject().getWorkspace().getRoot();
		CDIResourceVisitor visitor = getResourceVisitor();
		for (IPath p: visitor.webinfs) {
			IFile f = root.getFile(p.append("beans.xml"));
			if(f.exists()) {
				XModelObject o = EclipseResourceUtil.getObjectByResource(f);
				if(o == null) {
					o = EclipseResourceUtil.createObjectForResource(f);
				}
				if(o != null) {
					return o;
				}
			}
		}
		for (IPath p: visitor.srcs) {
			IFile f = root.getFile(p.append("META-INF/beans.xml"));
			if(f.exists()) {
				XModelObject o = EclipseResourceUtil.getObjectByResource(f);
				if(o == null) {
					o = EclipseResourceUtil.createObjectForResource(f);
				}
				if(o != null) {
					return o;
				}
			}
		}
		return null;
	}

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
		try {
			CDIResourceVisitor rv = getResourceVisitor();
			rv.incremental = false;
			getCurrentProject().accept(rv);
			FileSet fs = rv.fileSet;
			if(getCDICoreNature().getBeanDiscoveryMode() == BeanArchiveDetector.NONE) {
				for (IPath path: fs.getClasses().keySet()) {
					getCDICoreNature().getDefinitions().getWorkingCopy().clean(path);
				}
				fs = new FileSet();
			}
			invokeBuilderDelegates(fs, getCDICoreNature());
			
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		if(getCDICoreNature().getBeanDiscoveryMode() == BeanArchiveDetector.NONE) {
			return;
		}
		CDIResourceVisitor rv = getResourceVisitor();
		rv.incremental = true;
		delta.accept(new SampleDeltaVisitor());
		FileSet fs = rv.fileSet;
		invokeBuilderDelegates(fs, getCDICoreNature());
	}

	protected void incrementalBuild(IncrementCheck increment,
			IProgressMonitor monitor) throws CoreException {
		if(getCDICoreNature().getBeanDiscoveryMode() == BeanArchiveDetector.NONE) {
			return;
		}
		CDIResourceVisitor rv = getResourceVisitor();
		rv.incremental = true;
		rv.visit(increment.file);
		FileSet fs = rv.fileSet;
		invokeBuilderDelegates(fs, getCDICoreNature());
	}

	protected void buildJars(JarSet newJars) throws CoreException {
		IJavaProject jp = EclipseResourceUtil.getJavaProject(getCDICoreNature().getProject());
		if(jp == null) return;
		FileSet fileSet = new FileSet();
		fileSet.setCheckVetoed(getCDICoreNature().getVersion() != CDIVersion.CDI_1_0);
		
		for (String jar: newJars.getBeanModules().keySet()) {
			Path path = new Path(jar);
			IPackageFragmentRoot root = BeanArchiveDetector.findPackageFragmentRoot(jar, jp);
			if (root == null || !root.exists()) {
				continue;
			}

			XModelObject beansXML = newJars.getBeanModules().get(jar);
			int bdm = getBeanDiscoveryMode(beansXML);
			if(bdm == BeanArchiveDetector.NONE) {
				continue;
			}
			boolean annotatedOnly = bdm == BeanArchiveDetector.ANNOTATED;
			
			IJavaElement[] es = root.getChildren();
			for (IJavaElement e : es) {
				if (e instanceof IPackageFragment) {
					IPackageFragment pf = (IPackageFragment) e;
					IClassFile[] cs = pf.getClassFiles();
					IType packageInfo = BeanArchiveDetector.findPackageInfo(cs);
					if(packageInfo != null && BeanArchiveDetector.isVetoed(packageInfo)) {
						continue;
					}

					for (IClassFile c : cs) {
						IType t = c.getType();
						if(!annotatedOnly || BeanArchiveDetector.isAnnotatedBean(t, getCDICoreNature())) {
							fileSet.add(path, c.getType());
						}
					}
				}
			}
			if(beansXML != null) {
				fileSet.setBeanXML(path, beansXML);
			}
			
			for (IBuildParticipantFeature p: buildParticipants) p.visitJar(path, root, beansXML);
		}
		if(!buildParticipants2.isEmpty()) {
			for (String jar: newJars.getFileSystems().keySet()) {
				Path path = new Path(jar);
				XModelObject fs = newJars.getFileSystems().get(jar);
				for (IBuildParticipant2Feature p: buildParticipants2) p.visitJar(path, fs);
			}
		}
		addBasicTypes(fileSet);
		invokeBuilderDelegates(fileSet, getCDICoreNature());
	}

	void invokeBuilderDelegates(FileSet fileSet, CDICoreNature n) {
		builderDelegate.build(fileSet, n);
		for (IBuildParticipantFeature p: buildParticipants) p.buildDefinitions(fileSet);
	}

	void addBasicTypes(FileSet fs) throws CoreException {
		IJavaProject jp = EclipseResourceUtil.getJavaProject(getCurrentProject());
		if(jp == null) return;
		for (String s: AnnotationHelper.SCOPE_ANNOTATION_TYPES) {
			IType type = EclipseJavaUtil.findType(jp, s);
			if(type != null) fs.add(type.getPath(), type);
		}
		for (String s: AnnotationHelper.QUALIFIER_ANNOTATION_TYPES) {
			IType type = EclipseJavaUtil.findType(jp, s);
			if(type != null) fs.add(type.getPath(), type);
		}
		for (String s: AnnotationHelper.STEREOTYPE_ANNOTATION_TYPES) {
			IType type = EclipseJavaUtil.findType(jp, s);
			if(type != null) fs.add(type.getPath(), type);
		}
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		CDICoreNature n = getCDICoreNature();
		if(n != null) n.clean();
	}

	/**
	 * Returns files directly dependent on path which are not included into visited set. 
	 * 
	 * @param path
	 * @param visited
	 * @return
	 */
	Set<IFile> getDependentFiles(IPath path, Set<IPath> visited) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		Dependencies d = getCDICoreNature().getDefinitions().getWorkingCopy().getDependencies();
		Set<IFile> result = new HashSet<IFile>();

		// we do not need to recurse: that will be done by visitor.
		Set<IPath> ps = d.getDirectDependencies(path);
		if(ps != null) for (IPath p: ps) {
			if(visited.contains(p)) continue;
			IFile f = root.getFile(p);
			if(f.exists()) {
				result.add(f);
			}
		}
		
		return result;		
	}

	/**
	 * Check delta for incremental build to find that
	 * 1) A single resource is changed, and
	 * 2) It is a java source file, and
	 * 3) It contains no annotations, no file level or inner static interfaces; and
	 * 4) Each type was previously processed and TypeDefinition object is cached for it,
	 * 5) Changes does not involve imports and super types.
	 * 
	 * If all this checks pass isIncremental remains set to true, and is changed to false otherwise.
	 * 
	 * This flag is used by build method to apply changes to model without
	 * resetting all references to Java model objects in class beans that 
	 * may not be affected.  
	 *
	 */
	class IncrementCheck implements IResourceDeltaVisitor {
		IFile file = null;
		CDICoreNature n;

		boolean isIncremental = true;
		
		IncrementCheck(IResourceDelta delta, CDICoreNature n) throws CoreException {
			this.n = n;
			isIncremental = true;
			delta.accept(this);
			if(!isIncremental) {
				return;
			}			
			isIncremental = false;
			if(file == null) {				
				return;
			}			
			IPath path = file.getFullPath();
			CDIResourceVisitor v = new CDIResourceVisitor();
			for (int i = 0; i < v.outs.length; i++) {
				if(v.outs[i].isPrefixOf(path)) {
					return;
				}
			}
			boolean foundInSrc = false;
			for (int i = 0; i < v.srcs.length && !foundInSrc; i++) {
				if(v.srcs[i].isPrefixOf(path)) {
					foundInSrc = true;
				}
			}
			if(!foundInSrc) {
				return;
			}
			
			ICompilationUnit unit = EclipseUtil.getCompilationUnit(file);
			if(unit == null) {
				return;
			}
			IType[] ts = unit.getTypes();
			if(ts == null) {
				return;
			}
			FileSet fs = new FileSet();
			fs.add(file.getFullPath(), ts);
			if(!fs.getAnnotations().isEmpty() || !fs.getInterfaces().isEmpty()) {
				return;
			}
			List<IType> types = fs.getClasses().get(file.getFullPath());
			for (IType t: types) {
				TypeDefinition oldDefinition = n.getDefinitions().getTypeDefinition(t.getFullyQualifiedName());
				if(oldDefinition == null) {
					return;
				}
				TypeDefinition newDefinition = new TypeDefinition();
				newDefinition.setType(t, n.getDefinitions(), 0);
				if(oldDefinition.getParametedType().getInheritanceCode()
						!= newDefinition.getParametedType().getInheritanceCode()) {
					return;
				}
			}
			isIncremental = true;
		}

		public boolean visit(IResourceDelta delta) throws CoreException {
			if(!isIncremental) return false;
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.REMOVED:
			case IResourceDelta.ADDED:
				isIncremental = false;
				return false;
			case IResourceDelta.CHANGED:
				if(resource instanceof IFile) {
					IFile f = (IFile)resource;
					if(f.getName().toLowerCase().endsWith(".class")) {
						return false;
					}
					if(file != null || !f.getName().toLowerCase().endsWith(".java") || isPackageInfo(f)) {
						isIncremental = false;
						return false;
					} else {
						file = f;
					}
				}
			}
			return true;
		}
	}

	class SampleDeltaVisitor implements IResourceDeltaVisitor {
		/*
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				return getResourceVisitor().visit(resource);
			case IResourceDelta.REMOVED:
				CDICoreNature p = getCDICoreNature();
				CDIResourceVisitor v = getResourceVisitor();
				Set<IFile> fs = getDependentFiles(resource.getFullPath(), v.visited);
				for (IFile f: fs) {
					v.visit(f);
				}
				if(p != null) {
					p.getDefinitions().getWorkingCopy().clean(resource.getFullPath());
				}
				break;
			case IResourceDelta.CHANGED:
				return getResourceVisitor().visit(resource);
			}
			//return true to continue visiting children.
			return true;
		}
	}

	class CDIResourceVisitor implements IResourceVisitor {
		boolean incremental = false;
		FileSet fileSet = new FileSet();
		IPath[] outs = new IPath[0];
		IPath[] srcs = new IPath[0];
		IPath[] webinfs = new IPath[0];
		Set<IPath> visited = new HashSet<IPath>();
		Map<IPath, PackageInfo> checkedPackages = new HashMap<IPath, PackageInfo>();
		
		CDIResourceVisitor() {
			fileSet.setCheckVetoed(getCDICoreNature().getVersion() != CDIVersion.CDI_1_0);
			webinfs = WebUtils.getWebInfPaths(getCurrentProject());
			getJavaSourceRoots(getCurrentProject());
		}

		void getJavaSourceRoots(IProject project) {
			IJavaProject javaProject = EclipseResourceUtil.getJavaProject(project);
			if(javaProject == null) return;
			List<IPath> ps = new ArrayList<IPath>();
			List<IPath> os = new ArrayList<IPath>();
			try {
				IPath output = javaProject.getOutputLocation();
				if(output != null) os.add(output);
				IClasspathEntry[] es = javaProject.getResolvedClasspath(true);
				for (int i = 0; i < es.length; i++) {
					if(es[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
						IResource findMember = ResourcesPlugin.getWorkspace().getRoot().findMember(es[i].getPath());
						if(findMember != null && findMember.exists()) {
							ps.add(findMember.getFullPath());
						}
						IPath out = es[i].getOutputLocation();
						if(out != null && !os.contains(out)) {
							os.add(out);
						}
					} 
				}
				srcs = ps.toArray(new IPath[ps.size()]);
				outs = os.toArray(new IPath[os.size()]);
			} catch(CoreException ce) {
				CDICorePlugin.getDefault().logError("Error while locating java source roots for " + project, ce);
			}
		}

		public boolean visit(IResource resource) throws CoreException {
			IPath path = resource.getFullPath();
			path = UniquePaths.getInstance().intern(path);
			if(resource instanceof IFile) {
				if(visited.contains(path)) {
					return false;
				}
				visited.add(path);
				IFile f = (IFile)resource;
				for (int i = 0; i < outs.length; i++) {
					if(outs[i].isPrefixOf(path)) {
						return false;
					}
				}
				for (int i = 0; i < srcs.length; i++) {
					if(srcs[i].isPrefixOf(path)) {
						if(f.getName().endsWith(".java")) {
							ICompilationUnit unit = EclipseUtil.getCompilationUnit(f);
							if(unit!=null) {
								if(isPackageInfo(f)) {
									IPackageDeclaration[] pkg = unit.getPackageDeclarations();
									if(pkg != null && pkg.length > 0) {
										fileSet.add(f.getFullPath(), pkg[0]);
										if(incremental) {
											IResource[] ms = resource.getParent().members();
											for (IResource m: ms) {
												if(m instanceof IFile && !isPackageInfo((IFile)m)) {
													visit(m);
												}
											}
										}
									}
								} else {
									IType[] ts = isInVetoedPackage(f) ? new IType[0] : unit.getTypes();
									//do not filter vetoed types now, do it in FileSet to process nested types.
									if(ts.length > 0 && getCDICoreNature().getBeanDiscoveryMode() == BeanArchiveDetector.ANNOTATED) {
										ts = BeanArchiveDetector.getAnnotatedTypes(ts, getCDICoreNature());
									}
									fileSet.add(f.getFullPath(), ts);
								}
							}
						}
						else if(path.segmentCount() == srcs[i].segmentCount() + 2
							&& "META-INF".equals(path.segments()[path.segmentCount() - 2])) {
							addBeansXML(f, fileSet);
						}
						for (IBuildParticipantFeature p: buildParticipants) p.visit(f, srcs[i], null);
						Set<IFile> ds = getDependentFiles(path, visited);
						if(ds != null) for (IFile d: ds) visit(d);
						return false;
					}
				}
				for (IPath webinf: webinfs) {
					if(webinf.isPrefixOf(path)) {
						if(webinf.segmentCount() == path.segmentCount() - 1) {
							addBeansXML(f, fileSet);
						}
						for (IBuildParticipantFeature p: buildParticipants) p.visit(f, null, webinf);
					}
				}
				
				Set<IFile> ds = getDependentFiles(path, visited);
				if(ds != null) for (IFile d: ds) visit(d);
			}
			
			if(resource instanceof IFolder) {
				for (IPath out: outs) {
					if(out.isPrefixOf(path)) {
						return false;
					}
				}
				for (IPath src: srcs) {
					if(src.isPrefixOf(path) || path.isPrefixOf(src)) {
						return true;
					}
				}
				for (IPath webinf: webinfs) {
					if(webinf.isPrefixOf(path) || path.isPrefixOf(webinf)) {
						return true;
					}
				}
				if(resource == resource.getProject()) {
					return true;
				}
				return false;
			}
			//return true to continue visiting children.
			return true;
		}

		private boolean isInVetoedPackage(IFile f) throws CoreException {
			if(getCDICoreNature().getVersion() == CDIVersion.CDI_1_0) {
				return false;
			}
			IContainer c = f.getParent();
			PackageInfo b = checkedPackages.get(c.getFullPath());
			if(b == null) {
				b = new PackageInfo(c);
				checkedPackages.put(c.getFullPath(), b);
			}
			return b.exists && b.isVetoed;
		}		
	}

	private class PackageInfo {
		IPath path = null;
		boolean exists = false;
		boolean isVetoed = false;

		PackageInfo(IContainer c) throws CoreException {
			IFile f = c.getFile(new Path(PACKAGE_INFO));
			path = f.getFullPath();
			exists = f.exists();
			if(exists) {
				DefinitionContext context = getCDICoreNature().getDefinitions().getWorkingCopy();
				ICompilationUnit unit = EclipseUtil.getCompilationUnit(f);
				IPackageDeclaration[] pkg = unit.getPackageDeclarations();
				if(pkg != null && pkg.length > 0) {
					PackageDefinition def = null;
					//we cannot be sure that copy in context is up to date. 
					//context.getPackageDefinition(pkg[0].getElementName());
					if(def == null) {
						def = new PackageDefinition();
						def.setPackage(pkg[0], context);
						//Add to context now?
					}
					isVetoed = def.isVetoed();
				}
			}
		}
	}

	private void addBeansXML(IFile f, FileSet fileSet) {
		if(f.getName().equals("beans.xml")) {
			XModelObject beansXML = EclipseResourceUtil.getObjectByResource(f);
			if(beansXML == null) {
				beansXML = EclipseResourceUtil.createObjectForResource(f);
			}
			if(beansXML != null) {
				fileSet.setBeanXML(f.getFullPath(), beansXML);
			}
		}
	}

	public static boolean isPackageInfo(IResource f) {
		return f instanceof IFile && f.getName().equals(PACKAGE_INFO);		
	}

}

