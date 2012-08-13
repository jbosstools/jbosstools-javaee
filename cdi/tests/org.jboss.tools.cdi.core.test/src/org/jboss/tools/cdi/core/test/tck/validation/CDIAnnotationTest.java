/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.tck.validation;

import org.jboss.tools.cdi.internal.core.validation.CDICoreValidator;
import org.jboss.tools.common.base.test.validation.AbstractAnnotationTest;

/**
 * @author Alexey Kazakov
 */
public class CDIAnnotationTest extends AbstractAnnotationTest {

	@Override
	protected String getMarkerType() {
		return CDICoreValidator.PROBLEM_TYPE;
	}
}