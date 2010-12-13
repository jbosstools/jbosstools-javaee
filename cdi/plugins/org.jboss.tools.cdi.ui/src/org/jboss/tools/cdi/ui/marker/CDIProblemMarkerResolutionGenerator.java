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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
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

		IFile file = (IFile) marker.getResource();

		attribute = ((Integer) marker.getAttribute(IMarker.CHAR_START));
		if (attribute == null)
			return new IMarkerResolution[] {};
		int start = attribute.intValue();

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
					if(types.size() == 0 && !isMethodPublic(method)){
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
			}
		}
		return new IMarkerResolution[] {};
	}
	
	private IMethod findMethod(IFile file, int start){
		try{
			ICompilationUnit original = EclipseUtil.getCompilationUnit(file);
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			IJavaElement javaElement = compilationUnit.getElementAt(start);
			if(javaElement != null && javaElement instanceof IMethod){
				IMethod method = (IMethod)javaElement;
				if(!method.isBinary())
					return method;
			}
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
			ICompilationUnit original = EclipseUtil.getCompilationUnit(file);
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			IJavaElement javaElement = compilationUnit.getElementAt(start);
			
			if(javaElement != null && javaElement instanceof IField){
				IField field = (IField)javaElement;
				if(!Flags.isStatic(field.getFlags()) && !field.isBinary())
					return field;
			}
		}catch(CoreException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
		return null;
	}
	
	private boolean isMethodPublic(IMethod method) throws JavaModelException{
		if(Flags.isPublic(method.getFlags()))
			return true;
		return false;
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
