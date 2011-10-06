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
package org.jboss.tools.cdi.seam.solder.core;

import org.jboss.tools.cdi.core.CDIConstants;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface CDISeamSolderConstants30 extends CDISeamSolderConstants {
	public String PACKAGE_NAME_30 = "org.jboss.seam.solder";

	public String EXACT_ANNOTATION_TYPE_NAME_30 = PACKAGE_NAME_30 + ".core.Exact";

	public String FULLY_QUALIFIED_ANNOTATION_TYPE_NAME_30 = PACKAGE_NAME_30 + ".core.FullyQualified";
	public String REQUIRES_ANNOTATION_TYPE_NAME_30 = PACKAGE_NAME_30 + ".core.Requires";
	public String VETO_ANNOTATION_TYPE_NAME_30 = PACKAGE_NAME_30 + ".core.Veto";

	public String MESSAGE_LOGGER_ANNOTATION_TYPE_NAME_30 = PACKAGE_NAME_30 + ".logging.MessageLogger";
	public String MESSAGE_BUNDLE_ANNOTATION_TYPE_NAME_30 = PACKAGE_NAME_30 + ".messages.MessageBundle";

	public String SERVICE_HANDLER_TYPE_ANNOTATION_TYPE_NAME_30 = PACKAGE_NAME_30 + ".serviceHandler.ServiceHandlerType";

	public String DEFAULT_BEAN_ANNOTATION_TYPE_NAME_30 = PACKAGE_NAME_30 + ".bean.defaultbean.DefaultBean";

	public String UNWRAPS_ANNOTATION_TYPE_NAME_30 = PACKAGE_NAME_30 + ".unwraps.Unwraps";

	public String GENERIC_TYPE_ANNOTATION_TYPE_NAME_30 = PACKAGE_NAME_30 + ".bean.generic.GenericType";
	public String GENERIC_QUALIFIER_TYPE_NAME_30 = PACKAGE_NAME_30 + ".bean.generic.Generic";
	public String GENERIC_CONFIGURATION_ANNOTATION_TYPE_NAME_30 = PACKAGE_NAME_30 + ".bean.generic.GenericConfiguration";
	public String APPLY_SCOPE_ANNOTATION_TYPE_NAME_30 = PACKAGE_NAME_30 + ".bean.generic.ApplyScope";
	public String INJECT_GENERIC_ANNOTATION_TYPE_NAME_30 = PACKAGE_NAME_30 + ".bean.generic.InjectGeneric";

}
