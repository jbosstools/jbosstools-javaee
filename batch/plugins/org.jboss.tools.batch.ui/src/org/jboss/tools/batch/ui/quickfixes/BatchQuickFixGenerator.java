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
package org.jboss.tools.batch.ui.quickfixes;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.DocumentProviderRegistry;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.internal.core.validation.BatchValidator;
import org.jboss.tools.batch.ui.BatchUIPlugin;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.quickfix.AbstractQuickFixGenerator;
import org.jboss.tools.common.quickfix.IQuickFix;
import org.jboss.tools.common.refactoring.MarkerResolutionUtils;

public class BatchQuickFixGenerator extends AbstractQuickFixGenerator{


	@Override
	public boolean hasProposals(Annotation annotation, Position position) {
		if(annotation instanceof TemporaryAnnotation){
			return true;
		}
		return false;
	}

	@Override
	public IJavaCompletionProposal[] getProposals(Annotation annotation, Position position) {
		if(annotation instanceof TemporaryAnnotation){
			TemporaryAnnotation tempAnnotation = (TemporaryAnnotation)annotation;
			
			IFile file = MarkerResolutionUtils.getFile();
			int messageId = getMessageID(tempAnnotation);
			int start = tempAnnotation.getPosition().getOffset();
			int end = start + tempAnnotation.getPosition().getLength();
			
			try {
				return findXMLResolutions(file, messageId, start, end, true);
			} catch (JavaModelException e) {
				BatchUIPlugin.getDefault().logError(e);
			}
		}
		return new IJavaCompletionProposal[]{};
	}

	
	protected IMarkerResolution[] findResolutions(IMarker marker)
			throws CoreException {

		int messageId = getMessageID(marker);
		if (messageId == -1)
			return new IMarkerResolution[] {};

		if(marker.getResource() instanceof IFile){
			final IFile file = (IFile) marker.getResource();
	
			Integer attribute = ((Integer) marker.getAttribute(IMarker.CHAR_START));
			if (attribute == null)
				return new IMarkerResolution[] {};
			final int start = attribute.intValue();
			
			attribute = ((Integer) marker.getAttribute(IMarker.CHAR_END));
			if (attribute == null)
				return new IMarkerResolution[] {};
			final int end = attribute.intValue();
			
			if (XML_EXTENSION.equals(file.getFileExtension())){
				return findXMLResolutions(file, messageId, start, end, false);
			}
		}
		return new IMarkerResolution[] {};
	}
	
	
	
	private IQuickFix[] findXMLResolutions(IFile file, int messageId, int start, int end, boolean asYouType) throws JavaModelException{
		IJavaProject javaProject = EclipseUtil.getJavaProject(file.getProject());
		
		FileEditorInput input = new FileEditorInput(file);
		IDocumentProvider provider = DocumentProviderRegistry.getDefault().getDocumentProvider(input);
		String text="";
		try {
			provider.connect(input);
		
			IDocument document = provider.getDocument(input);
		
			text = document.get(start, end-start);
		} catch (BadLocationException e) {
			BatchUIPlugin.getDefault().logError(e);
		} catch (CoreException e) {
			BatchUIPlugin.getDefault().logError(e);
		} finally {
			provider.disconnect(input);
		}
		
		if(text.startsWith("\"") || text.startsWith("'")){
			text = text.substring(1);
		}
		if(text.endsWith("\"") || text.endsWith("'")){
			text = text.substring(0,text.length()-1);
		}
		
		if(messageId == BatchValidator.BATCHLET_IS_NOT_FOUND_ID){
			return getQuickFixes(file, javaProject, text, BatchArtifactType.BATCHLET, NLS.bind(BatchQuickFixMessages.CREATE_BATCHLET, text));
		}else if(messageId == BatchValidator.JOB_LISTENER_IS_NOT_FOUND_ID){
			return getQuickFixes(file, javaProject, text, BatchArtifactType.JOB_LISTENER, NLS.bind(BatchQuickFixMessages.CREATE_JOB_LISTENER, text));
		}else if(messageId == BatchValidator.STEP_LISTENER_IS_NOT_FOUND_ID){
			return getQuickFixes(file, javaProject, text, BatchArtifactType.STEP_LISTENER, NLS.bind(BatchQuickFixMessages.CREATE_STEP_LISTENER, text));
		}else if(messageId == BatchValidator.DECIDER_IS_NOT_FOUND_ID){
			return getQuickFixes(file, javaProject, text, BatchArtifactType.DECIDER, NLS.bind(BatchQuickFixMessages.CREATE_DECIDER, text));
		}else if(messageId == BatchValidator.CHECKPOINT_ALGORITHM_IS_NOT_FOUND_ID){
			return getQuickFixes(file, javaProject, text, BatchArtifactType.CHECKPOINT_ALGORITHM, NLS.bind(BatchQuickFixMessages.CREATE_CHECKPOINT_ALGORYTHM, text));
		}else if(messageId == BatchValidator.ITEM_READER_IS_NOT_FOUND_ID){
			return getQuickFixes(file, javaProject, text, BatchArtifactType.ITEM_READER, NLS.bind(BatchQuickFixMessages.CREATE_ITEM_READER, text));
		}else if(messageId == BatchValidator.ITEM_WRITER_IS_NOT_FOUND_ID){
			return getQuickFixes(file, javaProject, text, BatchArtifactType.ITEM_WRITER, NLS.bind(BatchQuickFixMessages.CREATE_ITEM_WRITER, text));
		}else if(messageId == BatchValidator.ITEM_PROCESSOR_IS_NOT_FOUND_ID){
			return getQuickFixes(file, javaProject, text, BatchArtifactType.ITEM_PROCESSOR, NLS.bind(BatchQuickFixMessages.CREATE_ITEM_PROCESSOR, text));
		}else if(messageId == BatchValidator.MAPPER_IS_NOT_FOUND_ID){
			return getQuickFixes(file, javaProject, text, BatchArtifactType.PARTITION_MAPPER, NLS.bind(BatchQuickFixMessages.CREATE_PARTITION_MAPPER, text));
		}else if(messageId == BatchValidator.ANALYZER_IS_NOT_FOUND_ID){
			return getQuickFixes(file, javaProject, text, BatchArtifactType.PARTITION_ANALYZER, NLS.bind(BatchQuickFixMessages.CREATE_PARTITION_ANALYZER, text));
		}else if(messageId == BatchValidator.COLLECTOR_IS_NOT_FOUND_ID){
			return getQuickFixes(file, javaProject, text, BatchArtifactType.PARTITION_COLLECTOR, NLS.bind(BatchQuickFixMessages.CREATE_PARTITION_COLLECTOR, text));
		}else if(messageId == BatchValidator.REDUCER_IS_NOT_FOUND_ID){
			return getQuickFixes(file, javaProject, text, BatchArtifactType.PARTITION_REDUCER, NLS.bind(BatchQuickFixMessages.CREATE_PARTITION_REDUCER, text));
		}
		return new IQuickFix[]{};
	}
	
	IQuickFix[] getQuickFixes(IFile file, IJavaProject javaProject, String text, BatchArtifactType type, String message) {
	    IJavaElement element = findJavaElementByQualifiedName(javaProject, text);
	    if(element == null){
	        return new IQuickFix[] {
	            new CreateBatchArtifactQuickFix(file, text, type, message)
	        };
	    }
	    return new IQuickFix[0];
	}
}
