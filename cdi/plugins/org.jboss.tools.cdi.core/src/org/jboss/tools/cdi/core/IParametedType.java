package org.jboss.tools.cdi.core;

import org.eclipse.jdt.core.IType;

public interface IParametedType {

	/**
	 * Returns the corresponding IType of the declaration.
	 * 
	 * @return the corresponding IType of the declaration.
	 */
	IType getType();
}
