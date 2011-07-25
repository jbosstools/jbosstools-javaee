/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.core.international;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IProperty {

	/**
	 * 
	 * @return name of the property
	 */
	public String getName();

	/**
	 * 
	 * @return name of the bundle that declares the property
	 */
	public IBundle getBundle();

	/**
	 * 
	 * @return value of the property for default locale, or if it is not available, than any available value
	 */
	public ILocalizedValue getValue();

	/**
	 * 
	 * @param locale
	 * @return value for the selected locale
	 */
	public ILocalizedValue getValue(String locale);

}
