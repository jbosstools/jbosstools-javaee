/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IDecorator;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInterceptor;
import org.jboss.tools.cdi.core.IParametedType;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IProducerField;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.IScopeDeclaration;
import org.jboss.tools.cdi.core.ISessionBean;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.core.IStereotyped;
import org.jboss.tools.cdi.core.ITypeDeclaration;
import org.jboss.tools.cdi.core.preferences.CDIPreferences;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.internal.KbProject;
import org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatingProjectSet;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager;
import org.jboss.tools.jst.web.kb.validation.IValidatingProjectSet;
import org.jboss.tools.jst.web.kb.validation.IValidationContext;
import org.jboss.tools.jst.web.kb.validation.IValidator;
import org.jboss.tools.jst.web.kb.validation.ValidationUtil;

public class CDICoreValidator extends CDIValidationErrorManager implements IValidator {
	public static final String ID = "org.jboss.tools.cdi.core.CoreValidator";

	ICDIProject cdiProject;
	String projectName;

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#getId()
	 */
	public String getId() {
		return ID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#getValidatingProjects(org.eclipse.core.resources.IProject)
	 */
	public IValidatingProjectSet getValidatingProjects(IProject project) {
		IValidationContext rootContext = null;
		IProject war = null; //TODO get war ?
		if(war != null && war.isAccessible()) {
			IKbProject kbProject = KbProjectFactory.getKbProject(war, false);
			if(kbProject!=null) {
				rootContext = kbProject.getValidationContext();
			} else {
				KbProject.checkKBBuilderInstalled(war);
				CDICoreNature cdiProject = CDICorePlugin.getCDI(project, false);
				if(cdiProject != null) {
					rootContext = null; //cdiProject.getDelegate().getValidationContext();
				}
			}
		}
		if(rootContext == null) {
			CDICoreNature cdiProject = CDICorePlugin.getCDI(project, false);
			if(cdiProject != null) {
				rootContext = cdiProject.getValidationContext();
			}
		}

		List<IProject> projects = new ArrayList<IProject>();
		projects.add(project);
//		IProject[] array = set.getAllProjects();
//		for (int i = 0; i < array.length; i++) {
//			if(array[i].isAccessible()) {
//				projects.add(array[i]);
//			}
//		}
		return new ValidatingProjectSet(project, projects, rootContext);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#shouldValidate(org.eclipse.core.resources.IProject)
	 */
	public boolean shouldValidate(IProject project) {
		try {
			// TODO check preferences
			return project != null && project.isAccessible() && project.hasNature(CDICoreNature.NATURE_ID);
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#init(org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter, org.jboss.tools.jst.web.kb.validation.IValidationContext)
	 */
	@Override
	public void init(IProject project, ContextValidationHelper validationHelper, org.eclipse.wst.validation.internal.provisional.core.IValidator manager, IReporter reporter) {
		super.init(project, validationHelper, manager, reporter);
		cdiProject = CDICorePlugin.getCDIProject(project, false);
		projectName = project.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#validate(java.util.Set, org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validate(Set<IFile> changedFiles, IProject project,
			ContextValidationHelper validationHelper, ValidatorManager manager,
			IReporter reporter) throws ValidationException {
		init(project, validationHelper, manager, reporter);
		displaySubtask(CDIValidationMessages.SEARCHING_RESOURCES);

		if(cdiProject == null) {
			return OK_STATUS;
		}
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		Set<IPath> resources = new HashSet<IPath>(); // Resources which we have to validate.
		for(IFile currentFile : changedFiles) {
			if(reporter.isCancelled()) {
				break;
			}
			if (ValidationUtil.checkFileExtensionForJavaAndXml(currentFile)) {
				resources.add(currentFile.getFullPath());

				// Get all the paths of related resources for given file. These links were saved in previous validation process.
				Set<String> oldReletedResources = validationContext.getVariableNamesByCoreResource(currentFile.getFullPath(), false);
				if(oldReletedResources!=null) {
					for (String resourcePath : oldReletedResources) {
						resources.add(Path.fromOSString(resourcePath));
					}
				}
			}
		}
		// Validate all collected linked resources.
		// Remove all links between collected resources because they will be linked again during validation.
		validationContext.removeLinkedCoreResources(resources);

		IFile[] filesToValidate = new IFile[resources.size()];
		int i = 0;
		// We have to remove markers from all collected source files first
		for (IPath linkedResource : resources) {
			filesToValidate[i] = root.getFile(linkedResource);
			removeAllMessagesFromResource(filesToValidate[i++]);
		}
		i = 0;
		// Then we can validate them
		for (IFile file : filesToValidate) {
			validateResource(file);
		}

		return OK_STATUS;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#validateAll(org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validateAll(IProject project,
			ContextValidationHelper validationHelper, ValidatorManager manager,
			IReporter reporter) throws ValidationException {
		init(project, validationHelper, manager, reporter);
		displaySubtask(CDIValidationMessages.VALIDATING_PROJECT, new String[]{projectName});
		removeAllMessagesFromResource(cdiProject.getNature().getProject());
		if(cdiProject == null) {
			return OK_STATUS;
		}
		IBean[] beans = cdiProject.getBeans();
		for (IBean bean : beans) {
			validateBean(bean);
		}

		IStereotype[] stereoTypes = cdiProject.getStereotypes();
		for (IStereotype type: stereoTypes) {
			validateStereotype(type);
		}

		// TODO
		return OK_STATUS;
	}

	/**
	 * Validates a resource.
	 * 
	 * @param file
	 */
	private void validateResource(IFile file) {
		if(reporter.isCancelled() || file==null || !file.isAccessible()) {
			return;
		}
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		for (IBean bean : beans) {
			validateBean(bean);
		}
		IStereotype stereotype = cdiProject.getStereotype(file.getFullPath());
		validateStereotype(stereotype);

		// TODO
	}

	/**
	 * Validates a bean.
	 * 
	 * @param bean
	 */
	private void validateBean(IBean bean) {
		if(reporter.isCancelled()) {
			return;
		}
		// Collect all relations between the bean and other CDI elements.
		String name = bean.getName();
		if(name!=null) {
			validationContext.addVariableNameForELValidation(name);
		}
		String beanPath = bean.getResource().getFullPath().toOSString();
		Set<IScopeDeclaration> scopeDeclarations = bean.getScopeDeclarations();
		for (IScopeDeclaration scopeDeclaration : scopeDeclarations) {
			IScope scope = scopeDeclaration.getScope();
			if(!scope.getSourceType().isReadOnly()) {
				validationContext.addLinkedCoreResource(beanPath, scope.getResource().getFullPath(), false);
			}
		}
		Set<IStereotypeDeclaration> stereotypeDeclarations = bean.getStereotypeDeclarations();
		for (IStereotypeDeclaration stereotypeDeclaration : stereotypeDeclarations) {
			IStereotype stereotype = stereotypeDeclaration.getStereotype();
			if(!stereotype.getSourceType().isReadOnly()) {
				validationContext.addLinkedCoreResource(beanPath, stereotype.getResource().getFullPath(), false);
			}
		}

		// validate
		validateTyped(bean);
		validateBeanScope(bean);

		if(bean instanceof IProducer) {
			validateProducer((IProducer)bean);
		}

		Set<IInjectionPoint> points = bean.getInjectionPoints();
		for (IInjectionPoint point : points) {
			validateInjectionPoint(point);
		}

		if(bean instanceof IInterceptor) {
			validateInterceptor((IInterceptor)bean);
		}

		if(bean instanceof IDecorator) {
			validateDecorator((IDecorator)bean);
		}

		if(bean instanceof IClassBean) {
			validateClassBean((IClassBean)bean);
		}

		// TODO
	}

	private void validateClassBean(IClassBean bean) {
		validateDisposers(bean);
	}

	private void validateDisposers(IClassBean bean) {
		Set<IBeanMethod> disposers = bean.getDisposers();
		for (IBeanMethod disposer : disposers) {
			List<IParameter> params = disposer.getParameters();
			/*
			 * 3.3.6. Declaring a disposer method
			 *  - method has more than one parameter annotated @Disposes
			 */
			Set<ITextSourceReference> disposerDeclarations = new HashSet<ITextSourceReference>();
			for (IParameter param : params) {
				ITextSourceReference declaration = param.getAnnotationPosition(CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME);
				if(declaration!=null) {
					disposerDeclarations.add(declaration);
				}
			}
			if(disposerDeclarations.size()>1) {
				for (ITextSourceReference declaration : disposerDeclarations) {
					addError(CDIValidationMessages.MULTIPLE_DISPOSING_PARAMETERS, CDIPreferences.MULTIPLE_DISPOSING_PARAMETERS, declaration, bean.getResource());
				}
			}
			/*
			 * 3.3.6. Declaring a disposer method
			 *  - a disposer method has a parameter annotated @Observes.
			 *  
			 * 10.4.2. Declaring an observer method
			 *  - a observer method has a parameter annotated @Disposes.
			 */
			Set<ITextSourceReference> declarations = new HashSet<ITextSourceReference>();
			boolean observesExists = false;
			declarations.addAll(disposerDeclarations);
			for (IParameter param : params) {
				ITextSourceReference declaration = param.getAnnotationPosition(CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME);
				if(declaration!=null) {
					declarations.add(declaration);
					observesExists = true;
				}
			}
			if(observesExists) {
				for (ITextSourceReference declaration : declarations) {
					addError(CDIValidationMessages.OBSERVER_PARAMETER_ILLEGALLY_ANNOTATED, CDIPreferences.OBSERVER_PARAMETER_ILLEGALLY_ANNOTATED, declaration, bean.getResource());
				}
			}
			/*
			 * 3.3.6. Declaring a disposer method
			 *  - a disposer method is annotated @Inject.
			 *  
			 * 3.9.1. Declaring an initializer method
			 *  - an initializer method has a parameter annotated @Disposes
			 */
			IAnnotationDeclaration injectDeclaration = disposer.getAnnotation(CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
			if(injectDeclaration!=null) {
				addError(CDIValidationMessages.DISPOSER_ANNOTATED_INJECT, CDIPreferences.DISPOSER_ANNOTATED_INJECT, injectDeclaration, bean.getResource());
				for (ITextSourceReference declaration : disposerDeclarations) {
					addError(CDIValidationMessages.DISPOSER_ANNOTATED_INJECT, CDIPreferences.DISPOSER_ANNOTATED_INJECT, declaration, bean.getResource());
				}
			}

			/*
			 * 3.3.6. Declaring a disposer method
			 *  - a non-static method of a session bean class has a parameter annotated @Disposes, and the method is not a business method of the session bean
			 */
			validateSessionBeanMethod(bean, disposer, disposerDeclarations, CDIValidationMessages.ILLEGAL_DISPOSER_IN_SESSION_BEAN, CDIPreferences.ILLEGAL_DISPOSER_IN_SESSION_BEAN);
		}
	}

	/**
	 * If the method is not a static method and is not a business method of the session bean and is observer or disposer then mark it as incorrect.
	 * 
	 * @param bean
	 * @param method
	 * @param annotatedParams
	 * @param errorKey
	 */
	private void validateSessionBeanMethod(IClassBean bean, IBeanMethod method, Set<ITextSourceReference> annotatedParams, String errorMessageKey, String preferencesKey) {
		if(bean instanceof ISessionBean) {
			if(annotatedParams!=null) {
				try {
					if(!Flags.isStatic(method.getMethod().getFlags())) {
						ISessionBean sessionBean = (ISessionBean)bean;
						Set<IParametedType> types = sessionBean.getLegalTypes();
						boolean businessMethod = false;
						for (IParametedType type : types) {
							IType sourceType = type.getType();
							IAnnotation annotation = sourceType.getAnnotation(CDIConstants.LOCAL_ANNOTATION_TYPE_NAME);
							if(annotation==null) {
								annotation = sourceType.getAnnotation("Local"); //$NON-NLS-N1
								if(annotation!=null && CDIConstants.LOCAL_ANNOTATION_TYPE_NAME.equals(EclipseJavaUtil.resolveType(sourceType, "Local"))) { //$NON-NLS-N1
									IMethod[] methods = sourceType.getMethods();
									for (IMethod iMethod : methods) {
										if(method.getMethod().isSimilar(iMethod)) {
											businessMethod = true;
											break;
										}
									}
									break;
								}
							}
						}
						if(!businessMethod) {
							for (ITextSourceReference declaration : annotatedParams) {
								addError(errorMessageKey, preferencesKey, declaration, bean.getResource());
							}
						}
					}
				} catch (JavaModelException e) {
					CDICorePlugin.getDefault().logError(e);
				}
			}
		}
	}

	private static final String[] RESOURCE_ANNOTATIONS = {CDIConstants.RESOURCE_ANNOTATION_TYPE_NAME, CDIConstants.WEB_SERVICE_REF_ANNOTATION_TYPE_NAME, CDIConstants.EJB_ANNOTATION_TYPE_NAME, CDIConstants.PERSISTENCE_CONTEXT_ANNOTATION_TYPE_NAME, CDIConstants.PERSISTENCE_UNIT_ANNOTATION_TYPE_NAME};

	private void validateProducer(IProducer producer) {
		if(producer instanceof IProducerField) {
			/*
			 * 3.5.1. Declaring a resource
			 * 	- producer field declaration specifies an EL name (together with one of @Resource, @PersistenceContext, @PersistenceUnit, @EJB, @WebServiceRef)
			 */
			IProducerField producerField = (IProducerField)producer;
			if(producerField.getName()!=null) {
				IAnnotationDeclaration declaration;
				for (String annotationType : RESOURCE_ANNOTATIONS) {
					declaration = producerField.getAnnotation(annotationType);
					if(declaration!=null) {
						IAnnotationDeclaration nameDeclaration = producerField.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
						if(nameDeclaration!=null) {
							declaration = nameDeclaration;
						}
						addError(CDIValidationMessages.RESOURCE_PRODUCER_FIELD_SETS_EL_NAME, CDIPreferences.RESOURCE_PRODUCER_FIELD_SETS_EL_NAME, declaration, producer.getResource());
					}
				}
			}
		} else {
			IProducerMethod producerMethod = (IProducerMethod)producer;
			List<IParameter> params = producerMethod.getParameters();
			Set<ITextSourceReference> declarations = new HashSet<ITextSourceReference>();
			declarations.add(producerMethod.getAnnotation(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME));
			for (IParameter param : params) {
				/*
				 * 3.3.6. Declaring a disposer method
				 *  - a disposer method is annotated @Produces.
				 *  
				 * 3.3.2. Declaring a producer method
				 *  - a has a parameter annotated @Disposes
				 */
				ITextSourceReference declaration = param.getAnnotationPosition(CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME);
				if(declaration!=null) {
					declarations.add(declaration);
				}
				/*
				 * 3.3.2. Declaring a producer method
				 *  - a has a parameter annotated @Observers
				 *  
				 * 10.4.2. Declaring an observer method
				 *  - an observer method is annotated @Produces
				 */
				declaration = param.getAnnotationPosition(CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME);
				if(declaration!=null) {
					declarations.add(declaration);
				}
			}
			if(declarations.size()>1) {
				for (ITextSourceReference declaration : declarations) {
					addError(CDIValidationMessages.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED, CDIPreferences.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED, declaration, producer.getResource());
				}
			}
		}
	}

	private void validateInjectionPoint(IInjectionPoint injection) {
		/*
		 * 3.11. The qualifier @Named at injection points
		 *  - injection point other than injected field declares a @Named annotation that does not specify the value member
		 */
		if(!(injection instanceof IInjectionPointField)) {
			IAnnotationDeclaration named = injection.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
			if(named!=null) {
				try {
					IMemberValuePair[] values = named.getDeclaration().getMemberValuePairs();
					boolean valueExists = false;
					for (IMemberValuePair pair : values) {
						if("value".equals(pair.getMemberName())) {
							valueExists = true;
							break;
						}
					}
					if(!valueExists) {
						addError(CDIValidationMessages.PARAM_INJECTION_DECLARES_EMPTY_NAME, CDIPreferences.PARAM_INJECTION_DECLARES_EMPTY_NAME, named, injection.getResource());
					}
				} catch (JavaModelException e) {
					CDICorePlugin.getDefault().logError(e);
				}
			}
		}
	}

	private void validateInterceptor(IInterceptor interceptor) {
		/*
		 * 2.5.3. Beans with no EL name - interceptor has a name (Non-Portable
		 * behavior)
		 */
		if(interceptor.getName()!=null) {
			ITextSourceReference declaration = interceptor.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
			if (declaration == null) {
				declaration = interceptor.getAnnotation(CDIConstants.INTERCEPTOR_ANNOTATION_TYPE_NAME);
			}
			if(declaration==null) {
				declaration = getNamedStereotypeDeclaration(interceptor);
			}
			addError(CDIValidationMessages.INTERCEPTOR_HAS_NAME, CDIPreferences.INTERCEPTOR_HAS_NAME, declaration, interceptor.getResource());
		}
	}

	private void validateDecorator(IDecorator decorator) {
		/*
		 * 2.5.3. Beans with no EL name
		 *	- decorator has a name (Non-Portable behavior)
		 */
		if(decorator.getName()!=null) {
			ITextSourceReference declaration = decorator.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
			if (declaration == null) {
				declaration = decorator.getAnnotation(CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME);
			}
			if(declaration==null) {
				declaration = getNamedStereotypeDeclaration(decorator);
			}
			addError(CDIValidationMessages.DECORATOR_HAS_NAME, CDIPreferences.DECORATOR_HAS_NAME, declaration, decorator.getResource());
		}
	}

	private IAnnotationDeclaration getNamedStereotypeDeclaration(IStereotyped stereotyped) {
		Set<IStereotypeDeclaration> declarations = stereotyped.getStereotypeDeclarations();
		for (IStereotypeDeclaration declaration : declarations) {
			if(CDIConstants.NAMED_QUALIFIER_TYPE_NAME.equals(declaration.getType().getFullyQualifiedName()) ||
				getNamedStereotypeDeclaration(declaration.getStereotype())!=null) {
				return declaration;
			}
		}
		return null;
	}

	/*
	 * 2.2.2. Restricting the bean types of a bean
	 *	      - bean class or producer method or field specifies a @Typed annotation, 
	 *		  and the value member specifies a class which does not correspond to a type 
	 *		  in the unrestricted set of bean types of a bean
	 */
	private void validateTyped(IBean bean) {
		Set<ITypeDeclaration> typedDeclarations = bean.getRestrictedTypeDeclaratios();
		if(!typedDeclarations.isEmpty()) {
			Set<IParametedType> allTypes = bean.getAllTypes();
			for (ITypeDeclaration typedDeclaration : typedDeclarations) {
				IType typedType = typedDeclaration.getType();
				if(typedType!=null) {
					boolean typeWasFound = false;
					for (IParametedType type : allTypes) {
						if(type!=null && typedType.getFullyQualifiedName().equals(type.getType().getFullyQualifiedName())) {
							typeWasFound = true;
							break;
						}
					}
					if(!typeWasFound) {
						addError(CDIValidationMessages.ILLEGAL_TYPE_IN_TYPED_DECLARATION, CDIPreferences.ILLEGAL_TYPE_IN_TYPED_DECLARATION, typedDeclaration, bean.getResource());
					}
				}
			}
		}
	}

	private void validateBeanScope(IBean bean) {
		Set<IScopeDeclaration> scopes = bean.getScopeDeclarations();
		//  2.4.3. Declaring the bean scope
		//         - bean class or producer method or field specifies multiple scope type annotations
		//
		if(scopes.size()>1) {
			for (IScopeDeclaration scope : scopes) {
				addError(CDIValidationMessages.MULTIPLE_SCOPE_TYPE_ANNOTATIONS, CDIPreferences.MULTIPLE_SCOPE_TYPE_ANNOTATIONS, scope, bean.getResource());
			}
		}

		// 2.4.4. Default scope
		//        - bean does not explicitly declare a scope when there is no default scope 
		//        (there are two different stereotypes declared by the bean that declare different default scopes)
		// 
		//        Such bean definitions are invalid because they declares two stereotypes that have different default scopes and the bean does not explictly define a scope to resolve the conflict.
		Set<IStereotypeDeclaration> stereotypeDeclarations = bean.getStereotypeDeclarations();
		if(!stereotypeDeclarations.isEmpty() && scopes.isEmpty()) {
			Map<String, IStereotypeDeclaration> declarationMap = new HashMap<String, IStereotypeDeclaration>();
			for (IStereotypeDeclaration stereotypeDeclaration : stereotypeDeclarations) {
				IStereotype stereotype = stereotypeDeclaration.getStereotype();
				IScope scope = stereotype.getScope();
				if(scope!=null) {
					declarationMap.put(scope.getSourceType().getFullyQualifiedName(), stereotypeDeclaration);
				}
			}
			if(declarationMap.size()>1) {
				for (IStereotypeDeclaration stereotypeDeclaration : declarationMap.values()) {
					addError(CDIValidationMessages.MISSING_SCOPE_WHEN_THERE_IS_NO_DEFAULT_SCOPE, CDIPreferences.MISSING_SCOPE_WHEN_THERE_IS_NO_DEFAULT_SCOPE, stereotypeDeclaration, bean.getResource());
				}
			}
		}
	}

	/**
	 * Validates a stereotype.
	 * 
	 * @param type
	 */
	private void validateStereotype(IStereotype stereotype) {
		// 2.7.1.3. Declaring a @Named stereotype
		//          - stereotype declares a non-empty @Named annotation (Non-Portable behavior)
		//          - stereotype declares any other qualifier annotation
		//          - stereotype is annotated @Typed

		if(stereotype == null) {
			return;
		}
		IResource resource = stereotype.getResource();
		if(resource == null || !resource.getName().toLowerCase().endsWith(".java")) {
			//validate sources only
			return;
		}
		List<IAnnotationDeclaration> as = stereotype.getAnnotationDeclarations();

		// 1. non-empty name
		IAnnotationDeclaration nameDeclaration = stereotype.getNameDeclaration();
		if(nameDeclaration != null) {
			IMemberValuePair[] ps = null;
			try {
				ps = nameDeclaration.getDeclaration().getMemberValuePairs();
			} catch (JavaModelException e) {
				CDICorePlugin.getDefault().logError(e);
			}
			if(ps != null && ps.length > 0) {
				Object name = ps[0].getValue();
				if(name != null && name.toString().length() > 0) {
					ITextSourceReference location = nameDeclaration;
					addError(CDIValidationMessages.STEREOTYPE_DECLARES_NON_EMPTY_NAME, CDIPreferences.STEREOTYPE_DECLARES_NON_EMPTY_NAME, location, resource);
				}
			}
		}

		// 2. typed annotation			
		IAnnotationDeclaration typedDeclaration = stereotype.getAnnotationDeclaration(CDIConstants.TYPED_ANNOTATION_TYPE_NAME);
		if(typedDeclaration != null) {
			ITextSourceReference location = typedDeclaration;
			addError(CDIValidationMessages.STEREOTYPE_IS_ANNOTATED_TYPED, CDIPreferences.STEREOTYPE_IS_ANNOTATED_TYPED, location, resource);
		}

		// 3. Qualifier other than @Named
		for (IAnnotationDeclaration a: as) {
			if(a instanceof IQualifierDeclaration && a != nameDeclaration) {
				ITextSourceReference location = a;
				addError(CDIValidationMessages.ILLEGAL_QUALIFIER_IN_STEREOTYPE, CDIPreferences.ILLEGAL_QUALIFIER_IN_STEREOTYPE, location, resource);
			}
		}

		// 2.7.1.1. Declaring the default scope for a stereotype
		//          - stereotype declares more than one scope
		Set<IScopeDeclaration> scopeDeclarations = stereotype.getScopeDeclarations();
		if(scopeDeclarations.size()>1) {
			for (IScopeDeclaration scope : scopeDeclarations) {
				addError(CDIValidationMessages.STEREOTYPE_DECLARES_MORE_THAN_ONE_SCOPE, CDIPreferences.STEREOTYPE_DECLARES_MORE_THAN_ONE_SCOPE, scope, stereotype.getResource());
			}
		}
	}
}