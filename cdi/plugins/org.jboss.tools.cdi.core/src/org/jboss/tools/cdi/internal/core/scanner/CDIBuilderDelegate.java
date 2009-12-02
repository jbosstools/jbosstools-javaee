package org.jboss.tools.cdi.internal.core.scanner;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.ICDIBuilderDelegate;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.common.model.util.EclipseJavaUtil;

public class CDIBuilderDelegate implements ICDIBuilderDelegate {

	public int computeRelevance(IProject project) {
		//nothing to compute, builder works only if cdi nature is present
		return 1;
	}

	public String getID() {
		return getClass().getName();
	}

	public Class<? extends ICDIProject> getProjectImplementationClass() {
		return CDIProject.class;
	}

	public void build(IFile file, CDICoreNature projectNature) {
		IProject project = projectNature.getProject();
		

	}

	public void build(IFile file, ICompilationUnit unit, CDICoreNature projectNature) {
		if(unit != null) {
			try {
				IType[] types = unit.getTypes();
				if(types != null) {
					for (IType type: types) {
						if(type.isAnnotation()) {
							IAnnotation[] as = type.getAnnotations();
							for (IAnnotation a: as) {
								String name = a.getElementName();
								String qName = EclipseJavaUtil.resolveType(type, name);
								System.out.println(qName);
							}
						}
					}
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		} else {
			
		}
	}
}
