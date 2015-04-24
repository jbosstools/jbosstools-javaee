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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.osgi.util.NLS;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.jboss.tools.batch.core.BatchConstants;
import org.jboss.tools.batch.core.IBatchArtifact;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.batch.core.IBatchProperty;
import org.jboss.tools.batch.internal.core.impl.BatchProject;
import org.jboss.tools.batch.internal.core.impl.BatchProjectFactory;
import org.jboss.tools.batch.internal.core.impl.BatchUtil;
import org.jboss.tools.batch.ui.BatchUIPlugin;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.refactoring.FileChangeFactory;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.common.util.BeanUtil;

public class BatchArtifactRenameParticipant extends RenameParticipant {
	private IType type;
	private IField field;
	private String newName;
	private RefactoringStatus status;
	private CompositeChange rootChange;
	private TextFileChange lastChange;
	private ArrayList<String> keys = new ArrayList<String>();

	@Override
	protected boolean initialize(Object element) {
		if (element instanceof IType) {
			type = (IType) element;

			status = new RefactoringStatus();
			rootChange = new CompositeChange(BatchParticipantMessages.Updating_Batch_Artifacts_References);

			return true;
		}else if(element instanceof IField){
			field = (IField) element;
			type = field.getDeclaringType();

			status = new RefactoringStatus();
			rootChange = new CompositeChange(BatchParticipantMessages.Updating_Batch_Artifacts_References);

			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context)
			throws OperationCanceledException {

		IProject project = type.getJavaProject().getProject();

		BatchProject batchProject = (BatchProject) BatchProjectFactory.getBatchProjectWithProgress(project);

		if (batchProject == null) {
			return status;
		}

		IBatchArtifact artifact = batchProject.getArtifact(type);
		IBatchProperty property = null;
		if (artifact != null) { // rename within Batch Artifact
			if(field != null){ // field rename
				for (IBatchProperty prop : artifact.getProperties()) {
					if (prop.getField().equals(field)) {
						IAnnotationDeclaration declaration = prop.getBatchPropertyDeclaration();
						if (declaration == null || declaration.getMemberValue(null) != null) { // Annotation @Named has a value
							return status;
						}
						newName = getArguments().getNewName();
						property = prop;
					}
				}
			}else{ // class rename
				IAnnotationDeclaration declaration = artifact.getNamedDeclaration();
				if (declaration == null || declaration.getMemberValue(null) != null) { // Annotation @Named has a value
					return status;
				}
				newName = BeanUtil.getDefaultBeanName(getArguments().getNewName());
			}
		} else { // rename exception
			String fullyQualifiedName = type.getFullyQualifiedName();
			StringBuilder b = new StringBuilder(fullyQualifiedName);
			b.replace(fullyQualifiedName.lastIndexOf(type.getElementName()), fullyQualifiedName.lastIndexOf(type.getElementName()) + type.getElementName().length(), getArguments().getNewName() );
			newName = b.toString();
		}
		searchInProject(batchProject, artifact, property, pm);

		BatchProject[] projects = batchProject.getAllDependentProjects(true);
		for (BatchProject p : projects) {
			searchInProject(p, artifact, property, pm);
		}
		return status;
	}

	private void searchInProject(IBatchProject batchProject, IBatchArtifact artifact, IBatchProperty property, IProgressMonitor monitor) {
		monitor.beginTask(BatchParticipantMessages.Searching_For_Batch_Artifacts_References, batchProject
				.getDeclaredBatchJobs().size());
		Collection<ITextSourceReference> references = new HashSet<ITextSourceReference>();

		if (!batchProject.getProject().isSynchronized(IResource.DEPTH_ZERO)) {
			Exception exception = new Exception(NLS.bind(
					BatchParticipantMessages.Cannot_Read_Out_Of_Sync_Resource, batchProject.getProject()
							.getFullPath().toString()));
			exception.setStackTrace(Thread.currentThread().getStackTrace());
			BatchUIPlugin.getDefault().logError(exception);
			return;
		}

		if (artifact != null) {
			if(property != null){
				references.addAll(searchForPropertyReferences(batchProject, artifact.getName(), property.getPropertyName(),
						monitor));
			}else{
				references.addAll(searchForReferences(batchProject, BatchConstants.ATTR_REF, artifact.getName(),
						monitor));
				
			}
		} else {
			references.addAll(searchForReferences(batchProject, BatchConstants.ATTR_CLASS,
					type.getFullyQualifiedName(), monitor));
		}

		for (ITextSourceReference reference : references) {
			if (((IFile) reference.getResource()).isReadOnly()) {
				Exception exception = new Exception(NLS.bind(
						BatchParticipantMessages.Cannot_Change_Read_Only_File, ((IFile) reference.getResource())
								.getFullPath().toString()));
				exception.setStackTrace(Thread.currentThread().getStackTrace());
				BatchUIPlugin.getDefault().logError(exception);
			} else {
				change((IFile) reference.getResource(), reference.getStartPosition(), reference.getLength(),
						newName);
			}
		}
		monitor.done();
	}

	private Collection<ITextSourceReference> searchForReferences(IBatchProject batchProject,
			String attributeName, String attributeValue, IProgressMonitor monitor) {
		int worked = 0;
		Collection<ITextSourceReference> references = new HashSet<ITextSourceReference>();
		for (IFile file : batchProject.getDeclaredBatchJobs()) {
			if (monitor.isCanceled()) {
				return references;
			}

			if (file.isPhantom()) {
				continue;
			}

			if (!file.isSynchronized(IResource.DEPTH_ZERO)) {
				Exception exception = new Exception(
						NLS.bind(BatchParticipantMessages.Cannot_Read_Out_Of_Sync_Resource, file.getFullPath()
								.toString()));
				exception.setStackTrace(Thread.currentThread().getStackTrace());
				BatchUIPlugin.getDefault().logError(exception);
				return references;
			}

			references.addAll(BatchUtil.getAttributeReferences(file, attributeName, attributeValue));
			monitor.worked(++worked);
		}
		return references;
	}

	private Collection<ITextSourceReference> searchForPropertyReferences(IBatchProject batchProject,
			String artifactName, String propertyName, IProgressMonitor monitor) {
		int worked = 0;
		Collection<ITextSourceReference> references = new HashSet<ITextSourceReference>();
		for (IFile file : batchProject.getDeclaredBatchJobs()) {
			if (monitor.isCanceled()) {
				return references;
			}

			if (file.isPhantom()) {
				continue;
			}

			if (!file.isSynchronized(IResource.DEPTH_ZERO)) {
				Exception exception = new Exception(
						NLS.bind(BatchParticipantMessages.Cannot_Read_Out_Of_Sync_Resource, file.getFullPath()
								.toString()));
				exception.setStackTrace(Thread.currentThread().getStackTrace());
				BatchUIPlugin.getDefault().logError(exception);
				return references;
			}

			references.addAll(BatchUtil.getPropertyAttributeReferences(file, artifactName, propertyName));
			monitor.worked(++worked);
		}
		return references;
	}

	protected TextFileChange getChange(IFile file) {
		if (lastChange != null && lastChange.getFile().equals(file))
			return lastChange;

		for (int i = 0; i < rootChange.getChildren().length; i++) {
			TextFileChange change = (TextFileChange) rootChange.getChildren()[i];
			if (change.getFile().equals(file)) {
				lastChange = change;
				return lastChange;
			}
		}
		lastChange = FileChangeFactory.getFileChange(file);
		MultiTextEdit root = new MultiTextEdit();
		lastChange.setEdit(root);
		rootChange.add(lastChange);

		return lastChange;
	}

	private void change(IFile file, int offset, int length, String text) {
		String key = file.getFullPath().toString() + " " + offset;
		if (!keys.contains(key)) {
			TextFileChange change = getChange(file);
			TextEdit edit = new ReplaceEdit(offset, length, text);
			change.addEdit(edit);
			keys.add(key);
		}
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		if (rootChange.getChildren().length > 0) {
			return rootChange;
		}
		return null;
	}
}
