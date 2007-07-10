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

/**
 * Represents method of seam component.
 * This interface represents only methods with types enumerated in SeamComponentMethodType
 * @author Alexey Kazakov
 */
public interface ISeamComponentMethod extends ISeamJavaSourceReference, ISeamObject {

	/**
	 * @return is @ Create method
	 */
	public boolean isCreate();
	
	/**
	 * @return is @ Destroy method
	 */
	public boolean isDestroy();
	
	/**
	 * Returns create or destroy depending on type
	 * @param type
	 * @return
	 */
	public boolean isOfType(SeamComponentMethodType type);
	
}