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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IType;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.Match;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELMethodInvocation;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.seam.internal.core.refactoring.SeamRefactorSeacher;

public class ELSearchQuery implements ISearchQuery {
	private String propertyName;
	private ELSearcher searcher;
	
	private ELSearchResult result;
	
	public ELSearchQuery(IFile file, IType type, String propertyName){
		this.propertyName = propertyName;
		searcher = new ELSearcher(file, propertyName);
		
		result = new ELSearchResult(this);
	}
	
	public String getPropertyName(){
		return propertyName;
	}

	public boolean canRerun() {
		return false;
	}

	public boolean canRunInBackground() {
		return true;
	}

	public String getLabel() {
		return "Seach for property references in Expression Language";
	}

	public ISearchResult getSearchResult() {
		return result;
	}

	public IStatus run(IProgressMonitor monitor)
			throws OperationCanceledException {
		searcher.findELReferences();
		
		return Status.OK_STATUS;
	}
	
	class ELSearcher extends SeamRefactorSeacher{
		public ELSearcher(IFile file, String name){
			super(file, name);
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
			result.addMatch(match);
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
