/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.ui.search;

import java.io.IOException;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.ui.search.JavaSearchQuery;
import org.eclipse.jdt.internal.ui.search.JavaSearchResult;
import org.eclipse.jdt.internal.ui.search.JavaSearchScopeFactory;
import org.eclipse.jdt.internal.ui.text.FastJavaPartitionScanner;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.search.core.text.TextSearchScope;
import org.eclipse.search.internal.core.text.DocumentCharSequence;
import org.eclipse.search.internal.core.text.FileCharSequenceProvider;
import org.eclipse.search.internal.core.text.TextSearchVisitor;
import org.eclipse.search.internal.core.text.TextSearchVisitor.ReusableMatchAccess;
import org.eclipse.search.internal.ui.Messages;
import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.search.internal.ui.SearchPlugin;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInstance;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELModel;
import org.jboss.tools.common.el.core.parser.ELParser;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.el.core.resolver.ElVarSearcher;
import org.jboss.tools.common.el.core.resolver.Var;
import org.jboss.tools.common.java.IJavaSourceReference;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.IOpenableElement;
import org.jboss.tools.seam.core.IRole;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamContextShortVariable;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamDeclaration;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.AbstractSeamDeclaration;
import org.jboss.tools.seam.internal.core.SeamComponent;
import org.jboss.tools.seam.internal.core.el.SeamELCompletionEngine;
import org.jboss.tools.seam.internal.core.scanner.ScannerException;
import org.jboss.tools.seam.internal.core.scanner.java.AnnotatedASTNode;
import org.jboss.tools.seam.internal.core.scanner.java.ResolvedAnnotation;
import org.jboss.tools.seam.internal.core.scanner.java.SeamAnnotations;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.text.java.scanner.JavaAnnotationScanner;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Seam Search Visitor - performs searching on the Seam Projects and their files
 * 
 * @author Jeremy
 *
 */
public class SeamSearchVisitor {
	private final SeamVariableMatcher[] fVariableMatchers;
	private final JavaElementMatcher[] fJavaMatchers;
	private final VarMatcher[] fVarMatchers;
	private final SeamSearchRequestor fCollector;

	private final MultiStatus fStatus;
	private IProgressMonitor fProgressMonitor;
	private int fNumberOfScannedFiles;
	private int fNumberOfFilesToScan;
	private IResource fSearchRoot;
	private ISeamProject fCurrentSeamProject;
	
	
	private Map fDocumentsInEditors;
	private final TextSearchVisitor.ReusableMatchAccess fMatchAccess;
	
	private IFile fCurrentFile;
	private final FileCharSequenceProvider fFileCharSequenceProvider;

	interface ISeamMatcher {
		String getName();
		boolean match(Object obj);
		Object getElement();
	}
	
	class VarMatcher implements ISeamMatcher {
		Var fVar;
		IFile fFile;
		
		VarMatcher(Var var, IFile file) {
			this.fVar = var;
			this.fFile = file;
		}
		
		public IFile getFile() {
			return fFile;
		}
		
		public String getName() {
			String name = fVar == null ? null : fVar.getName();
			return name == null ? "<null>" : name;
		}
		
		public boolean match(Object compare) {
			if (fVar == null)
				return false;
			
			return fVar.equals(compare);
		}

		public Var getElement() {
			return fVar;
		}
	}

	class SeamVariableMatcher implements ISeamMatcher {
		ISeamContextVariable fVariable;
		IProject fProject;
		
		SeamVariableMatcher(ISeamContextVariable variable, IProject project) {
			this.fVariable = variable;
			this.fProject = project;
		}
		
		public String getName() {
			String name = fVariable == null ? null : fVariable.getName();
			return name == null ? "<null>" : name;
		}
		
		public boolean match(Object compare) {
			if (fVariable == null)
				return false;

			if (fVariable.equals(compare))
				return true;

			return fVariable.equals(compare);
		}

		public ISeamContextVariable getElement() {
			return fVariable;
		}
	}

	class JavaElementMatcher implements ISeamMatcher {
		IJavaElement fElement;
		IProject fProject;
		JavaElementMatcher(IJavaElement javaElement, IProject project) {
			this.fElement = javaElement;
			this.fProject = project;
		}
		
		public String getName () {
			return fElement.getElementName();
		}
		
		public IJavaElement getElement() {
			return fElement;
		}
		
		public boolean match(Object object) {
			if (!(object instanceof IJavaElement))
				return false;
			
			IJavaElement compare = (IJavaElement)object;
				
			if (fElement.equals(compare))
				return true;
			
			if (fElement.getElementType() != compare.getElementType())
				return false;

			switch(fElement.getElementType()) {
			case IJavaElement.FIELD:
				return matchField((IField)compare);
			case IJavaElement.METHOD:
				return matchMethod((IMethod)compare);
			case IJavaElement.TYPE:
				return matchType((IType)compare);
			default:
				System.out.println("match: UnsupportedType:\n" +
						fElement.getElementName() + " ==>> " + fElement.getElementType());
			}
			return false;
		}
		
		boolean matchType (IType compare) {
			return internalMatchType((IType)fElement, compare);
		}

		private boolean internalMatchType (IType type, IType compare) {
			return EclipseJavaUtil.isDerivedClass(
					type.getFullyQualifiedName(), 
					compare.getFullyQualifiedName(), 
					fProject);
		}
		
		boolean matchField (IField compare) {
			IField field = (IField)fElement;
			if (!field.getElementName().equals(compare.getElementName()))
				return false;
			
			if (!internalMatchType(field.getDeclaringType(), compare.getDeclaringType()))
					return false;
			
			try {
				return field.getTypeSignature().equals(compare.getTypeSignature());
			} catch (JavaModelException e) {
//				e.printStackTrace();
				SeamGuiPlugin.getPluginLog().logError(e);
				return false;
			}
		}			
		
		boolean matchMethod (IMethod compare) {
			IMethod method = (IMethod)fElement;
			if (!method.getElementName().equals(compare.getElementName()))
				return false;
			
			if (!internalMatchType(method.getDeclaringType(), compare.getDeclaringType()))
					return false;
			
			try {
				return method.getSignature().equals(compare.getSignature());
			} catch (JavaModelException e) {
//				e.printStackTrace();
				SeamGuiPlugin.getPluginLog().logError(e);
				return false;
			}
		}
	}
	
	/**
	 * Constructs SeamSearchVisitor for a given {@link Var} set 
	 * using a given {@link SeamSearchRequestor} 
	 * 
	 * @param collector
	 * @param vars
	 * @param file
	 */
	public SeamSearchVisitor(SeamSearchRequestor collector,
			Var[] vars, IFile file) {
		fCollector= collector;
		fStatus= new MultiStatus(NewSearchUI.PLUGIN_ID, IStatus.OK, SearchMessages.TextSearchEngine_statusMessage, null);
		fSearchRoot = file;
		fJavaMatchers = null;
		fVariableMatchers = null; 
		fVarMatchers = new VarMatcher[vars == null ? 0 : vars.length];
		for (int i = 0; vars != null && i < vars.length; i++) {
			fVarMatchers[i]= vars[i] == null ? null : new VarMatcher(vars[i], file);
		}
		fFileCharSequenceProvider= new FileCharSequenceProvider();
		fMatchAccess= new ReusableMatchAccess();
	}

	/**
	 * Constructs SeamSearchVisitor for a given {@link IJavaElement} set 
	 * using a given {@link SeamSearchRequestor} 
	 * 
	 * @param collector
	 * @param elements
	 * @param project
	 */
	public SeamSearchVisitor(SeamSearchRequestor collector,
			IJavaElement[] elements, IProject project) {
		fCollector= collector;
		fStatus= new MultiStatus(NewSearchUI.PLUGIN_ID, IStatus.OK, SearchMessages.TextSearchEngine_statusMessage, null);
		fSearchRoot = project;
		fVarMatchers = null;
		fVariableMatchers = null; 
		fJavaMatchers = new JavaElementMatcher[elements == null ? 0 : elements.length];
		for (int i = 0; elements != null && i < elements.length; i++) {
			fJavaMatchers[i]= elements[i] == null ? null : new JavaElementMatcher(elements[i], project);
		}
		fFileCharSequenceProvider= new FileCharSequenceProvider();
		fMatchAccess= new ReusableMatchAccess();
	}

	/**
	 * Constructs SeamSearchVisitor for a given {@link ISeamContextVariable} set 
	 * using a given {@link SeamSearchRequestor} 

	 * @param collector
	 * @param variables
	 * @param project
	 */
	public SeamSearchVisitor(SeamSearchRequestor collector,
			ISeamContextVariable[] variables, IProject project) {
		fCollector= collector;
		fStatus= new MultiStatus(NewSearchUI.PLUGIN_ID, IStatus.OK, SearchMessages.TextSearchEngine_statusMessage, null);
		fSearchRoot = project;
		fVarMatchers = null;
		fJavaMatchers = null;
		fVariableMatchers = new SeamVariableMatcher[variables == null ? 0 : variables.length];
		for (int i = 0; variables!= null && i < variables.length; i++) {
			fVariableMatchers[i]= variables[i] == null ? null : new SeamVariableMatcher(variables[i], project);
		}
		fFileCharSequenceProvider= new FileCharSequenceProvider();
		fMatchAccess= new ReusableMatchAccess();
	}


	private SeamSearchScope fCurrentScope = null;
	
	/**
	 * Performs search operation using a given scope
	 * 
	 * @param scope
	 * @param monitor
	 * @return
	 */
	public IStatus search(TextSearchScope scope, IProgressMonitor monitor) {
		try {
			if (scope instanceof SeamSearchScope) {
				fCurrentScope = (SeamSearchScope)scope;
				if (SeamSearchEngine.isSearchForDeclarations(((SeamSearchScope)scope).getLimitTo())) {
					return searchForDeclarations(((SeamSearchScope)scope).evaluateSeamProjectsInScope(fStatus), monitor);
				} else {
					return searchForReferences(((SeamSearchScope)scope).evaluateSeamProjectsInScope(fStatus), monitor);
				}
			}
		} finally {
			fCurrentScope = null;
		}
		return Status.OK_STATUS;
    }

	private int calculateFiles() {
		if ((fJavaMatchers != null && fJavaMatchers.length > 0) ||
				(fVariableMatchers != null && fVariableMatchers.length > 0)) { 
			IFile[] files = fCurrentScope.evaluateFilesInScope(fStatus);
			return (files == null ? 0 : files.length);
		} else if (fVarMatchers != null && fVarMatchers.length > 0) {
			List<IFile> fileList = new ArrayList<IFile>(fVarMatchers.length);
			for (int i = 0; i < fVarMatchers.length; i++) {
				if (fVarMatchers[i] != null && fVarMatchers[i].getFile() != null)
					fileList.add(fVarMatchers[i].getFile());
			}
			return fileList.size();
		}
		return 0;
	}
	
	public IStatus searchForDeclarations(ISeamProject[] projects, IProgressMonitor monitor) {
		fProgressMonitor= monitor == null ? new NullProgressMonitor() : monitor;
        fNumberOfScannedFiles= 0;
        fNumberOfFilesToScan= calculateFiles();
        fCurrentSeamProject= null;
        
        Job monitorUpdateJob= new MonitorUpdateJob();

        try {
            fProgressMonitor.beginTask(getTaskName(), fNumberOfFilesToScan);
            monitorUpdateJob.setSystem(true);
            monitorUpdateJob.schedule();
            try {
	            fCollector.beginReporting();
	            processSeamProjects(projects, true);
	            return fStatus;
            } catch (Throwable x) {
//            	x.printStackTrace();
				SeamGuiPlugin.getPluginLog().logError(x);
	            return fStatus;
            } finally {
                monitorUpdateJob.cancel();
            } 
        } finally {
            fProgressMonitor.done();
            fCollector.endReporting();
        }
	}

	private void processSeamProjects(ISeamProject[] projects, boolean searchDeclarations) {
        for (int i= 0; i < projects.length; i++) {
        	fCurrentSeamProject= projects[i];
            boolean res= searchDeclarations ? processSeamDeclarationsInProject(fCurrentSeamProject) :
            	processSeamReferencesInProject(fCurrentSeamProject);
            if (!res)
            	break;
		}
	}

	private ElVarSearcher fELVarSearcher;
	private boolean processSeamReferencesInProject(ISeamProject project) {
		IFile[] files = null;
		if (fCurrentScope != null) {
			if ((fJavaMatchers != null && fJavaMatchers.length > 0) ||
					(fVariableMatchers != null && fVariableMatchers.length > 0)) { 
				files = evaluateProjectFilesInScope(project.getProject(), fStatus);
			} else if (fVarMatchers != null && fVarMatchers.length > 0) {
				List<IFile> fileList = new ArrayList<IFile>(fVarMatchers.length);
				for (int i = 0; i < fVarMatchers.length; i++) {
					if (fVarMatchers[i] != null && fVarMatchers[i].getFile() != null)
						fileList.add(fVarMatchers[i].getFile());
				}
				if (!fileList.isEmpty()) {
					files = fileList.toArray(new IFile[0]);
				}
			}
			
		}

		SeamELCompletionEngine fCompletionEngine = new SeamELCompletionEngine();
		fELVarSearcher = new ElVarSearcher(fCompletionEngine);
		fDocumentsInEditors= evalNonFileBufferDocuments();
		boolean res= true;
		for (int i = 0; files != null && i < files.length; i++) {
            res= processSeamReferencesInFile(files[i]);
            if (!res)
            	break;
			
		}
		fDocumentsInEditors= null;
		fELVarSearcher = null;
		return res;
	}

	private boolean processSeamReferencesInFile(IFile file) {
		try {
		    if (!fCollector.acceptFile(file) || 
//		    		fMatchers == null ||
		    		(fJavaMatchers == null && fVarMatchers == null && fVariableMatchers == null)) {
		       return true;
		    }
		        
			IDocument document= getOpenDocument(file);
			
			if (document != null) {
				DocumentCharSequence documentCharSequence= new DocumentCharSequence(document);
				// assume all documents are non-binary
				locateMatches(file, documentCharSequence);
			} else {
				CharSequence seq= null;
				try {
					seq= fFileCharSequenceProvider.newCharSequence(file);
					if (hasBinaryContent(seq, file) && !fCollector.reportBinaryFile(file)) {
						return true;
					}
					locateMatches(file, seq);
				} catch (FileCharSequenceProvider.FileCharSequenceException e) {
					e.throwWrappedException();
				} finally {
					if (seq != null) {
						try {
							fFileCharSequenceProvider.releaseCharSequence(seq);
						} catch (IOException e) {
							SearchPlugin.log(e);
						}
					}
				}
			}
		} catch (UnsupportedCharsetException e) {
			String[] args= { getCharSetName(file), file.getFullPath().makeRelative().toString()};
			String message= Messages.format(SearchMessages.TextSearchVisitor_unsupportedcharset, args); 
			fStatus.add(new Status(IStatus.WARNING, NewSearchUI.PLUGIN_ID, IStatus.WARNING, message, e));
		} catch (IllegalCharsetNameException e) {
			String[] args= { getCharSetName(file), file.getFullPath().makeRelative().toString()};
			String message= Messages.format(SearchMessages.TextSearchVisitor_illegalcharset, args);
			fStatus.add(new Status(IStatus.WARNING, NewSearchUI.PLUGIN_ID, IStatus.WARNING, message, e));
		} catch (IOException e) {
			String[] args= { getExceptionMessage(e), file.getFullPath().makeRelative().toString()};
			String message= Messages.format(SearchMessages.TextSearchVisitor_error, args); 
			fStatus.add(new Status(IStatus.WARNING, NewSearchUI.PLUGIN_ID, IStatus.WARNING, message, e));
		} catch (CoreException e) {
			String[] args= { getExceptionMessage(e), file.getFullPath().makeRelative().toString()};
			String message= Messages.format(SearchMessages.TextSearchVisitor_error, args); 
			fStatus.add(new Status(IStatus.WARNING, NewSearchUI.PLUGIN_ID, IStatus.WARNING, message, e));
		} catch (StackOverflowError e) {
			String message= SearchMessages.TextSearchVisitor_patterntoocomplex0;
			fStatus.add(new Status(IStatus.ERROR, NewSearchUI.PLUGIN_ID, IStatus.ERROR, message, e));
			return false;
		} finally {
			fNumberOfScannedFiles++;
		}
		if (fProgressMonitor.isCanceled())
			throw new OperationCanceledException(SearchMessages.TextSearchVisitor_canceled);

		return true;
	}
	
	private void locateMatches(IFile file, CharSequence searchInput) throws CoreException {
		fELVarSearcher.setFile(file);
		if("java".equalsIgnoreCase(file.getFileExtension())) { //$NON-NLS-1$
			locateMatchesInJava(file, searchInput);
		} else {
			locateMatchesInDom(file, searchInput);
		}
	}

	private List<Var> fVarListForCurentValidatedNode = new ArrayList<Var>();
	private void locateMatchesInDom(IFile file, CharSequence content) {
		fVarListForCurentValidatedNode.clear();
		IModelManager manager = StructuredModelManager.getModelManager();
		if(manager == null) {
			// this can happen if plugin org.eclipse.wst.sse.core 
			// is stopping or uninstalled, that is Eclipse is shutting down.
			// there is no need to report it, just stop validation.
			return;
		}
		IStructuredModel model = null;		
		try {
			model = manager.getModelForRead(file);
			if (model instanceof IDOMModel) {
				IDOMModel domModel = (IDOMModel) model;
				IDOMDocument document = domModel.getDocument();
				locateMatchesInChildNodes(file, document, content);
			}
		} catch (CoreException e) {
			SeamCorePlugin.getDefault().logError(SeamCoreMessages.SEAM_EL_VALIDATOR_ERROR_VALIDATING_SEAM_EL, e);
        } catch (IOException e) {
        	SeamCorePlugin.getDefault().logError(SeamCoreMessages.SEAM_EL_VALIDATOR_ERROR_VALIDATING_SEAM_EL, e);
		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
		return;
	}

	private void locateMatchesInChildNodes(IFile file, Node parent, CharSequence content) 
			throws CoreException {
		Var var = fELVarSearcher.findVar(parent);
		if(var!=null) {
			fVarListForCurentValidatedNode.add(var);
		}
		NodeList children = parent.getChildNodes();
		for(int i=0; i<children.getLength(); i++) {
			Node curentValidatedNode = children.item(i);
			if(Node.ELEMENT_NODE == curentValidatedNode.getNodeType()) {
				locateMatchesInNodeContent(file, ((IDOMNode)curentValidatedNode).getFirstStructuredDocumentRegion(), DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE, content);
			} else if(Node.TEXT_NODE == curentValidatedNode.getNodeType()) {
				locateMatchesInNodeContent(file, ((IDOMNode)curentValidatedNode).getFirstStructuredDocumentRegion(), DOMRegionContext.XML_CONTENT, content);
			}
			locateMatchesInChildNodes(file, curentValidatedNode, content);
		}
		if(var!=null) {
			fVarListForCurentValidatedNode.remove(var);
		}
	}

	private void locateMatchesInNodeContent(IFile file, IStructuredDocumentRegion node, 
			String regionType, CharSequence content) throws CoreException {
		ITextRegionList regions = node.getRegions();
		for(int i=0; i<regions.size(); i++) {
			ITextRegion region = regions.get(i);
			if(region.getType() == regionType) {
				String text = node.getFullText(region);
				if(text.indexOf("{")>-1) { //$NON-NLS-1$
					int offset = node.getStartOffset() + region.getStart();
					locateMatchesInString(file, text, offset, content);
				}
			}
		}
	}
	
	private void locateMatchesInJava(IFile file, CharSequence content) throws CoreException {
		try {
			FastJavaPartitionScanner scaner = new FastJavaPartitionScanner();
			Document document = new Document(content.subSequence(0, content.length()).toString());
			scaner.setRange(document, 0, document.getLength());
			IToken token = scaner.nextToken();
			while(token!=null && token!=Token.EOF) {
				if(IJavaPartitions.JAVA_STRING.equals(token.getData())) {
					int length = scaner.getTokenLength();
					int offset = scaner.getTokenOffset();
					String value = document.get(offset, length);
					if(value.indexOf('{')>-1) {
						locateMatchesInString(file, value, offset, content);
					}
				}
				token = scaner.nextToken();
			}
			
			// Search in annotations
			ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
			if (compilationUnit == null)
				return;
			
			JavaAnnotationScanner annotationScanner = new JavaAnnotationScanner();
			Map<ResolvedAnnotation, AnnotatedASTNode<ASTNode>> loadedAnnotations = null;
			
			try {
				annotationScanner.parse((ICompilationUnit)compilationUnit);
				loadedAnnotations = annotationScanner.getResolvedAnnotations();
			} catch (ScannerException e) {
				SeamGuiPlugin.getPluginLog().logError(e);
				return;
			}
			for (ResolvedAnnotation annotation : loadedAnnotations.keySet()) {
				if (annotationScanner.isAnnotationOfType(annotation, SeamAnnotations.IN_ANNOTATION_TYPE) ||
						annotationScanner.isAnnotationOfType(annotation, SeamAnnotations.OUT_ANNOTATION_TYPE)) {
					String value = annotationScanner.getAnnotationValue(annotation);
					if (value == null || value.length() == 0)
						continue;
					IRegion valueRegion = annotationScanner.getAnnotationValueRegion(annotation);
					if (valueRegion == null)
						continue;
					
					int length = valueRegion.getLength();
					int offset = valueRegion.getOffset();
					if(value != null && value.indexOf("#{") >= 0) {
						locateMatchesInString(file, value, offset, content);
					} else {
						String string = "#{" + value + "}";
						locateMatchesInString(file, string, offset - 2, content);
					}
				}
			}
		} catch (BadLocationException e) {
			SeamGuiPlugin.getDefault().logError(e);
		}
	}

	/**
	 * @param offset - offset of string in file
	 * @param length - length of string in file
	 */
	private void locateMatchesInString(IFile file, String string, int offset, CharSequence content) throws CoreException {
		int startEl = string.indexOf("#{"); //$NON-NLS-1$
		if(startEl>-1) {
			ELParser parser = ELParserUtil.getJbossFactory().createParser();
			ELModel model = parser.parse(string);
			List<ELInstance> is = model.getInstances();
			for (ELInstance i: is) {
				if(i.getExpression() != null) {
					locateMatchesInEL(file, i.getExpression(), content, offset);
				}
			}
		}
	}

	private void locateMatchesInEL(IFile file, ELExpression el, CharSequence content, int offset) throws CoreException {
		List<ELInvocationExpression> invocations = el.getInvocations();
		for (ELInvocationExpression token : invocations) {
//				validateElOperand(file, token, el.getStart());
				String operand = token.getText();
				String varName = operand;
				int offsetOfToken = offset + token.getFirstToken().getStart();
				if (fJavaMatchers != null) {
					ELInvocationExpression expr = token;
					while(expr != null) {
						List<IJavaElement> elements = null;
						SeamELCompletionEngine fCompletionEngine = new SeamELCompletionEngine();
						try {
							elements = fCompletionEngine.getJavaElementsForELOperandTokens(fCurrentSeamProject, file, expr);
						} catch (StringIndexOutOfBoundsException e) {
							SeamGuiPlugin.getPluginLog().logError(e);
						} catch (BadLocationException e) {
							SeamGuiPlugin.getPluginLog().logError(e);
						}
	
						if(elements != null) for (int i = 0; i < elements.size(); i++) {
							if (!matches(elements.get(i))) 
								continue;
	
							int start = 0;
							int end = expr.getEndPosition() - expr.getStartPosition();
							String variationText = operand.substring(start, end);
	
							int offsetOfOperandToken = offsetOfToken + start;
							int lengthOfOperandToken = end - start;
							fMatchAccess.initialize(file, offsetOfOperandToken, lengthOfOperandToken, content);
							boolean res= fCollector.acceptPatternMatch(fMatchAccess);
							if (!res) {
								return; // no further reporting requested
							}
						}
						expr = expr.getLeft();
					}
				} else if (fVariableMatchers != null) {
					ELInvocationExpression expr = token;
					
					while(expr != null) {
						Set<ISeamContextVariable> variables = fCurrentSeamProject.getVariablesByName(expr.getText());
	
						if(variables != null) for (ISeamContextVariable variable : variables) {
							if (!matches(variable)) 
								continue;
	
							int start = 0;
							int end = expr.getEndPosition() - expr.getStartPosition();
							String variationText = operand.substring(start, end);
	
							int offsetOfOperandToken = offsetOfToken + start;
							int lengthOfOperandToken = end - start;
							fMatchAccess.initialize(file, offsetOfOperandToken, lengthOfOperandToken, content);
							boolean res= fCollector.acceptPatternMatch(fMatchAccess);
							if (!res) {
								return; // no further reporting requested
							}
						}
						expr = expr.getLeft();
					}
				} else if (fVarMatchers != null) {
					Var var = fELVarSearcher.findVarForEl(operand, fVarListForCurentValidatedNode, false);
					if (var != null){
						if (matches(var)) {
							ELInvocationExpression expr = token;
							while(expr.getLeft() != null) expr = expr.getLeft();
							String varRefText = expr.getText();
							int start = expr.getStartPosition();
							int end = expr.getEndPosition();
							int offsetOfVarRefToken = offsetOfToken + start;
							int lengthOfVarRefToken = end - start;
							
							fMatchAccess.initialize(file, offsetOfVarRefToken, lengthOfVarRefToken, content);
							boolean res= fCollector.acceptPatternMatch(fMatchAccess);
							if (!res) {
								return; // no further reporting requested
							}
						}
					}
				}
		}
	}

	
	
	public boolean processSeamDeclarationsInProject(ISeamProject project) {
		if (fJavaMatchers != null && fJavaMatchers.length > 0) {
			JavaSearchScopeFactory factory= JavaSearchScopeFactory.getInstance();
			IJavaSearchScope scope= factory.createWorkspaceScope(true);
			String description= factory.getWorkspaceScopeDescription(true);
			for (int i = 0; i < fJavaMatchers.length; i++) {
				ElementQuerySpecification elementQuerySpecification = 
					new ElementQuerySpecification(
						fJavaMatchers[i].getElement(), 
						IJavaSearchConstants.DECLARATIONS, 
						scope, description);		
				JavaSearchQuery query= new JavaSearchQuery(elementQuerySpecification);
				query.run(fProgressMonitor);
				JavaSearchResult result = (JavaSearchResult)query.getSearchResult();
				Object[] elements = result.getElements();
				for (int j = 0; elements != null && j < elements.length; j++) {
					Match[] matches = result.getMatches(elements[j]);
					for (int k = 0; matches != null && k < matches.length; k++) {
						fCollector.reportMatch(matches[k]);
					}
				}
			}
		}
		if (fVariableMatchers != null && fVariableMatchers.length > 0) {
			try {
				for (int i = 0; i < fVariableMatchers.length; i++) {
					if (fVariableMatchers[i] == null)
						continue;
	
					ISeamContextVariable variable = fVariableMatchers[i].getElement();
					
					if (variable instanceof ISeamContextShortVariable) {
						variable = ((ISeamContextShortVariable)variable).getOriginal();
					}
					
					boolean continueWithFactories = true;
					if (variable instanceof SeamComponent) {
						SeamComponent comp = (SeamComponent)variable;
						Set<ISeamComponentDeclaration> declarations = comp.getAllDeclarations();
						for (ISeamComponentDeclaration decl : declarations) {
							if (decl instanceof IJavaSourceReference) {
								IJavaSourceReference sourceRef = (IJavaSourceReference)decl;
								IResource resource = sourceRef.getSourceMember().getResource();
								IJavaElement sourceMember = sourceRef.getSourceMember();
								String name = sourceRef.getSourceMember().getElementName();
								int offset = sourceRef.getStartPosition();
								int length = sourceRef.getLength();
								//fMatchAccess.initialize((IFile)resource, offset, length, (CharSequence)name);
								boolean res= fCollector.acceptSeamDeclarationSourceReferenceMatch(sourceRef);
								if (!res) {
									return true; // no further reporting requested
								}
								continueWithFactories = false;
							} else if (decl instanceof IOpenableElement) {
								IResource resource = decl.getResource(); 
								String name = decl.getName();
								
								ITextSourceReference textSourceReference = decl.getLocationFor(AbstractSeamDeclaration.PATH_OF_NAME);
								if (textSourceReference != null) {
									int offset = textSourceReference.getStartPosition();
									int length = textSourceReference.getLength();
		
									boolean res= fCollector.acceptSeamDeclarationMatch(decl);
									if (!res) {
										return true; // no further reporting requested
									}
									continueWithFactories = false;
								} else {					
									int offset = decl.getStartPosition();
									int length = decl.getLength();
		
									fMatchAccess.initialize((IFile)resource, offset, length, (CharSequence)name);
									boolean res= fCollector.acceptPatternMatch(fMatchAccess);
									if (!res) {
										return true; // no further reporting requested
									}
									continueWithFactories = false;
								}
							}
						}
					} else if (variable instanceof IRole) {
						// add the declaration
						ISeamDeclaration decl = (ISeamDeclaration)variable;
						IResource resource = decl.getResource();
						String name = decl.getName();
						
						ITextSourceReference textSourceReference = decl.getLocationFor(AbstractSeamDeclaration.PATH_OF_NAME);
						if (textSourceReference != null) {
							int offset = textSourceReference.getStartPosition();
							int length = textSourceReference.getLength();
							fMatchAccess.initialize((IFile)resource, offset, length, (CharSequence)name);
							boolean res= fCollector.acceptPatternMatch(fMatchAccess);
							if (!res) {
								return true; // no further reporting requested
							}
							continueWithFactories = false;
						}					
					} else if (variable instanceof IBijectedAttribute) {
						IBijectedAttribute ba = (IBijectedAttribute)variable;
						BijectedAttributeType[] types = ba.getTypes();
						boolean hasDeclarationType = false;
						for (int j = 0; !hasDeclarationType && types != null && j < types.length; j++) {
							if (types[j] == BijectedAttributeType.OUT ||
									types[j] == BijectedAttributeType.DATA_BINDER ||
									types[j] == BijectedAttributeType.DATA_MODEL_SELECTION) {
								hasDeclarationType = true;
							}
						}
						if (hasDeclarationType) {
							// add the declaration
							ISeamDeclaration decl = (ISeamDeclaration)variable;
							IResource resource = decl.getResource();
							String name = decl.getName();
							
							ITextSourceReference textSourceReference = decl.getLocationFor(AbstractSeamDeclaration.PATH_OF_NAME);
							if (textSourceReference != null) {
								int offset = textSourceReference.getStartPosition();
								int length = textSourceReference.getLength();
	
								fMatchAccess.initialize((IFile)resource, offset, length, (CharSequence)name);
								boolean res= fCollector.acceptPatternMatch(fMatchAccess);
								if (!res) {
									return true; // no further reporting requested
								}
								continueWithFactories = false;
							}					
						}
					}
					
					// Search for Seam factories
					if (continueWithFactories && variable instanceof ISeamDeclaration) {
						ISeamDeclaration decl = (ISeamDeclaration)variable;
						IResource resource = decl.getResource();
						String name = decl.getName();
						
						ITextSourceReference textSourceReference = decl.getLocationFor(AbstractSeamDeclaration.PATH_OF_NAME);
						if (textSourceReference != null) {
							int offset = textSourceReference.getStartPosition();
							int length = textSourceReference.getLength();
	
							boolean res= fCollector.acceptSeamDeclarationMatch(decl);
							if (!res) {
								return true; // no further reporting requested
							}
						}					
					}
				}
			} catch (CoreException ce) {
				String[] args= { getExceptionMessage(ce), project.getResource().getFullPath().makeRelative().toString()};
				String message= Messages.format(SearchMessages.TextSearchVisitor_error, args); 
				fStatus.add(new Status(IStatus.WARNING, NewSearchUI.PLUGIN_ID, IStatus.WARNING, message, ce));
			}
		}
		

		if (fProgressMonitor.isCanceled())
			throw new OperationCanceledException(SearchMessages.TextSearchVisitor_canceled);
		
		return true;

	}

	private class MonitorUpdateJob extends Job {
    	private int fLastNumberOfScannedFiles= 0;
    	
    	MonitorUpdateJob() {
    		super(SearchMessages.TextSearchVisitor_progress_updating_job);
		}
    	
    	public IStatus run(IProgressMonitor inner) {
    		while (!inner.isCanceled()) {
				ISeamProject seamProject= fCurrentSeamProject;
				if (seamProject != null) {
					String seamProjectName= seamProject.getProject().getName();
					Object[] args= { seamProjectName, new Integer(fNumberOfScannedFiles), new Integer(fNumberOfFilesToScan)};
					fProgressMonitor.subTask(Messages.format(SeamUIMessages.SeamSearchVisitor_scanning, args));
					int steps= fNumberOfScannedFiles - fLastNumberOfScannedFiles;
					fProgressMonitor.worked(steps);
					fLastNumberOfScannedFiles += steps;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					return Status.OK_STATUS;
				}
			}
			return Status.OK_STATUS;
    	}
	}
	
	private String getTaskName() {
		ISeamMatcher[] currentMatchers = (fJavaMatchers == null ? fVarMatchers : fJavaMatchers);
		
		
		StringBuffer elements = new StringBuffer();
		for(int i = 0; currentMatchers != null && i < currentMatchers.length; i++) {
			if (currentMatchers[i] != null) {
				if (elements.length() > 0) {
					elements.append(", ");
				}
				elements.append(currentMatchers[i].getName());
			}
		}
    	
		String taskName= elements.length() == 0 ? SearchMessages.TextSearchVisitor_filesearch_task_label :  Messages.format(SearchMessages.TextSearchVisitor_textsearch_task_label, elements.toString());
    	return taskName;
	}
	
	private IStatus searchForReferences(ISeamProject[] projects, IProgressMonitor monitor) {
		fProgressMonitor= monitor == null ? new NullProgressMonitor() : monitor;
        fNumberOfScannedFiles= 0;
        fNumberOfFilesToScan= calculateFiles();
        fCurrentSeamProject= null;
        
        Job monitorUpdateJob= new MonitorUpdateJob();

        try {
            fProgressMonitor.beginTask(getTaskName(), fNumberOfFilesToScan);
            monitorUpdateJob.setSystem(true);
            monitorUpdateJob.schedule();
            try {
	            fCollector.beginReporting();
	            processSeamProjects(projects, false);
	            return fStatus;
            } finally {
                monitorUpdateJob.cancel();
            }
        } finally {
            fProgressMonitor.done();
            fCollector.endReporting();
        }
	}
	
	ISeamMatcher fCurrentMatcher;
	private boolean matches (IJavaElement element) {
		fCurrentMatcher = null;
		for (int i = 0; fJavaMatchers != null && i < fJavaMatchers.length; i++) {
			if (fJavaMatchers[i] == null)
				continue;
			if (fJavaMatchers[i].match(element)) {
				fCurrentMatcher = fJavaMatchers[i];
				return true;
			}
		}
		return false;
	}
	
	private boolean matches (ISeamContextVariable element) {
		fCurrentMatcher = null;
		for (int i = 0; fVariableMatchers != null && i < fVariableMatchers.length; i++) {
			if (fVariableMatchers[i] == null)
				continue;
			if (fVariableMatchers[i].match(element)) {
				fCurrentMatcher = fVariableMatchers[i];
				return true;
			}
		}
		return false;
	}

	private boolean matches (Var var) {
		fCurrentMatcher = null;
		for (int i = 0; fVarMatchers != null && i < fVarMatchers.length; i++) {
			if (fVarMatchers[i] == null)
				continue;
			if (fVarMatchers[i].match(var)) {
				fCurrentMatcher = fVarMatchers[i];
				return true;
			}
		}
		return false;
	}
 	
	private static String getExceptionMessage(Exception e) {
		String message= e.getLocalizedMessage();
		if (message == null) {
			return e.getClass().getName();
		}
		return message;
	}

	private static boolean hasBinaryContent(CharSequence seq, IFile file) throws CoreException {
		IContentDescription desc= file.getContentDescription();
		if (desc != null) {
			IContentType contentType= desc.getContentType();
			if (contentType != null && contentType.isKindOf(Platform.getContentTypeManager().getContentType(IContentTypeManager.CT_TEXT))) {
				return false;
			}
		}
		
		// avoid calling seq.length() at it runs through the complete file,
		// thus it would do so for all binary files.
		try {
			int limit= FileCharSequenceProvider.BUFFER_SIZE;
			for (int i= 0; i < limit; i++) {
				if (seq.charAt(i) == '\0') {
					return true;
				}
			}
		} catch (IndexOutOfBoundsException e) {
		}
		return false;
	}

	/**
	 * @return returns a map from IFile to IDocument for all open, dirty editors
	 */
	private static Map evalNonFileBufferDocuments() {
		Map result= new HashMap();
		IWorkbench workbench= SearchPlugin.getDefault().getWorkbench();
		IWorkbenchWindow[] windows= workbench.getWorkbenchWindows();
		for (int i= 0; i < windows.length; i++) {
			IWorkbenchPage[] pages= windows[i].getPages();
			for (int x= 0; x < pages.length; x++) {
				IEditorReference[] editorRefs= pages[x].getEditorReferences();
				for (int z= 0; z < editorRefs.length; z++) {
					IEditorPart ep= editorRefs[z].getEditor(false);
					if (ep instanceof ITextEditor && ep.isDirty()) { // only dirty editors
						evaluateTextEditor(result, ep);
					}
				}
			}
		}
		return result;
	}

	private static void evaluateTextEditor(Map result, IEditorPart ep) {
		IEditorInput input= ep.getEditorInput();
		if (input instanceof IFileEditorInput) {
			IFile file= ((IFileEditorInput) input).getFile();
			if (!result.containsKey(file)) { // take the first editor found
				ITextFileBufferManager bufferManager= FileBuffers.getTextFileBufferManager();
				ITextFileBuffer textFileBuffer= bufferManager.getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
				if (textFileBuffer != null) {
					// file buffer has precedence
					result.put(file, textFileBuffer.getDocument());
				} else {
					// use document provider
					IDocument document= ((ITextEditor) ep).getDocumentProvider().getDocument(input);
					if (document != null) {
						result.put(file, document);
					}
				}
			}
		}
	}

	private IDocument getOpenDocument(IFile file) {
		return getOpenDocument(file, fDocumentsInEditors);
	}

	
	/**
	 * Returns the IDocument of file currently opened in an editor 
	 * @param file
	 * @param documentsInEditors
	 * @return
	 */
	public static IDocument getOpenDocument(IFile file, Map documentsInEditors) {
		IDocument document= (IDocument) documentsInEditors.get(file);
		if (document == null) {
			ITextFileBufferManager bufferManager= FileBuffers.getTextFileBufferManager();
			ITextFileBuffer textFileBuffer= bufferManager.getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
			if (textFileBuffer != null) {
				document= textFileBuffer.getDocument();
			}
		}
		return document;
	}
	
	private static String getCharSetName(IFile file) {
		try {
			return file.getCharset();
		} catch (CoreException e) {
			return "unknown"; //$NON-NLS-1$
		}
	}

	private static boolean acceptPaternMatch(SeamSearchRequestor collector, IFile file, 
			int offset, int length, CharSequence content) throws CoreException {
		ReusableMatchAccess matchAccess = new ReusableMatchAccess();
		
		matchAccess.initialize(file, offset, length, content);
		collector.beginReporting();
		boolean result = collector.acceptPatternMatch(matchAccess);
		collector.endReporting();
		
		return result;
	}


	/**
	 * Reports a Pattern match to a given {@link SeamSearchRequestor}
	 * 
	 * @param collector
	 * @param file
	 * @param offset
	 * @param length
	 * @return
	 */
	public static boolean acceptPaternMatch(SeamSearchRequestor collector, IFile file, int offset, int length) {
		try {
			IDocument document= getOpenDocument(file, evalNonFileBufferDocuments());
			
			if (document != null) {
				DocumentCharSequence documentCharSequence= new DocumentCharSequence(document);
				// assume all documents are non-binary
				return acceptPaternMatch(collector, file, offset, length, documentCharSequence);
			} else {
				FileCharSequenceProvider fileCharSequenceProvider = new FileCharSequenceProvider();
				CharSequence seq= null;
				try {
					seq= fileCharSequenceProvider.newCharSequence(file);
					if (hasBinaryContent(seq, file) && !collector.reportBinaryFile(file)) {
						return true;
					}
					return acceptPaternMatch(collector, file, offset, length, seq);
				} catch (FileCharSequenceProvider.FileCharSequenceException e) {
					e.throwWrappedException();
				} finally {
					if (seq != null) {
						try {
							fileCharSequenceProvider.releaseCharSequence(seq);
						} catch (IOException e) {
							SearchPlugin.log(e);
						}
					}
				}
			}
		} catch (UnsupportedCharsetException e) {
			SearchPlugin.log(e);
		} catch (IllegalCharsetNameException e) {
			SearchPlugin.log(e);
		} catch (IOException e) {
			SearchPlugin.log(e);
		} catch (CoreException e) {
			SearchPlugin.log(e);
		} catch (StackOverflowError e) {
		} 
		return false;
	}

	/**
	 * Evaluates all files in this scope.
	 * 
	 * @param status a {@link MultiStatus} to collect the error status that occurred while collecting resources.
	 * @return returns the files in the scope.
	 */
	public IFile[] evaluateProjectFilesInScope(IProject project, MultiStatus status) {
		return new ProjectFilesOfScopeCalculator(project, fCurrentScope, status).process();
	}

	private class ProjectFilesOfScopeCalculator implements IResourceProxyVisitor {
		private final IProject fProject;
		private final TextSearchScope fScope;
		private final MultiStatus fStatus;
		private ArrayList fFiles;

		public ProjectFilesOfScopeCalculator(IProject project, SeamSearchScope scope, MultiStatus status) {
			fProject = project;
			fScope = scope;
			fStatus= status;
		}
		
		public boolean visit(IResourceProxy proxy) {
			if (fScope == null)
				return false;
			
			boolean inScope= fScope.contains(proxy);

			if (inScope && proxy.getType() == IResource.FILE) {
				IFile file = (IFile)proxy.requestResource();
				if(!file.isSynchronized(IResource.DEPTH_ZERO)) {
					// The resource is out of sync with the file system
					// Just ignore this resource.
					return false;
				}
				
				if (fProject == file.getProject()) {
					fFiles.add(proxy.requestResource());
				}
			}
			
			return inScope;
		}
		
		public IFile[] process() {
			fFiles= new ArrayList();
			try {
				IResource[] roots= fScope.getRoots();
				for (int i= 0; i < roots.length; i++) {
					try {
						IResource resource= roots[i];
						if (resource.isAccessible()) {
							resource.accept(this, 0);
						}
					} catch (CoreException ex) {
						// report and ignore
						fStatus.add(ex.getStatus());
					}
				}
				return (IFile[]) fFiles.toArray(new IFile[fFiles.size()]);
			} finally {
				fFiles= null;
			}
		}
	}
}
