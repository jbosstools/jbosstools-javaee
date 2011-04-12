package org.jboss.tools.cdi.seam.solder.core;

import org.jboss.tools.cdi.core.CDIConstants;

public interface CDISeamSolderConstants extends CDIConstants {
	public String EXACT_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.core.Exact";
	public String FULLY_QUALIFIED_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.core.FullyQualified";
	public String REQUIRES_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.core.Requires";
	public String VETO_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.core.Veto";

	public String MESSAGE_LOGGER_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.logging.MessageLogger";
	public String MESSAGE_BUNDLE_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.messages.MessageBundle";

	public String SERVICE_HANDLER_TYPE_ANNOTATION_TYPE_NAME = "org.jboss.seam.solder.serviceHandler.ServiceHandlerType";
	public String SERVICE_ANNOTATION_KIND = "serviceAnnotation";

}
