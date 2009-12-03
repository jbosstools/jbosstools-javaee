package org.jboss.tools.cdi.internal.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDICorePlugin;

public class AnnotatedTypeDeclaration {
	List<AnnotationDeclaration> annotations = new ArrayList<AnnotationDeclaration>();
	String qualifiedName;
	IType type;

	public AnnotatedTypeDeclaration() {
	}

	public void setType(IType type) {
		this.type = type;
		try {
			init();
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	void init() throws CoreException {
		qualifiedName = type.getFullyQualifiedName();
		IAnnotation[] ts = type.getAnnotations();
		for (int i = 0; i < annotations.size(); i++) {
			AnnotationDeclaration a = new AnnotationDeclaration();
			a.setDeclaration(ts[i], type);
			annotations.add(a);
		}
	}
}
