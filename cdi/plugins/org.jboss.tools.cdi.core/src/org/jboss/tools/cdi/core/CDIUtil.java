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
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
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
				EclipseResourceUtil.addNatureToProject(project,
						IKbProject.NATURE_ID);
			}
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
			EclipseUtil.removeNatureFromProject(project,
					CDICoreNature.NATURE_ID);
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
	public static IInjectionPoint findInjectionPoint(Set<IBean> beans, IJavaElement element){
		if(!(element instanceof IField) && (element instanceof IMethod) )
			return null;
		
		for(IBean bean : beans){
			Set<IInjectionPoint> injectionPoints = bean.getInjectionPoints();
			for(IInjectionPoint iPoint : injectionPoints){
				if(element instanceof IField && iPoint instanceof IInjectionPointField){
					if(((IInjectionPointField)iPoint).getField() != null && ((IInjectionPointField)iPoint).getField().equals(element))
						return iPoint;
				}else if(element instanceof IMethod && iPoint instanceof IInjectionPointMethod){
					if(((IInjectionPointMethod)iPoint).getMethod() != null && ((IInjectionPointMethod)iPoint).getMethod().equals(element))
						return iPoint;
					
				}
			}
		}
	return null;
	}

	/**
	 * Sorts CDI beans. Sets for alternative beans higher position and for nonalternative beans lower position.
	 * 
	 * @param beans
	 * @param element
	 */
	public static List<IBean> sortBeans(Set<IBean> beans){
		Set<IBean> alternativeBeans = new HashSet<IBean>();
		Set<IBean> nonAlternativeBeans = new HashSet<IBean>();
		
		for(IBean bean : beans){
			if(bean.isAlternative())
				alternativeBeans.add(bean);
			else
				nonAlternativeBeans.add(bean);
		}
		
		ArrayList<IBean> sortedBeans = new ArrayList<IBean>();
		sortedBeans.addAll(alternativeBeans);
		sortedBeans.addAll(nonAlternativeBeans);
		return sortedBeans;
	}
}