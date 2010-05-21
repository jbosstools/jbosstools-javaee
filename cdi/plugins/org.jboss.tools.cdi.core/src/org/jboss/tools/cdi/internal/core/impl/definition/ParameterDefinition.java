package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.tools.cdi.core.IAnnotated;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;
import org.jboss.tools.cdi.internal.core.impl.ParametedType;
import org.jboss.tools.common.text.ITextSourceReference;

public class ParameterDefinition implements IAnnotated {
	protected MethodDefinition methodDefinition;
	
	protected String name;
	protected ParametedType type;
	protected int index;

	protected ITextSourceReference position = null;
	Map<String, ITextSourceReference> annotationsByTypeName = new HashMap<String, ITextSourceReference>();

	public ParameterDefinition() {}

	public String getName() {
		return name;
	}

	public ParametedType getType() {
		return type;
	}

	public MethodDefinition getMethodDefinition() {
		return methodDefinition;
	}

	public IAnnotationDeclaration getAnnotation(String annotationTypeName) {
		return null;
	}

	public List<AnnotationDeclaration> getAnnotations() {
		return null;
	}

	public boolean isAnnotationPresent(String annotationTypeName) {
		return annotationsByTypeName.containsKey(annotationTypeName);
	}

	public ITextSourceReference getAnnotationPosition(String annotationTypeName) {
		return annotationsByTypeName.get(annotationTypeName);
	}

	public Set<String> getAnnotationTypes() {
		return annotationsByTypeName.keySet();
	}

	public void setPosition(ITextSourceReference position) {
		this.position = position;
	}

	public ITextSourceReference getPosition() {
		return position;
	}

	public String getAnnotationText(String annotationTypeName) {
		ITextSourceReference pos = getAnnotationPosition(annotationTypeName);
		if(pos == null) return null;
		String text = methodDefinition.getTypeDefinition().getContent().substring(pos.getStartPosition(), pos.getStartPosition() + pos.getLength());
		return text;
	}
}
