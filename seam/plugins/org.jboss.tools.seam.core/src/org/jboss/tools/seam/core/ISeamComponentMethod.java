 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.core;

import java.util.Set;

import org.jboss.tools.common.java.IJavaSourceReference;

/**
 * Represents method of seam component.
 * This interface represents only methods with types enumerated in SeamComponentMethodType
 * @author Alexey Kazakov
 */
public interface ISeamComponentMethod extends IJavaSourceReference, ISeamElement {

	/**
	 * @return is types of the method
	 */
	public Set<SeamComponentMethodType> getTypes();
	
	/**
	 * Returns create or destroy depending on type
	 * @param type
	 * @return
	 */
	public boolean isOfType(SeamComponentMethodType type);
	
	public ISeamComponentMethod clone() throws CloneNotSupportedException;

}