package org.jboss.tools.seam.ui.pages.editor.ecore.pages;

import org.jboss.tools.seam.ui.pages.editor.edit.PageWrapper;

/** 
 * @author daniel
 * 
 * Page model interface
 * 
 * @model
 */
public interface Page extends PagesElement{
	
	/**
	 * returns true if page view has page element
	 * @model
	 */
	public boolean isConfirmed();
	
	/**
	 * Sets the value of the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page#isConfirmed <em>Confirmed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Confirmed</em>' attribute.
	 * @see #isConfirmed()
	 * @generated
	 */
	void setConfirmed(boolean value);

	/**
	 * returns true if params are visible
	 * @model
	 */
	public boolean isParamsVisible();

	/**
	 * Sets the value of the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page#isParamsVisible <em>Params Visible</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Params Visible</em>' attribute.
	 * @see #isParamsVisible()
	 * @generated
	 */
	void setParamsVisible(boolean value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public PageWrapper getParamList();

}
