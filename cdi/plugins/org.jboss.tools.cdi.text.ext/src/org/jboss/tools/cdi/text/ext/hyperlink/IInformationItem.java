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
package org.jboss.tools.cdi.text.ext.hyperlink;

import org.eclipse.swt.graphics.Image;

public interface IInformationItem {
	
	/**
	 * Returns the simple name of item
	 * 
	 * @return
	 */
	public String getInformation();
	
	/**
	 * Returns the java element fully qualified name
	 * 
	 * @return
	 */
	public String getFullyQualifiedName();
	
	/**
	 * Returns the icon image
	 * 
	 * @return
	 */
	public Image getImage();
}
