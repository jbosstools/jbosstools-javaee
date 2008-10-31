/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesFactory;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesPackage;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Param;
import org.jboss.tools.seam.ui.pages.editor.edit.PageWrapper;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Page</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PageImpl#isParamsVisible <em>Params Visible</em>}</li>
 *   <li>{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PageImpl#isConfirmed <em>Confirmed</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PageImpl extends PagesElementImpl implements Page {

	private String params = "";

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
	 * The default value of the '{@link #isConfirmed() <em>Confirmed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isConfirmed()
	 * @generated
	 * @ordered
	 */
	protected static final boolean CONFIRMED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isConfirmed() <em>Confirmed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isConfirmed()
	 * @generated
	 * @ordered
	 */
	protected boolean confirmed = CONFIRMED_EDEFAULT;

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
	public boolean isConfirmed() {
		return confirmed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setConfirmed(boolean newConfirmed) {
		boolean oldConfirmed = confirmed;
		confirmed = newConfirmed;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PagesPackage.PAGE__CONFIRMED, oldConfirmed, confirmed));
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
			case PagesPackage.PAGE__CONFIRMED:
				return isConfirmed() ? Boolean.TRUE : Boolean.FALSE;
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
			case PagesPackage.PAGE__CONFIRMED:
				setConfirmed(((Boolean)newValue).booleanValue());
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
			case PagesPackage.PAGE__CONFIRMED:
				setConfirmed(CONFIRMED_EDEFAULT);
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
			case PagesPackage.PAGE__CONFIRMED:
				return confirmed != CONFIRMED_EDEFAULT;
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
		result.append(", confirmed: ");
		result.append(confirmed);
		result.append(')');
		return result.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	int updatelock = 0;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void dataChanged() {
		if(updatelock > 0) return;
		updatelock++;
		try {
		XModelObject item = getModelObject();
			SeamPagesDiagramStructureHelper h = SeamPagesDiagramStructureHelper
					.getInstance();

			setName(h.getPageTitle(item));
			int[] shape = h.asIntArray(item, "shape");
			if (shape != null && shape.length >= 2) {
				setLocation(new Point(shape[0], shape[1]));
			} else {
				setLocation(new Point(0, 0));
			}
			if (shape != null && shape.length >= 4) {
				setSize(new Dimension(shape[2], shape[3]));
			}
			
			boolean confirmed = true;
			
			if(getData() != null && getData() instanceof ReferenceObject && ((ReferenceObject)getData()).getReference() == null){
				confirmed = false;
			}
			
			if(isConfirmed() != confirmed)
				setConfirmed(confirmed);

			String newParams = item.getAttributeValue("params");
			if (newParams == null)
				newParams = "";
			if (!params.equals(newParams)) {
				params = newParams;
				String[][] ps1 = h.getParams(item);
				List<Param> ps2 = getParams();
				for (int i = 0; i < ps1.length && i < ps2.size(); i++) {
					Param p = ps2.get(i);
					p.setName(ps1[i][0]);
					p.setValue(ps1[i][1]);
				}
				if (ps1.length > ps2.size()) {
					for (int i = ps2.size(); i < ps1.length; i++) {
						Param p = PagesFactory.eINSTANCE.createParam();
						p.setName(ps1[i][0]);
						p.setValue(ps1[i][1]);
						getChildren().add(p);
					}
				} else if (ps1.length < ps2.size()) {
					for (int i = ps1.length; i < ps2.size(); i++) {
						getChildren().remove(ps2.get(i));
					}
				}
			}
		} finally {
			updatelock--;
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	List<Param> getParams() {
		List<Param> ps = new ArrayList<Param>();
		for(PagesElement p:getChildren()){
			if(p instanceof Param)
				ps.add((Param)p);
		}
		return ps;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	PageWrapper paramList = new PageWrapper(this);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public PageWrapper getParamList() {
		return paramList;
	}

} //PageImpl
