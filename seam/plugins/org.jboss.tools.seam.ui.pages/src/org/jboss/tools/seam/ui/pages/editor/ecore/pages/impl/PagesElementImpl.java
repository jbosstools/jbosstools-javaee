/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl;

import java.util.Collection;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.swt.graphics.Image;

import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;
import org.jboss.tools.seam.ui.pages.SeamUiPagesPlugin;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesFactory;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesModel;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesElementImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesElementImpl#getLocation <em>Location</em>}</li>
 *   <li>{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesElementImpl#getSize <em>Size</em>}</li>
 *   <li>{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesElementImpl#getChildren <em>Children</em>}</li>
 *   <li>{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesElementImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesElementImpl#getInputLinks <em>Input Links</em>}</li>
 *   <li>{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesElementImpl#getOutputLinks <em>Output Links</em>}</li>
 *   <li>{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesElementImpl#getData <em>Data</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class PagesElementImpl extends EObjectImpl implements PagesElement {
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
	 * The default value of the '{@link #getLocation() <em>Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocation()
	 * @generated
	 * @ordered
	 */
	protected static final Point LOCATION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLocation() <em>Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocation()
	 * @generated
	 * @ordered
	 */
	protected Point location = LOCATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getSize() <em>Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSize()
	 * @generated
	 * @ordered
	 */
	protected static final Dimension SIZE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSize() <em>Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSize()
	 * @generated
	 * @ordered
	 */
	protected Dimension size = SIZE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getChildren() <em>Children</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChildren()
	 * @generated
	 * @ordered
	 */
	protected EList<PagesElement> children;

	/**
	 * The cached value of the '{@link #getParent() <em>Parent</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParent()
	 * @generated
	 * @ordered
	 */
	protected PagesElement parent;

	/**
	 * The cached value of the '{@link #getInputLinks() <em>Input Links</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInputLinks()
	 * @generated
	 * @ordered
	 */
	protected EList<Link> inputLinks;

	/**
	 * The cached value of the '{@link #getOutputLinks() <em>Output Links</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutputLinks()
	 * @generated
	 * @ordered
	 */
	protected EList<Link> outputLinks;

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
	protected PagesElementImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PagesPackage.Literals.PAGES_ELEMENT;
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
			eNotify(new ENotificationImpl(this, Notification.SET, PagesPackage.PAGES_ELEMENT__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Point getLocation() {
		return location;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLocation(Point newLocation) {
		Point oldLocation = location;
		location = newLocation;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PagesPackage.PAGES_ELEMENT__LOCATION, oldLocation, location));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public Image getImage() {
		return EclipseResourceUtil.getImage(getModelObject());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void changeLocationAndSize(Point location, Dimension size) {
		setLocation(location);
		setSize(size);
		commitShapeToData();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	protected void commitShapeToData() {
		Point location = getLocation();
		Dimension size = getSize();
		String shape = "";
		if(location != null) {
			shape = "" + location.x + "," + location.y;
		} else {
			shape = "0,0";
		}
		if(size != null) {
			shape += "," + size.width + "," + size.height;
		}
		XModelObject o = getModelObject();
		if(o != null && o.getModelEntity().getAttribute("shape") != null) {
			try {
				o.getModel().changeObjectAttribute(o, "shape", shape);
			} catch (XModelException e) {
				SeamUiPagesPlugin.getDefault().logError(e);
			}
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Dimension getSize() {
		return size;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSize(Dimension newSize) {
		Dimension oldSize = size;
		size = newSize;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PagesPackage.PAGES_ELEMENT__SIZE, oldSize, size));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<PagesElement> getChildren() {
		if (children == null) {
			children = new EObjectWithInverseResolvingEList<PagesElement>(PagesElement.class, this, PagesPackage.PAGES_ELEMENT__CHILDREN, PagesPackage.PAGES_ELEMENT__PARENT);
		}
		return children;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PagesElement getParent() {
		if (parent != null && parent.eIsProxy()) {
			InternalEObject oldParent = (InternalEObject)parent;
			parent = (PagesElement)eResolveProxy(oldParent);
			if (parent != oldParent) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, PagesPackage.PAGES_ELEMENT__PARENT, oldParent, parent));
			}
		}
		return parent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PagesElement basicGetParent() {
		return parent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParent(PagesElement newParent, NotificationChain msgs) {
		PagesElement oldParent = parent;
		parent = newParent;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PagesPackage.PAGES_ELEMENT__PARENT, oldParent, newParent);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParent(PagesElement newParent) {
		if (newParent != parent) {
			NotificationChain msgs = null;
			if (parent != null)
				msgs = ((InternalEObject)parent).eInverseRemove(this, PagesPackage.PAGES_ELEMENT__CHILDREN, PagesElement.class, msgs);
			if (newParent != null)
				msgs = ((InternalEObject)newParent).eInverseAdd(this, PagesPackage.PAGES_ELEMENT__CHILDREN, PagesElement.class, msgs);
			msgs = basicSetParent(newParent, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PagesPackage.PAGES_ELEMENT__PARENT, newParent, newParent));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Link> getInputLinks() {
		if (inputLinks == null) {
			inputLinks = new EObjectWithInverseResolvingEList<Link>(Link.class, this, PagesPackage.PAGES_ELEMENT__INPUT_LINKS, PagesPackage.LINK__TO_ELEMENT);
		}
		return inputLinks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Link> getOutputLinks() {
		if (outputLinks == null) {
			outputLinks = new EObjectWithInverseResolvingEList<Link>(Link.class, this, PagesPackage.PAGES_ELEMENT__OUTPUT_LINKS, PagesPackage.LINK__FROM_ELEMENT);
		}
		return outputLinks;
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
			eNotify(new ENotificationImpl(this, Notification.SET, PagesPackage.PAGES_ELEMENT__DATA, oldData, data));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PagesPackage.PAGES_ELEMENT__CHILDREN:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getChildren()).basicAdd(otherEnd, msgs);
			case PagesPackage.PAGES_ELEMENT__PARENT:
				if (parent != null)
					msgs = ((InternalEObject)parent).eInverseRemove(this, PagesPackage.PAGES_ELEMENT__CHILDREN, PagesElement.class, msgs);
				return basicSetParent((PagesElement)otherEnd, msgs);
			case PagesPackage.PAGES_ELEMENT__INPUT_LINKS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getInputLinks()).basicAdd(otherEnd, msgs);
			case PagesPackage.PAGES_ELEMENT__OUTPUT_LINKS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getOutputLinks()).basicAdd(otherEnd, msgs);
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
			case PagesPackage.PAGES_ELEMENT__CHILDREN:
				return ((InternalEList<?>)getChildren()).basicRemove(otherEnd, msgs);
			case PagesPackage.PAGES_ELEMENT__PARENT:
				return basicSetParent(null, msgs);
			case PagesPackage.PAGES_ELEMENT__INPUT_LINKS:
				return ((InternalEList<?>)getInputLinks()).basicRemove(otherEnd, msgs);
			case PagesPackage.PAGES_ELEMENT__OUTPUT_LINKS:
				return ((InternalEList<?>)getOutputLinks()).basicRemove(otherEnd, msgs);
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
			case PagesPackage.PAGES_ELEMENT__NAME:
				return getName();
			case PagesPackage.PAGES_ELEMENT__LOCATION:
				return getLocation();
			case PagesPackage.PAGES_ELEMENT__SIZE:
				return getSize();
			case PagesPackage.PAGES_ELEMENT__CHILDREN:
				return getChildren();
			case PagesPackage.PAGES_ELEMENT__PARENT:
				if (resolve) return getParent();
				return basicGetParent();
			case PagesPackage.PAGES_ELEMENT__INPUT_LINKS:
				return getInputLinks();
			case PagesPackage.PAGES_ELEMENT__OUTPUT_LINKS:
				return getOutputLinks();
			case PagesPackage.PAGES_ELEMENT__DATA:
				return getData();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case PagesPackage.PAGES_ELEMENT__NAME:
				setName((String)newValue);
				return;
			case PagesPackage.PAGES_ELEMENT__LOCATION:
				setLocation((Point)newValue);
				return;
			case PagesPackage.PAGES_ELEMENT__SIZE:
				setSize((Dimension)newValue);
				return;
			case PagesPackage.PAGES_ELEMENT__CHILDREN:
				getChildren().clear();
				getChildren().addAll((Collection<? extends PagesElement>)newValue);
				return;
			case PagesPackage.PAGES_ELEMENT__PARENT:
				setParent((PagesElement)newValue);
				return;
			case PagesPackage.PAGES_ELEMENT__INPUT_LINKS:
				getInputLinks().clear();
				getInputLinks().addAll((Collection<? extends Link>)newValue);
				return;
			case PagesPackage.PAGES_ELEMENT__OUTPUT_LINKS:
				getOutputLinks().clear();
				getOutputLinks().addAll((Collection<? extends Link>)newValue);
				return;
			case PagesPackage.PAGES_ELEMENT__DATA:
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
			case PagesPackage.PAGES_ELEMENT__NAME:
				setName(NAME_EDEFAULT);
				return;
			case PagesPackage.PAGES_ELEMENT__LOCATION:
				setLocation(LOCATION_EDEFAULT);
				return;
			case PagesPackage.PAGES_ELEMENT__SIZE:
				setSize(SIZE_EDEFAULT);
				return;
			case PagesPackage.PAGES_ELEMENT__CHILDREN:
				getChildren().clear();
				return;
			case PagesPackage.PAGES_ELEMENT__PARENT:
				setParent((PagesElement)null);
				return;
			case PagesPackage.PAGES_ELEMENT__INPUT_LINKS:
				getInputLinks().clear();
				return;
			case PagesPackage.PAGES_ELEMENT__OUTPUT_LINKS:
				getOutputLinks().clear();
				return;
			case PagesPackage.PAGES_ELEMENT__DATA:
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
			case PagesPackage.PAGES_ELEMENT__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case PagesPackage.PAGES_ELEMENT__LOCATION:
				return LOCATION_EDEFAULT == null ? location != null : !LOCATION_EDEFAULT.equals(location);
			case PagesPackage.PAGES_ELEMENT__SIZE:
				return SIZE_EDEFAULT == null ? size != null : !SIZE_EDEFAULT.equals(size);
			case PagesPackage.PAGES_ELEMENT__CHILDREN:
				return children != null && !children.isEmpty();
			case PagesPackage.PAGES_ELEMENT__PARENT:
				return parent != null;
			case PagesPackage.PAGES_ELEMENT__INPUT_LINKS:
				return inputLinks != null && !inputLinks.isEmpty();
			case PagesPackage.PAGES_ELEMENT__OUTPUT_LINKS:
				return outputLinks != null && !outputLinks.isEmpty();
			case PagesPackage.PAGES_ELEMENT__DATA:
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
		result.append(", location: ");
		result.append(location);
		result.append(", size: ");
		result.append(size);
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
	public PagesModel getPagesModel() {
		return parent == null ? null : parent.getPagesModel();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void dataChanged() {
		
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void childAdded(Object childData) {
		PagesModel pagesModel = getPagesModel();
		if(pagesModel == null) return;

		if(childData instanceof XModelObject) {
			SeamPagesDiagramStructureHelper h = SeamPagesDiagramStructureHelper.getInstance();
			XModelObject object = (XModelObject)childData;
			if(object.getModelEntity().getName().equals(SeamPagesConstants.ENT_DIAGRAM_ITEM_OUTPUT)) {
				PagesElement from = this; //pagesModel.findElement(object);
				XModelObject t = h.getItemOutputTarget(object);
				if(t == null) {
					//report
					return;
				}
				PagesElement to = pagesModel.findElement(t);
				if(to == null) {
					//TODO report failure
					return;
				}
				Link link = PagesFactory.eINSTANCE.createLink();
				pagesModel.bindLink(childData, link);
				link.setFromElement(from);
				link.dataChanged();
			}
		}

	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	protected XModelObject getModelObject() {
		if(data instanceof XModelObject) {
			return (XModelObject)data;
		}
		return null;
	}
} //PagesElementImpl
