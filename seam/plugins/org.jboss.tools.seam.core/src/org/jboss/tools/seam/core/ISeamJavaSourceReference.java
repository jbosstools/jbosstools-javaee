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

import org.eclipse.jdt.core.IMember;

/**
 * An interface of seam tools model object that has associated source object in JDT model
 * @author Alexey Kazakov
 */
public interface ISeamJavaSourceReference extends ISeamTextSourceReference {

	public IMember getSourceMember();
}