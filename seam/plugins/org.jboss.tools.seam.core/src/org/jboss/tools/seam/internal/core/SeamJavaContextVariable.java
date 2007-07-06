package org.jboss.tools.seam.internal.core;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.seam.core.ISeamJavaSourceReference;
import org.jboss.tools.seam.core.event.Change;

public class SeamJavaContextVariable extends AbstractContextVariable implements ISeamJavaSourceReference {
	protected IMember javaSource = null;

	public IMember getSourceMember() {
		return javaSource;
	}

	public int getLength() {
		if(javaSource == null) return 0;
		try {
			if(javaSource.getSourceRange() == null) return 0;
			return javaSource.getSourceRange().getLength();
		} catch (JavaModelException e) {
			//ignore
			return 0;
		}
	}

	public IResource getResource() {
		return javaSource == null ? null : javaSource.getTypeRoot().getResource();
	}

	public int getStartPosition() {
		if(javaSource == null) return 0;
		try {
			if(javaSource.getSourceRange() == null) return 0;
			return javaSource.getSourceRange().getOffset();
		} catch (JavaModelException e) {
			//ignore
			return 0;
		}
	}

	public List<Change> merge(AbstractContextVariable f) {
		List<Change> changes = super.merge(f);
		
		if(f instanceof SeamJavaContextVariable) {
			SeamJavaContextVariable sf = (SeamJavaContextVariable)f;
			javaSource = sf.javaSource;
			resource = sf.resource;
		}
		
		return changes;
	}

}
