package org.jboss.tools.cdi.internal.core.scanner;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIBuilderDelegate;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.definition.DefinitionContext;

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
		//TODO get context from projectNature 
		DefinitionContext context = new DefinitionContext();
		context.setProject(projectNature);
		Map<IFile, ICompilationUnit> as = fileSet.getAnnotations();
		for (IFile f: as.keySet()) {
			ICompilationUnit u = as.get(f);
			IType[] ts = null;
			try {
				ts = u.getTypes();
				if(ts != null) for (int i = 0; i < ts.length; i++) {
					if(ts[i].isAnnotation()) {
						//this builds annotation definition
						context.getAnnotationKind(ts[i]);
					}
				}
			} catch (CoreException e) {
				CDICorePlugin.getDefault().logError(e);
			}
		}
		
		Map<IFile, ICompilationUnit> is = fileSet.getInterfaces();
		for (IFile f: is.keySet()) {
			
		}
		
		Map<IFile, ICompilationUnit> cs = fileSet.getClasses();
		for (IFile f: cs.keySet()) {
			
		}
		
	}
}
