/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.domain.SeamWebWarTestProject.session;

import javax.ejb.Stateful;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;

/**
 * Test component for JBIDE-1696
 * @author Alexey Kazakov
 */
@Stateful
@Name("testComponentJBIDE1696") 
public class SubclassTestComponent extends SuperclassTestComponent {

	@Destroy public void foo1() {} 
}