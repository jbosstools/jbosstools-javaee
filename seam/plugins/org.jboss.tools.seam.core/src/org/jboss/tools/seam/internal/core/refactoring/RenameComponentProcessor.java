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
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
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
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamTextSourceReference;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;
import org.jboss.tools.seam.internal.core.scanner.java.SeamAnnotations;
import org.jboss.tools.seam.internal.core.validation.SeamContextValidationHelper;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Alexey Kazakov, Daniel Azarov
 */
public class RenameComponentProcessor extends RenameProcessor {
	private static final String JAVA_EXT = "java"; //$NON-NLS-1$
	private static final String XML_EXT = "xml"; //$NON-NLS-1$
	private static final String XHTML_EXT = "xhtml"; //$NON-NLS-1$
	private static final String JSP_EXT = "jsp"; //$NON-NLS-1$
	private static final String PROPERTIES_EXT = "properties"; //$NON-NLS-1$
	
	private static final String SEAM_PROPERTIES_FILE = "seam.properties"; //$NON-NLS-1$

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

	private void scan(IContainer container){
		try{
			for(IResource resource : container.members()){
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
		} else if(ext.equalsIgnoreCase(XML_EXT) || ext.equalsIgnoreCase(XHTML_EXT) || ext.equalsIgnoreCase(JSP_EXT))
			scanDOM(file, content);
		else if(ext.equalsIgnoreCase(PROPERTIES_EXT))
			scanProperties(file, content);
	}

	private void findDeclarations() throws CoreException{
		if(component.getJavaDeclaration() != null)
			renameJavaDeclaration(component.getJavaDeclaration());

		Set<ISeamXmlComponentDeclaration> xmlDecls = component.getXmlDeclarations();

		for(ISeamXmlComponentDeclaration xmlDecl : xmlDecls){
			renameXMLDeclaration(xmlDecl);
		}
	}
	
	private void renameJavaDeclaration(ISeamJavaComponentDeclaration javaDecl) throws CoreException{
		declarationFile = (IFile)javaDecl.getResource();
		if(declarationFile != null && !coreHelper.isJar(javaDecl)){
			ISeamTextSourceReference location = ((SeamComponentDeclaration)javaDecl).getLocationFor(ISeamXmlComponentDeclaration.NAME);
			if(location != null){
				TextFileChange change = getChange(declarationFile);
				TextEdit edit = new ReplaceEdit(location.getStartPosition(), location.getLength(), "\""+newName+"\""); //$NON-NLS-1$ //$NON-NLS-2$
				change.addEdit(edit);
			}
		}
	}
	
	private void renameXMLDeclaration(ISeamXmlComponentDeclaration xmlDecl){
		declarationFile = (IFile)xmlDecl.getResource();
		if(declarationFile != null && !coreHelper.isJar(xmlDecl)){
			ISeamTextSourceReference location = ((SeamComponentDeclaration)xmlDecl).getLocationFor(ISeamXmlComponentDeclaration.NAME);
			if(location != null)
				changeXMLNode(location, declarationFile);
		}
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
		
		if(!file.getName().equals(SEAM_PROPERTIES_FILE))
			return;
		
		StringTokenizer tokenizer = new StringTokenizer(content, "#= \t\r\n\f", true); //$NON-NLS-1$
		
		String lastToken = "\n"; //$NON-NLS-1$
		int offset = 0;
		boolean comment = false;
		boolean key = true;
		
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken("#= \t\r\n\f"); //$NON-NLS-1$
			if(token.equals("\r")) //$NON-NLS-1$
				token = "\n"; //$NON-NLS-1$
			
			if(token.equals("#") && lastToken.equals("\n")) //$NON-NLS-1$ //$NON-NLS-2$
				comment = true;
			else if(token.equals("\n") && comment) //$NON-NLS-1$
				comment = false;
			
			if(!comment){
				if(!token.equals("\n") && lastToken.equals("\n")) //$NON-NLS-1$ //$NON-NLS-2$
					key = true;
				else if(key && (token.equals("=") || token.equals(" "))) //$NON-NLS-1$ //$NON-NLS-2$
					key = false;
				
				if(key && token.startsWith(component.getName())){
					String changeText = token.replaceFirst(component.getName(), newName);
					TextFileChange change = getChange(file);
					TextEdit edit = new ReplaceEdit(offset, token.length(), changeText);
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
			result.addFatalError(SeamCoreMessages.RENAME_SEAM_COMPONENT_PROCESSOR_THIS_IS_NOT_A_SEAM_COMPONENT);
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
		rootChange = new CompositeChange(SeamCoreMessages.RENAME_SEAM_COMPONENT_PROCESSOR_RENAME_SEAM_COMPONENT);
		
		findDeclarations();
		
		findAnnotations();
		
		findELReferences();
		
		return rootChange;
	}
	
	private void findAnnotations(){
		// find @In annotations
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(declarationFile.getProject(), true);
		
		Set<IBijectedAttribute> inSet = seamProject.getBijectedAttributesByName(component.getName(), BijectedAttributeType.IN);
		
		for(IBijectedAttribute inAtt : inSet){
			ISeamTextSourceReference location = inAtt.getLocationFor(SeamAnnotations.IN_ANNOTATION_TYPE);
			if(location != null)
				changeAnnotation(location, (IFile)inAtt.getResource());
		}
		
		// find @Factory annotations
		Set<ISeamFactory> factorySet = seamProject.getFactoriesByName(component.getName());
		
		for(ISeamFactory factory : factorySet){
			IFile file = (IFile)factory.getResource();
			if(file.getFileExtension().equalsIgnoreCase(JAVA_EXT)){
				ISeamTextSourceReference location = factory.getLocationFor(SeamAnnotations.FACTORY_ANNOTATION_TYPE);
				if(location != null)
					changeAnnotation(location, file);
			}else{
				ISeamTextSourceReference location = factory.getLocationFor(ISeamXmlComponentDeclaration.NAME);
				if(location != null)
					changeXMLNode(location, file);
			}
		}
	}
	
	private boolean isBadLocation(ISeamTextSourceReference location){
		if(location.getStartPosition() == 0 && location.getLength() == 0)
			return true;
		else
			return false;
	}
	
	private void changeXMLNode(ISeamTextSourceReference location, IFile file){
		if(isBadLocation(location))
			return;
		
		String content = null;
		try {
			content = FileUtil.readStream(file.getContents());
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
			return;
		}
		
		TextFileChange change = getChange(file);
		
		String text = content.substring(location.getStartPosition(), location.getStartPosition()+location.getLength());
		if(text.startsWith("<")){ //$NON-NLS-1$
			int position = text.lastIndexOf("/>"); //$NON-NLS-1$
			if(position < 0){
				position = text.lastIndexOf(">"); //$NON-NLS-1$
			}
			
			TextEdit edit = new ReplaceEdit(location.getStartPosition()+position, 0, " name=\""+newName+"\""); //$NON-NLS-1$ //$NON-NLS-2$
			change.addEdit(edit);
		}else{
			TextEdit edit = new ReplaceEdit(location.getStartPosition(), location.getLength(), newName);
			change.addEdit(edit);
		}
	}
	
	private void changeAnnotation(ISeamTextSourceReference location, IFile file){
		if(isBadLocation(location))
			return;

		String content = null;
		try {
			content = FileUtil.readStream(file.getContents());
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
			return;
		}
		
		TextFileChange change = getChange(file);
		
		String text = content.substring(location.getStartPosition(), location.getStartPosition()+location.getLength());
		int openBracket = text.indexOf("("); //$NON-NLS-1$
		if(openBracket > 0){
			int closeBracket = text.indexOf(")", openBracket); //$NON-NLS-1$
			int openQuote = text.indexOf("\"", openBracket); //$NON-NLS-1$
			int equals = text.indexOf("=", openBracket); //$NON-NLS-1$
			int value = text.indexOf("value", openBracket); //$NON-NLS-1$
			
			if(closeBracket == openBracket+1){ // empty brackets
				String newText = "\""+newName+"\""; //$NON-NLS-1$ //$NON-NLS-2$
				TextEdit edit = new ReplaceEdit(location.getStartPosition()+openBracket+1, 0, newText);
				change.addEdit(edit);
			}else if(value > 0){ // construction value="name" found so change name
				String newText = text.replace(component.getName(), newName);
				TextEdit edit = new ReplaceEdit(location.getStartPosition(), location.getLength(), newText);
				change.addEdit(edit);
			}else if(equals > 0){ // other parameters are found
				String newText = "value=\""+newName+"\","; //$NON-NLS-1$ //$NON-NLS-2$
				TextEdit edit = new ReplaceEdit(location.getStartPosition()+openBracket+1, 0, newText);
				change.addEdit(edit);
			}else{ // other cases
				String newText = text.replace(component.getName(), newName);
				TextEdit edit = new ReplaceEdit(location.getStartPosition(), location.getLength(), newText);
				change.addEdit(edit);
			}
		}else{
			String newText = "(\""+newName+"\")"; //$NON-NLS-1$ //$NON-NLS-2$
			TextEdit edit = new ReplaceEdit(location.getStartPosition()+location.getLength(), 0, newText);
			change.addEdit(edit);
		}
	}

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
		return SeamCoreMessages.RENAME_SEAM_COMPONENT_PROCESSOR_RENAME_SEAM_COMPONENT;
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