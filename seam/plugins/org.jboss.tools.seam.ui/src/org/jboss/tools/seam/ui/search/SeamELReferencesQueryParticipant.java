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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IMethod;
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
import org.jboss.tools.seam.internal.core.refactoring.SeamRefactorSearcher;

public class SeamELReferencesQueryParticipant implements IQueryParticipant, IMatchPresentation{
	SeamSearchViewLabelProvider labelProvider = new SeamSearchViewLabelProvider(null, 0);
	private ELSearcher searcher;
	
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
				
				searcher = new ELSearcher(requestor, file, name);
				
				searcher.findELReferences();
			}
		}
	}
	
	public ILabelProvider createLabelProvider() {
		return labelProvider;
	}

	public void showMatch(Match match, int currentOffset,
			int currentLength, boolean activate) throws PartInitException {
	}
	
	class ELSearcher extends SeamRefactorSearcher{
		ISearchRequestor requestor;
		public ELSearcher(ISearchRequestor requestor, IFile file, String name){
			super(file, name);
			this.requestor = requestor;
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

		@Override
		protected void match(IFile file, int offset, int length) {
			Match match = new Match(file, offset, length);
			requestor.reportMatch(match);
		}
		
		protected ELInvocationExpression findComponentReference(ELInvocationExpression invocationExpression){
			ELInvocationExpression invExp = invocationExpression;
			while(invExp != null){
				if(invExp instanceof ELMethodInvocation || invExp instanceof ELPropertyInvocation){
					if(invExp.getMemberName() != null && invExp.getMemberName().equalsIgnoreCase(propertyName))
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
