 /*******************************************************************************
  * Copyright (c) 2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.refactoring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.text.FastJavaPartitionScanner;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RenameProcessor;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
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
import org.jboss.tools.common.el.core.model.ELInstance;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELModel;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.common.el.core.parser.ELParser;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamTextSourceReference;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;
import org.jboss.tools.seam.internal.core.validation.SeamContextValidationHelper;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Alexey Kazakov
 */
public class RenameComponentProcessor extends RenameProcessor {
	private static final String ANNOTATION_NAME = "org.jboss.seam.annotations.Name";
	private static final String ANNOTATION_IN = "org.jboss.seam.annotations.In";
	private static final String ANNOTATION_FACTORY = "org.jboss.seam.annotations.Factory";
	
	private static final String JAVA_EXT = "java";
	private static final String XML_EXT = "xml";
	private static final String XHTML_EXT = "xhtml";
	private static final String JSP_EXT = "jsp";
	private static final String PROPERTIES_EXT = "properties";
	
	private static final String COMPONENTS_FILE = "components.xml";
	private static final String COMPONENT_NODE = "component";
	private static final String FACTORY_NODE = "factory";
	private static final String NAME_ATTRIBUTE = "name";

	private IFile declarationFile=null;
	private ISeamComponent component;
	private String newName;
	SeamContextValidationHelper coreHelper = new SeamContextValidationHelper();

	/**
	 * @param component Renamed component
	 */
	public RenameComponentProcessor(ISeamComponent component) {
		super();
		this.component = component;
	}

	public ISeamComponent getComponent() {
		return component;
	}

	public void setComponent(ISeamComponent component) {
		this.component = component;
	}
	
	public void setNewComponentName(String componentName){
		this.newName = componentName;
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
	
	private List<IAnnotation> getAnnotations(IFile file, String[] annotationNames){
		ArrayList<IAnnotation> annotations = new ArrayList<IAnnotation>();
		try{
			ICompilationUnit unit = getCompilationUnit(file);
			for(IType type : unit.getAllTypes()){
				for(IAnnotation annotation : type.getAnnotations()){
					for(String annotationName : annotationNames){
						if(EclipseJavaUtil.resolveType(type, annotation.getElementName()).equals(annotationName))
							annotations.add(annotation);
					}
				}
			}
			for(IJavaElement element : unit.getChildren()){
				List<IAnnotation> list =  getAnnotations(element, annotationNames);
				annotations.addAll(list);
				
			}
		}catch(CoreException ex){
			SeamCorePlugin.getDefault().logError(ex);
		}
		return annotations;
	}
	
	private List<IAnnotation> getAnnotations(IJavaElement element, String[] annotationNames){
		IType type = (IType)element.getAncestor(IJavaElement.TYPE);
		ArrayList<IAnnotation> annotations = new ArrayList<IAnnotation>();
		if(element instanceof IAnnotatable){
			try{
				for(IAnnotation annotation : ((IAnnotatable)element).getAnnotations()){
					for(String annotationName : annotationNames){
						if(EclipseJavaUtil.resolveType(type, annotation.getElementName()).equals(annotationName))
							annotations.add(annotation);
					}
				}
			}catch(JavaModelException ex){
				SeamCorePlugin.getDefault().logError(ex);
			}
		}
		if(element instanceof IParent){
			try{
				for(IJavaElement child : ((IParent)element).getChildren()){
					List<IAnnotation> list =  getAnnotations(child, annotationNames);
					annotations.addAll(list);
				}
			}catch(JavaModelException ex){
				SeamCorePlugin.getDefault().logError(ex);
			}
		}
		
		return annotations;
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

	// we need to find references in .java .xml .xhtml .jsp .properties files
	private void findELReferences(){
		if(declarationFile == null)
			return;
		
		SeamProjectsSet projectsSet = new SeamProjectsSet(declarationFile.getProject());

		IProject[] projects = projectsSet.getAllProjects();
		for (int i = 0; i < projects.length; i++) {
			scan(projects[i]);
		}
	}

	private void scan(IProject project){
		try{
			for(IResource resource : project.members()){
				if(resource instanceof IFolder)
					scan((IFolder) resource);
				else if(resource instanceof IFile)
					scan((IFile) resource);
			}
		}catch(CoreException ex){
			SeamCorePlugin.getDefault().logError(ex);
		}
	}
	
	private void scan(IFolder folder){
		try{
			for(IResource resource : folder.members()){
				if(resource instanceof IFolder)
					scan((IFolder) resource);
				else if(resource instanceof IFile)
					scan((IFile) resource);
			}
		}catch(CoreException ex){
			SeamCorePlugin.getDefault().logError(ex);
		}
	}
	
	private void scan(IFile file){
		String ext = file.getFileExtension();
		String content = null;
		try {
			content = FileUtil.readStream(file.getContents());
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
			return;
		}
		if(ext.equalsIgnoreCase(JAVA_EXT)){
			scanJava(file, content);
			lookingForAnnotations(file);
		} else if(ext.equalsIgnoreCase(XML_EXT) || ext.equalsIgnoreCase(XHTML_EXT) || ext.equalsIgnoreCase(JSP_EXT))
			scanDOM(file, content);
		else if(ext.equalsIgnoreCase(PROPERTIES_EXT))
			scanProperties(file, content);
		
	}
	
	private void lookingForAnnotations(IFile file){
		String source;
		List<IAnnotation> annotations = getAnnotations(file, new String[]{ANNOTATION_IN, ANNOTATION_FACTORY});
		for(IAnnotation annotation : annotations){
			source = "";
			int memberValueNumber = 0;
			try{
				source = annotation.getSource();
				memberValueNumber = annotation.getMemberValuePairs().length;
			}catch(JavaModelException ex){
				SeamCorePlugin.getDefault().logError(ex);
			}
			
			if(source.indexOf("\""+component.getName()+"\"") >= 0){
				changeAnnotation(file, annotation);
			}else if(annotation.getParent().getElementType() == IJavaElement.FIELD){
				IField field = (IField)annotation.getParent();
				if(memberValueNumber == 0 && field.getElementName().equals(component.getName())){
					
//					RenameFieldProcessor fieldProcessor = new RenameFieldProcessor(field);
//					fieldProcessor.setUpdateReferences(true);
//					fieldProcessor.setNewElementName(newName);
//					try{
//						System.out.println("Rename Field");
//						DynamicValidationRefactoringChange change = (DynamicValidationRefactoringChange)fieldProcessor.createChange(new NullProgressMonitor());
//						rootChange.add(change);
//					}catch(CoreException ex){
//						SeamCorePlugin.getDefault().logError(ex);
//					}
					changeAnnotation(file, annotation);
				}
			}else if(annotation.getParent().getElementType() == IJavaElement.METHOD){
				IMethod method = (IMethod)annotation.getParent();
				if(memberValueNumber == 0 && getFieldName(method.getElementName()).equals(component.getName())){
//					TextChangeManager man = new TextChangeManager();
//					RenameNonVirtualMethodProcessor methodProcessor = new RenameNonVirtualMethodProcessor(method);
//					methodProcessor.setUpdateReferences(true);
//					methodProcessor.setNewElementName(newName);
//					try{
//						System.out.println("Rename Method");
//						DynamicValidationRefactoringChange change = (DynamicValidationRefactoringChange)methodProcessor.createChange(new NullProgressMonitor());
//						rootChange.add(change);
//					}catch(CoreException ex){
//						SeamCorePlugin.getDefault().logError(ex);
//					}
					changeAnnotation(file, annotation);
				}
			}
		}
	}
	
	private void findDeclarations() throws CoreException{
		if(component.getJavaDeclaration() != null)
			renameJavaDeclaration(component.getJavaDeclaration());
		
		Set<ISeamXmlComponentDeclaration> xmlDecls = component.getXmlDeclarations();
		Iterator<ISeamXmlComponentDeclaration> iter = xmlDecls.iterator();
		while(iter.hasNext()){
			ISeamXmlComponentDeclaration xmlDecl = iter.next();
			if(xmlDecl != null)
				renameXMLDeclaration(xmlDecl);
		}
	}
	
	private void renameJavaDeclaration(ISeamJavaComponentDeclaration javaDecl) throws CoreException{
		declarationFile = (IFile)javaDecl.getResource();
		if(declarationFile != null && !coreHelper.isJar(javaDecl)){
			ISeamTextSourceReference location = ((SeamComponentDeclaration)javaDecl).getLocationFor(ISeamXmlComponentDeclaration.NAME);
			if(location != null){
				TextFileChange change = getChange(declarationFile);
				TextEdit edit = new ReplaceEdit(location.getStartPosition(), location.getLength(), "\""+newName+"\"");
				change.addEdit(edit);
			}
		}
	}
	
	private void renameXMLDeclaration(ISeamXmlComponentDeclaration xmlDecl){
		declarationFile = (IFile)xmlDecl.getResource();
		if(declarationFile != null && !coreHelper.isJar(xmlDecl)){
			String content = null;
			try {
				content = FileUtil.readStream(declarationFile.getContents());
			} catch (CoreException e) {
				SeamCorePlugin.getPluginLog().logError(e);
				return;
			}
			ISeamTextSourceReference location = ((SeamComponentDeclaration)xmlDecl).getLocationFor(ISeamXmlComponentDeclaration.NAME);
			if(location != null){
				String text = content.substring(location.getStartPosition(), location.getStartPosition()+location.getLength());
				if(text.startsWith("<")){
					int position = text.lastIndexOf("/>");
					if(position < 0){
						position = text.lastIndexOf(">");
					}
					TextFileChange change = getChange(declarationFile);
					TextEdit edit = new ReplaceEdit(location.getStartPosition()+position, 0, " name=\""+newName+"\"");
					change.addEdit(edit);
				}else{
					TextFileChange change = getChange(declarationFile);
					TextEdit edit = new ReplaceEdit(location.getStartPosition(), location.getLength(), newName);
					change.addEdit(edit);
				}
			}
		}
	}
	
	private void changeAnnotation(IFile file, IAnnotation annotation){
		try{
			String annotationText = annotation.getSource();
			//String annotationText = "@In(\""+newName+"\")";
			int open = annotationText.indexOf("(");
			if(open >= 0){
				annotationText = annotationText.substring(0, open) + "(\""+newName+"\")";
			}else
				annotationText += "(\""+newName+"\")";
		
			TextEdit edit = new ReplaceEdit(annotation.getSourceRange().getOffset(), annotation.getSourceRange().getLength(), annotationText);
			TextFileChange change = getChange(file);
			change.addEdit(edit);
		}catch(JavaModelException ex){
			SeamCorePlugin.getDefault().logError(ex);
		}
	}
	
	private String getFieldName(String methodName){
		if(methodName.startsWith("is") || methodName.startsWith("get") || methodName.startsWith("set")){
			if(methodName.startsWith("is"))
				return methodName.substring(2,3).toLowerCase()+methodName.substring(3);
			else
				return methodName.substring(3,4).toLowerCase()+methodName.substring(4);
		}else
			return "";
	}
	
	private void scanJava(IFile file, String content){
		try {
			FastJavaPartitionScanner scaner = new FastJavaPartitionScanner();
			Document document = new Document(content);
			scaner.setRange(document, 0, document.getLength());
			IToken token = scaner.nextToken();
			while(token!=null && token!=Token.EOF) {
				if(IJavaPartitions.JAVA_STRING.equals(token.getData())) {
					int length = scaner.getTokenLength();
					int offset = scaner.getTokenOffset();
					String value = document.get(offset, length);
					if(value.indexOf('{')>-1) {
						scanString(file, value, offset);
					}
				}
				token = scaner.nextToken();
			}
		} catch (BadLocationException e) {
			SeamCorePlugin.getDefault().logError(e);
		}
	}
	
	private void scanDOM(IFile file, String content){
		IModelManager manager = StructuredModelManager.getModelManager();
		if(manager == null) {
			return;
		}
		IStructuredModel model = null;		
		try {
			model = manager.getModelForRead(file);
			if (model instanceof IDOMModel) {
				IDOMModel domModel = (IDOMModel) model;
				IDOMDocument document = domModel.getDocument();
				scanChildNodes(file, document);
				//if(file.getName().equals(COMPONENTS_FILE))
				//	scanChildComponent(file, document);
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
	}
	
	private void scanChildComponent(IFile file, Node parent) {
		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node curentValidatedNode = children.item(i);
			if (Node.ELEMENT_NODE == curentValidatedNode.getNodeType()
					&& (curentValidatedNode.getNodeName().equals(COMPONENT_NODE)
						|| curentValidatedNode.getNodeName().equals(FACTORY_NODE))) {
				scanComponentNode(file, curentValidatedNode);
			}
			scanChildComponent(file, curentValidatedNode);
		}
	}
	
	private void scanComponentNode(IFile file, Node node) {
		Node nameNode = node.getAttributes().getNamedItem(NAME_ATTRIBUTE);
		if(nameNode != null){
			if(nameNode.getNodeValue().equals(component.getName())){
				IStructuredDocumentRegion region =  ((IDOMNode)node).getFirstStructuredDocumentRegion();
				TextFileChange change = getChange(file);
				
				
				TextEdit edit = new ReplaceEdit(region.getStartOffset(), region.getLength(), region.getFullText().replace(component.getName(), newName));
				change.addEdit(edit);
			}
		}
		
	}

	private void scanChildNodes(IFile file, Node parent) {
		NodeList children = parent.getChildNodes();
		for(int i=0; i<children.getLength(); i++) {
			Node curentValidatedNode = children.item(i);
			if(Node.ELEMENT_NODE == curentValidatedNode.getNodeType()) {
				scanNodeContent(file, ((IDOMNode)curentValidatedNode).getFirstStructuredDocumentRegion(), DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE);
			} else if(Node.TEXT_NODE == curentValidatedNode.getNodeType()) {
				scanNodeContent(file, ((IDOMNode)curentValidatedNode).getFirstStructuredDocumentRegion(), DOMRegionContext.XML_CONTENT);
			}
			scanChildNodes(file, curentValidatedNode);
		}
	}

	private void scanNodeContent(IFile file, IStructuredDocumentRegion node, String regionType) {
		ITextRegionList regions = node.getRegions();
		for(int i=0; i<regions.size(); i++) {
			ITextRegion region = regions.get(i);
			if(region.getType() == regionType) {
				String text = node.getFullText(region);
				if(text.indexOf("{")>-1) { //$NON-NLS-1$
					int offset = node.getStartOffset() + region.getStart();
					scanString(file, text, offset);
				}
			}
		}
	}
	
	private CompositeChange rootChange;
	private TextFileChange lastChange;
	
	// lets collect all changes for the same files in one MultiTextEdit
	private TextFileChange getChange(IFile file){
		if(lastChange != null && lastChange.getFile().equals(file))
			return lastChange;
		
		for(int i=0; i < rootChange.getChildren().length; i++){
			TextFileChange change = (TextFileChange)rootChange.getChildren()[i];
			if(change.getFile().equals(file)){
				lastChange = change;
				return lastChange;
			}
		}
		lastChange = new TextFileChange(file.getName(), file);
		MultiTextEdit root = new MultiTextEdit();
		lastChange.setEdit(root);
		rootChange.add(lastChange);
		
		return lastChange;
	}

	// looking for component references in EL
	private void scanString(IFile file, String string, int offset) {
		int startEl = string.indexOf("#{"); //$NON-NLS-1$
		if(startEl>-1) {
			ELParser parser = ELParserUtil.getJbossFactory().createParser();
			ELModel model = parser.parse(string);
			for (ELInstance instance : model.getInstances()) {
				for(ELInvocationExpression ie : instance.getExpression().getInvocations()){
					ELPropertyInvocation pi = findComponentReference(ie);
					if(pi != null){
						TextFileChange change = getChange(file);
						TextEdit edit = new ReplaceEdit(offset+pi.getStartPosition(), pi.getName().getStart()+pi.getName().getLength()-pi.getStartPosition(), newName);
						change.addEdit(edit);
					}
				}
			}
		}
	}
	
	private ELPropertyInvocation findComponentReference(ELInvocationExpression invocationExpression){
		ELInvocationExpression invExp = invocationExpression;
		while(invExp != null){
			if(invExp instanceof ELPropertyInvocation){
				if(((ELPropertyInvocation)invExp).getQualifiedName() != null && ((ELPropertyInvocation)invExp).getQualifiedName().equals(component.getName()))
						return (ELPropertyInvocation)invExp;
				else
					invExp = invExp.getLeft();
				
			}else{
				invExp = invExp.getLeft();
			}
		}
		return null;
	}

	private void scanProperties(IFile file, String content){
		scanString(file, content, 0);
		StringTokenizer tokenizer = new StringTokenizer(content, ".#= \t\r\n\f", true);
		
		String lastToken = "\n";
		int offset = 0;
		boolean comment = false;
		boolean key = true;
		
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken(".#= \t\r\n\f"); //$NON-NLS-1$
			if(token.equals("\r"))
				token = "\n";
			
			if(token.equals("#") && lastToken.equals("\n"))
				comment = true;
			else if(token.equals("\n") && comment)
				comment = false;
			
			if(!comment){
				if(!token.equals("\n") && lastToken.equals("\n"))
					key = true;
				else if(key && (token.equals("=") || token.equals(" ")))
					key = false;
				
				if(key && token.equals(component.getName())){
					TextFileChange change = getChange(file);
					TextEdit edit = new ReplaceEdit(offset, token.length(), newName);
					change.addEdit(edit);
				}
			}
			
			lastToken = token;
			offset += token.length();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#checkFinalConditions(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
	 */
	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws CoreException,
			OperationCanceledException {
		return new RefactoringStatus();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#checkInitialConditions(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		RefactoringStatus result = new RefactoringStatus();
		if(component==null) {
			result.addFatalError("This is not a Seam Component.");
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#createChange(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		rootChange = new CompositeChange("Rename Seam Component");
		
		findDeclarations();
		
		//findAnnotations();
		
		findELReferences();
		
		
		return rootChange;
	}
	
//	private void findAnnotations(){
//		Set<ISeamContextVariable> variables = seamProject.getVariablesByName(component.getName());
//		
//		Iterator<ISeamContextVariable> iter = variables.iterator();
//		while(iter.hasNext()){
//			ISeamContextVariable var = iter.next();
//			System.out.println("var - "+var.getClass());
//			if(var instanceof BijectedAttribute){
//				System.out.println("Bijected...");
//			}
//		}
//	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getElements()
	 */
	@Override
	public Object[] getElements() {
		return new ISeamComponent[]{component};
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return getClass().getName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getProcessorName()
	 */
	@Override
	public String getProcessorName() {
		return "Rename Seam Component";
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#isApplicable()
	 */
	@Override
	public boolean isApplicable() throws CoreException {
		return component!=null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#loadParticipants(org.eclipse.ltk.core.refactoring.RefactoringStatus, org.eclipse.ltk.core.refactoring.participants.SharableParticipants)
	 */
	@Override
	public RefactoringParticipant[] loadParticipants(RefactoringStatus status,
			SharableParticipants sharedParticipants) throws CoreException {
		return null;
	}
}