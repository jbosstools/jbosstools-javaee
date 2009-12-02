package org.jboss.tools.cdi.internal.core.impl;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.common.text.INodeReference;

public class CDIProject implements ICDIProject {

	public List<INodeReference> getAlternativeClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<INodeReference> getAlternativeStereotypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<INodeReference> getAlternatives(String fullQualifiedTypeName) {
		// TODO Auto-generated method stub
		return null;
	}

	public IClassBean getBeanClass(IType type) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IBean> getBeans(String name,
			boolean attemptToResolveAmbiguousNames) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IBean> getBeans(boolean attemptToResolveAmbiguousDependency,
			IType beanType, IAnnotationDeclaration... qualifiers) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IBean> getBeans(IInjectionPoint injectionPoints) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IBean> getBeans(IPath path) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<INodeReference> getDecoratorClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<INodeReference> getDecoratorClasses(String fullQualifiedTypeName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<INodeReference> getInterceptorClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<INodeReference> getInterceptorClasses(
			String fullQualifiedTypeName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IType> getQualifierTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IType> getStereotypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isNormalScope(IType annotationType) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPassivatingScope(IType annotationType) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isQualifier(IType annotationType) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isScope(IType annotationType) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStereotype(IType annotationType) {
		// TODO Auto-generated method stub
		return false;
	}

	public Set<IBean> resolve(Set<IBean> beans) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IObserverMethod> resolveObserverMethods(
			IInjectionPoint injectionPoint) {
		// TODO Auto-generated method stub
		return null;
	}

}
