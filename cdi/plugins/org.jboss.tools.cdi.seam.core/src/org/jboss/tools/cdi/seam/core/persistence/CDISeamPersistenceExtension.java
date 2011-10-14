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
package org.jboss.tools.cdi.seam.core.persistence;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.extension.AbstractDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.internal.core.impl.BeanMember;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.ClassBean;
import org.jboss.tools.cdi.internal.core.impl.definition.FieldDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.cdi.seam.core.CDISeamCorePlugin;
import org.jboss.tools.common.java.IAnnotated;
import org.jboss.tools.common.java.ParametedType;
import org.jboss.tools.common.java.TypeDeclaration;
import org.jboss.tools.common.model.XModelObject;

/**
 * 
 * @author Viacheslav Kabanlvich
 *
 */
public class CDISeamPersistenceExtension implements ICDIExtension, IBuildParticipantFeature {
	CDISeamPersistenceDefinitionContext context = new CDISeamPersistenceDefinitionContext();

	@Override
	public IDefinitionContextExtension getContext() {
		return context;
	}

	@Override
	public void beginVisiting() {
	}

	@Override
	public void visitJar(IPath path, IPackageFragmentRoot root,
			XModelObject beansXML) {
	}

	@Override
	public void visit(IFile file, IPath src, IPath webinf) {
	}

	@Override
	public void buildDefinitions() {
	}

	@Override
	public void buildDefinitions(FileSet fileSet) {
	}

	@Override
	public void buildBeans(CDIProject target) {
		if(target.getNature() != context.getRootContext().getProject()) {
			//because we getAll type definitions
			return;
		}
		List<TypeDefinition> definitions = target.getNature().getAllTypeDefinitions();
		if(definitions.isEmpty()) {
			//no beans to build
			return;
		}
		ParametedType entityManager = getType(CDIPersistenceConstants.ENTITY_MANAGER_TYPE_NAME, target);
		ParametedType entityManagerFactory = getType(CDIPersistenceConstants.ENTITY_MANAGER_FACTORY_TYPE_NAME, target);
		ParametedType session = getType(CDIPersistenceConstants.SESSION_TYPE_NAME, target);
		ParametedType sessionFactory = getType(CDIPersistenceConstants.SESSION_FACTORY_TYPE_NAME, target);
		if(entityManager == null && session == null) {
			return;
		}

		CDIProject cdi = target;
		
		for (TypeDefinition def: definitions) {
			if(def.isVetoed() || !isArtifact(def)) {
				continue;
			}
			ClassBean bean = new ClassBean();
			bean.setParent(cdi);
			bean.setDefinition(def);
			Set<IProducer> ps = bean.getProducers();
			for (IProducer p: ps) {
				if(isArtefact(p)) {
					BeanMember m = (BeanMember)p;
					TypeDeclaration d = m.getTypeDeclaration();
					
					ParametedType substitute = null;
					if(entityManagerFactory != null && entityManager != null && d.getType().equals(entityManagerFactory.getType())) {
						substitute = entityManager;
					} else if(sessionFactory != null && session != null && d.getType().equals(sessionFactory.getType())) {
						substitute = session;
					}
					
					if(substitute != null) {
						d = new TypeDeclaration(substitute, d.getResource(), d.getStartPosition(), d.getLength());
						m.setTypeDeclaration(d);
						cdi.addBean(p);
					}
				}
			}
		}
	}

	boolean isArtifact(TypeDefinition typeDefinition) {
		List<FieldDefinition> fs = typeDefinition.getFields();
		for (FieldDefinition f: fs) {
			if(isArtefact(f)) {
				return true;
			}
		}
		List<MethodDefinition> ms = typeDefinition.getMethods();
		for (MethodDefinition m: ms) {
			if(isArtefact(m)) {
				return true;
			}
		}
		
		return false;
	}

	boolean isArtefact(IAnnotated m) {
		return (m instanceof IProducer || m.isAnnotationPresent(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME))
				&& (m.isAnnotationPresent(CDIPersistenceConstants.EXTENSION_MANAGED_ANNOTATION_TYPE_NAME)
					|| m.isAnnotationPresent(CDIPersistenceConstants.EXTENSION_MANAGED_ANNOTATION_TYPE_NAME_30));
	}

	private ParametedType getType(String name, CDIProject project) {
		IType t = project.getNature().getType(name);
		if(t == null) {
			return null;
		}
		try {
			return project.getNature().getTypeFactory().getParametedType(t, "L" + name + ";");
		} catch (JavaModelException e) {
			CDISeamCorePlugin.getDefault().logError(e);
			return null;
		}
	}
}

class CDISeamPersistenceDefinitionContext extends AbstractDefinitionContextExtension {

	public CDISeamPersistenceDefinitionContext() {}

	@Override
	protected AbstractDefinitionContextExtension copy(boolean clean) {
		CDISeamPersistenceDefinitionContext copy = new CDISeamPersistenceDefinitionContext();
		copy.root = root;
		if(!clean) {
		}
		return copy;
	}

	protected void doApplyWorkingCopy() {
	}
}
