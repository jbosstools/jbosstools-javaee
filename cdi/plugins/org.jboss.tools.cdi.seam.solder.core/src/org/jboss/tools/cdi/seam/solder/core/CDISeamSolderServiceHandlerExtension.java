/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.solder.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.extension.AbstractDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.ClassBean;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.cdi.seam.solder.core.definition.InterfaceDefinition;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.model.XModelObject;

/**
 * Implements support for org.jboss.seam.solder.serviceHandler.ServiceHandlerExtension.
 * 
 * During building definitions
 * 		1) participates in recognizing annotations type annotated with @ServiceHandlerType and stores them;
 * 		2) builds and stores definitions annotated with those annotation types.
 * 
 * During building beans creates beans of stored definitions.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDISeamSolderServiceHandlerExtension implements ICDIExtension, IBuildParticipantFeature {
	ServiceHandlerDefinitionContext context = new ServiceHandlerDefinitionContext();

	protected Version getVersion() {
		return Version.instance;
	}

	public IDefinitionContextExtension getContext() {
		return context;
	}

	public void beginVisiting() {
	}

	public void visitJar(IPath path, IPackageFragmentRoot root, XModelObject beansXML) {
	}

	public void visit(IFile file, IPath src, IPath webinf) {
	}

	public void buildDefinitions() {
	}

	public void buildDefinitions(FileSet fileSet) {
		ServiceHandlerDefinitionContext workingCopy = (ServiceHandlerDefinitionContext)context.getWorkingCopy();
		
		Map<IPath, List<IType>> is = fileSet.getInterfaces();
		for (IPath path: is.keySet()) {
			for (IType t: is.get(path)) {
				InterfaceDefinition i = new InterfaceDefinition(t, workingCopy);
				List<IAnnotationDeclaration> as = i.getAnnotations();
				for (IAnnotationDeclaration a: as) {
					if(workingCopy.isServiceAnnotation(a.getType())) {
						TypeDefinition d = new TypeDefinition();
						d.setType(t, workingCopy.getRootContext(), 0);
						d.setBeanConstructor(true);
						workingCopy.addService(path, d);
					}
				}
			}
		}
	}

	public void buildBeans(CDIProject target) {
		CDIProject p = target;
		Map<String, TypeDefinition> services = context.getServices();
		for (TypeDefinition d: services.values()) {
			ClassBean b = new ClassBean();
			b.setDefinition(d);
			b.setParent(p);
			p.addBean(b);
		}

	}

	class ServiceHandlerDefinitionContext extends AbstractDefinitionContextExtension {
		Set<String> serviceAnnotations = new HashSet<String>();
		Map<String, TypeDefinition> services = new HashMap<String, TypeDefinition>();
	

		protected ServiceHandlerDefinitionContext copy(boolean clean) {
			ServiceHandlerDefinitionContext copy = new ServiceHandlerDefinitionContext();
			copy.root = root;
			if(!clean) {
				for (String qn: services.keySet()) {
					TypeDefinition d = services.get(qn);
					if(d.exists()) {
						copy.services.put(qn, d);
					}
				}
				copy.serviceAnnotations.addAll(serviceAnnotations);
			}

			return copy;
		}

		@Override
		protected void doApplyWorkingCopy() {
			services = ((ServiceHandlerDefinitionContext)workingCopy).services;
			serviceAnnotations = ((ServiceHandlerDefinitionContext)workingCopy).serviceAnnotations;
		}

		@Override
		public void clean() {
			services.clear();
			serviceAnnotations.clear();
		}

		@Override
		public void clean(String typeName) {
			services.remove(typeName);
			serviceAnnotations.remove(typeName);
		}

		public void addService(IPath path, TypeDefinition def) {
			String typeName = def.getType().getFullyQualifiedName();
			services.put(typeName, def);
			root.addType(path, typeName);
		}
		
		public Map<String, TypeDefinition> getServices() {
			return services;
		}

		@Override
		public void computeAnnotationKind(AnnotationDefinition annotation) {
			if(annotation.isAnnotationPresent(getVersion().getHandlerTypeAnnotationTypeName())) {
				annotation.setExtendedKind(CDISeamSolderConstants.SERVICE_ANNOTATION_KIND);
				serviceAnnotations.add(annotation.getType().getFullyQualifiedName());
			}
		}

		public boolean isServiceAnnotation(IType type) {
			return (type != null && serviceAnnotations.contains(type.getFullyQualifiedName()));
		}
	}
}