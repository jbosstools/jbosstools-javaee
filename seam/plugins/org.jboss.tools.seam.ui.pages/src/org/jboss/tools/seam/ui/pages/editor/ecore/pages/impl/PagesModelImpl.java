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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.event.XModelTreeListener;
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
	Map<String, PagesElement> elementsByPath = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	XModelTreeListener modelListener = new ML();

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
			data = ((XModelObject)data).getPath();
		}
		if(data instanceof String) {
			PagesElement result = elementsByPath.get(data);
			return result;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	void addElement(XModelObject data, PagesElement element) {
		elementsByPath.put(data.getPath(), element);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void load() {
		XModelObject installedProcess = (XModelObject)getData();
		if(installedProcess == null) return;
		
		elementsByPath = new HashMap<String, PagesElement>();
		addElement(installedProcess, this);
		XModelObject[] is = h.getItems(installedProcess);
		for (int i = 0; i < is.length; i++) {
			addItem(is[i]);
		}

		for (int i = 0; i < is.length; i++) {
			addItemLinks(is[i]);
		}
		installedProcess.getModel().removeModelTreeListener(modelListener);
		installedProcess.getModel().addModelTreeListener(modelListener);
	}

	private void addItem(XModelObject item) {
		String type = item.getAttributeValue(SeamPagesConstants.ATTR_TYPE);
		if(SeamPagesConstants.TYPE_PAGE.equals(type)) {
			Page page = PagesFactory.eINSTANCE.createPage();
			page.setData(item);
			page.dataChanged();
			addElement(item, page);
			getChildren().add(page);
		} else if(SeamPagesConstants.TYPE_EXCEPTION.equals(type)) {
			PgException exc = PagesFactory.eINSTANCE.createPgException();
			exc.setData(item);
			exc.dataChanged();
			addElement(item, exc);
			getChildren().add(exc);
		} else {
			//TODO
		}
	}

	private void addItemLinks(XModelObject item) {
		String type = item.getAttributeValue(SeamPagesConstants.ATTR_TYPE);
		if(SeamPagesConstants.TYPE_PAGE.equals(type)
			|| SeamPagesConstants.TYPE_EXCEPTION.equals(type)) {
			PagesElement from = findElement(item);
			if(from == null) {
				//TODO report failure
				return;
			}
			XModelObject[] os = h.getOutputs(item);
			for (int j = 0; j < os.length; j++) {
				XModelObject t = h.getItemOutputTarget(os[j]);
				if(t == null) {
					//TODO report failure
					return;
				}
				PagesElement to = findElement(t);
				if(to == null) {
					//TODO report failure
					return;
				}
				Link link = PagesFactory.eINSTANCE.createLink();
				link.setFromElement(from);
				link.setToElement(to);
				link.setName(h.getItemOutputPresentation(os[j]));
				link.setShortcut(h.isShortcut(os[j]));
			}
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void update() {
		XModelObject installedProcess = (XModelObject)getData();
		if(installedProcess == null) return;

	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void dispose() {
		XModelObject installedProcess = (XModelObject)getData();
		if(installedProcess == null) return;
		installedProcess.getModel().removeModelTreeListener(modelListener);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	class ML implements XModelTreeListener {

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated NOT
		 */
		public void nodeChanged(XModelTreeEvent event) {
			if(getData() == null) return;
			XModelObject installedProcess = (XModelObject)getData();
			if(!event.getModelObject().getPath().startsWith(installedProcess.getPath())) {
				return;
			}
			PagesElement item = findElement(event.getModelObject());
			if(item != null) {
				item.dataChanged();
			}
			//TODO update link
		}

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated NOT
		 */
		public void structureChanged(XModelTreeEvent event) {
			if(getData() == null) return;
			XModelObject installedProcess = (XModelObject)getData();
			XModelObject target = event.getModelObject();
			if(!target.getPath().startsWith(installedProcess.getPath())) {
				return;
			}
			if(event.kind() == XModelTreeEvent.CHILD_ADDED) {
				XModelObject added = (XModelObject)event.getInfo();
				if(target == installedProcess) {
					addItem(added);
					addItemLinks(added);
				} else {
					PagesElement item = findElement(target);
					//TODO
				}
			} else if(event.kind() == XModelTreeEvent.CHILD_REMOVED) {
				if(target == installedProcess) {
					PagesElement removed = findElement(event.getInfo());
					if(removed != null) {
						elementsByPath.remove(event.getInfo());
						Link[] ls = removed.getOutputLinks().toArray(new Link[0]);
						for (int i = 0; i < ls.length; i++) {
							ls[i].setFromElement(null);
							ls[i].setToElement(null);
						}
						ls = removed.getInputLinks().toArray(new Link[0]);
						for (int i = 0; i < ls.length; i++) {
							ls[i].setFromElement(null);
							ls[i].setToElement(null);
						}
						getChildren().remove(removed);
					}
				} else {
					
				}
			}
			update();
		}
		
	}

} //PagesModelImpl
