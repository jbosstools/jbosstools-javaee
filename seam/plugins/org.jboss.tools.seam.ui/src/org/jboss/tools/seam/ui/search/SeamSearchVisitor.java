package org.jboss.tools.seam.ui.search;

import java.io.IOException;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.internal.core.JavaModel;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.text.FastJavaPartitionScanner;
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
import org.eclipse.search.internal.core.text.FilesOfScopeCalculator;
import org.eclipse.search.internal.core.text.TextSearchVisitor;
import org.eclipse.search.internal.core.text.TextSearchVisitor.ReusableMatchAccess;
import org.eclipse.search.internal.ui.Messages;
import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.search.internal.ui.SearchPlugin;
import org.eclipse.search.ui.NewSearchUI;
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
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.IOpenableElement;
import org.jboss.tools.seam.core.IRole;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamContextShortVariable;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamDeclaration;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamJavaSourceReference;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamScope;
import org.jboss.tools.seam.core.ISeamTextSourceReference;
import org.jboss.tools.seam.core.ISeamXmlFactory;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamPreferences;
import org.jboss.tools.seam.internal.core.AbstractSeamDeclaration;
import org.jboss.tools.seam.internal.core.BijectedAttribute;
import org.jboss.tools.seam.internal.core.Role;
import org.jboss.tools.seam.internal.core.SeamComponent;
import org.jboss.tools.seam.internal.core.SeamJavaContextVariable;
import org.jboss.tools.seam.internal.core.el.ELOperandToken;
import org.jboss.tools.seam.internal.core.el.ELStringToken;
import org.jboss.tools.seam.internal.core.el.ELToken;
import org.jboss.tools.seam.internal.core.el.ElVarSearcher;
import org.jboss.tools.seam.internal.core.el.SeamELCompletionEngine;
import org.jboss.tools.seam.internal.core.el.SeamELOperandTokenizerForward;
import org.jboss.tools.seam.internal.core.el.SeamELStringTokenizer;
import org.jboss.tools.seam.internal.core.el.SeamELTokenizer;
import org.jboss.tools.seam.internal.core.el.ElVarSearcher.Var;
import org.jboss.tools.seam.internal.core.scanner.ScannerException;
import org.jboss.tools.seam.internal.core.scanner.java.AnnotatedASTNode;
import org.jboss.tools.seam.internal.core.scanner.java.ResolvedAnnotation;
import org.jboss.tools.seam.internal.core.scanner.java.SeamAnnotations;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.text.java.scanner.JavaAnnotationScanner;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SeamSearchVisitor {
	private final Matcher[] fMatchers;
	private final SeamSearchRequestor fCollector;

	private final MultiStatus fStatus;
	private IProgressMonitor fProgressMonitor;
	private int fNumberOfScannedFiles;
	private int fNumberOfFilesToScan;
	private ISeamProject fCurrentSeamProject;
	private Map fDocumentsInEditors;
	private final TextSearchVisitor.ReusableMatchAccess fMatchAccess;

	private IFile fCurrentFile;
	private final FileCharSequenceProvider fFileCharSequenceProvider;
	private final SeamELCompletionEngine fCompletionEngine;
	
	public SeamSearchVisitor(SeamSearchRequestor collector,
			Pattern[] searchPatterns) {
		fCollector= collector;
		fStatus= new MultiStatus(NewSearchUI.PLUGIN_ID, IStatus.OK, SearchMessages.TextSearchEngine_statusMessage, null);
		fMatchers = new Matcher[searchPatterns == null ? 0 : searchPatterns.length];
		for (int i = 0; searchPatterns != null && i < searchPatterns.length; i++) {
			fMatchers[i]= searchPatterns[i].pattern().length() == 0 ? null : searchPatterns[i].matcher(new String());
		}
		fFileCharSequenceProvider= new FileCharSequenceProvider();
		fMatchAccess= new ReusableMatchAccess();
		fCompletionEngine = new SeamELCompletionEngine();
	}

	SeamSearchScope fCurrentScope = null;
	public IStatus search(TextSearchScope scope, IProgressMonitor monitor) {
		try {
			if (scope instanceof SeamSearchScope) {
				fCurrentScope = (SeamSearchScope)scope;
				if (((SeamSearchScope)scope).isLimitToDeclarations()) {
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

	public IStatus searchForDeclarations(ISeamProject[] projects, IProgressMonitor monitor) {
		fProgressMonitor= monitor == null ? new NullProgressMonitor() : monitor;
        fNumberOfScannedFiles= 0;
        fNumberOfFilesToScan= projects.length;
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
	public boolean processSeamReferencesInProject(ISeamProject project) {
		IFile[] files = fCurrentScope == null ? null :
					fCurrentScope.evaluateFilesInScope(fStatus);
		
		
		
		fELVarSearcher = new ElVarSearcher(project, fCompletionEngine);
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
		    if (!fCollector.acceptFile(file) || fMatchers == null) {
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

	private List<Var> varListForCurentValidatedNode = new ArrayList<Var>();
	private void locateMatchesInDom(IFile file, CharSequence content) {
		varListForCurentValidatedNode.clear();
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
		Var var = ElVarSearcher.findVar(parent);
		if(var!=null) {
			varListForCurentValidatedNode.add(var);
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
			varListForCurentValidatedNode.remove(var);
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
					String string = "#{" + value + "}";
					locateMatchesInString(file, string, offset - 2, content);
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
			SeamELStringTokenizer st = new SeamELStringTokenizer(string);
			List<ELStringToken> tokens = st.getTokens();
			for (ELStringToken stringToken : tokens) {
				stringToken.setStart(offset + stringToken.getStart() + 2);
				locateMatchesInEL(file, stringToken, content);
			}
		}
	}

	private void locateMatchesInEL(IFile file, ELStringToken el, CharSequence content) throws CoreException {
		String exp = el.getBody();
		SeamELTokenizer elTokenizer = new SeamELTokenizer(exp);
		List<ELToken> tokens = elTokenizer.getTokens();
		for (ELToken token : tokens) {
			if(token.getType()==ELToken.EL_VARIABLE_TOKEN) {
//				validateElOperand(file, token, el.getStart());
				String operand = token.getText();
				String varName = operand;
				int offsetOfToken = el.getStart() + token.getStart();
				SeamELOperandTokenizerForward forwardTokenizer = new SeamELOperandTokenizerForward(operand, 0);
				List<ELOperandToken>operandTokens = forwardTokenizer.getTokens();
				List<List<ELOperandToken>> variations = SeamELCompletionEngine.getPossibleVarsFromPrefix(operandTokens);

				for (List<ELOperandToken> variation : variations) {
					int start = variation.get(0).getStart();
					int end = variation.get(variation.size() - 1).getStart() + 
									variation.get(variation.size() - 1).getLength();
					String variationText = operand.substring(start, end);
					
					if (!matches(variationText)) 
						continue;

					int offsetOfOperandToken = offsetOfToken + start;
					int lengthOfOperandToken = end - start;
					fMatchAccess.initialize(file, offsetOfOperandToken, lengthOfOperandToken, content);
					boolean res= fCollector.acceptPatternMatch(fMatchAccess);
					if (!res) {
						return; // no further reporting requested
					}
					
					System.out.println("");
				}
			}
		}
	}

	
	
	public boolean processSeamDeclarationsInProject(ISeamProject project) {
		try {
			Set<ISeamContextVariable> variables = project.getVariables(true);
			Set<String> namesToExclude = new HashSet<String>();
			boolean continueSearch = true; // Is to be set to false in case of at least one component or role/out/databinder declaration is found
			
			// Search for Seam components and @Name, @Role, @Out/DataBinder  
			for (ISeamContextVariable variable : variables) {
				String varName = variable.getName();
				ISeamContextVariable origin = variable;
				
				if (!matches(varName)) 
					continue;

				if (variable instanceof ISeamContextShortVariable) {
					variable = ((ISeamContextShortVariable)variable).getOriginal();
				}
				
				if (variable instanceof SeamComponent) {
					namesToExclude.add(varName);
					SeamComponent comp = (SeamComponent)variable;
					Set<ISeamComponentDeclaration> declarations = comp.getAllDeclarations();
					for (ISeamComponentDeclaration decl : declarations) {
						if (decl instanceof ISeamJavaSourceReference) {
							ISeamJavaSourceReference sourceRef = (ISeamJavaSourceReference)decl;
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
							continueSearch = false;
						} else if (decl instanceof IOpenableElement) {
							IResource resource = decl.getResource();
							String name = decl.getName();
							
							ISeamTextSourceReference textSourceReference = decl.getLocationFor(AbstractSeamDeclaration.PATH_OF_NAME);
							int offset = textSourceReference == null ? decl.getStartPosition() : textSourceReference.getStartPosition();
							int length = textSourceReference == null ? decl.getLength() : textSourceReference.getLength();

							fMatchAccess.initialize((IFile)resource, offset, length, (CharSequence)name);
							boolean res= fCollector.acceptPatternMatch(fMatchAccess);
							if (!res) {
								return true; // no further reporting requested
							}
							continueSearch = false;
						}
					}
				} else if (variable instanceof IRole) {
					namesToExclude.add(varName);
					// add the declaration
					ISeamDeclaration decl = (ISeamDeclaration)variable;
					IResource resource = decl.getResource();
					String name = decl.getName();
					
					ISeamTextSourceReference textSourceReference = decl.getLocationFor(AbstractSeamDeclaration.PATH_OF_NAME);
					if (textSourceReference != null) {
						int offset = textSourceReference.getStartPosition();
						int length = textSourceReference.getLength();
						fMatchAccess.initialize((IFile)resource, offset, length, (CharSequence)name);
						boolean res= fCollector.acceptPatternMatch(fMatchAccess);
						if (!res) {
							return true; // no further reporting requested
						}
						continueSearch = false;
					}					
					
				} else if (variable instanceof IBijectedAttribute) {
					namesToExclude.add(varName);
					IBijectedAttribute ba = (IBijectedAttribute)variable;
					BijectedAttributeType[] types = ba.getTypes();
					boolean hasDeclarationType = false;
					for (int i = 0; !hasDeclarationType && types != null && i < types.length; i++) {
						if (types[i] == BijectedAttributeType.OUT ||
								types[i] == BijectedAttributeType.DATA_BINDER ||
								types[i] == BijectedAttributeType.DATA_MODEL_SELECTION) {
							hasDeclarationType = true;
						}
					}
					if (hasDeclarationType) {
						// add the declaration
						ISeamDeclaration decl = (ISeamDeclaration)variable;
						IResource resource = decl.getResource();
						String name = decl.getName();
						
						ISeamTextSourceReference textSourceReference = decl.getLocationFor(AbstractSeamDeclaration.PATH_OF_NAME);
						if (textSourceReference != null) {
							int offset = textSourceReference.getStartPosition();
							int length = textSourceReference.getLength();

							fMatchAccess.initialize((IFile)resource, offset, length, (CharSequence)name);
							boolean res= fCollector.acceptPatternMatch(fMatchAccess);
							if (!res) {
								return true; // no further reporting requested
							}
							continueSearch = false;
						}					
					}
				}
			}
			if (continueSearch) {
				// Search for Seam factories 
				for (ISeamContextVariable variable : variables) {
					String varName = variable.getName();
					if (namesToExclude.contains(varName)) // Do not process used names
						continue;
					if (!matches(varName)) 
						continue;
	
					if (variable instanceof ISeamDeclaration) {
						ISeamDeclaration decl = (ISeamDeclaration)variable;
						IResource resource = decl.getResource();
						String name = decl.getName();
						
						ISeamTextSourceReference textSourceReference = decl.getLocationFor(AbstractSeamDeclaration.PATH_OF_NAME);
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
			}
		} catch (CoreException ce) {
			String[] args= { getExceptionMessage(ce), project.getResource().getFullPath().makeRelative().toString()};
			String message= Messages.format(SearchMessages.TextSearchVisitor_error, args); 
			fStatus.add(new Status(IStatus.WARNING, NewSearchUI.PLUGIN_ID, IStatus.WARNING, message, ce));
		}
		if (fProgressMonitor.isCanceled())
			throw new OperationCanceledException(SearchMessages.TextSearchVisitor_canceled);
		
		return true;
/*		
		IFile file = null; // was a parameter
		try {
		    if (!fCollector.acceptFile(file) || fMatcher == null) {
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
	*/
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
    	String taskName= fMatchers[0] == null ? SearchMessages.TextSearchVisitor_filesearch_task_label :  Messages.format(SearchMessages.TextSearchVisitor_textsearch_task_label, fMatchers[0].pattern());
    	
    	return taskName;
	}
	
	public IStatus searchForReferences(ISeamProject[] projects, IProgressMonitor monitor) {
		fProgressMonitor= monitor == null ? new NullProgressMonitor() : monitor;
        fNumberOfScannedFiles= 0;
        fNumberOfFilesToScan= projects.length;
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
	
	
	
	private boolean matches(CharSequence searchInput) {
		try {
			for (int i = 0; fMatchers != null && i < fMatchers.length; i++) {
				if (fMatchers[i] == null)
					continue;
				fMatchers[i].reset(searchInput);
				if (fMatchers[i].find() && fMatchers[i].group().equals(searchInput)) {
					return true;
				}
			}
		} finally {
//			fMatcher.reset();
		}
		return false;
	}
	
	private String getExceptionMessage(Exception e) {
		String message= e.getLocalizedMessage();
		if (message == null) {
			return e.getClass().getName();
		}
		return message;
	}

	private boolean hasBinaryContent(CharSequence seq, IFile file) throws CoreException {
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
	private Map evalNonFileBufferDocuments() {
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

	private void evaluateTextEditor(Map result, IEditorPart ep) {
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
		IDocument document= (IDocument) fDocumentsInEditors.get(file);
		if (document == null) {
			ITextFileBufferManager bufferManager= FileBuffers.getTextFileBufferManager();
			ITextFileBuffer textFileBuffer= bufferManager.getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
			if (textFileBuffer != null) {
				document= textFileBuffer.getDocument();
			}
		}
		return document;
	}
	
	private String getCharSetName(IFile file) {
		try {
			return file.getCharset();
		} catch (CoreException e) {
			return "unknown"; //$NON-NLS-1$
		}
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

	class ProjectFilesOfScopeCalculator implements IResourceProxyVisitor {
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

			if (inScope && proxy.getType() == IResource.FILE && 
					fProject == proxy.requestResource().getProject()) {
				fFiles.add(proxy.requestResource());
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
