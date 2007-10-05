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

import org.jboss.tools.seam.core.SeamCoreMessages;

/**
 * Java annotations processed in building seam model
 * 
 * @author Viacheslav Kabanovich
 */
public interface SeamAnnotations {

	public static String SEAM_ANNOTATION_TYPE_PREFIX = "org.jboss.seam.annotations."; //$NON-NLS-1$
	public static String NAME_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + SeamCoreMessages.getString("SEAM_ANNOTATIONS_NAME"); //$NON-NLS-1$
	public static String SCOPE_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + SeamCoreMessages.getString("SEAM_ANNOTATIONS_SCOPE"); //$NON-NLS-1$
	public static String INSTALL_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + SeamCoreMessages.getString("SEAM_ANNOTATIONS_INSTALL"); //$NON-NLS-1$
	
	public static String IN_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + SeamCoreMessages.getString("SEAM_ANNOTATIONS_IN"); //$NON-NLS-1$
	public static String OUT_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + SeamCoreMessages.getString("SEAM_ANNOTATIONS_OUT"); //$NON-NLS-1$
	public static String DATA_MODEL_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "datamodel.DataModel"; //$NON-NLS-1$
	public static String DATA_MODEL_SELECTION_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "datamodel.DataModelSelection"; //$NON-NLS-1$
	public static String DATA_MODEL_SELECTION_INDEX_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + "datamodel.DataModelSelectionIndex"; //$NON-NLS-1$
	
	public static String CREATE_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + SeamCoreMessages.getString("SEAM_ANNOTATIONS_CREATE"); //$NON-NLS-1$
	public static String DESTROY_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + SeamCoreMessages.getString("SEAM_ANNOTATIONS_DESTROY"); //$NON-NLS-1$
	public static String UNWRAP_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + SeamCoreMessages.getString("SEAM_ANNOTATIONS_UNWRAP"); //$NON-NLS-1$
	public static String OBSERVER_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + SeamCoreMessages.getString("SEAM_ANNOTATIONS_OBSERVER"); //$NON-NLS-1$
	public static String REMOVE_ANNOTATION_TYPE = "javax.ejb.Remove"; //$NON-NLS-1$

	public static String FACTORY_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + SeamCoreMessages.getString("SEAM_ANNOTATIONS_FACTORY"); //$NON-NLS-1$
	
	public static String ROLES_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + SeamCoreMessages.getString("SEAM_ANNOTATIONS_ROLES"); //$NON-NLS-1$
	public static String ROLE_ANNOTATION_TYPE = SEAM_ANNOTATION_TYPE_PREFIX + SeamCoreMessages.getString("SEAM_ANNOTATIONS_ROLE"); //$NON-NLS-1$
	
	public static String ENTITY_ANNOTATION_TYPE = "javax.persistence.Entity"; //$NON-NLS-1$
	public static String STATEFUL_ANNOTATION_TYPE = "javax.ejb.Stateful"; //$NON-NLS-1$
	public static String STATELESS_ANNOTATION_TYPE = "javax.ejb.Stateless"; //$NON-NLS-1$
	public static String MESSAGE_DRIVEN_ANNOTATION_TYPE = "javax.ejb.MessageDriven"; //$NON-NLS-1$

}
