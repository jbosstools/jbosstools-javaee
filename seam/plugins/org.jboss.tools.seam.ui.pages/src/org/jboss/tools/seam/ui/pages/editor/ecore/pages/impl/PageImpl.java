/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesProcessStructureHelper;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Page</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PageImpl#isParamsVisible <em>Params Visible</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PageImpl extends PagesElementImpl implements Page {
	/**
	 * The default value of the '{@link #isParamsVisible() <em>Params Visible</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isParamsVisible()
	 * @generated
	 * @ordered
	 */
	protected static final boolean PARAMS_VISIBLE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isParamsVisible() <em>Params Visible</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isParamsVisible()
	 * @generated
	 * @ordered
	 */
	protected boolean paramsVisible = PARAMS_VISIBLE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PageImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PagesPackage.Literals.PAGE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isParamsVisible() {
		return paramsVisible;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParamsVisible(boolean newParamsVisible) {
		boolean oldParamsVisible = paramsVisible;
		paramsVisible = newParamsVisible;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PagesPackage.PAGE__PARAMS_VISIBLE, oldParamsVisible, paramsVisible));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case PagesPackage.PAGE__PARAMS_VISIBLE:
				return isParamsVisible() ? Boolean.TRUE : Boolean.FALSE;
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case PagesPackage.PAGE__PARAMS_VISIBLE:
				setParamsVisible(((Boolean)newValue).booleanValue());
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case PagesPackage.PAGE__PARAMS_VISIBLE:
				setParamsVisible(PARAMS_VISIBLE_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case PagesPackage.PAGE__PARAMS_VISIBLE:
				return paramsVisible != PARAMS_VISIBLE_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (paramsVisible: ");
		result.append(paramsVisible);
		result.append(')');
		return result.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void dataChanged() {
		XModelObject item = getModelObject();
		SeamPagesProcessStructureHelper h = SeamPagesProcessStructureHelper.getInstance();

		setName(h.getPageTitle(item));
		int[] shape = h.asIntArray(item, "shape");
		if(shape != null && shape.length >= 2) {
			setLocation(new Point(shape[0],shape[1]));
		} else {
			setLocation(new Point(0,0));
		}
		if(shape != null && shape.length >= 4) {
			setSize(new Dimension(shape[2],shape[3]));
		}
		
	}
} //PageImpl
