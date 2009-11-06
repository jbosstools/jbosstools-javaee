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

import org.jboss.tools.common.text.ITextSourceReference;

/**
 * Represents declaration of seam component.
 * @author Alexey Kazakov
 */
public interface ISeamComponentDeclaration extends ISeamDeclaration, ITextSourceReference {

	public ISeamComponentDeclaration clone() throws CloneNotSupportedException;

}