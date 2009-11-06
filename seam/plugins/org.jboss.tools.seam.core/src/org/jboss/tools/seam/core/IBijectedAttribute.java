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

import org.jboss.tools.common.java.IJavaSourceReference;

/**
 * Base interface of bijected attribute of seam component.
 * @author Alexey Kazakov
 */
public interface IBijectedAttribute extends ISeamContextVariable, IJavaSourceReference {

	/**
	 * @return type of attribute
	 */
	public BijectedAttributeType[] getTypes();

	/**
	 * Checks if type is contained in list of types.
	 * @param type
	 * @return
	 */
	public boolean isOfType(BijectedAttributeType type);
	
	/**
	 * Though this interface extends ISeamContextVariable, not all
	 * types are allowed to be context variables
	 * @return
	 */
	public boolean isContextVariable();
	
	/**
	 * Returns value of annotation attribute 'value'.
	 * If value is not set but bijection type defaults value to field name, returns it.
	 * @return
	 */
	public String getValue();
	
	public IBijectedAttribute clone() throws CloneNotSupportedException;

}
