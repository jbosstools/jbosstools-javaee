package org.jboss.tools.cdi.internal.core.impl.definition;

import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;

public class BeanMemberDefinition extends AbstractMemberDefinition {
	AbstractTypeDefinition typeDefinition;

	public BeanMemberDefinition() {}

	public void setTypeDefinition(AbstractTypeDefinition typeDefinition) {
		this.typeDefinition = typeDefinition;
	}

	public AbstractTypeDefinition getTypeDefinition() {
		return typeDefinition;
	}

	public boolean isCDIAnnotated() {
		return getInjectAnnotation() != null || getProducesAnnotation() != null;
	}

	public AnnotationDeclaration getProducesAnnotation() {
		return annotationsByType.get(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME);
	}

	public AnnotationDeclaration getInjectAnnotation() {
		return annotationsByType.get(CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
	}

	public AnnotationDeclaration getDelegateAnnotation() {
		return annotationsByType.get(CDIConstants.DELEGATE_STEREOTYPE_TYPE_NAME);
	}

}
