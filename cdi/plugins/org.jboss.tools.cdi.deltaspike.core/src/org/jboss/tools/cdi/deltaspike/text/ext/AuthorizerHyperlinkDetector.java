/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.deltaspike.text.ext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.text.JavaWordFinder;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.deltaspike.core.DeltaspikeAuthorityMethod;
import org.jboss.tools.cdi.deltaspike.core.DeltaspikeCorePlugin;
import org.jboss.tools.cdi.deltaspike.core.DeltaspikeSecurityBindingConfiguration;
import org.jboss.tools.cdi.deltaspike.core.DeltaspikeSecurityDefinitionContext;
import org.jboss.tools.cdi.deltaspike.core.DeltaspikeSecurityExtension;
import org.jboss.tools.cdi.deltaspike.core.SecurityBindingDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.common.java.IAnnotationDeclaration;

public class AuthorizerHyperlinkDetector extends AbstractHyperlinkDetector{
	protected IRegion region;
	protected IDocument document;
	protected ITextViewer viewer;

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		this.region = region;
		this.viewer = textViewer;
		
		ITextEditor textEditor= (ITextEditor)getAdapter(ITextEditor.class);
		if (region == null || !canShowMultipleHyperlinks || !(textEditor instanceof JavaEditor))
			return null;
		
		int offset= region.getOffset();
		
		ITypeRoot input = EditorUtility.getEditorInputJavaElement(textEditor, false);
		if (input == null)
			return null;

		document= textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		IRegion wordRegion= JavaWordFinder.findWord(document, offset);
		if (wordRegion == null)
			return null;
		
		IProject project = null;
		
		project = input.getJavaProject().getProject();
		
		if(project == null)
			return null;
		
		CDICoreNature cdiNature = CDIUtil.getCDINatureWithProgress(project);
		if(cdiNature == null)
			return null;
		
		IJavaElement[] elements = null;
		IType annotationType = null;
		
		try {
			elements = input.codeSelect(wordRegion.getOffset(), wordRegion.getLength());
			if (elements == null) 
				return null;
			if(elements.length != 1)
				return null;
			
			ArrayList<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
			int position = 0;
			if(elements[0] instanceof IType){
				annotationType = (IType)elements[0];
				if(!annotationType.isAnnotation()) {
					annotationType = null;
				}
				elements[0] = input.getElementAt(wordRegion.getOffset());
				if(elements[0] == null)
					return null;
				
				if(elements[0] instanceof IMethod){
					position = offset;
				}
			}

			findAuthorizerMethods(cdiNature, elements[0], annotationType, position, input.getPath(), hyperlinks);
			
			if (hyperlinks != null && !hyperlinks.isEmpty()) {
				return (IHyperlink[])hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
			}
		} catch (JavaModelException jme) {
			DeltaspikeCorePlugin.getDefault().logError(jme);
		}
		return null;
	}
	
	protected void findAuthorizerMethods(CDICoreNature nature, IJavaElement element, IType annotationType, int offset, IPath path, ArrayList<IHyperlink> hyperlinks){
		ICDIProject cdiProject = nature.getDelegate();
		
		if(cdiProject == null) {
			return;
		}
		
		DeltaspikeSecurityExtension extension = DeltaspikeSecurityExtension.getExtension(nature);
		if(extension == null) return;

		DeltaspikeSecurityDefinitionContext context = (DeltaspikeSecurityDefinitionContext)extension.getContext();
		
		Map<String, DeltaspikeSecurityBindingConfiguration> cs = context.getConfigurations();

		for (DeltaspikeSecurityBindingConfiguration c: cs.values()) {
			Map<AbstractMemberDefinition, SecurityBindingDeclaration> ms = c.getBoundMembers();
			Set<DeltaspikeAuthorityMethod> as = c.getAuthorizerMembers();
			Set<String> authorityMethods = new HashSet<String>();
			for (AbstractMemberDefinition m: ms.keySet()) {
				if(element.equals(m.getMember())) {
					for (DeltaspikeAuthorityMethod a: as) {
						try {
							IAnnotationDeclaration b = ms.get(m).getBinding();
							IAnnotationDeclaration d = ms.get(m).getDeclaration();
							if(annotationType != null && !annotationType.getFullyQualifiedName().equals(d.getTypeName())) {
								continue;
							}
							if(a.isMatching(b)) {
								IMethod method = a.getMethod().getMethod();
								if(authorityMethods.contains(method.getSignature())) {
									continue;
								}
								authorityMethods.add(method.getSignature());
								JavaElementHyperlink h = new JavaElementHyperlink(region, method, document);
								hyperlinks.add(h);
							}
						} catch (CoreException e) {
							DeltaspikeCorePlugin.getDefault().logError(e);
						}
					}
				}
			}
		}
	
	}

}
