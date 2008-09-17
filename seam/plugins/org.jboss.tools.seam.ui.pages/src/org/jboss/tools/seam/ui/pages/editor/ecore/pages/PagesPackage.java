/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.jboss.tools.seam.ui.pages.editor.ecore.pages;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesFactory
 * @model kind="package"
 * @generated
 */
public interface PagesPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "pages";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http:///org/jboss/tools/seam/ui/pages/editor/ecore/pages.ecore";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "org.jboss.tools.seam.ui.pages.editor.ecore.pages";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	PagesPackage eINSTANCE = org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesElementImpl <em>Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesElementImpl
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesPackageImpl#getPagesElement()
	 * @generated
	 */
	int PAGES_ELEMENT = 2;

	/**
	 * The meta object id for the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.LinkImpl <em>Link</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.LinkImpl
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesPackageImpl#getLink()
	 * @generated
	 */
	int LINK = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__NAME = 0;

	/**
	 * The feature id for the '<em><b>From Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__FROM_ELEMENT = 1;

	/**
	 * The feature id for the '<em><b>To Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__TO_ELEMENT = 2;

	/**
	 * The feature id for the '<em><b>Shortcut</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__SHORTCUT = 3;

	/**
	 * The feature id for the '<em><b>Data</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK__DATA = 4;

	/**
	 * The number of structural features of the '<em>Link</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LINK_FEATURE_COUNT = 5;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_ELEMENT__NAME = 0;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_ELEMENT__LOCATION = 1;

	/**
	 * The feature id for the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_ELEMENT__SIZE = 2;

	/**
	 * The feature id for the '<em><b>Children</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_ELEMENT__CHILDREN = 3;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_ELEMENT__PARENT = 4;

	/**
	 * The feature id for the '<em><b>Input Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_ELEMENT__INPUT_LINKS = 5;

	/**
	 * The feature id for the '<em><b>Output Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_ELEMENT__OUTPUT_LINKS = 6;

	/**
	 * The feature id for the '<em><b>Data</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_ELEMENT__DATA = 7;

	/**
	 * The number of structural features of the '<em>Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_ELEMENT_FEATURE_COUNT = 8;

	/**
	 * The meta object id for the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PageImpl <em>Page</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PageImpl
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesPackageImpl#getPage()
	 * @generated
	 */
	int PAGE = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE__NAME = PAGES_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE__LOCATION = PAGES_ELEMENT__LOCATION;

	/**
	 * The feature id for the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE__SIZE = PAGES_ELEMENT__SIZE;

	/**
	 * The feature id for the '<em><b>Children</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE__CHILDREN = PAGES_ELEMENT__CHILDREN;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE__PARENT = PAGES_ELEMENT__PARENT;

	/**
	 * The feature id for the '<em><b>Input Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE__INPUT_LINKS = PAGES_ELEMENT__INPUT_LINKS;

	/**
	 * The feature id for the '<em><b>Output Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE__OUTPUT_LINKS = PAGES_ELEMENT__OUTPUT_LINKS;

	/**
	 * The feature id for the '<em><b>Data</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE__DATA = PAGES_ELEMENT__DATA;

	/**
	 * The feature id for the '<em><b>Params Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE__PARAMS_VISIBLE = PAGES_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Confirmed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE__CONFIRMED = PAGES_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Page</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_FEATURE_COUNT = PAGES_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesModelImpl <em>Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesModelImpl
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesPackageImpl#getPagesModel()
	 * @generated
	 */
	int PAGES_MODEL = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_MODEL__NAME = PAGES_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_MODEL__LOCATION = PAGES_ELEMENT__LOCATION;

	/**
	 * The feature id for the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_MODEL__SIZE = PAGES_ELEMENT__SIZE;

	/**
	 * The feature id for the '<em><b>Children</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_MODEL__CHILDREN = PAGES_ELEMENT__CHILDREN;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_MODEL__PARENT = PAGES_ELEMENT__PARENT;

	/**
	 * The feature id for the '<em><b>Input Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_MODEL__INPUT_LINKS = PAGES_ELEMENT__INPUT_LINKS;

	/**
	 * The feature id for the '<em><b>Output Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_MODEL__OUTPUT_LINKS = PAGES_ELEMENT__OUTPUT_LINKS;

	/**
	 * The feature id for the '<em><b>Data</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_MODEL__DATA = PAGES_ELEMENT__DATA;

	/**
	 * The number of structural features of the '<em>Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_MODEL_FEATURE_COUNT = PAGES_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.ParamImpl <em>Param</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.ParamImpl
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesPackageImpl#getParam()
	 * @generated
	 */
	int PARAM = 4;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAM__NAME = PAGES_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAM__LOCATION = PAGES_ELEMENT__LOCATION;

	/**
	 * The feature id for the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAM__SIZE = PAGES_ELEMENT__SIZE;

	/**
	 * The feature id for the '<em><b>Children</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAM__CHILDREN = PAGES_ELEMENT__CHILDREN;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAM__PARENT = PAGES_ELEMENT__PARENT;

	/**
	 * The feature id for the '<em><b>Input Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAM__INPUT_LINKS = PAGES_ELEMENT__INPUT_LINKS;

	/**
	 * The feature id for the '<em><b>Output Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAM__OUTPUT_LINKS = PAGES_ELEMENT__OUTPUT_LINKS;

	/**
	 * The feature id for the '<em><b>Data</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAM__DATA = PAGES_ELEMENT__DATA;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAM__VALUE = PAGES_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Param</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAM_FEATURE_COUNT = PAGES_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PageExceptionImpl <em>Page Exception</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PageExceptionImpl
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesPackageImpl#getPageException()
	 * @generated
	 */
	int PAGE_EXCEPTION = 5;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_EXCEPTION__NAME = PAGES_ELEMENT__NAME;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_EXCEPTION__LOCATION = PAGES_ELEMENT__LOCATION;

	/**
	 * The feature id for the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_EXCEPTION__SIZE = PAGES_ELEMENT__SIZE;

	/**
	 * The feature id for the '<em><b>Children</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_EXCEPTION__CHILDREN = PAGES_ELEMENT__CHILDREN;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_EXCEPTION__PARENT = PAGES_ELEMENT__PARENT;

	/**
	 * The feature id for the '<em><b>Input Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_EXCEPTION__INPUT_LINKS = PAGES_ELEMENT__INPUT_LINKS;

	/**
	 * The feature id for the '<em><b>Output Links</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_EXCEPTION__OUTPUT_LINKS = PAGES_ELEMENT__OUTPUT_LINKS;

	/**
	 * The feature id for the '<em><b>Data</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_EXCEPTION__DATA = PAGES_ELEMENT__DATA;

	/**
	 * The number of structural features of the '<em>Page Exception</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_EXCEPTION_FEATURE_COUNT = PAGES_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '<em>Point</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.draw2d.geometry.Point
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesPackageImpl#getPoint()
	 * @generated
	 */
	int POINT = 6;

	/**
	 * The meta object id for the '<em>Dimension</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.draw2d.geometry.Dimension
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesPackageImpl#getDimension()
	 * @generated
	 */
	int DIMENSION = 7;


	/**
	 * Returns the meta object for class '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link <em>Link</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Link</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link
	 * @generated
	 */
	EClass getLink();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link#getName()
	 * @see #getLink()
	 * @generated
	 */
	EAttribute getLink_Name();

	/**
	 * Returns the meta object for the reference '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link#getFromElement <em>From Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>From Element</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link#getFromElement()
	 * @see #getLink()
	 * @generated
	 */
	EReference getLink_FromElement();

	/**
	 * Returns the meta object for the reference '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link#getToElement <em>To Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>To Element</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link#getToElement()
	 * @see #getLink()
	 * @generated
	 */
	EReference getLink_ToElement();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link#isShortcut <em>Shortcut</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Shortcut</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link#isShortcut()
	 * @see #getLink()
	 * @generated
	 */
	EAttribute getLink_Shortcut();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link#getData <em>Data</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Data</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link#getData()
	 * @see #getLink()
	 * @generated
	 */
	EAttribute getLink_Data();

	/**
	 * Returns the meta object for class '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page <em>Page</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Page</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page
	 * @generated
	 */
	EClass getPage();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page#isParamsVisible <em>Params Visible</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Params Visible</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page#isParamsVisible()
	 * @see #getPage()
	 * @generated
	 */
	EAttribute getPage_ParamsVisible();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page#isConfirmed <em>Confirmed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Confirmed</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page#isConfirmed()
	 * @see #getPage()
	 * @generated
	 */
	EAttribute getPage_Confirmed();

	/**
	 * Returns the meta object for class '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement <em>Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Element</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement
	 * @generated
	 */
	EClass getPagesElement();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getName()
	 * @see #getPagesElement()
	 * @generated
	 */
	EAttribute getPagesElement_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getLocation <em>Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Location</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getLocation()
	 * @see #getPagesElement()
	 * @generated
	 */
	EAttribute getPagesElement_Location();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getSize <em>Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Size</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getSize()
	 * @see #getPagesElement()
	 * @generated
	 */
	EAttribute getPagesElement_Size();

	/**
	 * Returns the meta object for the reference list '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getChildren <em>Children</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Children</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getChildren()
	 * @see #getPagesElement()
	 * @generated
	 */
	EReference getPagesElement_Children();

	/**
	 * Returns the meta object for the reference '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Parent</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getParent()
	 * @see #getPagesElement()
	 * @generated
	 */
	EReference getPagesElement_Parent();

	/**
	 * Returns the meta object for the reference list '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getInputLinks <em>Input Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Input Links</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getInputLinks()
	 * @see #getPagesElement()
	 * @generated
	 */
	EReference getPagesElement_InputLinks();

	/**
	 * Returns the meta object for the reference list '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getOutputLinks <em>Output Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Output Links</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getOutputLinks()
	 * @see #getPagesElement()
	 * @generated
	 */
	EReference getPagesElement_OutputLinks();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getData <em>Data</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Data</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement#getData()
	 * @see #getPagesElement()
	 * @generated
	 */
	EAttribute getPagesElement_Data();

	/**
	 * Returns the meta object for class '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesModel <em>Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Model</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesModel
	 * @generated
	 */
	EClass getPagesModel();

	/**
	 * Returns the meta object for class '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Param <em>Param</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Param</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.Param
	 * @generated
	 */
	EClass getParam();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.Param#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.Param#getValue()
	 * @see #getParam()
	 * @generated
	 */
	EAttribute getParam_Value();

	/**
	 * Returns the meta object for class '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.PageException <em>Page Exception</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Page Exception</em>'.
	 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.PageException
	 * @generated
	 */
	EClass getPageException();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.draw2d.geometry.Point <em>Point</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Point</em>'.
	 * @see org.eclipse.draw2d.geometry.Point
	 * @model instanceClass="org.eclipse.draw2d.geometry.Point"
	 * @generated
	 */
	EDataType getPoint();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.draw2d.geometry.Dimension <em>Dimension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Dimension</em>'.
	 * @see org.eclipse.draw2d.geometry.Dimension
	 * @model instanceClass="org.eclipse.draw2d.geometry.Dimension"
	 * @generated
	 */
	EDataType getDimension();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	PagesFactory getPagesFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.LinkImpl <em>Link</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.LinkImpl
		 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesPackageImpl#getLink()
		 * @generated
		 */
		EClass LINK = eINSTANCE.getLink();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINK__NAME = eINSTANCE.getLink_Name();

		/**
		 * The meta object literal for the '<em><b>From Element</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LINK__FROM_ELEMENT = eINSTANCE.getLink_FromElement();

		/**
		 * The meta object literal for the '<em><b>To Element</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LINK__TO_ELEMENT = eINSTANCE.getLink_ToElement();

		/**
		 * The meta object literal for the '<em><b>Shortcut</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINK__SHORTCUT = eINSTANCE.getLink_Shortcut();

		/**
		 * The meta object literal for the '<em><b>Data</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LINK__DATA = eINSTANCE.getLink_Data();

		/**
		 * The meta object literal for the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PageImpl <em>Page</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PageImpl
		 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesPackageImpl#getPage()
		 * @generated
		 */
		EClass PAGE = eINSTANCE.getPage();

		/**
		 * The meta object literal for the '<em><b>Params Visible</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE__PARAMS_VISIBLE = eINSTANCE.getPage_ParamsVisible();

		/**
		 * The meta object literal for the '<em><b>Confirmed</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE__CONFIRMED = eINSTANCE.getPage_Confirmed();

		/**
		 * The meta object literal for the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesElementImpl <em>Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesElementImpl
		 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesPackageImpl#getPagesElement()
		 * @generated
		 */
		EClass PAGES_ELEMENT = eINSTANCE.getPagesElement();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGES_ELEMENT__NAME = eINSTANCE.getPagesElement_Name();

		/**
		 * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGES_ELEMENT__LOCATION = eINSTANCE.getPagesElement_Location();

		/**
		 * The meta object literal for the '<em><b>Size</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGES_ELEMENT__SIZE = eINSTANCE.getPagesElement_Size();

		/**
		 * The meta object literal for the '<em><b>Children</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PAGES_ELEMENT__CHILDREN = eINSTANCE.getPagesElement_Children();

		/**
		 * The meta object literal for the '<em><b>Parent</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PAGES_ELEMENT__PARENT = eINSTANCE.getPagesElement_Parent();

		/**
		 * The meta object literal for the '<em><b>Input Links</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PAGES_ELEMENT__INPUT_LINKS = eINSTANCE.getPagesElement_InputLinks();

		/**
		 * The meta object literal for the '<em><b>Output Links</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PAGES_ELEMENT__OUTPUT_LINKS = eINSTANCE.getPagesElement_OutputLinks();

		/**
		 * The meta object literal for the '<em><b>Data</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGES_ELEMENT__DATA = eINSTANCE.getPagesElement_Data();

		/**
		 * The meta object literal for the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesModelImpl <em>Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesModelImpl
		 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesPackageImpl#getPagesModel()
		 * @generated
		 */
		EClass PAGES_MODEL = eINSTANCE.getPagesModel();

		/**
		 * The meta object literal for the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.ParamImpl <em>Param</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.ParamImpl
		 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesPackageImpl#getParam()
		 * @generated
		 */
		EClass PARAM = eINSTANCE.getParam();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAM__VALUE = eINSTANCE.getParam_Value();

		/**
		 * The meta object literal for the '{@link org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PageExceptionImpl <em>Page Exception</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PageExceptionImpl
		 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesPackageImpl#getPageException()
		 * @generated
		 */
		EClass PAGE_EXCEPTION = eINSTANCE.getPageException();

		/**
		 * The meta object literal for the '<em>Point</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.draw2d.geometry.Point
		 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesPackageImpl#getPoint()
		 * @generated
		 */
		EDataType POINT = eINSTANCE.getPoint();

		/**
		 * The meta object literal for the '<em>Dimension</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.draw2d.geometry.Dimension
		 * @see org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl.PagesPackageImpl#getDimension()
		 * @generated
		 */
		EDataType DIMENSION = eINSTANCE.getDimension();

	}

} //PagesPackage
