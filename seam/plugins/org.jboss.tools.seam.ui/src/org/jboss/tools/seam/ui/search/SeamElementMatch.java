package org.jboss.tools.seam.ui.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.search.ui.text.Match;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamDeclaration;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamJavaSourceReference;
import org.jboss.tools.seam.internal.core.el.ElVarSearcher.Var;

public class SeamElementMatch extends Match {
	private long fCreationTimeStamp;
	
	public SeamElementMatch(ISeamElement element) {
		super(element, 0, 0);
		fCreationTimeStamp= element.getResource().getModificationStamp();
	}

	public SeamElementMatch(ISeamJavaSourceReference element) {
		super(element, 0, 0);
		fCreationTimeStamp= element.getSourceMember().getResource().getModificationStamp();
	}

	public SeamElementMatch(ISeamDeclaration element) {
		super(element, 0, 0);
		fCreationTimeStamp= element.getResource().getModificationStamp();
	}

	public SeamElementMatch(IFile element, int offset, int length) {
		super(element, offset, length);
		fCreationTimeStamp= ((IFile)element).getModificationStamp();
	}
	
	public IFile getFile() {
		if (getElement() instanceof ISeamJavaSourceReference) {
			return (IFile) ((ISeamJavaSourceReference)getElement()).getSourceMember().getResource(); 
		} else if (getElement() instanceof ISeamDeclaration) {
			return (IFile) ((ISeamDeclaration)getElement()).getResource(); 
		} else if (getElement() instanceof IFile) {
			return (IFile)getElement();
		}
		return (IFile) ((ISeamElement)getElement()).getResource();
	}

	public long getCreationTimeStamp() {
		return fCreationTimeStamp;
	}
}
