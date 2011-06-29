package org.jboss.tools.cdi.ui.test.testmodel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
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
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.java.ITypeDeclaration;
import org.jboss.tools.common.text.ITextSourceReference;

public class CDIBean extends CDIElement implements IClassBean{
	private ICDIProject project;
	private HashSet<IQualifier> qualifiers;
	private CDIClass cdiClass;

	public CDIBean(ICDIProject project, String qualifiedName){
		this.project = project;
		qualifiers = new HashSet<IQualifier>();
		IQualifier anyQualifier = project.getQualifier(CDIConstants.ANY_QUALIFIER_TYPE_NAME);
		IQualifier defaultQualifier = project.getQualifier(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
		qualifiers.add(anyQualifier);
		qualifiers.add(defaultQualifier);
		cdiClass = new CDIClass(qualifiedName);
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
	public Set<IStereotypeDeclaration> getStereotypeDeclarations() {
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
	public ITextSourceReference getAnnotationPosition(
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
		return null;
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
	public ITextSourceReference getNameLocation() {
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
	public String getSimpleJavaName() {
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
	public Set<IInterceptorBindingDeclaration> getInterceptorBindingDeclarations(
			boolean includeInherited) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IInterceptorBinding> getInterceptorBindings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IProducer> getProducers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IBeanMethod> getDisposers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IBeanMethod> getBeanConstructors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IBeanMethod> getAllMethods() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IObserverMethod> getObserverMethods() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<? extends IClassBean> getSpecializingBeans() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IParametedType getSuperType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IClassBean getSuperClassBean() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSuperClassBean(IClassBean bean) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IMember getSourceMember() {
		return getBeanClass();
	}
}