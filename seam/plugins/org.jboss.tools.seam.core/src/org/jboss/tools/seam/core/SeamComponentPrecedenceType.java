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

/**
 * List of component precedence types
 * @author Alexey Kazakov
 */
public interface SeamComponentPrecedenceType {
	int BUILT_IN = 0;
	int FRAMEWORK = 10;
	int APPLICATION = 20;
	int DEPLOYMENT = 30;
	int MOCK = 40;
	
	int DEFAULT = APPLICATION;
}