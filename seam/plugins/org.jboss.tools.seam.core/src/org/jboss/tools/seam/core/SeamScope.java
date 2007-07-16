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
package org.jboss.tools.seam.core;

/**
 * @author Alexey Kazakov
 */
public interface SeamScope {

	public static final int UNDEFINED_PRIORITY_ORDER = -1;
	public static final int EVENT_PRIORITY_ORDER = 0;
	public static final int PAGE_PRIORITY_ORDER = 1;
	public static final int CONVERSATION_PRIORITY_ORDER = 2;
	public static final int SESSION_PRIORITY_ORDER = 3;
	public static final int BUSINESS_PROCESS_PRIORITY_ORDER = 4;
	public static final int APPLICATION_PRIORITY_ORDER = 5;
}