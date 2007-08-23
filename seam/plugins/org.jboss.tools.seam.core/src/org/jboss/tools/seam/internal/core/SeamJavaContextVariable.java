/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

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
	
	public void setSourceMember(IMember javaSource) {
		this.javaSource = javaSource;
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
		return javaSource == null ? super.getResource() : javaSource.getTypeRoot().getResource();
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

	public List<Change> merge(SeamObject s) {
		List<Change> changes = super.merge(s);
		
		if(s instanceof SeamJavaContextVariable) {
			SeamJavaContextVariable sf = (SeamJavaContextVariable)s;
			javaSource = sf.javaSource;
			resource = sf.resource;
		}
		
		return changes;
	}

	public void copyTo(SeamObject copy) {
		super.copyTo(copy);
		if(copy instanceof SeamJavaContextVariable) {
			SeamJavaContextVariable sf = (SeamJavaContextVariable)copy;
			sf.javaSource = javaSource;
		}
	}

}
