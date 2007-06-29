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
package org.jboss.tools.seam.ui.widget.editor;

import java.beans.PropertyChangeListener;

public interface IFieldEditor extends INamedElement {
	
	/**
	 * 
	 * @param composite
	 */
	public Object[] getEditorControls(Object composite);

	/**
	 * 
	 * @return
	 */
	public Object[] getEditorControls();

	/**
	 * 
	 * @return
	 */
	public int getNumberOfControls();

	/**
	 * 
	 * @param parent
	 */
	public void doFillIntoGrid(Object parent);

	/**
	 * 
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * 
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * 
	 * @return
	 */
	public boolean isEditable();

	/**
	 * 
	 * @param aEdiatble
	 */
	public void setEditable(boolean aEdiatble);

	/**
	 * @return
	 * 
	 */
	public boolean setFocus();

	/**
	 * 
	 * @return
	 */
	public boolean isEnabled();

	/**
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled);
	
	/**
	 * 
	 */
	public void dispose();
	

}
