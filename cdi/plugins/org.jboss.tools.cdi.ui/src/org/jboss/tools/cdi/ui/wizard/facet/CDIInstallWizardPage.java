/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.cdi.ui.wizard.facet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;
import org.eclipse.wst.common.project.facet.ui.IFacetWizardPage;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.internal.core.project.facet.ICDIFacetDataModelProperties;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditorFactory;

/**
 * @author Alexey Kazakov
 */
public class CDIInstallWizardPage extends AbstractFacetWizardPage implements IFacetWizardPage, IDataModelListener {

	private boolean generateBeansXml;
	private IDataModel model = null;

	protected Map<String,IFieldEditor> editorRegistry = new HashMap<String,IFieldEditor>();

	public CDIInstallWizardPage() {
		super(CDIUIMessages.CDI_INSTALL_WIZARD_PAGE_FACET);
		setTitle(CDIUIMessages.CDI_INSTALL_WIZARD_PAGE_FACET);
		setImageDescriptor(CDIImages.getImageDescriptor(CDIImages.WELD_WIZARD_IMAGE_PATH));
		setDescription(CDIUIMessages.CDI_INSTALL_WIZARD_PAGE_CONFIGURE);
	}

	public void propertyChanged(DataModelEvent event) {
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite root = new Composite(parent, SWT.NONE);

		GridData gd = new GridData();

		gd.horizontalSpan = 1;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;

		GridLayout gridLayout = new GridLayout(1, false);
		root.setLayout(gridLayout);

		Composite generalGroup = new Composite(root, SWT.NONE);
		generalGroup.setLayoutData(gd);
		gridLayout = new GridLayout(4, false);

		generalGroup.setLayout(gridLayout);

		generateBeansXml = true;

		IFieldEditor generateBeansXmlCheckBox = IFieldEditorFactory.INSTANCE.createCheckboxEditor(
				CDIUIMessages.CDI_GENERATE_BEANS_XML, CDIUIMessages.CDI_GENERATE_BEANS_XML, generateBeansXml);
		generateBeansXmlCheckBox.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				Object value = evt.getNewValue();
				if (value instanceof Boolean) {
					boolean v = ((Boolean) value).booleanValue();
					setGenerateBeansXml(v);
				}
			}
		});
		editorRegistry.put(generateBeansXmlCheckBox.getName(), generateBeansXmlCheckBox);
		generateBeansXmlCheckBox.doFillIntoGrid(generalGroup);

		setControl(root);
	}

	public void setGenerateBeansXml(boolean generate) {
		generateBeansXml = generate;
		model.setProperty(ICDIFacetDataModelProperties.GENERATE_BEANS_XML, generateBeansXml);
	}

	public void setConfig(Object config) {
		model = (IDataModel) config;
	}
}