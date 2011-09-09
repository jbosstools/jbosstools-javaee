package org.jboss.tools.cdi.ui.test.testmodel;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.text.ITextSourceReference;

public class CDIQualifier implements IQualifier{
	private ICDIProject project;
	private CDIClass cdiClass;
	
	public CDIQualifier(ICDIProject project, String qualifiedName){
		this.project = project;
		this.cdiClass = new CDIClass(qualifiedName);
	}

	@Override
	public IType getSourceType() {
		return cdiClass;
	}

	@Override
	public IAnnotationDeclaration getInheritedDeclaration() {
		return null;
	}

	@Override
	public List<IAnnotationDeclaration> getAnnotationDeclarations() {
		return null;
	}

	@Override
	public IAnnotationDeclaration getAnnotationDeclaration(String typeName) {
		return null;
	}

	@Override
	public Set<IMethod> getNonBindingMethods() {
		return null;
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
	public ICDIProject getDeclaringProject() {
		return null;
	}

	@Override
	public boolean exists() {
		return false;
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
	public ITextSourceReference getAnnotationPosition(String annotationTypeName) {
		return null;
	}

	@Override
	public boolean isAnnotationPresent(String annotationTypeName) {
		return false;
	}
}