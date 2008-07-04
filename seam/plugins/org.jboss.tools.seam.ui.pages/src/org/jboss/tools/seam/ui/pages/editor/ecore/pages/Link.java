package org.jboss.tools.seam.ui.pages.editor.ecore.pages;

import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.emf.ecore.EObject;

/** 
 * @author daniel
 * 
 * Link model interface
 * 
 * @model
 */
public interface Link extends EObject {
	/**
	 * returns link's name
	 * @model
	 */
	public String getName();
	
	/**
	 * Sets the value of the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * returns element link started from
	 * @model opposite = "outputLinks"
	 */
	public PagesElement getFromElement();

	/**
	 * Sets the value of the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link#getFromElement <em>From Element</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>From Element</em>' reference.
	 * @see #getFromElement()
	 * @generated
	 */
	void setFromElement(PagesElement value);

	/**
	 * returns element link started from
	 * @model opposite = "inputLinks"
	 */
	public PagesElement getToElement();
	
	/**
	 * Sets the value of the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link#getToElement <em>To Element</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>To Element</em>' reference.
	 * @see #getToElement()
	 * @generated
	 */
	void setToElement(PagesElement value);

	/**
	 * returns true if link is shortcut
	 * @model
	 */
	public boolean isShortcut();

	/**
	 * Sets the value of the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link#isShortcut <em>Shortcut</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Shortcut</em>' attribute.
	 * @see #isShortcut()
	 * @generated
	 */
	void setShortcut(boolean value);

	/**
	 * returns data of link
	 * @model
	 */
	public Object getData();

	/**
	 * Sets the value of the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link#getData <em>Data</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Data</em>' attribute.
	 * @see #getData()
	 * @generated
	 */
	void setData(Object value);

	public void dataChanged();
	
	public PointList getPointList();
	
	public void savePointList(PointList list);
	
	public void clearPointList();
	
	public String getPathFromModel();
}
