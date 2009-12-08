package org.jboss.tools.cdi.internal.core.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;

public class StereotypeElement extends CDIElement implements IStereotype {
	AnnotationDefinition definition;
	protected AnnotationDeclaration named;
	protected AnnotationDeclaration alternative;

	public StereotypeElement() {}

	public void setDefinition(AnnotationDefinition definition) {
		this.definition = definition;
		setAnnotations(definition.getAnnotations());
	}
	
	protected void setAnnotations(List<AnnotationDeclaration> ds) {
		for (AnnotationDeclaration d: ds) {
			String typeName = d.getTypeName();
			if(CDIConstants.NAMED_QUALIFIER_TYPE_NAME.equals(typeName)) {
				named = d;
			} else if(CDIConstants.ALTERNATIVE_ANNOTATION_TYPE_NAME.equals(typeName)) {
				alternative = d;
			}
		}
	}

	public IAnnotationDeclaration getAlternativeDeclaration() {
		return alternative;
	}

	public Set<IInterceptorBindingDeclaration> getInterceptorBindingDeclarations() {
		// TODO 
		return new HashSet<IInterceptorBindingDeclaration>();
	}

	public IAnnotation getNameLocation() {
		return named != null ? named.getDeclaration() : null;
	}

	public IType getSourceType() {
		return definition.getType();
	}

	public Set<IStereotypeDeclaration> getStereotypeDeclarations() {
		Set<IStereotypeDeclaration> result = new HashSet<IStereotypeDeclaration>();
		for (AnnotationDeclaration d: definition.getAnnotations()) {
			if(d instanceof IStereotypeDeclaration) {
				result.add((IStereotypeDeclaration)d);
			}
		}
		return result;
	}

	public boolean isAlternative() {
		if(alternative != null) return true;
		return alternative != null;
	}

	public IType getScope() {
		Set<IAnnotationDeclaration> ss = getScopeDeclarations();
		if(!ss.isEmpty()) {
			return ss.iterator().next().getType();
		}
		Set<IStereotypeDeclaration> ds = getStereotypeDeclarations();
		for (IStereotypeDeclaration d: ds) {
			
		}
		return null;
	}

	public Set<IAnnotationDeclaration> getScopeDeclarations() {
		return ProducerField.getScopeDeclarations(getCDIProject().getNature(), definition.getAnnotations());
	}

}
