/*******************************************************************************
 * Copyright (c) 2010, 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.internal.core.scanner;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDICoreBuilder;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIBuilderDelegate;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.definition.BeansXMLDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.DefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.definition.PackageDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.kb.internal.KbBuilder;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
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

	public void build(IFileSet fileSet, CDICoreNature projectNature) {
		build(fileSet, projectNature, null);
	}

	public void build(IFileSet fileSet, CDICoreNature projectNature, IProgressMonitor monitor) {
		DefinitionContext context = projectNature.getDefinitions().getWorkingCopy();
		build(fileSet, context, monitor);
	}

	public void build(IFileSet fileSet, DefinitionContext context) {
		build(fileSet, context, null);
	}

	public void build(IFileSet fileSet, DefinitionContext context, IProgressMonitor monitor) {
		Set<IPath> ps = fileSet.getAllPaths();
		for (IPath p: ps) context.clean(p);
		Map<IPath, List<IType>> as = fileSet.getAnnotations();
		for (IPath f: as.keySet()) {
			for (IType type: as.get(f)) {
				//this builds annotation definition
				context.getAnnotationKind(type);
			}
		}
		
		Map<IPath, List<IType>> is = fileSet.getInterfaces();
		for (IPath f: is.keySet()) {
			for (IType type: is.get(f)) {
				// Jars present package-info as binary interface 
				// whereas sources present it as compilation unit with package declaration. 
				if(type.getElementName().equals("package-info")) {
					KbBuilder.checkCanceled(monitor);
					PackageDefinition def = new PackageDefinition();
					def.setBinaryType(type, context);
					context.addPackage(f, def.getQualifiedName(), def);
				}
			}
		}
		
		Map<IPath, List<IType>> cs = fileSet.getClasses();
		for (IPath f: cs.keySet()) {
			for (IType type: cs.get(f)) {
				KbBuilder.checkCanceled(monitor);
				TypeDefinition def = new TypeDefinition();
				def.setType(type, context, 0);
				context.addType(f, type.getFullyQualifiedName(), def);
			}
		}

		Map<IPath, IPackageDeclaration> pkgs = fileSet.getPackages();
		for (IPath f: pkgs.keySet()) {
			KbBuilder.checkCanceled(monitor);
			IPackageDeclaration pkg = pkgs.get(f);
			PackageDefinition def = new PackageDefinition();
			def.setPackage(pkg, context);
			context.addPackage(f, def.getQualifiedName(), def);
			IResource res = pkg.getResource();
			if(CDICoreBuilder.isPackageInfo(res)) {
				IResource[] ms = new IResource[0];
				try {
					ms = res.getParent().members();
				} catch (CoreException e) {
					CDICorePlugin.getDefault().logError(e);
				}
				for (IResource m: ms) {
					if(m instanceof IFile && !m.getName().equals(CDICoreBuilder.PACKAGE_INFO)) {
						context.addDependency(f, m.getFullPath());
					}
				}
				
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
