/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.bot.test.cssdialog.jbide;
/**
 * Exception thrown from CssFileParser class
 * @author Vladimir Pakan
 */
public class CssFileParserException extends RuntimeException {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public CssFileParserException (String errorMessage){
    super(errorMessage);
  }
}
