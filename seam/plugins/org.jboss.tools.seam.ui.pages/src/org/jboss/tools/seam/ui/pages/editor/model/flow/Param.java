package org.jboss.tools.seam.ui.pages.editor.ecore.pages;

/** 
 * @author daniel
 * 
 * Param model interface
 * 
 * @model
 */
public interface Param extends PagesElement{
	/**
	 * returns value of param
	 * @model
	 */
	public String getValue();

	/**
	 * Sets the value of the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Param#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(String value);
}
