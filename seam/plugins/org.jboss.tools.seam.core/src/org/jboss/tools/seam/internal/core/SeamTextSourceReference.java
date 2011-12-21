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
package org.jboss.tools.seam.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.jboss.tools.common.java.IJavaSourceReference;

/**
 * @author Alexey Kazakov
 */
public class SeamTextSourceReference implements IJavaSourceReference {
	private IResource resource;
	private int length;
	private int startPosition;
	private IMember member;

	public SeamTextSourceReference(IMember member, int length, int startPosition, IResource resource) {
		this.length = length;
		this.startPosition = startPosition;
		this.resource = resource;
		this.member = member;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamTextSourceReference#getLength()
	 */
	public int getLength() {
		return length;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamTextSourceReference#getStartPosition()
	 */
	public int getStartPosition() {
		return startPosition;
	}

	/**
	 * @param length
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @param startPosition
	 */
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public IResource getResource() {
		return resource;
	}

	public IMember getSourceMember() {
		return member;
	}

	public IJavaElement getSourceElement() {
		return member;
	}
}