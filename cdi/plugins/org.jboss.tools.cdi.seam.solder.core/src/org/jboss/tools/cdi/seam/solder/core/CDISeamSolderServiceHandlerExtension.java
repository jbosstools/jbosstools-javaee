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
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.extension.AbstractDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.ClassBean;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.common.model.XModelObject;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDISeamSolderServiceHandlerExtension implements ICDIExtension, IBuildParticipantFeature {
	CDICoreNature project;
	ServiceHandlerDefinitionContext context = new ServiceHandlerDefinitionContext();

	public Object getAdapter(Class adapter) {
		return null;
	}

	public void setProject(CDICoreNature n) {
		project = n;
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
		
		Map<IPath, Set<IType>> is = fileSet.getInterfaces();
		for (IPath path: is.keySet()) {
			Set<IType> ts = is.get(path);
			for (IType t: ts) {
				InterfaceDefinition i = new InterfaceDefinition(t);
				List<IAnnotationDeclaration> as = i.getAnnotations();
				for (IAnnotationDeclaration a: as) {
					if(workingCopy.isServiceAnnotation(a.getType())) {
						TypeDefinition d = new TypeDefinition();
						d.setType(t, workingCopy.getRootContext());
						workingCopy.addService(path, d);
					}
				}
			}
		}
	}

	public void buildBeans() {
		CDIProject p = ((CDIProject)project.getDelegate());
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
				copy.services.putAll(services);
				copy.serviceAnnotations.addAll(serviceAnnotations);
			}

			return copy;
		}

		protected void doApplyWorkingCopy() {
			services = ((ServiceHandlerDefinitionContext)workingCopy).services;
			serviceAnnotations = ((ServiceHandlerDefinitionContext)workingCopy).serviceAnnotations;
		}
		

		public void clean() {
			services.clear();
			serviceAnnotations.clear();
		}

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
		
		public void computeAnnotationKind(AnnotationDefinition annotation) {
			if(annotation.isAnnotationPresent(CDISeamSolderConstants.SERVICE_HANDLER_TYPE_ANNOTATION_TYPE_NAME)) {
				annotation.setExtendedKind(CDISeamSolderConstants.SERVICE_ANNOTATION_KIND);
				serviceAnnotations.add(annotation.getType().getFullyQualifiedName());
			}
		}
	
		public boolean isServiceAnnotation(IType type) {
			return (type != null && serviceAnnotations.contains(type.getFullyQualifiedName()));
		}

	}

	class InterfaceDefinition extends AbstractMemberDefinition {
		InterfaceDefinition(IType type) {
			setAnnotatable(type, type, context.getRootContext());
		}
	}

}
