/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.jboss.tools.seam.internal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.model.ServiceDialog;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.model.project.ext.event.Change;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamPropertiesDeclaration;
import org.jboss.tools.seam.core.ISeamProperty;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.w3c.dom.Element;

public class SeamPropertiesDeclaration extends SeamComponentDeclaration
		implements ISeamPropertiesDeclaration {

	protected Map<String,ISeamProperty> properties = new HashMap<String, ISeamProperty>();

	public void addProperty(ISeamProperty property) {
		properties.put(property.getName(), property);
		adopt(property);
	}

	public List<ISeamProperty> getProperties(String propertyName) {
		List<ISeamProperty> list = new ArrayList<ISeamProperty>();
		ISeamProperty p = properties.get(propertyName);
		if(p != null) list.add(p);
		return list;
	}

	public Collection<ISeamProperty> getProperties() {
		return properties.values();
	}

	public ISeamProperty getProperty(String propertyName) {
		return properties.get(propertyName);
	}

	public void removeProperty(ISeamProperty property) {
		properties.remove(property.getName());		
	}
	
	/**
	 * Merges loaded data into currently used declaration.
	 * If changes were done returns a list of changes. 
	 * @param d
	 * @return list of changes
	 */
	@Override
	public List<Change> merge(ISeamElement s) {
		List<Change> changes = super.merge(s);
		SeamPropertiesDeclaration pd = (SeamPropertiesDeclaration)s;
		
		Change children = new Change(this, null, null, null);

		String[] names = properties.keySet().toArray(new String[0]);
		for (int i = 0; i < names.length; i++) {
			SeamProperty p1 = (SeamProperty)properties.get(names[i]);
			SeamProperty p2 = (SeamProperty)pd.properties.get(names[i]);
			if(p2 == null) {
				changes = Change.addChange(changes, new Change(this, null, p1, null));
				properties.remove(names[i]);
			} else {
				String oldName = p1.getName();
				List<Change> cc = p1.merge(p2);
				if(cc != null && !cc.isEmpty()) children.addChildren(cc);
				if(oldName != null && !oldName.equals(p1.getName())) {
					properties.remove(oldName);
					addProperty(p1);
				}
			}
		}
		names = pd.properties.keySet().toArray(new String[0]);
		for (int i = 0; i < names.length; i++) {
			SeamProperty p1 = (SeamProperty)properties.get(names[i]);
			SeamProperty p2 = (SeamProperty)pd.properties.get(names[i]);
			if(p1 == null) {
				changes = Change.addChange(changes, new Change(this, null, null, p2));
				addProperty(p2);
			}
		}

		changes = Change.addChange(changes, children);

		return changes;
	}

	public void open() {
		if(id instanceof XModelObject) {
			XModelObject o = (XModelObject)id;
			FindObjectHelper.findModelObject(o, FindObjectHelper.IN_EDITOR_ONLY);
		} else if(getResource() instanceof IFile) {
			IFile f = (IFile)getResource();
			if(f == null || !f.exists()) {
				ServiceDialog d = PreferenceModelUtilities.getPreferenceModel().getService();
				d.showDialog("Warning", "File " + getSourcePath() + " does not exist.", new String[]{SpecialWizardSupport.OK}, null, ServiceDialog.WARNING);
				return;
			}

			try {
				IWorkbench workbench = SeamCorePlugin.getDefault().getWorkbench();
				if(workbench == null) return;
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				if(window == null) return;
				IWorkbenchPage page = window.getActivePage();
				if(page != null) IDE.openEditor(page, f);
			} catch(PartInitException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
			
		}
	}
	
	public SeamPropertiesDeclaration clone() throws CloneNotSupportedException {
		SeamPropertiesDeclaration c = (SeamPropertiesDeclaration)super.clone();
		c.properties = new HashMap<String, ISeamProperty>();
		for (String name : properties.keySet()) {
			ISeamProperty p = properties.get(name).clone();
			c.addProperty(p);
		}
		return c;
	}

	public String getXMLClass() {
		return SeamXMLConstants.CLS_PROPERTIES;
	}

	public Element toXML(Element parent, Properties context) {
		Element element = super.toXML(parent, context);

		XModelObject old = pushModelObject(context);

		for (String name: properties.keySet()) {
			SeamProperty p = (SeamProperty)properties.get(name);
			p.toXML(element, context);
		}

		popModelObject(context, old);

		return element;
	}
	
	public void loadXML(Element element, Properties context) {
		super.loadXML(element, context);

		XModelObject old = pushModelObject(context);

		Element[] cs = XMLUtilities.getChildren(element, SeamXMLConstants.TAG_PROPERTY);
		for (int i = 0; i < cs.length; i++) {
			SeamProperty p = new SeamProperty();
			p.loadXML(cs[i], context);
			addProperty(p);
		}

		popModelObject(context, old);
	}

}
