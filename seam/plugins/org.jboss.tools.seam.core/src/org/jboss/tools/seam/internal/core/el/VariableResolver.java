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
package org.jboss.tools.seam.internal.core.el;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.jst.jsf.context.symbol.ISymbol;
import org.eclipse.jst.jsf.designtime.context.DTFacesContext;
import org.eclipse.jst.jsf.designtime.el.AbstractDTVariableResolver;

/**
 * A design time variable JSF resolver. JBoss Tools provides its own variable resolve mechanism.
 * So we need to "disable" JST JSF variable resolver to avoid wrong error messages.  
 * @author Alexey Kazakov
 */
public class VariableResolver extends AbstractDTVariableResolver {

	public final static String ID = "org.jboss.tools.seam.el.variableresolver";
	private final static ISymbol NULL_SYMBOL = new NullSymbol();
	private final static ISymbol[] NULL_SYMBOL_ARRAY = new ISymbol[0];

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jst.jsf.designtime.el.AbstractDTVariableResolver#resolveVariable(org.eclipse.jst.jsf.designtime.context.DTFacesContext, java.lang.String, org.eclipse.core.runtime.IAdaptable)
	 */
    public ISymbol resolveVariable(DTFacesContext context, String name, IAdaptable externalContextKey) {
    	return NULL_SYMBOL;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jst.jsf.designtime.el.AbstractDTVariableResolver#getAllVariables(org.eclipse.jst.jsf.designtime.context.DTFacesContext, org.eclipse.core.runtime.IAdaptable)
     */
    public ISymbol[] getAllVariables(DTFacesContext facesContext, IAdaptable externalContextKey) {
    	return NULL_SYMBOL_ARRAY;
    }

    private static class NullSymbol extends EObjectImpl implements ISymbol {
		/* (non-Javadoc)
		 * @see org.eclipse.jst.jsf.context.symbol.ISymbol#getName()
		 */
		public String getName() {
			return "";
		}
		/* (non-Javadoc)
		 * @see org.eclipse.jst.jsf.context.symbol.ISymbol#setName(java.lang.String)
		 */
		public void setName(String value) {
		}
    }
}