/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedMemberFeature;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedTypeFeature;
import org.jboss.tools.cdi.core.extension.feature.IValidatorFeature;
import org.jboss.tools.cdi.deltaspike.core.validation.DeltaspikeValidationMessages;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.BeanMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.cdi.internal.core.validation.CDICoreValidator;
import org.jboss.tools.common.java.IAnnotated;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.preferences.SeverityPreferences;

/**
 * Runtime
 * org.apache.deltaspike.security.impl.authorization.SecurityExtension
 * 
 * @author Viacheslav Kabanovich
 */
public class DeltaspikeSecurityExtension implements ICDIExtension, IBuildParticipantFeature, IProcessAnnotatedTypeFeature, IProcessAnnotatedMemberFeature, IValidatorFeature, DeltaspikeConstants {
	public static String ID = "org.apache.deltaspike.security.impl.authorization.SecurityExtension"; //$NON-NLS-1$
	DeltaspikeSecurityDefinitionContext context = new DeltaspikeSecurityDefinitionContext();

	public static DeltaspikeSecurityExtension getExtension(CDICoreNature project) {
		return (DeltaspikeSecurityExtension)project.getExtensionManager().getExtensionByRuntime(ID);
	}

	@Override
	public IDefinitionContextExtension getContext() {
		return context;
	}

	@Override
	public void beginVisiting() {}

	@Override
	public void visitJar(IPath path, IPackageFragmentRoot root, XModelObject beansXML) {}

	@Override
	public void visit(IFile file, IPath src, IPath webinf) {}

	@Override
	public void buildDefinitions() {}

	@Override
	public void buildDefinitions(FileSet fileSet) {}

	@Override
	public void buildBeans(CDIProject target) {}

	@Override
	public void processAnnotatedMember(BeanMemberDefinition memberDefinition,
			IRootDefinitionContext context) {
		if(!(memberDefinition instanceof MethodDefinition)) {
			return;
		}
		if(memberDefinition.isAnnotationPresent(SECURES_ANNOTATION_TYPE_NAME)) {
			MethodDefinition method = (MethodDefinition)memberDefinition;
			method.setCDIAnnotated(true);
			DeltaspikeAuthorityMethod authorizer = new DeltaspikeAuthorityMethod(method);
			DeltaspikeSecurityDefinitionContext contextCopy = ((DeltaspikeSecurityDefinitionContext)this.context.getWorkingCopy());
			contextCopy.allAuthorizerMethods.getAuthorizerMembers().add(authorizer);
			List<SecurityBindingDeclaration> ds = findAnnotationAnnotatedWithSecurityBindingType(memberDefinition, contextCopy.getRootContext());
			for (SecurityBindingDeclaration d: ds) {
				DeltaspikeSecurityBindingConfiguration c = ((DeltaspikeSecurityDefinitionContext)this.context.getWorkingCopy()).getConfiguration(d.getBinding().getTypeName());
				authorizer.addBinding(d, c);
				c.getAuthorizerMembers().add(authorizer);
				addToDependencies(c, authorizer.getMethod(), context);
			}
		} else {
			addSecurityMember(memberDefinition, context);
		}
	}

	@Override
	public void processAnnotatedType(TypeDefinition typeDefinition,
			IRootDefinitionContext context) {
		addSecurityMember(typeDefinition, context);
	}

	private void addSecurityMember(AbstractMemberDefinition def, IRootDefinitionContext context) {
		List<SecurityBindingDeclaration> ds = findAnnotationAnnotatedWithSecurityBindingType(def, context);
		for (SecurityBindingDeclaration d: ds) {
			addBoundMember(def, d, context);
		}
	}

	private void addBoundMember(AbstractMemberDefinition def, SecurityBindingDeclaration d, IRootDefinitionContext context) {
		String securityBindingType = d.getBinding().getTypeName();
		if(def instanceof MethodDefinition) {
			((MethodDefinition)def).setCDIAnnotated(true);
		}
		DeltaspikeSecurityBindingConfiguration c = ((DeltaspikeSecurityDefinitionContext)this.context.getWorkingCopy()).getConfiguration(securityBindingType);
		
		c.getBoundMembers().put(def, d);

		addToDependencies(c, def, context);
	}


	private void addToDependencies(DeltaspikeSecurityBindingConfiguration c, AbstractMemberDefinition def, IRootDefinitionContext context) {
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

	static List<SecurityBindingDeclaration> EMPTY = Collections.<SecurityBindingDeclaration>emptyList();

	private List<SecurityBindingDeclaration> findAnnotationAnnotatedWithSecurityBindingType(AbstractMemberDefinition m, IRootDefinitionContext context) {
		List<SecurityBindingDeclaration> result = null;
		List<IAnnotationDeclaration> ds = m.getAnnotations();
		for (IAnnotationDeclaration d: ds) {
			if(d instanceof IStereotypeDeclaration) {
				AnnotationDefinition t = context.getAnnotation(d.getTypeName());
				if(t != null) {
					List<IAnnotationDeclaration> ds1 = findSecurityBindingAnnotations(t, null, context);
					if(ds1 != null) {
						for (IAnnotationDeclaration d1: ds1) {
							result.add(new SecurityBindingDeclaration(d, d1));
						}
					}
				}
			} else if(d.getTypeName() != null) {
				AnnotationDefinition a = context.getAnnotation(d.getTypeName());
				if(a != null && a.isAnnotationPresent(SECURITY_BINDING_ANNOTATION_TYPE_NAME)) {
					if(result == null) {
						result = new ArrayList<SecurityBindingDeclaration>();
					}
					result.add(new SecurityBindingDeclaration(d, d));
				} else if(a != null && d instanceof IStereotypeDeclaration) {
					List<IAnnotationDeclaration> ds1 = findSecurityBindingAnnotations(a, null, context);
					if(ds1 != null) {
						if(result == null) {
							result = new ArrayList<SecurityBindingDeclaration>();
						}
						for (IAnnotationDeclaration d1: ds1) {
							result.add(new SecurityBindingDeclaration(d, d1));
						}
					}
				}
			}
		}
		return result == null ? EMPTY : result;
	}

	private List<IAnnotationDeclaration> findSecurityBindingAnnotations(IAnnotated s, List<IAnnotationDeclaration> result, IRootDefinitionContext context) {
		List<IAnnotationDeclaration> ds = s.getAnnotations();
		for (IAnnotationDeclaration d: ds) {
			if(d.getTypeName() != null) {
				AnnotationDefinition a = context.getAnnotation(d.getTypeName());
				if(a != null && a.isAnnotationPresent(SECURITY_BINDING_ANNOTATION_TYPE_NAME)) {
					if(result == null) {
						result = new ArrayList<IAnnotationDeclaration>();
					}
					result.add(d);
				} else if(a != null && d instanceof IStereotypeDeclaration) {
					List<IAnnotationDeclaration> ds1 = findSecurityBindingAnnotations(a, null, context);
					if(ds1 != null) {
						if(result == null) {
							result = new ArrayList<IAnnotationDeclaration>();
						}
						result.addAll(ds1);
					}
				}
			}
		}		
		return result;
	}

	@Override
	public void validateResource(IFile file, CDICoreValidator validator) {
		Set<DeltaspikeAuthorityMethod> authorizers = context.getAuthorityMethods(file.getFullPath());
		for (DeltaspikeAuthorityMethod authorizer: authorizers) {
			IAnnotationDeclaration a = authorizer.getMethod().getAnnotation(SECURES_ANNOTATION_TYPE_NAME);
			if(authorizer.getBindings().isEmpty()) {
				validator.addError(DeltaspikeValidationMessages.INVALID_AUTHORIZER_NO_BINDINGS, 
						DeltaspikeSeverityPreferences.INVALID_AUTHORIZER,  
						new String[]{authorizer.getMethod().getMethod().getElementName()}, 
						a, file);
			} else if(authorizer.getBindings().size() > 1) {
				validator.addError(DeltaspikeValidationMessages.INVALID_AUTHORIZER_MULTIPLE_BINDINGS, 
						DeltaspikeSeverityPreferences.INVALID_AUTHORIZER,  
						new String[]{authorizer.getMethod().getMethod().getElementName()}, 
						a, file);
			}
			try {
				String returnType = authorizer.getMethod().getMethod().getReturnType();
				if(!"Z".equals(returnType)) { //$NON-NLS-1$
					validator.addError(DeltaspikeValidationMessages.INVALID_AUTHORIZER_NOT_BOOLEAN, 
							DeltaspikeSeverityPreferences.INVALID_AUTHORIZER,  
							new String[]{authorizer.getMethod().getMethod().getElementName()}, 
							a, file);
				}
				
			} catch (JavaModelException e) {
				DeltaspikeCorePlugin.getDefault().logError(e);
			}
			
		}
	
		Set<DeltaspikeSecurityExtension> parents = null;
		
		for (DeltaspikeSecurityBindingConfiguration c: context.getConfigurations().values()) {
			if(c.getInvolvedTypes().contains(file.getFullPath())) {
				if(parents == null) {
					parents = getParents(getContext().getRootContext().getProject());
				}
				Set<DeltaspikeAuthorityMethod> authorizers2 = collectAuthorizerMethods(parents, c.getSecurityBindingTypeName());
				authorizers2.addAll(c.getAuthorizerMembers());
				Map<AbstractMemberDefinition, SecurityBindingDeclaration> bound = c.getBoundMembers();
				for (AbstractMemberDefinition d: bound.keySet()) {
					String name = d instanceof MethodDefinition ? ((MethodDefinition)d).getMethod().getElementName()
							: d instanceof TypeDefinition ? ((TypeDefinition)d).getQualifiedName() : "";
					if(file.getFullPath().equals(d.getTypeDefinition().getType().getPath())) {
						SecurityBindingDeclaration dc = bound.get(d);
						int k = 0;
						for (DeltaspikeAuthorityMethod a: authorizers2) {
							try {
								if(a.isMatching(dc.getBinding())) k++;
							} catch (CoreException e) {
								DeltaspikeCorePlugin.getDefault().logError(e);
							}
						}
						if(k == 0) {
							validator.addError(DeltaspikeValidationMessages.UNRESOLVED_AUTHORIZER, 
									DeltaspikeSeverityPreferences.UNRESOLVED_AUTHORIZER,  
									new String[]{dc.getBinding().getTypeName(), name}, 
									dc.getDeclaration(), file);
						} else if(k > 1) {
							validator.addError(DeltaspikeValidationMessages.AMBIGUOUS_AUTHORIZER, 
									DeltaspikeSeverityPreferences.AMBIGUOUS_AUTHORIZER,  
									new String[]{dc.getBinding().getTypeName(), name}, 
									dc.getDeclaration(), file);
						}
							
					}
				}
			}
		}
		//
	}

	@Override
	public SeverityPreferences getSeverityPreferences() {
		return DeltaspikeSeverityPreferences.getInstance();
	}

	public static Set<DeltaspikeSecurityExtension> getParents(CDICoreNature nature) {
		Set<CDICoreNature> ps = nature.getCDIProjects(true);
		Set<DeltaspikeSecurityExtension> parents = new HashSet<DeltaspikeSecurityExtension>();
		for (CDICoreNature p: ps) {
			DeltaspikeSecurityExtension ext = DeltaspikeSecurityExtension.getExtension(p);
			if(ext != null) parents.add(ext);
		}
		return parents;		
	}

	public static Set<DeltaspikeAuthorityMethod> collectAuthorizerMethods(Set<DeltaspikeSecurityExtension> parents, String securityBindingTypeName) {
		Set<DeltaspikeAuthorityMethod> result = new HashSet<DeltaspikeAuthorityMethod>();
		for (DeltaspikeSecurityExtension ext: parents) {
			DeltaspikeSecurityDefinitionContext context = (DeltaspikeSecurityDefinitionContext)ext.getContext();
			DeltaspikeSecurityBindingConfiguration c = context.getConfiguration(securityBindingTypeName);
			if(c != null) {
				result.addAll(c.getAuthorizerMembers());
			}
		}
		return result;
	}

}
