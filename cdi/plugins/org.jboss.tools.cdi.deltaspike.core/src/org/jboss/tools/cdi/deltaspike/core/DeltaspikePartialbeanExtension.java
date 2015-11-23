/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.deltaspike.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedTypeFeature;
import org.jboss.tools.cdi.core.extension.feature.IValidatorFeature;
import org.jboss.tools.cdi.deltaspike.core.validation.DeltaspikeValidationMessages;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractTypeDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.DefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.cdi.internal.core.validation.CDICoreValidator;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.preferences.SeverityPreferences;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@SuppressWarnings("restriction")
public class DeltaspikePartialbeanExtension implements ICDIExtension, IBuildParticipantFeature, IProcessAnnotatedTypeFeature, IValidatorFeature, DeltaspikeConstants {
	public static String ID = "org.apache.deltaspike.partialbean.impl.PartialBeanBindingExtension"; //$NON-NLS-1$
	DeltaspikePartialbeanDefinitionContext context = new DeltaspikePartialbeanDefinitionContext();

	public static DeltaspikePartialbeanExtension getExtension(CDICoreNature project) {
		return (DeltaspikePartialbeanExtension)project.getExtensionManager().getExtensionByRuntime(ID);
	}

	public DeltaspikePartialbeanExtension() {}

	public DeltaspikePartialbeanDefinitionContext getContext() {
		return context;
	}
	
	@Override
	public void processAnnotatedType(TypeDefinition typeDefinition,
			IRootDefinitionContext context) {
		//we cannot process here interfaces 
		//but can process abstract classes
		IAnnotationDeclaration d = findAnnotationAnnotatedWithPartialBeanBindingType(typeDefinition, context);
		if(d != null) {
			DeltaspikePartialbeanDefinitionContext contextCopy = (DeltaspikePartialbeanDefinitionContext)this.context.getWorkingCopy();
			DeltaspikePartialbeanBindingConfiguration c = contextCopy.getConfiguration(d.getTypeName());
			if(typeDefinition.isAbstract()) {
				typeDefinition.setBeanConstructor(true);
				c.addPartialBean(typeDefinition);
			} else if(isImplementingInvocationHandler(typeDefinition)) {
				c.addInvocationHandler(typeDefinition);
			} else {
				c.addInvalidPartialBean(typeDefinition);
			}
			addToDependencies(c, typeDefinition, context);
		}		
	}

	@Override
	public void beginVisiting() {
	}

	@Override
	public void visitJar(IPath path, IPackageFragmentRoot root,	XModelObject beansXML) {
	}

	@Override
	public void visit(IFile file, IPath src, IPath webinf) {
	}

	@Override
	public void buildDefinitions() {
	}

	@Override
	public void buildDefinitions(FileSet fileSet) {
		DeltaspikePartialbeanDefinitionContext workingCopy = (DeltaspikePartialbeanDefinitionContext)context.getWorkingCopy();
		Map<IPath, List<IType>> is = fileSet.getInterfaces();
		for (IPath p: is.keySet()) {
			for (IType type: is.get(p)) {
				InterfaceDefinition def = new InterfaceDefinition(type, workingCopy);
				IAnnotationDeclaration d = findAnnotationAnnotatedWithPartialBeanBindingType(def, workingCopy.getRootContext());
				if(d != null) {
					TypeDefinition typeDefinition = new TypeDefinition();
					typeDefinition.setType(type, workingCopy.getRootContext(), 0);
					typeDefinition.setBeanConstructor(true);
					DeltaspikePartialbeanBindingConfiguration c = workingCopy.getConfiguration(d.getTypeName());
					c.addPartialBean(typeDefinition);
					((DefinitionContext)context.getRootContext().getWorkingCopy()).addType(type.getPath(), type.getFullyQualifiedName(), typeDefinition);
					addToDependencies(c, typeDefinition, workingCopy.getRootContext());
				}
			}
		}
	}

	@Override
	public void buildBeans(CDIProject target) {
	}

	@Override
	public void validateResource(IFile file, CDICoreValidator validator) {
		IPath path = file.getFullPath();
		for (DeltaspikePartialbeanBindingConfiguration c: context.partialbeanBindingConfigurations.values()) {
			if(c.getInvolvedTypes().contains(path)) {
				for (TypeDefinition def: c.getInvocationHandlers().values()) {
					if(file.equals(def.getResource())) {
						reportIgnoredBindings(file, validator, def);
						IAnnotationDeclaration dc = findAnnotationAnnotatedWithPartialBeanBindingType(def, context.getRootContext());
						if(c.getInvocationHandlers().size() > 1) {
							validator.addError(DeltaspikeValidationMessages.MULTIPLE_PARTIAL_BEAN_HANDLERS, 
									DeltaspikeSeverityPreferences.MULTIPLE_PARTIAL_BEAN_HANDLERS,  
									new String[]{dc.getTypeName()}, 
									dc, file);
						}
						IClassBean cb = context.getRootContext().getProject().getDelegate().getBeanClass(def.getType());
						if(cb != null && !cb.getScope().isNorlmalScope()) {
							validator.addError(DeltaspikeValidationMessages.INVALID_PARTIAL_BEAN_HANDLER, 
									DeltaspikeSeverityPreferences.INVALID_PARTIAL_BEAN_HANDLER,  
									new String[]{}, 
									dc, file);
						}
					}
				}
				for (TypeDefinition def: c.getPartialBeans().values()) {
					if(file.equals(def.getResource())) {
						reportIgnoredBindings(file, validator, def);
						IAnnotationDeclaration dc = findAnnotationAnnotatedWithPartialBeanBindingType(def, context.getRootContext());
						if(c.getInvocationHandlers().isEmpty()) {
							validator.addError(DeltaspikeValidationMessages.MISSING_PARTIAL_BEAN_HANDLER, 
									DeltaspikeSeverityPreferences.MISSING_PARTIAL_BEAN_HANDLER,  
									new String[]{def.getQualifiedName(), dc.getTypeName()}, 
									dc, file);
						}
					}
				}
				for (TypeDefinition def: c.getInvalidPartialBeans().values()) {
					if(file.equals(def.getResource())) {
						reportIgnoredBindings(file, validator, def);
						IAnnotationDeclaration dc = findAnnotationAnnotatedWithPartialBeanBindingType(def, context.getRootContext());
						validator.addError(DeltaspikeValidationMessages.ILLEGAL_PARTIAL_BEAN, 
								DeltaspikeSeverityPreferences.ILLEGAL_PARTIAL_BEAN,  
								new String[]{dc.getTypeName()}, 
								dc, file);
					}
				}
			}
		}		
	}

	private void reportIgnoredBindings(IFile file, CDICoreValidator validator, TypeDefinition def) {
		List<IAnnotationDeclaration> dcs = getAllAnnotationsAnnotatedWithPartialBeanBindingType(def, context.getRootContext());
		if(dcs.size() > 1) {
			IAnnotationDeclaration d0 = dcs.remove(0);
			for (IAnnotationDeclaration dc: dcs) {
				validator.addError(DeltaspikeValidationMessages.MULTIPLE_PARTIAL_BEAN_BINDINGS, 
						DeltaspikeSeverityPreferences.MULTIPLE_PARTIAL_BEAN_BINDINGS,  
						new String[]{dc.getTypeName(), d0.getTypeName()}, 
						dc, file);
			}
		}
		
	}

	@Override
	public SeverityPreferences getSeverityPreferences() {
		return DeltaspikeSeverityPreferences.getInstance();
	}

	private IAnnotationDeclaration findAnnotationAnnotatedWithPartialBeanBindingType(AbstractTypeDefinition t, IRootDefinitionContext context) {
		for (IAnnotationDeclaration d: t.getAnnotations()) {
			if(d.getTypeName() != null) {
				AnnotationDefinition a = context.getAnnotation(d.getTypeName());
				if(a != null && a.isAnnotationPresent(PARTIALBEAN_BINDING_ANNOTATION_TYPE_NAME)) {
					return d;
				}
			}
		}
		return null;
	}

	private List<IAnnotationDeclaration> getAllAnnotationsAnnotatedWithPartialBeanBindingType(AbstractTypeDefinition t, IRootDefinitionContext context) {
		List<IAnnotationDeclaration> result = new ArrayList<IAnnotationDeclaration>();
		for (IAnnotationDeclaration d: t.getAnnotations()) {
			if(d.getTypeName() != null) {
				AnnotationDefinition a = context.getAnnotation(d.getTypeName());
				if(a != null && a.isAnnotationPresent(PARTIALBEAN_BINDING_ANNOTATION_TYPE_NAME)) {
					result.add(d);
				}
			}
		}
		return result;
	}

	private boolean isImplementingInvocationHandler(TypeDefinition typeDefinition) {
		for (IParametedType t: typeDefinition.getAllTypes()) {
			IType type = t.getType();
			if(type != null && INVOCATION_HANDLER_TYPE.equals(type.getFullyQualifiedName())) {
				return true;
			}
		}
		
		return false;
	}

	private void addToDependencies(DeltaspikePartialbeanBindingConfiguration c, AbstractMemberDefinition def, IRootDefinitionContext context) {
		IResource r = def.getResource();
		if(r != null && r.exists() && !c.getInvolvedTypes().contains(r.getFullPath())) {
			IPath newPath = r.getFullPath();
			Set<IPath> ps = c.getInvolvedTypes();
			for (IPath p: ps) {
				context.addDependency(p, newPath);
				context.addDependency(newPath, p);
			}
			ps.add(newPath);				
		}
	}

}
