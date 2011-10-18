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
package org.jboss.tools.cdi.ui.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.IMatchPresentation;
import org.eclipse.jdt.ui.search.IQueryParticipant;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIElement;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.core.IParameter;

public class InjectionPointQueryParticipant implements IQueryParticipant{
	ArrayList<String> objects = new ArrayList<String>();
	
	public int estimateTicks(QuerySpecification specification) {
		return 10;
	}

	public IMatchPresentation getUIParticipant() {
		return new InjectionPointMatchPresentation();
	}
	
	private static boolean containsInSearchScope(QuerySpecification querySpecification, IPath projectPath){
		IJavaSearchScope searchScope = querySpecification.getScope();
		if(searchScope == null)
			return true;
		IPath[] paths = searchScope.enclosingProjectsAndJars();
		for(IPath path : paths){
			if(path.equals(projectPath))
				return true;
		}
		return false;
	}
	
	public static boolean containsInSearchScope(QuerySpecification querySpecification, ICDIElement element){
		return containsInSearchScope(querySpecification, element.getResource().getProject().getFullPath());
	}

	public void search(ISearchRequestor requestor,
			QuerySpecification querySpecification, IProgressMonitor monitor)
			throws CoreException {
		objects.clear();
		
		if(querySpecification instanceof ElementQuerySpecification){
			if (!isSearchForReferences(querySpecification.getLimitTo()))
				return;
			
			ElementQuerySpecification qs = (ElementQuerySpecification)querySpecification;
			IJavaElement element = qs.getElement();
			if(element instanceof IMethod || element instanceof IField || element instanceof ILocalVariable){
				IProject project = element.getJavaProject().getProject();
				if(project == null)
					return;
				
				CDICoreNature cdiNature = CDICorePlugin.getCDI(project, true);
				
				if(cdiNature == null)
					return;
				
				ICDIProject cdiProject = cdiNature.getDelegate();
				
				if(cdiProject == null)
					return;
				
				Set<IBean> beans = cdiProject.getBeans(element.getPath());
				
				IInjectionPoint injectionPoint = CDIUtil.findInjectionPoint(beans, element, 0);
				if(injectionPoint != null){
					Set<IBean> resultBeanSet = cdiProject.getBeans(false, injectionPoint);
					List<IBean> resultBeanList = CDIUtil.sortBeans(resultBeanSet);
					for(IBean bean : resultBeanList){
						if(bean != null && containsInSearchScope(querySpecification, bean)){
							CDIMatch match = new CDIMatch(bean);
							if(!objects.contains(match.getPath())){
								requestor.reportMatch(match);
								objects.add(match.getPath());
							}
						}
					}
					resolveObserverMethods(cdiProject, injectionPoint, requestor, querySpecification);
				}
				if(element instanceof IMethod){
					IParameter param = findObserverParameter(beans, (IMethod)element);
					if(param != null){
						findObservedEvents(cdiProject, param, requestor, querySpecification);
					}
				}
			}
		}
	}
	
	private void resolveObserverMethods(ICDIProject cdiProject, IInjectionPoint injectionPoint, ISearchRequestor requestor,
			QuerySpecification querySpecification){
		Set<IObserverMethod> observerMethods = cdiProject.resolveObserverMethods(injectionPoint);
		for(IObserverMethod observerMethod : observerMethods){
			if(containsInSearchScope(querySpecification, observerMethod)){
				// match observer method
				CDIMatch match = new CDIMatch(observerMethod);
				if(!objects.contains(match.getPath())){
					requestor.reportMatch(match);
					objects.add(match.getPath());
				}
			}
		}
	}
	
	private void findObservedEvents(ICDIProject cdiProject, IParameter param, ISearchRequestor requestor,
			QuerySpecification querySpecification){
		Set<IInjectionPoint> events = cdiProject.findObservedEvents(param);
		for(IInjectionPoint event : events){
			if(containsInSearchScope(querySpecification, event)){
				// match event
				CDIMatch match = new CDIMatch(event);
				if(!objects.contains(match.getPath())){
					requestor.reportMatch(match);
					objects.add(match.getPath());
				}
			}
		}
	}
	
	private IParameter findObserverParameter(Set<IBean> beans, IMethod method) throws JavaModelException {
		for (IBean bean: beans) {
			if(bean instanceof IClassBean) {
				Set<IObserverMethod> observers = ((IClassBean)bean).getObserverMethods();
				for (IObserverMethod bm: observers) {
					if(bm.getMethod().equals(method)){
						IObserverMethod obs = (IObserverMethod)bm;
						Set<IParameter> ps = obs.getObservedParameters();
						if(!ps.isEmpty()) {
							return ps.iterator().next();
						}
					}
				}
			}
		}
		
		return null;
	}

	
	public boolean isSearchForReferences(int limitTo) {
    	int maskedLimitTo = limitTo & ~(IJavaSearchConstants.IGNORE_DECLARING_TYPE+IJavaSearchConstants.IGNORE_RETURN_TYPE);
    	if (maskedLimitTo == IJavaSearchConstants.REFERENCES || maskedLimitTo == IJavaSearchConstants.ALL_OCCURRENCES) {
    		return true;
    	}
    
    	return false;
    }
	
}
