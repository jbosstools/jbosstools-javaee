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
 * Represents Seam Context Variable.
 * @author Alexey Kazakov
 */
public interface ISeamContextVariable extends ISeamElement {

	/**
	 * @return name of context variable
	 */
	public String getName();

	/**
	 * Sets name
	 * @param name
	 */
	public void setName(String name);

	/**
	 * @return scope type
	 */
	public ScopeType getScope();

	/**
	 * Sets scope type
	 * @param type
	 */
	public void setScope(ScopeType type);
}