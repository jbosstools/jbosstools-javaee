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
package org.jboss.tools.cdi.internal.core.event;

/**
 * CDI Project Listener
 * 
 * @author Victor V. Rubezhny
 */
public interface ICDIProjectChangeListener {
	/**
	 * Called when CDI project is changed.
	 * @param event
	 */
	public void projectChanged(CDIProjectChangeEvent event);

}
