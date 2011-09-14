package org.jboss.tools.cdi.internal.core.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.IScopeDeclaration;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.java.ITypeDeclaration;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * 10.3.2. The built-in Event
 * The container must provide a built-in bean with:
 * Event<X> in its set of bean types, for every Java type X that does not contain a type variable,
 * every event qualifier type in its set of qualifier types,
 * scope @ Dependent,
 * no bean EL name
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class EventBean extends CDIElement implements IBean {
	IParametedType type;
	IInjectionPoint point = null;
	Set<IQualifier> qualifiers = null;
	
	public EventBean(IParametedType type, IInjectionPoint point) {
		this.type = type;
		this.point = point;
	}

	public IScope getScope() {
		return getCDIProject().getScope(CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME);
	}

	public Set<IScopeDeclaration> getScopeDeclarations() {
		return new HashSet<IScopeDeclaration>();
	}

	public Set<IStereotypeDeclaration> getStereotypeDeclarations() {
		return new HashSet<IStereotypeDeclaration>();
	}

	public List<IAnnotationDeclaration> getAnnotations() {
		return new ArrayList<IAnnotationDeclaration>();
	}

	public IAnnotationDeclaration getAnnotation(String annotationTypeName) {
		return null;
	}

	public ITextSourceReference getAnnotationPosition(String annotationTypeName) {
		return null;
	}

	public boolean isAnnotationPresent(String annotationTypeName) {
		return false;
	}

	public IType getBeanClass() {
		return type.getType();
	}

	public String getName() {
		return null;
	}

	public ITextSourceReference getNameLocation(boolean stereotypeLocation) {
		return null;
	}

	public Set<IParametedType> getLegalTypes() {
		return getAllTypes();
	}

	public Set<IParametedType> getAllTypes() {
		Set<IParametedType> result = new HashSet<IParametedType>();
		result.add(type);
		return result;
	}

	public Set<ITypeDeclaration> getAllTypeDeclarations() {
		return new HashSet<ITypeDeclaration>();
	}

	public Set<ITypeDeclaration> getRestrictedTypeDeclaratios() {
		return new HashSet<ITypeDeclaration>();
	}

	public Set<IQualifierDeclaration> getQualifierDeclarations() {
		return new HashSet<IQualifierDeclaration>();
	}

	public Set<IQualifierDeclaration> getQualifierDeclarations(boolean includeInherited) {
		return new HashSet<IQualifierDeclaration>();
	}

	public Set<IQualifier> getQualifiers() {
		if(qualifiers == null) {
			computeQualifiers();
		}
		return qualifiers;
	}
	
	void computeQualifiers() {
		Set<IQualifier> qs = new HashSet<IQualifier>();
		
		boolean isParameter = point instanceof InjectionPointParameter;
		
		if(isParameter) {
			qs = ((InjectionPointParameter)point).getQualifiers();
		} else if(point != null) {
			Set<IQualifierDeclaration> ds = point.getQualifierDeclarations();
			for (IQualifierDeclaration d: ds) {
				IQualifier q = d.getQualifier();
				if(q != null) qs.add(q);
			}
		}
		
		qualifiers = qs;
	}

	public boolean isAlternative() {
		return false;
	}

	public boolean isSelectedAlternative() {
		return false;
	}

	public IAnnotationDeclaration getAlternativeDeclaration() {
		return null;
	}

	public Set<IInjectionPoint> getInjectionPoints() {
		return new HashSet<IInjectionPoint>();
	}

	public IBean getSpecializedBean() {
		return null;
	}

	public IAnnotationDeclaration getSpecializesAnnotationDeclaration() {
		return null;
	}

	public boolean isSpecializing() {
		return false;
	}

	public boolean isDependent() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isNullable() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IBean#getSimpleJavaName()
	 */
	@Override
	public String getElementName() {
		if(type!=null) {
			return type.getSimpleName();
		}
		return "";
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub
		
	}
}