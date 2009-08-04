/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.ui.wizard;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.model.ITableFilter;
import org.hibernate.eclipse.console.model.impl.ReverseEngineeringDefinitionImpl;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.views.DBTableFilterView;

/**
 * The page for selection table filters.
 * @author Dmitry Geraskov
 */
public class SeamGenerateEntitiesTablesWizardPage extends WizardPage /*implements ISelectionChangedListener*/ {

	public static final String pageName = "seam.generate.entities.tablesPage";		//$NON-NLS-1$

	private String cfgName;

	private IReverseEngineeringDefinition model;

	public SeamGenerateEntitiesTablesWizardPage() {
		super(pageName, SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_TITLE, null);
		setMessage("Select Tables");												//$NON-NLS-1$
	}

	public void createControl(Composite parent) {
		setPageComplete(true);
		Composite top = new Composite(parent, SWT.NONE);
		top.setLayout(new FillLayout());
		final DBTableFilterView tfView = new DBTableFilterView(top, SWT.NONE){

			@Override
			protected String getConsoleConfigurationName() {
				return cfgName;
			}
		};

		model = new ReverseEngineeringDefinitionImpl();

		tfView.setModel(model);

		if (getWizard().getContainer() instanceof WizardDialog){
			WizardDialog wd = (WizardDialog) getWizard().getContainer();
			wd.addPageChangedListener(new IPageChangedListener(){

				//set console configuration as treeViewer input
				public void pageChanged(PageChangedEvent event) {
					if (event.getSelectedPage() == SeamGenerateEntitiesTablesWizardPage.this){
						SeamGenerateEntitiesWizardPage page1 = (SeamGenerateEntitiesWizardPage)getWizard().getPreviousPage(SeamGenerateEntitiesTablesWizardPage.this);
						cfgName = page1.getConsoleCongigurationName();
					}
				}});
		}
		setControl(top);
	}

	/*
	 * Get filters founded on the selected tables
	 */
	public String getFilters() {
		StringBuilder builder = new StringBuilder();
		if (model != null){
			ITableFilter[] filters = model.getTableFilters();
			if (filters.length == 0) return builder.toString();
			builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n")//$NON-NLS-1$
			.append("<!DOCTYPE hibernate-reverse-engineering PUBLIC \"-//Hibernate/Hibernate Reverse Engineering DTD 3.0//EN\"")//$NON-NLS-1$
			.append(" \"http://hibernate.sourceforge.net/hibernate-reverse-engineering-3.0.dtd\" >\r\n")//$NON-NLS-1$
			.append("\r\n")//$NON-NLS-1$
			.append("<hibernate-reverse-engineering>\r\n");//$NON-NLS-1$
			for (ITableFilter element : filters) {
				builder.append(generateStringForFilter(element));
			}
			builder.append("</hibernate-reverse-engineering>\r\n");	//$NON-NLS-1$
		}
		return  builder.toString();
	}

	private String generateStringForFilter(ITableFilter filter){
		String filterStr = "<table-filter match-name=\"" + filter.getMatchName() + "\"";	//$NON-NLS-1$	//$NON-NLS-2$
		if (filter.getMatchSchema() != null){
			filterStr += " match-schema=\"" + filter.getMatchSchema() + "\"";//$NON-NLS-1$	//$NON-NLS-2$
		}
		//TODO some DBs jdbc readers filters by catalog name incorrectly
		if (filter.getMatchCatalog() != null){
			filterStr += " match-catalog=\"" + filter.getMatchCatalog() + "\"";	//$NON-NLS-1$//$NON-NLS-2$
		}
		if (filter.getExclude()){
			filterStr += " exclude=\"true\"";	//$NON-NLS-1$
		}
		return filterStr += "></table-filter>\r\n";		//$NON-NLS-1$
	}

}
