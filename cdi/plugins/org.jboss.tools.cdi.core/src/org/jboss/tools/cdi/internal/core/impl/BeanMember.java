package org.jboss.tools.cdi.internal.core.impl;

import java.util.List;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBeanMember;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.ITypeDeclaration;
import org.jboss.tools.common.model.util.EclipseJavaUtil;

public abstract class BeanMember extends CDIElement implements IBeanMember {
	protected IClassBean classBean;
	protected ITypeDeclaration typeDeclaration;
	protected AnnotationDeclaration named;
	protected AnnotationDeclaration alternative;
	protected AnnotationDeclaration specializes;
	protected AnnotationDeclaration typed;

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

	protected void setAnnotations(List<AnnotationDeclaration> ds) {
		for (AnnotationDeclaration d: ds) {
			String typeName = d.getTypeName();
			if(CDIConstants.NAMED_QUALIFIER_TYPE_NAME.equals(typeName)) {
				named = d;
			} else if(CDIConstants.ALTERNATIVE_ANNOTATION_TYPE_NAME.equals(typeName)) {
				alternative = d;
			} else if(CDIConstants.SPECIALIZES_ANNOTATION_TYPE_NAME.equals(typeName)) {
				specializes = d;
			} else if(CDIConstants.TYPED_ANNOTATION_TYPE_NAME.equals(typeName)) {
				typed = d;
			}
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
