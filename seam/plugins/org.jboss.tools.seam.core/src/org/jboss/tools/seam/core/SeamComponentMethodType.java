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
package org.jboss.tools.seam.core;

import org.jboss.tools.seam.internal.core.scanner.java.SeamAnnotations;

/**
 * @author Alexey Kazakov
 */
public enum SeamComponentMethodType implements SeamAnnotations {
	CREATE(CREATE_ANNOTATION_TYPE),
	DESTROY(DESTROY_ANNOTATION_TYPE),
	REMOVE(REMOVE_ANNOTATION_TYPE),
	UNWRAP(UNWRAP_ANNOTATION_TYPE),
	OBSERVER(OBSERVER_ANNOTATION_TYPE);

	String annotationType;

	SeamComponentMethodType(String annotationType) {
		this.annotationType = annotationType;
	}

	public String getAnnotationType() {
		return annotationType;
	}
}