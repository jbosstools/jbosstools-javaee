package org.jboss.tools.cdi.internal.core.scanner;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.ICDIBuilderDelegate;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.definition.BeansXMLDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.DefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.common.model.XModelObject;

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
		DefinitionContext context = projectNature.getDefinitions().getWorkingCopy();
		Set<IPath> ps = fileSet.getAllPaths();
		for (IPath p: ps) context.clean(p);
		Map<IPath, Set<IType>> as = fileSet.getAnnotations();
		for (IPath f: as.keySet()) {
			Set<IType> ts = as.get(f);
			for (IType type: ts) {
				//this builds annotation definition
				context.getAnnotationKind(type);
			}
		}
		
		Map<IPath, Set<IType>> is = fileSet.getInterfaces();
		for (IPath f: is.keySet()) {
			//TODO
		}
		
		Map<IPath, Set<IType>> cs = fileSet.getClasses();
		for (IPath f: cs.keySet()) {
			Set<IType> ts = cs.get(f);
			for (IType type: ts) {
				TypeDefinition def = new TypeDefinition();
				def.setType(type, context);
				context.addType(f, type.getFullyQualifiedName(), def);
			}
		}

		for (IPath f: ps) {
			XModelObject beansXML = fileSet.getBeanXML(f);
			if(beansXML == null) continue;
			
			BeansXMLDefinition def = new BeansXMLDefinition();
			def.setPath(f);
			def.setBeansXML(beansXML);
			
			context.addBeanXML(f, def);			
		}
		
	}

}
