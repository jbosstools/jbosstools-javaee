/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.jsf2.refactoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.internal.corext.refactoring.changes.TextChangeCompatibility;
import org.eclipse.ltk.core.refactoring.GroupCategory;
import org.eclipse.ltk.core.refactoring.GroupCategorySet;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.jboss.tools.jsf.jsf2.model.JSF2ComponentModelManager;
import org.jboss.tools.jsf.jsf2.refactoring.core.StructuredChanges;
import org.jboss.tools.jsf.jsf2.refactoring.core.StructuredTextFileChange;
import org.jboss.tools.jsf.jsf2.util.JSF2ComponentUtil;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.w3c.dom.Element;

/**
 * 
 * @author yzhishko
 *
 */

@SuppressWarnings("restriction")
public class RefactoringChangesFactory {

	private static final GroupCategorySet CATEGORY_COMPOSITE_URI_RENAME = new GroupCategorySet(
			new GroupCategory(
					"org.jboss.tools.jsf.jsf2.refactoring.rename.composite.uri.type", JSFUIMessages.Refactoring_JSF_2_Rename_Composite_URI_Changes, JSFUIMessages.Refactoring_JSF_2_Changes_Rename_Composite_URI)); //$NON-NLS-1$ 

	private static final GroupCategorySet CATEGORY_COMPOSITE_COMPONENT_RENAME = new GroupCategorySet(
			new GroupCategory(
					"org.jboss.tools.jsf.jsf2.refactoring.rename.composite.uri.type", JSFUIMessages.Refactoring_JSF_2_Rename_Composite_Component_Changes, JSFUIMessages.Refactoring_JSF_2_Changes_Rename_Composite_Component)); //$NON-NLS-1$ 

	public static StructuredChanges createRenameURIChanges(IResource resource,
			Map<String, String> urisMap) throws CoreException {
		StructuredChanges changes = new StructuredChanges(
				JSFUIMessages.Refactoring_JSF_2_Rename_Composite_URI_Changes);
		createRenameURIChangesRecursively(resource, urisMap, changes);
		if (changes.getChildren() == null || changes.getChildren().length == 0) {
			return null;
		}
		return changes;
	}

	public static StructuredChanges createRenameCompositeComponentsChanges(
			IResource resource, String uri, String oldFileName,
			String newFileName) throws CoreException {
		StructuredChanges changes = new StructuredChanges(
				JSFUIMessages.Refactoring_JSF_2_Rename_Composite_Component_Changes);
		createRenameComponentChangesRecursively(resource, uri, oldFileName,
				newFileName, changes);
		if (changes.getChildren() == null || changes.getChildren().length == 0) {
			return null;
		}
		return changes;
	}

	private static void createRenameComponentChangesRecursively(
			IResource resource, String uri, String oldFileName,
			String newFileName, StructuredChanges changes) throws CoreException {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			StructuredTextFileChange change = createRanameComponentChange(file,
					uri, oldFileName, newFileName);
			if (change != null) {
				changes.add(change);
			}
		} else if (resource instanceof IProject) {
			IResource[] children = ((IProject) resource).members();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					createRenameComponentChangesRecursively(children[i], uri,
							oldFileName, newFileName, changes);
				}
			}
		} else if (resource instanceof IFolder) {
			IResource[] children = ((IFolder) resource).members();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					createRenameComponentChangesRecursively(children[i], uri,
							oldFileName, newFileName, changes);
				}
			}
		}
	}

	private static StructuredTextFileChange createRanameComponentChange(
			IFile file, String uri, String oldName, String newName) {
		if (!isFileCorrect(file)) {
			return null;
		}
		StructuredTextFileChange fileChange = null;
		Map<String, List<Element>> compositeComponentsMap = JSF2ComponentUtil
				.findCompositeComponents(JSF2ComponentModelManager
						.getReadableDOMDocument(file));
		List<Element> compositeComponents = compositeComponentsMap.get(uri);
		if (compositeComponents != null) {
			for (Element element : compositeComponents) {
				if (oldName.equals(element.getLocalName())) {
					if (element instanceof IDOMElement) {
						IDOMElement domElement = (IDOMElement) element;
						if (fileChange == null) {
							fileChange = new StructuredTextFileChange(file
									.getFullPath().toOSString(), file);
						}
						ReplaceEdit[] edits = createReplaceEditsForElement(domElement, oldName, newName);
						for (int i = 0; i < edits.length; i++) {
							TextChangeCompatibility.addTextEdit(fileChange,
									JSFUIMessages.Refactoring_JSF_2_Rename_Composite_Component,
									edits[i], CATEGORY_COMPOSITE_COMPONENT_RENAME);
						}
					}
				}
			}
		}
		return fileChange;
	}
	
	private static ReplaceEdit[] createReplaceEditsForElement(IDOMElement element, String oldName, String newName){
		List<ReplaceEdit> edits = new ArrayList<ReplaceEdit>();
		String sourceString = element.getSource();
		int startOffset = element.getStartOffset();
		int endStartOffset = element.getEndStartOffset();
		int endOffset = element.getEndOffset();
		if (endOffset != endStartOffset) {
			ReplaceEdit edit = new ReplaceEdit(sourceString.lastIndexOf(oldName) + startOffset, oldName.length(), newName);
			edits.add(edit);
		}
		ReplaceEdit edit = new ReplaceEdit(startOffset + sourceString.indexOf(oldName), oldName.length(), newName);
		edits.add(edit);
		return edits.toArray(new ReplaceEdit[0]);
	}

	private static void createRenameURIChangesRecursively(IResource resource,
			Map<String, String> urisMap, StructuredChanges changes)
			throws CoreException {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			StructuredTextFileChange change = createRanameURIChange(file,
					urisMap);
			if (change != null) {
				changes.add(change);
			}
		} else if (resource instanceof IProject) {
			IResource[] children = ((IProject) resource).members();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					createRenameURIChangesRecursively(children[i], urisMap,
							changes);
				}
			}
		} else if (resource instanceof IFolder) {
			IResource[] children = ((IFolder) resource).members();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					createRenameURIChangesRecursively(children[i], urisMap,
							changes);
				}
			}
		}
	}

	private static StructuredTextFileChange createRanameURIChange(IFile file,
			Map<String, String> urisMap) {
		if (!isFileCorrect(file)) {
			return null;
		}
		StructuredTextFileChange fileChange = null;
		IDOMAttr[] uriAttrs = JSF2ComponentUtil
				.findURIContainers(JSF2ComponentModelManager
						.getReadableDOMDocument(file));
		for (IDOMAttr uriAttr : uriAttrs) {
			String valueToBeReplaced = uriAttr.getValue();
			if (urisMap.containsKey(valueToBeReplaced)) {
				if (fileChange == null) {
					fileChange = new StructuredTextFileChange(file
							.getFullPath().toOSString(), file);
				}
				ReplaceEdit edit = new ReplaceEdit(uriAttr
						.getValueRegionStartOffset() + 1, valueToBeReplaced
						.length(), urisMap.get(valueToBeReplaced));
				TextChangeCompatibility.addTextEdit(fileChange,
						JSFUIMessages.Refactoring_JSF_2_Rename_Composite_URI,
						edit, CATEGORY_COMPOSITE_URI_RENAME);
			}
		}
		return fileChange;
	}

	private static boolean isFileCorrect(IFile file) {
		if (file == null) {
			return false;
		}
		if (!"xhtml".equals(file.getFileExtension()) && !"jsp".equals(file.getFileExtension())) { //$NON-NLS-1$ //$NON-NLS-2$
			IContentType contentType = IDE.getContentType(file);
			if (contentType == null) {
				return false;
			}
			String id = contentType.getId();
			if (!"org.eclipse.jst.jsp.core.jspsource".equals(id) && !"org.eclipse.wst.html.core.htmlsource".equals(id)) { //$NON-NLS-1$ //$NON-NLS-2$
				return false;
			}
		}
		return true;
	}

}
