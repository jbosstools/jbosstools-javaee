package org.jboss.tools.seam.internal.core.scanner.java;

public interface SeamAnnotations {

	public static String SEAM_ANNOTATION_TYPE_PREFIX = "org.jboss.seam.annotations.";
	public static String NAME_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Name";
	public static String SCOPE_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Scope";
	public static String INSTALL_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Install";
	
	public static String IN_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "In";
	public static String OUT_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Out";

	public static String CREATE_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Create";
	public static String DESTROY_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Destroy";

	public static String FACTORY_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Factory";
	
	
	public static String STATEFUL_TYPE = "javax.ejb.Stateful";

}
