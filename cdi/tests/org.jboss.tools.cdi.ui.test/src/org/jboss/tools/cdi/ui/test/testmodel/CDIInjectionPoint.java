package org.jboss.tools.cdi.ui.test.testmodel;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IMember;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.text.ITextSourceReference;

public class CDIInjectionPoint implements IInjectionPoint {
	private ICDIProject project;
	private IClassBean bean;
	
	public CDIInjectionPoint(ICDIProject project, IClassBean bean){
		this.project = project;
		this.bean = bean;
	}

	@Override
	public ICDIProject getCDIProject() {
		return project;
	}

	@Override
	public ICDIProject getDeclaringProject() {
		return project;
	}

	@Override
	public IPath getSourcePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IClassBean getClassBean() {
		return bean;
	}

	@Override
	public IParametedType getMemberType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMember getSourceMember() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStartPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<IAnnotationDeclaration> getAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAnnotationDeclaration getAnnotation(String annotationTypeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITextSourceReference getAnnotationPosition(String annotationTypeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAnnotationPresent(String annotationTypeName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IParametedType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IQualifierDeclaration> getQualifierDeclarations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasDefaultQualifier() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDelegate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ITextSourceReference getDelegateAnnotation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAnnotationDeclaration getInjectAnnotation() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return null;
	}
}
