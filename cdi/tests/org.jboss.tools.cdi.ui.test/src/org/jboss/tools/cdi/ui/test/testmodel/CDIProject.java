package org.jboss.tools.cdi.ui.test.testmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IDecorator;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInterceptor;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.text.INodeReference;

public class CDIProject implements ICDIProject{
	public static final String QUALIFIER1 = "org.test.Qualifier1";
	public static final String QUALIFIER2 = "org.test.Qualifier2";
	public static final String QUALIFIER3 = "org.test.Qualifier3";
	public static final String QUALIFIER4 = "org.test.Qualifier4";
	public static final String QUALIFIER5 = "org.test.Qualifier5";
	
	private IQualifier defaultQualifier, namedQualifier, anyQualifier, newQualifier;
	
	private ArrayList<IQualifier> qualifiers = new ArrayList<IQualifier>();
	
	public CDIProject(){
		defaultQualifier = new CDIQualifier(this, CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
		anyQualifier = new CDIQualifier(this, CDIConstants.ANY_QUALIFIER_TYPE_NAME);
		namedQualifier = new CDIQualifier(this, CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
		newQualifier = new CDIQualifier(this, CDIConstants.NEW_QUALIFIER_TYPE_NAME);
		
		qualifiers.add(namedQualifier);
		qualifiers.add(newQualifier);
		qualifiers.add(new CDIQualifier(this, QUALIFIER1));
		qualifiers.add(new CDIQualifier(this, QUALIFIER2));
		qualifiers.add(new CDIQualifier(this, QUALIFIER3));
		qualifiers.add(new CDIQualifier(this, QUALIFIER4));
		qualifiers.add(new CDIQualifier(this, QUALIFIER5));
	}

	@Override
	public IBean[] getBeans() {
		return null;
	}

	@Override
	public Set<IBean> getNamedBeans(boolean attemptToResolveAmbiguousNames) {
		return null;
	}

	@Override
	public Set<IBean> getBeans(String name,
			boolean attemptToResolveAmbiguousNames) {
		return null;
	}

	@Override
	public Set<IBean> getBeans(boolean attemptToResolveAmbiguousDependency,
			IParametedType beanType, IQualifierDeclaration... qualifiers) {
		return null;
	}

	@Override
	public Set<IBean> getBeans(boolean attemptToResolveAmbiguousDependency,
			IParametedType beanType, IType... qualifiers) {
		return null;
	}

	@Override
	public Set<IBean> getBeans(boolean attemptToResolveAmbiguousDependency,
			String fullyQualifiedBeanType,
			String... fullyQualifiedQualifiersTypes) {
		return null;
	}

	@Override
	public Set<IBean> getBeans(boolean attemptToResolveAmbiguousDependency,
			IInjectionPoint injectionPoint) {
		return null;
	}

	@Override
	public IClassBean getBeanClass(IType type) {
		return null;
	}

	@Override
	public Set<IBean> getBeans(IPath path) {
		return null;
	}

	@Override
	public IQualifier[] getQualifiers() {
		return (IQualifier[])qualifiers.toArray(new IQualifier[qualifiers.size()]);
	}

	@Override
	public IStereotype[] getStereotypes() {
		return null;
	}

	@Override
	public IBean[] getAlternatives() {
		return null;
	}

	@Override
	public IDecorator[] getDecorators() {
		return null;
	}

	@Override
	public IInterceptor[] getInterceptors() {
		return null;
	}

	@Override
	public IStereotype getStereotype(String qualifiedName) {
		return null;
	}

	@Override
	public IStereotype getStereotype(IPath path) {
		return null;
	}

	@Override
	public IStereotype getStereotype(IType type) {
		return null;
	}

	@Override
	public IInterceptorBinding[] getInterceptorBindings() {
		return null;
	}

	@Override
	public IInterceptorBinding getInterceptorBinding(String qualifiedName) {
		return null;
	}

	@Override
	public IInterceptorBinding getInterceptorBinding(IPath path) {
		return null;
	}

	@Override
	public IQualifier getQualifier(String qualifiedName) {
		if(qualifiedName.equals(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME))
			return defaultQualifier;
		else if(qualifiedName.equals(CDIConstants.NAMED_QUALIFIER_TYPE_NAME))
			return namedQualifier;
		else if(qualifiedName.equals(CDIConstants.ANY_QUALIFIER_TYPE_NAME))
			return anyQualifier;
		else if(qualifiedName.equals(CDIConstants.NEW_QUALIFIER_TYPE_NAME))
			return newQualifier;
		else{
			for(IQualifier q : qualifiers){
				if(q.getSourceType().getFullyQualifiedName().equals(qualifiedName))
					return q;
			}
		}
		return null;
	}

	@Override
	public IQualifier getQualifier(IPath path) {
		return null;
	}

	@Override
	public Set<String> getScopeNames() {
		return null;
	}

	@Override
	public IScope getScope(String qualifiedName) {
		return null;
	}

	@Override
	public IScope getScope(IPath path) {
		return null;
	}

	@Override
	public Set<IObserverMethod> resolveObserverMethods(
			IInjectionPoint injectionPoint) {
		return null;
	}

	@Override
	public Set<IInjectionPoint> findObservedEvents(
			IParameter observedEventParameter) {
		return null;
	}

	@Override
	public Set<IBean> resolve(Set<IBean> beans) {
		return null;
	}

	@Override
	public Set<IBeanMethod> resolveDisposers(IProducerMethod producer) {
		return null;
	}

	@Override
	public boolean isScope(IType annotationType) {
		return false;
	}

	@Override
	public boolean isNormalScope(IType annotationType) {
		return false;
	}

	@Override
	public boolean isPassivatingScope(IType annotationType) {
		return false;
	}

	@Override
	public boolean isQualifier(IType annotationType) {
		return false;
	}

	@Override
	public boolean isStereotype(IType annotationType) {
		return false;
	}

	@Override
	public List<INodeReference> getAlternativeClasses() {
		return null;
	}

	@Override
	public List<INodeReference> getAlternativeStereotypes() {
		return null;
	}

	@Override
	public List<INodeReference> getAlternatives(String fullyQualifiedTypeName) {
		return null;
	}

	@Override
	public List<INodeReference> getDecoratorClasses() {
		return null;
	}

	@Override
	public List<INodeReference> getDecoratorClasses(
			String fullyQualifiedTypeName) {
		return null;
	}

	@Override
	public List<INodeReference> getInterceptorClasses() {
		return null;
	}

	@Override
	public List<INodeReference> getInterceptorClasses(
			String fullyQualifiedTypeName) {
		return null;
	}

	@Override
	public Set<IInjectionPoint> getInjections(String fullyQualifiedTypeName) {
		return null;
	}

	@Override
	public CDICoreNature getNature() {
		return null;
	}

	@Override
	public void setNature(CDICoreNature n) {
		
	}

	@Override
	public void update(boolean updateDependent) {
	}

}
