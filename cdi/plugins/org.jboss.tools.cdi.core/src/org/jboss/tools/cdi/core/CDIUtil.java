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
package org.jboss.tools.cdi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.jst.web.kb.IKbProject;

/**
 * @author Alexey Kazakov
 */
public class CDIUtil {

	/**
	 * Adds CDI and KB builders to the project.
	 * 
	 * @param project
	 */
	public static void enableCDI(IProject project) {
		try {
			EclipseUtil.addNatureToProject(project, CDICoreNature.NATURE_ID);
			if (!project.hasNature(IKbProject.NATURE_ID)) {
				EclipseResourceUtil.addNatureToProject(project, IKbProject.NATURE_ID);
			}
			EclipseResourceUtil.addBuilderToProject(project, ValidationPlugin.VALIDATION_BUILDER_ID);
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	/**
	 * Removes CDI builder from the project.
	 * 
	 * @param project
	 */
	public static void disableCDI(IProject project) {
		try {
			EclipseUtil.removeNatureFromProject(project, CDICoreNature.NATURE_ID);
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	/**
	 * Finds CDI injected point in beans for particular java element.
	 * 
	 * @param beans
	 * @param element
	 */
	public static IInjectionPoint findInjectionPoint(Set<IBean> beans, IJavaElement element, int position) {
		if (!(element instanceof IField) && (element instanceof IMethod) && (element instanceof ILocalVariable)) {
			return null;
		}

		for (IBean bean : beans) {
			Set<IInjectionPoint> injectionPoints = bean.getInjectionPoints();
			for (IInjectionPoint iPoint : injectionPoints) {
				if (element instanceof IField && iPoint instanceof IInjectionPointField) {
					if (((IInjectionPointField) iPoint).getField() != null && ((IInjectionPointField) iPoint).getField().equals(element))
						return iPoint;
				} else if (element instanceof IMethod && iPoint instanceof IInjectionPointMethod && position == 0) {
					if (((IInjectionPointMethod) iPoint).getMethod() != null && ((IInjectionPointMethod) iPoint).getMethod().equals(element))
						return iPoint;
				}else if(element instanceof ILocalVariable && iPoint instanceof IInjectionPointParameter){
					if (((IInjectionPointParameter) iPoint).getName().equals(element.getElementName())) 
						return iPoint;
				}else if(iPoint instanceof IInjectionPointParameter && position != 0){
					if(iPoint.getStartPosition() <= position && (iPoint.getStartPosition()+iPoint.getLength()) >= position)
						return iPoint;
				}
			}
		}
		return null;
	}

	/**
	 * Sorts CDI beans which may be injected. Sets for alternative beans higher
	 * position and for nonalternative beans lower position.
	 * 
	 * @param beans
	 * @param element
	 */
	public static List<IBean> sortBeans(Set<IBean> beans) {
		Set<IBean> alternativeBeans = new HashSet<IBean>();
		Set<IBean> nonAlternativeBeans = new HashSet<IBean>();

		for (IBean bean : beans) {
			if (bean == null || bean instanceof IDecorator || bean instanceof IInterceptor) {
				continue;
			}
			if (bean.isAlternative()) {
				alternativeBeans.add(bean);
			} else {
				nonAlternativeBeans.add(bean);
			}
		}

		ArrayList<IBean> sortedBeans = new ArrayList<IBean>();
		sortedBeans.addAll(alternativeBeans);
		sortedBeans.addAll(nonAlternativeBeans);
		return sortedBeans;
	}

	/**
	 * Checks if the bean has @Depended scope. If it has different scope then @Depended
	 * then returns this scope declaration or a stereotype which declares the
	 * scope. Otherwise returns null.
	 * 
	 * @param bean
	 * @param scopeTypeName
	 * @return
	 */
	public static IAnnotationDeclaration getDifferentScopeDeclarationThanDepentend(IScoped scoped) {
		return getAnotherScopeDeclaration(scoped, CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME);
	}

	/**
	 * Checks if the bean has @ApplicationScoped scope. If it has different scope then @ApplicationScoped
	 * then returns this scope declaration or a stereotype which declares the
	 * scope. Otherwise returns null.
	 * 
	 * @param bean
	 * @param scopeTypeName
	 * @return
	 */
	public static IAnnotationDeclaration getDifferentScopeDeclarationThanApplicationScoped(IScoped scoped) {
		return getAnotherScopeDeclaration(scoped, CDIConstants.APPLICATION_SCOPED_ANNOTATION_TYPE_NAME);
	}

	/**
	 * Checks if the bean has given scope. If it has different scope then given
	 * then returns this scope declaration or a stereotype which declares the
	 * scope. Otherwise returns null.
	 * 
	 * @param bean
	 * @param scopeTypeName
	 * @return
	 */
	public static IAnnotationDeclaration getAnotherScopeDeclaration(IScoped scoped, String scopeTypeName) {
		IScope scope = scoped.getScope();
		if (!scopeTypeName.equals(scope.getSourceType().getFullyQualifiedName())) {
			Set<IScopeDeclaration> scopeDeclarations = scoped.getScopeDeclarations();
			if (!scopeDeclarations.isEmpty()) {
				return scopeDeclarations.iterator().next();
			}
			if (scoped instanceof IStereotyped) {
				Set<IStereotypeDeclaration> stereoTypeDeclarations = ((IStereotyped) scoped).getStereotypeDeclarations();
				for (IStereotypeDeclaration stereotypeDeclaration : stereoTypeDeclarations) {
					IStereotype stereotype = stereotypeDeclaration.getStereotype();
					IScope stereotypeScope = stereotype.getScope();
					if (stereotypeScope != null && !scopeTypeName.equals(stereotypeScope.getSourceType().getFullyQualifiedName())) {
						return stereotypeDeclaration;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the scope annotation declaration if it exists in the bean. If the
	 * scope declared in a stereotype then returns this stereotype declaration.
	 * Returns null if there is not this scope declaration neither corresponding
	 * stereotype declaration.
	 * 
	 * @param bean
	 * @param scopeTypeName
	 * @return
	 */
	public static IAnnotationDeclaration getScopeDeclaration(IBean bean, String scopeTypeName) {
		IScope scope = bean.getScope();
		if (scopeTypeName.equals(scope.getSourceType().getFullyQualifiedName())) {
			Set<IScopeDeclaration> scopeDeclarations = bean.getScopeDeclarations();
			for (IScopeDeclaration scopeDeclaration : scopeDeclarations) {
				if (scopeTypeName.equals(scopeDeclaration.getScope().getSourceType().getFullyQualifiedName())) {
					return scopeDeclaration;
				}
			}
			Set<IStereotypeDeclaration> stereoTypeDeclarations = bean.getStereotypeDeclarations();
			for (IStereotypeDeclaration stereotypeDeclaration : stereoTypeDeclarations) {
				IScope stereotypeScope = stereotypeDeclaration.getStereotype().getScope();
				if (stereotypeScope != null && scopeTypeName.equals(stereotypeScope.getSourceType().getFullyQualifiedName())) {
					return stereotypeDeclaration;
				}
			}
		}
		return null;
	}

	/**
	 * Return @Named declaration or the stereotype declaration if it declares @Named.
	 * 
	 * @param stereotyped
	 * @return
	 */
	public static IAnnotationDeclaration getNamedDeclaration(IBean bean) {
		return getQualifierDeclaration(bean, CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
	}

	/**
	 * Return the qualifier declaration or the stereotype or @Specializes declaration if it declares this qualifier.
	 * 
	 * @param stereotyped
	 * @return
	 */
	public static IAnnotationDeclaration getQualifierDeclaration(IBean bean, String qualifierTypeName) {
		IAnnotationDeclaration declaration = bean.getAnnotation(qualifierTypeName);
		if (declaration == null) {
			declaration = getQualifiedStereotypeDeclaration(bean, qualifierTypeName);
		}
		if(declaration == null) {
			declaration = getQualifiedSpecializesDeclaration(bean, qualifierTypeName);
		}
		return declaration;
	}

	/**
	 * Returns the @Specializes declaration of the bean if the specialized bean declares the given qualifier.
	 * 
	 * @param bean
	 * @param qualifierTypeName
	 * @return
	 */
	public static IAnnotationDeclaration getQualifiedSpecializesDeclaration(IBean bean, String qualifierTypeName) {
		IBean specializedBean = bean.getSpecializedBean();
		return specializedBean!=null?getQualifierDeclaration(bean, qualifierTypeName):null;
	}

	/**
	 * Return the stereotype declaration which declares the given qualifier.
	 * 
	 * @param stereotyped
	 * @return
	 */
	public static IAnnotationDeclaration getQualifiedStereotypeDeclaration(IStereotyped stereotyped, String qualifierTypeName) {
		Set<IStereotypeDeclaration> declarations = stereotyped.getStereotypeDeclarations();
		for (IStereotypeDeclaration declaration : declarations) {
			if (qualifierTypeName.equals(declaration.getType().getFullyQualifiedName())
					|| getQualifiedStereotypeDeclaration(declaration.getStereotype(), qualifierTypeName) != null) {
				return declaration;
			}
		}
		return null;
	}

	/**
	 * Return the stereotype declaration which declares @Named.
	 * 
	 * @param stereotyped
	 * @return
	 */
	public static IAnnotationDeclaration getNamedStereotypeDeclaration(IStereotyped stereotyped) {
		return getQualifiedStereotypeDeclaration(stereotyped, CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
	}

	/**
	 * Returns all found annotations for parameters of the method.
	 * 
	 * @param method
	 * @param annotationTypeName
	 * @return
	 */
	public static Set<ITextSourceReference> getAnnotationPossitions(IBeanMethod method, String annotationTypeName) {
		List<IParameter> params = method.getParameters();
		Set<ITextSourceReference> declarations = new HashSet<ITextSourceReference>();
		for (IParameter param : params) {
			ITextSourceReference declaration = param.getAnnotationPosition(annotationTypeName);
			if (declaration != null) {
				declarations.add(declaration);
			}
		}
		return declarations;
	}

	/**
	 * Returns true if the class bean is a session bean.
	 * 
	 * @param bean
	 * @return
	 */
	public static IAnnotationDeclaration getSessionDeclaration(IClassBean bean) {
		IAnnotationDeclaration declaration = bean.getAnnotation(CDIConstants.STATEFUL_ANNOTATION_TYPE_NAME);
		if(declaration!=null) {
			return declaration;
		}
		declaration = bean.getAnnotation(CDIConstants.STATELESS_ANNOTATION_TYPE_NAME);
		if(declaration!=null) {
			return declaration;
		}
		declaration = bean.getAnnotation(CDIConstants.SINGLETON_ANNOTATION_TYPE_NAME);
		return declaration;
	}

	/**
	 * Returns true if the class bean is a session bean.
	 * 
	 * @param bean
	 * @return
	 */
	public static boolean isSessionBean(IBean bean) {
		return bean instanceof ISessionBean || (bean instanceof IClassBean && (bean.getAnnotation(CDIConstants.STATEFUL_ANNOTATION_TYPE_NAME)!=null || bean.getAnnotation(CDIConstants.STATELESS_ANNOTATION_TYPE_NAME)!=null || bean.getAnnotation(CDIConstants.SINGLETON_ANNOTATION_TYPE_NAME)!=null));
	}

	/**
	 * Returns true if the class bean is a decorator.
	 * 
	 * @param bean
	 * @return
	 */
	public static boolean isDecorator(IBean bean) {
		return bean instanceof IDecorator || (bean instanceof IClassBean && bean.getAnnotation(CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME)!=null);
	}

	/**
	 * Returns true if the class bean is an interceptor.
	 * 
	 * @param bean
	 * @return
	 */
	public static boolean isInterceptor(IBean bean) {
		return bean instanceof IInterceptor || (bean instanceof IClassBean && bean.getAnnotation(CDIConstants.INTERCEPTOR_ANNOTATION_TYPE_NAME)!=null);
	}

	/**
	 * Returns false if the method is a non-static method of the session bean class, and the method is not a business method of the session bean.
	 * 
	 * @param bean
	 * @param method
	 * @return
	 */
	public static boolean isBusinessMethod(ISessionBean bean, IBeanMethod method) {
		return getBusinessMethodDeclaration(bean, method)!=null;
	}

	/**
	 * Returns IMethod of @Local interface which is implemented by given business method.
	 * Returns null if the method is a non-static method of the session bean class, and the method is not a business method of the session bean.
	 * If the method is a static one then returns this method.
	 * 
	 * @param bean
	 * @param method
	 * @return
	 */
	public static IMethod getBusinessMethodDeclaration(ISessionBean bean, IBeanMethod method) {
		try {
			if (!Flags.isStatic(method.getMethod().getFlags())) {
				Set<IParametedType> types = bean.getLegalTypes();
				for (IParametedType type : types) {
					IType sourceType = type.getType();
					if (sourceType == null) {
						continue;
					}
					if(!sourceType.isInterface()) {
						continue;
					}
					IAnnotation annotation = sourceType.getAnnotation(CDIConstants.LOCAL_ANNOTATION_TYPE_NAME);
					if (annotation == null) {
						annotation = sourceType.getAnnotation("Local"); //$NON-NLS-N1
					}
					if (annotation != null && CDIConstants.LOCAL_ANNOTATION_TYPE_NAME.equals(EclipseJavaUtil.resolveType(sourceType, "Local"))) { //$NON-NLS-N1
						IMethod[] methods = sourceType.getMethods();
						for (IMethod iMethod : methods) {
							if (method.getMethod().isSimilar(iMethod)) {
								return iMethod;
							}
						}
						break;
					}
				}
				return null;
			}
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return method.getMethod();
	}

	/**
	 * Finds the method which is overridden by the given method. Or null if this method overrides nothing. 
	 *    
	 * @param method
	 * @return
	 */
	public static IMethod getOverridingMethodDeclaration(IBeanMethod method) {
		IClassBean bean = method.getClassBean();
		Map<IType, IMethod> foundMethods = new HashMap<IType, IMethod>();
		try {
			if (Flags.isStatic(method.getMethod().getFlags())) {
				return null;
			}
			Set<IParametedType> types = bean.getLegalTypes();
			for (IParametedType type : types) {
				IType sourceType = type.getType();
				if (sourceType == null || sourceType.isInterface()) {
					continue;
				}
				IMethod[] methods = sourceType.getMethods();
				for (IMethod iMethod : methods) {
					if (method.getMethod().isSimilar(iMethod)) {
						foundMethods.put(iMethod.getDeclaringType(), iMethod);
					}
				}
			}
			if(foundMethods.size()==1) {
				return foundMethods.values().iterator().next();
			} else if(foundMethods.size()>1) {
				IType type = bean.getBeanClass();
				IType superClass = getSuperClass(type);
				while(superClass!=null) {
					IMethod m = foundMethods.get(superClass);
					if(m!=null) {
						return m;
					}
					superClass = getSuperClass(superClass);
				}
			}
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return null;
	}

	/**
	 * Finds the method which is overridden by the given method. Or null if this method overrides nothing. 
	 *    
	 * @param method
	 * @return
	 */
	public static IMethod getDirectOverridingMethodDeclaration(IBeanMethod method) {
		IClassBean bean = method.getClassBean();
		try {
			if (Flags.isStatic(method.getMethod().getFlags())) {
				return null;
			}
			IType type = bean.getBeanClass();
			IType superClass = getSuperClass(type);
			if(superClass!=null) {
				IMethod[] methods = superClass.getMethods();
				for (IMethod iMethod : methods) {
					if (method.getMethod().isSimilar(iMethod)) {
						return iMethod;
					}
				}
			}
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return null;
	}

	/**
	 * Returns all the injection point parameters of the bean class.
	 *  
	 * @param bean
	 * @return
	 */
	public static Set<IInjectionPointParameter> getInjectionPointParameters(IClassBean bean) {
		Set<IInjectionPoint> points = bean.getInjectionPoints();
		Set<IInjectionPointParameter> params = new HashSet<IInjectionPointParameter>();
		for (IInjectionPoint injection : points) {
			if(injection instanceof IInjectionPointParameter) {
				params.add((IInjectionPointParameter)injection);
			}
		}
		return params;
	}

	/**
	 * Returns true if the method is generic
	 * 
	 * @param method
	 * @return
	 */
	public static boolean isMethodGeneric(IBeanMethod method) {
		try {
			return method.getMethod().getTypeParameters().length>0;
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return false;
	}

	/**
	 * Returns true if the method is static
	 * 
	 * @param method
	 * @return
	 */
	public static boolean isMethodStatic(IBeanMethod method) {
		try {
			return Flags.isStatic(method.getMethod().getFlags());
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return false;
	}

	/**
	 * Returns true if the method is abstract
	 * 
	 * @param method
	 * @return
	 */
	public static boolean isMethodAbstract(IBeanMethod method) {
		try {
			return Flags.isAbstract(method.getMethod().getFlags());
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return false;
	}

	/**
	 * Checks if the bean member has a type variable as a type.
	 * If the bean member is a field then checks its type.
	 * If the bean member is a parameter of a method then checks its type.
	 * If the bean member is a method then checks its return type.
	 * 
	 * @param member
	 * @param checkGenericMethod if true then checks if this member use a type variable which is declared in the generic method (in case of the member is a method).
	 * @return
	 */
	public static boolean isTypeVariable(IBeanMember member, boolean checkGenericMethod) {
		try {
			String[] typeVariableSegnatures = member.getClassBean().getBeanClass().getTypeParameterSignatures();
			List<String> variables = new ArrayList<String>();
			for (String variableSig : typeVariableSegnatures) {
				variables.add(Signature.getTypeVariable(variableSig));
			}
			if(checkGenericMethod) {
				ITypeParameter[] typeParams = null;
				if(member instanceof IParameter) {
					typeParams = ((IParameter)member).getBeanMethod().getMethod().getTypeParameters();
				} if(member instanceof IBeanMethod) {
					typeParams = ((IBeanMethod)member).getMethod().getTypeParameters();
				}
				if(typeParams!=null) {
					for (ITypeParameter param : typeParams) {
						variables.add(param.getElementName());
					}
				}
			}
			String signature = null;
			if(member instanceof IBeanField) {
				signature = ((IBeanField)member).getField().getTypeSignature();
			} else if(member instanceof IParameter) {
				if(((IParameter)member).getType()==null) {
					return false;
				}
				signature = ((IParameter)member).getType().getSignature();
			} else if(member instanceof IBeanMethod) {
				signature = ((IBeanMethod)member).getMethod().getReturnType();
			}
			return isTypeVariable(variables, signature);
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return false;
	}

	private static boolean isTypeVariable(List<String> typeVariables, String signature) {
		if(signature==null) {
			return false;
		}
		String typeString = Signature.toString(signature);
		for (String variableName : typeVariables) {
			if(typeString.equals(variableName)) {
				return true;
			}
		}
		return false;
	}

	private static IType getSuperClass(IType type) throws JavaModelException {
		String superclassName = type.getSuperclassName();
		if(superclassName!=null) {
			String fullySuperclassName = EclipseJavaUtil.resolveType(type, superclassName);
			if(fullySuperclassName!=null&&!fullySuperclassName.equals("java.lang.Object")) { //$NON-NLS-1$
				if(fullySuperclassName.equals(type.getFullyQualifiedName())) {
					return null;
				}
				IType superType = type.getJavaProject().findType(fullySuperclassName);
				return superType;
			}
		}
		return null;
	}

	/**
	 * Returns true if the member annotated @NonBinding.
	 * 
	 * @param sourceType the type where the member is declared
	 * @param member
	 * @return
	 */
	public static boolean hasNonBindingAnnotationDeclaration(IType sourceType, IAnnotatable member) {
		return hasAnnotationDeclaration(sourceType, member, CDIConstants.NON_BINDING_ANNOTATION_TYPE_NAME);
	}

	/**
	 * Returns true if the member has the given annotation.
	 * 
	 * @param sourceType the type where the member is declared
	 * @param member
	 * @param annotationTypeName
	 * @return
	 */
	public static boolean hasAnnotationDeclaration(IType sourceType, IAnnotatable member, String annotationTypeName) {
		try {
			IAnnotation[] annotations = member.getAnnotations();
			String simpleAnnotationTypeName = annotationTypeName;
			int lastDot = annotationTypeName.lastIndexOf('.');
			if(lastDot>-1) {
				simpleAnnotationTypeName = simpleAnnotationTypeName.substring(lastDot + 1);
			}
			for (IAnnotation annotation : annotations) {
				if(annotationTypeName.equals(annotation.getElementName())) {
					return true;
				}
				if(simpleAnnotationTypeName.equals(annotation.getElementName())) {
					String fullAnnotationclassName = EclipseJavaUtil.resolveType(sourceType, simpleAnnotationTypeName);
					if(fullAnnotationclassName!=null) {
						IType annotationType = sourceType.getJavaProject().findType(fullAnnotationclassName);
						if(annotationType!=null && annotationType.getFullyQualifiedName().equals(annotationTypeName)) {
							return true;
						}
					}
				}
			}
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return false;
	}

	/**
	 * Converts ISourceRange to ITextSourceReference
	 * 
	 * @param range
	 * @return
	 */
	public static ITextSourceReference convertToSourceReference(final ISourceRange range) {
		return new ITextSourceReference() {

			public int getStartPosition() {
				return range.getOffset();
			}

			public int getLength() {
				return range.getLength();
			}
		};
	}
}