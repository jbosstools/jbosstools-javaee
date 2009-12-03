package org.jboss.tools.cdi.internal.core.impl;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

public class AnnotationDeclaration implements IAnnotationDeclaration {
	IAnnotation annotation;
	int startPosition = -1;
	int length = 0;	
	String annotationTypeName = null;

	public AnnotationDeclaration() {}

	public void setDeclaration(IAnnotation annotation, IType declaringType) {
		this.annotation = annotation;
		try {
			ISourceRange range = annotation.getSourceRange();
			if(range != null) {
				startPosition = range.getOffset();
				length = range.getLength();
			}
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		String name = annotation.getElementName();
		annotationTypeName = EclipseJavaUtil.resolveType(declaringType, name);
	}

	public IAnnotation getDeclaration() {
		return annotation;
	}

	public IMember getParentMember() {
		return (IMember)annotation.getParent();
	}

	public IType getType() {
		return getParentMember().getDeclaringType();
	}

	public int getLength() {
		return length;
	}

	public int getStartPosition() {
		return startPosition;
	}

}
