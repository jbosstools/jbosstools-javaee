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
package org.jboss.tools.seam.ui.refactoring;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.text.FastJavaPartitionScanner;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.services.IWorkbenchLocationService;
import org.eclipse.ui.menus.AbstractContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.jboss.tools.common.el.core.model.ELInstance;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELModel;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.common.el.core.parser.ELParser;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * @author Daniel Azarov
 */
public class SeamContributionFactory extends AbstractContributionFactory {
	private static final String ANNOTATION_NAME = "org.jboss.seam.annotations.Name";
	private static final String JAVA_EXT = "java"; //$NON-NLS-1$
	private static final String XML_EXT = "xml"; //$NON-NLS-1$
	private static final String XHTML_EXT = "xhtml"; //$NON-NLS-1$
	private static final String JSP_EXT = "jsp"; //$NON-NLS-1$
	private static final String PROPERTIES_EXT = "properties"; //$NON-NLS-1$
	
	String selectedText;
	IFile editorFile;
	String fileContent;
	
	public SeamContributionFactory(){
		super("","");
	}
	
	public SeamContributionFactory(String location, String namespace){
		super(location, namespace);
	}

	@Override
	public void createContributionItems(IServiceLocator serviceLocator,
			IContributionRoot additions) {
		
		if(serviceLocator.hasService(IWorkbenchLocationService.class)){
			IWorkbenchLocationService service = (IWorkbenchLocationService)serviceLocator.getService(IWorkbenchLocationService.class);
			IEditorPart editor = service.getWorkbenchWindow().getActivePage().getActiveEditor();
			Shell activeShell = service.getWorkbenchWindow().getShell();
			
			ISelection sel = editor.getEditorSite().getSelectionProvider().getSelection();
			
			if(sel == null || sel.isEmpty())
				return;
			
			if(sel instanceof TextSelection && editor.getEditorInput() instanceof FileEditorInput){
				TextSelection selection = (TextSelection)sel;
				
				selectedText = selection.getText();
				
				FileEditorInput input = (FileEditorInput)editor.getEditorInput();
			
				editorFile = input.getFile();
				
				String ext = editorFile.getFileExtension();
				fileContent = null;
				try {
					fileContent = FileUtil.readStream(editorFile.getContents());
				} catch (CoreException e) {
					SeamCorePlugin.getPluginLog().logError(e);
					return;
				}
				
				MenuManager mm = new MenuManager("Seam Refactor");
				mm.setVisible(true);
				
				IAnnotation nameAnnotation = getNameAnnotation(editorFile);
				if(nameAnnotation != null){
					try{
						ISourceRange range = nameAnnotation.getSourceRange();
						if(selection.getOffset() >= range.getOffset() && selection.getOffset()+selection.getLength() <= range.getOffset()+range.getLength()){
							mm.add(new SeamComponentRefactoringAction());
							
							additions.addContributionItem(mm, null);
						}
					}catch(JavaModelException ex){
						SeamCorePlugin.getPluginLog().logError(ex);
						return;
					}
				}
				
				boolean status = false;
				
				if(JAVA_EXT.equalsIgnoreCase(ext)){
					status = findContextVariableInJava(editorFile, fileContent, selection);
				} else if(XML_EXT.equalsIgnoreCase(ext) || XHTML_EXT.equalsIgnoreCase(ext) || JSP_EXT.equalsIgnoreCase(ext))
					status = findContextVariableInDOM(editorFile, fileContent, selection);
				else if(PROPERTIES_EXT.equalsIgnoreCase(ext))
					status = findContextVariableInProperties(editorFile, fileContent, selection);
				
				if(status){
					mm.add(new SeamContextVariableRefactoringAction());
					
					additions.addContributionItem(mm, null);
				}
			}
		}
	}
	
	private boolean findContextVariableInJava(IFile file, String content, TextSelection selection){
		try {
			FastJavaPartitionScanner scaner = new FastJavaPartitionScanner();
			Document document = new Document(content);
			scaner.setRange(document, 0, document.getLength());
			IToken token = scaner.nextToken();
			while(token!=null && token!=Token.EOF) {
				if(IJavaPartitions.JAVA_STRING.equals(token.getData())) {
					int length = scaner.getTokenLength();
					int offset = scaner.getTokenOffset();
					if(offset <= selection.getOffset() && (offset+length) >= (selection.getOffset()+selection.getLength())){
						String value = document.get(offset, length);
						if(value.indexOf('{')>-1) {
							return scanString(file, value, offset, selection);
						}
					}
				}
				token = scaner.nextToken();
			}
		} catch (BadLocationException e) {
			SeamCorePlugin.getDefault().logError(e);
		}
		return false;
	}
	
	private boolean scanString(IFile file, String string, int offset, TextSelection selection) {
		int startEl = string.indexOf("#{"); //$NON-NLS-1$
		if(startEl>-1) {
			ELParser parser = ELParserUtil.getJbossFactory().createParser();
			ELModel model = parser.parse(string);
			for (ELInstance instance : model.getInstances()) {
				for(ELInvocationExpression ie : instance.getExpression().getInvocations()){
					ELPropertyInvocation pi = findComponentReference(ie);
					if(pi != null){
						if(offset+pi.getStartPosition() == selection.getOffset() && pi.getLength() == selection.getLength())
							return true;
					}
				}
			}
		}
		return false;
	}
	
	private ELPropertyInvocation findComponentReference(ELInvocationExpression invocationExpression){
		ELInvocationExpression invExp = invocationExpression;
		while(invExp != null){
			if(invExp instanceof ELPropertyInvocation){
				if(((ELPropertyInvocation)invExp).getQualifiedName() != null && ((ELPropertyInvocation)invExp).getQualifiedName().equals(selectedText))
					return (ELPropertyInvocation)invExp;
				else
					invExp = invExp.getLeft();
				
			}else{
				invExp = invExp.getLeft();
			}
		}
		return null;
	}
	
	private boolean findContextVariableInDOM(IFile file, String content, TextSelection selection){
		IModelManager manager = StructuredModelManager.getModelManager();
		if(manager == null) {
			return false;
		}
		IStructuredModel model = null;		
		try {
			model = manager.getModelForRead(file);
			if (model instanceof IDOMModel) {
				IDOMModel domModel = (IDOMModel) model;
				IDOMDocument document = domModel.getDocument();
				//scanChildNodes(file, document);
			}
		} catch (CoreException e) {
			SeamCorePlugin.getDefault().logError(e);
        } catch (IOException e) {
        	SeamCorePlugin.getDefault().logError(e);
		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
		return false;
	}

	private boolean findContextVariableInProperties(IFile file, String content, TextSelection selection){
		return false;
	}
	
	private IAnnotation getNameAnnotation(IFile file){
		try{
			ICompilationUnit unit = getCompilationUnit(file);
			for(IType type : unit.getAllTypes()){
				for(IAnnotation annotation : type.getAnnotations()){
					if(EclipseJavaUtil.resolveType(type, annotation.getElementName()).equals(ANNOTATION_NAME))
						return annotation;
					}
			}
		}catch(CoreException ex){
			SeamCorePlugin.getDefault().logError(ex);
		}
		return null;
	}
	
	private ICompilationUnit getCompilationUnit(IFile file) throws CoreException {
		IProject project = file.getProject();
		IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
		for (IResource resource : EclipseResourceUtil.getJavaSourceRoots(project)) {
			if(resource.getFullPath().isPrefixOf(file.getFullPath())) {
				IPath path = file.getFullPath().removeFirstSegments(resource.getFullPath().segmentCount());
				IJavaElement element = javaProject.findElement(path);
				if(element instanceof ICompilationUnit) {
					return (ICompilationUnit)element;
				}
			}
		}
		return null;
	}
	
	class SeamComponentRefactoringAction extends Action{
		public SeamComponentRefactoringAction(){
			super("Seam Component Refactoring");
		}
		public void run(){
			
		}
	}
	class SeamContextVariableRefactoringAction extends Action{
		public SeamContextVariableRefactoringAction(){
			super("Seam Context Variable Refactoring");
		}
		public void run(){
			
		}
	}
}
