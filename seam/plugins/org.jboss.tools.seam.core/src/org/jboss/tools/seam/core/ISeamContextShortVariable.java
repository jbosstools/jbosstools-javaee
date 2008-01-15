package org.jboss.tools.seam.core;

/**
 * A seam variable can be accessed by a short name if seam package  
 * is imported. In this model we duplicate variable, the copy has 
 * name equal to short name of original. This interface is designed
 * for instantiating a duplication object.
 * 
 * @author Viacheslav Kabanovich
 */
public interface ISeamContextShortVariable extends ISeamContextVariable {
	/**
	 * Returns variable with full name used to create this instance.
	 * @return
	 */
	public ISeamContextVariable getOriginal();
}
