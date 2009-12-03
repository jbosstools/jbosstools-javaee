package org.jboss.tools.cdi.internal.core.scanner;

import java.util.Map;

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

	public void build(FileSet fileSet, CDICoreNature projectNature) {
		Map<IFile, ICompilationUnit> as = fileSet.getAnnotations();
		for (IFile f: as.keySet()) {
			
		}
		
		Map<IFile, ICompilationUnit> is = fileSet.getInterfaces();
		for (IFile f: is.keySet()) {
			
		}
		
		Map<IFile, ICompilationUnit> cs = fileSet.getClasses();
		for (IFile f: cs.keySet()) {
			
		}
		
	}
}
