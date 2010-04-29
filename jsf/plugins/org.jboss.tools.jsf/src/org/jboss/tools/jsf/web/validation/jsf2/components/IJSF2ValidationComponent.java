/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.web.validation.jsf2.components;

/**
 * 
 * @author yzhishko
 * 
 */

public interface IJSF2ValidationComponent {

	String JSF2_TYPE_KEY = "JSF2_TYPE_KEY"; //$NON-NLS-1$

	String JSF2_COMPONENT_NAME_KEY = "AJSF2_COMPONENT_NAME_KEY"; //$NON-NLS-1$

	String JSF2_ATTR_NAME_KEY = "ATTR_NAME_KEY"; //$NON-NLS-1$

	String JSF2_URI_NAME_KEY = "JSF2_URI_NAME_KEY"; //$NON-NLS-1$

	String JSF2_URI_TYPE_KEY = "JSF2_URI_TYPE_KEY"; //$NON-NLS-1$

	String JSF2_COMPOSITE_COMPONENT_TYPE = "JSF2_COMPOSITE_COMPONENT_TYPE"; //$NON-NLS-1$

	String JSF2_UNFIXABLE_ATTR_TYPE = "JSF2_UNFIXABLE_ATTR_TYPE"; //$NON-NLS-1$

	String JSF2_FIXABLE_ATTR_TYPE = "JSF2_FIXABLE_ATTR_TYPE"; //$NON-NLS-1$

	String JSF2_URI_TYPE = "JSF2_URI_TYPE"; //$NON-NLS-1$

	int getLine();

	int getStartOffSet();

	int getLength();

	String getValidationMessage();

	Object[] getMessageParams();

	String getType();

	String getComponentResourceLocation();

	int getSeverity();

}
