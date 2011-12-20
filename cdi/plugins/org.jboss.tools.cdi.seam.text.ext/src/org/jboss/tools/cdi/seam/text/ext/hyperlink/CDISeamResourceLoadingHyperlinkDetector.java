/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.seam.text.ext.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jboss.tools.cdi.internal.core.refactoring.CDIMarkerResolutionUtils;
import org.jboss.tools.cdi.seam.text.ext.CDISeamExtPlugin;
import org.jboss.tools.common.EclipseUtil;

public class CDISeamResourceLoadingHyperlinkDetector extends AbstractHyperlinkDetector{
	public static final String RESOURCE_ANNOTATION = "org.jboss.seam.solder.resourceLoader.Resource";
	public static final String VALUE = "value";

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		ITextEditor textEditor= (ITextEditor)getAdapter(ITextEditor.class);
		if (region == null || !(textEditor instanceof JavaEditor))
			return null;
		
		int offset = region.getOffset();
		
		ITypeRoot input = EditorUtility.getEditorInputJavaElement(textEditor, false);
		if (input == null)
			return null;

		IDocument document= textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());

		IFile file = null;
		
		try {
			IResource resource = input.getCorrespondingResource();
			if (resource instanceof IFile)
				file = (IFile) resource;
		} catch (JavaModelException e) {
			CDISeamExtPlugin.log(e);
		}
		
		if(file == null)
			return null;
		
		try {
			
			ICompilationUnit cu = EclipseUtil.getCompilationUnit(file);
			
			IJavaElement element = cu.getElementAt(offset);
			if(element != null){
				if(element instanceof IField){
					IAnnotation annotation = CDIMarkerResolutionUtils.findAnnotation(element, RESOURCE_ANNOTATION);
					if(annotation != null && annotation.exists()){
						IRegion annotationRegion = getAnnotationRegion(document, annotation);
						String text = getValue(annotation);
						if(text != null){
							return new IHyperlink[]{new CDISeamResourceLoadingHyperlink(file, document, annotationRegion, text)};
						}
					}
				}
			}
		}catch(CoreException ex){
			CDISeamExtPlugin.log(ex);
		}catch(BadLocationException ex){
			CDISeamExtPlugin.log(ex);
		}
	
		return null;
	}
	
	private IRegion getAnnotationRegion(IDocument document, IAnnotation annotation) throws BadLocationException, JavaModelException{
		ISourceRange sourceRange = annotation.getSourceRange();
		String annotationText = document.get(sourceRange.getOffset(), sourceRange.getLength());
		int offset = sourceRange.getOffset();
		int length = sourceRange.getLength();
		int first = annotationText.indexOf("\"");
		if(first >= 0){
			int last = annotationText.lastIndexOf("\"");
			if(last > 0 && last != first){
				offset += first + 1;
				length = last - first - 1;
			}
		}
		
		IRegion region = new Region(offset, length);
		return region;
	}
	
	private String getValue(IAnnotation annotation) throws JavaModelException{
		IMemberValuePair[] pairs = annotation.getMemberValuePairs();
		for(IMemberValuePair pair : pairs){
			if(pair.getMemberName().equals(VALUE) && pair.getValueKind() == IMemberValuePair.K_STRING){
				return (String)pair.getValue();
			}
		}
		return null;
	}

}
