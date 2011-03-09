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
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
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

	private IMarkerResolution[] findResolutions(IMarker marker)
			throws CoreException {
		Integer attribute = ((Integer) marker
				.getAttribute(CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME));
		if (attribute == null)
			return new IMarkerResolution[] {};

		int messageId = attribute.intValue();

		final IFile file = (IFile) marker.getResource();

		attribute = ((Integer) marker.getAttribute(IMarker.CHAR_START));
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
		
		Set<IBean> allBeans = cdiProject.getBeans(file.getFullPath());
		
		IInjectionPoint ip = CDIUtil.findInjectionPoint(allBeans, element, start);
		
		return ip;
	}
	
	private List<IBean> findBeans(IInjectionPoint injectionPoint){
		ICDIProject cdiProject = injectionPoint.getCDIProject();
		Set<IBean> beanSet = cdiProject.getBeans(false, injectionPoint);
		
		List<IBean> beanList = CDIUtil.sortBeans(beanSet);
		
		return beanList;
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
	
	public boolean hasResolutions(IMarker marker) {
		try {
			if (findResolutions(marker).length != 0)
				return true;
		} catch (CoreException ex) {
			CDIUIPlugin.getDefault().logError(ex);
		}
		return false;
	}
	
}
