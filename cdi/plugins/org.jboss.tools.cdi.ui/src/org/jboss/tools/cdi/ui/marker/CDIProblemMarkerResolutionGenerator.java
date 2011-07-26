/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.marker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationErrorManager;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

/**
 * @author Daniel Azarov
 */
public class CDIProblemMarkerResolutionGenerator implements
		IMarkerResolutionGenerator2 {
	private static final String JAVA_EXTENSION = "java"; //$NON-NLS-1$
	private static final int MARKER_RESULUTION_NUMBER_LIMIT = 7;

	public IMarkerResolution[] getResolutions(IMarker marker) {
		try {
			return findResolutions(marker);
		} catch (CoreException ex) {
			CDIUIPlugin.getDefault().logError(ex);
		}
		return new IMarkerResolution[] {};
	}
	
	/**
	 * return message id or -1 if impossible to find
	 * @param marker
	 * @return
	 */
	private int getMessageID(IMarker marker)throws CoreException{
		Integer attribute = ((Integer) marker.getAttribute(CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME));
		if (attribute != null)
			return attribute.intValue();

		return -1; 
	}

	private IMarkerResolution[] findResolutions(IMarker marker)
			throws CoreException {

		int messageId = getMessageID(marker);
		if (messageId == -1)
			return new IMarkerResolution[] {};

		final IFile file = (IFile) marker.getResource();

		Integer attribute = ((Integer) marker.getAttribute(IMarker.CHAR_START));
		if (attribute == null)
			return new IMarkerResolution[] {};
		final int start = attribute.intValue();

		if (JAVA_EXTENSION.equals(file.getFileExtension())) {
			if (messageId == CDIValidationErrorManager.ILLEGAL_PRODUCER_FIELD_IN_SESSION_BEAN_ID) {
				IField field = findNonStaticField(file, start);
				if(field != null){
					return new IMarkerResolution[] {
						new MakeFieldStaticMarkerResolution(field, file)
					};
				}
			}else if (messageId == CDIValidationErrorManager.ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN_ID || 
					messageId == CDIValidationErrorManager.ILLEGAL_DISPOSER_IN_SESSION_BEAN_ID ||
					messageId == CDIValidationErrorManager.ILLEGAL_OBSERVER_IN_SESSION_BEAN_ID) {
				IMethod method = findMethod(file, start);
				if(method != null){
					List<IType> types = findLocalAnnotattedInterfaces(method);
					if(types.size() == 0 && !Flags.isPublic(method.getFlags())){
						return new IMarkerResolution[] {
							new MakeMethodPublicMarkerResolution(method, file)
						};
					}else{
						IMarkerResolution[] resolutions = new IMarkerResolution[types.size()+1];
						for(int i = 0; i < types.size(); i++){
							resolutions[i] = new MakeMethodBusinessMarkerResolution(method, types.get(i), file);
						}
						resolutions[types.size()] = new AddLocalBeanMarkerResolution(method, file);
						return resolutions;
					}
				}
			}else if (messageId == CDIValidationErrorManager.MULTIPLE_DISPOSERS_FOR_PRODUCER_ID) {
				IMethod method = findMethod(file, start);
				if(method != null){
					return new IMarkerResolution[] {
							new DeleteAllDisposerDuplicantMarkerResolution(method, file)
						};
				}
			}else if (messageId == CDIValidationErrorManager.MULTIPLE_INJECTION_CONSTRUCTORS_ID) {
				IMethod method = findMethod(file, start);
				if(method != null){
					return new IMarkerResolution[] {
							new DeleteAllInjectedConstructorsMarkerResolution(method, file)
						};
				}
			}else if(messageId == CDIValidationErrorManager.AMBIGUOUS_INJECTION_POINTS_ID){
				IInjectionPoint injectionPoint = findInjectionPoint(file, start);
				if(injectionPoint != null){
					List<IBean> beans = findBeans(injectionPoint);
					if(beans.size() < MARKER_RESULUTION_NUMBER_LIMIT){
						IMarkerResolution[] resolutions = new IMarkerResolution[beans.size()];
						for(int i = 0; i < beans.size(); i++){
							resolutions[i] = new MakeInjectedPointUnambiguousMarkerResolution(injectionPoint, beans, i);
						}
						return resolutions;
					}else{
						IMarkerResolution[] resolutions = new IMarkerResolution[1];
						resolutions[0] = new SelectBeanMarkerResolution(injectionPoint, beans);
						return resolutions;
					}
				}
			}else if(messageId == CDIValidationErrorManager.UNSATISFIED_INJECTION_POINTS_ID){
				IInjectionPoint injectionPoint = findInjectionPoint(file, start);
				if(injectionPoint != null){
					
					List<IBean> beans = findLegalBeans(injectionPoint);
			    	if(beans.size() < MARKER_RESULUTION_NUMBER_LIMIT){
						IMarkerResolution[] resolutions = new IMarkerResolution[beans.size()];
						for(int i = 0; i < beans.size(); i++){
							resolutions[i] = new MakeInjectedPointUnambiguousMarkerResolution(injectionPoint, beans, i);
						}
						return resolutions;
					}else{
						IMarkerResolution[] resolutions = new IMarkerResolution[1];
						resolutions[0] = new SelectBeanMarkerResolution(injectionPoint, beans);
						return resolutions;
					}
				}
			}else if(messageId == CDIValidationErrorManager.NOT_PASSIVATION_CAPABLE_BEAN_ID){
				IType type = findTypeWithNoSerializable(file, start);
				
				if(type != null){
					return new IMarkerResolution[] {
							new AddSerializableInterfaceMarkerResolution(type, file)
						};
				}
			}else if(messageId == CDIValidationErrorManager.ILLEGAL_SCOPE_FOR_MANAGED_BEAN_WITH_PUBLIC_FIELD_ID){
				IField field = findPublicField(file, start);
				CDICoreNature cdiNature = CDIUtil.getCDINatureWithProgress(file.getProject());
				if(cdiNature != null){
					ICDIProject cdiProject = cdiNature.getDelegate();
					
					if(cdiProject != null){
						Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
						Iterator<IBean> iter = beans.iterator();
						if(iter.hasNext()){
							IBean bean = iter.next();
							if(field != null){
								return new IMarkerResolution[] {
										new MakeFieldProtectedMarkerResolution(field, file),
										new MakeBeanScopedDependentMarkerResolution(bean, file)
									};
							}
						}
					}
				}
			}else if(messageId == CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE_ID ||
					messageId == CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_SCOPE_TYPE_ID ||
					messageId == CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_STEREOTYPE_TYPE_ID){
				
				TypeAndAnnotation ta = findTypeAndAnnotation(file, start, CDIConstants.RETENTION_ANNOTATION_TYPE_NAME);
				if(ta != null){
					if(ta.annotation == null){
						return new IMarkerResolution[] {
								new AddRetentionAnnotationMarkerResolution(ta.type)
							};
					}else{
						return new IMarkerResolution[] {
								new ChangeRetentionAnnotationMarkerResolution(ta.type, ta.annotation)
							};
						
					}
				}
			}else if(messageId == CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE_ID){
				TypeAndAnnotation ta = findTypeAndAnnotation(file, start, CDIConstants.TARGET_ANNOTATION_TYPE_NAME);
				if(ta != null){
					if(ta.annotation == null){
						return new IMarkerResolution[] {
								new AddTargetAnnotationMarkerResolution(ta.type, new String[]{CDIConstants.ELEMENT_TYPE_TYPE_NAME, CDIConstants.ELEMENT_TYPE_METHOD_NAME, CDIConstants.ELEMENT_TYPE_FIELD_NAME, CDIConstants.ELEMENT_TYPE_PARAMETER_NAME}),
								new AddTargetAnnotationMarkerResolution(ta.type, new String[]{CDIConstants.ELEMENT_TYPE_FIELD_NAME, CDIConstants.ELEMENT_TYPE_PARAMETER_NAME})
							};
					}else{
						return new IMarkerResolution[] {
								new ChangeTargetAnnotationMarkerResolution(ta.type, ta.annotation, new String[]{CDIConstants.ELEMENT_TYPE_TYPE_NAME, CDIConstants.ELEMENT_TYPE_METHOD_NAME, CDIConstants.ELEMENT_TYPE_FIELD_NAME, CDIConstants.ELEMENT_TYPE_PARAMETER_NAME}),
								new ChangeTargetAnnotationMarkerResolution(ta.type, ta.annotation, new String[]{CDIConstants.ELEMENT_TYPE_FIELD_NAME, CDIConstants.ELEMENT_TYPE_PARAMETER_NAME})
							};
						
					}
				}
			}else if(messageId == CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE_ID){
				TypeAndAnnotation ta = findTypeAndAnnotation(file, start, CDIConstants.TARGET_ANNOTATION_TYPE_NAME);
				if(ta != null){
					if(ta.annotation == null){
						return new IMarkerResolution[] {
							new AddTargetAnnotationMarkerResolution(ta.type, new String[]{CDIConstants.ELEMENT_TYPE_TYPE_NAME, CDIConstants.ELEMENT_TYPE_METHOD_NAME, CDIConstants.ELEMENT_TYPE_FIELD_NAME}),
							new AddTargetAnnotationMarkerResolution(ta.type, new String[]{CDIConstants.ELEMENT_TYPE_METHOD_NAME, CDIConstants.ELEMENT_TYPE_FIELD_NAME}),
							new AddTargetAnnotationMarkerResolution(ta.type, new String[]{CDIConstants.ELEMENT_TYPE_TYPE_NAME}),
							new AddTargetAnnotationMarkerResolution(ta.type, new String[]{CDIConstants.ELEMENT_TYPE_METHOD_NAME}),
							new AddTargetAnnotationMarkerResolution(ta.type, new String[]{CDIConstants.ELEMENT_TYPE_FIELD_NAME})
						};
					}else{
						return new IMarkerResolution[] {
							new ChangeTargetAnnotationMarkerResolution(ta.type, ta.annotation, new String[]{CDIConstants.ELEMENT_TYPE_TYPE_NAME, CDIConstants.ELEMENT_TYPE_METHOD_NAME, CDIConstants.ELEMENT_TYPE_FIELD_NAME}),
							new ChangeTargetAnnotationMarkerResolution(ta.type, ta.annotation, new String[]{CDIConstants.ELEMENT_TYPE_METHOD_NAME, CDIConstants.ELEMENT_TYPE_FIELD_NAME}),
							new ChangeTargetAnnotationMarkerResolution(ta.type, ta.annotation, new String[]{CDIConstants.ELEMENT_TYPE_TYPE_NAME}),
							new ChangeTargetAnnotationMarkerResolution(ta.type, ta.annotation, new String[]{CDIConstants.ELEMENT_TYPE_METHOD_NAME}),
							new ChangeTargetAnnotationMarkerResolution(ta.type, ta.annotation, new String[]{CDIConstants.ELEMENT_TYPE_FIELD_NAME})
						};
					
					}
				}
			}else if(messageId == CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE_ID){
				TypeAndAnnotation ta = findTypeAndAnnotation(file, start, CDIConstants.TARGET_ANNOTATION_TYPE_NAME);
				if(ta != null){
					if(ta.annotation == null){
						return new IMarkerResolution[] {
							new AddTargetAnnotationMarkerResolution(ta.type, new String[]{CDIConstants.ELEMENT_TYPE_TYPE_NAME, CDIConstants.ELEMENT_TYPE_METHOD_NAME, CDIConstants.ELEMENT_TYPE_FIELD_NAME})
						};
					}else{
						return new IMarkerResolution[] {
							new ChangeTargetAnnotationMarkerResolution(ta.type, ta.annotation, new String[]{CDIConstants.ELEMENT_TYPE_TYPE_NAME, CDIConstants.ELEMENT_TYPE_METHOD_NAME, CDIConstants.ELEMENT_TYPE_FIELD_NAME})
						};
					
					}
				}
			}else if(messageId == CDIValidationErrorManager.MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_INTERCEPTOR_BINDING_TYPE_MEMBER_ID ||
					messageId == CDIValidationErrorManager.MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_QUALIFIER_TYPE_MEMBER_ID ||
					messageId == CDIValidationErrorManager.MISSING_NONBINDING_FOR_ARRAY_VALUE_IN_INTERCEPTOR_BINDING_TYPE_MEMBER_ID ||
					messageId == CDIValidationErrorManager.MISSING_NONBINDING_FOR_ARRAY_VALUE_IN_QUALIFIER_TYPE_MEMBER_ID){
				IJavaElement element = findJavaElement(file, start);
				if(element != null){
					IAnnotation annotation = getAnnotation(element, CDIConstants.NON_BINDING_ANNOTATION_TYPE_NAME);
					if(element instanceof IMember && annotation == null){
						return new IMarkerResolution[] {
							new AddAnnotationMarkerResolution((IMember)element, CDIConstants.NON_BINDING_ANNOTATION_TYPE_NAME)
						};
					}
				}
			}else if(messageId == CDIValidationErrorManager.DISPOSER_ANNOTATED_INJECT_ID){
				IJavaElement element = findJavaElement(file, start);
				if(element != null){
					IJavaElement injectElement = findJavaElementByAnnotation(element, CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
					IJavaElement disposesElement = findJavaElementByAnnotation(element, CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME);
					if(injectElement != null && disposesElement != null){
						return new IMarkerResolution[] {
							new DeleteAnnotationMarkerResolution(injectElement, CDIConstants.INJECT_ANNOTATION_TYPE_NAME),
							new DeleteAnnotationMarkerResolution(disposesElement, CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME)
						};
					}
				}
			}else if(messageId == CDIValidationErrorManager.PRODUCER_ANNOTATED_INJECT_ID){
				IJavaElement element = findJavaElement(file, start);
				if(element != null){
					IJavaElement injectElement = findJavaElementByAnnotation(element, CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
					IJavaElement produsesElement = findJavaElementByAnnotation(element, CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME);
					if(injectElement != null && produsesElement != null){
						return new IMarkerResolution[] {
							new DeleteAnnotationMarkerResolution(injectElement, CDIConstants.INJECT_ANNOTATION_TYPE_NAME),
							new DeleteAnnotationMarkerResolution(produsesElement, CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME)
						};
					}
				}
			}else if(messageId == CDIValidationErrorManager.OBSERVER_ANNOTATED_INJECT_ID){
				IJavaElement element = findJavaElement(file, start);
				if(element != null){
					IJavaElement injectElement = findJavaElementByAnnotation(element, CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
					IJavaElement observerElement = findJavaElementByAnnotation(element, CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME);
					if(injectElement != null && observerElement != null){
						return new IMarkerResolution[] {
							new DeleteAnnotationMarkerResolution(injectElement, CDIConstants.INJECT_ANNOTATION_TYPE_NAME),
							new DeleteAnnotationMarkerResolution(observerElement, CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME)
						};
					}
				}
			}else if(messageId == CDIValidationErrorManager.CONSTRUCTOR_PARAMETER_ANNOTATED_OBSERVES_ID){
				IJavaElement element = findJavaElement(file, start);
				if(element != null){
					IJavaElement observerElement = findJavaElementByAnnotation(element, CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME);
					if(observerElement != null){
						return new IMarkerResolution[] {
							new DeleteAnnotationMarkerResolution(observerElement, CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME)
						};
					}
				}
			}else if(messageId == CDIValidationErrorManager.DISPOSER_IN_INTERCEPTOR_ID ||
					messageId == CDIValidationErrorManager.DISPOSER_IN_DECORATOR_ID ||
					messageId == CDIValidationErrorManager.CONSTRUCTOR_PARAMETER_ANNOTATED_DISPOSES_ID){
				IJavaElement element = findJavaElement(file, start);
				if(element != null){
					IJavaElement disposerElement = findJavaElementByAnnotation(element, CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME);
					if(disposerElement != null){
						return new IMarkerResolution[] {
							new DeleteAnnotationMarkerResolution(disposerElement, CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME)
						};
					}
				}
			}else if(messageId == CDIValidationErrorManager.PRODUCER_IN_INTERCEPTOR_ID ||
					messageId == CDIValidationErrorManager.PRODUCER_IN_DECORATOR_ID){
				IJavaElement element = findJavaElement(file, start);
				if(element != null){
					IJavaElement producerElement = findJavaElementByAnnotation(element, CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME);
					if(producerElement != null){
						return new IMarkerResolution[] {
							new DeleteAnnotationMarkerResolution(producerElement, CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME)
						};
					}
				}
			}else if(messageId == CDIValidationErrorManager.STEREOTYPE_DECLARES_NON_EMPTY_NAME_ID){
				TypeAndAnnotation ta = findTypeAndAnnotation(file, start, CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
				if(ta != null && ta.annotation != null && ta.type != null){
					return new IMarkerResolution[] {
						new ChangeAnnotationMarkerResolution(ta.annotation),
						new DeleteAnnotationMarkerResolution(ta.type, CDIConstants.NAMED_QUALIFIER_TYPE_NAME)
					};
				}
			}else if(messageId == CDIValidationErrorManager.INTERCEPTOR_HAS_NAME_ID ||
					messageId == CDIValidationErrorManager.DECORATOR_HAS_NAME_ID){
				TypeAndAnnotation ta = findTypeAndAnnotation(file, start, CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
				if(ta != null && ta.annotation != null && ta.type != null){
					return new IMarkerResolution[] {
						new DeleteAnnotationMarkerResolution(ta.type, CDIConstants.NAMED_QUALIFIER_TYPE_NAME)
					};
				}
			}
		}
		return new IMarkerResolution[] {};
	}
	
	private List<IBean> findLegalBeans(IInjectionPoint injectionPoint){
		IBean[] bs = injectionPoint.getCDIProject().getBeans();
		
		String injectionPointTypeName = injectionPoint.getClassBean().getBeanClass().getFullyQualifiedName();
		String injectionPointPackage = null;
		
		int dotLastIndex = injectionPointTypeName.lastIndexOf(MarkerResolutionUtils.DOT);
		
		if(dotLastIndex < 0)
			injectionPointPackage = "";
		else
			injectionPointPackage = injectionPointTypeName.substring(0, dotLastIndex);

		ArrayList<IBean> beans = new ArrayList<IBean>();
    	for(IBean bean : bs){
    		if(CDIProject.containsType(bean.getLegalTypes(), injectionPoint.getType())){
    			boolean isPublic = true;
				try{
					isPublic = Flags.isPublic(bean.getBeanClass().getFlags());
				}catch(JavaModelException ex){
					CDIUIPlugin.getDefault().logError(ex);
				}
    			String beanTypeName = bean.getBeanClass().getFullyQualifiedName();
    			String beanPackage = null;
    			
    			dotLastIndex = beanTypeName.lastIndexOf(MarkerResolutionUtils.DOT);
    			
    			if(dotLastIndex < 0)
    				beanPackage = "";
    			else
    				beanPackage = beanTypeName.substring(0,dotLastIndex);
    			
    			if(isPublic || injectionPointPackage.equals(beanPackage))
    				beans.add(bean);
    		}
    	}
    	return beans;
	}
	
	
	private IInjectionPoint findInjectionPoint(IFile file, int start){
		IJavaElement element = findJavaElement(file, start);
		if(element == null)
			return null;
		
		CDICoreNature cdiNature = CDIUtil.getCDINatureWithProgress(file.getProject());
		if(cdiNature == null)
			return null;

		
		ICDIProject cdiProject = cdiNature.getDelegate();
		
		if(cdiProject == null){
			return null;
		}
		
		Set<IBean> allBeans = CDIUtil.getFilteredBeans(cdiProject, file.getFullPath());
		
		IInjectionPoint ip = CDIUtil.findInjectionPoint(allBeans, element, start);
		
		return ip;
	}
	
	private List<IBean> findBeans(IInjectionPoint injectionPoint){
		ICDIProject cdiProject = injectionPoint.getCDIProject();
		return CDIUtil.getSortedBeans(cdiProject, false, injectionPoint);
	}
	
	private IMethod findMethod(IFile file, int start){
		IJavaElement javaElement = findJavaElement(file, start);
		if(javaElement != null && javaElement instanceof IMethod){
			IMethod method = (IMethod)javaElement;
			if(!method.isBinary())
				return method;
		}
		return null;
	}

	private IType findTypeWithNoSerializable(IFile file, int start) throws JavaModelException{
		IJavaElement javaElement = findJavaElement(file, start);
		if(javaElement != null && javaElement instanceof IType){
			IType type = (IType)javaElement;
			if(!type.isBinary()){
				String shortName = MarkerResolutionUtils.getShortName(AddSerializableInterfaceMarkerResolution.SERIALIZABLE);
				String[] interfaces = type.getSuperInterfaceNames();
				for(String name : interfaces){
					if(name.equals(shortName))
						return null;
				}
				return type;
			}
		}
		return null;
	}
	
	class TypeAndAnnotation{
		IType type;
		IAnnotation annotation;
		
		public TypeAndAnnotation(IType type){
			this.type = type;
		}
		
		public TypeAndAnnotation(IType type, IAnnotation annotation){
			this(type);
			this.annotation = annotation;
		}
	}
	
	private TypeAndAnnotation findTypeAndAnnotation(IFile file, int start, String annotationQualifiedName) throws JavaModelException{
		IJavaElement javaElement = findJavaElement(file, start);
		if(javaElement != null && javaElement instanceof IType){
			IType type = (IType)javaElement;
			if(!type.isBinary()){
				IAnnotation annotation = getAnnotation(type, annotationQualifiedName);
				if(annotation != null){
					return new TypeAndAnnotation(type, annotation);
				}
				return new TypeAndAnnotation(type);
			}
		}
		return null;
	}
	
	private IJavaElement findJavaElement(IFile file, int start){
		try{
			ICompilationUnit compilationUnit = EclipseUtil.getCompilationUnit(file);
			
			return compilationUnit.getElementAt(start);
		}catch(CoreException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
		return null;
		
	}
	
	private List<IType> findLocalAnnotattedInterfaces(IMethod method) throws JavaModelException{
		ArrayList<IType> types = new ArrayList<IType>();
		
		if(method.getTypeParameters().length > 0)
			return types;
		
		IType type = method.getDeclaringType();
		String[] is = type.getSuperInterfaceNames();
		for(int i = 0; i < is.length; i++){
			String f = EclipseJavaUtil.resolveType(type, is[i]);
			IType t = EclipseResourceUtil.getValidType(type.getJavaProject().getProject(), f);
			if(t != null && t.isInterface()){
				IAnnotation localAnnotation = EclipseJavaUtil.findAnnotation(t, t, CDIConstants.LOCAL_ANNOTATION_TYPE_NAME);
				if(localAnnotation != null){
					if(isMethodExists(t, method)){
						types.clear();
						return types;
					}
					types.add(t);
				}
			}
		}
		return types;
	}
	
	private boolean isMethodExists(IType interfaceType, IMethod method){
		IMethod existingMethod = interfaceType.getMethod(method.getElementName(), method.getParameterTypes());
		if(existingMethod != null && existingMethod.exists())
			return true;
		return false;
	}
	
	private IField findNonStaticField(IFile file, int start){
		try{
			IJavaElement javaElement = findJavaElement(file, start);
			
			if(javaElement != null && javaElement instanceof IField){
				IField field = (IField)javaElement;
				if(!Flags.isStatic(field.getFlags()) && !field.isBinary())
					return field;
			}
		}catch(JavaModelException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
		return null;
	}

	private IField findPublicField(IFile file, int start){
		try{
			IJavaElement javaElement = findJavaElement(file, start);
			
			if(javaElement != null && javaElement instanceof IField){
				IField field = (IField)javaElement;
				if(Flags.isPublic(field.getFlags()) && !field.isBinary())
					return field;
			}
		}catch(JavaModelException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
		return null;
	}
	
	public boolean hasResolutions(IMarker marker) {
		try {
			return getMessageID(marker) >= 0;
		} catch (CoreException ex) {
			CDIUIPlugin.getDefault().logError(ex);
		}
		return false;
	}
	
	private IAnnotation getAnnotation(IJavaElement element, String annotationQualifiedName){
		if(element instanceof IAnnotatable){
			String shortName = MarkerResolutionUtils.getShortName(annotationQualifiedName);
			IAnnotation[] annotations;
			try {
				annotations = ((IAnnotatable)element).getAnnotations();
				for(IAnnotation annotation : annotations){
					if(annotation.getElementName().equals(annotationQualifiedName) ||
							annotation.getElementName().equals(shortName))
						return annotation;
						
				}
			} catch (JavaModelException e) {
				CDIUIPlugin.getDefault().logError(e);
			}
		}
		return null;
	}
	
	private IJavaElement findJavaElementByAnnotation(IJavaElement element, String qualifiedName) throws JavaModelException{
		IAnnotation annotation = getAnnotation(element, qualifiedName);
		if(annotation != null)
			return element;
		
		if(element instanceof IMethod){
			for(ILocalVariable parameter : ((IMethod)element).getParameters()){
				annotation = getAnnotation(parameter, qualifiedName);
				if(annotation != null)
					return parameter;
			}
		}else if(element instanceof ILocalVariable){
			IJavaElement parent = element.getParent();
			if(parent != null){
				annotation = getAnnotation(parent, qualifiedName);
				if(annotation != null)
					return parent;
			}
		}else if(element instanceof IType){
			for(IField field : ((IType)element).getFields()){
				annotation = getAnnotation(field, qualifiedName);
				if(annotation != null)
					return field;
			}
			
			for(IMethod method : ((IType)element).getMethods()){
				annotation = getAnnotation(method, qualifiedName);
				if(annotation != null)
					return method;
				for(ILocalVariable parameter : method.getParameters()){
					annotation = getAnnotation(parameter, qualifiedName);
					if(annotation != null)
						return parameter;
				}
			}
		}
		
		if(element instanceof IMember){
			annotation = getAnnotation(((IMember)element).getDeclaringType(), qualifiedName);
			if(annotation != null)
				return ((IMember)element).getDeclaringType();
		}
		
		return null;
	}
	
}
