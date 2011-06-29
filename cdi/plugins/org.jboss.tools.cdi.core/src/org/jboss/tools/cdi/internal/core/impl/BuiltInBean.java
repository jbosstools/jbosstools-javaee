package org.jboss.tools.cdi.internal.core.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.IScopeDeclaration;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.java.ITypeDeclaration;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * 3.6. Additional built-in beans.

 * scope @ Dependent,
 * no bean EL name
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BuiltInBean extends CDIElement implements IBean {
	protected IParametedType type;
	protected Set<IQualifier> qualifiers = null;
	
	public BuiltInBean(IParametedType type) {
		this.type = type;
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

	public ITextSourceReference getNameLocation() {
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
	
	protected void computeQualifiers() {
		qualifiers = new HashSet<IQualifier>();
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
	public String getSimpleJavaName() {
		if(type!=null) {
			return type.getSimpleName();
		}
		return "";
	}
}