package org.jboss.tools.seam.ui.pages.editor.ecore.pages;

import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Image;

/**
 * @author daniel
 * 
 * Base interface for flow elements which may moved, resized and connected by links
 * 
 * @model abstract="true"
 */

public interface PagesElement extends EObject {
	/**
	 * returns name of element
	 * @model
	 */
	public String getName();
	
	/**
	 * Sets the value of the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * returns location of element
	 * @model
	 */
	public Point getLocation();
	
	/**
	 * Sets the value of the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getLocation <em>Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Location</em>' attribute.
	 * @see #getLocation()
	 * @generated
	 */
	void setLocation(Point value);

	/**
	 * returns size of element
	 * @model
	 */
	public Dimension getSize();
	
	/**
	 * Sets the value of the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getSize <em>Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Size</em>' attribute.
	 * @see #getSize()
	 * @generated
	 */
	void setSize(Dimension value);

	/**
	 * returns consumers
	 * @model type="PagesElement" opposite="parent"
	 */
	public EList<PagesElement> getChildren();
	
	/**
	 * returns consumers
	 * @model opposite = "children"
	 */
	public PagesElement getParent();
	
	/**
	 * Sets the value of the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getParent <em>Parent</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent</em>' reference.
	 * @see #getParent()
	 * @generated
	 */
	void setParent(PagesElement value);

	/**
	 * @model type="Link" opposite ="toElement"
	 */
	public EList<Link> getInputLinks();
	
	/**
	 * @model type="Link" opposite ="fromElement"
	 */
	public EList<Link> getOutputLinks();

	public Image getImage();
	
	/**
	 * returns data of element
	 * @model
	 */
	public Object getData();

	/**
	 * Sets the value of the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getData <em>Data</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Data</em>' attribute.
	 * @see #getData()
	 * @generated
	 */
	void setData(Object value);

	public void dataChanged();

	public PagesModel getPagesModel();

	public void childAdded(Object childData);

	public void changeLocationAndSize(Point location, Dimension size);
}
