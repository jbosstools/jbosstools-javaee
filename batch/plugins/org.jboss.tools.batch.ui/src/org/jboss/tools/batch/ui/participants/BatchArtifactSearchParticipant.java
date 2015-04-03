/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.batch.ui.participants;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.IMatchPresentation;
import org.eclipse.jdt.ui.search.IQueryParticipant;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.search.ui.text.Match;
import org.jboss.tools.batch.core.BatchConstants;
import org.jboss.tools.batch.core.IBatchArtifact;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.batch.core.IBatchProperty;
import org.jboss.tools.batch.internal.core.impl.BatchProject;
import org.jboss.tools.batch.internal.core.impl.BatchProjectFactory;
import org.jboss.tools.batch.internal.core.impl.BatchUtil;
import org.jboss.tools.common.text.ITextSourceReference;

public class BatchArtifactSearchParticipant implements IQueryParticipant {

	@Override
	public void search(ISearchRequestor requestor, QuerySpecification querySpecification,
			IProgressMonitor monitor) throws CoreException {
		if (querySpecification instanceof ElementQuerySpecification) {
			if (!isSearchForReferences(querySpecification.getLimitTo())) {
				return;
			}

			ElementQuerySpecification qs = (ElementQuerySpecification) querySpecification;
			IJavaElement element = qs.getElement();
			IProject project = element.getJavaProject().getProject();

			BatchProject batchProject = (BatchProject) BatchProjectFactory.getBatchProjectWithProgress(project);

			if (batchProject == null) {
				return;
			}

			if (containsInSearchScope(querySpecification, project.getFullPath())) {
				searchInProject(requestor, querySpecification, batchProject, monitor, element);
			}

			BatchProject[] projects = batchProject.getAllDependentProjects(true);
			for (BatchProject p : projects) {
				if (containsInSearchScope(querySpecification, p.getProject().getFullPath())) {
					searchInProject(requestor, querySpecification, p, monitor, element);
				}
			}
		}
	}

	private static boolean containsInSearchScope(QuerySpecification querySpecification, IPath projectPath) {
		IJavaSearchScope searchScope = querySpecification.getScope();
		if (searchScope == null)
			return true;
		IPath[] paths = searchScope.enclosingProjectsAndJars();
		for (IPath path : paths) {
			if (path.equals(projectPath))
				return true;
		}
		return false;
	}

	private void searchInProject(ISearchRequestor requestor, QuerySpecification querySpecification,
			IBatchProject batchProject, IProgressMonitor monitor, IJavaElement element) {
		int worked = 0;
		monitor.beginTask(BatchParticipantMessages.Searching_For_Batch_Artifacts_References, batchProject
				.getDeclaredBatchJobs().size());
		Collection<ITextSourceReference> references = new HashSet<ITextSourceReference>();
		if (element instanceof IType) {
			IType type = (IType) element;
			IBatchArtifact artifact = batchProject.getArtifact(type);
			if (artifact != null) {
				for (IFile file : batchProject.getDeclaredBatchJobs()) {
					if (monitor.isCanceled()) {
						return;
					}
					references.addAll(BatchUtil.getAttributeReferences(file, BatchConstants.ATTR_REF,
							artifact.getName()));
					monitor.worked(++worked);
				}
			} else {
				for (IFile file : batchProject.getDeclaredBatchJobs()) {
					if (monitor.isCanceled()) {
						return;
					}
					references.addAll(BatchUtil.getAttributeReferences(file, BatchConstants.ATTR_CLASS,
							type.getFullyQualifiedName()));
					monitor.worked(++worked);
				}
			}

		} else if (element instanceof IField) {
			IField field = (IField) element;
			IType type = field.getDeclaringType();
			IBatchArtifact artifact = batchProject.getArtifact(type);
			if (artifact != null) {
				for (IBatchProperty property : artifact.getProperties()) {
					if (property.getField().equals(field)) {
						for (IFile file : batchProject.getDeclaredBatchJobs()) {
							if (monitor.isCanceled()) {
								return;
							}
							references.addAll(BatchUtil.getPropertyAttributeReferences(file, artifact.getName(),
									property.getPropertyName()));
							monitor.worked(++worked);
						}
					}
				}
			}
		}
		for (ITextSourceReference reference : references) {
			Match match = new Match((IFile) reference.getResource(), reference.getStartPosition(),
					reference.getLength());
			requestor.reportMatch(match);
		}
		monitor.done();
	}

	@Override
	public int estimateTicks(QuerySpecification specification) {
		return 500;
	}

	@Override
	public IMatchPresentation getUIParticipant() {
		return null;
	}

	public boolean isSearchForReferences(int limitTo) {
		int maskedLimitTo = limitTo
				& ~(IJavaSearchConstants.IGNORE_DECLARING_TYPE + IJavaSearchConstants.IGNORE_RETURN_TYPE);
		if (maskedLimitTo == IJavaSearchConstants.REFERENCES
				|| maskedLimitTo == IJavaSearchConstants.ALL_OCCURRENCES) {
			return true;
		}

		return false;
	}
}
