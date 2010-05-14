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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.jboss.tools.common.EclipseUtil;
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
	public static IInjectionPoint findInjectionPoint(Set<IBean> beans, IJavaElement element) {
		if (!(element instanceof IField) && (element instanceof IMethod)) {
			return null;
		}

		for (IBean bean : beans) {
			Set<IInjectionPoint> injectionPoints = bean.getInjectionPoints();
			for (IInjectionPoint iPoint : injectionPoints) {
				if (element instanceof IField && iPoint instanceof IInjectionPointField) {
					if (((IInjectionPointField) iPoint).getField() != null && ((IInjectionPointField) iPoint).getField().equals(element)) {
						return iPoint;
					}
				} else if (element instanceof IMethod && iPoint instanceof IInjectionPointMethod) {
					if (((IInjectionPointMethod) iPoint).getMethod() != null && ((IInjectionPointMethod) iPoint).getMethod().equals(element)) {
						return iPoint;
					}
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
	 * Return @Named declaration or the stereotype declaration if it declares
	 * 
	 * @Named.
	 * 
	 * @param stereotyped
	 * @return
	 */
	public static IAnnotationDeclaration getNamedDeclaration(IBean bean) {
		IAnnotationDeclaration declaration = bean.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
		if (declaration == null) {
			return getNamedStereotypeDeclaration(bean);
		}
		return declaration;
	}

	/**
	 * Return the stereotype declaration which declares @Named.
	 * 
	 * @param stereotyped
	 * @return
	 */
	public static IAnnotationDeclaration getNamedStereotypeDeclaration(IStereotyped stereotyped) {
		Set<IStereotypeDeclaration> declarations = stereotyped.getStereotypeDeclarations();
		for (IStereotypeDeclaration declaration : declarations) {
			if (CDIConstants.NAMED_QUALIFIER_TYPE_NAME.equals(declaration.getType().getFullyQualifiedName())
					|| getNamedStereotypeDeclaration(declaration.getStereotype()) != null) {
				return declaration;
			}
		}
		return null;
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
}