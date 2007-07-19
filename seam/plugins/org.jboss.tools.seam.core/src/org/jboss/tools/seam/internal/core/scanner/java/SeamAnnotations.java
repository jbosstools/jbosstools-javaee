 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.scanner.java;

/**
 * Java annotations processed in building seam model
 * 
 * @author Viacheslav Kabanovich
 */
public interface SeamAnnotations {

	public static String SEAM_ANNOTATION_TYPE_PREFIX = "org.jboss.seam.annotations.";
	public static String NAME_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Name";
	public static String SCOPE_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Scope";
	public static String INSTALL_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Install";
	
	public static String IN_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "In";
	public static String OUT_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Out";
	public static String DATA_MODEL_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "datamodel.DataModel";
	public static String DATA_MODEL_SELECTION_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "datamodel.DataModelSelection";
	public static String DATA_MODEL_SELECTION_INDEX_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "datamodel.DataModelSelectionIndex";
	
	public static String CREATE_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Create";
	public static String DESTROY_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Destroy";
	public static String UNWRAP_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Unwrap";
	public static String OBSERVER_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Observer";
	public static String REMOVE_ANNOTATION_TYPE = "javax.ejb.Remove";

	public static String FACTORY_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Factory";
	
	public static String ROLES_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Roles";
	public static String ROLE_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "Role";
	
	public static String ENTITY_ANNOTATION_TYPE = "javax.persistence.Entity";
	public static String STATEFUL_ANNOTATION_TYPE = "javax.ejb.Stateful";
	public static String STATELESS_ANNOTATION_TYPE = "javax.ejb.Stateless";
	public static String MESSAGE_DRIVEN_ANNOTATION_TYPE = "javax.ejb.MessageDriven";

}
