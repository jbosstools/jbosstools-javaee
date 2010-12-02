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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.jboss.tools.cdi.internal.core.validation.CDICoreValidator;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.EclipseUtil;

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
				.getAttribute(CDICoreValidator.MESSAGE_ID_ATTRIBUTE_NAME));
		if (attribute == null)
			return new IMarkerResolution[] {};

		int messageId = attribute.intValue();

		IFile file = (IFile) marker.getResource();

		attribute = ((Integer) marker.getAttribute(IMarker.CHAR_START));
		if (attribute == null)
			return new IMarkerResolution[] {};
		int start = attribute.intValue();

		attribute = ((Integer) marker.getAttribute(IMarker.CHAR_END));
		if (attribute == null)
			return new IMarkerResolution[] {};
		int end = attribute.intValue();

		if (JAVA_EXTENSION.equals(file.getFileExtension())) {
			if (messageId == CDICoreValidator.ILLEGAL_PRODUCER_FIELD_IN_SESSION_BEAN_ID) {
				IField field = findNonStaticField(file, start);
				if(field != null){
					return new IMarkerResolution[] {
						new MakeFieldStaticMarkerResolution(field, file)
					};
				}
			}
		}
		return new IMarkerResolution[] {};
	}
	
	private IField findNonStaticField(IFile file, int start){
		try{
		ICompilationUnit original = EclipseUtil.getCompilationUnit(file);
		ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
		
		IJavaElement javaElement = compilationUnit.getElementAt(start);
		IType type = compilationUnit.findPrimaryType();
		if(javaElement != null && type != null){
			if(javaElement instanceof IField){
				IField field = (IField)javaElement;
				if((field.getFlags() & Flags.AccStatic) == 0)
					return field;
			}
		}
		}catch(CoreException ex){
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
