package org.jboss.tools.cdi.internal.core.impl.definition;

import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;

public class BeanMemberDefinition extends AbstractMemberDefinition {

	public BeanMemberDefinition() {}

	public boolean isCDIAnnotated() {
		return getInjectAnnotation() != null || getProducesAnnotation() != null;
	}

	public AnnotationDeclaration getProducesAnnotation() {
		return annotationsByType.get(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME);
	}

	public AnnotationDeclaration getInjectAnnotation() {
		return annotationsByType.get(CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
	}

}
