package org.jboss.tools.cdi.internal.core.impl;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBeanMember;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.ITypeDeclaration;
import org.jboss.tools.common.model.util.EclipseJavaUtil;

public abstract class BeanMember extends AbstractBeanElement implements IBeanMember {
	protected IClassBean classBean;
	protected ITypeDeclaration typeDeclaration;

	public BeanMember() {}

	protected void setMember(IMember member) {
		try {
			String returnType = EclipseJavaUtil.getMemberTypeAsString(member);
			if(returnType != null) {
				IType t = EclipseJavaUtil.findType(member.getJavaProject(), returnType);
				if(t != null) {
					typeDeclaration = new TypeDeclaration(t, -1, 0);
				}
			}
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	public IClassBean getClassBean() {
		return classBean;
	}

	public void setClassBean(IClassBean classBean) {
		this.classBean = classBean;
	}

	public int getLength() {
		ISourceRange r = null;
		try {
			getSourceMember().getSourceRange();
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return r == null ? 0 : r.getLength();
	}

	public int getStartPosition() {
		ISourceRange r = null;
		try {
			getSourceMember().getSourceRange();
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return r == null ? 0 : r.getOffset();
	}

}
