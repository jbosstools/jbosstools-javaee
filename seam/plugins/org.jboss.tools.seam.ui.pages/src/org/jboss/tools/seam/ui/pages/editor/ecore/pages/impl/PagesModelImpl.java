/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EClass;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesProcessStructureHelper;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesFactory;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesModel;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesPackage;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PgException;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class PagesModelImpl extends PagesElementImpl implements PagesModel {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	SeamPagesProcessStructureHelper h = SeamPagesProcessStructureHelper.getInstance();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	Map<XModelObject, PagesElement> elements = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PagesModelImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PagesPackage.Literals.PAGES_MODEL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */

	public PagesElement findElement(Object data) {
		if(data instanceof XModelObject) {
			PagesElement result = elements.get(data);
			//Check null and something else
			return result;
		}
		return null;
	}
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void load() {
		XModelObject installedProcess = (XModelObject)getData();
		if(installedProcess == null) return;
		
		elements = new HashMap<XModelObject, PagesElement>();
		XModelObject[] is = h.getItems(installedProcess);
		for (int i = 0; i < is.length; i++) {
			String type = is[i].getAttributeValue(SeamPagesConstants.ATTR_TYPE);
			if(SeamPagesConstants.TYPE_PAGE.equals(type)) {
				Page page = PagesFactory.eINSTANCE.createPage();
				page.setName(h.getPageTitle(is[i]));
				int[] shape = h.asIntArray(is[i], "shape");
				if(shape != null && shape.length >= 2) {
					page.setLocation(new Point(shape[0],shape[1]));
				}
				if(shape != null && shape.length >= 4) {
					page.setSize(new Dimension(shape[2],shape[3]));
				}
				page.setData(is[i]);
				getChildren().add(page);
				elements.put(is[i], page);
			} else if(SeamPagesConstants.TYPE_EXCEPTION.equals(type)) {
				PgException exc = PagesFactory.eINSTANCE.createPgException();
				exc.setName(is[i].getPresentationString());
				int[] shape = h.asIntArray(is[i], "shape");
				if(shape != null && shape.length >= 2) {
					exc.setLocation(new Point(shape[0],shape[1]));
				}
				if(shape != null && shape.length >= 4) {
					exc.setSize(new Dimension(shape[2],shape[3]));
				}
				exc.setData(is[i]);
				getChildren().add(exc);
				//maybe we need other map for exceptions?
				elements.put(is[i], exc);
			} else {
				//TODO
			}
		}

		for (int i = 0; i < is.length; i++) {
			String type = is[i].getAttributeValue(SeamPagesConstants.ATTR_TYPE);
			if(SeamPagesConstants.TYPE_PAGE.equals(type)
				|| SeamPagesConstants.TYPE_EXCEPTION.equals(type)) {
				PagesElement from = elements.get(is[i]);
				if(from == null) {
					//TODO report failure
					continue;
				}
				XModelObject[] os = h.getOutputs(is[i]);
				for (int j = 0; j < os.length; j++) {
					XModelObject t = h.getItemOutputTarget(os[j]);
					if(t == null) {
						//TODO report failure
						continue;
					}
					PagesElement to = elements.get(t);
					if(to == null) {
						//TODO report failure
						continue;
					}
					Link link = PagesFactory.eINSTANCE.createLink();
					link.setFromElement(from);
					link.setToElement(to);
					link.setName(h.getItemOutputPresentation(os[j]));
					link.setShortcut(h.isShortcut(os[j]));
				}
			}
		}

	}

} //PagesModelImpl
