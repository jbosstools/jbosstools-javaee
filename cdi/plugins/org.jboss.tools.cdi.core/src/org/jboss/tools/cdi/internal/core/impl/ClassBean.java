package org.jboss.tools.cdi.internal.core.impl;

import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.core.ITypeDeclaration;
import org.jboss.tools.common.text.ITextSourceReference;

public class ClassBean extends CDIElement implements IClassBean {

	public ClassBean() {}

	public Set<IBeanMethod> getBeanConstructor() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IBeanMethod> getDisposers() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IInterceptorBindingDeclaration> getInterceptorBindings() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IObserverMethod> getObserverMethods() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IProducer> getProducers() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<ITypeDeclaration> getAllTypeDeclarations() {
		// TODO Auto-generated method stub
		return null;
	}

	public IAnnotationDeclaration getAlternativeDeclaration() {
		// TODO Auto-generated method stub
		return null;
	}

	public IType getBeanClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IInjectionPoint> getInjectionPoints() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IType> getLegalTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITextSourceReference getNameLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IAnnotationDeclaration> getQualifierDeclarations() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<ITypeDeclaration> getRestrictedTypeDeclaratios() {
		// TODO Auto-generated method stub
		return null;
	}

	public IBean getSpecializedBean() {
		// TODO Auto-generated method stub
		return null;
	}

	public IAnnotationDeclaration getSpecializesAnnotationDeclaration() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IStereotypeDeclaration> getStereotypeDeclarations() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAlternative() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDependent() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSpecializing() {
		// TODO Auto-generated method stub
		return false;
	}

	public IType getScope() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IAnnotationDeclaration> getScopeDeclarations() {
		// TODO Auto-generated method stub
		return null;
	}

}
