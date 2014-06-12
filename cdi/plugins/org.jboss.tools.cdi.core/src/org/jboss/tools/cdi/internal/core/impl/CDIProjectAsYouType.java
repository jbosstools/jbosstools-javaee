/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.internal.core.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIVersion;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.ICDIElement;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IDecorator;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInterceptor;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.DefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.scanner.CDIBuilderDelegate;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.cdi.internal.core.scanner.ImplementationCollector;
import org.jboss.tools.cdi.internal.core.scanner.lib.BeanArchiveDetector;
import org.jboss.tools.common.CommonPlugin;
import org.jboss.tools.common.java.IJavaReference;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.text.INodeReference;

/**
 * 
 * @author Viacheslav Kabanovich 
 *
 */
public class CDIProjectAsYouType implements ICDIProject, ICDIElement {
	ICDIProject project;
	IFile file;

	Collection<IBean> beans = new ArrayList<IBean>();
	StereotypeElement stereotype;
	QualifierElement qualifier;
	ScopeElement scope;
	IInterceptorBinding interceptorBinding;

	public CDIProjectAsYouType(ICDIProject project, IFile file) {
		if(file.getProject() != project.getNature().getProject()) {
			/**
			 * Validation is done in context of the root project, 
			 * however, at present as-you-type project based on the root project
			 * appears to be too complicated to implement, so that currently
			 * it will be based on the project of the file itself.
			 */
			ICDIProject p = CDICorePlugin.getCDIProject(file.getProject(), true);
			if(p != null) project = p;
		}
		this.project = project;
		this.file = file;
		try {
			build();
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		CDIProject p = ((CDIProject)project).getModifiedCopy(file, beans);
		if(p != null) {
			this.project = p;
		}
	}

	public CDIVersion getVersion() {
		return project.getVersion();
	}

	private void build() throws CoreException {
		DefinitionContext context = project.getNature().getDefinitions().getCleanCopy();
		FileSet fileSet = new FileSet();
		if(file.getName().endsWith(".java")) {
			ICompilationUnit unit = findCompilationUnit();// EclipseUtil.getCompilationUnit(file);
			if(unit!=null) {
				if(file.getName().equals("package-info.java")) {
					IPackageDeclaration[] pkg = unit.getPackageDeclarations();
					if(pkg != null && pkg.length > 0) {
						fileSet.add(file.getFullPath(), pkg[0]);
//						if(incremental) {
//							IResource[] ms = file.getParent().members();
//							for (IResource m: ms) {
//								if(m instanceof IFile && !m.getName().equals("package-info.java")) {
//									visit(m);
//								}
//							}
//						}
					}
				} else {
					IType[] ts = unit.getTypes();
					if(getNature().getBeanDiscoveryMode() == BeanArchiveDetector.NONE) {
						ts = new IType[0];
					}
					if(ts.length > 0 && getNature().getBeanDiscoveryMode() == BeanArchiveDetector.ANNOTATED) {
						ts = BeanArchiveDetector.getAnnotatedTypes(ts, getNature());
					}
					fileSet.add(file.getFullPath(), ts);
				}
			}
		}
		CDIBuilderDelegate builder = new CDIBuilderDelegate();
		builder.build(fileSet, context);

		rebuildAnnotationTypes(context.getAllAnnotations());
		rebuildBeans(context.getTypeDefinitions());

	}

	private ICompilationUnit findCompilationUnit() {
		IWorkbench workbench = CommonPlugin.getDefault().getWorkbench();
		if(workbench != null) {
			IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
			for (IWorkbenchWindow window: windows) {
				if(window.getShell() != null) {
					IWorkbenchPage[] pages = window.getPages();
					for (IWorkbenchPage page: pages) {
						IEditorReference[] rs = page.getEditorReferences();
						for (IEditorReference r: rs) {
							IEditorPart part = r.getEditor(false);
							if(part != null) {
								IFile file = getFile(part);
								if(file != null && file.equals(this.file) && part instanceof CompilationUnitEditor) {
									IWorkingCopyManager manager= JavaUI.getWorkingCopyManager();
									ICompilationUnit unit= manager.getWorkingCopy(part.getEditorInput());
									if(unit != null) {
										try {
											unit.reconcile(ICompilationUnit.NO_AST,
													false /* don't force problem detection */,
													null /* use primary owner */,
													null /* no progress monitor */);
										} catch (JavaModelException e) {
											CDICorePlugin.getDefault().logError(e);
										}
										return unit;
									}
								}
							}							
						}
					}
				}
			}
		}
		return null;
	}

	private IFile getFile(IEditorPart part) {
		IEditorInput input = part.getEditorInput();
		return (input instanceof IFileEditorInput) ? ((IFileEditorInput)input).getFile() : null;
	}

	synchronized void rebuildAnnotationTypes(List<AnnotationDefinition> ds) {
		for (AnnotationDefinition d: ds) {
			if(d.getResource() == null || !d.getResource().getFullPath().equals(file.getFullPath())) {
				continue;
			}
//			System.out.println("Annotation " + d.getQualifiedName());
			if((d.getKind() & AnnotationDefinition.STEREOTYPE) > 0) {
				StereotypeElement s = new StereotypeElement();
				initAnnotationElement(s, d);
				stereotype = s;
			}
			if((d.getKind() & AnnotationDefinition.INTERCEPTOR_BINDING) > 0) {
				InterceptorBindingElement s = new InterceptorBindingElement();
				initAnnotationElement(s, d);
				interceptorBinding = s;
			}
			if((d.getKind() & AnnotationDefinition.QUALIFIER) > 0) {
				QualifierElement s = new QualifierElement();
				initAnnotationElement(s, d);
				qualifier = s;
			}
			if((d.getKind() & AnnotationDefinition.SCOPE) > 0) {
				ScopeElement s = new ScopeElement();
				initAnnotationElement(s, d);
				scope = s;
			}
		}
	}

	private void initAnnotationElement(CDIAnnotationElement s, AnnotationDefinition d) {
		s.setDefinition(d);
		s.setParent((CDIElement)project);
		IPath r = d.getType().getPath();
		if(r != null) {
			s.setSourcePath(r);
		}
	}
	
	void rebuildBeans(List<TypeDefinition> typeDefinitions) {
//		Set<String> vetoedTypes = n.getAllVetoedTypes();
		List<IBean> beans = new ArrayList<IBean>();

		Set<IType> newAllTypes = new HashSet<IType>();
		for (TypeDefinition d: typeDefinitions) {
			newAllTypes.add(d.getType());
		}
		Map<TypeDefinition, ClassBean> newDefinitionToClassbeans = new HashMap<TypeDefinition, ClassBean>();
		Map<IType, IClassBean> newClassBeans = new HashMap<IType, IClassBean>();
		
		ImplementationCollector ic = new ImplementationCollector(typeDefinitions);

		for (TypeDefinition typeDefinition : typeDefinitions) {
			ClassBean bean = null;
			if(typeDefinition.getInterceptorAnnotation() != null || ic.isInterceptor(typeDefinition.getType())) {
				bean = new InterceptorBean();
			} else if(typeDefinition.getDecoratorAnnotation() != null || ic.isDecorator(typeDefinition.getType())) {
				bean = new DecoratorBean();
			} else if(typeDefinition.getStatefulAnnotation() != null || typeDefinition.getStatelessAnnotation() != null || typeDefinition.getSingletonAnnotation() != null) {
				bean = new SessionBean();
			} else {
				bean = new ClassBean();
			}
			/*
			 * Parent can be either 'this' or 'project'. In the second case, it is an original project
			 * that will return 'unmodified' data. In the first case, correctness of 'declaring project'
			 * needs attention.
			 */
			bean.setParent(this);  
			bean.setDefinition(typeDefinition);
			
			newDefinitionToClassbeans.put(typeDefinition, bean);

			String typeName = typeDefinition.getType().getFullyQualifiedName();
			if(!typeDefinition.isVetoed() 
					    //Type is defined in another project and modified/replaced in config in this (dependent) project
					    //We should reject type definition based on type, but we have to accept 
					&& !(/*vetoedTypes.contains(typeName)*/false && getNature().getDefinitions().getTypeDefinition(typeName) == null && typeDefinition.getOriginalDefinition() == null)) {
				if(typeDefinition.hasBeanConstructor()) {
					beans.add(bean);
					newClassBeans.put(typeDefinition.getType(), bean);
				} else {
					beans.add(bean);
				}

				for (IProducer producer: bean.getProducers()) {
					beans.add(producer);
				}
			}
		}
	
		for (IClassBean bean: newClassBeans.values()) {
			IParametedType s = ((ClassBean)bean).getSuperType();
			if(s != null && s.getType() != null) {
				IClassBean superClassBean = newClassBeans.get(s.getType());
				if(superClassBean == null) superClassBean = project.getBeanClass(s.getType());
				if(bean instanceof ClassBean) {
					((ClassBean)bean).setSuperClassBean(superClassBean);
				}
			}
		}	

		for (IBean bean: beans) {
			addBean(bean);
		}
	
	}

	public void addBean(IBean bean) {
		beans.add(bean);
		//TODO
	}

	@Override
	public IBean[] getBeans() {
		return project.getBeans();
	}

	@Override
	public Collection<IBean> getNamedBeans(boolean attemptToResolveAmbiguousNames) {
		return project.getNamedBeans(attemptToResolveAmbiguousNames);
	}

	@Override
	public Collection<IBean> getBeans(String name,
			boolean attemptToResolveAmbiguousNames) {
		return project.getBeans(name, attemptToResolveAmbiguousNames);
	}

	@Override
	public Collection<IBean> getBeans(boolean attemptToResolveAmbiguousDependency,
			IParametedType beanType, IQualifierDeclaration... qualifiers) {
		return project.getBeans(attemptToResolveAmbiguousDependency, beanType, qualifiers);
	}

	@Override
	public Collection<IBean> getBeans(boolean attemptToResolveAmbiguousDependency,
			IParametedType beanType, IType... qualifiers) {
		return project.getBeans(attemptToResolveAmbiguousDependency, beanType, qualifiers);
	}

	@Override
	public Collection<IBean> getBeans(boolean attemptToResolveAmbiguousDependency,
			String fullyQualifiedBeanType,
			String... fullyQualifiedQualifiersTypes) {
		return project.getBeans(attemptToResolveAmbiguousDependency, fullyQualifiedBeanType, fullyQualifiedQualifiersTypes);
	}

	@Override
	public Collection<IBean> getBeans(boolean attemptToResolveAmbiguousDependency,
			IInjectionPoint injectionPoint) {
		return project.getBeans(attemptToResolveAmbiguousDependency, injectionPoint);
	}

	@Override
	public IClassBean getBeanClass(IType type) {
		return project.getBeanClass(type);
	}

	@Override
	public Collection<IBean> getBeans(IPath path) {
		if(path.equals(file.getFullPath())) {
			return beans;
		}
		return project.getBeans(path);
	}

	@Override
	public Collection<IBean> getBeans(IJavaElement element) {
		if(element.getResource() != null && element.getResource().getFullPath().equals(file.getFullPath())) {
			Set<IBean> result = new HashSet<IBean>();
			for (IBean bean: beans) {
				if(bean instanceof IJavaReference) {
					if(((IJavaReference)bean).getSourceMember().equals(element)) {
						result.add(bean);
					}
				}
			}
			return result;
		}
		return project.getBeans(element);
	}

	@Override
	public IQualifier[] getQualifiers() {
		return project.getQualifiers();
	}

	@Override
	public IStereotype[] getStereotypes() {
		return project.getStereotypes();
	}

	@Override
	public IBean[] getAlternatives() {
		return project.getAlternatives();
	}

	@Override
	public IDecorator[] getDecorators() {
		return project.getDecorators();
	}

	@Override
	public IInterceptor[] getInterceptors() {
		return project.getInterceptors();
	}

	@Override
	public IStereotype getStereotype(String qualifiedName) {
		return project.getStereotype(qualifiedName);
	}

	@Override
	public IStereotype getStereotype(IPath path) {
		if(path.equals(file.getFullPath())) {
			return stereotype;
		}
		return project.getStereotype(path);
	}

	@Override
	public IStereotype getStereotype(IType type) {
		return getStereotype(type.getPath());
	}

	@Override
	public IInterceptorBinding[] getInterceptorBindings() {
		return project.getInterceptorBindings();
	}

	@Override
	public IInterceptorBinding getInterceptorBinding(String qualifiedName) {
		return project.getInterceptorBinding(qualifiedName);
	}

	@Override
	public IInterceptorBinding getInterceptorBinding(IPath path) {
		if(path.equals(file.getFullPath())) {
			return interceptorBinding;
		}
		return project.getInterceptorBinding(path);
	}

	@Override
	public IQualifier getQualifier(String qualifiedName) {
		return project.getQualifier(qualifiedName);
	}

	@Override
	public IQualifier getQualifier(IPath path) {
		if(path.equals(file.getFullPath())) {
			return qualifier;
		}
		return project.getQualifier(path);
	}

	@Override
	public Collection<String> getScopeNames() {
		return project.getScopeNames();
	}

	@Override
	public IScope getScope(String qualifiedName) {
		return project.getScope(qualifiedName);
	}

	@Override
	public IScope getScope(IPath path) {
		if(path.equals(file.getFullPath())) {
			return scope;
		}
		return project.getScope(path);
	}

	@Override
	public Collection<IObserverMethod> resolveObserverMethods(IInjectionPoint injectionPoint) {
		// TODO resolve in file
		return project.resolveObserverMethods(injectionPoint);
	}

	@Override
	public Collection<IInjectionPoint> findObservedEvents(IParameter observedEventParameter) {
		// TODO find in file
		return project.findObservedEvents(observedEventParameter);
	}

	@Override
	public Collection<IBean> resolve(Collection<IBean> beans) {
		return project.resolve(beans);
	}

	@Override
	public Collection<IBeanMethod> resolveDisposers(IProducerMethod producer) {
		return project.resolveDisposers(producer);
	}

	@Override
	public boolean isScope(IType annotationType) {
		return project.isScope(annotationType);
	}

	@Override
	public boolean isNormalScope(IType annotationType) {
		return project.isNormalScope(annotationType);
	}

	@Override
	public boolean isPassivatingScope(IType annotationType) {
		return project.isPassivatingScope(annotationType);
	}

	@Override
	public boolean isQualifier(IType annotationType) {
		return project.isQualifier(annotationType);
	}

	@Override
	public boolean isStereotype(IType annotationType) {
		return project.isStereotype(annotationType);
	}

	@Override
	public List<INodeReference> getAlternativeClasses() {
		return project.getAlternativeClasses();
	}

	@Override
	public List<INodeReference> getAlternativeStereotypes() {
		return project.getAlternativeStereotypes();
	}

	@Override
	public List<INodeReference> getAlternatives(String fullyQualifiedTypeName) {
		return  project.getAlternatives(fullyQualifiedTypeName);
	}

	@Override
	public List<INodeReference> getDecoratorClasses() {
		return project.getDecoratorClasses();
	}

	@Override
	public List<INodeReference> getDecoratorClasses(String fullyQualifiedTypeName) {
		return project.getDecoratorClasses(fullyQualifiedTypeName);
	}

	@Override
	public List<INodeReference> getInterceptorClasses() {
		return project.getInterceptorClasses();
	}

	@Override
	public List<INodeReference> getInterceptorClasses(String fullyQualifiedTypeName) {
		return project.getInterceptorClasses(fullyQualifiedTypeName);
	}

	@Override
	public Collection<IInjectionPoint> getInjections(String fullyQualifiedTypeName) {
		return project.getInjections(fullyQualifiedTypeName);
	}

	@Override
	public CDICoreNature getNature() {
		return project.getNature();
	}

	@Override
	public void setNature(CDICoreNature n) {
		//nothing
	}

	@Override
	public void update(boolean updateDependent) {
		//nothing
	}

	/**
	 *
	 */

	@Override
	public ICDIProject getCDIProject() {
		return this;
	}

	@Override
	public ICDIProject getDeclaringProject() {
		return ((ICDIElement)project).getDeclaringProject();
	}

	@Override
	public IPath getSourcePath() {
		return getResource().getFullPath();
	}

	@Override
	public IResource getResource() {
		return getNature().getProject();
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public String getElementName() {
		return getNature().getProject().getName();
	}

	@Override
	public boolean isTypeAlternative(String qualifiedName) {
		return project.isTypeAlternative(qualifiedName);
	}

	@Override
	public boolean isStereotypeAlternative(String qualifiedName) {
		return project.isStereotypeAlternative(qualifiedName);
	}

	@Override
	public boolean isClassAlternativeActivated(String fullQualifiedTypeName) {
		return project.isClassAlternativeActivated(fullQualifiedTypeName);
	}

	@Override
	public int getId() {
		return ((ICDIElement)project).getId();
	}

}
