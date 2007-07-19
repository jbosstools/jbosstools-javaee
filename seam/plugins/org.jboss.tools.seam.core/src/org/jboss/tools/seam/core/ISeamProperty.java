/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.core;

import org.jboss.tools.seam.core.event.ISeamValue;

/**
 * A property of Seam Component defined in component.xml or seam.properties files
 */
public interface ISeamProperty extends ISeamElement, ISeamTextSourceReference {

	/**
	 * @return name of this property
	 */
	public String getName();

	/**
	 * @return value of this property
	 */
	public ISeamValue getValue();

	/**
	 * Sets value of this property
	 * @param value
	 */
	public void setValue(ISeamValue value);
}