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
package org.jboss.tools.seam.internal.core.refactoring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.text.FastJavaPartitionScanner;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.RenameProcessor;
import org.eclipse.ltk.internal.core.refactoring.Messages;
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
import org.jboss.tools.common.model.project.ext.ITextSourceReference;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamProject;
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
 * @author Daniel Azarov
 */
public abstract class SeamRenameProcessor extends RenameProcessor {
	protected static final String JAVA_EXT = "java"; //$NON-NLS-1$
	protected static final String XML_EXT = "xml"; //$NON-NLS-1$
	protected static final String XHTML_EXT = "xhtml"; //$NON-NLS-1$
	protected static final String JSP_EXT = "jsp"; //$NON-NLS-1$
	protected static final String PROPERTIES_EXT = "properties"; //$NON-NLS-1$
	
	protected static final String SEAM_PROPERTIES_FILE = "seam.properties"; //$NON-NLS-1$
	
	protected RefactoringStatus status;
	
	private SeamContextValidationHelper coreHelper = new SeamContextValidationHelper();
	
	protected CompositeChange rootChange;
	protected TextFileChange lastChange;
	protected IFile declarationFile=null;
	protected SeamProjectsSet projectsSet;
	
	private String newName;
	private String oldName;
	
	public void setNewName(String newName){
		this.newName = newName;
	}
	
	protected String getNewName(){
		return newName;
	}
	
	protected void setOldName(String oldName){
		this.oldName = oldName;
	}
	
	public String getOldName(){
		return oldName;
	}
	
	// lets collect all changes for the same files in one MultiTextEdit
	protected TextFileChange getChange(IFile file){
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
	
	protected void findDeclarations(ISeamComponent component) throws CoreException{
		changeDeclarations(component);
		
		if(declarationFile == null)
			return;
		
		projectsSet = new SeamProjectsSet(declarationFile.getProject());

		IProject[] projects = projectsSet.getAllProjects();
		for (IProject project : projects) {
			ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
			if(seamProject != null){
				ISeamComponent comp = seamProject.getComponent(getOldName());
				if(comp != null)
					changeDeclarations(comp);
			}
		}
	}
	
	protected void findAnnotations(){
		if(declarationFile == null){
			status.addFatalError(Messages.format(SeamCoreMessages.SEAM_RENAME_PROCESSOR_DECLARATION_NOT_FOUND, getOldName()));
			return;
		}
		
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(declarationFile.getProject(), true);
		
		if(seamProject == null)
			return;
		
		findInFactoryAnnotations(seamProject);
		
		IProject[] projects = projectsSet.getAllProjects();
		for (IProject project : projects) {
			ISeamProject sProject = SeamCorePlugin.getSeamProject(project, true);
			if(sProject != null){
				findInFactoryAnnotations(sProject);
			}
		}
		
	}
	
	private void findInFactoryAnnotations(ISeamProject seamProject){
		// find @In annotations
		findAnnotations(seamProject, BijectedAttributeType.IN, SeamAnnotations.IN_ANNOTATION_TYPE);
		
		
		findFactories(seamProject);
	}
	
	private void findFactories(ISeamProject seamProject){
		// find @Factory annotations
		Set<ISeamFactory> factorySet = seamProject.getFactoriesByName(getOldName());
		
		for(ISeamFactory factory : factorySet){
			changeFactory(factory);
		}
	}
	
	private void findAnnotations(ISeamProject seamProject, BijectedAttributeType type, String locationPath){
		Set<IBijectedAttribute> attributes = seamProject.getBijectedAttributesByName(getOldName(), type);
		
		for(IBijectedAttribute attribute : attributes){
			ITextSourceReference location = attribute.getLocationFor(locationPath);
			changeAnnotation(location, (IFile)attribute.getResource());
		}
	}
	
	private void changeFactory(ISeamFactory factory){
		IFile file = (IFile)factory.getResource();
		
		if(file.getFileExtension().equalsIgnoreCase(JAVA_EXT)){
			ITextSourceReference location = factory.getLocationFor(SeamAnnotations.FACTORY_ANNOTATION_TYPE);
			changeAnnotation(location, file);
			
		}else{
			ITextSourceReference location = factory.getLocationFor(ISeamXmlComponentDeclaration.NAME);
			changeXMLNode(location, file);
			
		}
	}
	
	private boolean isBadLocation(ITextSourceReference location, IFile file){
		boolean flag;
		if(location == null)
			flag = true;
		else
			flag = location.getStartPosition() == 0 && location.getLength() == 0;
		
		if(flag)
			status.addFatalError(Messages.format(SeamCoreMessages.SEAM_RENAME_PROCESSOR_LOCATION_NOT_FOUND, file.getFullPath().toString()));
		return flag;
	}
	
	private void changeXMLNode(ITextSourceReference location, IFile file){
		if(isBadLocation(location, file))
			return;
		
		if(!isFileCorrect(file))
			return;
		
		String content = null;
		try {
			content = FileUtil.readStream(file.getContents());
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
			return;
		}
		
		String text = content.substring(location.getStartPosition(), location.getStartPosition()+location.getLength());
		if(text.startsWith("<")){ //$NON-NLS-1$
			int position = text.lastIndexOf("/>"); //$NON-NLS-1$
			if(position < 0){
				position = text.lastIndexOf(">"); //$NON-NLS-1$
			}
			change(file, location.getStartPosition()+position, 0, " name=\""+getNewName()+"\""); //$NON-NLS-1$ //$NON-NLS-2$
		}else{
			change(file, location.getStartPosition(), location.getLength(), getNewName());
		}
	}
	
	private void changeAnnotation(ITextSourceReference location, IFile file){
		if(isBadLocation(location, file))
			return;
		
		if(!isFileCorrect(file))
			return;

		String content = null;
		try {
			content = FileUtil.readStream(file.getContents());
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
			return;
		}
		
		String text = content.substring(location.getStartPosition(), location.getStartPosition()+location.getLength());
		int openBracket = text.indexOf("("); //$NON-NLS-1$
		int openQuote = text.indexOf("\""); //$NON-NLS-1$
		if(openBracket >= 0){
			int closeBracket = text.indexOf(")", openBracket); //$NON-NLS-1$
			
			int equals = text.indexOf("=", openBracket); //$NON-NLS-1$
			int value = text.indexOf("value", openBracket); //$NON-NLS-1$
			
			if(closeBracket == openBracket+1){ // empty brackets
				String newText = "\""+getNewName()+"\""; //$NON-NLS-1$ //$NON-NLS-2$
				change(file, location.getStartPosition()+openBracket+1, 0, newText);
			}else if(value > 0){ // construction value="name" found so change name
				String newText = text.replace(getOldName(), getNewName());
				change(file, location.getStartPosition(), location.getLength(), newText);
			}else if(equals > 0){ // other parameters are found
				String newText = "value=\""+getNewName()+"\","; //$NON-NLS-1$ //$NON-NLS-2$
				change(file, location.getStartPosition()+openBracket+1, 0, newText);
			}else{ // other cases
				String newText = text.replace(getOldName(), getNewName());
				change(file, location.getStartPosition(), location.getLength(), newText);
			}
		}else if(openQuote >= 0){
			int closeQuota = text.indexOf("\"", openQuote); //$NON-NLS-1$
			
			if(closeQuota == openQuote+1){ // empty quotas
				String newText = "\""+getNewName()+"\""; //$NON-NLS-1$ //$NON-NLS-2$
				change(file, location.getStartPosition()+openQuote+1, 0, newText);
			}else{ // the other cases
				String newText = text.replace(getOldName(), getNewName());
				change(file, location.getStartPosition(), location.getLength(), newText);
			}
		}else{
			String newText = "(\""+getNewName()+"\")"; //$NON-NLS-1$ //$NON-NLS-2$
			change(file, location.getStartPosition()+location.getLength(), 0, newText);
		}
	}

	
	private void changeDeclarations(ISeamComponent component) throws CoreException{
		if(component.getJavaDeclaration() != null)
			renameJavaDeclaration(component.getJavaDeclaration());
		

		Set<ISeamXmlComponentDeclaration> xmlDecls = component.getXmlDeclarations();

		for(ISeamXmlComponentDeclaration xmlDecl : xmlDecls){
			renameXMLDeclaration(xmlDecl);
		}
	}
	
	protected void checkDeclarations(ISeamComponent component) throws CoreException{
		if(component.getJavaDeclaration() != null){
			if(coreHelper.isJar(component.getJavaDeclaration()))
				status.addInfo(Messages.format(SeamCoreMessages.SEAM_RENAME_PROCESSOR_COMPONENT_HAS_DECLARATION_FROM_JAR, new String[]{component.getName(), component.getJavaDeclaration().getResource().getFullPath().toString()}));
		}

		Set<ISeamXmlComponentDeclaration> xmlDecls = component.getXmlDeclarations();

		for(ISeamXmlComponentDeclaration xmlDecl : xmlDecls){
			if(coreHelper.isJar(xmlDecl))
				status.addInfo(Messages.format(SeamCoreMessages.SEAM_RENAME_PROCESSOR_COMPONENT_HAS_DECLARATION_FROM_JAR, new String[]{component.getName(), xmlDecl.getResource().getFullPath().toString()}));
		}
	}
	
	protected boolean isFileCorrect(IFile file){
			if(!file.isSynchronized(IResource.DEPTH_ZERO)){
				status.addFatalError(Messages.format(SeamCoreMessages.SEAM_RENAME_PROCESSOR_OUT_OF_SYNC_FILE, file.getFullPath().toString()));
				return false;
			}else if(file.isPhantom()){
				status.addFatalError(Messages.format(SeamCoreMessages.SEAM_RENAME_PROCESSOR_ERROR_PHANTOM_FILE, file.getFullPath().toString()));
				return false;
			}else if(file.isReadOnly()){
				status.addFatalError(Messages.format(SeamCoreMessages.SEAM_RENAME_PROCESSOR_ERROR_READ_ONLY_FILE, file.getFullPath().toString()));
				return false;
			}
			return true;
	}
	
	private void renameJavaDeclaration(ISeamJavaComponentDeclaration javaDecl) throws CoreException{
		IFile file  = (IFile)javaDecl.getResource();
		
		if(file != null && !coreHelper.isJar(javaDecl)){
			ITextSourceReference location = ((SeamComponentDeclaration)javaDecl).getLocationFor(ISeamXmlComponentDeclaration.NAME);
			if(location != null && !isBadLocation(location, file))
				change(file, location.getStartPosition(), location.getLength(), "\""+getNewName()+"\""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		declarationFile = file;
	}
	
	private void renameXMLDeclaration(ISeamXmlComponentDeclaration xmlDecl) throws CoreException{
		IFile file = (IFile)xmlDecl.getResource();
		
		if(file != null && !coreHelper.isJar(xmlDecl)){
			ITextSourceReference location = ((SeamComponentDeclaration)xmlDecl).getLocationFor(ISeamXmlComponentDeclaration.NAME);
			if(location != null && !isBadLocation(location, file))
				changeXMLNode(location, file);
		}
		if(declarationFile == null)
			declarationFile = file;
	}

	
	// we need to find references in .java .xml .xhtml .jsp .properties files
	protected void findELReferences(){
		if(declarationFile == null)
			return;
		
		IProject[] projects = projectsSet.getAllProjects();
		for (IProject project : projects) {
			if(project == null) continue;
			
			IJavaProject javaProject = EclipseResourceUtil.getJavaProject(project);
			
			// searching java, xml and property files in source folders
			if(javaProject != null){
				for(IResource resource : EclipseResourceUtil.getJavaSourceRoots(project)){
					if(resource instanceof IFolder)
						scanForJava((IFolder) resource);
					else if(resource instanceof IFile)
						scanForJava((IFile) resource);
				}
			}
			
			// searching jsp, xhtml and xml files in WebContent folders
			if(project.equals(projectsSet.getWarProject()))
				scan(projectsSet.getDefaultViewsFolder());
			else if(project.equals(projectsSet.getEarProject()))
				scan(projectsSet.getDefaultEarViewsFolder());
			else{
				scan(project);
			}
		}
	}
	
	private void scan(IProject project){
		IJavaProject javaProject = EclipseResourceUtil.getJavaProject(project);
		if(javaProject == null)
			return;
		
		IResource[] sources = EclipseResourceUtil.getJavaSourceRoots(project);
		IPath output = null;
		try{
			output = javaProject.getOutputLocation();
		}catch(JavaModelException ex){
			SeamCorePlugin.getDefault().logError(ex);
		}
		
		try{
			for(IResource resource : project.members()){
				if(resource instanceof IFolder){
					if(checkFolder(resource, sources, output))
						scan((IFolder) resource);
				}else if(resource instanceof IFile)
					scan((IFile) resource);
			}
		}catch(CoreException ex){
			SeamCorePlugin.getDefault().logError(ex);
		}
	}
	
	private boolean checkFolder(IResource resource, IResource[] sources, IPath output){
		for(IResource folder : sources){
			if(resource.equals(folder))
				return false;
		}
		
		if(resource.getFullPath().equals(output))
			return false;
		
		return true;
	}
	
	private void scanForJava(IContainer container){
		try{
			for(IResource resource : container.members()){
				if(resource instanceof IFolder)
					scanForJava((IFolder) resource);
				else if(resource instanceof IFile)
					scanForJava((IFile) resource);
			}
		}catch(CoreException ex){
			SeamCorePlugin.getDefault().logError(ex);
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
	
	private void scanForJava(IFile file){
		String ext = file.getFileExtension();
		
		if(!isFileCorrect(file))
			return;
		
		String content = null;
		try {
			content = FileUtil.readStream(file.getContents());
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
			return;
		}
		if(JAVA_EXT.equalsIgnoreCase(ext)){
			scanJava(file, content);
		}else if(XML_EXT.equalsIgnoreCase(ext))
			scanDOM(file, content);
		else if(PROPERTIES_EXT.equalsIgnoreCase(ext))
			scanProperties(file, content);
	}

	private void scan(IFile file){
		String ext = file.getFileExtension();
		
		if(!isFileCorrect(file))
			return;
		
		String content = null;
		try {
			content = FileUtil.readStream(file.getContents());
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
			return;
		}
		if(XML_EXT.equalsIgnoreCase(ext) || XHTML_EXT.equalsIgnoreCase(ext) || JSP_EXT.equalsIgnoreCase(ext))
			scanDOM(file, content);
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

	// looking for component references in EL
	private void scanString(IFile file, String string, int offset) {
		int startEl = string.indexOf("#{"); //$NON-NLS-1$
		if(startEl>-1) {
			ELParser parser = ELParserUtil.getJbossFactory().createParser();
			ELModel model = parser.parse(string);
			for (ELInstance instance : model.getInstances()) {
				for(ELInvocationExpression ie : instance.getExpression().getInvocations()){
					ELPropertyInvocation pi = findComponentReference(ie);
					if(pi != null)
						change(file, offset+pi.getStartPosition(), pi.getName().getStart()+pi.getName().getLength()-pi.getStartPosition(), newName);
				}
			}
		}
	}
	
	private ELPropertyInvocation findComponentReference(ELInvocationExpression invocationExpression){
		ELInvocationExpression invExp = invocationExpression;
		while(invExp != null){
			if(invExp instanceof ELPropertyInvocation){
				if(((ELPropertyInvocation)invExp).getQualifiedName() != null && ((ELPropertyInvocation)invExp).getQualifiedName().equals(getOldName()))
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
				
				if(key && token.startsWith(getOldName())){
					String changeText = token.replaceFirst(getOldName(), getNewName());
					change(file, offset, token.length(), changeText);
				}
			}
			
			lastToken = token;
			offset += token.length();
		}
	}
	
	protected void renameComponent(IProgressMonitor pm, ISeamComponent component)throws CoreException{
		pm.beginTask("", 3);
		
		clearChanges();
		
		findDeclarations(component);
		
		pm.worked(1);
		
		findAnnotations();
		
		pm.worked(1);
		
		findELReferences();
		
		pm.done();
	}
	
	protected void renameSeamContextVariable(IProgressMonitor pm, IFile sourceFile)throws CoreException{
		pm.beginTask("", 2);
		
		clearChanges();
		
		declarationFile = sourceFile;
		
		findOutDataModelFactory();
		
		pm.worked(1);
		
		findELReferences();
		
		pm.done();
	}
	
	protected void findOutDataModelFactory(){
		if(declarationFile == null)
			return;
		
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(declarationFile.getProject(), true);
		
		if(seamProject == null)
			return;
		
		IProject[] projects = projectsSet.getAllProjects();
		for (IProject project : projects) {
			ISeamProject sProject = SeamCorePlugin.getSeamProject(project, true);
			if(sProject != null){
				findAnnotations(sProject, BijectedAttributeType.OUT, SeamAnnotations.OUT_ANNOTATION_TYPE);
				findAnnotations(sProject, BijectedAttributeType.DATA_BINDER, SeamAnnotations.DATA_MODEL_ANNOTATION_TYPE);
				findFactories(sProject);
			}
		}
	}
	
	ArrayList<String> keys = new ArrayList<String>();
	
	private void clearChanges(){
		keys.clear();
	}
	
	private void change(IFile file, int offset, int length, String text){
		//System.out.println("change file - "+file.getFullPath()+" offset - "+offset+" len - "+length+" text"+text);
		String key = file.getFullPath().toString()+" "+offset;
		if(!keys.contains(key)){
			TextFileChange change = getChange(file);
			TextEdit edit = new ReplaceEdit(offset, length, text);
			change.addEdit(edit);
			keys.add(key);
		}
	}
}
