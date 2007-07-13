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
 * @author Viacheslav Kabanovich
 */
public interface IValueInfo {
	
	/**
	 * Returns string value
	 * @return
	 */
	public String getValue();
	
	/**
	 * Returns start position of value or, when value is implied,
	 * position of object that could contains that value.
	 * @return
	 */
	public int getStartPosition();
	
	/**
	 * Returns length of value or, when value is implied,
	 * that of object that could contains that value.
	 * @return
	 */
	public int getLength();
	
}
