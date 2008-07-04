/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.model.helpers.WebProcessStructureHelper;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesModel;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Link</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.LinkImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.LinkImpl#getFromElement <em>From Element</em>}</li>
 *   <li>{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.LinkImpl#getToElement <em>To Element</em>}</li>
 *   <li>{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.LinkImpl#isShortcut <em>Shortcut</em>}</li>
 *   <li>{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.LinkImpl#getData <em>Data</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LinkImpl extends EObjectImpl implements Link {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getFromElement() <em>From Element</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFromElement()
	 * @generated
	 * @ordered
	 */
	protected PagesElement fromElement;

	/**
	 * The cached value of the '{@link #getToElement() <em>To Element</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getToElement()
	 * @generated
	 * @ordered
	 */
	protected PagesElement toElement;

	/**
	 * The default value of the '{@link #isShortcut() <em>Shortcut</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isShortcut()
	 * @generated
	 * @ordered
	 */
	protected static final boolean SHORTCUT_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isShortcut() <em>Shortcut</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isShortcut()
	 * @generated
	 * @ordered
	 */
	protected boolean shortcut = SHORTCUT_EDEFAULT;

	/**
	 * The default value of the '{@link #getData() <em>Data</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getData()
	 * @generated
	 * @ordered
	 */
	protected static final Object DATA_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getData() <em>Data</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getData()
	 * @generated
	 * @ordered
	 */
	protected Object data = DATA_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LinkImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PagesPackage.Literals.LINK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PagesPackage.LINK__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PagesElement getFromElement() {
		if (fromElement != null && fromElement.eIsProxy()) {
			InternalEObject oldFromElement = (InternalEObject)fromElement;
			fromElement = (PagesElement)eResolveProxy(oldFromElement);
			if (fromElement != oldFromElement) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, PagesPackage.LINK__FROM_ELEMENT, oldFromElement, fromElement));
			}
		}
		return fromElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PagesElement basicGetFromElement() {
		return fromElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetFromElement(PagesElement newFromElement, NotificationChain msgs) {
		PagesElement oldFromElement = fromElement;
		fromElement = newFromElement;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PagesPackage.LINK__FROM_ELEMENT, oldFromElement, newFromElement);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFromElement(PagesElement newFromElement) {
		if (newFromElement != fromElement) {
			NotificationChain msgs = null;
			if (fromElement != null)
				msgs = ((InternalEObject)fromElement).eInverseRemove(this, PagesPackage.PAGES_ELEMENT__OUTPUT_LINKS, PagesElement.class, msgs);
			if (newFromElement != null)
				msgs = ((InternalEObject)newFromElement).eInverseAdd(this, PagesPackage.PAGES_ELEMENT__OUTPUT_LINKS, PagesElement.class, msgs);
			msgs = basicSetFromElement(newFromElement, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PagesPackage.LINK__FROM_ELEMENT, newFromElement, newFromElement));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PagesElement getToElement() {
		if (toElement != null && toElement.eIsProxy()) {
			InternalEObject oldToElement = (InternalEObject)toElement;
			toElement = (PagesElement)eResolveProxy(oldToElement);
			if (toElement != oldToElement) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, PagesPackage.LINK__TO_ELEMENT, oldToElement, toElement));
			}
		}
		return toElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PagesElement basicGetToElement() {
		return toElement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetToElement(PagesElement newToElement, NotificationChain msgs) {
		PagesElement oldToElement = toElement;
		toElement = newToElement;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PagesPackage.LINK__TO_ELEMENT, oldToElement, newToElement);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setToElement(PagesElement newToElement) {
		if (newToElement != toElement) {
			NotificationChain msgs = null;
			if (toElement != null)
				msgs = ((InternalEObject)toElement).eInverseRemove(this, PagesPackage.PAGES_ELEMENT__INPUT_LINKS, PagesElement.class, msgs);
			if (newToElement != null)
				msgs = ((InternalEObject)newToElement).eInverseAdd(this, PagesPackage.PAGES_ELEMENT__INPUT_LINKS, PagesElement.class, msgs);
			msgs = basicSetToElement(newToElement, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PagesPackage.LINK__TO_ELEMENT, newToElement, newToElement));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isShortcut() {
		return shortcut;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setShortcut(boolean newShortcut) {
		boolean oldShortcut = shortcut;
		shortcut = newShortcut;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PagesPackage.LINK__SHORTCUT, oldShortcut, shortcut));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getData() {
		return data;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setData(Object newData) {
		Object oldData = data;
		data = newData;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PagesPackage.LINK__DATA, oldData, data));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PagesPackage.LINK__FROM_ELEMENT:
				if (fromElement != null)
					msgs = ((InternalEObject)fromElement).eInverseRemove(this, PagesPackage.PAGES_ELEMENT__OUTPUT_LINKS, PagesElement.class, msgs);
				return basicSetFromElement((PagesElement)otherEnd, msgs);
			case PagesPackage.LINK__TO_ELEMENT:
				if (toElement != null)
					msgs = ((InternalEObject)toElement).eInverseRemove(this, PagesPackage.PAGES_ELEMENT__INPUT_LINKS, PagesElement.class, msgs);
				return basicSetToElement((PagesElement)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PagesPackage.LINK__FROM_ELEMENT:
				return basicSetFromElement(null, msgs);
			case PagesPackage.LINK__TO_ELEMENT:
				return basicSetToElement(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case PagesPackage.LINK__NAME:
				return getName();
			case PagesPackage.LINK__FROM_ELEMENT:
				if (resolve) return getFromElement();
				return basicGetFromElement();
			case PagesPackage.LINK__TO_ELEMENT:
				if (resolve) return getToElement();
				return basicGetToElement();
			case PagesPackage.LINK__SHORTCUT:
				return isShortcut() ? Boolean.TRUE : Boolean.FALSE;
			case PagesPackage.LINK__DATA:
				return getData();
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
			case PagesPackage.LINK__NAME:
				setName((String)newValue);
				return;
			case PagesPackage.LINK__FROM_ELEMENT:
				setFromElement((PagesElement)newValue);
				return;
			case PagesPackage.LINK__TO_ELEMENT:
				setToElement((PagesElement)newValue);
				return;
			case PagesPackage.LINK__SHORTCUT:
				setShortcut(((Boolean)newValue).booleanValue());
				return;
			case PagesPackage.LINK__DATA:
				setData(newValue);
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
			case PagesPackage.LINK__NAME:
				setName(NAME_EDEFAULT);
				return;
			case PagesPackage.LINK__FROM_ELEMENT:
				setFromElement((PagesElement)null);
				return;
			case PagesPackage.LINK__TO_ELEMENT:
				setToElement((PagesElement)null);
				return;
			case PagesPackage.LINK__SHORTCUT:
				setShortcut(SHORTCUT_EDEFAULT);
				return;
			case PagesPackage.LINK__DATA:
				setData(DATA_EDEFAULT);
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
			case PagesPackage.LINK__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case PagesPackage.LINK__FROM_ELEMENT:
				return fromElement != null;
			case PagesPackage.LINK__TO_ELEMENT:
				return toElement != null;
			case PagesPackage.LINK__SHORTCUT:
				return shortcut != SHORTCUT_EDEFAULT;
			case PagesPackage.LINK__DATA:
				return DATA_EDEFAULT == null ? data != null : !DATA_EDEFAULT.equals(data);
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
		result.append(" (name: ");
		result.append(name);
		result.append(", shortcut: ");
		result.append(shortcut);
		result.append(", data: ");
		result.append(data);
		result.append(')');
		return result.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void dataChanged() {
		if(data instanceof XModelObject) {
			XModelObject object = (XModelObject)data;
			PagesModel pagesModel = null;
			if(getFromElement() != null) {
				pagesModel = getFromElement().getPagesModel();
			} else if(getToElement() != null) {
				pagesModel = getToElement().getPagesModel();
			} else {
				return;
			}
			SeamPagesDiagramStructureHelper h = SeamPagesDiagramStructureHelper.getInstance();
			setName(h.getItemOutputPresentation(object));
			setShortcut(h.isShortcut(object));

			XModelObject t = h.getItemOutputTarget(object);
			if(t != null) {
				PagesElement to = pagesModel.findElement(t);
				if(to != getToElement()) {
					setToElement(to);
				}
			}
		}
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	private final static String PROPERTY_SHAPE = "shape";
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public PointList getPointList() {
		int[] path = null;

		path = SeamPagesDiagramStructureHelper.instance.asIntArray((XModelObject)getData(), PROPERTY_SHAPE);

		PointList list = new PointList();

		if (path.length < 4)
			return list;

		for (int i = 0; i < path.length; i += 2) {
			list.addPoint(path[i], path[i + 1]);
		}

		return list;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void savePointList(PointList list) {
		String value = "";
		for (int i = 0; i < list.size(); i++) {
			if (i != 0)
				value += ",";
			Point p = list.getPoint(i);
			value += p.x + "," + p.y;
		}
		SeamPagesDiagramStructureHelper.instance.setAttributeValue((XModelObject)getData(), PROPERTY_SHAPE, value);
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void clearPointList() {
		((XModelObject)getData()).setAttributeValue(PROPERTY_SHAPE, "");
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public String getPathFromModel() {
			return ((XModelObject)getData()).getAttributeValue(PROPERTY_SHAPE);
	}
} //LinkImpl
