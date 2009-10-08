/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.search;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.internal.ui.search.JavaSearchResultPage;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.IMatchPresentation;
import org.eclipse.jdt.ui.search.IQueryParticipant;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.PartInitException;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELMethodInvocation;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.common.el.core.refactoring.RefactorSearcher;
import org.jboss.tools.seam.core.SeamProjectsSet;

public class SeamELReferencesQueryParticipant implements IQueryParticipant, IMatchPresentation{
	private ELSearcher searcher;
	JavaSearchResultPage searchPage = null;
	
	public int estimateTicks(QuerySpecification specification) {
		return 10;
	}

	public IMatchPresentation getUIParticipant() {
		return this;
	}

	public void search(ISearchRequestor requestor,
			QuerySpecification querySpecification, IProgressMonitor monitor)
			throws CoreException {
		
		if(querySpecification instanceof ElementQuerySpecification){
			ElementQuerySpecification qs = (ElementQuerySpecification)querySpecification;
			if(qs.getElement() instanceof IMethod){
				IFile file = (IFile)qs.getElement().getResource();
				String name = ELSearcher.getPropertyName(qs.getElement().getElementName());
				
				searcher = new ELSearcher(requestor, qs.getElement(), file, name);
				searcher.setSearchScope(qs.getScope());
				
				searcher.findELReferences();
			}
		}
	}
	
	public ILabelProvider createLabelProvider() {
		return null;
	}

	public void showMatch(Match match, int currentOffset,
			int currentLength, boolean activate) throws PartInitException {
	}
	
	class ELSearcher extends RefactorSearcher{
		ISearchRequestor requestor;
		SeamProjectsSet projectsSet;
		
		public ELSearcher(ISearchRequestor requestor, IJavaElement element, IFile file, String name){
			super(file, name, element);
			this.requestor = requestor;
			projectsSet = new SeamProjectsSet(file.getProject());
		}

		@Override
		protected boolean isFileCorrect(IFile file){
			if(!file.isSynchronized(IResource.DEPTH_ZERO)){
				return false;
			}else if(file.isPhantom()){
				return false;
			}else if(file.isReadOnly()){
				return false;
			}
			return true;
		}
		
		protected IProject[] getProjects(){
			return projectsSet.getAllProjects();
		}
		
		protected IContainer getViewFolder(IProject project){
			if(project.equals(projectsSet.getWarProject()))
				return projectsSet.getDefaultViewsFolder();
			else if(project.equals(projectsSet.getEarProject()))
				return projectsSet.getDefaultEarViewsFolder();
			
			return null;
		}

		@Override
		protected void match(IFile file, int offset, int length) {
			Match match = new Match(file, offset, length);
			requestor.reportMatch(match);
		}
		
		protected ELInvocationExpression findComponentReference(ELInvocationExpression invocationExpression){
			ELInvocationExpression invExp = invocationExpression;
			while(invExp != null){
				if(invExp instanceof ELMethodInvocation || invExp instanceof ELPropertyInvocation){
					if(invExp.getMemberName() != null && invExp.getMemberName().equals(propertyName))
						return invExp;
					else
						invExp = invExp.getLeft();
				}else{
					invExp = invExp.getLeft();
				}
			}
			return null;
		}
	}
}
