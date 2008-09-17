/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EClass;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesPackage;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PageException;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Pg Exception</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class PageExceptionImpl extends PagesElementImpl implements PageException {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PageExceptionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PagesPackage.Literals.PAGE_EXCEPTION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void dataChanged() {
		XModelObject item = getModelObject();
		SeamPagesDiagramStructureHelper h = SeamPagesDiagramStructureHelper.getInstance();
		
		setName(item.getPresentationString());
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

} //PgExceptionImpl
