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

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.search.ui.text.Match;
import org.jboss.tools.common.java.IJavaSourceReference;
import org.jboss.tools.seam.core.ISeamDeclaration;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.ui.SeamGuiPlugin;

/**
 * Seam Element match contains an information on the match found during the Seam Search Action
 *  
 * @author Jeremy
 */
public class SeamElementMatch extends Match {
	private long fCreationTimeStamp;
	
	/**
	 * Constructs SeamElementMatch using the {@link ISeamElement} object
	 * 
	 * @param element
	 */
	public SeamElementMatch(ISeamElement element) {
		super(element, 0, 0);
		fCreationTimeStamp= element.getResource().getModificationStamp();
	}

	/**
	 * Constructs SeamElementMatch using the {@link ISeamJavaSourceReference} object
	 * 
	 * @param element
	 */
	public SeamElementMatch(IJavaSourceReference element) {
		super(element, 0, 0);
		if (element != null && element.getSourceMember() != null && element.getSourceMember().getResource() != null) {
			fCreationTimeStamp= element.getSourceMember().getResource().getModificationStamp();
		} else
			try {
				if (element != null && element.getSourceMember().getUnderlyingResource() != null) {
					fCreationTimeStamp = element.getSourceMember().getUnderlyingResource().getModificationStamp();
				} else {
					fCreationTimeStamp = 0;
				}
			} catch (JavaModelException e) {
				fCreationTimeStamp = 0;
				SeamGuiPlugin.getDefault().logError(e);
			}
	}

	/**
	 * Constructs SeamElementMatch using the {@link ISeamDeclaration} object
	 * 
	 * @param element
	 */
	public SeamElementMatch(ISeamDeclaration element) {
		super(element, 0, 0);
		fCreationTimeStamp= element.getResource().getModificationStamp();
	}

	/**
	 * Constructs SeamElementMatch using the text file and text selection offset/length 
	 * 
	 * @param element
	 */
	public SeamElementMatch(IFile element, int offset, int length) {
		super(element, offset, length);
		fCreationTimeStamp= ((IFile)element).getModificationStamp();
	}
	
	/**
	 * Returns the IFile where the Match occurres
	 * @return
	 */
	public IFile getFile() {
		if (getElement() instanceof IJavaSourceReference) {
			return (IFile) ((IJavaSourceReference)getElement()).getSourceMember().getResource(); 
		} else if (getElement() instanceof ISeamDeclaration) {
			return (IFile) ((ISeamDeclaration)getElement()).getResource(); 
		} else if (getElement() instanceof IFile) {
			return (IFile)getElement();
		}
		return (IFile) ((ISeamElement)getElement()).getResource();
	}

	/**
	 * Returns the creation time stamp
	 * @return
	 */
	public long getCreationTimeStamp() {
		return fCreationTimeStamp;
	}
}
