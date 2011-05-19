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
public interface CDISeamSolderConstants extends CDIConstants {
	public String EXACT_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.core.Exact";
	public String FULLY_QUALIFIED_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.core.FullyQualified";
	public String REQUIRES_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.core.Requires";
	public String VETO_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.core.Veto";

	public String MESSAGE_LOGGER_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.logging.MessageLogger";
	public String MESSAGE_BUNDLE_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.messages.MessageBundle";

	public String SERVICE_HANDLER_TYPE_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.serviceHandler.ServiceHandlerType";
	public String SERVICE_ANNOTATION_KIND = "serviceAnnotation";

	public String DEFAULT_BEAN_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.bean.defaultbean.DefaultBean";

	public String UNWRAPS_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.unwraps.Unwraps";

	public String GENERIC_TYPE_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.bean.generic.GenericType";
	public String GENERIC_ANNOTATION_KIND = "genericAnnotation";
	public String GENERIC_QUALIFIER_TYPE_NAME = "org.jboss.seam.solder.bean.generic.Generic";
	public String GENERIC_CONFIGURATION_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.bean.generic.GenericConfiguration";

}
