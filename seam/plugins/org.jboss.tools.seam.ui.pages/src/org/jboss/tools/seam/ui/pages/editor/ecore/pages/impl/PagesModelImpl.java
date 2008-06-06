/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.jboss.tools.seam.ui.pages.editor.ecore.pages.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.event.XModelTreeListener;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesFactory;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesModel;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesPackage;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PageException;

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
	SeamPagesDiagramStructureHelper h = SeamPagesDiagramStructureHelper.getInstance();

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
	Map<String, Link> linksByPath = null;

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
	public PagesModel getPagesModel() {
		return this;
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
	public void bindElement(Object data, PagesElement element) {
		element.setData(data);
		if(data instanceof XModelObject) {
			addElement((XModelObject)data, element);
		} else if(data != null) {
			elementsByPath.put(data.toString(), element);
		}
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
	public Link findLink(Object data) {
		if(data instanceof XModelObject) {
			data = ((XModelObject)data).getPath();
		}
		if(data instanceof String) {
			Link result = linksByPath.get(data);
			return result;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void bindLink(Object data, Link link) {
		link.setData(data);
		if(data instanceof XModelObject) {
			linksByPath.put(((XModelObject)data).getPath(), link);
		} else if(data != null) {
			linksByPath.put(data.toString(), link);
		}
	}

	void unbind(String path) {
		Iterator<String> it = elementsByPath.keySet().iterator();
		while(it.hasNext()) {
			String p = it.next();
			if(p.equals(path) || p.startsWith(path + "/")) {
				it.remove();
			}
		}
		it = linksByPath.keySet().iterator();
		while(it.hasNext()) {
			String p = it.next();
			if(p.equals(path) || path.startsWith(p + "/")) {
				it.remove();
			}
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void load() {
		XModelObject diagramXML = (XModelObject)getData();
		if(diagramXML == null) return;
		
		elementsByPath = new HashMap<String, PagesElement>();
		linksByPath = new HashMap<String, Link>();

		addElement(diagramXML, this);

		XModelObject[] is = h.getItems(diagramXML);
		for (int i = 0; i < is.length; i++) {
			addItem(is[i]);
		}

		for (int i = 0; i < is.length; i++) {
			addItemLinks(is[i]);
		}
		diagramXML.getModel().removeModelTreeListener(modelListener);
		diagramXML.getModel().addModelTreeListener(modelListener);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void childAdded(Object childData) {
		if(childData instanceof XModelObject) {
			XModelObject added = (XModelObject)childData;
			addItem(added);
			addItemLinks(added);
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	private void addItem(XModelObject item) {
		String type = item.getAttributeValue(SeamPagesConstants.ATTR_TYPE);
		if(SeamPagesConstants.TYPE_PAGE.equals(type)) {
			Page page = PagesFactory.eINSTANCE.createPage();
			bindElement(item, page);
			page.dataChanged();
			getChildren().add(page);
		} else if(SeamPagesConstants.TYPE_EXCEPTION.equals(type)) {
			PageException exc = PagesFactory.eINSTANCE.createPgException();
			bindElement(item, exc);
			exc.dataChanged();
			getChildren().add(exc);
		} else {
			//TODO
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
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
				from.childAdded(os[j]);
			}
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void dispose() {
		XModelObject diagramXML = (XModelObject)getData();
		if(diagramXML == null) return;
		diagramXML.getModel().removeModelTreeListener(modelListener);
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
			XModelObject diagramXML = (XModelObject)getData();
			if(!event.getModelObject().getPath().startsWith(diagramXML.getPath())) {
				return;
			}
			PagesElement item = findElement(event.getInfo().toString());
			if(item != null) {
				item.dataChanged();
			}
			Link link = findLink(event.getInfo().toString());
			if(link != null) {
				link.dataChanged();
			}
		}

		/**
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated NOT
		 */
		public void structureChanged(XModelTreeEvent event) {
			if(getData() == null) return;
			XModelObject diagramXML = (XModelObject)getData();
			XModelObject target = event.getModelObject();
			if(!target.getPath().startsWith(diagramXML.getPath())) {
				return;
			}
			if(event.kind() == XModelTreeEvent.CHILD_ADDED) {
				XModelObject added = (XModelObject)event.getInfo();
				if(target == diagramXML) {
					childAdded(added);
				} else {
					PagesElement item = findElement(target);
					item.childAdded(added);
				}
			} else if(event.kind() == XModelTreeEvent.CHILD_REMOVED) {
				if(target == diagramXML) {
					PagesElement removed = findElement(event.getInfo());
					if(removed != null) {
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
				} else if(findElement(target) != null) {
					Link removed = findLink(event.getInfo());
					if(removed != null) {
						removed.setToElement(null);
						removed.setFromElement(null);
						linksByPath.remove(event.getInfo());
					}
					
				}

				unbind(event.getInfo().toString());
			}

		}
		
	}

} //PagesModelImpl
