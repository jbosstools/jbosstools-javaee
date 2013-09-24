/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.test.testmodel;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInitializerMethod;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.IScopeDeclaration;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IJavaSourceReference;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.java.ITypeDeclaration;
import org.jboss.tools.common.text.ITextSourceReference;

public class CDIBean extends CDIElement implements IClassBean{
	private ICDIProject project;
	private HashSet<IQualifier> qualifiers;
	private Type cdiClass;
	private File cdiFile;

	public CDIBean(ICDIProject project, String qualifiedName){
		this.project = project;
		qualifiers = new HashSet<IQualifier>();
		IQualifier anyQualifier = project.getQualifier(CDIConstants.ANY_QUALIFIER_TYPE_NAME);
		IQualifier defaultQualifier = project.getQualifier(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
		qualifiers.add(anyQualifier);
		qualifiers.add(defaultQualifier);
		cdiClass = new Type(qualifiedName);
		cdiFile = new File();
	}
	
	@Override
	public IScope getScope() {
		return null;
	}

	@Override
	public Set<IScopeDeclaration> getScopeDeclarations() {
		return null;
	}

	@Override
	public Collection<IStereotypeDeclaration> getStereotypeDeclarations() {
		return null;
	}

	@Override
	public List<IAnnotationDeclaration> getAnnotations() {
		return null;
	}

	@Override
	public IAnnotationDeclaration getAnnotation(String annotationTypeName) {
		return null;
	}

	@Override
	public IJavaSourceReference getAnnotationPosition(
			String annotationTypeName) {
		return null;
	}

	@Override
	public boolean isAnnotationPresent(String annotationTypeName) {
		return false;
	}

	@Override
	public ICDIProject getCDIProject() {
		return project;
	}

	@Override
	public IPath getSourcePath() {
		return null;
	}

	@Override
	public IResource getResource() {
		return cdiFile;
	}

	@Override
	public IType getBeanClass() {
		return cdiClass;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public ITextSourceReference getNameLocation(boolean stereotypeLocation) {
		return null;
	}

	@Override
	public Set<IParametedType> getLegalTypes() {
		return null;
	}

	@Override
	public Set<IParametedType> getAllTypes() {
		return null;
	}

	@Override
	public String getElementName() {
		return null;
	}

	@Override
	public Set<ITypeDeclaration> getAllTypeDeclarations() {
		return null;
	}

	@Override
	public Set<ITypeDeclaration> getRestrictedTypeDeclaratios() {
		return null;
	}

	@Override
	public Set<IQualifierDeclaration> getQualifierDeclarations() {
		return null;
	}

	@Override
	public Set<IQualifierDeclaration> getQualifierDeclarations(
			boolean includeInherited) {
		return null;
	}

	@Override
	public Set<IQualifier> getQualifiers() {
		return qualifiers;
	}

	@Override
	public boolean isAlternative() {
		return false;
	}

	@Override
	public boolean isSelectedAlternative() {
		return false;
	}

	@Override
	public IAnnotationDeclaration getAlternativeDeclaration() {
		return null;
	}

	@Override
	public Set<IInjectionPoint> getInjectionPoints() {
		return null;
	}

	@Override
	public Set<IInjectionPoint> getInjectionPoints(boolean all) {
		return null;
	}

	@Override
	public IBean getSpecializedBean() {
		return null;
	}

	@Override
	public IAnnotationDeclaration getSpecializesAnnotationDeclaration() {
		return null;
	}

	@Override
	public boolean isSpecializing() {
		return false;
	}

	@Override
	public boolean isDependent() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public Collection<IInterceptorBindingDeclaration> getInterceptorBindingDeclarations(
			boolean includeInherited) {
		return null;
	}

	@Override
	public Set<IInterceptorBinding> getInterceptorBindings() {
		return null;
	}

	@Override
	public Set<IProducer> getProducers() {
		return null;
	}

	@Override
	public Set<IBeanMethod> getDisposers() {
		return null;
	}

	@Override
	public Set<IBeanMethod> getBeanConstructors() {
		return null;
	}

	@Override
	public Set<IBeanMethod> getAllMethods() {
		return null;
	}

	@Override
	public Set<IObserverMethod> getObserverMethods() {
		return null;
	}

	@Override
	public Set<? extends IClassBean> getSpecializingBeans() {
		return null;
	}

	@Override
	public IClassBean getSuperClassBean() {
		return null;
	}

	@Override
	public IMember getSourceMember() {
		return getBeanClass();
	}

	@Override
	public ICDIProject getDeclaringProject() {
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public void open() {
	}

	@Override
	public Set<IInitializerMethod> getInitializers() {
		return null;
	}

	@Override
	public IJavaElement getSourceElement() {
		return getSourceMember();
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Integer getPriority() {
		// TODO Auto-generated method stub
		return null;
	}
}